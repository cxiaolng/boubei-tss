package com.boubei.tss.um.ldap;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.csn.CsnFactory;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.util.Strings;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.api.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.ldap.LdapServer;

public class LdapUtils {
	
	public static final String DEFAULT_HOST = "localhost";

	public static final String DEFAULT_ADMIN = ServerDNConstants.ADMIN_SYSTEM_DN; // "uid=admin,ou=system";

	public static final String DEFAULT_PASSWORD = "secret";

	public static LdapNetworkConnection createAdminConnection(LdapServer ldapServer) throws Exception {
		int port = ldapServer.getPort();
		System.out.println(" =========== " + port);
		
		LdapNetworkConnection conn = new LdapNetworkConnection(DEFAULT_HOST, port);
		conn.bind(DEFAULT_ADMIN, DEFAULT_PASSWORD);
		return conn;
	}
	
    private static final CsnFactory CSN_FACTORY = new CsnFactory( 0 );
    
    public static void injectEntryInStore( Partition store, Entry entry, long index ) throws Exception {
        entry.add( SchemaConstants.ENTRY_CSN_AT, CSN_FACTORY.newInstance().toString() );
        entry.add( SchemaConstants.ENTRY_UUID_AT, Strings.getUUID( index ).toString() );

        AddOperationContext addContext = new AddOperationContext( null, entry );
        store.add( addContext );
    }

}
