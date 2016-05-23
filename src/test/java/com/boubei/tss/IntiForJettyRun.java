package com.boubei.tss;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.boubei.tss.framework.test.TestUtil;
import com.boubei.tss.util.FileHelper;
 
public class IntiForJettyRun { 
 
    protected Logger log = Logger.getLogger(this.getClass());    
    
    @Test
    public void intiForJettyRun() {
        // 初始化Portal测试的组件模型存放目录（model目录）及freemarker文件的目录
        String portalTargetPath = TestUtil.getProjectDir() + "/webapp/target";
        
        FileHelper.createDir(portalTargetPath + "/portal/model/portal").getPath();
        FileHelper.createDir(portalTargetPath + "/portal/model/portlet").getPath();
        FileHelper.createDir(portalTargetPath + "/portal/model/layout").getPath();
        FileHelper.createDir(portalTargetPath + "/portal/model/decorator").getPath();
        
        File freemarkerDir = FileHelper.createDir(portalTargetPath + "/freemarker");
        FileHelper.writeFile(new File(freemarkerDir + "/common.ftl"), "");
    }
}
