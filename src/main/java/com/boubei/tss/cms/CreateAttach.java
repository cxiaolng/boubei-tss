package com.boubei.tss.cms;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.boubei.tss.cms.entity.Attachment;
import com.boubei.tss.cms.service.IArticleService;
import com.boubei.tss.framework.Global;
import com.boubei.tss.framework.web.servlet.AfterUpload;

public class CreateAttach implements AfterUpload {

	Logger log = Logger.getLogger(this.getClass());

	public String processUploadFile(HttpServletRequest request,
			String filepath, String oldfileName) throws Exception {

		Long articleId = Long.parseLong(request.getParameter("articleId"));
		Long channelId = Long.parseLong(request.getParameter("channelId"));
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
		IArticleService articleService = (IArticleService) Global.getBean("ArticleService");
		Attachment attachObj = articleService.processFile(targetFile, articleId, channelId, type, oldfileName);

		// 向前台返回成功信息
		String downloadUrl = attachObj.getRelationUrl();
		Integer seqNo = attachObj.getSeqNo();
		String fileName = attachObj.getFileName();
		String fileExt = attachObj.getFileExt();
		
		return "parent.addAttachments(" + seqNo + ", '" + fileName + "', '" 
				+ fileExt + "', '" + oldfileName + "', '" + downloadUrl + "')";
	}
}