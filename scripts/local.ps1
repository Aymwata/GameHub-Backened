[CmdletBinding()]
param(
    [ValidateSet('Start', 'Stop')]
    [string]$Action = 'Start',
    [switch]$AllServices,
    [switch]$CoreOnly
)

$ErrorActionPreference = 'Stop'
$backend = Split-Path -Parent $PSScriptRoot
$runtime = Join-Path $backend '.local'
$logs = Join-Path $runtime 'logs'
$pidFile = Join-Path $runtime 'processes.json'

function Get-LocalProcesses {
    if (Test-Path -LiteralPath $pidFile) {
        foreach ($record in (Get-Content -LiteralPath $pidFile -Raw | ConvertFrom-Json)) {
            $process = Get-Process -Id $record.Id -ErrorAction SilentlyContinue
            if ($process -and $process.ProcessName -eq 'java' -and
                $process.StartTime.ToUniversalTime().Ticks -eq [long]$record.StartTicks) {
                $process
            }
        }
    }
}

$running = @(Get-LocalProcesses)
if ($Action -eq 'Stop') {
    if ($running.Count -gt 0) {
        $running | Stop-Process
        $running | Wait-Process -Timeout 15
    }
    if (Test-Path -LiteralPath $pidFile) {
        Remove-Item -LiteralPath $pidFile
    }
    Write-Host 'Backend local detenido.'
    return
}

if ($running.Count -gt 0) {
    throw 'El backend local ya esta iniciado. Usa -Action Stop antes de reiniciarlo.'
}

if ($AllServices -and $CoreOnly) {
    throw 'Usa -CoreOnly para el arranque reducido o inicia sin opciones para levantar todo.'
}

if (-not $env:JAVA_HOME -and (Test-Path 'C:\Program Files\Java\jdk-25\bin\javac.exe')) {
    $env:JAVA_HOME = 'C:\Program Files\Java\jdk-25'
}
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'java.exe' }
$javac = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/javac.exe' } else { 'javac.exe' }
$javaVersion = & $javac -version
if ($LASTEXITCODE -ne 0 -or "$javaVersion" -notmatch '^javac (\d+)' -or [int]$Matches[1] -lt 25) {
    throw 'Configura JAVA_HOME con la carpeta del JDK 25.'
}

$services = [ordered]@{
    'eureka-server' = 8761
    'user-service' = 8080
    'category-service' = 8081
    'auth-service' = 8082
    'product-service' = 8083
    'inventory-service' = 8085
    'order-service' = 8086
}
if (-not $CoreOnly) {
    $services['promotion-service'] = 8084
    $services['shipping-service'] = 8087
    $services['review-service'] = 8088
    $services['payment-service'] = 8089
}
$services['gateway-service'] = 8099

foreach ($port in $services.Values) {
    $listener = [System.Net.Sockets.TcpListener]::new([System.Net.IPAddress]::Loopback, $port)
    try {
        $listener.Start()
    } catch [System.Net.Sockets.SocketException] {
        throw "El puerto local $port no esta disponible. Revisa si ya ejecutaste ese servicio en IntelliJ."
    } finally {
        $listener.Stop()
    }
}

$jars = @{}
foreach ($service in $services.Keys) {
    $module = Join-Path $backend $service
    Write-Host "Compilando $service..."
    Push-Location -LiteralPath $module
    try {
        & .\mvnw.cmd -B -ntp -DskipTests package
        if ($LASTEXITCODE -ne 0) {
            throw "Maven no pudo compilar $service (codigo $LASTEXITCODE)."
        }
        $jars[$service] = (Get-ChildItem -Path 'target' -Filter "$service-*.jar" -File |
            Where-Object Name -NotMatch '-(sources|javadoc|tests)\.jar$' |
            Sort-Object LastWriteTime -Descending |
            Select-Object -First 1).FullName
        if (-not $jars[$service]) {
            throw "No se encontro el JAR ejecutable de $service."
        }
    } finally {
        Pop-Location
    }
}

New-Item -ItemType Directory -Path $logs -Force | Out-Null
$credentialsFile = Join-Path $runtime 'demo-credentials.json'
if (-not (Test-Path -LiteralPath $credentialsFile)) {
    @(
        @{ email = 'cliente@gamehub.example'; rol = 'CLIENTE'; password = [guid]::NewGuid().ToString('N') }
        @{ email = 'admin@gamehub.example'; rol = 'ADMIN'; password = [guid]::NewGuid().ToString('N') }
    ) | ConvertTo-Json | Set-Content -LiteralPath $credentialsFile
}
$admin = (Get-Content -LiteralPath $credentialsFile -Raw | ConvertFrom-Json) | Where-Object rol -eq 'ADMIN'
$processes = [ordered]@{}
$previousSecret = $env:JWT_SECRET
$env:JWT_SECRET = [guid]::NewGuid().ToString('N') + [guid]::NewGuid().ToString('N')

