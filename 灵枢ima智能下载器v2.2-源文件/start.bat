@echo off
chcp 65001 >nul
echo ========================================
echo LingShu IMA Knowledge Base Downloader V2.2
echo ========================================
echo.

REM Set encoding
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8

REM Check Java environment
echo [1/4] Checking Java environment...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found. Please install Java 8 or higher
    pause
    exit /b 1
)
echo Java environment OK
echo.

REM Create necessary directories
echo [2/4] Creating directories...
if not exist "downloads" mkdir downloads
if not exist "logs" mkdir logs
if not exist "bin" mkdir bin
echo Directories created
echo.

REM Compile Java source files
echo [3/4] Compiling Java source files...
javac -encoding UTF-8 -cp "lib\*;config" -d bin ^
    src\com\lingshu\ima\config\*.java ^
    src\com\lingshu\ima\dto\*.java ^
    src\com\lingshu\ima\dto\helper\*.java ^
    src\com\lingshu\ima\service\*.java ^
    src\com\lingshu\ima\ui\UnifiedMainFrame.java ^
    src\com\lingshu\ima\UnifiedMain.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation failed
    pause
    exit /b 1
)
echo Compilation successful
echo.

REM Start program
echo [4/4] Starting program...
echo.
echo Program is starting, please wait...
echo If the GUI window does not appear, check Java installation
echo.
java -Xmx512m -cp "lib\jackson-annotations-2.15.2.jar;lib\jackson-core-2.15.2.jar;lib\jackson-databind-2.15.2.jar;lib\lombok-1.18.30.jar;lib\okhttp-4.12.0.jar;lib\okio-3.4.0.jar;config;bin" com.lingshu.ima.UnifiedMain

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Program execution failed
    echo Error code: %errorlevel%
    echo.
    echo Possible reasons:
    echo 1. Java version incompatible (need Java 8+)
    echo 2. Missing dependencies
    echo 3. Config file corrupted
    echo 4. Insufficient permissions
    echo.
    echo Please check and try again
    pause
    exit /b 1
)

echo.
echo Program exited normally
pause
