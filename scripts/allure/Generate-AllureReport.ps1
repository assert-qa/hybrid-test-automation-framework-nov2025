param(
    [string]$Profile = "dev",
    [string]$SuiteXmlFile = "testng-all.xml",
    [string]$ResultsDirectory = "exports/AllureReport",
    [string]$ReportDirectory = "exports/AllureHtml",
    [switch]$Open
)

$ErrorActionPreference = "Stop"

if (Test-Path -LiteralPath $ResultsDirectory) {
    Remove-Item -LiteralPath $ResultsDirectory -Recurse -Force
}

mvn clean test "-P$Profile" "-DsuiteXmlFile=$SuiteXmlFile"

$scriptDirectory = Split-Path -Parent $MyInvocation.MyCommand.Path
& (Join-Path $scriptDirectory "Prepare-AllureResults.ps1") `
    -ResultsDirectory $ResultsDirectory `
    -ReportDirectory $ReportDirectory

allure generate $ResultsDirectory -o $ReportDirectory --clean

if ($Open) {
    allure open $ReportDirectory
}
