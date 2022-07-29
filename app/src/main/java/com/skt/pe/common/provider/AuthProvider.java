package com.skt.pe.common.provider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.data.Auth;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.AuthJob;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.AuthService;
import com.skt.pe.util.Base64Util;
import com.skt.pe.util.EncryptUtil;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;


//*//**
// * 인증 프로바이더
// *
// * @author  : june
// * @date    : $Date$
// * @id      : $Id$
// *//*
public class AuthProvider extends ContentProvider {
	String TAG = "AuthProvider";
	public static final Uri CONTENT_URI = Constants.Auth.CONTENT_URI;

	private static Auth   auth = null;
	private static String mac  = null;
	private static Object lock = new Object();

	private static Context context;
	
	static {
		auth = new Auth("", "");
	}

	@Override
	public boolean onCreate() {
		SharedPreferences pref = getContext().getSharedPreferences("UPDATE_GMP", Context.MODE_PRIVATE);			
		
		if(!("").equals(pref.getString("secretKey", ""))){
			Intent intent = new Intent(getContext(), AuthService.class);
			
			getContext().startService(intent);

			context = getContext(); 
			
			Intent alarmIntent = new Intent(Constants.Action.AUTH_SERVICE);
			PendingIntent pIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);
			AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC, System.currentTimeMillis()+1000, pIntent);

			SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER]", "OnCreate : AuthService.auth=[" + AuthService.auth + "]");
			try {
				mac = SKTUtil.getMdn(getContext());
				Log.i(TAG, "mdn ============"+mac);
				String[] list = context.fileList();
				if(list != null) {
					for(String file : list) {
						context.deleteFile(file);
					}
				}
			} catch(Exception e) { }

			if(AuthService.auth != null) {
				auth = AuthService.auth;
			}


			if(auth.getSecretKey() == null || auth.getSecretKey().length() <= 0){
				Log.d(TAG,"onCreate prefInfo[secretKey]: "+pref.getString("secretKey", ""));
				Log.d(TAG,"onCreate prefInfo[nonce]: "+pref.getString("nonce", ""));
				Log.d(TAG,"onCreate prefInfo[id]:"+pref.getString("id", ""));
				Log.d(TAG,"onCreate prefInfo[pwd]:"+pref.getString("pwd", ""));
				Log.d(TAG,"onCreate prefInfo[timestamp]:"+pref.getString("timestamp", ""));
				Log.d(TAG,"onCreate prefInfo[auth]:"+auth.toString());
				Log.d(TAG,"onCreate prefInfo[secretkey]: "+pref.getString("secretKey", ""));

				auth.setSecretKey(pref.getString("secretKey", ""));
				auth.setNonce(pref.getString("nonce", ""));
				auth.setId(pref.getString("id", ""));
				auth.setPwd(pref.getString("pwd", ""));
				auth.setTimestamp(pref.getString("timestamp", ""));
				auth.setCheckedCompanyCd("EX");


				try{
						String pwd = auth.getPwd();
						byte[] buf = EncryptUtil.generateAesEncode(pwd.getBytes(), auth.getSecretKey());
						pwd = Base64Util.encodeBinary(buf);

						AuthJob aj = new AuthJob("EX","한국도로공사",pwd);
						auth.setBasicJob(aj);

				}catch(Exception e){
					e.printStackTrace();
					Log.e("AuthProvider", "AuthProvider Error...");
				}



				Log.d("AuthProvider onCreate prefInfo[auth set]:",auth.toString());
			}
		}
		else{
//			Intent intent = new Intent(getContext(), LoginActivity2.class);
//			getContext().startActivity(intent);
		}



		return false;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

