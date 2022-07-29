package com.skt.pe.common.vpn;

import com.sgvpn.vpn_service.api.MobileApi;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class StopRunnable implements Runnable {

	String TAG = "StopRunnable";
	IBinder tempService;
	
	public StopRunnable(IBinder tempService) {
	    this.tempService = tempService;
	}
	
	@Override
	public void run() {
	    if(tempService != null){
	        MobileApi objAidl = MobileApi.Stub.asInterface(tempService);
	        try{
	            objAidl.stopVPN();
	        }catch (RemoteException e){
	            Log.e(TAG, "StopRuunable Exception : "+e);
	        }
	
	    }
	}
}
