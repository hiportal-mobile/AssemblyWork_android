package com.skt.pe.common.conf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.util.StringUtil;


/**
 * 상수
 *
 * @author  : june
 * 
 */
public final class Constants {
	/**
	 * 필수 어플리케이션 정보 클래스 
	 * @author june
	 *
	 */

	public static class CoreComponent {
		private static final Map<String, String> appMap = new LinkedHashMap<String, String>();
		
		public static String APP_ID_AUTH     = "MOCR000001";
		public static String APP_ID_CLIENT   = "MOCR000003";
		public static String APP_ID_MEMBER	 = "MOCR000004";
		public static String APP_ID_EMAIL    = "MOGP000001";
		public static String APP_ID_SCHEDULE = "MOGP000002";
		public static String APP_ID_WIDGET   = "MOCR000004";
		public static String APP_ID_VIEWER   = "MOCR000002";
		public static String APP_ID_EASY_APPROVAL = "MOGP000006";
		public static String APP_ID_DRM		 = "";
		
		public static String APP_NM_AUTH     = "hi-moffice인증";
		public static String APP_NM_CLIENT   = "hi-스토어";
		public static String APP_NM_MEMBER	 = "직원검색";
		public static String APP_NM_EMAIL    = "사내메일";
		public static String APP_NM_SCHEDULE = "일정관리";
		public static String APP_NM_WIDGET   = "hi-moffice";
		public static String APP_NM_VIEWER   = "파일뷰어";
		public static String APP_NM_DRM		 = "DRM";

		static {
			appMap.put(APP_ID_AUTH    , APP_NM_AUTH);
			appMap.put(APP_ID_CLIENT  , APP_NM_CLIENT);
			appMap.put(APP_ID_WIDGET  , APP_NM_WIDGET);
			appMap.put(APP_ID_VIEWER  , APP_NM_VIEWER);
		}

		/**
		 * 필수 어플 목록을 리턴한다.
		 * @return 필수 목록 iterator
		 */
		public static Iterator<String> iterator() {
			return appMap.keySet().iterator();
		}

		/**
		 * 필수 어플리케이션 여부를 리턴한다.
		 * @param context context
		 * @return 필수여부
		 */
		public static boolean isCore(Context context) {
			String appId = SKTUtil.getAppId(context);
			return appMap.containsKey(appId);
		}

		/**
		 * 어플아이디로 필수 어플리케이션 여부를 리턴한다.
		 * @param appId 어플아이디
		 * @return 필수여부
		 */
		public static boolean isCore(String appId) {
			return appMap.containsKey(appId);
		}

		/**
		 * 필수 어플리케이션 설치 여부를 체크한다.
		 * @param context context
		 * @return 미설치된 어플리케이션 리스트
		 * TODO: getInstalledPackages 안의 '0'을 PackageManager.GET_UNINSTALLED_PACKAGES 로 바꾸어 본다. 
		 */
		public static List<String> checkCore(Context context) {
			PackageManager manager = context.getPackageManager();
			List<PackageInfo> packages = manager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
			
			
			List<String> appIdList = new ArrayList<String>();
			for(PackageInfo pack : packages) {
//				SKTUtil.log("Debug", "[checkCore] package : " + pack.toString());
				ApplicationInfo app = pack.applicationInfo;
				String b_appId = app.loadDescription(manager) + "";
				
				if(!StringUtil.isNull(b_appId)) {
					appIdList.add(b_appId);
				}
			}

			List<String> ret = new ArrayList<String>();
			Iterator<String> it = appMap.keySet().iterator();
			while(it.hasNext()) {
				String appId = it.next();
				if(!appIdList.contains(appId)) {
					ret.add(appId);
				}
			}

			if(ret.size() > 0) {
				return ret;
			} else {
				return null;
			}
		}
		
		public static String getCoreName(String appId) {
			return appMap.get(appId);
		}
	}

