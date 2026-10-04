@echo off
if exist "%~dp0..\01-运行环境\local-paths.bat" call "%~dp0..\01-运行环境\local-paths.bat"
if not defined REDIS_HOME set REDIS_HOME=D:\Redis
cd /d "%REDIS_HOME%"
start redis-server.exe redis.windows.conf
pause
