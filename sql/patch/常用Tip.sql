DATE_ADD(now(),  INTERVAL 2 DAY)
date_format(日期字段,’%Y-%m-%d’)

注：在mysql里执行delete、update语句，有时会提示：rror Code: 1175. You are using safe update mode
   可以先执行：SET SQL_SAFE_UPDATES = 0;

// oracle
{
    "customizerClass": "com.boubei.tss.framework.persistence.connpool.ConnPoolCustomizer",
    "poolClass": "com.boubei.tss.cache.extension.ReusablePool",
    "code": "connpool-wms",
    "name": "DB连接池-WMS",
    "cyclelife": "1800000",
    "paramFile": "oracle.jdbc.driver.OracleDriver,jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = *.*.*.*)(PORT = 1521)) (CONNECT_DATA = (SERVER = DEDICATED) (SERVICE_NAME = wms))),wms,******",
    "interruptTime": "1000",
    "initNum":"1",
    "poolSize": "30"
}

// mysql
    "paramFile": "com.mysql.jdbc.Driver,jdbc:mysql://*.*.*.*:3306/wmsdata?useUnicode=true&amp;characterEncoding=utf8,xxx,*****",


SELECT loginName, email, userName, SUBSTRING( userName, 1, 1 ) FROM demo_bi.um_user where loginName like '%BL%'

update demo_bi.um_user 
    set loginName = replace(loginName, 'BL', 'XL'),
		email = replace(email, '800best', 'xxx'),
		userName = replace(userName, SUBSTRING( userName, 1, 1 ), '*')
	where loginName like '%BL%'
