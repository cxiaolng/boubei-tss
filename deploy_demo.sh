# deploy to my mac localhost

TOMCAT_HOME="/Users/jinpujun/Desktop/tomcat7063"

#找到tomcat进程的id并kill掉
ps -ef |grep tomcat  |awk {'print $2'} | sed -e "s/^/kill -9 /g" | sh -

#删除日志文件
rm  $TOMCAT_HOME/logs/* -rf

mvn clean install -Pdemo -Dmaven.test.skip=true


set -m

rm -rf $TOMCAT_HOME/webapps/tss

cp webapp/target/tss.war $TOMCAT_HOME/webapps

cd $TOMCAT_HOME/bin

./startup.sh

tail -f $TOMCAT_HOME/logs/catalina.out

