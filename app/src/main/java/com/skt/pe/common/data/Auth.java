package com.skt.pe.common.data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.exception.InternalException;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.util.EncryptUtil;
import com.skt.pe.util.StringUtil;

import android.util.Log;


/**
 * 인증 정보 클래스
 * @author june
 *
 */
public class Auth {
	private static final String SEP        = ":";

	public static String secretKey = "";
	public static String nonce     = "";

	public static String checkedCompanyCd = "";
	
	public static AuthJob               basicJob  = null;
	public static Map<String, AuthJob>  addJobMap = new LinkedHashMap<String, AuthJob>(); 

	//CJ 추가
	public static String id  = "";
	public static String pwd = "";
	public static String timestamp = "";

//	public final static Auth instance = new Auth(id, pwd, secretKey, nonce);
	
	public Auth(String secretKey, String nonce) {
		this.secretKey = secretKey;
		this.nonce     = nonce;
	}
	public Auth(String id, String pwd, String secretKey, String nonce) {
		this.id = id;
		this.pwd = pwd;
		this.secretKey = secretKey;
		this.nonce     = nonce;
		Log.i("Auth Construction(param, param, param, param)", "auth id"+id+"   pwd :"+pwd);
	}
	
//	public static Auth getInstance(){
//		
//		return instance;
//	}
	/**
	 * 로그인 체크
	 * @throws SKTException
	 */
	public void checkAuth() throws SKTException {
		
		Log.d("Auth checkAuth()", "getSecretKey() ::::::::::::: "+getSecretKey());


		if(getSecretKey()!=null && !("").equals(getSecretKey())){
			
			Log.d("Auth checkAuth()", "inner getSecretKey");			
			
			setSecretKey(getSecretKey());
			setNonce(getNonce());
			setId(getId());
			setCheckedCompanyCd(getCheckedCompanyCd());
			setPwd(getEncPwd());
		}
		if(getSecretKey()==null || getSecretKey().trim().length()==0 || getNonce()==null || getNonce().trim().length()==0) {
			
			Log.d("Auth checkAuth()", "inner getSecretKey2");
			
			throw new SKTException("Login need!", Constants.Status.E_AUTH_NULL);
		}		
	}

	/**
	 * 인증토큰 얻기
	 * @param mdn mdn
	 * @param appId appId
	 * @return 인증토큰
	 * @throws SKTException
	 */
	public String getAuthKey(String mdn, String appId) throws SKTException {
		
		Log.i("Auth", "===============getAuthKEY================");
		checkAuth();
		
		StringBuffer sb = new StringBuffer();
		sb.append(AuthData.ID_MDN    + "=" + mdn + SEP);
		sb.append(AuthData.ID_APP_ID + "=" + appId    + SEP);
//		sb.append(AuthData.ID_NONCE  + "=" + getNonce().substring(0, 8));		//2015.11.10 수정 mwp.jar의 MoGreeter와 같이 수정
		sb.append(AuthData.ID_NONCE  + "=" + getNonce());		//2015.11.10 수정 mwp.jar의 MoGreeter와 같이 수정

		SKTUtil.log(Log.DEBUG, "Auth-getAuthKey", sb.toString());
		
		String ret = null;
		try {
			
			ret = EncryptUtil.generateHMac(EncryptUtil.HMAC_SHA1, getSecretKey(), sb.toString());			
			/**
			 * 접속 종료 후 재접속 시 secret key가 변경되면서 mwp.jar의 MoGreeter.class에서 authKey와 ComparedSignature 가 달라짐
			 * MoGreeter와 pair로 변경
			 *  현재는 MoGreeter에서 코드 비교하지 않고 nonce가 null만 아니면 bypass 함
			 *  2015.11.10
			 * **/
//			ret = EncryptUtil.generateHMac(EncryptUtil.HMAC_SHA1, "ca4491b125557ffc6693349d3139fa8e" , sb.toString());
		} catch(Exception e) {
			throw new SKTException(e, Constants.Status.E_AUTH_FAIL);
		}
		return ret; 
	}

