mvn clean install -Pboubei -Dmaven.test.skip=true
cp webapp/target/tss.war /Users/jinpujun/Desktop/workspace/release/boubei


#boubei.com demoBI发布步骤
#  a、打包 deploy-boubei.sh，上传至 /home/tss/release
#  b、导出本地库，mysqldump -uroot -p800best@com demo_bi > /Users/jinpujun/Desktop/workspace/backup/demo_bi.sql
#  c、上传并导入，mysql -u root -p demo_bi   < /home/tss/temp/demo_bi.sql
#  d、./deploy.sh
#  e、启动后修改 栏目站点的文件目录地址， 并重新发布所有文章、重建索引
#  f、修改系统参数，默认导出目录：/home/tss/temp ；导出数据分流机器：http://www.boubei.com:8080

