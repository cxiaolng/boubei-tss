package com.boubei.tss.um.syncdata;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boubei.tss.framework.component.progress.Progress;
import com.boubei.tss.framework.component.progress.Progressable;
import com.boubei.tss.um.AbstractTest4UM;
import com.boubei.tss.um.UMConstants;
import com.boubei.tss.um.entity.Application;
import com.boubei.tss.um.entity.Group;
import com.boubei.tss.um.ldap.ApacheDS;
import com.boubei.tss.um.ldap.LdapUtils;
import com.boubei.tss.um.service.IResourceService;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

/**
 * 测试用户同步
 */
public class SyncDataFromLDAPTest extends AbstractTest4UM {

	@Autowired ISyncService syncService;
	@Autowired IResourceService resourceService;

	ApacheDS apacheDS;
	LdapServer server;
	DirectoryService service;
 
	public void init() {
		super.init();
		
		apacheDS = new ApacheDS();
		try {
			server = apacheDS.startServer();
		} catch (Exception e) {
			log.error("start apache LDAP error:", e);
		} 
		service = server.getDirectoryService();
	}

	@After
	public void teardown() throws Exception {
		apacheDS.destroy();
		super.tearDown();
	}
 
	@Test
	public void testSyncLDAP() {
		try {
			prepareData();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		Group mainGroup = new Group();
		mainGroup.setParentId(UMConstants.MAIN_GROUP_ID);
		mainGroup.setName("金禾");
		mainGroup.setGroupType(Group.MAIN_GROUP_TYPE);
		mainGroup.setFromApp(UMConstants.TSS_APPLICATION_ID);
		mainGroup.setFromGroupId("o=jinhe");
		
		groupService.createNewGroup(mainGroup, "", "-1");
		Long mainGroupId = mainGroup.getId();
		log.debug(mainGroup + "\n");

		Application application = resourceService.getApplication(UMConstants.TSS_APPLICATION_ID);
        Assert.assertNotNull(application);
        application.setDataSourceType(UMConstants.DATA_SOURCE_TYPE_LDAP);
        
        URL template = URLUtil.getResourceFileUrl("template/um/syncdata/template_LDAP.xml");
        String paramDesc = FileHelper.readFile(new File(template.getPath()));
        application.setParamDesc(paramDesc);
        
        resourceService.saveApplication(application);
        
        String applicationId = application.getApplicationId();
		String fromGroupId = mainGroup.getFromGroupId();
		Map<String, Object> datasMap = syncService.getCompleteSyncGroupData(mainGroupId, applicationId , fromGroupId);
		List<?> groups = (List<?>)datasMap.get("groups");
        List<?> users  = (List<?>)datasMap.get("users");
        int totalCount = users.size() + groups.size();
        Assert.assertTrue(totalCount == 6);
        
        for(Object temp : groups) {
        	log.debug(temp);
        }
        for(Object temp : users) {
        	log.debug(temp);
        }
	    
        log.debug("totalCount = " + totalCount);
		Progress progress = new Progress(totalCount);
		((Progressable)syncService).execute(datasMap, progress );
	}
	
	private void prepareData() throws Exception {
    	SchemaManager schemaManager = service.getSchemaManager();
    	Partition store = apacheDS.addPartition("TSS", "o=jinhe", false);
    	
        Dn suffixDn = new Dn( schemaManager, "o=jinhe" );
        long index = 1L;

        // 公司
        Entry entry = new DefaultEntry( schemaManager, suffixDn,
            "objectClass: organization",
            "o: jinhe",
            "postalCode: 1",
            "postOfficeBox: 1" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // 销售部
        Dn dn = new Dn( schemaManager, "ou=Sales,o=jinhe" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: organizationalUnit",
            "ou: Sales",
            "postalCode: 1",
            "postOfficeBox: 1" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // 研发部
        dn = new Dn( schemaManager, "ou=RD2,o=jinhe" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: organizationalUnit",
            "ou: RD2",
            "postalCode: 2",
            "postOfficeBox: 2" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // RD2下的IT部
        dn = new Dn( schemaManager, "ou=IT,ou=RD2,o=jinhe" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: organizationalUnit",
            "ou: IT",
            "postalCode: 3",
            "description: test and test!" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // 销售部员工
        dn = new Dn( schemaManager, "cn=JIM BEAN,ou=Sales,o=jinhe" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "ou: Sales",
            "cn: JIM BEAN",
            "SN: JIM");
        LdapUtils.injectEntryInStore( store, entry, index++ );
        
        // RD2的员工
        dn = new Dn( schemaManager, "cn=Jack Daniels,ou=RD2,o=jinhe" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "ou: RD2",
            "cn: Jack Daniels",
            "SN: Jack",
            "uid: Jack");
        LdapUtils.injectEntryInStore( store, entry, index++ );
        
        Dn adminDn = new Dn( schemaManager, "cn=admin2,ou=RD2,o=jinhe" );
        entry = new DefaultEntry( schemaManager, adminDn,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "ou: RD2",
            "cn: admin2",
            "SN: admin2",
            "userPassword: 123456");
        LdapUtils.injectEntryInStore( store, entry, index++ );
    }
}
