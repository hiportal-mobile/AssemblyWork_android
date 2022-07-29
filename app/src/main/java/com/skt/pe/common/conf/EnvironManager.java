package com.skt.pe.common.conf;

import java.io.InputStream;
import java.net.URL;

import android.content.Context;

import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;


/**
 * 환경 관리자 클래스
 *
 * @author  : june
 * 
 */
public class EnvironManager {
	public static final boolean PRODUCT_MODE = true;
	public static final boolean LOG_MODE     = false;
	public static final boolean IMEI_MODE	 = true; //ghchoi
//	public static final boolean SSL_VPN_MODE = false;
	public static final boolean SSL_VPN_MODE = true;
//	public static final String  MASTER_KEY   = "_CTRL_V_";
	public static final String  MASTER_KEY   = "c068e13c3f04ddf1acaa020f88504edd767bc3c0";
	public static final String  GROUP_CD     = "EX";
	public static final String  ACTION_PREFIX = "com." + GROUP_CD.toLowerCase() + ".group";

	private static Environ environ = null;
	private static String  testMdn = null;
	private static String  testCompanyCd = null;
	private static String  testEncPwd    = null;
	private static boolean needEncPwd = true;
    
    public static Environ getEnviron(Context context) throws SKTException {
    	if(environ == null) {
    		//TODO 환경설정파일
    		InputStream is = null;

    		try {
    			environ = new Environ(context, testMdn);

    			if(Environ.HTTPS.equals(environ.getProtocol())) {
    				System.setProperty("http.keepAlive", "false");
    			} else {
    				System.setProperty("http.keepAlive", "true");
    			}
    		} catch(Exception e) {
    			throw new SKTException(e);
    		} finally {
    			if(is != null) {
    				try { 
    					is.close(); 
    				} catch(Exception e) { }
    			}
    		}
    	} else {
    		//TODO
    		if(testMdn == null) {
				environ.setMdn(SKTUtil.getMdn(context));
    		} else {
    			environ.setMdn(testMdn);
    		}
    	}

    	return environ;
    }

    public static void setupEnviron(Context context) throws SKTException {
    	if(environ == null) {
    		try {
    			environ = new Environ(context, testMdn);
    		} catch(Exception e) {
    			throw new SKTException(e);
    		}
    	}
    	
    	/*
    	 * 현재는 wifi로 접속 시 SSL로 접속하는 이슈가 없어서 주석으로 처리...
    	 * by pluto248 
    	 * for 한국도로공사
    	 *
    	NetworkInfo wifi   = SKTUtil.getWifiNetwork(context);
		if(wifi.isConnected()) {
			environ.setProtocol(Environ.AUTH_PROTOCOL);
			environ.setHost(Environ.AUTH_HOST);
			environ.setPort(Environ.AUTH_PORT);
		} else {
			environ.setProtocol(Environ.PROTOCOL);
			environ.setHost(Environ.HOST);
			environ.setPort(Environ.PORT);
		}
		 */

		if(Environ.HTTPS.equals(environ.getProtocol())) {
			System.setProperty("http.keepAlive", "false");
		} else {
			System.setProperty("http.keepAlive", "true");
		}
    }
    
    public static boolean getNeedEncPwd() {
    	return needEncPwd;
    }
    
    public static void setNeedEncPwd(boolean isNeedEncPwd) {
    	needEncPwd = isNeedEncPwd;
    }

    public static String getTestMdn() {
    	return testMdn;
    }
    
    public static void setTestMdn(String mdn) {
    	if(!PRODUCT_MODE)
    		testMdn = mdn; 
    }
    
    public static String getTestCompanyCd() {
    	return testCompanyCd;
    }
    
    public static void setTestCompany(String companyCd) {
    		testCompanyCd = companyCd;   
    }
    
    public static String getTestEncPwd() {
    	return testEncPwd;
    }
    
    public static void setTestEncPwd(String encPwd) {
   		testEncPwd = encPwd; 
    }
    //TODO
    public static String getClassPath(String classFileName) {
        if(classFileName==null || classFileName.length()==0)
            return null;
        
        URL url = EnvironManager.class.getClassLoader().getResource(classFileName);
        
        if(url == null)
            return null;
  
        return url.getFile();
    }

}
