package com.boubei.tss.util;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

public class FileHelperTest {
	
	public static String CK_FILE_PATH = "log4j.properties";
    
	@Test
    public void testFileHelper() throws Exception {
         System.out.println(File.pathSeparatorChar);
         System.out.println(File.separatorChar);
         System.out.println(File.pathSeparator);
         System.out.println(File.separator);
         
         URL url = URLUtil.getResourceFileUrl(CK_FILE_PATH);
         String log4jPath = url.getPath(); 
         File classDir = new File(log4jPath).getParentFile();
         
         Assert.assertTrue(FileHelper.checkFile(classDir, CK_FILE_PATH));
         Assert.assertTrue(FileHelper.checkFile(classDir, "log4j.properties.haha") == false);
         
         File tempDir1 = FileHelper.createDir(classDir + "/temp1");
         File tempDir2 = FileHelper.createDir(classDir + "/temp2");
         File tempFile1 = new File(tempDir1.getPath() + "/1.txt");
         File tempFile2 = new File(tempDir2.getPath() + "/2.txt");
         
         Assert.assertFalse( FileHelper.isFolder(tempDir1, null) );
         Assert.assertFalse( FileHelper.isFolder(tempDir1, "1.txt") );
         
         FileHelper.writeFile(tempFile1, "111111111111");
         FileHelper.writeFile(tempFile2.getPath(), "222222222222", false);
         
         FileHelper.copyFile(tempDir2, tempFile1);
         
         FileHelper.copyFolder(tempDir2, tempDir1);
         
         File tempDir1_1 = FileHelper.createDir(classDir + "/temp1/dir1");
         File tempFile1_1 = new File(tempDir1_1.getPath() + "/11.txt");
         FileHelper.writeFile(tempFile1_1, "1111111222222211111");
         
         Assert.assertTrue(FileHelper.renameFile("temp1/dir1/11.txt", "1_1.txt"));
         Assert.assertFalse(FileHelper.renameFile("temp1/dir1/22.txt", "2_2.txt"));
         
         FileHelper.exportZip(tempDir2.getPath(), tempDir1);
         
         File subDir1 = FileHelper.findPathByName(tempDir1, "dir1");
         Assert.assertNotNull(subDir1);
         
         File subDir2 = FileHelper.findPathByName(tempDir1, "dir2");
         Assert.assertNull(subDir2);
         
         Assert.assertEquals("1", FileHelper.getFileNameNoSuffix("1.txt"));
         Assert.assertEquals("1", FileHelper.getFileNameNoSuffix("1"));
         Assert.assertNull(FileHelper.getFileNameNoSuffix(null));
         
         Assert.assertEquals("txt", FileHelper.getFileSuffix("1.txt"));
         Assert.assertEquals("", FileHelper.getFileSuffix("1"));
         Assert.assertNull(FileHelper.getFileSuffix(null));
         
         FileHelper.listFileNamesByTypeDeeply("txt", tempDir2);
         FileHelper.listFiles(tempDir2);
         FileHelper.listFiles(tempDir2.getPath());
         FileHelper.listFilesDeeply(tempDir2);
         FileHelper.listSubDir(tempDir2);
         
         FileHelper.readFile(tempFile1.getPath());
         FileHelper.readFile(tempFile1);
         FileHelper.readFile(tempFile1, "UTF-8");
         
         FileHelper.zip(tempDir2);
         List<String> fileList = FileHelper.listFilesByType("zip", classDir);
         Assert.assertEquals(1, fileList.size());
         
		 String zipPath = fileList.get(0);
         File zipFile = new File(classDir + "/" + zipPath);
		 FileHelper.upZip(zipFile);
         
         FileHelper.wirteOldFile(tempFile1.getPath(), tempDir2, "2.txt");
         
         FileHelper.deleteFile(tempDir1);
         FileHelper.deleteFile(tempDir2.getPath());
         FileHelper.deleteFile(zipFile);
         
         FileHelper.closeSteam(null, null);
         
         HttpServletResponse response = new MockHttpServletResponse();
         FileHelper.downloadFile(response, "/1.pdf", null);
         
		 FileHelper.downloadFile(response, "/1.jpg", "1.jpg");
		 FileHelper.downloadFile(response, "/1.png", "1.png");
		 FileHelper.downloadFile(response, "/1.pdf", "1.pdf");
    }
}
