mvn clean install -Pboubei -Dmaven.test.skip=true
cp target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei


#boubei.com tssBi发布步骤【 用：tssbi / J..k*ng***6 来发布 】
#  a、打包 deploy-boubei.sh，上传至 /home/tssbi/release
#  d、./deploy.sh
#  c、【放开tssUtil.js的CNZZ统计的注释】 vim ../tomcat7/webapps/tss/tools/tssUtils.js


#boubei.com 机器被重启后需要手动重启mysql 和 nginx
# /etc/rc.d/init.d/mysqld start
# service nginx start


#如果要导出本地库到boubei.com环境，则：
#  1、导出本地库，mysqldump -uroot -p800best@com demo_bi > /Users/jinpujun/Desktop/workspace/backup/demo_bi.sql
#  2、上传并导入，mysql -u root -p demo_bi   < /home/tssbi/temp/demo_bi.sql
#  3、启动后修改 栏目站点的文件目录地址， 并重新发布所有文章、重建索引
#  4、修改系统参数，默认导出目录：/home/tssbi/temp ；导出数据分流机器：http://www.boubei.com:8080

#阿里云boubei.com重启步骤：
#  service nginx start
#  /etc/rc.d/init.d/mysqld start
#  ./home/tss/release/restart.sh

# 备份阿里云TSS库
# mysqldump -uroot -p demo_bi | gzip > /home/tssbi/mysql_bk/demo_bi_`date '+%m-%d-%Y'`.sql.gz