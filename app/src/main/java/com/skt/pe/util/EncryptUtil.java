package com.skt.pe.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class EncryptUtil {
	
	public static final String HMAC_SHA1  = "HmacSHA1";
	public static final String TRIPLE_DES = "DESede";
	public static final String AES        = "AES";

	public static String generateHMac(String algorithm, String key, String input) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
		byte[] keyArr = toByte(key);
		SecretKeySpec skeySpec = new SecretKeySpec(keyArr, algorithm);
			
		Mac mac = Mac.getInstance(algorithm);
		mac.init(skeySpec);
	       
		byte[] result = mac.doFinal(input.getBytes());
		return toHex(result);
	}
	
	public static String toHex(byte input[]) {
		StringBuffer buf = new StringBuffer(input.length * 2);
		
		for(int i=0; i<input.length; i++) {
			int intVal = input[i] & 0xff;			
			if (intVal < 0x10) {
				// append a zero before a one digit hex
				// number to make it two digits.
				buf.append("0");
			}
			buf.append(Integer.toHexString(intVal));
		}
		
		return buf.toString();
	}

	private static byte[] toByte(String hex) {
		byte[] ba = new byte[hex.length() / 2];
		for(int i = 0; i < ba.length; i++)
			ba[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		
		return ba;
	}
	
	//DESede
	public static byte[] generate3DesEncode(byte[] source, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (null == source || null == key || key.length() < 1)
        	return null;

        SecretKeySpec keySpec;
        if(key.length() > 24) {
        	keySpec = new SecretKeySpec(key.getBytes(), 0, 24, TRIPLE_DES);
        } else if(key.length() == 24) {
        	keySpec = new SecretKeySpec(key.getBytes(), TRIPLE_DES);
        } else {
        	return null;
        }

        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        
        byte[] cipherbyte = cipher.doFinal(source);
        return cipherbyte;
	}

	public static byte[] generate3DesDecode(byte[] source, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        if (null == source || null == key || key.length() < 1)
        	return null;
        
        SecretKeySpec keySpec;
        if(key.length() > 24) {
        	keySpec = new SecretKeySpec(key.getBytes(), 0, 24, TRIPLE_DES);
        } else if(key.length() == 24) {
        	keySpec = new SecretKeySpec(key.getBytes(), TRIPLE_DES);
        } else {
        	return null;
        }

        Cipher cipher = Cipher.getInstance(TRIPLE_DES);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        byte[] base64Bytes = cipher.doFinal(source);
        return base64Bytes;
	}
	
	//AES
	public static byte[] generateAesEncode(byte[] source, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (null == source || null == key || key.length() < 1)
        	return null;

		byte[] keyArr = toByte(key);
		SecretKeySpec keySpec = new SecretKeySpec(keyArr, AES);
		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] encBytes = cipher.doFinal(source);
		return encBytes;
	}
	
	public static byte[] generateAesDecode(byte[] source, String key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        if (null == source || null == key || key.length() < 1)
        	return null;

		byte[] keyArr = toByte(key);
		SecretKeySpec keySpec = new SecretKeySpec(keyArr, AES);
		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] decBytes = cipher.doFinal(source);
		return decBytes;
	}

}
