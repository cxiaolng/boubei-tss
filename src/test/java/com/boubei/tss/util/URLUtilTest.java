package com.boubei.tss.util;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

public class URLUtilTest {

	@Test
    public void testGetResourceFileUrl() {
		URLUtil.getResourceFileUrl(null);
		URLUtil.getResourceFileUrl("");
		
        URL url = URLUtil.getResourceFileUrl("log4j.properties");
        System.out.println(url);
        System.out.println(url.getPath()); 
        
        String path = "/D:/Temp/apps/pms/WEB-INF/classes/cn/";
        int lastIndex = path.lastIndexOf("WEB-INF");
        try {
            path = path.substring(0, lastIndex) + "core";
            url = new URL(url, path);
        } catch (MalformedURLException e) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + path);
            e.printStackTrace();
        }
        System.out.println(url);
        System.out.println(url.getPath());
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(URLUtil.getClassesPath().getPath());
    }
 
    @Test
    public void testGetWebFileUrl() {
        try {
            URL url = URLUtil.getWebFileUrl("log4j.properties");
            System.out.println(url);
            System.out.println(url.getPath()); 
        } catch (Exception e) {
            Assert.assertFalse("WEB-INF path not exists", false);
        }

    }
    
    @Test
    public void testGetClassesPath() {
        URL url = URLUtil.getClassesPath();
        System.out.println(url);
        System.out.println(url.getPath()); 
    }
}
