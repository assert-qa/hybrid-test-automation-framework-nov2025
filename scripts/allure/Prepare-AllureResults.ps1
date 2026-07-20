param(
    [string]$ResultsDirectory = "exports/AllureReport",
    [string]$ReportDirectory = "exports/AllureHtml"
)

$ErrorActionPreference = "Stop"

$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path
& (Join-Path $scriptDirectory "Merge-AllureCategories.ps1") -ResultsDirectory $ResultsDirectory

$historySource = Join-Path $ReportDirectory "history"
$historyTarget = Join-Path $ResultsDirectory "history"
if (Test-Path -LiteralPath $historySource) {
    if (Test-Path -LiteralPath $historyTarget) {
        Remove-Item -LiteralPath $historyTarget -Recurse -Force
    }
    Copy-Item -LiteralPath $historySource -Destination $historyTarget -Recurse -Force
    Write-Host "Allure history copied: $historySource -> $historyTarget"
} else {
    Write-Host "Allure history not found. Trend starts after second report run."
}
