#!/bin/bash

echo --------------start gen idls---------------

echo "idls path: "$(cd `dirname $0`; pwd)

current=$(cd `dirname $0`; pwd);

for i in `ls $current`
    	do
			#echo $i;
			currenti="$current/$i";
			#echo $currenti;
	       	if [ -e "$currenti/srv.thrift" ];then
				thrift -out $current/../src/main/java/ -r --gen java $currenti/srv.thrift;
	        	echo "$i";
	        fi
    	done