	public enum Connection_N_Status{
		 LEVEL_NOLEVEL,
         LEVEL_CONNECTING,
         LEVEL_CONNECTED,
         LEVEL_AUTH_FAILED,
         LEVEL_PW_EXPIRED,
         LEVEL_BLOCK_ACCESS,
         LEVEL_DUP_LOGIN,
         LEVEL_NONETWORK,
         LEVEL_CRITICAL_ITEMS_NOT_FOUNTD,
         LEVEL_DISCONNECTED_DONE
    }
	
	public static class SGVPN{
		public static final String API = "com.sgvpn.vpn_service.api.MobileApi";
		public static final String STATUS = "com.sgvpn.vpnservice.STATUS";
		public static final String PACKAGE = "com.sgvpn.vpn_service";
	}
	/**
	 * 인증 정보 클래스
	 * @author june
	 *
	 */
	public static class Auth {
//		public static final String CONTENT_URI_STRING = "content://" + EnvironManager.ACTION_PREFIX + ".auth";
		public static final String CONTENT_URI_STRING = "content://" + EnvironManager.ACTION_PREFIX + ".folder";
		public static final Uri CONTENT_URI           = Uri.parse(CONTENT_URI_STRING);
		public static final Uri CONTENT_AUTHKEY_URI   = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_AUTH_KEY);
		public static final Uri CONTENT_SECRETKEY_URI = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_SECRET_KEY);
		public static final Uri CONTENT_NONCE_URI     = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_NONCE);
		public static final Uri CONTENT_ENC_PWD_URI   = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_ENC_PWD);
		public static final Uri CONTENT_GMP_AUTH_URI  = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_GMP_AUTH);
		public static final Uri CONTENT_GMP_AUTH_PWD_URI = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_GMP_AUTH_PWD);
		public static final Uri CONTENT_CHECKED_COMPANY_CD_URI = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_CHECKED_COMPANY_CD);
		public static final Uri CONTENT_COMPANY_LIST_URI = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_COMPANY_LIST);
		//CJ 추가
		public static final Uri CONTENT_ID_URI = Uri.parse(CONTENT_URI_STRING + "/" + AuthData.ID_ID);
	}

	/**
	 * 다운로더 정보 클래스
	 * @author june
	 *
	 */
	public static class Download {
		public static final String CONTENT_URI_STRING = "content://" + EnvironManager.ACTION_PREFIX + ".installer";
		public static final Uri CONTENT_URI           = Uri.parse(CONTENT_URI_STRING);

		public static final String KEY_INSTALL_FLAG = "installFlag";
		public static final String KEY_DOWNLOAD_URL = "downloadUrl";
		public static final String KEY_APP_ID       = "appId";
		public static final String KEY_APP_NAME     = "appName";
		public static final String KEY_APP_ICON     = "appIcon";
		public static final String KEY_APP_ICON_URL = "appIconUrl";
		public static final String KEY_APP_TMP_PATH = "appTmpPath";
		public static final String KEY_APP_DOWN_PATH = "appDownPath";
		public static final String KEY_APP_AUTH     = "appAuth";

		public static final String KEY_APP_IDS      = "appIds";
		
		public static final int    INST_TYPE_NONE   = 0;
		public static final int    INST_TYPE_SILENT = 1;
		public static final int    INST_TYPE_MANUAL = 2;

		public static final String BUFFER_DIR   = Environment.getExternalStorageDirectory() + "/.installer/.tmp";
		public static final String INSTALL_DIR  = Environment.getExternalStorageDirectory() + "/.installer/apk";
		public static final String WDOWNLOAD_DIR  = Environment.getExternalStorageDirectory() + "/download";
		public static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory() + "/.installer";
		
		public static final String SSM_INSTALL_DIR = Environment.getExternalStorageDirectory() + "/download"; 
		
		
		public static final String INSTALL_FAILED_RETCODE = "INSTALL_FAILED_RETCODE";
		public static final String INSTALL_RETURN_CODE    = "INSTALL_RETURN_CODE";
		
		public static final int RET_INVALID = -104;
	}

	/**
	 * 액션 정보 클래스 
	 * @author june
	 *
	 */
	public static class Action {
		public static final String LOGIN_TICK		= EnvironManager.ACTION_PREFIX + ".folder.LOGIN_TICK";
		public static final String LOGOUT_TICK		= EnvironManager.ACTION_PREFIX + ".folder.LOGOUT_TICK";

		public static final String AUTH_SERVICE     = EnvironManager.ACTION_PREFIX + ".folder.AUTH_SERVICE";
		public static final String GMP_LOGIN       	= EnvironManager.ACTION_PREFIX + ".folder.LOGIN";
		public static final String LEGACY_LOGIN    	= EnvironManager.ACTION_PREFIX + ".folder.LEGACY_LOGIN";

		public static final String FOLDER_VIEW      = EnvironManager.ACTION_PREFIX + ".folder.FOLDER";
		public static final String EMAIL_WRITE     	= EnvironManager.ACTION_PREFIX + ".mail.EMAILCLIENT";
//		public static final String IMAGE_VIEW      	= EnvironManager.ACTION_PREFIX + ".imageviewer.IMAGEVIEWER";
		public static final String IMAGE_VIEW      	= EnvironManager.ACTION_PREFIX + ".imageviewer.IMAGEVIEWER";
		public static final String STORE_DETAIL_VIEW= EnvironManager.ACTION_PREFIX + ".store.detail";		//com.ex.group.store.detail

		public final static String SCHEDULE_REGISTER= EnvironManager.ACTION_PREFIX + ".schedule.REGISTER_NOTIFY";
		public final static String SCHEDULE_DELETE  = EnvironManager.ACTION_PREFIX + ".schedule.DELETE_NOTIFY";
		
		public final static String MEMBER_SEARCH = EnvironManager.ACTION_PREFIX + ".addressbook";
		public final static String MEMBER_SEARCH_MAIN = EnvironManager.ACTION_PREFIX + ".addressbook.activity.AddressTabActivity";

		public static final String DOWNLOADING_SERVICE     = EnvironManager.ACTION_PREFIX + ".installer.DOWNLOADING_SERVICE";
		public static final String DOWNLOADED_SERVICE      = EnvironManager.ACTION_PREFIX + ".installer.DOWNLOADED_SERVICE";
		public static final String DOWNLOAD_FAILED_SERVICE = EnvironManager.ACTION_PREFIX + ".installer.DOWNLOAD_FAILED_SERVICE";
		public static final String INSTALLING_SERVICE      = EnvironManager.ACTION_PREFIX + ".installer.INSTALLING_SERVICE";
		public static final String INSTALLED_SERVICE       = EnvironManager.ACTION_PREFIX + ".installer.INSTALLED_SERVICE";
		public static final String INSTALL_FAILED_SERVICE  = EnvironManager.ACTION_PREFIX + ".installer.INSTALL_FAILED_SERVICE";
		
		public static final String MYGROUP_NOTICE_DETAIL = EnvironManager.ACTION_PREFIX + ".notice.mygroup.detail";
	}

	/**
	 * 에러 정보 클래스
	 * @author june
	 *
	 */
	public static class Status {

		public static final String S_SUCCESS   = "1000";

		public static final String E_AUTH_NULL    = "E001";		
		public static final String E_AUTH_FAIL    = "E002";
		public static final String E_AUTH_CNFM    = "E003";
		public static final String E_AUTH_EMPY    = "E004";
		public static final String E_AUTH_PW_N    = "E005";
		public static final String E_AUTH_NOT     = "E006";
		public static final String E_AUTH_GMP     = "E007";
		public static final String E_AUTH_LEGACY  = "E008";
		public static final String E_NORMAL       = "E011";	//무선접속 안 된 상태
		public static final String E_SERVER       = "E012"; //메세지 등록하지 않았음. 추후 등록
		public static final String E_PARSING      = "E013"; //메세지 등록하지 않았음. 추후 등록
		public static final String E_INTERNAL     = "E014"; //메세지 등록하지 않았음. 추후 등록
		public static final String E_DOWNLOAD     = "E018"; //메세지 등록하지 않았음. 추후 등록
		public static final String E_INSTALL      = "E019"; //메세지 등록하지 않았음. 추후 등록
		public static final String E_CONN_TIMEOUT = "E021";	//업무망접속X
		public static final String E_READ_TIMEOUT = "E022";	//업무망접속X
		public static final String E_RESPONSE     = "E023";
		public static final String E_ROAMING      = "E031";
		public static final String E_USIM         = "E032";
		public static final String E_NETWORK      = "E033";
		public static final String E_CORE_CHK     = "E034";
		public static final String E_CORE_CHK_WEB = "E034_WEB";
		public static final String E_KEYCODE      = "E041";
		public static final String E_IMG_VIEW     = "E051";

		//CJ 추가
		public static final String E_DIFF_ID      = "E066";
		
		// 한국도로공사 추가
		public static final String E_NOT_INSTALL_SSLVPN_ID      = "E071";
		public static final String E_NOT_CONNECT_SSLVPN_ID      = "E072";
		public static final String E_NOT_BIND_SSLVPN_ID      	= "E073";

		public static final String N_3000 = "3000";
		public static final String N_3101 = "3101";
		public static final String N_3102 = "3102";
		public static final String N_3103 = "3103";
		public static final String N_3104 = "3104";
		public static final String N_3201 = "3201";
		public static final String N_3202 = "3202";
		public static final String N_3203 = "3203";
		public static final String N_3204 = "3204";
		public static final String N_3205 = "3205";
		public static final String N_3205_A   = "3205_A";
		public static final String N_3205_WEB = "3205_WEB";
		public static final String N_3206 = "3206";
		public static final String N_3300 = "3300";
		
		//메세지 등록하지 않음
		public static final String N_7001 = "7001";
		public static final String N_7002 = "7002";
		public static final String N_7003 = "7003";
		public static final String N_7004 = "7004";
		public static final String N_7005 = "7005";
		public static final String N_7008 = "7008";
		public static final String N_7009 = "7009";
		public static final String N_7010 = "7010";
		public static final String N_7011 = "7011";
		public static final String N_7012 = "7012";
		public static final String N_7014 = "7014";
		public static final String N_7015 = "7015";
		
		public static final String N_8002 = "8002";
		public static final String N_8003 = "8003";
		
		public static final int VPN_SERVICE_PERMISSION_ALLOW = 9131;
		public static final int VPN_SERVICE_PERMISSION_DNEY = 9132;
		public static final int APP_PERMISSION_RETURN = 9133;
		public static final int PERMISSION_CHECK = 9134;

		public static List<String> errList = new ArrayList<String>();

		static {
			errList.add(E_AUTH_NULL);
			errList.add(E_AUTH_FAIL);
			errList.add(E_AUTH_CNFM);
			errList.add(E_AUTH_EMPY);
			errList.add(E_AUTH_PW_N);
			errList.add(E_AUTH_NOT);
			errList.add(E_AUTH_GMP);
			errList.add(E_AUTH_LEGACY);
			errList.add(E_NORMAL);
			errList.add(E_CONN_TIMEOUT);
			errList.add(E_READ_TIMEOUT);
			errList.add(E_RESPONSE);
			errList.add(E_ROAMING);
			errList.add(E_USIM);
			errList.add(E_NETWORK);
			errList.add(E_CORE_CHK);
			errList.add(E_CORE_CHK_WEB);
			errList.add(E_KEYCODE);
			errList.add(E_IMG_VIEW);
			errList.add(E_NOT_INSTALL_SSLVPN_ID);
			errList.add(E_NOT_CONNECT_SSLVPN_ID);

			errList.add(N_3000);
			errList.add(N_3101);
			errList.add(N_3102);
			errList.add(N_3103);
			errList.add(N_3104);
			errList.add(N_3201);
			errList.add(N_3202);
			errList.add(N_3203);
			errList.add(N_3204);
			errList.add(N_3205);
			errList.add(N_3205_A);
			errList.add(N_3205_WEB);
			errList.add(N_3206);
			errList.add(N_3300);

			errList.add(N_8002);
			errList.add(N_8003);
		}

		public static void addErrMsg(String messageResId) {
			errList.add(messageResId);
		}
		
	}
	
	public static final String URL_T_OFFICE       = "http://mg.ex.co.kr"; // "http://himoffice.iptime.org:5000/";
	public static final String PKG_T_OFFICE       = EnvironManager.ACTION_PREFIX + ".store";