	/**
	 * 비밀키 얻기
	 * @return
	 */
	public static String getSecretKey() {
		return secretKey;
	}
	/**
	 * 비밀키 설정하기
	 * @param secretKey
	 */
	public void setSecretKey(String secretKey) {
		Log.e("Auth.....", "setSecretKey.........."+secretKey);
		
		this.secretKey = secretKey;
	}
	/**
	 * 논스값 얻기
	 * @return
	 */
	public static String getNonce() {
		
		return nonce;
	}
	/**
	 * 논스값 설정하기
	 * @param nonce
	 */
	public void setNonce(String nonce) {
		Log.e("Auth.....", "setNonce.........."+nonce);
		this.nonce = nonce;
	}
	/**
	 * 현재 사용중인 회사 얻기
	 * @return
	 */
	public static String getCheckedCompanyCd() {
		return checkedCompanyCd;
	}
	/**
	 * 회사 변경하기
	 * @param checkedCompanyCd
	 */
	public void setCheckedCompanyCd(String checkedCompanyCd) {
		this.checkedCompanyCd = checkedCompanyCd;
	}
	
	public static String getId() {
		return id;
	}

	public void setId(String id) {
		Log.e("Auth...........", "setId.........."+id);
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		Log.e("Auth...........", "setPwd.........."+pwd);
		this.pwd = pwd;
	}

	public static String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 주계정 얻기
	 * @return
	 */
	public AuthJob getBasicJob() {
		return basicJob;
	}
	/**
	 * 주계정 설정하기
	 * @param basicJob
	 */
	public void setBasicJob(AuthJob basicJob) {
		this.basicJob = basicJob;
		this.checkedCompanyCd = basicJob.getCompanyCd();
	}
	/**
	 * 부계정맵 얻기
	 * @return
	 */
	public Map<String, AuthJob> getAddJobMap() {
		return addJobMap;
	}
	/**
	 * 부계정맵 설정하기
	 * @param addJobMap
	 */
	public void setAddJobMap(Map<String, AuthJob> addJobMap) {
		this.addJobMap = addJobMap;
	}

	/**
	 * 암호화된 패스워드 얻기
	 * @return
	 * @throws InternalException
	 */
	public static String getEncPwd() throws InternalException {
		if(checkedCompanyCd.equals(basicJob.getCompanyCd())) {
			if(!StringUtil.isNull(basicJob.getEncPwd()))
				return basicJob.getEncPwd();
			else
				throw new InternalException("EncPwd is not found!", Constants.Status.E_AUTH_GMP);		
		}
		if(addJobMap.containsKey(checkedCompanyCd)) {
			if(!StringUtil.isNull(addJobMap.get(checkedCompanyCd).getEncPwd()))
				return addJobMap.get(checkedCompanyCd).getEncPwd();
			else
				throw new InternalException("EncPwd is not found!", Constants.Status.E_AUTH_LEGACY);
		}

		throw new InternalException("EncPwd is not found!", Constants.Status.E_AUTH_GMP);
	}
	
	/**
	 * 부계정 추가하기
	 * @param addJob
	 */
	public void setAddJob(AuthJob addJob) {
		if(addJob == null)
			return;

		String companyCd = addJob.getCompanyCd();
		if(addJobMap.containsKey(companyCd)) {
			AuthJob job = addJobMap.get(companyCd);
			addJob.setCompanyNm(job.getCompanyNm());
			addJobMap.put(companyCd, addJob);
		}
	}

	/**
	 * 부계정 추가하기
	 * @param addJob
	 */
	public void setAddJob(AuthJob[] addJob) {
		if(addJob == null)
			return;

		for(int i=0; i<addJob.length; i++) {
			if(StringUtil.isNull(addJob[i].getEncPwd())) {
				String companyCd = addJob[i].getCompanyCd();
				if(addJobMap.containsKey(companyCd)) {
					addJob[i] = addJobMap.get(companyCd);
				}				
			}
		}

		addJobMap.clear();
		for(int i=0; i<addJob.length; i++) {
			addJobMap.put(addJob[i].getCompanyCd(), addJob[i]);
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("\nsecretKey=" + secretKey);
		sb.append("\nnonce=" + nonce);
		sb.append("\ncheckedCompanyCd=" + checkedCompanyCd);
		sb.append("\nbasicJob=" + basicJob);

		Iterator<String> it = addJobMap.keySet().iterator();
		int i = 0;
		while(it.hasNext()) {
			String companyCd = it.next();
			sb.append("\naddJobMap[" + (i++) + "]=" + addJobMap.get(companyCd));
		}
		
		return sb.toString();
	}
	
}
