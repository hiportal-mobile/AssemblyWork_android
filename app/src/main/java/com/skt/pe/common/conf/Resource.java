package com.skt.pe.common.conf;
import java.lang.reflect.Field;

import android.content.Context;
import android.util.Log;

/**
 * 공통 리소스 클래스
 * @author june
 *
 */
public class Resource {
	public static boolean isFirst = true;
	
	public static final String RES_POSITIVE_ID   = "RES_POSITIVE";
	public static final String RES_NEGATIVE_ID   = "RES_NEGATIVE";
	public static final String RES_NEUTRAL_ID    = "RES_NEUTRAL";
	public static final String RES_CLOSE_ID      = "RES_CLOSE";
	public static final String RES_RETRIEVING_ID = "RES_RETRIEVING";
	public static final String RES_SENDING_ID    = "RES_SENDING";
	public static final String RES_VALID_COMPANYCD_ID = "RES_VALID_COMPANYCD";
	public static final String RES_LOGOUT_ID     = "RES_LOGOUT";
	public static final String RES_DLG_CLOSE_ID  = "RES_DLG_CLOSE";

	public static final String RES_POSITIVE_ko   = "확인";
	public static final String RES_NEGATIVE_ko   = "취소";
	public static final String RES_NEUTRAL_ko    = "중립";
	public static final String RES_CLOSE_ko      = "종료";
	public static final String RES_RETRIEVING_ko = "Loading...";
	public static final String RES_SENDING_ko    = "Sending Data...";
	public static final String RES_VALID_COMPANYCD_ko = "로그인 정보가 잘못되었습니다\n계정 정보를 확인하세요.";
	public static final String RES_LOGOUT_ko     = "로그아웃되었습니다.\n로그인 해주세요.";
	public static final String RES_DLG_CLOSE_ko  = "서비스를 종료하시겠습니까?";
	public static final String _E001_ko= "로그인 정보가 없습니다.\n로그인 해주세요.";
	public static final String _E002_ko= "보안을 위해 자동으로 로그아웃되었습니다.\n다시 로그인하십시오.";
	public static final String _E003_ko= "두 비밀번호가 일치하지 않습니다.";
	public static final String _E004_ko= "로그인 정보가 입력 되지 않았습니다.";
	public static final String _E005_ko= "비밀번호가 입력 되지 않았습니다.";
	public static final String _E006_ko= "인증 어플리케이션이 미설치 되었습니다.";
	public static final String _E007_ko= "로그인 정보가 없습니다.\n로그인 해주세요.";
	public static final String _E008_ko= "관계사 로그인 정보가 없습니다.\n로그인 해 주세요.";
	public static final String _E011_ko= "기능 처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E021_ko= "업무망에 접속되지  않았습니다.\n로그인 후 이용해 주십시오.";
	public static final String _E022_ko= "업무망에 접속되지 않았습니다.\n로그인 후 이용해 주십시오.";
	public static final String _E023_ko= "서버 접속이 원활하지 않습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E031_ko= "해외 로밍 데이터 요금이 부과됩니다. 이용하시겠습니까?";
	public static final String _E032_ko= "USIM 정보를 확인할 수 없어 서비스를 종료합니다.";
	public static final String _E033_ko= "서버 접속이 원활하지 않습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E034_ko= "_@@ 어플리케이션이 미설치 되었습니다.\n확인을 눌러 해당 어플리케이션을 설치하시기 바랍니다.";
	public static final String _E034_WEB_ko= "_@@ 어플리케이션이 미설치 되었습니다.\n모바일 사이트에 로그인 후 다운로드하십시오.";
	public static final String _E041_ko= "한글, 영문, 숫자만 입력이 가능합니다.";
	public static final String _E051_ko= "모바일에서 지원하지 않은 파일 포맷입니다.";
	public static final String _E071_ko= "무선접속 어플이 설치되지 않았습니다.";
	public static final String _E072_ko= "업무망에 접속되지 않았습니다. 업무접속을 진행하여 주십시오.";
	public static final String _E073_kn= "네트워크 체크 오류. 잠시 후 다시 사용해 주십시오.";
	public static final String INVALID_ID= "ID가 일치하지 않습니다.";
	public static final String ALREADY_CONNECTION= "이미 로그인한 사용자입니다.";
	public static final String ALREADY_VPN= "이미 VPN 업무망에 접속된 사용자입니다.";
	public static final String INVALID_ACCOUNT= "사원번호 또는 비밀번호가 일치하지 않습니다.";

