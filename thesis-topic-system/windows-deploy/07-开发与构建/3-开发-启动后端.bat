@echo off
cd /d "%~dp0..\source\Back\hadluo-server"
call mvn spring-boot:run -Dspring-boot.run.profiles=prod
pause