//	*//**
//	 * 데이타 조회하기
//	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
//	 *//*
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.i(TAG, "insert ------------------authProvider insert(param, param)");
		List<String> reqValue = uri.getPathSegments();

		if(reqValue.size() > 0) {
			String serviceType = reqValue.get(0);

			SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY]", "serviceType=[" + serviceType + "]");

			String result  = AuthData.RET_SUCCESS;
			Log.i(TAG, "service type==========........."+serviceType);
			SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY]", "insert : AuthService.auth=[" + AuthService.auth + "]");

			try {
//				try {
//					auth.checkAuth();
//				} catch(SKTException e) {
//					result = e.getErrCode();
//					throw new SKTException("", result);
//				}

				//CJ 수정사항
				//mdn 다시 복귀
				String mdn   = values.getAsString(AuthData.ID_MDN);
//				String mdn   = auth.getId();

				// authKey 얻기
				if(serviceType.equals(AuthData.ID_AUTH_KEY)) {
					String appId = values.getAsString(AuthData.ID_APP_ID);

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "secretKey=[" + auth.getSecretKey() + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "mdn=[" + mdn + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "appId=[" + appId + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "nonce=[" + auth.getNonce() + "]");

					String authKey = "";
					try {
						authKey = auth.getAuthKey(mdn, appId);
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					}

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]", "authKey=[" + authKey + "] /" + result);
					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + URLEncoder.encode(authKey));
				// secretKey 얻기
				} else if(serviceType.equals(AuthData.ID_SECRET_KEY)) {
					String secretkey = "";
					try {
						auth.checkAuth();
						secretkey = auth.getSecretKey();
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					}

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType + ")]", "secretKey=[" + secretkey + "] /" + result);
					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + URLEncoder.encode(secretkey));
				// nonce 얻기
				} else if(serviceType.equals(AuthData.ID_NONCE)) {
					String nonce = "";
					try {
						auth.checkAuth();
						nonce = auth.getNonce();
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					}

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType + ")]", "nonce=[" + nonce + "] /" + result);
					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + URLEncoder.encode(nonce));
				// authKey, companyCd 얻기
				} else if(serviceType.equals(AuthData.ID_GMP_AUTH)) {
					String appId = values.getAsString(AuthData.ID_APP_ID);

					Log.d(TAG, "authprovider service type is gmp auth!!!!!!!!!!!!!!!!!!!!!!");
					Log.i(TAG, "authprovider service type is gmp auth secret key=====> "+auth.getSecretKey());
					Log.i(TAG, "authprovider service type is gmp auth secret key=====> "+auth.getNonce());


					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "secretKey=[" + auth.getSecretKey() + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "mdn=[" + mdn + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "appId=[" + appId + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "nonce=[" + auth.getNonce() + "]");

					String authKey = "";
					String checkedCompanyCd = "";
					try {
						authKey = auth.getAuthKey(mdn, appId);
						checkedCompanyCd = auth.getCheckedCompanyCd();
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					}

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]", "authKey=[" + authKey + "] | companyCd=[" + checkedCompanyCd + "] / " + result);
					//CJ 변경
					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + URLEncoder.encode(authKey) + "/" + URLEncoder.encode(checkedCompanyCd) + "/" + URLEncoder.encode(auth.getId()));
				// authKey, companyCd, encPwd 얻기
				} else if(serviceType.equals(AuthData.ID_GMP_AUTH_PWD)) {
					String appId = values.getAsString(AuthData.ID_APP_ID);

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "secretKey=[" + auth.getSecretKey() + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "mdn=[" + mdn + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "appId=[" + appId + "]");
					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]" , "nonce=[" + auth.getNonce() + "]");

					String authKey = "";
					String checkedCompanyCd = "";
					String encPwd  = "";
					try {
						authKey = auth.getAuthKey(mdn, appId);
						checkedCompanyCd = auth.getCheckedCompanyCd();
						encPwd  = auth.getEncPwd();
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					}

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]", "authKey=[" + authKey + "] | companyCd=[" + checkedCompanyCd + "] | encPwd=[" + encPwd + "] / " + result);
					//CJ 변경
					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + URLEncoder.encode(authKey) + "/" + URLEncoder.encode(checkedCompanyCd) + "/" + URLEncoder.encode(encPwd) + "/" + URLEncoder.encode(auth.getId()));
				// checkedCompanyCd 얻기
				} else if(serviceType.equals(AuthData.ID_CHECKED_COMPANY_CD)) {
					String checkedCompanyCd = "";
					try {
						auth.checkAuth();
						checkedCompanyCd = auth.getCheckedCompanyCd();
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					}

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]", "checkedCompanyCd=[" + checkedCompanyCd + "] / " + result);
					//CJ 변경
					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + URLEncoder.encode(checkedCompanyCd) + "/" + URLEncoder.encode(auth.getId()));
				// companyList 얻기(basicJob, addJob)
				} else if(serviceType.equals(AuthData.ID_COMPANY_LIST)) {
					try {
						auth.checkAuth();
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					}

					StringBuffer sb = new StringBuffer();
					if(AuthData.RET_SUCCESS.equals(result)) {
						sb.append(auth.getBasicJob().getCompanyCd() + "|" + auth.getBasicJob().getCompanyNm());
						Iterator<String> it = auth.getAddJobMap().keySet().iterator();
						while(it.hasNext()) {
							String companyCd = it.next();
							AuthJob job = auth.getAddJobMap().get(companyCd);
							sb.append("|" + companyCd + "|" + job.getCompanyNm());
						}
					}

					SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-QUERY(" + serviceType +")]", "companyList=[" + sb.toString() + "] / " + result);
					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + URLEncoder.encode(sb.toString()));
				//CJ 추가
				} else if(serviceType.equals(AuthData.ID_ID)) {
					String pwd = "";
					try {
						auth.checkAuth();
						String secretkey = auth.getSecretKey();
						pwd = auth.getPwd();
						byte[] buf = EncryptUtil.generateAesEncode(pwd.getBytes(), secretkey);
						pwd = Base64Util.encodeBinary(buf);
					} catch(SKTException e) {
						result = e.getErrCode();
						throw new SKTException("", result);
					} catch (Exception e) {
						throw new SKTException("", "E999");
					}

					return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result + "/" + (mac==null || mac.trim().length()==0 ? "NONE" : mac) + "/" + URLEncoder.encode(auth.getId()) + "/" + URLEncoder.encode(pwd) + "/" + URLEncoder.encode(auth.getTimestamp()));
				}
			} catch(SKTException e) {
				result = e.getErrCode();
				return Uri.parse(Constants.Auth.CONTENT_URI_STRING + "/" + serviceType + "/" + result);
			}
		}

		return Constants.Auth.CONTENT_URI;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

