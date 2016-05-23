package com.boubei.tss.um.ldap;

import java.io.File;

import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.schemaextractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schemaextractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.api.ldap.schemaloader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schemamanager.impl.DefaultSchemaManager;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boubei.tss.util.FileHelper;

public class ApacheDS {

	Logger log = LoggerFactory.getLogger(ApacheDS.class);

	/** The directory service */
	private DirectoryService service;

	/** The LDAP server */
	private LdapServer server;
	private static File workDir;
	
	public void destroy() throws Exception {
		if (server != null && server.isStarted()) {
			server.stop();
		}
		if (service != null) {
			service.shutdown();
		}
		
		FileHelper.deleteFile(workDir);
	}
 
	public LdapServer startServer() throws Exception {
		server = new LdapServer();
		int serverPort = 10389;
		server.setTransports(new TcpTransport(serverPort));

		String tmpDir = System.getProperty("java.io.tmpdir");
		if (System.getenv("OPENSHIFT_DATA_DIR") != null) {
			tmpDir = System.getenv("OPENSHIFT_DATA_DIR");
		}
		workDir = new File(tmpDir, "server-work");
		workDir.mkdirs();

		// Create the service
		initDirectoryService(workDir);
		server.setDirectoryService(service);

		server.start();
		return server;
	}

	private void initDirectoryService(File workDir) throws Exception {
		// Initialize the LDAP service
		service = new DefaultDirectoryService();
		service.setSchemaManager(new DefaultSchemaManager());
		service.setInstanceLayout(new InstanceLayout(workDir));
		
		initSchemaPartition(); // first load the schema
		
        Partition systemPartition = addPartition( "system", ServerDNConstants.SYSTEM_DN, true );
        service.setSystemPartition( systemPartition );

		// And start the service
		service.startup();
	}
	
	private void initSchemaPartition() throws Exception {
		File schemaDir = new File(workDir, "schema");
		log.info("Schema directory: {}", schemaDir);
		
		// Extract the schema on disk (a brand new one) and load the registries
		if (!schemaDir.exists()) {
			SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(workDir);
			extractor.extractOrCopy(true);
		}
		
		SchemaManager schemaManager = service.getSchemaManager();
		schemaManager.setSchemaLoader(new LdifSchemaLoader(schemaDir));
		// We have to load the schema now, otherwise we won't be able to initialize 
		// the Partitions, as we won't be able to parse and normalize their suffix DN
		schemaManager.loadAllEnabled();
		
		LdifPartition ldifPartition = new LdifPartition(schemaManager);
		ldifPartition.setPartitionPath(schemaDir.toURI());
		
		SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
		schemaPartition.setWrappedPartition(ldifPartition);
		service.setSchemaPartition(schemaPartition);
	}
	
	public Partition addPartition(String partitionId, String partitionDn, boolean isSystem) throws Exception {
		JdbmPartition partition = new JdbmPartition(service.getSchemaManager());
		partition.setId(partitionId);
		partition.setPartitionPath(new File(workDir, partitionId).toURI());
		partition.setSuffixDn(new Dn(partitionDn));
		
		if(!isSystem) {
			service.addPartition(partition);
		}

		return partition;
	}
}
