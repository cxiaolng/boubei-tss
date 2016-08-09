Alter table `dm_access_log` add index `accessTime_IDX` (`accessTime`);
Alter table `dm_access_log` add index `userId_IDX` (`userId`);
Alter table `dm_access_log` ADD INDEX `methodCnName_IDX` (`methodCnName`);
Alter table `dm_access_log` ADD INDEX `className_IDX` (`className`);

CHECK TABLE dm_access_log;
REPAIR TABLE dm_access_log;
ANALYZE TABLE dm_access_log;


SELECT COUNT(*) FROM dm_access_log t 

-- 清除日志
SELECT t.`methodCnName`, COUNT(*) FROM dm_access_log t WHERE `methodCnName` LIKE 'Get%' GROUP BY t.`methodCnName`
DELETE FROM dm_access_log  WHERE `methodCnName` LIKE 'Get%';


-- 设置标记，忽略记录访问日志
select * FROM btrbi.dm_report  WHERE `needLog` = 1 and `name` LIKE 'Get%';
update btrbi.dm_report set `needLog` = 0 WHERE `needLog` = 1 and `name` LIKE 'Get%';

delete FROM dm_access_log  WHERE accessTime  < DATE_SUB(CURDATE(), INTERVAL 30 DAY);
delete FROM component_log  WHERE operatetime < DATE_SUB(CURDATE(), INTERVAL 30 DAY);
DELETE FROM component_log  WHERE `operateTable` = '系统异常';

truncate table component_log;