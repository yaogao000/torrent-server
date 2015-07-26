package com.drink.common.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

public class AESUtilsTest {

	@Test
	public void test1() throws Exception {
		test("test", "1234567891011111");
		test("aesstring", "1234567891011111");
	}
	private void test(String content, String password) throws Exception{
		//加密   
		System.out.println("加密前：" + content);  
		byte[] encryptResult = AESUtils.encrypt(content.getBytes(), password);  
		//解密   
		byte[] decryptResult = AESUtils.decrypt(Base64Utils.encode(encryptResult),password);  
		System.out.println("解密后：" + new String(decryptResult));
	}

	// 用jdk实现:
	@Test
	public void jdkAES() {
		try {
			String src = "aes test";
			// 生成KEY
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			// 产生密钥
			SecretKey secretKey = keyGenerator.generateKey();
			// 获取密钥
			byte[] keyBytes = secretKey.getEncoded();

			// KEY转换
			Key key = new SecretKeySpec(keyBytes, "AES");

			// 加密
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(src.getBytes());
			System.out
					.println("jdk aes encrypt:" + Hex.encodeHexString(result));

			// 解密
			cipher.init(Cipher.DECRYPT_MODE, key);
			result = cipher.doFinal(result);
			System.out.println("jdk aes decrypt:" + new String(result));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
