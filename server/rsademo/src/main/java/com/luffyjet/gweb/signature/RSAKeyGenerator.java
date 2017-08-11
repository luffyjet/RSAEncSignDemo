package com.luffyjet.gweb.signature;


import java.security.*;
import java.util.Base64;

/**
 * 请不要使用这个类生产 密钥。否则iOS无法兼容。请使用 OpenSSL生产 密钥。
 * RSA keypair 生成工具，示范Java如何生产密钥对
 */
public class RSAKeyGenerator {
	
	/**
	 * * 生成密钥对 *
	 *  JDK 产生的私钥类型为 PKCS8
	 * 
	 * @return KeyPair *
	 * @throws Exception
	 */
	public static KeyPair generateKeyPair() throws Exception {
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			final int KEY_SIZE = 1024;
			keyPairGen.initialize(KEY_SIZE);
			KeyPair keyPair = keyPairGen.generateKeyPair();

			System.out.println("公钥："+new String(Base64.getEncoder().encode(keyPair.getPublic().getEncoded())));
			System.out.println("私钥："+new String(Base64.getEncoder().encode(keyPair.getPrivate().getEncoded())));

			return keyPair;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}


	public static void main(String[] args) throws Exception {
		RSAKeyGenerator.generateKeyPair().getPublic();
	}
}
