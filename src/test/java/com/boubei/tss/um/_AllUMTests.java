package com.boubei.tss.um;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.boubei.tss.um.helper.PasswordRuleTest;
import com.boubei.tss.um.module.GroupModuleTest;
import com.boubei.tss.um.module.RoleModuleTest;
import com.boubei.tss.um.module.SubAuthorizeModuleTest;
import com.boubei.tss.um.module.UserModuleTest;
import com.boubei.tss.um.search.GeneralSearchTest;
import com.boubei.tss.um.servlet.GetLoginInfoTest;
import com.boubei.tss.um.servlet.GetPasswordTest;
import com.boubei.tss.um.servlet.GetPasswordStrengthTest;
import com.boubei.tss.um.servlet.GetQuestionTest;
import com.boubei.tss.um.servlet.ImportAppConfigTest;
import com.boubei.tss.um.servlet.RegisterTest;
import com.boubei.tss.um.servlet.ResetPasswordTest;
import com.boubei.tss.um.sso.FetchPermissionAfterLoginTest;
import com.boubei.tss.um.sso.UMIdentityGetterTest;
import com.boubei.tss.um.sso.UMPasswordIdentifierTest;
import com.boubei.tss.um.sso.online.DBOnlineUserManagerTest;
import com.boubei.tss.um.sso.othersystem.LtpaTokenIdentifierTest;
import com.boubei.tss.um.syncdata.SyncDataFromDBTest;
import com.boubei.tss.um.syncdata.SyncDataFromLDAPTest;
import com.boubei.tss.um.zlast.ResourceModuleTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	GroupModuleTest.class,
	UserModuleTest.class,
	RoleModuleTest.class,
	SubAuthorizeModuleTest.class,
	ResourceModuleTest.class,
	FetchPermissionAfterLoginTest.class,
	UMPasswordIdentifierTest.class,
	UMIdentityGetterTest.class,
	GetLoginInfoTest.class,
	GetPasswordTest.class,
	GetQuestionTest.class,
	GetPasswordStrengthTest.class,
	RegisterTest.class,
	ResetPasswordTest.class,
	PasswordRuleTest.class,
	GeneralSearchTest.class,
	SyncDataFromDBTest.class,
	SyncDataFromLDAPTest.class,
	LtpaTokenIdentifierTest.class,
	DBOnlineUserManagerTest.class,
	ImportAppConfigTest.class
})
public class _AllUMTests {
 
}
