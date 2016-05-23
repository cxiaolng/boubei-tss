package com.boubei.tss.dm.record.file;

import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.boubei.tss.dm.record.RecordService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.sso.Environment;
import com.boubei.tss.framework.web.servlet.AfterUpload;
import com.boubei.tss.util.FileHelper;

public class CreateAttach implements AfterUpload {

	Logger log = Logger.getLogger(this.getClass());

	public String processUploadFile(HttpServletRequest request,
			String filepath, String oldfileName) throws Exception {

		Long recordId  = Long.parseLong(request.getParameter("recordId"));
		Long itemId = Long.parseLong(request.getParameter("itemId"));
		int type = Integer.parseInt(request.getParameter("type"));
		
		int separatorIndex = oldfileName.lastIndexOf("\\");
		if(separatorIndex > 0) {
			oldfileName = oldfileName.substring(separatorIndex + 1);
		}
		separatorIndex = oldfileName.lastIndexOf("/");
		if(separatorIndex > 0) {
			oldfileName = oldfileName.substring(separatorIndex + 1);
		}

		// 保存附件信息
		File targetFile = new File(filepath);
		RecordAttach attach = saveAttach(targetFile, recordId, itemId, type, oldfileName);

		// 向前台返回成功信息。
		// 因为上传附件都是通过一个隐藏的iframe来实现上传的（可防止刷新主页面），所以上传成功回调JS需要加上 parent.
		return "parent.addAttach(" + attach.getId() + ", " + attach.getType() + ", '" 
				+ attach.getName() + "', '" + attach.getDownloadUrl() + "', '" + attach.getUploadUser() + "')";
	}
	
	private RecordAttach saveAttach(File file, Long recordId, Long itemId, int type, String oldfileName) {
		RecordService recordService = (RecordService) Global.getBean("RecordService");
		
		if( FileHelper.isImage(oldfileName) ) {
			type = RecordAttach.ATTACH_TYPE_PIC;
		}
		
		// 保存附件信息对象
		RecordAttach attach = new RecordAttach();
		attach.setType(type);
		attach.setName(oldfileName);
		attach.setRecordId(recordId);
		attach.setItemId(itemId);
		attach.setSeqNo(recordService.getAttachSeqNo(recordId, itemId));
		attach.setUploadDate(new Date());
		attach.setUploadUser(Environment.getUserName());
		
        String attachDir = RecordAttach.getAttachDir(recordId, itemId);
        File rootDir = new File(attachDir);
        
        // 将附件从上传临时目录剪切到站点指定的附件目录里
        String fileName = FileHelper.copyFile(rootDir, file); 
		String fileSuffix = FileHelper.getFileSuffix(fileName);
		
		// file指向剪切后的地址
		file = new File(rootDir + "/" +fileName);
		
		// 对附件进行重命名
		fileName = System.currentTimeMillis() + "." + fileSuffix;
		String newPath = file.getParent() + "/" + fileName;
        file.renameTo(new File(newPath));
        
        attach.setFileName(fileName);
        attach.setFileExt(fileSuffix.toLowerCase());
		
        recordService.createAttach(attach);

		return attach;
	}
}