package com.boubei.tss.framework.license;

import java.util.Date;
import java.util.Properties;

import com.boubei.tss.util.ConfigurableContants;
import com.boubei.tss.util.DateUtil;
import com.boubei.tss.util.EasyUtils;

/** 
 * <p> 授权许可 </p> 
 */
public final class License extends ConfigurableContants {
	
    /** 授权许可类型 */
    enum LicenseType {
    	NON_Commercial, /** 非商业的 */
    	Commercial,     /** 商业的   */
    	Evaluation,     /** 评测的  */
    	Advanced        /** 高级的 */
    }
    
    String product;          // 产品
    String version;          // 版本号
    String macAddress;       // 安装服务器的Mac地址
    Date   expiresDate;      // 过期时间
    String licenseSignature; // license签名
    
    String licenseType; // license类型
    
    static Properties licenseProps;
    
    /**
     * 把license的属性值拼成一个字符串，然后转身字节数组
     */
    public byte[] getFingerprint() {
        StringBuffer buf = new StringBuffer(100);
        buf.append(product).append(version).append(licenseType);
        buf.append(DateUtil.format(expiresDate));
        if(macAddress != null) {
            buf.append(macAddress);
        }
        return buf.toString().getBytes();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        buf.append("product=" + product).append("\n");
        buf.append("version=" + version).append("\n");
        buf.append("type=" + licenseType).append("\n");
        buf.append("expiry=" + DateUtil.format(expiresDate)).append("\n");
 
        if( !EasyUtils.isNullOrEmpty(macAddress) ) {
            buf.append("macaddress=" + macAddress).append("\n");
        }
        buf.append("signature=" + licenseSignature);
        return buf.toString();
    }

    /**
     * 读取许可文件
     * @param fileName
     */
    public static License fromConfigFile(String fileName) throws Exception {
    	licenseProps = init(fileName);
        
        License license = new License();
        license.product = licenseProps.getProperty("product");
        license.version = licenseProps.getProperty("version");
        license.licenseType = licenseProps.getProperty("type");
        
        String expiry = licenseProps.getProperty("expiry");
        license.expiresDate = DateUtil.parse(expiry);
        
        String macAddress = licenseProps.getProperty("macaddress");
        if( !EasyUtils.isNullOrEmpty(macAddress) ) {
            license.macAddress = macAddress;
        }
        
        license.licenseSignature = licenseProps.getProperty("signature");
        
        return license;
    }
}