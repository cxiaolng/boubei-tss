  -- Drop TBL_TEMP table
  drop table TBL_TEMP cascade constraints;
  
  -- Create TBL_TEMP table
  create global temporary table TBL_TEMP
    (
      ID NUMBER(10) not null, 
      udf1 varchar2(255 char), 
      udf2 varchar2(255 char), 
      udf3 varchar2(255 char)
    )
    on commit delete rows;
  -- Create/Recreate primary, unique and foreign key constraints 
    alter table TBL_TEMP
    add primary key (ID);

    
truncate table component_param;
insert into component_param 
	(id, code, name, value, text, decode, hidden, lockVersion) 
values
	(0, '0', '0', '0', '0', '00000', 1, 0);
commit;