//	*//**
//	 * 데이타를 업데이트한다(비밀키 || NONCE || CHECKED_COMPANYCD)
//	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
//	 *//*
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// 로그아웃 추후 추가된 사항이라 기존 인증어플의 경우 무시된다.
		if(values.size() == 0) {
			auth.setSecretKey("");
			auth.setNonce("");
			auth.getBasicJob().setCompanyCd("");
			auth.getBasicJob().setCompanyNm("");
			auth.getBasicJob().setEncPwd("");
			SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-LOGOUT]", "LOGOUT");


			getContext().sendBroadcast(new Intent(Constants.Action.LOGOUT_TICK));
			return 1;
		}

		// secretKey 올리기
		if(values.containsKey(AuthData.ID_SECRET_KEY)) {
			String secretKey = values.getAsString(AuthData.ID_SECRET_KEY);
			if(secretKey!=null && secretKey.trim().length()>0) {
				synchronized(lock) {
					auth.setSecretKey(values.getAsString(AuthData.ID_SECRET_KEY));
				}
				SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-UPDATE(" + AuthData.ID_SECRET_KEY + ")]", "[" + auth.getSecretKey() + "]");
			} else {
				SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-UPDATE-SKIP(" + AuthData.ID_SECRET_KEY + ")]", "[" + auth.getSecretKey() + "]");
			}
		}

		// nonce 올리기
		if(values.containsKey(AuthData.ID_NONCE)) {
			String nonce = values.getAsString(AuthData.ID_NONCE);
			if(nonce!=null && nonce.trim().length()>0) {
				synchronized(lock) {
					auth.setNonce(values.getAsString(AuthData.ID_NONCE));
				}
				SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-UPDATE(" + AuthData.ID_NONCE + ")]", "[" + auth.getNonce() + "]");
			} else {
				SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-UPDATE-SKIP(" + AuthData.ID_NONCE + ")]", "[" + auth.getNonce() + "]");
			}
		}

		// checkedCompnayCd 올리기
		if(values.containsKey(AuthData.ID_CHECKED_COMPANY_CD)) {
			synchronized(lock) {
				auth.setCheckedCompanyCd(values.getAsString(AuthData.ID_CHECKED_COMPANY_CD));
			}
			SKTUtil.log(Log.DEBUG, "[AUTH-PROVIDER-UPDATE(" + AuthData.ID_CHECKED_COMPANY_CD + ")]", "[" + auth.getCheckedCompanyCd() + "]");
		}

		return 1;
	}

	public static Auth getAuth() {
		return auth;
	}

	public static void updateGMP(Auth nowAuth) {
		synchronized(lock) {
			auth = nowAuth;
		}
	}
	
