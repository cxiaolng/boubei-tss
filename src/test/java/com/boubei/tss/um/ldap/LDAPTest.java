package com.boubei.tss.um.ldap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.api.CoreSession;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP"),
		@CreateTransport(protocol = "LDAPS") })
public class LDAPTest extends AbstractLdapTestUnit {

	private LdapConnection connection;
	private CoreSession session;

	@Before
	public void setup() throws Exception {
	    connection = LdapUtils.createAdminConnection( getLdapServer() );

		session = getLdapServer().getDirectoryService().getAdminSession();
	}

	@Test
	public void testLDAP() throws Exception {
		
		assertFalse(session.exists("cn=JonKing,ou=system"));

		connection.add(new DefaultEntry(
				"cn=JonKing,ou=system",
				"ObjectClass : top", 
				"ObjectClass : person", 
				"cn: JonKing_sn",
				"sn: JonKing_sn", 
				"userPassword: 123456"));

		assertTrue(session.exists("cn=JonKing,ou=system"));

		Entry entry = connection.lookup("cn=JonKing,ou=system");
		assertNotNull(entry);

		// 初始化参数设置
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, "ldap://localhost:" + getLdapServer().getPort());
		env.put(Context.SECURITY_PRINCIPAL, "cn=JonKing,ou=system");
		env.put(Context.SECURITY_CREDENTIALS, "123456");

		// 连接到数据源
		DirContext ctx = null;
		try {
			ctx = new InitialDirContext(env);
			System.out.println("密码在LDAP中验证通过。");
		} catch (Exception e) {
			System.out.println("密码在LDAP中验证不通过。");
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
				}
			}
		}
	}
}
