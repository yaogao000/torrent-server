@echo off
echo �������������ѡ�dev, alpha, alpha2, prd, test,test2�е�һ��
set /p env=>nul 
echo ���û�����%env%
cd %~dp0
call mvn clean package -Dmaven.test.skip=true -Denv=%env% -e
pause