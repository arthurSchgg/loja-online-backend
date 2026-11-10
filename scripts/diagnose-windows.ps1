# Diagnostic only. Never deletes IDE files, resets branches, or changes configuration.
$ErrorActionPreference = "Stop"
Set-Location (Resolve-Path (Join-Path $PSScriptRoot ".."))
Write-Host "== Lojinha da Katia: Java backend preflight =="

if (Get-Command git -ErrorAction SilentlyContinue) {
    git status --short
    git branch --show-current
} else {
    Write-Warning "Git nao encontrado no PATH."
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw "Java nao encontrado. Instale/configure JDK 21 (JAVA_HOME e PATH)."
}
java -version
$javaVersion = (& java -version 2>&1 | Select-Object -First 1 | Out-String)
if ($javaVersion -notmatch 'version "21[."]|openjdk 21[ .]') {
    Write-Warning "O projeto usa Java 21. Confirme Project SDK e Maven Runner JDK 21 no IntelliJ."
}

$mvn = Get-Command mvn -ErrorAction SilentlyContinue
$wrapper = Join-Path $PWD "mvnw.cmd"
if ($mvn) {
    mvn -version
    $command = "mvn"
} elseif (Test-Path $wrapper) {
    $command = $wrapper
} else {
    throw "Maven nao encontrado: instale Maven ou use o Maven integrado ao IntelliJ."
}

if (Get-Command python -ErrorAction SilentlyContinue) {
    python scripts/check_java_references.py
    if ($LASTEXITCODE -ne 0) { throw "Referencias de pacotes/imports inconsistentes." }
} elseif (Get-Command py -ErrorAction SilentlyContinue) {
    py -3 scripts/check_java_references.py
    if ($LASTEXITCODE -ne 0) { throw "Referencias de pacotes/imports inconsistentes." }
} else {
    Write-Warning "Python nao encontrado; verificacao estatica ignorada."
}

& $command -B -DskipTests compile
if ($LASTEXITCODE -ne 0) { throw "Falha na compilacao Maven. Verifique o primeiro erro acima." }
Write-Host "Compilacao Maven OK. Se a IDE ainda marcar classes em vermelho: recarregue o projeto Maven e confira o SDK 21."
