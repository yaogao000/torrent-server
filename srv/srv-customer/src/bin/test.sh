CLASSPATH=./
for jar in `ls lib/*.jar`
do
      CLASSPATH="$CLASSPATH:""./$jar"
done

if [ ! -d "./log" ]; then  
mkdir "./log"  
fi  

java -cp $CLASSPATH com.drink.srv.CustomerClientMain