function Start-LocalService([string]$service) {
    $port = $services[$service]
    $arguments = @(
        '-Xmx384m', '-jar', ('"{0}"' -f $jars[$service]),
        '--server.address=127.0.0.1',
        "--server.port=$port",
        '--gamehub.seed.random-data=false',
        '--eureka.instance.hostname=127.0.0.1',
        '--eureka.instance.prefer-ip-address=false',
        '--eureka.client.service-url.defaultZone=http://127.0.0.1:8761/eureka/'
    )
    if ($service -eq 'auth-service') {
        $arguments += '--gamehub.auth.cookie-secure=false'
    }
    if ($service -eq 'eureka-server') {
        $arguments += '--eureka.server.use-read-only-response-cache=false'
    }
    $previousEmail = $env:GAMEHUB_AUTH_BOOTSTRAP_ADMIN_EMAIL
    $previousPassword = $env:GAMEHUB_AUTH_BOOTSTRAP_ADMIN_PASSWORD
    try {
        if ($service -eq 'auth-service') {
            $env:GAMEHUB_AUTH_BOOTSTRAP_ADMIN_EMAIL = $admin.email
            $env:GAMEHUB_AUTH_BOOTSTRAP_ADMIN_PASSWORD = $admin.password
        }
        $process = Start-Process -FilePath $java -ArgumentList $arguments -WorkingDirectory $runtime `
            -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $logs "$service.log") `
            -RedirectStandardError (Join-Path $logs "$service.err.log")
    } finally {
        $env:GAMEHUB_AUTH_BOOTSTRAP_ADMIN_EMAIL = $previousEmail
        $env:GAMEHUB_AUTH_BOOTSTRAP_ADMIN_PASSWORD = $previousPassword
    }
    $processes[$service] = $process
    @($processes.Values | ForEach-Object {
        @{ Id = $_.Id; StartTicks = $_.StartTime.ToUniversalTime().Ticks.ToString() }
    }) | ConvertTo-Json | Set-Content -LiteralPath $pidFile
    Write-Host "Iniciando $service en el puerto $port..."
}

function Wait-LocalService([string]$service, [string]$url) {
    $deadline = (Get-Date).AddMinutes(3)
    do {
        if ($processes[$service].HasExited) { break }
        try {
            Invoke-WebRequest -Uri $url -UseBasicParsing -TimeoutSec 3 | Out-Null
            return
        } catch {
            Start-Sleep -Seconds 1
        }
    } while ((Get-Date) -lt $deadline)
    throw "$service no respondio en $url. Revisa .local/logs/$service.log y $service.err.log."
}

function Wait-EurekaRegistry([string[]]$servicesToCheck) {
    $deadline = (Get-Date).AddMinutes(3)
    $missing = $servicesToCheck
    do {
        if ($processes['eureka-server'].HasExited) { break }
        try {
            $registry = Invoke-RestMethod -Uri 'http://127.0.0.1:8761/eureka/apps' `
                -Headers @{ Accept = 'application/json' } -TimeoutSec 3
            $up = $registry.applications.application |
                Where-Object { $_.instance.status -contains 'UP' } |
                ForEach-Object { $_.name }
            $missing = @($servicesToCheck | Where-Object { $_ -notin $up })
            if ($missing.Count -eq 0) { return }
        } catch {
            $missing = $servicesToCheck
        }
        Start-Sleep -Seconds 1
    } while ((Get-Date) -lt $deadline)
    throw "Eureka no registro como UP los siguientes servicios: $($missing -join ', '). Revisa .local/logs."
}

try {
    Write-Host "Servicios que se iniciaran: $($services.Keys -join ', ')"
    Start-LocalService 'eureka-server'
    Wait-LocalService 'eureka-server' 'http://127.0.0.1:8761/'

    $businessServices = $services.Keys | Where-Object { $_ -notin 'eureka-server', 'gateway-service' }
    foreach ($service in $businessServices) {
        Start-LocalService $service
    }
    foreach ($service in $businessServices) {
        Wait-LocalService $service "http://127.0.0.1:$($services[$service])/v3/api-docs"
    }

    Write-Host 'Esperando el registro completo en Eureka...'
    Wait-EurekaRegistry $businessServices
    Start-LocalService 'gateway-service'
    foreach ($service in $businessServices) {
        $ruta = $service -replace '-service$', ''
        Wait-LocalService 'gateway-service' "http://127.0.0.1:8099/api-docs/$ruta"
    }
    & (Join-Path $PSScriptRoot 'seed-local.ps1') -RuntimeDirectory $runtime
    Write-Host "Backend listo en http://127.0.0.1:8099 ($($services.Count) procesos)"
} catch {
    $processes.Values | Where-Object { -not $_.HasExited } | Stop-Process
    throw
} finally {
    $env:JWT_SECRET = $previousSecret
}
