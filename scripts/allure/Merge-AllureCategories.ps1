param(
    [string]$SourceDirectory = "src/test/resources/allure/categories",
    [string]$ResultsDirectory = "exports/AllureReport"
)

$ErrorActionPreference = "Stop"

$resolvedSourceDirectory = Resolve-Path -LiteralPath $SourceDirectory
if (-not (Test-Path -LiteralPath $ResultsDirectory)) {
    New-Item -ItemType Directory -Path $ResultsDirectory | Out-Null
}

$categories = @()
Get-ChildItem -LiteralPath $resolvedSourceDirectory -Filter "*-categories.json" | Sort-Object Name | ForEach-Object {
    $items = Get-Content -LiteralPath $_.FullName -Raw | ConvertFrom-Json
    foreach ($item in $items) {
        $categories += $item
    }
}

$outputPath = Join-Path $ResultsDirectory "categories.json"
$categories | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $outputPath -Encoding UTF8

Write-Host "Allure categories merged: $outputPath"
