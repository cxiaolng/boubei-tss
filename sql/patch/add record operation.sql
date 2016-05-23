1、新增record资源操作项

update um_operation set name = '录入数据' where id = 27;
update um_operation set name = '维护录入表' where id = 28;
update um_operation set name = '删除录入表' where id = 29;
update um_operation set seqNo = 3 where id = 27;
update um_operation set seqNo = 1 where id = 28;
update um_operation set seqNo = 2 where id = 29;
 
INSERT INTO `um_operation`(`id`, `applicationId`, `dependId`, `dependParent`, `name`, `operationId`, `resourceTypeId`, `seqNo`)
VALUES (30, 'tss', null, null, '浏览数据', '4', 'D2', '4');

INSERT INTO `um_operation`(`id`, `applicationId`, `dependId`, `dependParent`, `name`, `operationId`, `resourceTypeId`, `seqNo`)
VALUES (31, 'tss', 'opt4', null, '维护数据', '5', 'D2', '5');
 
-- 把所有资源对应新增操作项的权限授给Admin
INSERT INTO `dm_permission_record` (`id`, `isGrant`, `isPass`, `operationId`, `permissionState`, `resourceId`, `roleId`, `resourceName`)
	SELECT 10000 + id, '1', '1', '4', '2', t.id, -1, t.name FROM view_record_resource t;
INSERT INTO `dm_permission_record` (`id`, `isGrant`, `isPass`, `operationId`, `permissionState`, `resourceId`, `roleId`, `resourceName`)
	SELECT 20000 + id, '1', '1', '5', '2', t.id, -1, t.name FROM view_record_resource t;
	
	
