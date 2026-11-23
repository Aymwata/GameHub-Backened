[CmdletBinding()]
param([Parameter(Mandatory)][string]$RuntimeDirectory)
$ErrorActionPreference = 'Stop'
function Add-DemoRecord([string]$uri, [hashtable]$body, [hashtable]$headers = @{}) {
    $payload = $body | ConvertTo-Json -Compress
    try {
        Invoke-RestMethod -Method Post -Uri $uri -ContentType 'application/json; charset=utf-8' -Body $payload -Headers $headers -TimeoutSec 15
    } catch {
        throw "POST falló en $uri : $($_.Exception.Message) $($_.ErrorDetails.Message)"
    }
}
$categoriesUrl = 'http://127.0.0.1:8081/api/categories'
$productsUrl = 'http://127.0.0.1:8083/api/products'
$inventoryUrl = 'http://127.0.0.1:8085/api/inventory'
$usersUrl = 'http://127.0.0.1:8080/api/users'
$addressesUrl = 'http://127.0.0.1:8080/api/directions'
$accountsUrl = 'http://127.0.0.1:8099/api/auth/cuentas'
$loginUrl = 'http://127.0.0.1:8099/api/auth/login'
$promotionsUrl = 'http://127.0.0.1:8084/api/promotions'

$categoryDefinitions = @(
    @{ nombre = 'Computadores'; descripcion = 'Notebooks y equipos de escritorio' }
    @{ nombre = 'Componentes y piezas'; descripcion = 'Tarjetas gráficas y componentes para tu equipo' }
    @{ nombre = 'Periféricos'; descripcion = 'Monitores y accesorios gamer' }
)
$categoriesResponse = Invoke-WebRequest -Uri $categoriesUrl -UseBasicParsing -TimeoutSec 15
$categories = ConvertFrom-Json -InputObject $categoriesResponse.Content
$categoryIds = @{}
foreach ($definition in $categoryDefinitions) {
    $category = $null
    foreach ($existingCategory in $categories) {
        $nombreExistente = [string]$existingCategory.nombre
        $nombreNormalizado = $nombreExistente.Replace('Ã©', 'é').Replace('Ã¡', 'á').Replace('Ã­', 'í').Replace('Ã³', 'ó').Replace('Ãº', 'ú')
        if ($nombreNormalizado -ieq $definition.nombre) {
            if ($nombreExistente -cne $definition.nombre) {
                $body = @{ nombre = $definition.nombre; descripcion = $definition.descripcion } | ConvertTo-Json -Compress
                $category = Invoke-RestMethod -Method Put -Uri "$categoriesUrl/$($existingCategory.id)" -ContentType 'application/json; charset=utf-8' -Body $body -TimeoutSec 15
            } else {
                $category = $existingCategory
            }
            break
        }
    }
    if (-not $category) {
        $category = Add-DemoRecord $categoriesUrl $definition
        $categories += $category
    }
    $categoryIds[$definition.nombre] = [long]$category.id
}
$fixtures = @(
    @{
        nombre = 'Monitor Gamer Samsung 27", FHD, 180Hz, VA, 1ms, FreeSync, Odyssey G3 LS27DG300ELXZS'
        marca = 'samsung'; modelo = 'SAM-ODYSSEY-G3-LS27DG300ELXZS'; precio = 180000
        categoria = 'Periféricos'; imagen = 'assets/images/monitorSamsung.jpg'; stock = 15
        descripcion = 'Monitor gamer Samsung Odyssey G3 de 27 pulgadas, Full HD y 180 Hz.'
        detallesTecnicos = @{
            'Tamaño de pantalla' = '27 pulgadas'
            'Resolución' = 'Full HD (1920 × 1080)'
            'Tipo de panel' = 'VA'
            'Frecuencia de actualización' = '180 Hz'
            'Tiempo de respuesta' = '1 ms'
            'Sincronización' = 'FreeSync'
        }
    }
    @{
        nombre = 'Notebook Gamer LOQ Gen 10 Intel Core i7-13645HX NVIDIA GeForce RTX 5050 15.6" 144Hz 16GB RAM 512GB Windows 11 Home'
        marca = 'lenovo'; modelo = 'LEN-LOQ-GEN10-I7-RTX5050'; precio = 850000
        categoria = 'Computadores'; imagen = 'assets/images/notebookLenovo.jpg'; stock = 5
        descripcion = 'Notebook gamer Lenovo LOQ con Intel Core i7, RTX 5050 y pantalla de 144 Hz.'
        detallesTecnicos = @{
            'Procesador' = 'Intel Core i7-13645HX'
            'Tarjeta gráfica' = 'NVIDIA GeForce RTX 5050'
            'Memoria RAM' = '16 GB'
            'Almacenamiento' = 'SSD de 512 GB'
            'Pantalla' = '15,6 pulgadas, 144 Hz'
            'Sistema operativo' = 'Windows 11 Home'
        }
    }
    @{
        nombre = 'Tarjeta de Video MSI Nvidia GeForce RTX 5060 8G VENTUS 2X OC WHITE'
        marca = 'msi'; modelo = 'MSI-RTX5060-VENTUS-WHITE'; precio = 320000
        categoria = 'Componentes y piezas'; imagen = 'assets/images/tarjetaNvidia.jpg'; stock = 0
        descripcion = 'Tarjeta de video MSI GeForce RTX 5060 Ventus 2X OC White.'
        detallesTecnicos = @{
            'Chip gráfico' = 'NVIDIA GeForce RTX 5060'
            'Memoria de video' = '8 GB'
            'Refrigeración' = 'MSI Ventus 2X'
            'Edición' = 'OC White'
        }
    }
    @{
        nombre = 'Desktop Gamer Esgaming Ryzen 5 5500 32GB DDR4 RGB 500GB SSD RTX 5050'
        marca = 'esgaming'; modelo = 'ESG-RYZEN5500-RTX5050'; precio = 1200000
        categoria = 'Computadores'; imagen = 'assets/images/desktopEsgaming.jpg'; stock = 2
        descripcion = 'Desktop gamer Esgaming con Ryzen 5 5500, 32 GB RAM y RTX 5050.'
        detallesTecnicos = @{
            'Procesador' = 'AMD Ryzen 5 5500'
            'Memoria RAM' = '32 GB DDR4 RGB'
            'Almacenamiento' = 'SSD de 500 GB'
            'Tarjeta gráfica' = 'NVIDIA GeForce RTX 5050'
        }
    }
    @{
        nombre = 'Monitor Gamer Samsung Curvo 49” Odyssey G9 G91F QHD 144Hz'
        marca = 'samsung'; modelo = 'SAM-ODYSSEY-G9-G91F'; precio = 849990
        categoria = 'Periféricos'; imagen = 'assets/images/monitorSamsungCurvo.jpg'; stock = 10
        descripcion = 'Monitor gamer Samsung Odyssey G9 curvo de 49 pulgadas, QHD y 144 Hz.'
        detallesTecnicos = @{
            'Tamaño de pantalla' = '49 pulgadas'
            'Diseño' = 'Curvo'
            'Resolución' = 'QHD'
            'Frecuencia de actualización' = '144 Hz'
            'Modelo' = 'Odyssey G9 G91F'
        }
    }
)
$products = Invoke-RestMethod -Uri $productsUrl -TimeoutSec 15
$inventoryResponse = Invoke-RestMethod -Uri $inventoryUrl -Headers @{ Accept = 'application/hal+json' } -TimeoutSec 15
$inventory = $inventoryResponse._embedded.inventoryResponseDTOList
foreach ($fixture in $fixtures) {
    $product = $products | Where-Object modelo -eq $fixture.modelo | Select-Object -First 1
    if (-not $product) {
        $product = Add-DemoRecord $productsUrl @{
            nombre = $fixture.nombre
            marca = $fixture.marca
            modelo = $fixture.modelo
            descripcion = $fixture.descripcion
            precio = $fixture.precio
            categoriaId = $categoryIds[$fixture.categoria]
            imagen = $fixture.imagen
            detallesTecnicos = $fixture.detallesTecnicos
        }
        $products += $product
    }
    $detallesTecnicos = [ordered]@{}
    foreach ($entrada in $fixture.detallesTecnicos.GetEnumerator()) {
        $detallesTecnicos[$entrada.Key] = $entrada.Value
    }
    $technicalDetailsBody = @{ detallesTecnicos = $detallesTecnicos } | ConvertTo-Json -Depth 5 -Compress
    Invoke-RestMethod -Method Put -Uri "$productsUrl/$($product.id)/detalles-tecnicos" `
        -ContentType 'application/json; charset=utf-8' -Body $technicalDetailsBody -TimeoutSec 15 | Out-Null
    if ($fixture.stock -gt 0 -and -not ($inventory | Where-Object productId -eq $product.id)) {
        Add-DemoRecord "$inventoryUrl/setup" @{
            productId = $product.id
            quantity = $fixture.stock
            location = 'Bodega GameHub'
        } | Out-Null
    }
    if ($fixture.stock -gt 0) {
        $stock = Invoke-RestMethod -Uri "$inventoryUrl/product/$($product.id)" -TimeoutSec 15
    } else {
        $stock = @{ actualAvailable = 0 }
    }
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
        $users += $user
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
$promoCodeUrl = "$promotionsUrl/codigo/GAME20"
try {
    Invoke-RestMethod -Uri $promoCodeUrl -TimeoutSec 15 | Out-Null
    Write-Host 'Cupón GAME20 ya existe.'
} catch {
    Add-DemoRecord $promotionsUrl @{
        codigo = 'GAME20'
        tipo = 'PORCENTAJE'
        valor = 20
        fechaInicio = '2026-01-01T00:00:00'
        fechaFin = '2026-12-31T23:59:59'
        montoMinimo = 0
        usosMaximos = 1000
    } | Out-Null
    Write-Host 'Cupón GAME20 creado: 20% de descuento.'
}
Write-Host 'Login JWT comprobado para ADMIN y CLIENTE.'
Write-Host 'Credenciales ficticias en .local/demo-credentials.json'


