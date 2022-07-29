package com.skt.pe.common.vpn;

// import com.dreamsecurity.util.DEFCrypto;
import com.sgvpn.vpn_service.api.MobileApi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class SGVPNConnection {
	 public static String TAG = "SGVPNConnection";
	 public static final String SGN_PACKAGE = "com.sgvpn.vpn_service";
//	 public static String vpnUrl = "180.148.182.24";
	 public static String vpnUrl = "180.148.182.123";
//	 public static String vpnUrl = "128.200.121.123";
	 
	 
    private static SGVPNConnection instance = new SGVPNConnection();
    public static IBinder tempService = null;
    //public static SgnServiceConnection mConnection;
    
   
    /*public static SgnServiceConnection mConnection = new SgnServiceConnection();
    public static SgnServiceConnection getConnection(){
    	if(tempService == null){
    		mConnection = new SgnServiceConnection();
    	}
    	return mConnection;
    }*/
    
    Context mContext;

    public static SGVPNConnection getInstance(){
    	
        if(instance == null){
            instance = new SGVPNConnection();
            Log.i(TAG, "SGVPNConnection instance is null");
        }
        else{
        	Log.i(TAG, "SGVPNConnection instance is not null");
        }
        return instance;
    }
    
    public static SGVPNConnection getInstance(final IBinder tempservice){
    	Log.i(TAG, "getinstance tempservice  : "+tempservice);
    	if(tempservice != null){
    		tempService = tempservice;	
    	}
    	
        if(instance == null){
            instance = new SGVPNConnection();
            Log.i(TAG, "SGVPNConnection instance is null");
        }
        else{
        	Log.i(TAG, "SGVPNConnection instance is not null");
        }
        return instance;
    }

    /*public void setInit(Context context){

        Intent intent = new Intent("com.sgvpn.vpn_service.api.MobileApi");
        intent.setPackage(SGN_PACKAGE);
        if(!mContext.bindService(intent, mConnection, mContext.BIND_AUTO_CREATE)){
            Log.i(TAG, "service bind error");
        }else{
            mContext.startService(intent);
            Log.i(TAG, "service bind success");
        }
    }*/

    public int getStatus() {
        int status = 0;
        Log.i(TAG, "getStatus");
        if(tempService == null){
//            getInstance();
//        	getConnection();
        }
        if (tempService != null) {
            MobileApi objAidl = MobileApi.Stub.asInterface(tempService);
            try {
                status = objAidl.statusVPN();
                Log.i(TAG, "status : " + status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public static class SgnServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service){
        	if(tempService != null){
        		tempService = service;
                Log.i(TAG, "=======SgnServiceConnection Service Connected=======");	
        	}
            
//	            try{
//	                PermissionCheck(PERMISSION_CHECK);
//	            }catch (RemoteException e){
//	                System.out.println();
//	            }
        }
        public void onServiceDisconnected(ComponentName name){
            Log.i(TAG, "=======SgnServiceConnection Service Disconnected=======");
            tempService = null;
        }
    }
//    public static SgnServiceConnection getConnection(){
//        Log.i(TAG, "getConnection mConnection : "+mConnection);
//        return mConnection;
//    }

    public static IBinder getTempService(){
    	if(tempService == null){
//    		getInstance();
//    		getConnection();
    	}
        return tempService;
    }
    
    //prepareStartProfile
    public Intent getService(){
    	Intent requestpermission = null;
    	
    	if(tempService == null){
//    		getInstance();
//    		getConnection();
    	}
    	
		 MobileApi objAidl = MobileApi.Stub.asInterface(tempService);
		 try{
			 requestpermission = objAidl.prepareVPNService();	 
		 }
		 catch(Exception e){
			 Log.e(TAG, "getService exception : "+e);
			 e.printStackTrace();
		 }
         
         return requestpermission;
    }
    
    //vpn-service 앱 권한 체크
    public Intent getPermissionCheck(){
    	 Intent permissioncheck = null;
    	 if(tempService == null){
//    		 getInstance();
//    		 getConnection();
    	 }
    	 
    	 MobileApi objAidl = MobileApi.Stub.asInterface(tempService);
    	 
    	 try{
    		 permissioncheck = objAidl.preparePermissionCheck();
    	 }
    	 catch(Exception e){
    		Log.e(TAG, "getPermissionCheck exception : "+e); 
    	 }
    	 
    	 return permissioncheck;
    }
    
    public void connection(String id, String pw){
    	try {
            // pw = DEFCrypto.getInstance().getSha256Def(pw);
		} catch (Exception e) {
			e.printStackTrace();
		}
        Log.i(TAG, "connection tempService : "+tempService);
        if(tempService == null){
//        	getInstance();
//        	getConnection();
        }
        StartRunnable start_VPN = new StartRunnable(tempService, vpnUrl, id, pw);
        Thread start = new Thread(start_VPN);
        start.start();
    }

    public void disconnection(){
        StopRunnable stop_VPN = new StopRunnable(tempService);
        Thread stop = new Thread(stop_VPN);
        stop.start();
    }
    
}
