# Compiles and runs all tests.
# Usage: .\run_tests.ps1

if (!(Test-Path out)) {
    New-Item -ItemType Directory -Path out
}

Write-Host "Compiling..."
javac -cp "lib/*;out" -d out src/main/java/servicehub/*.java src/main/java/servicehub/ds/*.java src/test/java/servicehub/*.java src/test/java/servicehub/util/*.java src/test/java/servicehub/ds/*.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "Running tests..."
    java -cp "out;lib/*;lib/junit-platform-console-standalone.jar" org.junit.platform.console.ConsoleLauncher --class-path out --scan-class-path --details=tree
} else {
    Write-Host "Compilation failed"
}
