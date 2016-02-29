package net.web.base.util;

import java.io.UnsupportedEncodingException;

public class StringUtils {

	public static int byteLength(String str) throws UnsupportedEncodingException {
		return str.getBytes("GBK").length;
	}

	public static String leftByte(String str, int length) throws UnsupportedEncodingException {
		int num = 0;// 已经截取字符的长度
		int len = 0;// 每个字符的长度
		StringBuffer sb = new StringBuffer();
		char ch[] = str.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			len = String.valueOf(ch[i]).getBytes("GBK").length;
			num += len;
			if (num > length) {
				break;
			}
			sb.append(ch[i]);
		}
		return sb.toString();
	}

}