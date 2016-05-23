package com.boubei.tss.cms.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.boubei.tss.util.FileHelper;

/**
 * 对文章附件内容进行索引。
 * 
 * 用于读取文章的附件内容进行全文检索。
 * 附件可能为压缩文件、txt、pdf、word、excel、ppt等多种类型。
 */
public class AttachmentIndex {
    
    private static Logger log = Logger.getLogger(AttachmentIndex.class);

    private AttachmentIndex(){
    }

    private static AttachmentIndex manager;
    
    public static AttachmentIndex getInstance(){
        if(manager == null) {
            manager = new AttachmentIndex();
        }
        return manager;
    }
    
	/**
	 * 附件处理得到到附件文本信息(zip文件，解压后取文件列表文本信息)
	 * @param file
	 * @return
	 * @throws Exception 
	 */
	public String disposeAttachment(File file) {
        if ( !file.exists() || !file.isFile() ) {
            return "";
        }
 
		String suffix = FileHelper.getFileSuffix(file.getName());
		if ( "doc".equals(suffix) || "docx".equals(suffix) ) { // word文档
			// TODO
		} 
		else if ( "ppt".equals(suffix) || "pptx".equals(suffix) ) { // ppt文档
			// TODO
		} 
		else if ( "xls".equals(suffix) || "xlsx".equals(suffix) ) { // excle文档
			// TODO
		} 
		else if ( "pdf".equals(suffix) ) { // pdf文档
			// TODO
		}
		else if ( "txt".equals(suffix) ) { 
			return getContentFromText(file); // 普通文档，txt等
		}
        
		return "";
	}
	
    private String getContentFromText(File textFile) {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        try {
            br = new BufferedReader(new FileReader(textFile));
            String temp = "";
            while (null != temp) {
                sb.append(br.readLine()).append("\n");
                temp = br.readLine();
            }
        } catch (Exception e) {
            log.error("发布索引时提取文档:" + textFile.getPath() + " 内容失败！", e);
            return "";
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                log.error("发布索引时关闭文件:" + textFile.getPath() + " 流失败！", e);
            }
        }
        return sb.toString();
    }
}