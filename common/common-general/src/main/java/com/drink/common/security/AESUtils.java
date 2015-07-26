package com.drink.common.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.spec.AlgorithmParameterSpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 
 * JDK不支持PKCS7Padding,这里使用PKCS5Padding(实现上和PKCS7Padding一致),
 * 这里手工加入iOS支持的PKCS7Padding模式
 *
 */
public class AESUtils {

	public final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	public final static String ALGORITHM = "AES";

	private static AlgorithmParameterSpec getIV() {
		byte[] iv = { 0xA, 1, 0xB, 5, 0xC, 4, 0xF, 7, 0xD, 9, 0x17, 3, 2, 0xE,
				8, 12 };
		return new IvParameterSpec(iv);
	}

	public static byte[] encrypt(byte[] plain, String passwd) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, getIV());
		return cipher.doFinal(plain);
	}

	public static byte[] encrypt(String data, String passwd) throws Exception {
		return encrypt(data.getBytes(), passwd);
	}

	public static byte[] decrypt(String data, String passwd) throws Exception {
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, getIV());
		return cipher.doFinal(Base64.decodeBase64(data));
	}

}
