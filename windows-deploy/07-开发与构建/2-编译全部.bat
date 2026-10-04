@echo off
chcp 65001 >nul
set SRC=%~dp0..\source
set OUT=%~dp0..\05-预编译产物
cd /d "%SRC%\Back\hadluo-server" && call mvn -q package -DskipTests
cd /d "%SRC%\Front\hadluo-vue" && call npm run build
cd /d "%SRC%\Front\hadluo-vue-admin" && call npm run build
copy /Y "%SRC%\Back\hadluo-server\target\cl3196870-0.0.1-SNAPSHOT.jar" "%OUT%\backend\"
copy /Y "%SRC%\Back\hadluo-server\src\main\resources\application-prod.yml" "%OUT%\backend\"
xcopy /E /Y /I "%SRC%\Front\hadluo-vue\dist\*" "%OUT%\frontend-user\"
xcopy /E /Y /I "%SRC%\Front\hadluo-vue-admin\dist\*" "%OUT%\frontend-admin\"
pause
