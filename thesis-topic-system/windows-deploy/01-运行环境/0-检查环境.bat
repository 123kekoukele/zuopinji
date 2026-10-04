@echo off
chcp 65001 >nul
where java >nul 2>&1 && java -version 2>&1 | findstr /i version || echo [X] java
where mvn >nul 2>&1 && mvn -version 2>&1 | findstr Maven || echo [X] mvn
where node >nul 2>&1 && node -v || echo [X] node
if exist "%~dp0local-paths.bat" call "%~dp0local-paths.bat"
pause
