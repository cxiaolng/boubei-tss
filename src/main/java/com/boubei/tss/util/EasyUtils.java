/* ==================================================================   
 * Created [2006-6-19] by Jon.King 
 * ==================================================================  
 * TSS 
 * ================================================================== 
 * mailTo:jinpujun@gmail.com
 * Copyright (c) Jon.King, 2015-2018  
 * ================================================================== 
*/
package com.boubei.tss.util;

import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/** 
 * <p> EasyUtils.java </p> 
 * 
 * 一些常用的工具方法
 */
public class EasyUtils {
    
    static Logger log = Logger.getLogger(EasyUtils.class);
    
    /**
     * 将对象转换成Double。
     * 用于SQL取出的数据类型字段处理，因为double 单元测试环境下取出的是BigDecimal，jobss下取出的是Double。
     * 统一转为String再转回Double。
     * 
     * @param value
     * @return
     */
    public static final Double obj2Double(Object value) {
        Double rlt = 0D;
        try{
            rlt = Double.valueOf(value == null ? "0" : value.toString());
        } catch (Exception e) {
            throw new RuntimeException("error double value: " + value + ", " + e.getMessage());
        }
        return rlt;
    }
    
    public static final Long obj2Long(Object value) {
        Long rlt = 0L;
        try{
            rlt = Long.valueOf(value == null ? "0" : value.toString());
        } catch (Exception e) {
        	 throw new RuntimeException("error long value: " + value + ", " + e.getMessage());
        }
        return rlt;
    }
    
    public static final Integer obj2Int(Object value) {
        Integer rlt = 0;
        try{
            rlt = Integer.valueOf(value == null ? "0" : value.toString());
        } catch (Exception e) {
        	 throw new RuntimeException("error int value:" + value + ", " + e.getMessage());
        }
        return rlt;
    }
    
    public static final String obj2String(Object value) {
        if(value == null) {
        	return "";
        }
        return value.toString();
    }
    
    public static String obj2Json(Object obj) {
    	ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {  
			return "";
  	    }  
    }
    
    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str.trim());
    }
    
    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || isNullOrEmpty(obj.toString());
    }
    
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 将list转换以”,“号隔开的一组字符串。
     * 通常用于转换id列表。
     * 
     * @param list
     * @return
     */
    public static String list2Str(Collection<?> list){
        return list2Str(list, ",");
    }
    
    public static String list2Str(Collection<?> list, String seperator){
        if( isNullOrEmpty(list) ) return "";
        
        StringBuffer sb = new StringBuffer();
        int index = 0;
        for(Object obj : list){
            if(index++ > 0) {
                sb.append(seperator);
            }
            sb.append(obj == null ? "" : obj);
        }
        return sb.toString();
    }
    
    /**
     * 生成下拉所需要的value和 text
     * 用法如： EasyUtils.list2Combo(list, "year", "name", "|")
     * 
     * @param list
     * @param valueName 实体value属性名称
     * @param textName  实体text 属性名称
     * @param seperator  分隔符 "|"/","等
     * @return
     */
    public static String[] list2Combo(Collection<?> list, String valueName, String textName, String seperator){
        StringBuffer value = new StringBuffer();
        StringBuffer text = new StringBuffer();
        for(Object bean : list){
            if(value.length() > 0){
                value.append(seperator);
                text.append(seperator);
            }
            value.append(BeanUtil.getPropertyValue(bean, valueName));
            text.append (BeanUtil.getPropertyValue(bean, textName));
        }
        return new String[]{value.toString(), text.toString()};
    }
    
    /**
     * 转换utf8字符集
     * @param str
     * @return
     */
    public static String toUtf8String(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                } catch (Exception ex) {
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将十六进制的字符串解码
     * @param hex
     * @return
     */
    public static final byte[] decodeHex(String hex) {
        char chars[] = hex.toCharArray();
        byte bytes[] = new byte[chars.length / 2];
        int byteCount = 0;
        for(int i = 0; i < chars.length; i += 2){
            int newByte = 0;
            newByte |= hexCharToByte(chars[i]);
            newByte <<= 4;
            newByte |= hexCharToByte(chars[i + 1]);
            bytes[byteCount] = (byte)newByte;
            byteCount++;
        }
        return bytes;
    }

    /**
     * 将字节数组加密成十六进制
     * @param bytes
     * @return
     */
    public static final String encodeHex(byte bytes[]) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for(int i = 0; i < bytes.length; i++) {
            if((bytes[i] & 0xff) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }
 
    private static final byte hexCharToByte(char ch)  {
        switch(ch) {
        case 48: // '0'
            return 0;
        case 49: // '1'
            return 1;
        case 50: // '2'
            return 2;
        case 51: // '3'
            return 3;
        case 52: // '4'
            return 4;
        case 53: // '5'
            return 5;
        case 54: // '6'
            return 6;
        case 55: // '7'
            return 7;
        case 56: // '8'
            return 8;
        case 57: // '9'
            return 9;
        case 97: // 'a'
            return 10;
        case 98: // 'b'
            return 11;
        case 99: // 'c'
            return 12;
        case 100: // 'd'
            return 13;
        case 101: // 'e'
            return 14;
        case 102: // 'f'
            return 15;
        default:
            return 0;
        }
    }

    /**
     * 将字符串按其中子字符串分解成字符串数组。
     * @param s
     * @param s1
     * @return
     */
    public static String[] split(String s, String s1) {
        if(s == null) return null;
        
        StringTokenizer stringtokenizer = new StringTokenizer(s, s1);
        int i = stringtokenizer.countTokens();
        String as[] = new String[i];
        for(int j = 0; j < i; j++) {
            as[j] = stringtokenizer.nextToken();
        }

        return as;
    }
}