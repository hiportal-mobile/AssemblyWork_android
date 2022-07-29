package com.skt.pe.util;

import org.apache.commons.codec.binary.Base64;

import com.skt.pe.common.exception.SKTException;


/**
 * Base64 유틸리티
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class Base64Util {
	
	public static byte[] dec(byte[] src) throws SKTException {
		try {
			return Base64.decodeBase64(src);
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		}
	}
	
	public static byte[] enc(byte[] src) throws SKTException {
		try {
			return Base64.encodeBase64(src);
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		}		
	}
	
	public static byte[] decodeBinay(String src) throws SKTException {
		try {
			return Base64.decodeBase64(src.getBytes());
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		}
	}
	
	public static String encodeBinary(byte[] src) throws SKTException {
		try {
			return new String(Base64.encodeBase64(src));
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		}		
	}
	
	public static String encode(String src) throws SKTException {
		try {
    		return encodeBinary(src.getBytes());
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		}
	}
	
	public static String decode(String src) throws SKTException {
		try {
			return new String(Base64Util.decodeBinay(src));
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		}
	}
	
}
