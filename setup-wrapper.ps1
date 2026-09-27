$ErrorActionPreference = 'Stop'

$repo = 'https://github.com/NeoForgeMDKs/MDK-26.2-ModDevGradle/archive/refs/heads/main.zip'
$temp = Join-Path $env:TEMP ('raw-xaero-mdk-' + [guid]::NewGuid())
$zip = "$temp.zip"

Write-Host 'Downloading official NeoForge 26.2 ModDevGradle MDK...'
Invoke-WebRequest -Uri $repo -OutFile $zip
Expand-Archive -Path $zip -DestinationPath $temp

$mdk = Get-ChildItem $temp -Directory | Select-Object -First 1
if (-not $mdk) { throw 'Could not locate extracted MDK.' }

Copy-Item (Join-Path $mdk.FullName 'gradlew') . -Force
Copy-Item (Join-Path $mdk.FullName 'gradlew.bat') . -Force
Copy-Item (Join-Path $mdk.FullName 'gradle') . -Recurse -Force

Remove-Item $zip -Force
Remove-Item $temp -Recurse -Force

Write-Host 'Gradle wrapper installed.'
Write-Host 'Now run: .\gradlew.bat build'
