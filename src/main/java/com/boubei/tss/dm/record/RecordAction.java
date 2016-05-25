package com.boubei.tss.dm.record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.boubei.tss.cache.extension.CacheHelper;
import com.boubei.tss.dm.DMConstants;
import com.boubei.tss.dm.record.permission.RecordPermission;
import com.boubei.tss.dm.record.permission.RecordResource;
import com.boubei.tss.framework.web.dispaly.tree.LevelTreeParser;
import com.boubei.tss.framework.web.dispaly.tree.TreeEncoder;
import com.boubei.tss.framework.web.dispaly.xform.XFormEncoder;
import com.boubei.tss.framework.web.mvc.BaseActionSupport;
import com.boubei.tss.modules.param.Param;
import com.boubei.tss.modules.param.ParamManager;
import com.boubei.tss.um.permission.PermissionHelper;
import com.boubei.tss.um.service.ILoginService;
import com.boubei.tss.util.EasyUtils;

@Controller
@RequestMapping("/auth/rc")
public class RecordAction extends BaseActionSupport {
    
    @Autowired private RecordService recordService;
    @Autowired private ILoginService loginService;
    
    @RequestMapping("/all")
    public void getAllRecord(HttpServletResponse response) {
    	List<Record> result = new ArrayList<Record>();
    	List<Record> all = recordService.getAllRecords();
        List<Record> list1 = recordService.getRecordables(); 
        List<Record> list2 = recordService.getVisiables(); // include record groups
        for(Record record : all) {
        	if( list1.contains(record) || list2.contains(record) ){
        		result.add(record);
        	}
        }
        
        TreeEncoder treeEncoder = new TreeEncoder(result, new LevelTreeParser());
        print("SourceTree", treeEncoder);
    }
    
    @RequestMapping("/groups")
    public void getAllRecordGroups(HttpServletResponse response) {
        List<?> list = recordService.getAllRecordGroups();
        TreeEncoder treeEncoder = new TreeEncoder(list, new LevelTreeParser());
        treeEncoder.setNeedRootNode(true);
        print("SourceTree", treeEncoder);
    }
    
    @RequestMapping(value = "/detail/{type}")
    public void getRecord(HttpServletRequest request, HttpServletResponse response, @PathVariable("type") int type) {
        String uri = null;
        if(Record.TYPE0 == type) {
            uri = DMConstants.XFORM_GROUP;
        } else {
            uri = DMConstants.XFORM_RECORD;
        }
        
        XFormEncoder xformEncoder;
        String recordIdValue = request.getParameter("recordId");
        
        if( recordIdValue == null) {
            Map<String, Object> map = new HashMap<String, Object>();
            
            String parentIdValue = request.getParameter("parentId"); 
            if("_root".equals(parentIdValue)) {
            	parentIdValue = null;
            }
            
            Long parentId = parentIdValue == null ? Record.DEFAULT_PARENT_ID : EasyUtils.obj2Long(parentIdValue);
            map.put("parentId", parentId);
            map.put("type", type);
            xformEncoder = new XFormEncoder(uri, map);
        } 
        else {
            Long recordId = EasyUtils.obj2Long(recordIdValue);
            Record record = recordService.getRecord(recordId);
            xformEncoder = new XFormEncoder(uri, record);
        }
        
        if( Record.TYPE1 == type ) {
            try {
            	List<Param> datasources = ParamManager.getComboParam(DMConstants.DATASOURCE_LIST);
                xformEncoder.fixCombo("datasource", datasources);	
            } catch (Exception e) {
            }
        }
 
        print("SourceInfo", xformEncoder);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void saveRecord(HttpServletResponse response, Record record) {
        boolean isnew = (null == record.getId());
        recordService.saveRecord(record);
        
		String cacheKey = "_db_record_" + record.getId();
		CacheHelper.getLongCache().removeObject(cacheKey);
		
        doAfterSave(isnew, record, "SourceTree");
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(HttpServletResponse response, @PathVariable("id") Long id) {
        recordService.delete(id);
        printSuccessMessage();
    }
    
    @RequestMapping(value = "/disable/{id}/{disabled}", method = RequestMethod.POST)
    public void startOrStop(HttpServletResponse response, 
            @PathVariable("id") Long id, @PathVariable("disabled") int disabled) {
        
    	recordService.startOrStop(id, disabled);
        printSuccessMessage();
    }
 
    @RequestMapping(value = "/sort/{startId}/{targetId}/{direction}", method = RequestMethod.POST)
    public void sort(HttpServletResponse response, 
            @PathVariable("startId") Long startId, 
            @PathVariable("targetId") Long targetId, 
            @PathVariable("direction") int direction) {
        
        recordService.sort(startId, targetId, direction);
        printSuccessMessage();
    }
 
    @RequestMapping(value = "/move/{recordId}/{groupId}", method = RequestMethod.POST)
    public void move(HttpServletResponse response, 
            @PathVariable("recordId") Long recordId, @PathVariable("groupId") Long groupId) {
        
        recordService.move(recordId, groupId);
        printSuccessMessage();
    }
    
	@RequestMapping("/operations/{resourceId}")
    public void getOperations(HttpServletResponse response, @PathVariable("resourceId") Long resourceId) {
        List<String> list = PermissionHelper.getInstance().getOperationsByResource(resourceId,
                        RecordPermission.class.getName(), RecordResource.class);

        print("Operation", EasyUtils.list2Str(list));
    }
}
