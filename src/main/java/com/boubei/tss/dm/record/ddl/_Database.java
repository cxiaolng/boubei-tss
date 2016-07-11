package com.boubei.tss.dm.record.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.dom4j.Document;

import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.DMUtil;
import com.boubei.tss.dm.data.sqlquery.SQLExcutor;
import com.boubei.tss.dm.record.Record;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.record.permission.RecordResource;
import com.boubei.tss.cache.Cacheable;
import com.boubei.tss.cache.JCache;
import com.boubei.tss.cache.Pool;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.modules.log.IBusinessLogger;
import com.boubei.tss.modules.log.Log;
import com.boubei.tss.modules.param.ParamConstants;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.util.EasyUtils;
import com.boubei.tss.util.XMLDocUtil;

public abstract class _Database {
	
	static Logger log = Logger.getLogger(_Database.class);
	
	public Long recordId;
	public String recordName;
	public String datasource;
	public String table;
	public String customizeTJ;
	
	private boolean needLog;
	public boolean needFile;
	
	List<Map<Object, Object>> fields;
	public List<String> fieldCodes;
	public List<String> fieldTypes;
	public List<String> fieldNames;
	
	public String toString() {
		return "【" + this.datasource + ", " + this.recordName + ", " + this.table + "】";
	}
	
	public _Database(Record record) {
		this.recordId = record.getId();
		this.recordName = record.getName();
		this.datasource = record.getDatasource();
		this.table = record.getTable();
		this.fields = parseJson(record.getDefine());
		this.customizeTJ = record.getCustomizeTJ();
		this.needLog = ParamConstants.TRUE.equals(record.getNeedLog());
		this.needFile = ParamConstants.TRUE.equals(record.getNeedFile());
		
		this.initFieldCodes();
	}
	
	protected void initFieldCodes() {
		this.fieldCodes = new ArrayList<String>();
		this.fieldTypes = new ArrayList<String>();
		this.fieldNames = new ArrayList<String>();
		for(Map<Object, Object> fDefs : this.fields) {
			this.fieldCodes.add((String) fDefs.get("code"));
			this.fieldTypes.add((String) fDefs.get("type"));
			this.fieldNames.add((String) fDefs.get("label"));
		}
	}
	
	@SuppressWarnings("unchecked")
	protected List<Map<Object, Object>> parseJson(String define) {
		if(EasyUtils.isNullOrEmpty(define)) {
			return new ArrayList<Map<Object,Object>>();
		}
		
		define = define.replaceAll("'", "\"");
		
		try {  
   			List<Map<Object, Object>> list = new ObjectMapper().readValue(define, List.class);  
   			for(int i = 0; i < list.size(); i++) {
   	        	Map<Object, Object> fDefs = list.get(i);
   	        	int index = i + 1;
   	        	
   				String code = (String) fDefs.get("code");
   				code = (EasyUtils.isNullOrEmpty(code) ? "f" + index : code);
   				fDefs.put("code", code);
   			}
   			return list;
   	    } 
   		catch (Exception e) {  
   	        String errorMsg = "【" + recordName + "】的参数配置有误，JSON格式存在错误，请检查修正后再保存。具体原因：" + e.getMessage();
			throw new BusinessException(errorMsg);
   	    } 
	}
	
	protected abstract String getFieldType(Map<Object, Object> fDefs);
	
	public abstract void createTable();
	
	public void dropTable(String table, String datasource) {
		SQLExcutor.excute("drop table " + this.table, datasource);
	}
	
