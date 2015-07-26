cd `dirname $0`

CLASSPATH=./
for jar in `ls lib/*.jar`
do
      CLASSPATH="$CLASSPATH:""./$jar"
done

if [ ! -d "./log" ]; then  
mkdir "./log"  
fi  

java -Xms2048M -Xmx4096M -XX:PermSize=64M -XX:MaxPermSize=256M -cp $CLASSPATH com.drink.srv.CustomerServiceMain $1 >> log/log.txt 2>&1 &
