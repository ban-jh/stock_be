$ErrorActionPreference = 'Stop'

# .env.properties의 앱키/시크릿키를 현재 Windows 계정의 DPAPI로 암호화합니다.
# 평문 값은 화면이나 출력에 표시하지 않습니다.
$envFile = Join-Path $PSScriptRoot '.env.properties'
if (-not (Test-Path -LiteralPath $envFile)) {
    throw '프로젝트 루트에 .env.properties 파일이 없습니다. .env.example을 복사해 먼저 만드세요.'
}

$content = [System.IO.File]::ReadAllText($envFile)

function Protect-LocalValue {
    param([Parameter(Mandatory = $true)][string]$Value)

    $plainValue = $Value.Trim()
    if ($plainValue.StartsWith('DPAPI:', [System.StringComparison]::Ordinal)) {
        return $plainValue
    }
    if ($plainValue.Length -ge 2 -and $plainValue.StartsWith('"') -and $plainValue.EndsWith('"')) {
        $plainValue = $plainValue.Substring(1, $plainValue.Length - 2)
    }
    if ([string]::IsNullOrWhiteSpace($plainValue)) {
        throw '앱키 또는 시크릿키 값이 비어 있습니다. .env.properties를 확인하세요.'
    }

    $secureValue = ConvertTo-SecureString -String $plainValue -AsPlainText -Force
    try {
        return 'DPAPI:' + (ConvertFrom-SecureString -SecureString $secureValue)
    }
    finally {
        $secureValue.Dispose()
        $plainValue = $null
    }
}

foreach ($propertyName in @('TOSS_CLIENT_ID', 'TOSS_CLIENT_SECRET')) {
    $pattern = '(?im)^\s*' + [regex]::Escape($propertyName) + '\s*=(.*)$'
    $match = [regex]::Match($content, $pattern)
    if (-not $match.Success) {
        throw "$propertyName 설정 줄이 .env.properties에 없습니다."
    }

    $protectedValue = Protect-LocalValue -Value $match.Groups[1].Value
    $replacement = $propertyName + '=' + $protectedValue
    $content = $content.Remove($match.Index, $match.Length).Insert($match.Index, $replacement)
}

# 프론트엔드-백엔드 개인 접근 토큰은 선택 설정입니다. 설정되어 있으면 동일하게 DPAPI로 보호합니다.
$optionalTokenPattern = '(?im)^\s*STOCK_API_ACCESS_TOKEN\s*=(.*)$'
$optionalTokenMatch = [regex]::Match($content, $optionalTokenPattern)
if ($optionalTokenMatch.Success -and -not [string]::IsNullOrWhiteSpace($optionalTokenMatch.Groups[1].Value)) {
    $protectedToken = Protect-LocalValue -Value $optionalTokenMatch.Groups[1].Value
    $replacement = 'STOCK_API_ACCESS_TOKEN=' + $protectedToken
    $content = $content.Remove($optionalTokenMatch.Index, $optionalTokenMatch.Length).Insert($optionalTokenMatch.Index, $replacement)
}

# 모든 변환이 끝난 뒤 파일을 교체하여 중간 실패 시 원본을 유지합니다.
$tempFile = $envFile + '.tmp'
try {
    [System.IO.File]::WriteAllText($tempFile, $content, [System.Text.UTF8Encoding]::new($false))
    Move-Item -LiteralPath $tempFile -Destination $envFile -Force
}
finally {
    if (Test-Path -LiteralPath $tempFile) {
        Remove-Item -LiteralPath $tempFile -Force
    }
}

$content = $null
Write-Output '앱키와 시크릿키를 현재 Windows 사용자 계정에 묶어 암호화했습니다.'
Write-Output '이후 서버는 .\run-local.ps1로 실행하세요.'
