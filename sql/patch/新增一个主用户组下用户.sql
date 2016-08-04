insert into um_user (ID, DISABLED, ACCOUNTLIFE, AUTHMETHOD, LOGINNAME, PASSWORD, USERNAME, lockVersion)
values (-9999, 0, str_to_date('01/01/2099', '%m/%d/%Y'), 'com.boubei.tss.um.sso.UMPasswordIdentifier', 'biAdmin', '13E3FDD3ABFA294BD6707BDE2BDC8C32', 'BI管理员', 0);

insert into um_groupuser(ID, GROUPID, USERID) values (-9999, -2, -9999);
insert into um_roleuser (ID, ROLEID, USERID)  values (-9999, 296, -9999);
commit;