//	*//**
//	 * GMP 로그인 정보 일괄 쓰기
//	 * @param secretKey secretKey
//	 * @param nonce nonce
//	 * @param basicJob 주계정
//	 * @param addJob 부계정
//	 *//*
	public static void updateGMP(String secretKey, String nonce, AuthJob basicJob, AuthJob[] addJob) {
		synchronized(lock) {
			auth.setSecretKey(secretKey);
			auth.setNonce(nonce);
			if(basicJob != null) {
				auth.setBasicJob(basicJob);
				auth.setAddJob(addJob);
			}
		}
	}

	//CJ 추가
	public static void updateGMP(String secretKey, String nonce, AuthJob basicJob, AuthJob[] addJob, String id, String pwd, String timestamp) {
		Log.i("AuthProvider", "--------------------------------updateGMP--------------------------------");
		Log.i("AuthProvider", "secretKey==="+secretKey);
		Log.i("AuthProvider", "nonce==="+nonce);
		Log.i("AuthProvider", "id==="+id);
		Log.i("AuthProvider", "pwd==="+pwd);
		SharedPreferences pref = context.getSharedPreferences("UPDATE_GMP", Context.MODE_PRIVATE);
		Editor ed = pref.edit();
        
		ed.putString("secretKey", secretKey);
		ed.putString("nonce", nonce);
		ed.putString("id", id);
		ed.putString("pwd", pwd);
		ed.putString("timestamp", timestamp);
		ed.commit();		

		updateGMP(secretKey, nonce, basicJob, addJob);
		
		synchronized(lock) {
			auth.setId(id);
			auth.setPwd(pwd);
			auth.setTimestamp(timestamp);
		}
	}
	public static void updateGMP(Context parentActivity, String secretKey, String nonce, AuthJob basicJob, AuthJob[] addJob, String id, String pwd, String timestamp) {
		Log.i("AuthProvider", "=================================updateGMP============================");
		Log.i("AuthProvider", "secretKey==="+secretKey);
		Log.i("AuthProvider", "nonce==="+nonce);
		Log.i("AuthProvider", "id==="+id);
		Log.i("AuthProvider", "pwd==="+pwd);
		
		SharedPreferences pref = parentActivity.getSharedPreferences("UPDATE_GMP", Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		
		ed.putString("secretKey", secretKey);
		ed.putString("nonce", nonce);
		ed.putString("id", id);
		ed.putString("pwd", pwd);
		ed.putString("timestamp", timestamp);
		ed.commit();		
		
		updateGMP(secretKey, nonce, basicJob, addJob);
//		Auth auth = new Auth(id, pwd, secretKey, nonce);
//		auth.getInstance();
		
		synchronized(lock) {
			auth.setId(id);
			auth.setPwd(pwd);
			auth.setTimestamp(timestamp);
		}
	}

	//**
//	  GMP Legacy 로그인 정보 일괄 쓰기
//	 * @param companyCd companyCd
//	 * @param encPwd encPwd
	public static void updateLegacy(String companyCd, String encPwd) {
		synchronized(lock) {
			auth.setAddJob(new AuthJob(companyCd, null, encPwd));
		}
	}

	public static void changeCompany(String companyCd) {
		synchronized(lock) {
			auth.setCheckedCompanyCd(companyCd);
		}
	}

}
