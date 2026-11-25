$ErrorActionPreference = 'Stop'

# DPAPI 암호문을 현재 Windows 계정으로 복호화해 Maven/Java 하위 프로세스에만 전달합니다.
$envFile = Join-Path $PSScriptRoot '.env.properties'
if (-not (Test-Path -LiteralPath $envFile)) {
    throw '프로젝트 루트에 .env.properties 파일이 없습니다.'
}

$properties = @{}
foreach ($line in [System.IO.File]::ReadAllLines($envFile)) {
    if ($line -match '^\s*[#!]' -or $line -match '^\s*$') { continue }
    if ($line -match '^\s*([^=]+?)\s*=(.*)$') {
        $properties[$Matches[1].Trim()] = $Matches[2].Trim()
    }
}

function Unprotect-LocalValue {
    param(
        [Parameter(Mandatory = $true)][string]$PropertyName,
        [Parameter(Mandatory = $true)][string]$StoredValue
    )

    if (-not $StoredValue.StartsWith('DPAPI:', [System.StringComparison]::Ordinal)) {
        throw "$PropertyName가 암호화되어 있지 않습니다. 먼저 .\protect-local-secrets.ps1을 실행하세요."
    }

    $secureValue = $null
    try {
        $secureValue = ConvertTo-SecureString -String $StoredValue.Substring(6)
        return (New-Object System.Net.NetworkCredential('', $secureValue)).Password
    }
    catch {
        throw "$PropertyName 복호화에 실패했습니다. 암호화할 때 사용한 Windows 계정에서 실행하세요."
    }
    finally {
        if ($null -ne $secureValue) { $secureValue.Dispose() }
    }
}

foreach ($requiredName in @('TOSS_CLIENT_ID', 'TOSS_CLIENT_SECRET')) {
    if (-not $properties.ContainsKey($requiredName)) {
        throw "$requiredName 설정이 .env.properties에 없습니다."
    }
}

$clientId = Unprotect-LocalValue -PropertyName 'TOSS_CLIENT_ID' -StoredValue $properties['TOSS_CLIENT_ID']
$clientSecret = Unprotect-LocalValue -PropertyName 'TOSS_CLIENT_SECRET' -StoredValue $properties['TOSS_CLIENT_SECRET']
$apiAccessToken = $null
if ($properties.ContainsKey('STOCK_API_ACCESS_TOKEN') -and -not [string]::IsNullOrWhiteSpace($properties['STOCK_API_ACCESS_TOKEN'])) {
    $apiAccessToken = Unprotect-LocalValue -PropertyName 'STOCK_API_ACCESS_TOKEN' -StoredValue $properties['STOCK_API_ACCESS_TOKEN']
}
$previousClientId = [System.Environment]::GetEnvironmentVariable('TOSS_CLIENT_ID', 'Process')
$previousClientSecret = [System.Environment]::GetEnvironmentVariable('TOSS_CLIENT_SECRET', 'Process')
$previousApiAccessToken = [System.Environment]::GetEnvironmentVariable('STOCK_API_ACCESS_TOKEN', 'Process')

try {
    $env:TOSS_CLIENT_ID = $clientId
    $env:TOSS_CLIENT_SECRET = $clientSecret
    if ($null -ne $apiAccessToken) { $env:STOCK_API_ACCESS_TOKEN = $apiAccessToken }
    Push-Location $PSScriptRoot
    & mvn spring-boot:run
}
finally {
    if ($null -eq $previousClientId) {
        Remove-Item Env:TOSS_CLIENT_ID -ErrorAction SilentlyContinue
    } else {
        $env:TOSS_CLIENT_ID = $previousClientId
    }
    if ($null -eq $previousClientSecret) {
        Remove-Item Env:TOSS_CLIENT_SECRET -ErrorAction SilentlyContinue
    } else {
        $env:TOSS_CLIENT_SECRET = $previousClientSecret
    }
    if ($null -ne $apiAccessToken) {
        if ($null -eq $previousApiAccessToken) { Remove-Item Env:STOCK_API_ACCESS_TOKEN -ErrorAction SilentlyContinue }
        else { $env:STOCK_API_ACCESS_TOKEN = $previousApiAccessToken }
    }
    if ((Get-Location).Path -eq $PSScriptRoot) { Pop-Location }
    $clientId = $null
    $clientSecret = $null
    $apiAccessToken = $null
}
