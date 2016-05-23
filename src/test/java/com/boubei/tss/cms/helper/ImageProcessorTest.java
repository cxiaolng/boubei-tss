package com.boubei.tss.cms.helper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.boubei.tss.cms.AbstractCMSTest;
import com.boubei.tss.util.FileHelper;
import com.boubei.tss.util.URLUtil;

public class ImageProcessorTest {
	
	File tempDir1;
	
	@Before
	public void setUp() {
		URL url = URLUtil.getResourceFileUrl(AbstractCMSTest.CK_FILE_PATH);
        String log4jPath = url.getPath(); 
        File classDir = new File(log4jPath).getParentFile();
        Assert.assertTrue(FileHelper.checkFile(classDir, AbstractCMSTest.CK_FILE_PATH));
        
        tempDir1 = FileHelper.createDir(classDir + "/temp1");
	}
    
	@Test
	public void testResize() {
		try {
			// 先用java创建一张图片
			int width = 60;   
	        int height = 30;   
	        String s = "8341";   
	        
	        String filePath = tempDir1 + "/1.jpg";
	        File file = new File(filePath);   
	           
	        Font font = new Font("Serif", Font.ITALIC, 25);   
	        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);   
	        Graphics2D g2 = (Graphics2D)bi.getGraphics();   
	        g2.setBackground(Color.WHITE);   
	        g2.clearRect(0, 0, width, height);   
	        g2.setPaint(Color.RED);   
	        g2.setFont(font);
	           
	        FontRenderContext context = g2.getFontRenderContext();   
	        Rectangle2D bounds = font.getStringBounds(s, context);   
	        double x = (width - bounds.getWidth()) / 2.5;   
	        double y = (height - bounds.getHeight()) / 1.2;   
	        double baseY = y - bounds.getY();   
	           
	        g2.drawString(s, (int)x, (int)baseY);   
	        ImageIO.write(bi, "jpg", file);   
	        
	        // 测试缩略图
			ImageProcessor imageProcessor = new ImageProcessor(filePath);
			imageProcessor.resize(0.98);
			
			imageProcessor.resizeFix(20, 20);
		} 
		catch (IOException e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
}
