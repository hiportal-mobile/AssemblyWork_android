package com.skt.pe.common.activity;

import java.util.Iterator;
import java.util.Map;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.conf.Resource;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.ExProgress;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Controller;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.common.vpn.SGVPNConnection;
import com.skt.pe.util.StringUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.LinearLayout;


/**
 * 베이스 Activity
 * checkCore
 *
 * @author : june
 */
public abstract class SKTActivity extends Activity {
    private static final String TAG = "SKTActivity";

    int DISCONNECT_ALARM_MINUTE = 10;
    final String PRIMITIVE_LAUNCHER = "COMMON_APP_LAUNCHERHYBRID";
    final String PRIMITIVE_MAIL_COUNT = "COMMON_MAILTEST_PRIVATEBOX";
    final String PRIMITIVE_APPROVAL_COUNT = "COMMON_APPROVALNEW_COUNT";
    final String PRIMITIVE_CONN_USER_INFO = "COMMON_MAILNEW_GETADDRESS";


    Activity mine;
    String errorMessage = "";

    //private static boolean _isGroupApp = true;
    /*
     * 속도를 위해 groupCompanyCd 검증은 생략한다.
     * 무슨 문제가 있을 지 면밀히 검토가 필요하다.
     * by pluto248 2011-11-01
     */
    private String _groupCompanyCd = "";
    private SKTActivity parentActivity = this;
    private String appId;
    protected LocalActivityManager mLocalActivityManager;

    SharedPreferences pref;
    Editor ed;

    private int layoutResId = -1;
    private String[] b_arquments = null;
    private Parameters b_parameters = null;
    private boolean b_postFlag = false;
    private String b_serviceFile = null;
    private SKTException exception = null;
    private int userBlock = -1;
    private int block = -1;

    private float version = 0;
    //CJ 추가
    protected String myId = "";

    // EX 추가
    private static boolean b_isCheckCore = true;

    private boolean mIsMCuvic = false;
    private String mId = null;
    private String mEncPwd = null;
    private String mLoginMethod = null;

    public static SGVPNConnection vpnConn;

    /**
     * SGVPN
     */
    private String mIp, mUserId, mUserPw;
    private int mPort;
    private String mSessionId;
    private int mAuthMethod;
    private boolean mByforce;

    public static IBinder tempService = null;
    public SgnServiceConnection mConnection = new SgnServiceConnection();

    public class SgnServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "===================onServiceConnected===================");
            tempService = service;
            vpnConn = SGVPNConnection.getInstance(tempService);
