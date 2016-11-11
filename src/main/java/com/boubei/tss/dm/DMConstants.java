package com.boubei.tss.dm;

import com.boubei.tss.modules.param.ParamConfig;
import com.boubei.tss.modules.param.ParamManager;

public final class DMConstants {
	
	public final static String DATASOURCE_LIST   = "datasource_list";
	public final static String DEFAULT_CONN_POOL = "default_conn_pool";
	public final static String LOCAL_CONN_POOL   = "connectionpool";

	public final static String TEMP_EXPORT_PATH  = "TEMP_EXPORT_PATH"; // 用于报表导出及数据录入附件上传
    
	public final static String SCRIPT_MACRO = "ReportScriptMacros";
    
	public final static String USER_ID = "userId";
	public final static String USER_CODE = "userCode";
	public final static String FROM_USER_ID = "fromUserId";
	
	public final static String ACLOG_IGNORE_REPORT = "ACLOG_IGNORE_REPORT";
	public final static String DATA_SERVICE_CONFIG = "DATA_SERVICE_CONFIG";
	
	// XForm 模板
	public static final String XFORM_GROUP = "template/dm/group_xform.xml";
	public static final String XFORM_REPORT = "template/dm/report_xform.xml";
	public static final String XFORM_RECORD = "template/dm/record_xform.xml";
    
    // Grid 模板
	public static final String GRID_RECORD_ATTACH = "template/dm/record_attach_grid.xml";
    
    /**
     * 报表模板资源文件目录
     */
	public static final String REPORT_TL_DIR_DEFAULT = "more/bi_template";
	public static final String REPORT_TL_DIR = "report.template.dir";
	public static final String REPORT_TL_TYPE = "reportTL";
	
	public static String getReportTLDir() {
		return ParamConfig.getAttribute(REPORT_TL_DIR, REPORT_TL_DIR_DEFAULT);
	}
	
	public static String getDS(String ds) {
		if( ds == null ) {
            try {
                return ParamManager.getValue(DMConstants.DEFAULT_CONN_POOL).trim(); // 默认数据源
            } catch (Exception e) {
            }
        }
		return ds;
	}
	
	public static String getDefaultDS() {
    	// ParamManager.getValue 有缓存，不宜用。（单元测试环节或自动切换数据源时容易出问题）
        // String datasource = ParamManager.getValue(DEFAULT_CONN_POOL).trim();
        String datasource = ParamManager.getValueNoSpring(DEFAULT_CONN_POOL).trim();
        return datasource;
    }
}
