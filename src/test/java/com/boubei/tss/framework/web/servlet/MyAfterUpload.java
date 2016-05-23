package com.boubei.tss.framework.web.servlet;

import javax.servlet.http.HttpServletRequest;

public class MyAfterUpload implements AfterUpload {

	public String processUploadFile(HttpServletRequest request, 
			String filepath, String oldfileName) throws Exception {
		
         return "parent.alert(\"导入成功！\");parent.loadInitData();";
	}
}