	public void updateTable(Record _new) {
		String newDS = _new.getDatasource();
		String table = _new.getTable();
		this.customizeTJ = _new.getCustomizeJS();
		this.needLog = ParamConstants.TRUE.equals(_new.getNeedLog());
		this.needFile = ParamConstants.TRUE.equals(_new.getNeedFile());
		
		if(!newDS.equals(this.datasource) || !table.equals(this.table)) {
			this.datasource = newDS;
			this.table = table;
			createTable();
			return;
		}
		
		// 比较新旧字段定义的异同（新增的和删除的，暂时只关心新增的）
		List<Map<Object, Object>> newFields = parseJson(_new.getDefine());
				
		// 新增加的字段
		int index = 0;
		for(Map<Object, Object> fDefs1 : newFields) {
			String code = (String) fDefs1.get("code");
			code = (EasyUtils.isNullOrEmpty(code) ? "f" + (++index) : code);
			fDefs1.put("code", code);
			
			boolean exsited = false;
        	for(Map<Object, Object> fDefs2 : this.fields ) {
        		if(code.equals(fDefs2.get("code"))) {
        			exsited = true; 
        			//TODO 进一步判断字段类型及长度，及是否可空等有无发生变化
        		}
        	}
        	
        	if( !exsited ) {
        		String fieldType = getFieldType(fDefs1);
        		try {
    				SQLExcutor.excute("alter table " + this.table + " add " + code + " " + fieldType, newDS);
    			} catch(Exception e) { }
        	}
		}
		
		// 被删除的字段（原来有的，在新的定义里没有了）
		for(Map<Object, Object> fDefs2 : this.fields ) {
			Object oldCode = fDefs2.get("code");
			boolean exsited = false;
    		for(Map<Object, Object> fDefs1 : newFields) {
    			String code = (String) fDefs1.get("code");
 
				if(code.equals(oldCode)) {
        			exsited = true; 
        		}
    		}
    		if( !exsited ) {
    			try {
    				SQLExcutor.excute("alter table " + this.table + " drop column " + oldCode, newDS);
    			} catch(Exception e) { }
        	}
    	}
		
		this.fields = newFields;
		initFieldCodes();
	}
	
	public void insert(Map<String, String> valuesMap) {
		Map<Integer, Object> paramsMap = buildInsertParams(valuesMap);
		SQLExcutor.excute(createInsertSQL(), paramsMap, this.datasource);
	}

	private Map<Integer, Object> buildInsertParams(Map<String, String> valuesMap) {
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		int index = 0;
		for(String field : this.fieldCodes) {
			Object value = DMUtil.preTreatValue(valuesMap.get(field), fieldTypes.get(index));
			paramsMap.put(++index, value);
		}
		paramsMap.put(++index, new Timestamp(new Date().getTime())); 
		paramsMap.put(++index, Environment.getUserCode());
		paramsMap.put(++index, 0);
		return paramsMap;
	}
	
	public void insertBatch(Collection<Map<String, String>> valuesMaps) {
		if(valuesMaps == null || valuesMaps.isEmpty()) return;
		
		List<Map<Integer, Object>> paramsList = new ArrayList<Map<Integer,Object>>();
		for(Map<String, String> valuesMap : valuesMaps) {
			Map<Integer, Object> paramsMap = buildInsertParams(valuesMap);
			paramsList.add(paramsMap);
		}
		
		// 判断是否是匿名用户。匿名用户（登录超时后变的）不允许新增数据
		if( Environment.isAnonymous() ) {
			throw new BusinessException("您当前的登录已超时，请注销后重新登录！");
		}
		SQLExcutor.excuteBatch(createInsertSQL(), paramsList , this.datasource);
	}
	
	protected String createInsertSQL() {
		String valueTags = "", fieldTags = "";
		for(String field : this.fieldCodes) {
			valueTags += "?,";
			fieldTags += field + ",";
		}
		String insertSQL = "insert into " + this.table + "(" + fieldTags + "createtime,creator,version) " +
				" values (" + valueTags + " ?, ?, ?)";
		return insertSQL;
	}

	public void update(Long id, Map<String, String> valuesMap) {
		Map<String, Object> old = get(id);
		if( old == null ) {
			throw new BusinessException("修改出错，该记录不存在，可能已经被删除。");
		}
		
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		int index = 0;
		String tags = "";
		for(String field : this.fieldCodes) {
			Object value = valuesMap.get(field);
			value = DMUtil.preTreatValue((String)value, fieldTypes.get(index));
			
			paramsMap.put(++index, value);
			tags += field + "=?, ";
		}
		paramsMap.put(++index, new Timestamp(new Date().getTime()));
		paramsMap.put(++index, Environment.getUserCode());
		paramsMap.put(++index, id);
		
		String updateSQL = "update " + this.table + " set " + tags + "updatetime=?, updator=?, version=version+1 where id=?";
		SQLExcutor.excute(updateSQL, paramsMap, this.datasource);
		
		if(this.needLog) { // 记录修改日志
			Log excuteLog = new Log(recordName + ", " + id, "\n修改前： " + old + " \n修改后： " + valuesMap);
	    	excuteLog.setOperateTable("数据录入修改");
	        ((IBusinessLogger) Global.getBean("BusinessLogger")).output(excuteLog);
		}
	}
	
