@echo off
echo 请输入编译配置选项：dev, alpha, prd, test中的一项
set /p env=>nul 
echo 配置环境：%env%
cd %~dp0
call mvn clean package -DportableConfig="src/main/portable/%env%.xml" -e
pause