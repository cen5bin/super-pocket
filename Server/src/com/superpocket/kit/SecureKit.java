package com.superpocket.kit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecureKit {
	private static final Logger logger = LogManager.getLogger();
	private static final String PASSWORD_ENCRYPT_ALGO = "SHA-256"; //服务端对密码进行加密的算法
	private static final String COOKIE_ENCRYPT_ALGO = "MD5"; //cookie加密
	private static final String COOKIE_ENCRYPT_KEY = "UdbhIhf，UgGF？%i2N";
	
	/**
	 * 将bytes数组用ENCRYPT_ALGO加密
	 * @param bytes 待加密的字符数组
	 * @param algo  加密算法
	 * @return 加密后的byte数组
	 */
	private static byte[] encrypt(byte[] bytes, String algo) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algo);
			md.update(bytes);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加密密码 加盐
	 * @param password 密码
	 * @param salt 盐
	 * @return
	 */
	public static String encryptPassword(String password, String salt) {
		byte[] saltBytes = Base64.getDecoder().decode(salt);
		byte[] passBytes = encrypt(password.getBytes(), PASSWORD_ENCRYPT_ALGO);
		byte[] passWithSalt = new byte[saltBytes.length + passBytes.length];
		for (int i = 0; i < passBytes.length; ++i) passWithSalt[i] = passBytes[i];
		for (int i = 0; i < saltBytes.length; ++i) passWithSalt[i+passBytes.length] = saltBytes[i];
		byte[] encryptedPass = encrypt(passWithSalt, PASSWORD_ENCRYPT_ALGO);
		return Base64.getEncoder().encodeToString(encryptedPass);
	}
	
	/**
	 * 产生盐
	 * @param len 盐的位数
	 * @return
	 */
	public static String generateSalt(int len) {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[len];
		random.nextBytes(bytes);
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	/**
	 * 加密用户email，写到cookie中
	 * @param email
	 * @return
	 */
	public static String encryptCookie(String email) {
		byte[] bytes = encrypt((email+COOKIE_ENCRYPT_KEY).getBytes(), COOKIE_ENCRYPT_ALGO);
		return Base64.getEncoder().encodeToString(bytes);
	}
	
	public static void main(String[] args) {
		logger.debug(encryptPassword("asd", generateSalt(32)));
		logger.debug(encryptPassword("asd", generateSalt(32)).length());
		
		logger.debug(encryptPassword("asd", generateSalt(32)));
		String s = Base64.getEncoder().encodeToString("asdasda".getBytes());
		String s1 = new String(Base64.getDecoder().decode(s));
		logger.debug(s1);
		
		logger.debug(encryptPassword("asd", generateSalt(32)).length());
		logger.debug(encryptPassword("asd", generateSalt(32)));
	}
}
