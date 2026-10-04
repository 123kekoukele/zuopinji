@echo off
if exist "%~dp0..\01-运行环境\local-paths.bat" call "%~dp0..\01-运行环境\local-paths.bat"
if not defined NGINX_HOME set NGINX_HOME=D:\nginx
xcopy /E /Y /I "%~dp0..\05-预编译产物\frontend-user\*" "%NGINX_HOME%\html\"
if not exist "%NGINX_HOME%\html\admin" mkdir "%NGINX_HOME%\html\admin"
xcopy /E /Y /I "%~dp0..\05-预编译产物\frontend-admin\*" "%NGINX_HOME%\html\admin\"
cd /d "%NGINX_HOME%"
nginx.exe -t && start nginx.exe
pause
