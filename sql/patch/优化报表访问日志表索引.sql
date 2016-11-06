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

select count(*) FROM component_log l where l.operateTable='系统异常';

-- 将日志变成分区表
ALTER TABLE dm_access_log CHANGE COLUMN accessTime accessTime DATETIME NOT NULL  
, DROP PRIMARY KEY 
, ADD PRIMARY KEY (id, accessTime);

alter table dm_access_log
partition by range(year(accessTime)*100+month(accessTime))
(
	partition p201601 values less than (201601),
	partition p201602 values less than (201602),
	partition p201603 values less than (201603),
	partition p201604 values less than (201604),
	partition p201605 values less than (201605),
	partition p201606 values less than (201606),
	partition p201607 values less than (201607),
	partition p201608 values less than (201608),
	partition p201609 values less than (201609),
	partition p201610 values less than (201610),
	partition p201611 values less than (201611),
	partition p201612 values less than (201612),
	PARTITION p0 values less than  MAXVALUE
);

alter table dm_access_log add partition (partition p201701 values less than(201701)); 
