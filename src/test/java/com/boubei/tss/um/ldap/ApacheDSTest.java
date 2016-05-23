package com.boubei.tss.um.ldap;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApacheDSTest {

	Logger log = LoggerFactory.getLogger(ApacheDSTest.class);
	
	ApacheDS apacheDS;
	LdapServer server;
	DirectoryService service;
 
	@Before
	public void setUp() throws Exception {
		apacheDS = new ApacheDS();
		server = apacheDS.startServer(); 
		service = server.getDirectoryService();
	}

	@After
	public void teardown() throws Exception {
		apacheDS.destroy();
	}

	@Test
	public void test() {
		try {
			prepareData();

			// LdapConnection conn = new LdapCoreSessionConnection(service);
			LdapConnection conn = LdapUtils.createAdminConnection(server);

			
			Dn dn = new Dn("o=BouBei");
			Entry result = conn.lookup(dn);
			log.info("Found entry : " + result);

			EntryCursor cursor = conn.search(dn, "(objectclass=organizationalUnit)", SearchScope.SUBTREE);
			while (cursor.next()) {
				log.info(cursor.get().toString());
			}
			
			cursor = conn.search(dn, "(objectclass=person)", SearchScope.SUBTREE);
			while (cursor.next()) {
				log.info(cursor.get().toString());
			}
			
			cursor.getSearchResultDone();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	private void prepareData() throws Exception {
    	
    	SchemaManager schemaManager = service.getSchemaManager();
    	Partition store = apacheDS.addPartition("TSS", "o=BouBei", false);
    	
        Dn suffixDn = new Dn( schemaManager, "o=BouBei" );
        long index = 1L;

        // 公司
        Entry entry = new DefaultEntry( schemaManager, suffixDn,
            "objectClass: organization",
            "o: BouBei",
            "postalCode: 1",
            "postOfficeBox: 1" );
        LdapUtils.injectEntryInStore( store, entry, index++ );
        
        // 销售部
        Dn dn = new Dn( schemaManager, "ou=Sales,o=BouBei" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: organizationalUnit",
            "ou: Sales",
            "postalCode: 1",
            "postOfficeBox: 1" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // 研发部
        dn = new Dn( schemaManager, "ou=RD,o=BouBei" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: organizationalUnit",
            "ou: RD",
            "postalCode: 2",
            "postOfficeBox: 2" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // 销售部员工
        dn = new Dn( schemaManager, "cn=JIM BEAN,ou=Sales,o=BouBei" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "ou: Sales",
            "cn: JIM BEAN",
            "SN: BEAN",
            "postalCode: 3",
            "postOfficeBox: 3" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // RD下的IT部
        dn = new Dn( schemaManager, "ou=IT,ou=RD,o=BouBei" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: organizationalUnit",
            "ou: Apache",
            "postalCode: 5",
            "postOfficeBox: 5" );
        LdapUtils.injectEntryInStore( store, entry, index++ );

        // RD的员工
        dn = new Dn( schemaManager, "cn=Jack Daniels,ou=RD,o=BouBei" );
        entry = new DefaultEntry( schemaManager, dn,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "ou: RD",
            "cn: Jack Daniels",
            "SN: Daniels",
            "postalCode: 6",
            "postOfficeBox: 6" );
        LdapUtils.injectEntryInStore( store, entry, index++ );
        
        Dn adminDn = new Dn( schemaManager, "cn=admin,ou=RD,o=BouBei" );
        entry = new DefaultEntry( schemaManager, adminDn,
            "objectClass: top",
            "objectClass: person",
            "objectClass: organizationalPerson",
            "ou: RD",
            "cn: admin",
            "userPassword: 123456");
        LdapUtils.injectEntryInStore( store, entry, index++ );
    }
}
