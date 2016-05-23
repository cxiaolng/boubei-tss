TOMCAT_HOME="/home/btrbi/tomcat7"

cd $TOMCAT_HOME/../
source .bash_profile

#找到tomcat进程的id并kill掉
ps -ef |grep tomcat7 |awk {'print $2'} | sed -e "s/^/kill -9 /g" | sh -

cd $TOMCAT_HOME/bin

./startup.sh

tail -f $TOMCAT_HOME/logs/catalina.out
