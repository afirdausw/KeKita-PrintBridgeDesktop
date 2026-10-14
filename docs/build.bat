@echo off
setlocal enabledelayedexpansion
title PrintBridge - Build Release

rem ============================================================
rem  PrintBridge - build release 1 klik
rem  Hasil: dist\PrintBridge\PrintBridge.exe (portable, tanpa Java)
rem ============================================================

rem Pindah ke root project (folder di atas docs\)
cd /d "%~dp0.."

set APP_NAME=PrintBridge
set APP_VERSION=1.0.0
set MAIN_CLASS=devyana.kekita.printbridge.Main
set ICON=src\main\resources\icons\icon.ico
set STAGE=%TEMP%\pbstage
set MODULES=java.base,java.desktop,java.instrument,java.management,java.naming,java.security.jgss,java.sql,java.logging,java.xml,jdk.unsupported,jdk.crypto.ec

rem --- Pilih JDK 21: PB_JDK (opsional) -> %USERPROFILE%\.jdks\jbr-21* / jdk-21* -> JAVA_HOME bawaan ---
rem Hanya berlaku selama skrip ini berjalan, environment Windows tidak diubah.
set PB_JDK_FOUND=
if defined PB_JDK if exist "%PB_JDK%\bin\jpackage.exe" set PB_JDK_FOUND=%PB_JDK%
if not defined PB_JDK_FOUND (
    for /d %%D in ("%USERPROFILE%\.jdks\jbr-21*" "%USERPROFILE%\.jdks\*-21*") do (
        if exist "%%~D\bin\jpackage.exe" set PB_JDK_FOUND=%%~D
    )
)
if defined PB_JDK_FOUND (
    set "JAVA_HOME=%PB_JDK_FOUND%"
    set "PATH=%PB_JDK_FOUND%\bin;%PATH%"
)

rem --- Cek jpackage ---
where jpackage >nul 2>&1
if errorlevel 1 (
    echo [ERROR] jpackage tidak ditemukan. Pasang JDK 21 atau set PB_JDK ke folder JDK.
    goto :fail
)
echo Memakai JDK:
java -version 2>&1 | findstr /i "version"

rem --- Cari Maven: PATH -> MAVEN_HOME -> bawaan IntelliJ ---
set MVN=
where mvn >nul 2>&1
if not errorlevel 1 set MVN=mvn
if not defined MVN if defined MAVEN_HOME if exist "%MAVEN_HOME%\bin\mvn.cmd" set MVN="%MAVEN_HOME%\bin\mvn.cmd"
if not defined MVN (
    for /d %%D in ("%ProgramFiles%\JetBrains\*") do (
        if exist "%%D\plugins\maven\lib\maven3\bin\mvn.cmd" set MVN="%%D\plugins\maven\lib\maven3\bin\mvn.cmd"
    )
)
if not defined MVN (
    echo [ERROR] Maven tidak ditemukan. Pasang Maven atau set MAVEN_HOME.
    goto :fail
)

rem --- 1. Build JAR ---
echo.
echo [1/3] Build JAR dengan Maven...
call %MVN% clean package -q
if errorlevel 1 (
    echo [ERROR] Build Maven gagal.
    goto :fail
)

rem --- Cari JAR hasil shade (bukan original-/shaded) ---
set JAR=
for %%F in (target\printbridge-*.jar) do (
    set NAME=%%~nxF
    if /i not "!NAME:~0,9!"=="original-" if /i "!NAME:-shaded=!"=="!NAME!" set JAR=!NAME!
)
if not defined JAR (
    echo [ERROR] JAR hasil build tidak ditemukan di target\.
    goto :fail
)
echo      JAR: %JAR%

rem --- 2. Siapkan folder staging (hanya berisi JAR) ---
echo.
echo [2/3] Menyiapkan paket...
if exist "%STAGE%" rmdir /s /q "%STAGE%"
if exist "dist\%APP_NAME%" rmdir /s /q "dist\%APP_NAME%"
mkdir "%STAGE%"
if not exist dist mkdir dist
copy /y "target\%JAR%" "%STAGE%\" >nul

rem --- 3. Buat paket .exe dengan runtime bawaan ---
echo.
echo [3/3] Membuat PrintBridge.exe dengan jpackage...
jpackage --type app-image --name %APP_NAME% --app-version %APP_VERSION% ^
  --input "%STAGE%" --main-jar "%JAR%" --main-class %MAIN_CLASS% ^
  --icon %ICON% --dest dist ^
  --add-modules %MODULES% ^
  --jlink-options "--strip-debug --no-header-files --no-man-pages --compress=0"
if errorlevel 1 (
    echo [ERROR] jpackage gagal.
    goto :fail
)

if exist "%STAGE%" rmdir /s /q "%STAGE%"

echo.
echo ============================================================
echo  SELESAI
echo  Hasil: %CD%\dist\%APP_NAME%\%APP_NAME%.exe
echo  Zip folder dist\%APP_NAME% lalu salin ke komputer tujuan.
echo ============================================================
explorer "dist\%APP_NAME%"
pause
exit /b 0

:fail
echo.
echo Build dibatalkan.
pause
exit /b 1
