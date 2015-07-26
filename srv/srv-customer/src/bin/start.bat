@echo off
SetLocal EnableDelayedExpansion   
SET CLASSPATH=.
FOR %%i IN ("./lib/*.jar") DO SET CLASSPATH=!CLASSPATH!;lib\%%i

echo %CLASSPATH%
java -cp %CLASSPATH% com.drink.srv.CustomerServiceMain %1

EndLocal
