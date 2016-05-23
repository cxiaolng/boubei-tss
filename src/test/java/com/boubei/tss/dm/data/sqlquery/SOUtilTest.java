package com.boubei.tss.dm.data.sqlquery;

import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
	
public class SOUtilTest {
	
	@Test
	public void testSOUtil() {
		CustomerSO so = new CustomerSO();
		so.id = "1200";
		so.xxCodes = "x1,x2";
		so.birthday = new Date();
		
		Map<String, Object> map = SOUtil.getProperties(so, "id");
		Assert.assertNull(map.get("id"));
		Assert.assertEquals("'x1','x2'", map.get("xxCodes"));
		
		System.out.println(so.toString());
		System.out.println(SOUtil.generateQueryParametersMap(so));
		
		String script = "my id is ${id}";
		script = SOUtil.freemarkerParse(script, so);
		Assert.assertEquals("my id is 1200", script);
		
		Assert.assertEquals("'s1','s2','s3'", SOUtil.insertSingleQuotes("s1,s2,s3"));
		Assert.assertEquals("'s1'", SOUtil.insertSingleQuotes("s1"));
		Assert.assertNull(SOUtil.insertSingleQuotes(null));
		
		so.xxCodes = "";
		Assert.assertNull(SOUtil.getProperties(so, "id").get("xxCodes"));
	}
	
	public class CustomerSO extends AbstractSO {
 
		private static final long serialVersionUID = 1L;
		
		private String id;  
	    private String name;  
	    private String xxCodes;  
	    private Date birthday;  
	    
	    public String getId() {  
	        return id;  
	    }  
	    public void setId(String id) {  
	        this.id = id;  
	    }  
	    public String getName() {  
	        return name;  
	    }  
	    public void setName(String name) {  
	        this.name = name;  
	    }  
	    public Date getBirthday() {  
	        return birthday;  
	    }  
	    public void setBirthday(Date birthday) {  
	        this.birthday = birthday;  
	    }  
	 
		public String[] getParameterNames() {
			return new String[] {"id", "name", "birthday"};
		}
		public String getXxCodes() {
			return xxCodes;
		}
		public void setXxCodes(String xxCodes) {
			this.xxCodes = xxCodes;
		}  
	}
}  
