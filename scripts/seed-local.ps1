[CmdletBinding()]
param(
    [Parameter(Mandatory)]
    [string]$RuntimeDirectory
)

$ErrorActionPreference = 'Stop'

function Add-DemoRecord([string]$uri, [hashtable]$body, [hashtable]$headers = @{}) {
    Invoke-RestMethod -Method Post -Uri $uri -ContentType 'application/json' `
        -Body ($body | ConvertTo-Json -Compress) -Headers $headers -TimeoutSec 15
}

$categoriesUrl = 'http://127.0.0.1:8081/api/categories'
$productsUrl = 'http://127.0.0.1:8083/api/products'
$inventoryUrl = 'http://127.0.0.1:8085/api/inventory'
$usersUrl = 'http://127.0.0.1:8080/api/users'
$addressesUrl = 'http://127.0.0.1:8080/api/directions'
$accountsUrl = 'http://127.0.0.1:8099/api/auth/cuentas'
$loginUrl = 'http://127.0.0.1:8099/api/auth/login'

$category = (Invoke-RestMethod -Uri $categoriesUrl -TimeoutSec 15) |
    Where-Object nombre -eq 'Perifericos Demo' | Select-Object -First 1
if (-not $category) {
    $category = Add-DemoRecord $categoriesUrl @{
        nombre = 'Perifericos Demo'
        descripcion = 'Catalogo ficticio para la Evaluacion 3'
    }
}

$products = Invoke-RestMethod -Uri $productsUrl -TimeoutSec 15
$inventory = (Invoke-RestMethod -Uri $inventoryUrl -Headers @{ Accept = 'application/hal+json' } `
    -TimeoutSec 15)._embedded.inventoryResponseDTOList
$fixtures = @(
    @{ nombre = 'Teclado mecanico Demo'; modelo = 'GH-DEMO-TECLADO'; precio = 39990; stock = 12 }
    @{ nombre = 'Mouse gamer Demo'; modelo = 'GH-DEMO-MOUSE'; precio = 19990; stock = 20 }
)
foreach ($fixture in $fixtures) {
    $product = $products | Where-Object modelo -eq $fixture.modelo | Select-Object -First 1
    if (-not $product) {
        $product = Add-DemoRecord $productsUrl @{
            nombre = $fixture.nombre
            modelo = $fixture.modelo
            marca = 'GameHub Demo'
            precio = $fixture.precio
            categoriaId = $category.id
        }
    }
    if (-not ($inventory | Where-Object productId -eq $product.id)) {
        Add-DemoRecord "$inventoryUrl/setup" @{
            productId = $product.id
            quantity = $fixture.stock
            location = 'Bodega Demo'
        } | Out-Null
    }
    $stock = Invoke-RestMethod -Uri "$inventoryUrl/product/$($product.id)" -TimeoutSec 15
    Write-Host "$($product.nombre) (ID $($product.id)) - $($stock.actualAvailable) unidades disponibles"
}

$credentialsFile = Join-Path $RuntimeDirectory 'demo-credentials.json'
$credentials = Get-Content -LiteralPath $credentialsFile -Raw | ConvertFrom-Json
$users = Invoke-RestMethod -Uri $usersUrl -TimeoutSec 15
foreach ($credential in $credentials) {
    $user = $users | Where-Object email -eq $credential.email | Select-Object -First 1
    if (-not $user) {
        $user = Add-DemoRecord $usersUrl @{
            nombre = "$($credential.rol) Demo"
            email = $credential.email
            telefono = '900000000'
            rol = $credential.rol
        }
    }
    if ($credential.rol -eq 'CLIENTE') {
        $addresses = Invoke-RestMethod -Uri "$addressesUrl/usuario/$($user.id)" -TimeoutSec 15
        if (-not ($addresses | Where-Object calle -eq 'Calle Demo')) {
            Add-DemoRecord $addressesUrl @{
                usuarioId = $user.id
                comuna = 'Santiago'
                ciudad = 'Santiago'
                calle = 'Calle Demo'
                numero = '100'
            } | Out-Null
        }
    }
}
$admin = $credentials | Where-Object rol -eq 'ADMIN'
$adminSesion = Add-DemoRecord $loginUrl @{ email = $admin.email; password = $admin.password; perfil = 'ADMIN' }
$adminHeaders = @{ Authorization = "Bearer $($adminSesion.accessToken)" }
$accounts = Invoke-RestMethod -Uri $accountsUrl -Headers $adminHeaders -TimeoutSec 15
$cliente = $credentials | Where-Object rol -eq 'CLIENTE'
if (-not ($accounts | Where-Object email -eq $cliente.email)) {
    Add-DemoRecord $accountsUrl @{
        email = $cliente.email
        password = $cliente.password
        rol = 'CLIENTE'
    } $adminHeaders | Out-Null
}
$clienteSesion = Add-DemoRecord $loginUrl @{ email = $cliente.email; password = $cliente.password; perfil = 'CLIENTE' }
if ($adminSesion.usuario.rol -ne 'ADMIN' -or $clienteSesion.usuario.rol -ne 'CLIENTE' -or
    -not $adminSesion.accessToken -or -not $clienteSesion.accessToken) {
    throw 'No se pudieron verificar los dos perfiles de prueba.'
}
Write-Host 'Login JWT comprobado para ADMIN y CLIENTE.'
Write-Host 'Credenciales ficticias en .local/demo-credentials.json'
