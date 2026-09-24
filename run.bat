@echo off
setlocal EnableExtensions DisableDelayedExpansion
cd /d "%~dp0"
set "JAVAC=javac"
set "JAVA=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" (
  set "JAVAC=%JAVA_HOME%\bin\javac.exe"
  set "JAVA=%JAVA_HOME%\bin\java.exe"
)
"%JAVAC%" -version >nul 2>&1
if errorlevel 1 (
  echo JDK 17 or later is required. Select a JDK in your IDE or set JAVA_HOME.
  pause
  exit /b 1
)
if not exist build mkdir build
if exist build\classes rmdir /s /q build\classes
mkdir build\classes
powershell -NoProfile -Command "Get-ChildItem -LiteralPath 'src/main/java' -Filter '*.java' -Recurse | ForEach-Object { [char]34 + ('src/main/java/' + $_.FullName.Substring((Resolve-Path 'src/main/java').Path.Length + 1)).Replace([char]92,[char]47) + [char]34 } | Set-Content -Encoding UTF8 'build/sources.txt'"
if errorlevel 1 exit /b 1
rem Use a UTF-8 list without a byte order mark for javac.
powershell -NoProfile -Command "$p = Join-Path (Get-Location) 'build/sources.txt'; $t = [System.IO.File]::ReadAllText($p); [System.IO.File]::WriteAllText($p, $t, (New-Object System.Text.UTF8Encoding($false)))"
"%JAVAC%" --release 17 -encoding UTF-8 -d build\classes @build/sources.txt
if errorlevel 1 (
  echo Compilation failed. Check the messages above.
  pause
  exit /b 1
)
"%JAVA%" -cp build\classes alerts.Main
if errorlevel 1 pause
