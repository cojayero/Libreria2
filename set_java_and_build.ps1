# Windows PowerShell script to set JAVA_HOME and update PATH for Android Studio JDK
# Adjust the path below if your Android Studio is installed elsewhere
$androidStudioJdk = "C:\Program Files\Android\Android Studio\jbr"
if (Test-Path $androidStudioJdk) {
    $env:JAVA_HOME = $androidStudioJdk
    $env:PATH = "$androidStudioJdk\bin;" + $env:PATH
    Write-Host "JAVA_HOME set to $androidStudioJdk"
    Write-Host "PATH updated."
} else {
    Write-Host "Android Studio JDK not found at $androidStudioJdk. Please update the script with the correct path."
}

# Limpiar carpetas de build y caché antes de compilar
Remove-Item -Recurse -Force .\app\build, .\build, .\.gradle -ErrorAction SilentlyContinue

# Compilar e instalar en el dispositivo
Write-Host "Compilando e instalando en el dispositivo..."
./gradlew installDebug
if ($LASTEXITCODE -eq 0) {
    Write-Host "\n\n✅ Compilación e instalación completadas correctamente.\n"
} else {
    Write-Host "\n\n❌ Error en la compilación o instalación.\n"
}
