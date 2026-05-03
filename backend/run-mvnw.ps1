Push-Location .\OffGrid\OffGrid
# Forward all args to the Windows Maven wrapper
$cmd = Join-Path (Get-Location) 'mvnw.cmd'
if (-Not (Test-Path $cmd)) {
    Write-Error "mvnw.cmd not found in $pwd\OffGrid\OffGrid"
    Pop-Location
    exit 1
}
& $cmd @Args
$exit = $LASTEXITCODE
Pop-Location
exit $exit
