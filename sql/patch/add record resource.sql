二、新增record资源
1、资源配置
INSERT INTO `um_resourcetype_root`(`id`, `applicationId`, `resourceTypeId`, `rootId`)
	VALUES (7, 'tss', 'D2', 0);
        
INSERT INTO `um_resourcetype`(`id`, `lockVersion`, `applicationId`, `description`, `name`, `resourceTypeId`, `rootId`, `seqNo`, 
			`resourceTable`, `permissionTable`)
VALUES (7, 0, 'tss', '数据录入资源', '数据录入', 'D2', 0, 22,
        'com.boubei.tss.dm.record.permission.RecordResource', 'com.boubei.tss.dm.record.permission.RecordPermission');
        
INSERT INTO `um_operation`(`id`, `applicationId`, `dependId`, `dependParent`, `name`, `operationId`, `resourceTypeId`, `seqNo`)
VALUES (27, 'tss', null, '2', '查看录入', '1', 'D2', '1');

INSERT INTO `um_operation`(`id`, `applicationId`, `dependId`, `dependParent`, `name`, `operationId`, `resourceTypeId`, `seqNo`)
VALUES (28, 'tss', 'opt1', null, '维护录入', '2', 'D2', '2');

INSERT INTO `um_operation`(`id`, `applicationId`, `dependId`, `dependParent`, `name`, `operationId`, `resourceTypeId`, `seqNo`)
VALUES (29, 'tss', 'opt1', '2,3', '删除录入', '3', 'D2', '3');
 
  
三  、启动TSS后
1、待新表自动生成后，再初始化资源视图及设置root节点权限
drop table view_record_resource;

CREATE VIEW view_record_resource AS
SELECT 0 as id, 'root' as name, -1 as parentId, '00001' as decode FROM dual
UNION
SELECT id, name, parentId, decode FROM dm_record;

INSERT INTO `dm_permission_record` (`id`, `isGrant`, `isPass`, `operationId`, `permissionState`, `resourceId`, `roleId`, `resourceName`) VALUES('1','1','1','1','2','0','-1','root');
INSERT INTO `dm_permission_record` (`id`, `isGrant`, `isPass`, `operationId`, `permissionState`, `resourceId`, `roleId`, `resourceName`) VALUES('2','1','1','2','2','0','-1','root');
INSERT INTO `dm_permission_record` (`id`, `isGrant`, `isPass`, `operationId`, `permissionState`, `resourceId`, `roleId`, `resourceName`) VALUES('3','1','1','3','2','0','-1','root');