	public static final String _E081_ko= "SSM 앱을 설치 또는 실행 후 사용 하세요.";  //ghchoi
	public static final String _E082_ko= "인증되지 않은 단말입니다. \n단말인증 후 사용하세요";  //ghchoi
	public static final String _E083_ko= "V3 설치/인증 후 사용하세요.";  //ghchoi
	
	public static final String _E084_ko= "루팅된 단말은 접속하실 수 없습니다.";  //ghchoi
	
	public static final String _3000_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3001_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3002_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3003_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3004_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3201_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3202_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3203_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3204_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3205_ko= "_@@ 어플리케이션이 업그레이드 되었습니다.\n확인을 누르면 인스톨러에서 업그레이드 하실 수 있습니다.";
	public static final String _3205_A_ko= "_@@ 어플리케이션이 업그레이드 되었습니다.\n확인을 누르면 스토어에서 업그레이드 하실 수 있습니다.";
	public static final String _3205_WEB_ko= "_@@ 어플리케이션이 업그레이드 되었습니다.\n모바일 사이트에서 업그레이드 하시기 바랍니다.";
	public static final String _3206_ko= "다운로드 정보가 등록되지 않은 Application입니다.";
	public static final String _3300_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _8002_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _8003_ko= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";

	public static final String RES_POSITIVE_en   = "확인";
	public static final String RES_NEGATIVE_en   = "취소";
	public static final String RES_NEUTRAL_en    = "중립";
	public static final String RES_CLOSE_en      = "종료";
	public static final String RES_RETRIEVING_en = "Loading...";
	public static final String RES_SENDING_en    = "Sending Data...";
	public static final String RES_VALID_COMPANYCD_en = "로그인 정보가 잘못되었습니다\n계정 정보를 확인하세요.";
	public static final String RES_LOGOUT_en     = "로그아웃되었습니다.\n서비스를 종료하겠습니다.";
	public static final String RES_DLG_CLOSE_en  = "서비스를 종료하시겠습니까?";
	public static final String _E001_en= "로그인 정보가 없습니다.\n로그인 해주세요.";
	public static final String _E002_en= "보안을 위해 자동으로 로그아웃되었습니다.\n다시 로그인하십시오.";
	public static final String _E003_en= "두 비밀번호가 일치하지 않습니다.";
	public static final String _E004_en= "로그인 정보가 입력 되지 않았습니다.";
	public static final String _E005_en= "비밀번호가 입력 되지 않았습니다.";
	public static final String _E006_en= "인증 어플리케이션이 미설치 되었습니다.";
	public static final String _E007_en= "로그인 정보가 없습니다.\n로그인 해주세요.";
	public static final String _E008_en= "관계사 로그인 정보가 없습니다.\n로그인 해 주세요.";
	public static final String _E011_en= "기능 처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E021_en= "네트웍 접속이 원활하지 않습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E022_en= "네트웍 접속이 원활하지 않습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E023_en= "서버 접속이 원활하지 않습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E031_en= "해외 로밍 데이터 요금이 부과됩니다. 이용하시겠습니까?";
	public static final String _E032_en= "USIM 정보를 확인할 수 없어 서비스를 종료합니다.";
	public static final String _E033_en= "서버 접속이 원활하지 않습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _E034_en= "_@@ 어플리케이션이 미설치 되었습니다.\n확인을 눌러 해당 어플리케이션을 설치하시기 바랍니다.";
	public static final String _E034_WEB_en= "_@@ 어플리케이션이 미설치 되었습니다.\n모바일 사이트에 로그인 후 다운로드하십시오.";
	public static final String _E041_en= "한글, 영문, 숫자만 입력이 가능합니다.";
	public static final String _E051_en= "모바일에서 지원하지 않은 파일 포맷입니다.";
	public static final String _E071_en= "무선접속 어플이 설치되지 않았습니다.";
	public static final String _E072_en= "업무망에 접속되지 않았습니다. 무선접속 어플을 구동하여 주십시오.";
	public static final String _E073_en= "네트워크 체크 오류. 잠시 후 다시 사용해 주십시오.";
	
	public static final String _E081_en= "SSM 앱을 설치 또는 실행 후 사용 하세요.";  //ghchoi
	public static final String _E082_en= "인증되지 않은 단말입니다. \n단말인증 후 사용하세요";  //ghchoi
	public static final String _E083_en= "V3 앱을 설치 후 사용하십시오. (hi-moffice installer 실행해 설치)";  //ghchoi
	
	public static final String _E084_en= "루팅된 단말은 접속하실 수 없습니다.";  //ghchoi
	
