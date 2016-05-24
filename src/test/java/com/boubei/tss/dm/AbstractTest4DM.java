package com.boubei.tss.dm;

import com.boubei.tss.AbstractTest4TSS;
import com.boubei.tss.framework.component.param.Param;
import com.boubei.tss.framework.component.param.ParamConstants;
import com.boubei.tss.framework.component.param.ParamManager;

public abstract class AbstractTest4DM extends AbstractTest4TSS { 
    
    protected String getDefaultSource(){
    	return "connectionpool";
    }

    protected void init() {
    	super.init();
        
        if(paramService.getParam(DMConstants.DATASOURCE_LIST) == null) {
        	Param dlParam = ParamManager.addComboParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.DATASOURCE_LIST, "数据源列表");
            ParamManager.addParamItem(dlParam.getId(), "connectionpool-1", "数据源1", ParamConstants.COMBO_PARAM_MODE);
        }
        if(paramService.getParam(DMConstants.DEFAULT_CONN_POOL) == null) {
        	ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.DEFAULT_CONN_POOL, "默认数据源", getDefaultSource());
        }
        if(paramService.getParam(DMConstants.TEMP_EXPORT_PATH) == null) {
			String tmpDir = System.getProperty("java.io.tmpdir") + "temp";
			log.info("临时文件导出目录：" + tmpDir);
			ParamManager.addSimpleParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.TEMP_EXPORT_PATH, "临时文件导出目录", tmpDir);
        }
        if(paramService.getParam(DMConstants.SCRIPT_MACRO) == null) {
        	Param paramL = ParamManager.addComboParam(ParamConstants.DEFAULT_PARENT_ID, DMConstants.SCRIPT_MACRO, "常用脚本段");
        	ParamManager.addParamItem(paramL.getId(), "1=1", "testMacro", ParamConstants.COMBO_PARAM_MODE);
        }
    }
    
}
