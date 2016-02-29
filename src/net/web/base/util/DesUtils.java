package net.web.base.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.lang.ArrayUtils;

public class DesUtils {

	private static String ivInfo = "0000000000000000";// 初始向量

	private static char[] CHARARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static byte[] str2Bcd(char[] data) {
		int len = data.length;
		if (len % 2 != 0 || len == 0) {
			throw new RuntimeException("数据长度错误");
		}
		byte[] outData = new byte[len >> 1];
		for (int i = 0, j = 0; j < len; i++) {
			outData[i] = (byte) (((Character.digit(data[j], 16) & 0x0F) << 4) | (Character.digit(data[j + 1], 16) & 0x0F));
			j++;
			j++;
		}
		return outData;
	}

	private static String byte2HexStr(byte[] data) {
		int len = data.length;
		char[] outChar = new char[len << 1];
		for (int i = 0, j = 0; j < len; j++) {
			outChar[i++] = CHARARRAY[(0xF0 & data[j]) >>> 4];
			outChar[i++] = CHARARRAY[data[j] & 0x0F];
		}
		String outString = new String(outChar);
		return outString;
	}

	private static byte[] ecbEncrypt(byte[] data, byte[] key) {
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			// DES的ECB模式
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密钥初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
			// 执行加密操作
			byte encryptedData[] = cipher.doFinal(data);
			return encryptedData;
		} catch (Exception e) {
			throw new RuntimeException("ECB-DES算法，加密数据出错!");
		}
	}

	private static byte[] ecbDecrypt(byte[] data, byte[] key) {
		try {
			// DES算法要求有一个可信任的随机数源
			SecureRandom sr = new SecureRandom();
			// 从原始密匙数据创建一个DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);
			// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成 一个SecretKey
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			// DES的ECB模式
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
			// 用密钥初始化Cipher对象
			cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
			// 正式执行解密操作
			byte decryptedData[] = cipher.doFinal(data);
			return decryptedData;
		} catch (Exception e) {
			throw new RuntimeException("ECB-DES算法，解密出错。");
		}
	}

	private static byte[] cbcEncrypt(byte[] data, byte[] key, byte[] iv) {
		try {
			// 从原始密钥数据创建DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			// DES的CBC模式,采用NoPadding模式，data长度必须是8的倍数
			Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
			// 用密钥初始化Cipher对象
			IvParameterSpec param = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, param);
			// 执行加密操作
			byte encryptedData[] = cipher.doFinal(data);
			return encryptedData;
		} catch (Exception e) {
			throw new RuntimeException("CBC-DES算法，加密数据出错!");
		}
	}

	@SuppressWarnings("unused")
	private static byte[] cbcDecrypt(byte[] data, byte[] key, byte[] iv) {
		try {
			// 从原始密匙数据创建一个DESKeySpec对象
			DESKeySpec dks = new DESKeySpec(key);
			// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成一个SecretKey对象
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(dks);
			// DES的CBC模式,采用NoPadding模式，data长度必须是8的倍数
			Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
			// 用密钥初始化Cipher对象
			IvParameterSpec param = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, param);
			// 正式执行解密操作
			byte decryptedData[] = cipher.doFinal(data);
			return decryptedData;
		} catch (Exception e) {
			throw new RuntimeException("CBC-DES算法，解密出错。");
		}
	}

	public static String getPinData(String cardNo, String password) {
		/* xorPin 处理 */
		byte[] xorPin = str2Bcd(String.format("%02d%-14s", password.length(), password).replace(' ', 'F').toCharArray());
		/* xorPan 处理 */
		byte[] xorPan = str2Bcd(String.format("0000%12s", cardNo.substring(cardNo.length() - 13, cardNo.length() - 1)).toCharArray());

		byte[] xorData = new byte[8];
		for (int i = 0; i < 8; i++) {
			xorData[i] = (byte) (xorPin[i] ^ xorPan[i]);
		}
		return byte2HexStr(xorData);
	}

	public static String getCheckValue(String plainTextKey) {
		String checkValue = doubleEncrypt("0000000000000000", plainTextKey);
		return checkValue.substring(0, 6);
	}

	public static String doubleEncrypt(String plainText, String key) {
		byte[] keyByte = str2Bcd(key.toCharArray());
		byte[] leftKey = ArrayUtils.subarray(keyByte, 0, 8);
		byte[] rightKey = ArrayUtils.subarray(keyByte, 8, 16);
		byte[] plainTextByte = str2Bcd(plainText.toCharArray());
		byte[] cipherText = new byte[0];

		if (plainTextByte.length % 8 != 0) {
			throw new RuntimeException("Unsupported data length");
		}

		for (int i = 0; i < plainTextByte.length / 8; i++) {
			byte[] tmpPlainText = ArrayUtils.subarray(plainTextByte, i * 8, (i + 1) * 8);

			byte[] tmpCipherText = ecbEncrypt(tmpPlainText, leftKey);
			tmpCipherText = ecbDecrypt(tmpCipherText, rightKey);
			tmpCipherText = ecbEncrypt(tmpCipherText, leftKey);

			cipherText = ArrayUtils.addAll(cipherText, tmpCipherText);
		}

		return byte2HexStr(cipherText);
	}

	public static String doubleDecrypt(String cipherText, String key) {
		byte[] keyByte = str2Bcd(key.toCharArray());
		byte[] leftKey = ArrayUtils.subarray(keyByte, 0, 8);
		byte[] rightKey = ArrayUtils.subarray(keyByte, 8, 16);
		byte[] cipherTextByte = str2Bcd(cipherText.toCharArray());
		byte[] plainText = new byte[0];

		if (cipherTextByte.length % 8 != 0) {
			throw new RuntimeException("Unsupported data length");
		}

		for (int i = 0; i < cipherTextByte.length / 8; i++) {
			byte[] tmpCipherText = ArrayUtils.subarray(cipherTextByte, i * 8, (i + 1) * 8);

			byte[] tmpPlainText = ecbDecrypt(tmpCipherText, leftKey);
			tmpPlainText = ecbEncrypt(tmpPlainText, rightKey);
			tmpPlainText = ecbDecrypt(tmpPlainText, leftKey);

			plainText = ArrayUtils.addAll(plainText, tmpPlainText);
		}

		return byte2HexStr(plainText);
	}

	public static String encryptPinData(String pinKey, String pinData) {
		return doubleEncrypt(pinData, pinKey);
	}

	public static String encryptMacData(String macData, String macKey) {
		byte[] ivData = str2Bcd(ivInfo.toCharArray());
		byte[] inMacKey = str2Bcd(macKey.toCharArray());
		byte[] inMacData = str2Bcd(macData.toCharArray());

		if (inMacData.length % 8 != 0) {
			int iFillLen = 8 - inMacData.length % 8;
			byte[] bFillData = new byte[iFillLen];
			for (int i = 0; i < iFillLen; i++) {
				bFillData[i] = 0x00;
			}
			inMacData = ArrayUtils.addAll(inMacData, bFillData);
		}

		byte[] cipherText = cbcEncrypt(inMacData, inMacKey, ivData);
		cipherText = ArrayUtils.subarray(cipherText, cipherText.length - 8, cipherText.length);

		return byte2HexStr(cipherText);
	}

	public static void main(String[] args) {
		// 单倍长度
		String cipherText = doubleEncrypt("1111111111111111", "11111111111111111111111111111111");
		System.out.println(cipherText);

		String plainText = doubleDecrypt(cipherText, "11111111111111111111111111111111");
		System.out.println(plainText);

		// 双倍长度
		cipherText = doubleEncrypt("11111111111111111111111111111111", "11111111111111111111111111111111");
		System.out.println(cipherText);

		plainText = doubleDecrypt(cipherText, "11111111111111111111111111111111");
		System.out.println(plainText);

		// 三倍长度
		cipherText = doubleEncrypt("111111111111111111111111111111111111111111111111", "11111111111111111111111111111111");
		System.out.println(cipherText);

		plainText = doubleDecrypt(cipherText, "11111111111111111111111111111111");
		System.out.println(plainText);

		// pin加密
		String pinBuf = encryptPinData("11111111111111111111111111111111", getPinData("5123160028455874", "890502"));
		System.out.println(pinBuf);

		// 密钥校验值
		String checkValue = getCheckValue("11111111111111111111111111111111");
		System.out.println(checkValue);

		// 计算MAC
		String macBuf = encryptMacData("0000000000000000", "0000000000000000");
		System.out.println(macBuf);
	}

}