	public void updateBatch(String ids, String field, String value) {
		String updateSQL = "update " + this.table + " set " + field + "=?, updatetime=?, updator=?, version=version+1 where id in (" + ids + ")";
		
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		int index = 0, fieldIndex = this.fieldCodes.indexOf(field);
		
		paramsMap.put(++index, DMUtil.preTreatValue( value, fieldTypes.get(fieldIndex) ));
		paramsMap.put(++index, new Timestamp(new Date().getTime()));
		paramsMap.put(++index, Environment.getUserCode());
		
		SQLExcutor.excute(updateSQL, paramsMap, this.datasource);
	}

	private Map<String, Object> get(Long id) {
		String fieldTags = "";
		for(String field : this.fieldCodes) {
			fieldTags += field + ",";
		}
		String sql = "select " + fieldTags + "creator from " + this.table + " where id=?";
		List<Map<String, Object>> list = SQLExcutor.query(this.datasource, sql, id);
		if( EasyUtils.isNullOrEmpty(list) ) {
			return null;
		}
		return list.get(0);
	}

	public void delete(Long id) {
		Map<String, Object> old = get(id);
		
		String updateSQL = "delete from " + this.table + " where id=" + id;
		SQLExcutor.excute(updateSQL, this.datasource);
		
		// 记录删除日志
		Log excuteLog = new Log(recordName + ", " + id, Environment.getUserCode() + "删除了记录：" + old );
    	excuteLog.setOperateTable("数据录入删除");
        ((IBusinessLogger) Global.getBean("BusinessLogger")).output(excuteLog);
	}
	
	public SQLExcutor select() {
		 return this.select(1, 100, new HashMap<String, String>());
	}

	public SQLExcutor select(int page, int pagesize, Map<String, String> params) {
		Map<Integer, Object> paramsMap = new HashMap<Integer, Object>();
		paramsMap.put(1, Environment.getUserCode());
		
		if(params == null) {
			params = new HashMap<String, String>();
		}
		
		// 增加权限控制，针对有編輯权限的允許查看他人录入数据, '000' <> ? <==> 忽略创建人这个查询条件
		boolean visible = DMConstants.isAdmin();
		try {
			List<String> permissions = PermissionHelper.getInstance().getOperationsByResource(recordId,
	                RecordPermission.class.getName(), RecordResource.class);
			visible = visible || permissions.contains(Record.OPERATION_VDATA) 
					|| permissions.contains(Record.OPERATION_EDATA);
		} catch(Exception e) {
		}
		
		// 设置查询条件
		String condition;
		if( visible && !params.containsKey("creator") ) {
			condition = " '000' <> ? ";
		} else {
			condition = " creator = ? ";
		}
		
		for(String key : params.keySet()) {
			String valueStr = params.get(key);
			if(EasyUtils.isNullOrEmpty(valueStr)) continue;
			
			if( "creator".equals(key) ) {
				paramsMap.put(1, valueStr);  // 替换登录账号，允许查询其它人创建的数据; 
				continue;
			}
			
			if("updator".equals(key)) {
				condition += " and updator = ? ";
				paramsMap.put(paramsMap.size() + 1, valueStr);
				continue;
			}
			
			int fieldIndex = this.fieldCodes.indexOf(key);
			if(fieldIndex >= 0) {
				String paramType = this.fieldTypes.get(fieldIndex);
				
				String[] vals = DMUtil.preTreatScopeValue(valueStr);				
				if(vals.length == 1) {
					condition += " and " + key + " = ? ";
					paramsMap.put(paramsMap.size() + 1, DMUtil.preTreatValue(vals[0], paramType));
				}
				else if(vals.length == 2) {
					String val1 = vals[0], val2 = vals[1];
					if(!EasyUtils.isNullOrEmpty(val1)) {
						condition += " and " + key + " >= ? ";
						paramsMap.put(paramsMap.size() + 1, DMUtil.preTreatValue(val1, paramType));
					}
					if(!EasyUtils.isNullOrEmpty(val2)) {
						condition += " and " + key + " <= ? ";
						paramsMap.put(paramsMap.size() + 1, DMUtil.preTreatValue(val2, paramType));
					}
				}
			}
		}
		
		if( !EasyUtils.isNullOrEmpty(this.customizeTJ) ) {
			condition += " and " + DMUtil.customizeParse(this.customizeTJ);
		}
		
		// 设置排序方式
		String sortField = params.get("sortField");
		String sortType  = params.get("sortType");
		
		String orderby = "order by ";
		if( !EasyUtils.isNullOrEmpty(sortField) && 
				(this.fieldCodes.contains(sortField) 
						|| "createtime,updatetime".indexOf(sortField) >= 0) ) {
			
			if("onlynull".equals(sortType)) {
				condition += " and " + sortField + " is null ";
				orderby += " id desc ";
			}
			else {
				orderby += sortField;
				if( EasyUtils.isNullOrEmpty(sortType) ) {
					sortType = "asc";
				}
				orderby += " " + sortType;
			}
		}
		else {
			orderby += " id desc ";
		}
		
		String selectSQL = "select " + EasyUtils.list2Str(this.fieldCodes) + 
					",createtime,creator,updatetime,updator,version,id from " + this.table + 
					" where " + condition + orderby;
		
		SQLExcutor ex = new SQLExcutor(false);
		ex.excuteQuery(selectSQL, paramsMap, page, pagesize, this.datasource);
		
		return ex;
	}