//			try{
//				PermissionCheck(PERMISSION_CHECK);
//			}catch (RemoteException e){
//				Log.i(TAG, "onServiceConnected permissionCheck exception "+e);
//				e.printStackTrace();
//			}
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "===================onServiceDisconnected===================");
            tempService = null;
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
//		unregisterReceiver(mVpnReceiver);
    }

    protected static void setIsCheckCore(boolean isCheckCore) {
        b_isCheckCore = isCheckCore;
    }

    /**
     * 회사코드 설정한다.
     *
     * @param groupCompanyCd 회사코드
     */
    protected void setGroupCompanyCd(String groupCompanyCd) {
        //_isGroupApp = false;
        _groupCompanyCd = groupCompanyCd;
    }

    /**
     * 서비스 파일 저장
     *
     * @param b_serviceFile
     */
    public void setServiceFile(String b_serviceFile) {
        this.b_serviceFile = b_serviceFile;
    }

    /**
     * 회사코드를 얻는다.
     *
     * @return 회사코드
     */
    protected String getGroupCompanyCd() {
        return _groupCompanyCd;
    }

    /**
     * 저장된 파라미터를 얻는다.
     *
     * @return
     */
    public Parameters getParameters() {
        return b_parameters;
    }

    /**
     * 파라미터를 저장한다.
     *
     * @param params
     */
    public void setParameters(Parameters params) {
        this.b_parameters = params;
    }

    /**
     * GET/POST 여부 얻기
     *
     * @return
     */
    public boolean getPostFlag() {
        return b_postFlag;
    }

    /**
     * GET/POST 저장
     *
     * @param postFlag
     */
    public void setPostFlag(boolean postFlag) {
        this.b_postFlag = postFlag;
    }


    /**
     * 액션을 얻는다.
     *
     * @param primitive 액션명
     * @return 액션
     */
    public Action newAction(String primitive) {
        return new Action(primitive);
    }

    /**
     * 리소스 식별자를 리턴한다.
     *
     * @param name 리소스 아이디
     * @param type 리소스 타입
     * @return 리소스 식별자
     */
    protected int getResId(String name, String type) {
        return getResources().getIdentifier(name, type, getPackageName());
    }

    /**
     * 종료 다이얼로그를 보여준다.
     */
    protected void showCloseDlg() {
        String title = Resource.getString(this, Resource.RES_CLOSE_ID);
        String content = Resource.getString(this, Resource.RES_DLG_CLOSE_ID);
        SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_2);
        dlg.getDialog(title, content, new DialogButton(0) {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        }).show();
    }

    /**
     * 다이얼로그 사이즈를 변경한다.
     *
     * @param dlg             다이얼로그
     * @param portraitHeight  세로일때 높이
     * @param landscapeHeight 가로일때 높이
     */
    protected void resizeDialog(Dialog dlg, int portraitHeight, int landscapeHeight) {
        int id = getResId("ROOT_VIEW", Constants.TYPE_ID);
        LinearLayout v = null;
        if (dlg != null) {
            v = (LinearLayout) dlg.findViewById(id);
        } else {
            v = (LinearLayout) findViewById(id);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            v.getLayoutParams().height = landscapeHeight;
        } else {
            v.getLayoutParams().height = portraitHeight;
        }
    }

    /**
     * 다이얼로그 사이즈를 변경한다.
     *
     * @param dlg             다이얼로그
     * @param configuration   configuration
     * @param portraitHeight  세로일때 높이
     * @param landscapeHeight 가로일때 높이
     */
    protected void resizeDialog(Dialog dlg, Configuration configuration, int portraitHeight, int landscapeHeight) {
        int id = getResId("ROOT_VIEW", Constants.TYPE_ID);
        LinearLayout v = null;
        if (dlg != null) {
            v = (LinearLayout) dlg.findViewById(id);
        } else {
            v = (LinearLayout) findViewById(id);
        }
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            v.getLayoutParams().height = landscapeHeight;
        } else {
            v.getLayoutParams().height = portraitHeight;
        }
    }
	
	
	
	/*@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.i(TAG, "----onBackPressed----");
		//SGVPN 필수코드
		//VPN 연결 되어 있지 않은 경우 앱 뒤로 가기 
		super.onBackPressed();
	}*/

    @Override
    protected final void onCreate(Bundle savedInstanceState) {

        Log.d("SKTActivity", "action onCreate");
        vpnConn = SGVPNConnection.getInstance();
//		SKTUtil.log("TEST", "Pluto248->1: " + System.currentTimeMillis() / 1000);
        mine = SKTActivity.this;
        super.onCreate(savedInstanceState);

//		SKTActivity.initialize();

        //version = Float.parseFloat(Build.VERSION.RELEASE.substring(0, 3));

        Log.d("SKTActivity", "LOGIN_METHOD :" + getIntent().getStringExtra("LOGIN_METHOD"));
		
		/*if(getIntent().getStringExtra("LOGIN_METHOD") != null && getIntent().getStringExtra("LOGIN_METHOD").equals("MCUVIC")){
			mIsMCuvic = true;
			
			mId = getIntent().getStringExtra("ID");
			mEncPwd = getIntent().getStringExtra("PWD");
			mLoginMethod = getIntent().getStringExtra("LOGIN_METHOD");			
		}*/

//		Log.d("SKTActivity", "ID : "+ mId);
//		Log.d("SKTActivity", "PW : "+ mEncPwd);
//		Log.d("SKTActivity", "LOGIN_METHOD : "+ mLoginMethod);


        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onCreate]", Build.MODEL);
        SKTUtil.log("TEST", "onCreate enter");
        appId = SKTUtil.getAppId(this);

        layoutResId = assignLayout();


        if (EnvironManager.PRODUCT_MODE && mLocalActivityManager == null) {

            try {
                Log.i("SKTActivity", "EnvironManager.PRODUCT_MODE");
                //CJ 수정사항
                Map<String, String> map = SKTUtil.getCheckedCompanyCd_DiffId(this);
                String checkedCompanyCd = map.get(AuthData.ID_CHECKED_COMPANY_CD);
                myId = map.get(AuthData.ID_ID);

//				Log.i("SKTActivity", "checkedCompanyCd============"+checkedCompanyCd);
//				Log.i("SKTActivity", "_groupCompanyCd=========="+_groupCompanyCd);
//				Log.i("SKTActivity", "myId ============"+myId);

                if (!StringUtil.isNull(_groupCompanyCd)) {
                    if (!_groupCompanyCd.equals(checkedCompanyCd)) {

                        SKTDialog dlg = new SKTDialog(this);
                        dlg.getDialog(Resource.getString(this, Resource.RES_POSITIVE_ID), Resource.getString(this, Resource.RES_VALID_COMPANYCD_ID), new DialogButton(0) {
                            public void onClick(DialogInterface arg0, int arg1) {
                                SKTUtil.closeApp(SKTActivity.this);
                            }
                        }).show();

                        return;
                    }
                } else {
//					Log.i("", "groupCompanyCd is null");
                    _groupCompanyCd = checkedCompanyCd;
                }
            } catch (SKTException e) {        //hi-moffice 인증에 데이터 값이 없을 경우
                Log.i(TAG, "4444444444444444444444444444444");
                onCommonError(e);
            }
        }

        if (layoutResId > 0) {
            setContentView(layoutResId);
            Log.i(TAG, "setContentView(layoutResId)");

        }

        if (block == -1) {
            Log.i(TAG, "6666666666666666666666666");

            Intent intent = new Intent();
            intent.putExtra("savedInstanceState", savedInstanceState);

            //onResume 시에만 상태 확인
//			if (EnvironManager.SSL_VPN_MODE) {
//				try{
//					Log.i(TAG, "onCreate try66666666666666666666");
//					onCreateW(savedInstanceState);
//				}catch(Exception e){
//					SKTUtil.closeApp(SKTActivity.this);
//				}
//			} else {
            onCreateW(savedInstanceState);
//			}
        }
    }

    @Override
    protected final void onStart() {
        super.onStart();

        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onStart]", userBlock + " / " + block);


        if (userBlock == -1 && block == -1) {

            if (EnvironManager.PRODUCT_MODE && mLocalActivityManager == null) {
                try {
                    Log.i(TAG, "888888888888888888888888888");

                    //CJ 수정사항
                    Map<String, String> map = SKTUtil.getCheckedCompanyCd_DiffId(this);
                    String checkedCompanyCd = map.get(AuthData.ID_CHECKED_COMPANY_CD);

                    if (!StringUtil.isNull(_groupCompanyCd)) {
                        if (!_groupCompanyCd.equals(checkedCompanyCd)) {

                            SKTDialog dlg = new SKTDialog(this);
                            dlg.getDialog(Resource.getString(this, Resource.RES_POSITIVE_ID), Resource.getString(this, Resource.RES_VALID_COMPANYCD_ID), new DialogButton(0) {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    SKTUtil.closeApp(SKTActivity.this);
                                }
                            }).show();

                            return;
                        }
                    } else {
                        _groupCompanyCd = checkedCompanyCd;
                    }

                } catch (SKTException e) {

                    if (Constants.Status.E_AUTH_NULL.equals(e.getErrCode())) {
                        SKTDialog dlg = new SKTDialog(this);

                        if (getIntent().getStringExtra("LOGIN_METHOD") != null && getIntent().getStringExtra("LOGIN_METHOD").equals("MCUVIC")) {
                            dlg.getDialog(Resource.getString(this, Resource.RES_POSITIVE_ID), Resource.getString(this, Resource.RES_LOGOUT_ID), new DialogButton(0) {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //								SKTUtil.closeApp(SKTActivity.this);
                                    SKTUtil.runGMPLogin(parentActivity);
                                }
                            }).show();
                        }
                        return;
                    } else {
                        Log.i(TAG, "1000000000000000000000000");
                        onCommonError(e);
                    }
                }
            }
        }

        if (userBlock == -1 && block == -1) {
            //onResume 시에만 상태 확인
//			if (EnvironManager.SSL_VPN_MODE) {
//				try{
//					Log.i(TAG, "111000000000000000000000000000");
//					onStartW();
//				}catch(Exception e){
////					SKTUtil.runMCuvic(SKTActivity.this);
//					SKTUtil.closeApp(SKTActivity.this);
//				}
//			} else {
            onStartW();
//			}
        }
    }

    @Override
    protected final void onResume() {
        super.onResume();
        Log.i(TAG, "============onResume===============");

		/*if(vpnConn == null){
			vpnConn = SGVPNConnection.getInstance();
		}

		Intent intent = new Intent(Constants.SGVPN.API);
        intent.setPackage(Constants.SGVPN.PACKAGE);

        if(!bindService(intent, mConnection, BIND_AUTO_CREATE)){
            Log.i(TAG, "service bind error");
        }else{
            startService(intent);
        }

        IntentFilter intentFilter = new IntentFilter(Constants.SGVPN.STATUS);
        registerReceiver(mVpnReceiver,intentFilter);
*/

        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onResume]", userBlock + " / " + block);
        try {
            onResumeW();
        } catch (Exception e) {
            SKTUtil.closeApp(SKTActivity.this);
        }
    }


    //SGVPN Receiver
    private BroadcastReceiver mVpnReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "==========SGVPNBroadcastReceiver========== getAction ============" + intent.getAction());
            if (intent.getAction().equals(Constants.SGVPN.STATUS)) {
                int service_status = intent.getIntExtra("STATUS", 0);
                if (service_status == Constants.Connection_N_Status.LEVEL_NOLEVEL.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_NOLEVEL");
                    // not user
                } else if (service_status == Constants.Connection_N_Status.LEVEL_CONNECTING.ordinal()) {
                    //alertdialog_show("LEVEL_CONNECTING","VPN 접속 중..");
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_CONNECTING");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_CONNECTED.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_CONNECTED");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_AUTH_FAILED.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_AUTH_FAILED");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_NONETWORK.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_NONETWORK");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_DISCONNECTED_DONE.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_DISCONNECTED_DONE");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_CRITICAL_ITEMS_NOT_FOUNTD.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_CRITICAL_ITEMS_NOT_FOUNTD");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_DUP_LOGIN.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_DUP_LOGIN");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_PW_EXPIRED.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_PW_EXPIRED");
                } else if (service_status == Constants.Connection_N_Status.LEVEL_BLOCK_ACCESS.ordinal()) {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== LEVEL_BLOCK_ACCESS");
                } else {
                    Log.i(TAG, "========SGVPNBroadcastReceiver============== ETC");
                }
                Log.i(TAG, "VPN status on Receive : " + intent.getIntExtra("STATUS", 0) + " \t DETAILSTATUS : " + intent.getStringExtra("DETAILSTATUS"));
            }
        }
    };


    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("테스트", "123123");

        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onActivityResult]", userBlock + " / " + block);

        SKTActivity currentActivity = this;
        if (mLocalActivityManager != null) {
            SKTActivity act = (SKTActivity) mLocalActivityManager.getCurrentActivity();
            if (act != null)
                currentActivity = act;
        }

        if (requestCode == Constants.REQ_AUTH) {

        } else {
            if (requestCode == Constants.REQ_AUTH_ACTIVITY) {
                switch (resultCode) {
                    case Constants.RES_CLOSE:
                        SKTUtil.closeApp(this);
                        break;
                }
            } else if (requestCode == Constants.REQ_IMAGE) {
                Log.e("테스트", "123123");

                switch (resultCode) {
                    case Constants.RES_HISTORY:
                        String errCode = data.getStringExtra("errCode");
                        if (Constants.Status.N_8002.equals(errCode) ||
                                Constants.Status.N_8003.equals(errCode)) {
                            currentActivity.exception = null;

                            currentActivity.OnImageError();
                            return;
                        }
                        break;
                }
            }

            SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onActivityResultX]", requestCode + " / " + resultCode);
            onActivityResultX(requestCode, resultCode, data);
        }
    }


    /**
     * 하위 클래스에서 사용할 onStart 메소드
     */
    protected void onStartX() {
    }

    /**
     * 하위 클래스에서 사용할 onResume 메소드
     */
    protected void onResumeX() {
    }

    protected void onActivityResultX(int requestCode, int resultCode, Intent data) {
        Log.i("테스트", "123123");
    }

    /**
     * 이미지뷰어 에러 콜백 메소드
     */
    public void OnImageError() {
        if (b_parameters != null) {
            new Action(Action.PRIMITIVE_HISTORY).execute();
        } else {
        }
    }

    /**
     * 레이아웃을 지정한다.
     *
     * @return 레이아웃 식별자
     */
    protected abstract int assignLayout();

    /**
     * 하위 클래스에서 사용할 onCreate 메소드
     *
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void onCreateX(Bundle savedInstanceState);

    /**
     * 액션 처리전 UI 셋팅<br>
     * 기본적으로 액션별 pendding 메세지를 지정한다.(UI 스레드 동작)
     *
     * @param primitive 액션명
     * @return pendding 메세지 식별자
     */
    protected abstract int onActionPre(String primitive);

    /**
     * 액션 처리 핸들러<br>
     * 기본적으로 서버 리퀘스트 동작을 기술한다.(UI 스레드가 아닌 별도의 스레드 동작)
     *
     * @param primitive 액션명
     * @param args      파라미터
     * @return XML데이타
     * @throws SKTException
     */
    protected abstract XMLData onAction(String primitive, String... args) throws SKTException;

    /**
     * 액션 처리후 UI 셋팅<br>
     * 기본적으로 서버 리퀘스트의 결과로 화면을 셋팅한다.(UI 스레드 동작)
     *
     * @param primitive 액션명
     * @param result    XML데이타
     * @param e         액션 처리중 에러
     * @throws SKTException
     */
    protected abstract void onActionPost(String primitive, XMLData result, SKTException e) throws SKTException;

    /**
     * 공통 에러 핸들러
     *
     * @param ex 에러
     */
    public void onCommonError(SKTException ex) {
        if (ex != null) {
            Log.i(TAG, "===========onCommonError(SKTException ex)===========");

            Log.d(TAG, "ex.getErrCode() : " + ex.getErrCode());
            Log.d(TAG, "ex.mIsMCuvic() : " + mIsMCuvic);

            parentActivity = this;
            if (parentActivity.getParent() != null) {
                // by pluto248
                // 테스트를 위해 임시로 막아둔 코드...
                // 추후에 삭제할
                if (parentActivity.getParent() instanceof SKTActivity)
                    parentActivity = (SKTActivity) parentActivity.getParent();
            }
            if (Constants.Status.E_AUTH_NOT.equals(ex.getErrCode())) {
                ex.setAlertReset();
                ex.alert(this, new DialogButton(0) {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SKTUtil.closeApp(SKTActivity.this);
                    }
                });
            } else if (Constants.Status.E_AUTH_NULL.equals(ex.getErrCode()) ||
                    Constants.Status.E_AUTH_GMP.equals(ex.getErrCode())) {
                ex.setAlertReset();
//				Log.i("SKTActivity", "--------onCommonError :E001??---------------"+ex.getErrCode());
//				Log.i("SKTActivity", "-------mIsMCuvic??---------------"+mIsMCuvic);

                if (!mIsMCuvic) {

                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.runGMPLogin(parentActivity);
                            //arg0.dismiss();
                        }
                    });

                } else {
                    Log.i(TAG, "========================================");        //ID, PW 입력 있을 때
                    SKTUtil.runGMPLoginWithEncPwd(parentActivity, mId, mEncPwd, mLoginMethod);
                }
            } else if (Constants.Status.E_AUTH_LEGACY.equals(ex.getErrCode())) {
                ex.setAlertReset();
                ex.alert(this, new DialogButton(0) {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            String companyCd = SKTUtil.getCheckedCompanyCd(SKTActivity.this);
                            SKTUtil.runLegacyLogin(parentActivity, companyCd);
                        } catch (SKTException e) {
                            SKTUtil.runGMPLogin(parentActivity);
                        }
                    }
                });
            } else if (Constants.Status.E_AUTH_FAIL.equals(ex.getErrCode())) {
                ex.setAlertReset();

                if (!mIsMCuvic) {

                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            try {
                                String checkCompanyCd = SKTUtil.getCheckedCompanyCd(SKTActivity.this);
                                Map<String, String> companyMap = SKTUtil.getCompanyList(SKTActivity.this);

                                Iterator<String> it = companyMap.keySet().iterator();
                                if (it.hasNext()) {
                                    String companyCd = it.next();
                                    if (companyCd.equals(checkCompanyCd)) {
                                        SKTUtil.runGMPLogin(parentActivity);
                                        return;
                                    }
                                }

                                SKTUtil.runLegacyLogin(parentActivity, checkCompanyCd);
                            } catch (SKTException e) {
                                SKTUtil.runGMPLogin(parentActivity);
                            }
                        }
                    });

                } else {
                    SKTUtil.runGMPLoginWithEncPwd(parentActivity, mId, mEncPwd, mLoginMethod);
                }
            } else if (Constants.Status.N_7001.equals(ex.getErrCode()) || Constants.Status.N_7009.equals(ex.getErrCode())) {
                ex.setAlertReset();

                if (!mIsMCuvic) {
                    if (Constants.Status.N_7001.equals(ex.getErrCode())) {
                        SKTUtil.runGMPLogin(parentActivity);
						/*AlertDialog.Builder dialog = new AlertDialog.Builder(this);
						dialog.setMessage( ex.getEncodedMessage(this)).setPositiveButton(Resource.RES_POSITIVE_ko, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								SKTUtil.runGMPLogin(parentActivity);
							}
						}).create().show();*/
                    } else if (Constants.Status.N_7009.equals(ex.getErrCode())) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setMessage(ex.getEncodedMessage(this)).setPositiveButton(Resource.RES_POSITIVE_ko, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.dismiss();
                                SKTUtil.runGMPLogin(parentActivity);
                            }
                        }).create().show();
                    }

                } else {
                    SKTUtil.runGMPLoginWithEncPwd(parentActivity, mId, mEncPwd, mLoginMethod);
                }
            } else if (Constants.Status.N_7005.equals(ex.getErrCode()) ||
                    Constants.Status.N_7015.equals(ex.getErrCode())) {
                ex.setAlertReset();
                if (!mIsMCuvic) {
                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.runGMPLogin(parentActivity);
                        }
                    });
                } else {
                    SKTUtil.runGMPLoginWithEncPwd(parentActivity, mId, mEncPwd, mLoginMethod);
                }
            } else if (Constants.Status.N_7002.equals(ex.getErrCode()) ||
                    Constants.Status.N_7003.equals(ex.getErrCode())) {
                ex.setAlertReset();
                ex.alert(this, new DialogButton(0) {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SKTUtil.goToffice(SKTActivity.this);
                        SKTUtil.closeApp(SKTActivity.this);
                    }
                });
            } else if (Constants.Status.N_7004.equals(ex.getErrCode()) ||
                    Constants.Status.N_7010.equals(ex.getErrCode()) ||
                    Constants.Status.N_7011.equals(ex.getErrCode()) ||
                    Constants.Status.N_7012.equals(ex.getErrCode()) ||
                    Constants.Status.N_7014.equals(ex.getErrCode()) ||
                    Constants.Status.E_NETWORK.equals(ex.getErrCode())
            ) {
                ex.setAlertReset();
                ex.alert(this, new DialogButton(0) {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SKTUtil.closeApp(SKTActivity.this);
                    }
                });
            } else if (Constants.Status.E_CONN_TIMEOUT.equals(ex.getErrCode()) || Constants.Status.E_NORMAL.equals(ex.getErrCode()) || Constants.Status.E_READ_TIMEOUT.equals(ex.getErrCode())) {
                try {
                    SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
                    dlg.getDialog(Resource.getString(this, Resource.RES_POSITIVE_ID), "VPN 접속 후 사용해주시기 바랍니다.", new DialogButton(0) {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_MAIN);
                            i.addCategory(Intent.CATEGORY_HOME);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//	        				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
//	        				moveTaskToBack(true);
                            System.exit(0);
                        }
                    }).show();

                } catch (Exception e) {
                    SKTUtil.log("Error", "SKDialog show error");
                }



        		/*ex.setAlertReset();
        		Log.e(TAG, "on common error ERR CODE === "+ex.getErrCode());
        		ex.alert(this, new DialogButton(0) {
        			public void onClick(DialogInterface arg0, int arg1) {
        				Intent i = new Intent();
        				i.setAction(Intent.ACTION_MAIN);
        				i.addCategory(Intent.CATEGORY_HOME);
        				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        				i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        				startActivity(i);
        				moveTaskToBack(true);
//        				System.exit(0);
        				android.os.Process.killProcess(android.os.Process.myPid());
						SKTUtil.closeApp(SKTActivity.this);
					}
				});*/
            } else if (Constants.Status.E_ROAMING.equals(ex.getErrCode())) {
                ex.setAlertReset();
                ex.alert(this,
                        new DialogButton(0) {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Controller.ROAMING = true;
                                new Action(Action.PRIMITIVE_HISTORY).execute();
                            }
                        },
                        new DialogButton(0) {
                            public void onClick(DialogInterface arg0, int arg1) {
                                Controller.ROAMING = false;
                                SKTUtil.closeApp(SKTActivity.this);
                            }
                        }
                );
            } else if (Constants.Status.N_3205.equals(ex.getErrCode())) {
                if (Constants.CoreComponent.APP_ID_CLIENT.equals(appId)) {
                    ex.setErrCode(Constants.Status.N_3205_WEB);
                    ex.addParam(SKTUtil.getAppName(this));
                    ex.setAlertReset();
                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.goToffice(SKTActivity.this);
                            SKTUtil.closeApp(SKTActivity.this);
                        }
                    });
                } else if (Constants.CoreComponent.isCore(this)) {
                    ex.setErrCode(Constants.Status.N_3205_A);
                    ex.addParam(SKTUtil.getAppName(this));
                    ex.setAlertReset();
                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.goMobileOffice(SKTActivity.this);
                            SKTUtil.closeApp(SKTActivity.this);
                        }
                    });
                } else {
                    ex.setErrCode(Constants.Status.N_3205_A);
                    ex.addParam(SKTUtil.getAppName(this));
                    ex.setAlertReset();
                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.goMobileOffice(SKTActivity.this, appId);
                            SKTUtil.closeApp(SKTActivity.this);
                        }
                    });
                }
                //CJ 추가
            } else if (Constants.Status.E_DIFF_ID.equals(ex.getErrCode())) {
                SKTUtil.restartApp(SKTActivity.this);
            } else if (Constants.Status.E_DIFF_ID.equals(ex.getErrCode())) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "-------------------onDestroy---------------------");
        if (progressDlg != null && progressDlg.isShowing() == true) {
            progressDlg.dismiss();
        }
        super.onDestroy();
    }

    private void onCreateW(Bundle savedInstanceState) {
        onCreateX(savedInstanceState);
    }

    private void onStartW() {
        onStartX();
    }

    private void onResumeW() {
        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onResumeX]", userBlock + " / " + block);
        onResumeX();
    }

    /**
     * 액션 클래스
     *
     * @author june
     */
    private ProgressDialog progressDlg = null;
    private ExProgress dialog = null;

    public class Action extends AsyncTask<String, Integer, XMLData> {
        public static final String PRIMITIVE_HISTORY = "_HISTORY_";
        public static final int SERVICE_RETRIEVING = 0;
        public static final int SERVICE_SENDING = 1;

        private String serviceMessage = "";
        private String primitive = "";
        private boolean pendding = true;

        private boolean iscancelled = false;

        private SKTException ex = null;

        public Action(String primitive) {
            this(primitive, true);
        }

        public Action(String primitive, boolean pendding) {

            if (vpnConn == null) {
                vpnConn = SGVPNConnection.getInstance();
            }
            int status = vpnConn.getStatus();
            Log.i(TAG, "new Action vpn status=====>>>" + vpnConn.getStatus());
            if (vpnConn != null) {
				/*if(status == Connection_N_Status.LEVEL_DISCONNECTED_DONE.ordinal() ){
					Log.i(TAG, "==============VPN 연결 끊김==============");
					AlertDialog.Builder dialog = new AlertDialog.Builder(mine);
					dialog.setTitle("확인").setMessage("VPN 접속 후 사용해주시기 바랍니다.").setNeutralButton("확인", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							arg0.dismiss();
							finish();
							System.exit(0);
						}
					});
					dialog.show();
				}*/
            }

            if (dialog == null) {
                if (getParent() != null) {
                    Log.i(TAG, "dialog getParent");
                    dialog = new ExProgress(getParent());
                    dialog.setOwnerActivity(getParent());
                } else {
                    Log.i(TAG, "dialog SKTActivity");
                    dialog = new ExProgress(SKTActivity.this);
                    dialog.setOwnerActivity(SKTActivity.this);
                }

            }


            Resources res = SKTActivity.this.getResources();

            String b_primitive = primitive;

            if (PRIMITIVE_HISTORY.equals(primitive)) {
                b_primitive = b_parameters.getPrimitive();
            }
            this.pendding = pendding;

            int resId = onActionPre(b_primitive);

            if (pendding) {
                switch (resId) {
                    case SERVICE_RETRIEVING:
                        this.serviceMessage = Resource.getString(SKTActivity.this, Resource.RES_RETRIEVING_ID);
                        break;
                    case SERVICE_SENDING:
                        this.serviceMessage = Resource.getString(SKTActivity.this, Resource.RES_SENDING_ID);
                        break;
                    default:
                        this.serviceMessage = res.getString(resId);
                }
            }

            this.primitive = primitive;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (pendding) {

//				dialog.setCancelable(false);
                dialog.setMessage(this.serviceMessage);

//				if(!dialog.isShowing()){
                //					//안 읽은 메일 수, 간이결재 결재 수 등을 조회할 때는 ProgressDialog 창 뜨지 않게
                if ((PRIMITIVE_CONN_USER_INFO).equals(primitive) || (PRIMITIVE_MAIL_COUNT).equals(primitive) || (PRIMITIVE_APPROVAL_COUNT).equals(primitive)) {
                    Log.i(TAG, "primitive======>>>>" + primitive);
                } else {
                    Log.i(TAG, "primitive---------->>>>" + primitive);
                    try {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.show();
                    } catch (Exception e) {
                        Log.e(TAG, "progressDlg.isShowing error");
                        e.printStackTrace();
                    }
                }
//				}
            }
        }


        @Override
        protected XMLData doInBackground(String... arg0) {
            XMLData ret = null;

            try {
                if (!PRIMITIVE_HISTORY.equals(primitive)) {
                    b_arquments = arg0;
                    ret = onAction(primitive, arg0);
                } else {
                    String b_primitive = primitive;
                    if (PRIMITIVE_HISTORY.equals(primitive)) {
                        b_primitive = b_parameters.getPrimitive();
                    }
                    ret = onAction(b_primitive, arg0);
                }
                ex = null;
            } catch (SKTException e) {
                ex = e;
                SKTActivity.this.exception = e;
                Log.d(TAG, "SKTException errorcode " + ex.getErrCode() + "  get SKTException errMessage : " + ex.getMessage());
                //onCommonError(ex);
                e.printStackTrace();
            } catch (Exception e) {
                ex = new SKTException(e);
                //onCommonError(ex);
                Log.d(TAG, "exception errorcode " + ex.getErrCode() + "  get errMessage errMessage : " + ex.getMessage());
                SKTActivity.this.exception = ex;
                e.printStackTrace();
            }
            return ret;
        }

        @Override
        protected void onPostExecute(XMLData result) {
            long t = System.currentTimeMillis();
            Log.i(TAG, "onPostExecute @@@@@@@@@@@@@@@@@@@");
            super.onPostExecute(result);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog.cancel();
            }
            try {
                if (result != null) {
                } else {
                    if (ex != null) {
                        onCommonError(ex);
                    } else {
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "[Comm] : dismiss exception: " + ex.toString());
                ex.printStackTrace();
            }


            String b_primitive = primitive;
            if (PRIMITIVE_HISTORY.equals(primitive)) {
                b_primitive = b_parameters.getPrimitive();
            }

            try {
                onActionPost(b_primitive, result, ex);
            } catch (SKTException e) {
                e.alert(SKTActivity.this);
                e.printStackTrace();
            }

            SKTUtil.timestamp(SKTActivity.this, t, b_primitive, "[ ACT-PST DONE ]");
        }

        @Override
        protected void onCancelled() {
            try {
                if (dialog.isShowing() == true) {
                    dialog.dismiss();
                }

            } catch (IllegalArgumentException ex) {
                SKTUtil.log("DEBUG", "[Comm] : Cancel Dismiss exception: " + ex.toString());
            }
        }

    }
}
