@echo off
setlocal EnableDelayedExpansion

rem ---------------------------------------------------------------------------
rem  Builds and installs the pgvector extension into a native Windows
rem  PostgreSQL installation.
rem
rem  Needed because pgvector ships no Windows binaries and is not offered by
rem  Stack Builder, while auto-hub-ai's Liquibase migration
rem  (db/changelog/vector-store-changelog.sql) runs CREATE EXTENSION vector.
rem  Without the extension present on the server, startup fails with:
rem      extension "vector" is not available
rem
rem  Re-run this after every MAJOR PostgreSQL upgrade (18 -> 19): the compiled
rem  DLL is tied to the server's ABI. Minor upgrades (18.3 -> 18.4) are fine.
rem
rem  Usage (auto-elevates via UAC):
rem      scripts\install-pgvector.bat                 rem PG 18, pgvector v0.8.6
rem      scripts\install-pgvector.bat 19              rem PG 19, pgvector v0.8.6
rem      scripts\install-pgvector.bat 19 v0.9.0       rem explicit versions
rem
rem  Requires: git, and Visual Studio Build Tools with the C++ workload.
rem  Does NOT require a PostgreSQL restart - pgvector is not a
rem  shared_preload_library, so it becomes available immediately.
rem ---------------------------------------------------------------------------

set "PG_VERSION=%~1"
set "PGVECTOR_TAG=%~2"
set "ELEVATED=%~3"

if "%PG_VERSION%"=="" set "PG_VERSION=18"
if "%PGVECTOR_TAG%"=="" set "PGVECTOR_TAG=v0.8.6"

set "VSWHERE=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"
set "PGROOT=C:\Program Files\PostgreSQL\%PG_VERSION%"
set "BUILD_DIR=%TEMP%\pgvector-build-%PGVECTOR_TAG%"

echo.
echo  pgvector %PGVECTOR_TAG%  ^-^>  PostgreSQL %PG_VERSION%
echo  PGROOT: %PGROOT%
echo.

rem --- Installing into Program Files needs elevation; relaunch through UAC ----
net session >nul 2>&1
if errorlevel 1 (
    echo Administrator rights are required to write into "%PGROOT%".
    echo Requesting elevation - please accept the UAC prompt...
    powershell -NoProfile -Command "Start-Process -FilePath '%~f0' -ArgumentList '%PG_VERSION%','%PGVECTOR_TAG%','--elevated' -Verb RunAs"
    exit /b 0
)

rem --- Prerequisites ---------------------------------------------------------
if not exist "%PGROOT%\lib\postgres.lib" (
    echo ERROR: no PostgreSQL %PG_VERSION% development files at "%PGROOT%".
    echo        Check the version argument, or install the PostgreSQL headers.
    goto :fail
)

where git >nul 2>&1
if errorlevel 1 (
    echo ERROR: git was not found on PATH.
    goto :fail
)

if not exist "%VSWHERE%" (
    echo ERROR: vswhere.exe not found - Visual Studio Build Tools are missing.
    echo        Install them with the "Desktop development with C++" workload.
    goto :fail
)

set "VSPATH="
for /f "usebackq tokens=*" %%i in (`"%VSWHERE%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do set "VSPATH=%%i"

if "%VSPATH%"=="" (
    echo ERROR: no Visual Studio installation with the C++ toolset was found.
    echo        Install the "Desktop development with C++" workload.
    goto :fail
)

set "VCVARS=%VSPATH%\VC\Auxiliary\Build\vcvars64.bat"
if not exist "%VCVARS%" (
    echo ERROR: vcvars64.bat not found at "%VCVARS%".
    goto :fail
)

rem --- Fetch sources ---------------------------------------------------------
rem  Always clone fresh: object files compiled against another PostgreSQL
rem  version's headers link badly, and that failure mode is hard to read.
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"

echo Cloning pgvector %PGVECTOR_TAG%...
git clone --quiet --branch %PGVECTOR_TAG% --depth 1 https://github.com/pgvector/pgvector.git "%BUILD_DIR%"
if errorlevel 1 (
    echo ERROR: clone failed - is "%PGVECTOR_TAG%" a valid tag?
    goto :fail
)

rem --- Build and install -----------------------------------------------------
echo Loading the MSVC x64 environment...
call "%VCVARS%" >nul

cd /d "%BUILD_DIR%"

echo Compiling...
nmake /F Makefile.win
if errorlevel 1 (
    echo ERROR: compilation failed. Sources kept at "%BUILD_DIR%".
    goto :fail
)

echo Installing into "%PGROOT%"...
nmake /F Makefile.win install
if errorlevel 1 (
    echo ERROR: install failed. Sources kept at "%BUILD_DIR%".
    goto :fail
)

if not exist "%PGROOT%\share\extension\vector.control" (
    echo ERROR: vector.control is missing after install - something went wrong.
    goto :fail
)

cd /d "%TEMP%"
rmdir /s /q "%BUILD_DIR%"

echo.
echo  Done. vector.control and vector.dll are installed.
echo.
echo  Verify against the server:
echo      SELECT default_version FROM pg_available_extensions WHERE name = 'vector';
echo.
echo  New databases: Liquibase creates the extension on startup.
echo  Databases that already had an older pgvector: ALTER EXTENSION vector UPDATE;
echo  No PostgreSQL restart is needed.
echo.
if "%ELEVATED%"=="--elevated" pause
exit /b 0

:fail
echo.
echo  Installation aborted.
echo.
if "%ELEVATED%"=="--elevated" pause
exit /b 1