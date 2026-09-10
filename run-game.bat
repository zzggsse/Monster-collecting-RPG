@echo off
setlocal
rem Wild Spirit launcher (Windows).
set "GRADLE_PATH=D:\game\tools\gradle-8.10.2\bin\gradle.bat"
if exist "%GRADLE_PATH%" (
  echo Starting Wild Spirit ... this may take a moment on first compile.
  call "%GRADLE_PATH%" lwjgl3:run
  goto :eof
)
echo Local Gradle not found. Using wrapper (downloads once, requires network).
call gradlew.bat lwjgl3:run
