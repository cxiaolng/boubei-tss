package com.boubei.tss.framework.license;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.boubei.tss.framework.SystemInfo;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.FileHelper;

public class LicenseTest {
	
	private String licenceName = "cpu.license";
	
	private final Log log = LogFactory.getLog(getClass());
    
	@Test
    public void testLicense() {
        
        // 第一步：生成公钥、私钥对。公钥公开，注意保管好私钥（如果泄露，则有可能被hacker随意创建license）。
        try {
            LicenseFactory.generateKey();
        } catch (Exception e) {
        	log.error(e);
            assertTrue(e.getMessage(), false);
        }
        
        // 第二步：根据产品、版本、Mac地址、有效期等信息，签名产生注册号，并将该注册号复制到license文件中。
        License license = null;
        try {
            license = License.fromConfigFile(licenceName);
            LicenseFactory.sign(license);
        } catch (Exception e) {
        	log.error(e);
            assertTrue(e.getMessage(), false);
        }
        
        FileHelper.writeFile(new File(LicenseFactory.LICENSE_DIR + "/" + licenceName), license.toString());
        log.debug(license);
        
        // 第三步：利用公钥对license进行合法性验证。可以在软件代码的重要模块中加入下面的验证，比如登录模块等。
        LicenseManager manager = LicenseManager.getInstance();
        assertEquals("tss", license.product);
        assertEquals("4.1", license.version);
        assertEquals("Commercial", license.licenseType);
        assertEquals(DateUtil.parse("2022-06-22"), license.expiresDate);
        assertEquals("6C-62-6D-C6-49-9E", license.macAddress);
        
        boolean result = manager.validateLicense(license.product, license.version);
		assertEquals(true, result);
        
        String licenseType = manager.getLicenseType(license.product, license.version);
		assertEquals("Commercial", licenseType);
        
        Assert.assertTrue(SystemInfo.validateTSS());
    }
}

