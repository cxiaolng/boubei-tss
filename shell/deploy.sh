
TOMCAT_HOME="/home/btrbi/tomcat7"
BACKUP_HOME="/home/btrbi/release"

cd $TOMCAT_HOME/../
source .bash_profile

#找到tomcat进程的id并kill掉
ps -ef |grep tomcat7 |awk {'print $2'} | sed -e "s/^/kill -9 /g" | sh -

#删除日志文件
rm  $TOMCAT_HOME/logs/* -rf
#touch $TOMCAT_HOME/logs/catalina.out

#备份现有版本包
rm -rf $BACKUP_HOME/backup/tss
mv $TOMCAT_HOME/webapps/tss $BACKUP_HOME/backup/

set -m

#rm -rf $TOMCAT_HOME/webapps/tss
rm -rf $TOMCAT_HOME/webapps/tss.war

cp $BACKUP_HOME/tss.war $TOMCAT_HOME/webapps

cd $TOMCAT_HOME/bin

./startup.sh

tail -f $TOMCAT_HOME/logs/catalina.out