	public Document getGridTemplate() {
		StringBuffer sb = new StringBuffer();
        sb.append("<grid><declare sequence=\"true\" header=\"checkbox\">");
        
        int index = 0; 
        for(String filed : fieldNames) {
            sb.append("<column name=\"" + fieldCodes.get(index++) + "\" mode=\"string\" caption=\"" + filed + "\" />");
        }
        
        if(this.needFile) {
        	sb.append("<column name=\"fileNum\" mode=\"string\" caption=\"附件数\" />");
        }
        sb.append("<column name=\"createtime\" mode=\"string\" caption=\"创建时间\" sortable=\"true\"/>");
        sb.append("<column name=\"creator\" mode=\"string\" caption=\"创建人\" sortable=\"true\"/>");
        sb.append("<column name=\"updatetime\" mode=\"string\" caption=\"更新时间\" sortable=\"true\"/>");
        sb.append("<column name=\"updator\" mode=\"string\" caption=\"更新人\" sortable=\"true\"/>");
        sb.append("<column name=\"version\" mode=\"string\" caption=\"更新次数\" />");
        sb.append("<column name=\"id\" display=\"none\"/>");
        
        sb.append("</declare><data></data></grid>");
        
    	return XMLDocUtil.dataXml2Doc(sb.toString());
	}
	
	public List<Map<Object, Object>> getFields() {
		return this.fields;
	}
	
	private static Map<String, String> dsMappingType = new HashMap<String, String>();
	
	public static String getDBType(String datasource) {
		String result = dsMappingType.get(datasource);
		if(result != null) return result;
		
		Pool connpool = JCache.getInstance().getPool(datasource);
		if(connpool == null) {
			throw new BusinessException("数据源【" + datasource + "】不存在");
		}
        Cacheable connItem = connpool.checkOut(0);
        Connection conn = (Connection) connItem.getValue();
        
		try {
			String driveName = conn.getMetaData().getDriverName();
			log.debug(" database diverName: 【 " + driveName + "】。");
			
			for(String type : DB_TYPE) {
				if (driveName.startsWith(type)) {
					dsMappingType.put(datasource, result = type);
		            return result;
		        }
			}
		} catch (SQLException e) {
			
		} finally {
            connpool.checkIn(connItem); // 返回连接到连接池
        }
        
		throw new BusinessException("数据源【" + datasource + "】没有找到匹配的数据库类型");
	}
	
	public static String[] DB_TYPE = new String[] {"MySQL", "Oracle", "H2"};
	
	public static _Database getDB(Record record) {
		String type = getDBType(record.getDatasource());
		return getDB(type, record);
	}
	
	public static _Database getDB(String type, Record record) {
		if(DB_TYPE[0].equals(type)) {
			return new _MySQL(record);
		}
		else if(DB_TYPE[1].equals(type)) {
			return new _Oracle(record);
		}
		else {
			return new _H2(record);
		}
	}
}
