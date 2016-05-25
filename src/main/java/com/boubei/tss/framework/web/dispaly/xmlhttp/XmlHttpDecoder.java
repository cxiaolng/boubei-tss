package com.boubei.tss.framework.web.dispaly.xmlhttp;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;

import com.boubei.tss.framework.Config;
import com.boubei.tss.framework.exception.BusinessException;
import com.boubei.tss.framework.web.RewriteableHttpServletRequest;
import com.boubei.tss.framework.web.wrapper.RewriteableHttpServletRequestWrapper;
import com.boubei.tss.util.XMLDocUtil;

/**
 *
 * 将Request中XML数据流方式提交的参数解析成普通的 参数名/值 对后重新置回Request对象并返回
 *
 */
public class XmlHttpDecoder {
    
    private static Logger log = Logger.getLogger(XmlHttpDecoder.class);

	/**
	 * <p>
	 * 将Request中的XML数据流解析成普通的名值对后置回Request对象并返回
	 * </p>
	 * @param element
     *      类似：<Request><Param><Name><![CDATA[resourceId]]></Name><Value><![CDATA[2]]></Value></Param></Request>
	 * @param request
	 * @return
	 */
	public static RewriteableHttpServletRequest decode(Element element, HttpServletRequest request) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		RewriteableHttpServletRequest req = RewriteableHttpServletRequestWrapper.wrapRequest(httpRequest);

		// 输出请求（request）的详细信息：请求数据流
        log.debug("---------------------------- Request ----------------------------");
        log.debug("AppCode: " + Config.getAttribute(Config.APPLICATION_CODE));
        log.debug("Request: " + req.getContextPath() + req.getServletPath());
        log.debug("Method: " + req.getMethod());
        log.debug("Params: " + element.asXML());
        log.debug("---------------------------- End of Request ----------------------------");
		try {
            //解析Document对象，将相应的值置入Request对象中
            List<Element> paramNodes = XMLDocUtil.selectNodes(element, "Param");
            if (paramNodes != null) {
                for (Element paramNode : paramNodes) {
                    Node nameNode  = paramNode.selectSingleNode("Name");
                    Node valueNode = paramNode.selectSingleNode("Value");
                    if (nameNode != null && valueNode != null) {
                        String name  = nameNode.getText();
                        String value = valueNode.getText();
                        
                        value = value.replaceAll("&lt;!\\[CDATA\\[", "<![CDATA[").replaceAll("\\]\\]&gt;", "]]>");
                        req.addParameter(name, value);
                    }
                }
            }
		} catch (RuntimeException e) {
			throw new BusinessException("解析请求xml数据流失败", e);
		}
		return req;
	}
}
