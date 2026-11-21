@echo off
cd /d "%~dp0"
java -cp "bin;assets" run.GameMain
if errorlevel 1 pause
