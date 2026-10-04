@echo off
chcp 65001 >nul
if exist "%~dp0..\01-运行环境\local-paths.bat" call "%~dp0..\01-运行环境\local-paths.bat"
if not defined MYSQL_BIN set MYSQL_BIN=mysql
set DB=hadluo-lvyou
set SQL=%~dp0hadluo-lvyou.sql
"%MYSQL_BIN%" -uroot -p123456 -e "CREATE DATABASE IF NOT EXISTS \`%DB%\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
"%MYSQL_BIN%" -uroot -p123456 %DB% < "%SQL%"
echo Done.
pause
