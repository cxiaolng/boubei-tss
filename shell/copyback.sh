
TOMCAT_HOME="/home/btrbi/tomcat7"

rm -rf $TOMCAT_HOME/webapps/tss/modules/btr
rm -rf $TOMCAT_HOME/webapps/tss/modules/tools
cp -r backup/tss/modules/btr  $TOMCAT_HOME/webapps/tss/modules/
cp -r backup/tss/modules/tools  $TOMCAT_HOME/webapps/tss/modules/
