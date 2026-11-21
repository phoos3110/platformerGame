@echo off
setlocal
pushd "%~dp0"
if errorlevel 1 exit /b 1

where java >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java was not found. Install JDK 21 and add its bin folder to PATH.
    goto :error
)
where javac >nul 2>&1
if errorlevel 1 (
    echo ERROR: javac was not found. Install JDK 21 and add its bin folder to PATH.
    goto :error
)

if not exist bin mkdir bin
if not exist bin (
    echo ERROR: Could not create the bin folder.
    goto :error
)

echo Compiling game...
javac -encoding UTF-8 -d bin -sourcepath src src\module-info.java src\run\GameMain.java
if errorlevel 1 (
    echo ERROR: Compilation failed. Check the messages above.
    goto :error
)

echo Starting game...
java -cp "bin;assets" run.GameMain
if errorlevel 1 goto :error

popd
endlocal
exit /b 0

:error
pause
popd
endlocal
exit /b 1
