truncate table um_group;

insert into um_group (ID, NAME, DISABLED, GROUPTYPE, PARENTID, SEQNO, LEVELNO, DECODE, lockVersion)
values (-1, 'root',     0, 0,  0, 1, 1, '00001', 0);

insert into um_group (ID, NAME, DISABLED, GROUPTYPE, PARENTID, SEQNO, LEVELNO, DECODE, lockVersion)
values (-2, '主用户组',     0, 1,  -1, 1, 2, '0000100001', 0);

insert into um_group (ID, NAME, DISABLED, GROUPTYPE, PARENTID, SEQNO, LEVELNO, DECODE, lockVersion)
values (-3, '辅助用户组',   0, 2,  -1, 2, 2, '0000100002', 0);

insert into um_group (ID, NAME, DISABLED, GROUPTYPE, PARENTID, SEQNO, LEVELNO, DECODE, lockVersion)
values (-7, '自注册用户组', 0, 1, -2, 1, 3, '000010000100001', 0);

commit;


truncate table um_role;

insert into um_role (ID, ISGROUP, PARENTID, NAME, SEQNO, DISABLED, LEVELNO, DECODE, lockVersion)
values (-6, 1, 0, '全部', 1, 0, 1, '00001', 0);

insert into um_role (ID, STARTDATE, ENDDATE, ISGROUP, PARENTID, NAME, SEQNO, DISABLED, LEVELNO, DECODE, lockVersion)
values (-1,     sysdate(), str_to_date('01/01/2099', '%m/%d/%Y'), 0, -6, '系统管理员', 1, 0, 2, '0000100001', 0);

insert into um_role (ID, STARTDATE, ENDDATE, ISGROUP, PARENTID, NAME, SEQNO, DISABLED, LEVELNO, DECODE, lockVersion)
values (-10000, sysdate(), str_to_date('01/01/2099', '%m/%d/%Y'), 0, -6, '匿名角色',   2, 0, 2, '0000100002', 0);

commit;

truncate table um_user;

insert into um_user (ID, DISABLED, ACCOUNTLIFE, AUTHMETHOD, LOGINNAME, PASSWORD, USERNAME, lockVersion)
values (-1, 0, str_to_date('01/01/2099', '%m/%d/%Y'), 'com.boubei.tss.um.sso.UMPasswordIdentifier', 'Admin', 'E5E0A2593A3AE4C038081D5F113CEC78', '系统管理员', 0);

insert into um_user (ID, DISABLED, ACCOUNTLIFE, AUTHMETHOD, LOGINNAME, PASSWORD, USERNAME, lockVersion)
values (-10000, 0, str_to_date('01/01/2099', '%m/%d/%Y'), null, 'ANONYMOUS', null, '匿名用户', 0);
commit;


truncate table um_groupuser;
insert into um_groupuser (ID, GROUPID, USERID) values (-1, -2, -1);
insert into um_groupuser (ID, GROUPID, USERID) values (-2, -2, -10000);
commit;

truncate table um_roleuser;
insert into um_roleuser (ID, ROLEID, USERID) values (0, -1, -1);
commit;

truncate table um_roleusermapping;
insert into um_roleusermapping(ROLEID, USERID) values (-10000, -10000);
commit;
