package com.example.healthyolder.util;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
	public static boolean isValidate(String content) {
		return content != null && !"".equals(content.replaceAll("\\s*",""));
	}
	public static boolean isValidate(CharSequence content) {
		return content != null && !"".equals(content.toString().replaceAll("\\s*",""));
	}

	public static boolean isValidate(Collection<?> list) {
		return list != null && list.size() > 0;
	}

	public static boolean isValidate(Map<?, ?> map) {
		return map != null && map.size() > 0;
	}

	//计算字符串中有多少字节
//	public static int getCharNum(String str){
//		char[] c = str.toCharArray();
//		for (int i = 0; i < c.length; i++){
//			String len = Integer.toBinaryString(c[i]);
//			if (len.length() > 8){
//				count++;
//			}
//		}
//	}

	//计算字符串中有多少中文
	public static int getChineseNum(String str){
		Pattern p;
		Matcher m;
		int count = 0;
		String regEx = "[\\u4e00-\\u9fa5]";
		p = Pattern.compile(regEx);
		m = p.matcher(str);
		while (m.find())
			count++;
		return count;
	}
}
