package com.skt.pe.common.conf;

import java.net.URLEncoder;

import org.w3c.dom.Document;

import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.util.XMLUtil;

import android.content.Context;
import android.telephony.TelephonyManager;


/**
 * 환경 설정 클래스
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class Environ {
	public  static final String HTTP         = "http";
	public  static final String HTTPS        = "https";

	private static final String TAG_PROTOCOL = "protocol";
	private static final String TAG_HOST     = "host";
	private static final String TAG_PORT     = "port";

	public  static final String PARAM_MDN    = "mdn";
	public  static final String PARAM_APP_ID = "appId";
	public  static final String PARAM_APP_VER= "appVer";
	public  static final String PARAM_LANG   = "lang";
	public  static final String PARAM_GROUP_CD = "groupCd";

	//CJ 추가
	public  static final String PARAM_LOGINFO   = "logInfo";
	public static final String FILE_SERVICE      = "/emp_ex/service.pe";
//	public static final String FILE_AUTH         = "/emp.ex/login.pe";
	public static final String FILE_AUTH         = "/emp_ex/login.pe";
	public static final String FILE_NON_GP       = "/emp_ex/login.pe";
	public static final String FILE_NO_AUTH      = "/emp_ex/login.pe";
//	public static final String FILE_ATTACHMENT   = "/emp_ex/attachment.pe";
	public static final String FILE_ATTACHMENT   = "/emp_ex/service.pe";
	public static final String FILE_CONTENTIMG   = "/emp_ex/service.pe";
//	public static final String FILE_CONTENTIMG   = "/emp_ex/contentImage.pe";
	public static final String FILE_NOTIFICATION = "/emp_ex/notification.pe";
	public static final String FILE_INSTALLER    = "/emp_ex/mobileInstaller.pe";
	public static final String FILE_COMPANYIMG   = "/emp_ex/companyImage.pe";

	/* MO 서버 정보
	 * 개발서버 -
	 * VPN IP -
	 * 상용서버 -
	 */

	/* HTTP */
	public static String PROTOCOL = HTTP;
	/*
	 * 한국도로공사 실서버
	 */
	public static String HOST     = "128.200.121.68";
	public static int    PORT     = 9000;
	/*
	 * 한국도로공사 테스트 서버
	 */
//	public static String HOST     = "himoffice.iptime.org";
//	public static String HOST     = "175.113.80.36";
//	public static int    PORT     = 9000;
	/*
	 * 안전순찰 테스트 서버
	 */
//	public static String HOST     = "211.38.146.170";
//	public static int    PORT     = 8080;
	/* HTTPS */
	public static String AUTH_PROTOCOL = HTTPS;
	public static String AUTH_HOST     = HOST;
	public static int    AUTH_PORT     = 443;

	private String protocol = PROTOCOL;
	private String host     = HOST;
	private int    port     = PORT;

	private String mdn      = "";
	private String appId    = "";
	private String ver      = "";
	private String lang     = "";
	private String groupCd  = "";

	public Environ(Context context, String testMdn) throws SKTException {
		if(testMdn == null) {
			setMdn(SKTUtil.getMdn(context));
		} else {
			setMdn(testMdn);
		}

		String appId = SKTUtil.getAppId(context);
		String ver   = SKTUtil.getVersion(context);
		String lang  = SKTUtil.getLang(context);

		if(EnvironManager.PRODUCT_MODE && (appId==null || appId.trim().length()==0 || ver==null || ver.trim().length()==0)) {
			throw new SKTException("APP_ID/VER Not Found!");
		}

    	setAppId(appId);
    	setVer  (ver);
    	setLang (lang);
    	setGroupCd(EnvironManager.GROUP_CD);
	}

	public Environ(Document conf, Context context) throws SKTException {
		XMLUtil.getTextContent(conf.getElementsByTagName(TAG_PROTOCOL).item(0));
		XMLUtil.getTextContent(conf.getElementsByTagName(TAG_HOST).item(0));
		XMLUtil.getTextContent(conf.getElementsByTagName(TAG_PORT).item(0));

		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		setMdn(tm.getLine1Number());

		String appId = SKTUtil.getAppId(context);
		String ver   = SKTUtil.getVersion(context);
		String lang  = SKTUtil.getLang(context);

		if(EnvironManager.PRODUCT_MODE && (appId==null || appId.trim().length()==0 || ver==null || ver.trim().length()==0)) {
			throw new SKTException("APP_ID/VER Not Found!");
		}

    	setAppId(appId);
    	setVer  (ver);
    	setLang (lang);
    	setGroupCd(EnvironManager.GROUP_CD);
	}

	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setPort(String port) throws SKTException {
		this.port = SKTUtil.parseInt(port);
	}
	public String getMdn() {
		return mdn;
	}
	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getGroupCd() {
		return groupCd;
	}
	public void setGroupCd(String groupCd) {
		this.groupCd = groupCd;
	}

	public static boolean isAuthService(String serviceFile) {
		if(FILE_AUTH.equals(serviceFile) ||
				FILE_INSTALLER.equals(serviceFile) ||
				FILE_NON_GP.equals(serviceFile) ||
				FILE_NO_AUTH.equals(serviceFile)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSsl() {
		if(HTTPS.equalsIgnoreCase(protocol)) {
			return true;
		} else {
			return false;
		}
	}

	public String getEnvironParam() {
		StringBuffer sb = new StringBuffer();
		//CJ 수정사항
		// CJ에서는 MDN을 UserID로 사용하지만 테스트를 위해 임시로 테스트 서버에 등록된 mdn으로 설정
		// by pluto248
		sb.append(PARAM_MDN    + "=" + URLEncoder.encode(getMdn())   + "&");
//		sb.append(PARAM_MDN    + "=" + URLEncoder.encode("01012345678")   + "&");
//		sb.append(PARAM_MDN    + "=" + URLEncoder.encode("01011112222")   + "&");
		//////////////////////////////////////////////////
		sb.append(PARAM_APP_ID + "=" + URLEncoder.encode(getAppId()) + "&");
		sb.append(PARAM_APP_VER+ "=" + URLEncoder.encode(getVer())   + "&");
		sb.append(PARAM_LANG   + "=" + URLEncoder.encode(getLang())  + "&");
		//CJ추가
//		sb.append(PARAM_LOGINFO   + "=" + URLEncoder.encode(getMdn())  + "&");

		sb.append(PARAM_GROUP_CD  + "=" + URLEncoder.encode(getGroupCd()));


		return sb.toString();
	}

}
