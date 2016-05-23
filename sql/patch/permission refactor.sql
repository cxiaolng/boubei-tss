---------------------------- TSS ------------------------------------------
DROP TABLE IF EXISTS cms_permission_channel;
DROP TABLE IF EXISTS portal_permission_navigator;
DROP TABLE IF EXISTS portal_permission_portal;
DROP TABLE IF EXISTS um_permission_group;
DROP TABLE IF EXISTS um_permission_role;

ALTER  TABLE um_permissionfull_group RENAME TO um_permission_group;
ALTER  TABLE um_permissionfull_role RENAME TO um_permission_role;
ALTER  TABLE portal_permissionfull_portal RENAME TO portal_permission_portal;
ALTER  TABLE portal_permissionfull_navigator RENAME TO portal_permission_navigator;
ALTER  TABLE cms_permissionfull_channel RENAME TO cms_permission_channel;


DROP VIEW view_group_resources;
DROP VIEW view_role_resources;
DROP VIEW view_navigator_resources;
DROP VIEW view_portal_resources;
DROP VIEW view_channel_resources;

CREATE VIEW view_group_resource AS SELECT ID, parentId, NAME, DECODE FROM um_group ;
CREATE VIEW view_role_resource AS SELECT ID, parentId, NAME, DECODE FROM um_role;

CREATE VIEW view_portal_resource AS
SELECT 0 AS id, 'root' AS NAME, -1 AS parentId, '00001' AS DECODE FROM DUAL
UNION
SELECT id, NAME, parentId, DECODE FROM portal_structure;

CREATE VIEW view_navigator_resource AS
SELECT 0 AS id, 'root' AS NAME, -1 AS parentId, '00001' AS DECODE FROM DUAL
UNION
SELECT id, NAME, parentid, DECODE FROM portal_navigator;

CREATE VIEW view_channel_resource AS
SELECT -1 AS id, '全部' AS NAME, 0 AS parentId, '00001' AS DECODE FROM DUAL
UNION
SELECT id, NAME, parentId, DECODE FROM cms_channel;


ALTER TABLE um_resourcetype DROP COLUMN unSuppliedTable;
ALTER TABLE um_resourcetype CHANGE suppliedTable permissionTable VARCHAR(255) NOT NULL;

UPDATE um_resourcetype SET permissionTable = REPLACE( permissionTable, 'sFull', '');
UPDATE um_resourcetype SET resourceTable = REPLACE( resourceTable, 'View', '');
UPDATE um_resourcetype SET resourceTable = REPLACE( resourceTable, 'Resources', 'Resource');

--------------------- DM -------------------------------
DROP TABLE IF EXISTS dm_permission_report;
ALTER TABLE dm_permissionfull_report RENAME TO dm_permission_report;
DROP VIEW view_report_resources;

CREATE VIEW view_report_resource AS
SELECT 0 AS id, 'root' AS NAME, -1 AS parentId, '00001' AS DECODE FROM DUAL
UNION
SELECT id, NAME, parentId, DECODE FROM dm_report;
