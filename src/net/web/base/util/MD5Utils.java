package net.web.base.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public class MD5Utils {

	public static final String SALT = "{1!2@3#4$5%6^7&8*9(0)-zhuzhk}";

	public static String hashed(String plainText) {
		byte[] temp = (plainText + SALT).getBytes();
		StringBuffer buffer = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			md.update(temp);
			temp = md.digest();
			int i = 0;
			for (int offset = 0; offset < temp.length; ++offset) {
				i = temp[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buffer.append("0");
				buffer.append(Integer.toHexString(i));
			}
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

}
