@echo off

:: Set paths for the project
set SRC_DIR=src
set BIN_DIR=bin
set LIB_DIR=lib
set LIB_JARS=%LIB_DIR%\gson-2.11.0.jar

:: Compile the Java files
echo Compiling Java files...
javac -cp "%LIB_JARS%" -d %BIN_DIR% %SRC_DIR%\*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)

:: Run the application
echo Running Weather Application...
java -cp "%BIN_DIR%;%LIB_JARS%" WeatherSwingApp
if %errorlevel% neq 0 (
    echo Application encountered an error.
    pause
    exit /b
)

pause
