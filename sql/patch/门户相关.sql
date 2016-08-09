select * FROM portal_theme_info WHERE structureId NOT IN (SELECT id FROM portal_structure);
DELETE FROM portal_theme_info WHERE structureId NOT IN (SELECT id FROM portal_structure);

-- 导入导出组件表
mysqldump -u root -p --tables demo_bi portal_component > /Users/jinpujun/Desktop/workspace/temp/1.sql
mysql -u root -p demo_bi < /Users/jinpujun/Desktop/workspace/temp/1.sql