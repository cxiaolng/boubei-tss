package com.boubei.tss.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InfoEncoderTest {
    
	@Test
    public void testInfoEncoder() {
        InfoEncoder test = new InfoEncoder();
        
        String encodedMsg = test.createEncryptor("Jon.King");
		assertEquals("0q7lY7PAMeMid9RK9PA7ew==", encodedMsg);
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

	public static void main(String[] args) {
		InfoEncoder ie = new InfoEncoder();
		System.out.println( ie.createEncryptor("1212") );
		System.out.println( ie.createDecryptor("Xsz/s+VvOgY=") );
		System.out.println( ie.createDecryptor( "3u1dt5T33PNCgByaGoyMFOMw7N973TrsYbIN9AjcpUILtQjEGWFju4mdid01QmHK9o5ipEzBVO3xfREypahK5w==" ) );
	}
}
