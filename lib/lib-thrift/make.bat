@echo off
cd %~dp0
call idls/gen.bat
cd %~dp0
call mvn install
pause