/**
 * 
 */
package com.daliu.classtime.utils;

import java.util.List;

/**  
* @Title: StringUtils.java
* @Package:com.daliu.classtime.utils
* @Description:(对字符串的操作)
* @author:刘严岩 
* @date:2019年3月21日
*/
public class StringUtils {
	
	static String log="\r\n****************      新纪录开始       **********************\r\n";
	
	/**
	 * 
	 * @Description:(判断字符串的长度)
	 * @param:@param s
	 * @param:@return   
	 * @return:int  
	 * @date:2019年3月21日
	 */
	public static int getWordCount(String s)  
    {  
        int length = 0;  
        for(int i = 0; i < s.length(); i++)  
        {  
            int ascii = Character.codePointAt(s, i);  
            if(ascii >= 0 && ascii <=255)  
                length++;  
            else  
                length += 2;  

        }  
        return length;  

    }  
	
	/**
	 * 
	 * @Description:(可截取中文字符串)
	 * @param:@param str
	 * @param:@param length
	 * @param:@return   
	 * @return:String  
	 * @date:2019年3月21日
	 */
	public static String getSubString(String str, int length) {
        int count = 0;
        int offset = 0;
        char[] c = str.toCharArray();
        int size = c.length;
        if(size >= length){
            for (int i = 0; i < c.length; i++) {
                if (c[i] > 256) {
                    offset = 2;
                    count += 2;
                } else {
                    offset = 1;
                    count++;
                }
                if (count == length) {
                    return str.substring(0, i + 1);
                }
                if ((count == length + 1 && offset == 2)) {
                    return str.substring(0, i);
                }
            }
        }else{
            return str;
        }
        return "";
    }

	/**
	 * 
	 * @Description:(返回日志的格式，统一输出标准)
	 * @param:@param string
	 * @param:@return   
	 * @return:String  
	 * @date:2019年3月21日
	 */
	public static String logMsg(List<String> list){
		String str="";
		for (String string : list) {
			str=str+string+"\r\n";
		}
		str+=log;
		return str;
	}

}
