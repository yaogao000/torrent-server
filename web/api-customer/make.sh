#!/bin/bash

cd `dirname $0` && 

echo "请输入编译配置选项：dev, alpha, prd, test中的一项"

read env

echo -e `\n选择的环境为：$env`

if [ $env == "dev" ]||[ $env == "alpha" ]||[ $env == "prd" ]||[ $env == "test" ];then
	mvn clean package -DportableConfig="src/main/portable/$env.xml"
else
	echo -e "\a\nplease setting the correct env parameter"
fi


