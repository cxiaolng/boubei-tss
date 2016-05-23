package com.boubei.tss.dm.record;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.AbstractDMTest;
import com.boubei.tss.dm.record.ddl._Database;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.util.EasyUtils;

public class _DatabaseTest extends AbstractDMTest  {

/*
public class _DatabaseTest  {
	@Before
	public void setUp() {
		OperatorDTO loginUser = new OperatorDTO(UMConstants.ADMIN_USER_ID, UMConstants.ADMIN_USER_NAME);
    	String token = TokenUtil.createToken("1234567890", UMConstants.ADMIN_USER_ID); 
        IdentityCard card = new IdentityCard(token, loginUser);
        Context.initIdentityInfo(card);
	}
*/
	
	@Test
	public void testMySQL() {
		testDB("MySQL", DMConstants.LOCAL_CONN_POOL); // 暂通过H2模拟
	}
	
	@Test
	public void testOracle() {
		testDB("Oracle", DMConstants.LOCAL_CONN_POOL); // 暂通过H2模拟
	}
	
	private void testDB(String type, String datasource) {
		String tblDefine = "[ {'label':'类型', 'code':'f1', 'type':'number', 'nullable':'false'}," +
        		"{'label':'名称', 'code':'f2', 'type':'string'}," +
        		"{'label':'时间', 'code':'f3', 'type':'date', 'nullable':'false'}]";
		
		Record record = new Record();
		record.setId(1L);
		record.setDatasource(datasource);
		record.setTable(type + "_tbl_2");
		record.setDefine(tblDefine);
		
		_Database _db = _Database.getDB(type, record);
		_db.createTable();
		
		// test update table with change table name
		record = new Record();
		record.setId(2L);
		record.setDatasource(datasource);
		record.setTable(type + "_tbl_3");
		record.setDefine(tblDefine);
		
		_db.updateTable(record);
		
		// test update table with row = 0
		tblDefine = "[ {'label':'类型', 'code':'f1', 'type':'number', 'nullable':'false'}," +
        		"{'label':'名称', 'code':'f2', 'type':'string', 'nullable':'false'}," +
        		"{'label':'时间', 'code':'f3', 'type':'datetime'}," +
        		"{'label':'UDF', 'code':'f5', 'type':'string'}]";
		record = new Record();
		record.setId(3L);
		record.setDatasource(datasource);
		record.setTable(type + "_tbl_3");
		record.setDefine(tblDefine);
		record.setCustomizeJS(" creator = '${userCode}' ");
		
		_db.updateTable(record);
		
		// test insert
		Map<String, String> valuesMap = new HashMap<String, String>();
		valuesMap.put("f1", "10.9");
		valuesMap.put("f2", "just test");
		valuesMap.put("f3", "2015-04-05");
		_db.insert(valuesMap);
		
		List<Map<String, Object>> result = _db.select().result;
		Assert.assertTrue(result.size() == 1);
		
		Map<String, Object> row = result.get(0);
		Long id = EasyUtils.obj2Long( row.get("id") );
		Assert.assertEquals(10.9, row.get("f1"));
		Assert.assertNotNull(row.get("createtime"));
		Assert.assertEquals(0, EasyUtils.obj2Int( row.get("version") ).intValue());
		
		// test update
		valuesMap = new HashMap<String, String>();
		valuesMap.put("f1", "12");
		valuesMap.put("f2", "just test");
		valuesMap.put("f3", "2015-04-05");
		_db.update(id, valuesMap);
		
		result = _db.select().result;
		Assert.assertTrue(result.size() == 1);
		row = result.get(0);
		Assert.assertEquals(12.0, row.get("f1"));
		Assert.assertEquals("just test", row.get("f2"));
		Assert.assertNotNull(row.get("updatetime"));
		Assert.assertEquals(1, EasyUtils.obj2Int( row.get("version") ).intValue());
		
		// test update table with row > 0
		tblDefine = "[ {'label':'类型', 'code':'f1', 'type':'number', 'nullable':'false'}," +
        		"{'label':'名称', 'code':'f2', 'type':'string', 'height':'180px'}," +
        		"{'label':'时间2', 'code':'f4', 'type':'datetime', 'nullable':'false'}]";
		
		record = new Record();
		record.setId(4L);
		record.setDatasource(datasource);
		record.setTable(type + "_tbl_3");
		record.setDefine(tblDefine);
		
		_db.updateTable(record);
		result = _db.select().result;
		Assert.assertTrue(result.size() == 1);
		
		// test insert
		valuesMap = new HashMap<String, String>();
		valuesMap.put("f1", "10.9");
		valuesMap.put("f2", "just test");
		valuesMap.put("f4", "2015-04-05");
		_db.insert(valuesMap);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("f2", "just test");
		params.put("creator", UMConstants.ADMIN_USER_NAME);
		params.put("sortField", "f4");
		params.put("sortType", "desc");
		result = _db.select(1, 10, params).result;
		Assert.assertTrue(result.size() == 2);
		
		params.put("sortType", "onlynull");
		result = _db.select(1, 10, params).result;
		Assert.assertTrue(result.size() == 1);
		
		params.remove("sortType");
		params.put("f4", "[2015-04-04,2015-04-06]");
		result = _db.select(1, 10, params).result;
		Assert.assertTrue(result.size() == 1);
		
		params.put("f2", "no test");
		params.put("updator", UMConstants.ADMIN_USER_NAME);
		result = _db.select(1, 10, params).result;
		Assert.assertTrue(result.size() == 0);
		
		// test delete
		result = _db.select().result;
		Assert.assertTrue(result.size() == 2);
		id = EasyUtils.obj2Long( result.get(0).get("id") );
		
		_db.delete(id);
		Assert.assertTrue(_db.select().result.size() == 1);
	}
}