	public static final String _3000_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3001_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3002_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3003_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3004_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3201_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3202_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3203_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3204_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _3205_en= "_@@ 어플리케이션이 업그레이드 되었습니다.\n확인을 누르면 인스톨러에서 업그레이드 하실 수 있습니다.";
	public static final String _3205_A_en= "_@@ 어플리케이션이 업그레이드 되었습니다.\n확인을 누르면 스토어에서 업그레이드 하실 수 있습니다.";
	public static final String _3205_WEB_en= "_@@ 어플리케이션이 업그레이드 되었습니다.\n모바일 사이트에서 업그레이드 하시기 바랍니다.\n";
	public static final String _3206_en= "다운로드 정보가 등록되지 않은 Application입니다.";
	public static final String _3300_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _8002_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";
	public static final String _8003_en= "처리 중 오류가 발생하였습니다.\n잠시 후 다시 이용해 주십시오.";

	
	public static final String SSM_MANUAL_URL = "http://128.200.121.68:9000/emp_ex/ssm_manual/index.jsp";
	public static final String SSM_APK_URL = "http://128.200.121.68:9000/emp_ex/ssm_manual/SSM_installer.apk";
	public static final String SSM_URL = "https://mdm.ex.co.kr:52444/inhouse";
	
	public static final String SSM_UNINSTALLED = "SSM 앱을 설치 후 사용해 주십시오.\n확인버튼을 누르면 SSM 설치마법사 다운로드 페이지로 이동합니다.\nSSM 설치마법사를 통해 SSM 앱을 설치해주십시오.";
	public static final String V3_UNINSTALLED = "V3앱을 설치 후 사용해 주십시오.\n사내Wi-Fi 또는 업무망에 접속 후 hi-moffice installer를 실행해 설치해주십시오.";
	public static final String SSM_OLD_VERSION = "SSM 앱 최신버전이 있습니다.\n업데이트 후 사용해 주십시오.";
	public static final String SSM_UNREGISTERED = "SSM 인증 후 사용해 주십시오.\nSSM 인증은 업무망 접속 종료 후  먼저 hi-단말인증 후 SSM 앱을 실행, 인증해주십시오.";
	public static final String V3_UNREGISTERED = "V3앱을 실행, 인증 후 사용해 주십시오.\n업무망 접속 종료 후 V3앱을 실행, 업데이트해주십시오";
	public static final String SSM_DEFAULT = "SSM 앱 설치 또는 인증 후 사용해주십시오.";
	public static final String V3_DEFAULT = "V3 앱 설치 또는 인증 후 사용해주십시오.";
	public static final String IS_ROOTED = "루팅된 단말은 접속할 수 없습니다.";
	public static final String SSM_ERR_CONNECTION = "잠시 후 다시 시도해주시기 바랍니다.(SSM 바인딩 실패)";
	public static final String CHECK_SSM = "SSM 인증 확인 중 입니다.";
	public static final String CHECK_V3 = "V3 인증 확인 중입니다.";
	public static final String CONNECT_VPN = "VPN 업무망 접속중 입니다.";
	
	
	public static final String check1001 = "SSM 앱을 설치 후 사용해 주십시오.";
	public static final String check1002 = "V3앱을 설치 후 사용해 주십시오.";
	public static final String check1003 = "SSM 앱 최신버전이 있습니다.\n업데이트 후 사용해 주십시오.";
	public static final String check2001 = "SSM 인증 후 사용해 주십시오.\nSSM 인증은 업무망 접속 종료 후 SSM 앱을 실행하면 됩니다.";
	public static final String check2002 = "V3앱을 실행, 인증 후 사용해 주십시오.\n업무망 접속 종료 후 실행해야합니다.";
	public static final String check3001 = "SSM 앱 설치 또는 인증 후 사용해 주십시오.";
	public static final String check3002 = "V3 앱 설치 또는 인증 후 사용해 주십시오.";
	public static final String check4001 = "루팅된 단말은 접속할 수 없습니다.";

	public static String getString(Context context, String id) {
		String ret = "NULL";
		try {
			String lang = context.getResources().getConfiguration().locale.getLanguage();
			if(!"ko".equals(lang)) {
				lang = "en";
			}
			Field f = Resource.class.getField(id + "_" + lang);
			ret = (String)f.get(Resource.class);
		} catch(NoSuchFieldException e) {
		} catch(SecurityException e) {
		} catch(IllegalAccessException e) {
		}

		return ret;
	}
	
	public static String getCheckMessage(Context context, int id){
		String message = "";
		try {
			Field f = Resource.class.getField("check"+id);
			Log.i("Resource......................", "check"+id);
			message = (String)f.get(Resource.class);
		} catch (Exception e) {
		}
		return message;
	}
	
}
