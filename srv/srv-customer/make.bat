@echo off
echo 请输入编译配置选项：dev, alpha, alpha2, prd, test,test2中的一项
set /p env=>nul 
echo 配置环境：%env%
cd %~dp0
call mvn clean package -Dmaven.test.skip=true -Denv=%env% -e
pause