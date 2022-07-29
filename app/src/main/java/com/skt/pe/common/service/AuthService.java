package com.skt.pe.common.service;

import com.skt.pe.common.data.Auth;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.provider.AuthProvider;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

//*//**
// * 인증 서비스
// * @author june
// *
// *//*
public class AuthService extends Service {
	
	public static Auth auth = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		SKTUtil.log(android.util.Log.DEBUG, "[AUTH-SERVICE]", "onCreate : AuthService.auth=[" + auth + "]");

		try {
			auth = AuthProvider.getAuth();
		} catch(Exception e) { }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		SKTUtil.log(android.util.Log.DEBUG, "[AUTH-SERVICE]", "onDestory : AuthService.auth=[" + auth + "]");
		AuthProvider.updateGMP(auth);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
//		if(Const.DEBUG) {
//			SKTUtil.log(android.util.Log.DEBUG, "[AUTH-SERVICE]", "onStart : AuthService.auth=[" + auth + "]");
//		}
	}

}
