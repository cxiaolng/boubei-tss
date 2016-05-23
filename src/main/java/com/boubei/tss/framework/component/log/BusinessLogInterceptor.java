package com.boubei.tss.framework.component.log;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 记录业务日志的拦截器
 * 
 */
@Component("businessLogInterceptor")
public class BusinessLogInterceptor implements MethodInterceptor {

    protected Logger log = Logger.getLogger(this.getClass());

    /** 业务日志处理对象 */
    @Autowired private IBusinessLogger businessLogger;
 
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method targetMethod = invocation.getMethod(); /* 获取目标方法 */
        Object[] args = invocation.getArguments(); /* 获取目标方法的参数 */
        
        Long preTime = System.currentTimeMillis();
        Object returnVal = invocation.proceed(); /* 调用目标方法的返回值 */
        
        int methodExcuteTime = (int) (System.currentTimeMillis() - preTime);

        Logable annotation = targetMethod.getAnnotation(Logable.class); // 取得注释对象
        if (annotation != null) {

            String operateTable = annotation.operateObject();
            String operateInfo = annotation.operateInfo();
            
            String operateMethod = targetMethod.getName();

            Log log = new Log(operateMethod, parseMacro(operateInfo, args, returnVal));
            log.setOperateTable(operateTable);
            log.setMethodExcuteTime(methodExcuteTime);

            businessLogger.output(log);
        }

        return returnVal;
    }

    /**
     * 解析日志中的宏代码
     * 
     * @param logInfo
     * @param args
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String parseMacro(String logInfo, Object[] args, Object returnVal) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<String, Object>();
        if(args != null && args.length > 0) {
    		Object[] tempArgs = new Object[args.length];
    		for( int i = 0; i < args.length; i++ ) {
    			tempArgs[i] = args[i] == null ? "" : args[i];
    		}
    		 root.put("args", tempArgs);
    	}
        root.put("returnVal", returnVal == null ? "" : returnVal);

        Template temp = new Template("t.ftl", new StringReader(logInfo), new Configuration());
        Writer out = new StringWriter();
        temp.process(root, out);
        logInfo = out.toString();
        out.flush();

        return logInfo;
    }
}