//	public static final String PRIMITIVE_ATTACH_VIEW = "ATTACH_VIEW";
	public static final String PRIMITIVE_ATTACH_VIEW = "COMMON_ISS_RETRIEVE";
	public static final String PRIMITIVE_ORIGINAL_VIEW = "COMMON_ISS_SYNC";
	public static final String PRIMITIVE_COMMON_MO_LOGIN = "COMMON_MO_LOGIN";
	
	
	public static final String FMT_DATE_SOURCE    = "yyyy-MM-dd";
	public static final String FMT_DATE_TARGET    = "yyyy.MM.dd";
	public static final String FMT_DATE_PLAIN     = "yyyyMMdd";
	
	public static final String TYPE_LAYOUT        = "layout";
	public static final String TYPE_ID            = "id";
	public static final String TYPE_STRING        = "string";	
	public static final String TYPE_DRAWABLE      = "drawable";
	
	public static final int    REQ_AUTH           = 1007;
	public static final int    REQ_AUTH_FIRST           = 1000;		//첫 접속 시 인증 
	
	public static final int    REQ_ACCOUNT_CHANGE = 2008;
	public static final int    REQ_AUTH_ACTIVITY  = 404;
	public static final int    REQ_IMAGE          = 616;
	public static final int    RES_CLOSE		  = 303;
	public static final int    RES_HISTORY 	  	  = 1068;

	public static final int    MENU_CLOSE         = 303;
	
	public static final String ID_NODE            = "_NODE_";
	public static final String ID_MDN             = "_MDN_";
	public static final String CHECK_AUTH    = "_CHECKAUTH_";
	
	// 스토어 연동 파라미터
	public static final String KEY_DOWNLOAD_APPID = "DOWNLOAD_APPID"; 	// 기존 PE에서 사용되는 파라미터
	public static final String KEY_APP_ID         = "APP_ID";			// GMP에서 사용되는 파라미터
	public static final String KEY_APP_FILE       = "APP_FILE";

	// 이메일 연동 파라미터
	public static final String KEY_EMAIL_TO = "toList";

	// 투데이 연동 파라미터
	public static final String PREFIX_TODAY = "&sk_today_content_id=";
	public static final String KEY_TODAY_ID = "bulletinKey";

	public static final String TAG_PRIMITIVE      = "primitive";
	public static final String TAG_RESULT         = "result";
	public static final String TAG_RESULT_MESSAGE = "resultMessage";
	public static final String TAG_NEXT_NONCE     = "nextNonce";
	
	
	
	public static final int SSM_UNINSTALLED = 1001;						//SSM 설치되지 않은 경우
	public static final int V3_UNINSTALLED = 1002;						//V3 설치되지 않은 경우
	public static final int SSM_OLD_INSTALLED = 1003;						//SSM 구 버전설치된 경우
	public static final int SSM_UNREGISTERED = 2001;					//SSM 인증 되지 않았을 경우
	public static final int V3_UNREGISTERED = 2002;						//V3 인증 되지 않았을 경우
	public static final int SSM_DEFAULT = 3001;								//SSM 설치 또는 인증 실패
	public static final int V3_DEFAULT = 3002;									//V3 설치 또는 인증 실패
	public static final int ROOTED = 4001;									//루팅 된 단말

	
	

}
