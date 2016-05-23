package com.boubei.tss.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfoEncoderTest {
    
	@Test
    public void testInfoEncoder() {
        InfoEncoder test = new InfoEncoder();
        
        String encodedMsg = test.createEncryptor("Jon.King");
		assertEquals(encodedMsg, "TcftExflLdPiFfNNVkG9JQ==");
        assertEquals("Jon.King", test.createDecryptor(encodedMsg));
        
        String md5PWD = InfoEncoder.string2MD5("Admin_123456");
        assertEquals("E5E0A2593A3AE4C038081D5F113CEC78", md5PWD);
        
        String testValue = "Jon.King!@#$%^&*()";
        int key = 869;
        String encodeValue = InfoEncoder.simpleEncode(testValue, key);
        System.out.println(encodeValue);
        assertEquals(testValue, InfoEncoder.simpleDecode(encodeValue, key));
        
        System.out.println('X' ^ 't' ^ 't');
		System.out.println('!' ^ 't' ^ 't');
        
    }

}
