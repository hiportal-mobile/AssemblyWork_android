package com.skt.pe.common.vpn;

import com.sgvpn.vpn_service.api.MobileApi;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class StartRunnable implements Runnable{
	
	String TAG = "StartRunnable";

    String url;
    String id;
    String pw;
    IBinder tempService;

    public StartRunnable(IBinder tempservice, String url, String id, String pw) {
        Log.i(TAG, "StartRunnable======= url : "+url+"\tid : "+id+"\tpw : "+pw);
        tempService = tempservice;
        this.url = url;
        this.id = id;
        this.pw = pw;
    }

    @Override
    public void run() {
        Log.i(TAG, "tempService : "+tempService);

        if(tempService != null){

            MobileApi objAidl = MobileApi.Stub.asInterface(tempService);
            try{
                objAidl.startVPN(url, id, pw);
            }catch (RemoteException e){
                Log.e(TAG, "startrunnable exception ");
                e.printStackTrace();
            }
        }
    }
	
}
