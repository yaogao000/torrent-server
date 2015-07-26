package com.drink.common.security;

import java.util.Map;

import org.junit.Test;

public class RSAUtilsTest {

	private static String publicKeyStr;
	private static String privateKeyStr;

	static {
		try {
			Map<String, String> publicAndPrivateKey = RSAUtils
					.generateKeyPair();
			publicKeyStr = publicAndPrivateKey.get("public");
			privateKeyStr = publicAndPrivateKey.get("private");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEncrypt() throws Exception {
		System.out.println("公钥加密——私钥解密");
		String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
		System.out.println("\r加密前文字：\r\n" + source);
		byte[] data = source.getBytes();
		byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKeyStr);
		System.out.println("加密后文字：\r\n" + new String(encodedData));
		byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData,
				privateKeyStr);
		String target = new String(decodedData);
		System.out.println("解密后文字: \r\n" + target);
	}

	@Test
	public void testEncryptWithBase64Format() throws Exception {
		System.out.println("公钥加密——私钥解密");
		String source = "这是一行没有任何意义的文字，你看完了等于没看，不是吗？";
		System.out.println("\r加密前文字：\r\n" + source);
		byte[] data = source.getBytes();
		byte[] encodedData = RSAUtils.encryptByPublicKey(data, publicKeyStr);
		String encodeStr = Base64Utils.encode(encodedData);
		System.out.println("加密后文字：\r\n" + encodeStr);
		encodedData = Base64Utils.decode(encodeStr);
		byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData,
				privateKeyStr);
		String target = new String(decodedData);
		System.out.println("解密后文字: \r\n" + target);
	}

	@Test
	public void testSign() throws Exception {
		System.out.println("私钥加密——公钥解密");
		String source = "这是一行测试RSA数字签名的无意义文字";
		System.out.println("原文字：\r\n" + source);
		byte[] data = source.getBytes();
		byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKeyStr);
		System.out.println("加密后：\r\n" + new String(encodedData));
		byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData,
				publicKeyStr);
		String target = new String(decodedData);
		System.out.println("解密后: \r\n" + target);
		System.out.println("私钥签名——公钥验证签名");
		String sign = RSAUtils.sign(encodedData, privateKeyStr);
		System.out.println("签名:\r" + sign);
		boolean status = RSAUtils.verify(encodedData, publicKeyStr, sign);
		System.out.println("验证结果:\r" + status);
	}

}
