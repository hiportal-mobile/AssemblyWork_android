package com.skt.pe.common.activity;

import java.util.Iterator;
import java.util.Map;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.conf.Resource;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Controller;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.util.StringUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


/**
 * 베이스 Activity
 * checkCore
 *
 * @author : june
 */
public abstract class SKTActivity2 extends Activity {
    private static final String TAG = "SKTActivity";

    int DISCONNECT_ALARM_MINUTE = 10;
    final String PRIMITIVE_LAUNCHER = "COMMON_APP_LAUNCHERHYBRID";
    final String PRIMITIVE_MAIL_COUNT = "COMMON_MAILTEST_PRIVATEBOX";
    final String PRIMITIVE_APPROVAL_COUNT = "COMMON_APPROVALNEW_COUNT";


    Activity mine;
    String errorMessage = "";

    //private static boolean _isGroupApp = true;
    /*
     * 속도를 위해 groupCompanyCd 검증은 생략한다.
     * 무슨 문제가 있을 지 면밀히 검토가 필요하다.
     * by pluto248 2011-11-01
     */
    private String _groupCompanyCd = "";
    private SKTActivity2 parentActivity = this;
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
     * 회사코드를 얻는다.
     *
     * @return 회사코드
     */
    protected String getGroupCompanyCd() {
        return _groupCompanyCd;
    }

    /**
     * 인증어플과 연동하지 않는지 여부를 리턴한다.
     * @return 인증어플 연동여부
     */
	/*protected boolean () {
		if(Constants.CoreComponent.APP_ID_AUTH.equals(appId) || 
				Constants.CoreComponent.APP_ID_CLIENT.equals(appId)) {
			return true;
		} else {
			return false;
		}
	}*/

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
     * 서비스 파일 얻기
     *
     * @return
     */
    public String getServiceFile() {
        return b_serviceFile;
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
     * 사용자 플래그를 설정한다.
     */
    public void userBlocking() {
        synchronized (this) {
            userBlock = Constants.REQ_AUTH;
        }
    }

    /**
     * 사용자 플래그를 해제한다.
     */
    public void userUnblocking() {
        synchronized (this) {
            userBlock = -1;
        }
    }

    /**
     * 시스템 플래그를 설정한다.
     *
     * @param block 플래그
     */
    public void blocking(int block) {
        synchronized (this) {
            this.block = block;
        }
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

    @Override
    protected final void onCreate(Bundle savedInstanceState) {

        Log.d("SKTActivity", "action onCreate");

//		SKTUtil.log("TEST", "Pluto248->1: " + System.currentTimeMillis() / 1000);
        mine = SKTActivity2.this;
        super.onCreate(savedInstanceState);


//		version = Float.parseFloat(Build.VERSION.RELEASE.substring(0, 3));

        Log.d("SKTActivity", "LOGIN_METHOD :" + getIntent().getStringExtra("LOGIN_METHOD"));

        if (getIntent().getStringExtra("LOGIN_METHOD") != null && getIntent().getStringExtra("LOGIN_METHOD").equals("MCUVIC")) {
            mIsMCuvic = true;

            mId = getIntent().getStringExtra("ID");
            mEncPwd = getIntent().getStringExtra("PWD");
            mLoginMethod = getIntent().getStringExtra("LOGIN_METHOD");
        }

//		Log.d("SKTActivity", "ID : "+ mId);
//		Log.d("SKTActivity", "PW : "+ mEncPwd);
//		Log.d("SKTActivity", "LOGIN_METHOD : "+ mLoginMethod);

        registerFinishedReceiver();

        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onCreate]", Build.MODEL);
        SKTUtil.log("TEST", "onCreate enter");
        appId = SKTUtil.getAppId(this);

        layoutResId = assignLayout();
        Log.i(TAG, "111111111111111111111111111111111");
//		if(SKTUtil.getImsi(this) == null) {
//			try {
//				throw new SKTException("USIM not exist.", Constants.Status.E_USIM);
//			} catch(SKTException e) {
//				blocking(Constants.REQ_AUTH);
//
//				e.alert(this, new DialogButton(0) {
//					public void onClick(DialogInterface arg0, int arg1) {
//						SKTUtil.closeApp(SKTActivity.this);
//					}
//				});
//
//				return;
//			}
//		}

        if (EnvironManager.PRODUCT_MODE && b_isCheckCore == true) {
            Log.i(TAG, "2222222222222222222222222222");
//			List<String> noneAppList = null;
//			try {
//				noneAppList = Constants.CoreComponent.checkCore(this);
//				if(noneAppList != null) {
//					final String SEP = ", ";
//					String noneApps = "";
//
//					boolean notfoundStore   = noneAppList.contains(Constants.CoreComponent.APP_ID_CLIENT);
//					if(notfoundStore) {
//						noneApps += Constants.CoreComponent.getCoreName(Constants.CoreComponent.APP_ID_CLIENT) + SEP;
//						noneApps = noneApps.substring(0, noneApps.length() - SEP.length());
//						throw new SKTException("Core-Check fail!", Constants.Status.E_CORE_CHK_WEB).addParam(noneApps);
//					}
//
//					for(int i=0; i<noneAppList.size(); i++) {
//						noneApps += Constants.CoreComponent.getCoreName(noneAppList.get(i)) + SEP;
//					}
//					noneApps = noneApps.substring(0, noneApps.length() - SEP.length());
//
//					throw new SKTException("Core-Check fail!", Constants.Status.E_CORE_CHK).addParam(noneApps);
//				}
//			} catch(SKTException e) {
//				blocking(Constants.REQ_AUTH);
//
//				if(Constants.Status.E_CORE_CHK_WEB.equals(e.getErrCode())) {
//					e.alert(this, new DialogButton(0) {
//						public void onClick(DialogInterface arg0, int arg1) {
//							SKTUtil.goToffice(SKTActivity.this);
//							SKTUtil.closeApp(SKTActivity.this);
//						}
//					});
//				} else {
//					e.alert(this, new DialogButton(0) {
//						public void onClick(DialogInterface arg0, int arg1) {
//							SKTUtil.goMobileOffice(SKTActivity.this);
//							SKTUtil.closeApp(SKTActivity.this);
//						}
//					});
//				}
//
//				return;
//			}
        }

        if (EnvironManager.PRODUCT_MODE && mLocalActivityManager == null) {
            Log.i(TAG, "333333333333333333333333333333");

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
                        blocking(Constants.REQ_AUTH);

                        SKTDialog dlg = new SKTDialog(this);
                        dlg.getDialog(Resource.getString(this, Resource.RES_POSITIVE_ID), Resource.getString(this, Resource.RES_VALID_COMPANYCD_ID), new DialogButton(0) {
                            public void onClick(DialogInterface arg0, int arg1) {
                                SKTUtil.closeApp(SKTActivity2.this);
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
//			Log.i("SKTActivity", "setContentView layoutResId: "+layoutResId);
            setContentView(layoutResId);
            Log.i(TAG, "555555555555555555555555555");

        }

        if (block == -1) {
            Log.i(TAG, "6666666666666666666666666");

            Intent intent = new Intent();
            intent.putExtra("savedInstanceState", savedInstanceState);

//			Log.i("SKTActivity", "putExtra savedInstanceState"+savedInstanceState);

//			SKTUtil.log(android.util.Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onCreateX]", userBlock + " / " + block);
//			onCreateX(savedInstanceState);

            //onResume 시에만 상태 확인
            if (EnvironManager.SSL_VPN_MODE) {
//				Log.i("SKTActivity", "SSL_VPN_MODE : "+EnvironManager.SSL_VPN_MODE+"");

                try {
                    Log.i(TAG, "onCreate try66666666666666666666");
//					Log.d("isConnected", "SSLVPNController.isBindService(create) : "+SSLVPNController.isBindService());

//					if (SSLVPNController.isBindService() == false) {
//						SKTUtil.log("TEST", "mCuvic Service Starting...");
//						boolean isBind = SSLVPNController.bindService(this, new SecuwayServiceConnection1(savedInstanceState));
////						if (isBind == false) {
////							SKTUtil.log("TEST", "mCuvic Service Binding fail.");
////							SKTDialog dlg = new SKTDialog(this);
////							dlg.getDialog("확인", Resource.getString(this, "_E071"), new DialogButton(0) {
////								public void onClick(DialogInterface arg0, int arg1) {
////									SKTUtil.runMCuvic(SKTActivity.this);
////									SKTUtil.closeApp(SKTActivity.this);
////								}
////							}).show();
////						}
//					} else {
                    onCreateW(savedInstanceState);
//					}
                } catch (Exception e) {
//					SKTUtil.runMCuvic(SKTActivity.this);
                    SKTUtil.closeApp(SKTActivity2.this);
                }
            } else {
                onCreateW(savedInstanceState);
            }
        }

//		SKTUtil.log("TEST", "Pluto248->2: " + System.currentTimeMillis() / 1000);
    }

    @Override
    protected final void onStart() {
        super.onStart();

//		Log.i("SKTActivity", "--------------onStart()-----------------");
//		SKTUtil.log("TEST", "Pluto248->3: " + System.currentTimeMillis() / 1000);
        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onStart]", userBlock + " / " + block);

        Log.i(TAG, "==============onStart() ===========77777777777777777777777777");


        if (userBlock == -1 && block == -1) {
            if (EnvironManager.PRODUCT_MODE && b_isCheckCore == true) {


//				List<String> noneAppList = null;
//				try {
//					noneAppList = Constants.CoreComponent.checkCore(this);
//					if(noneAppList != null) {
//						final String SEP = ", ";
//						String noneApps = "";
//
//						boolean notfoundStore   = noneAppList.contains(Constants.CoreComponent.APP_ID_CLIENT);
//						if(notfoundStore) {
//							noneApps += Constants.CoreComponent.getCoreName(Constants.CoreComponent.APP_ID_CLIENT) + SEP;
//							noneApps = noneApps.substring(0, noneApps.length() - SEP.length());
//							throw new SKTException("Core-Check fail!", Constants.Status.E_CORE_CHK_WEB).addParam(noneApps);
//						}
//
//						for(int i=0; i<noneAppList.size(); i++) {
//							noneApps += Constants.CoreComponent.getCoreName(noneAppList.get(i)) + SEP;
//						}
//						noneApps = noneApps.substring(0, noneApps.length() - SEP.length());
//
//						throw new SKTException("Core-Check fail!", Constants.Status.E_CORE_CHK).addParam(noneApps);
//					}
//				} catch(SKTException e) {
//					blocking(Constants.REQ_AUTH);
//
//					if(Constants.Status.E_CORE_CHK_WEB.equals(e.getErrCode())) {
//						e.alert(this, new DialogButton(0) {
//							public void onClick(DialogInterface arg0, int arg1) {
//								SKTUtil.goToffice(SKTActivity.this);
//								SKTUtil.closeApp(SKTActivity.this);
//							}
//						});
//					} else {
//						e.alert(this, new DialogButton(0) {
//							public void onClick(DialogInterface arg0, int arg1) {
//								SKTUtil.goMobileOffice(SKTActivity.this);
//								SKTUtil.closeApp(SKTActivity.this);
//							}
//						});
//					}
//
//					return;
//				}
            }

            if (EnvironManager.PRODUCT_MODE && mLocalActivityManager == null) {
                try {
                    Log.i(TAG, "888888888888888888888888888");

                    //CJ 수정사항
                    Map<String, String> map = SKTUtil.getCheckedCompanyCd_DiffId(this);
                    String checkedCompanyCd = map.get(AuthData.ID_CHECKED_COMPANY_CD);

                    if (!StringUtil.isNull(_groupCompanyCd)) {
                        if (!_groupCompanyCd.equals(checkedCompanyCd)) {
                            blocking(Constants.REQ_AUTH);

                            SKTDialog dlg = new SKTDialog(this);
                            dlg.getDialog(Resource.getString(this, Resource.RES_POSITIVE_ID), Resource.getString(this, Resource.RES_VALID_COMPANYCD_ID), new DialogButton(0) {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    SKTUtil.closeApp(SKTActivity2.this);
                                }
                            }).show();

                            return;
                        }
                    } else {
                        _groupCompanyCd = checkedCompanyCd;
                    }

                    //CJ 추가사항
//					if("".equals(myId)) {
//						myId = map.get(AuthData.ID_ID);
//					} else if(!map.get(AuthData.ID_ID).equals(myId)) {
//						throw new SKTException("Diff-ID Validataion!", Constants.Status.E_DIFF_ID);
//					}
                } catch (SKTException e) {
                    blocking(Constants.REQ_AUTH);
                    Log.i(TAG, "999999999999999999999999999");

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
            if (EnvironManager.SSL_VPN_MODE) {
                try {
                    Log.i(TAG, "111000000000000000000000000000");
                    onStartW();
                } catch (Exception e) {
//					SKTUtil.runMCuvic(SKTActivity.this);
                    SKTUtil.closeApp(SKTActivity2.this);
                }
            } else {
                onStartW();
            }
//			SKTUtil.log(android.util.Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onStartX]", userBlock + " / " + block);
//			onStartX();
        }
//		SKTUtil.log("TEST", "Pluto248->4: " + System.currentTimeMillis() / 1000);
    }

    @Override
    protected final void onResume() {
//		SKTUtil.log("TEST", "Pluto248->5: " + System.currentTimeMillis() / 1000);
        super.onResume();
        Log.i(TAG, "============onResume===============");

        Log.d("", "action onResume");

        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onResume]", userBlock + " / " + block);

        if (userBlock == -1 && block == -1) {

            if (EnvironManager.SSL_VPN_MODE) {
                try {
                    onResumeW();
                } catch (Exception e) {
//					SKTUtil.runMCuvic(SKTActivity.this);
                    SKTUtil.closeApp(SKTActivity2.this);
                }
            } else {
                onResumeW();
            }
//			SKTUtil.log(android.util.Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onResumeX]", userBlock + " / " + block);
//			onResumeX();
        } else {
            blocking(-1);
        }
    }


    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("테스트", "123123");

        SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onActivityResult]", userBlock + " / " + block);

        SKTActivity2 currentActivity = this;
        if (mLocalActivityManager != null) {
            SKTActivity2 act = (SKTActivity2) mLocalActivityManager.getCurrentActivity();
            if (act != null)
                currentActivity = act;
        }

        if (requestCode == Constants.REQ_AUTH) {
            currentActivity.blocking(Constants.REQ_AUTH);

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
                            currentActivity.blocking(Constants.REQ_AUTH);
                            currentActivity.exception = null;

                            currentActivity.OnImageError();
                            return;
                        }
                        break;
                }
            }
			/*else if(requestCode == Constants.SSM_UNINSTALLED){
				Log.e(TAG, "onActivityResult SSM UNINSTALLED");
				ssmLib.startSSMInstaller(mine, downloadUrl, dialogTitle, dialogMessage, false);
				SKTUtil.closeApp(SKTActivity.this);
			}
			else if(requestCode == Constants.SSM_OLD_INSTALLED){
				Log.e(TAG, "onActivityResult SSM OLD INSTALLED");
				ssmLib.requestRemoveSSM();
//				ssmLib.startSSMInstaller(mine, downloadUrl, dialogTitle, dialogMessage, true);

//				SKTUtil.closeApp(SKTActivity.this);
				SKTUtil.closeApp(parentActivity);
			}*/


            SKTUtil.log(Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onActivityResultX]", requestCode + " / " + resultCode);
            onActivityResultX(requestCode, resultCode, data);
        }
    }


    public void alertDialog(int checkId) {
        try {
            SKTUtil.logout(SKTActivity2.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(SKTActivity2.this);
        dialog.setTitle("알림").setMessage(Resource.getCheckMessage(SKTActivity2.this, checkId));
        dialog.setCancelable(false);
        dialog.setNeutralButton("확인", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                try {
                    Log.d(TAG, "================logout==================");
//					SKTUtil.logout(SKTActivity.this);
//					SKTUtil.closeApp(SKTActivity.this);
                    SKTUtil.closeApp(parentActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.create().show();


		/*SKTDialog dlg = new SKTDialog(this);
		dlg.getDialog("확인", Resource.getCheckMessage(this, checkId), new DialogButton(0) {
			public void onClick(DialogInterface arg0, int arg1) {
//
				try{
					Log.d(TAG, "================logout==================");
					SKTUtil.logout(SKTActivity.this);
//					SKTUtil.closeApp(SKTActivity.this);
					SKTUtil.closeApp(parentActivity);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}).show();*/

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
            blocking(-1);
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
                if (parentActivity.getParent() instanceof SKTActivity2)
                    parentActivity = (SKTActivity2) parentActivity.getParent();
            }
            if (Constants.Status.E_AUTH_NOT.equals(ex.getErrCode())) {
                ex.setAlertReset();
                ex.alert(this, new DialogButton(0) {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SKTUtil.closeApp(SKTActivity2.this);
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
                            String companyCd = SKTUtil.getCheckedCompanyCd(SKTActivity2.this);
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
                                String checkCompanyCd = SKTUtil.getCheckedCompanyCd(SKTActivity2.this);
                                Map<String, String> companyMap = SKTUtil.getCompanyList(SKTActivity2.this);

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
            } else if (Constants.Status.N_7001.equals(ex.getErrCode()) ||
                    Constants.Status.N_7009.equals(ex.getErrCode())) {
                ex.setAlertReset();

                if (!mIsMCuvic) {

                    SKTDialog dlg = new SKTDialog(this, SKTDialog.DLG_TYPE_1);
                    dlg.getDialog(Resource.getString(this, Resource.RES_POSITIVE_ID), ex.getEncodedMessage(this)).show();
                    dlg.setPositiveButton(new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.runGMPLogin(parentActivity);
                        }
                    });

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
                        SKTUtil.goToffice(SKTActivity2.this);
                        SKTUtil.closeApp(SKTActivity2.this);
                    }
                });
            } else if (Constants.Status.N_7004.equals(ex.getErrCode()) ||
                    Constants.Status.N_7010.equals(ex.getErrCode()) ||
                    Constants.Status.N_7011.equals(ex.getErrCode()) ||
                    Constants.Status.N_7012.equals(ex.getErrCode()) ||
                    Constants.Status.N_7014.equals(ex.getErrCode()) ||
                    Constants.Status.E_NETWORK.equals(ex.getErrCode())) {
                ex.setAlertReset();
                ex.alert(this, new DialogButton(0) {
                    public void onClick(DialogInterface arg0, int arg1) {
                        SKTUtil.closeApp(SKTActivity2.this);
                    }
                });
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
                                SKTUtil.closeApp(SKTActivity2.this);
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
                            SKTUtil.goToffice(SKTActivity2.this);
                            SKTUtil.closeApp(SKTActivity2.this);
                        }
                    });
                } else if (Constants.CoreComponent.isCore(this)) {
                    ex.setErrCode(Constants.Status.N_3205_A);
                    ex.addParam(SKTUtil.getAppName(this));
                    ex.setAlertReset();
                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.goMobileOffice(SKTActivity2.this);
                            SKTUtil.closeApp(SKTActivity2.this);
                        }
                    });
                } else {
                    ex.setErrCode(Constants.Status.N_3205_A);
                    ex.addParam(SKTUtil.getAppName(this));
                    ex.setAlertReset();
                    ex.alert(this, new DialogButton(0) {
                        public void onClick(DialogInterface arg0, int arg1) {
                            SKTUtil.goMobileOffice(SKTActivity2.this, appId);
                            SKTUtil.closeApp(SKTActivity2.this);
                        }
                    });
                }
                //CJ 추가
            } else if (Constants.Status.E_DIFF_ID.equals(ex.getErrCode())) {
                SKTUtil.restartApp(SKTActivity2.this);
            } else if (Constants.Status.E_DIFF_ID.equals(ex.getErrCode())) {

            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "=======================onDestroy=======================");
//		VPNConnectionService.vpncon.unRegistAlarm(getApplicationContext());

        if (progressDlg != null && progressDlg.isShowing() == true) {
            progressDlg.dismiss();
        }

        unregisterFinishedReceiver();

//		if(pref != null){
//			ed = pref.edit();
//			ed.putString("isFirstActivity", "N");
//			ed.remove("isFirstActivity");
//			ed.commit();
//		}

        super.onDestroy();
    }

    private void onCreateW(Bundle savedInstanceState) {
//		Log.i("SKTActivity", "onCreateW");
//		try {
//			SSLVPNController.checkConnectedSslVpn(SKTActivity.this);
//			SKTUtil.log(android.util.Log.DEBUG, "[" + this.getClass().getSimpleName() + ".onCreateX]", userBlock + " / " + block);
        onCreateX(savedInstanceState);
//		} catch (SKTException e) {
//			e.alert(SKTActivity.this, new DialogButton("확인") {
//				public void onClick(DialogInterface dialog, int id) {
//					SKTUtil.closeApp(SKTActivity.this);
//					SKTUtil.runMCuvic(SKTActivity.this);
//				}
//			});
//			return;
//		}
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
    private ProgressBar bar = null;

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


            if (progressDlg == null) {
//				progressDlg = new ProgressDialog(SKTActivity.this);
                SKTUtil.log("TEST", "getParent: " + getParent());
                if (getParent() != null) {
//					bar = new ProgressBar(getParent(), null, android.R.attr.progressBarStyleHorizontal);
//					bar.setIndeterminate(true);

                    progressDlg = new ProgressDialog(getParent());
//					progressDlg.set
//					progressDlg = new ProgressDialog(getParent(), R.style.Widget_AppCompat_ProgressBar_Horizontal);
//					progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//					progressDlg.setMax(100);
//					progressDlg.setIndeterminate(false);
//					progressDlg.setProgress(10);
//					progressDlg.setSecondaryProgress(50);
//					progressDlg.setProgressNumberFormat(null);
//					progressDlg.setProgressPercentFormat(null);
                    progressDlg.setOwnerActivity(getParent());
                } else {
//					bar = new ProgressBar(SKTActivity.this, null, android.R.attr.progressBarStyleHorizontal);
//					bar.setIndeterminate(true);

                    progressDlg = new ProgressDialog(SKTActivity2.this);
//					progressDlg = new ProgressDialog(SKTActivity.this, R.style.Widget_AppCompat_ProgressBar_Horizontal);
//					progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//					progressDlg.setMax(100);
//					progressDlg.setIndeterminate(false);
//					progressDlg.setProgress(10);
//					progressDlg.setSecondaryProgress(50);
//					progressDlg.setProgressNumberFormat(null);
//					progressDlg.setProgressPercentFormat(null);
                    progressDlg.setOwnerActivity(SKTActivity2.this);

                }
            }

            Resources res = SKTActivity2.this.getResources();

            String b_primitive = primitive;

            if (PRIMITIVE_HISTORY.equals(primitive)) {
                b_primitive = b_parameters.getPrimitive();
            }
            this.pendding = pendding;

            int resId = onActionPre(b_primitive);

            if (pendding) {
                switch (resId) {
                    case SERVICE_RETRIEVING:
                        this.serviceMessage = Resource.getString(SKTActivity2.this, Resource.RES_RETRIEVING_ID);
                        break;
                    case SERVICE_SENDING:
                        this.serviceMessage = Resource.getString(SKTActivity2.this, Resource.RES_SENDING_ID);
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
                progressDlg.setCancelable(false);
                progressDlg.setMessage(this.serviceMessage);

                if (progressDlg.isShowing() == false) {

                    if ((PRIMITIVE_MAIL_COUNT).equals(primitive) || (PRIMITIVE_APPROVAL_COUNT).equals(primitive)) {
                        Log.i(TAG, "primitive======>>>>" + primitive);
                    } else {
                        Log.i(TAG, "primitive---------->>>>" + primitive);
                        try {
                            progressDlg.show();
                        } catch (Exception e) {
                            SKTUtil.log("TEST", "progressDlg.isShowing error");
                        }
                    }

                }

//				publishProgress(0);
                iscancelled = false;
            }
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
//			progressDlg.setProgress(values[0]);
//			bar.setProgress(values[0]);

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
					
					/*
					Controller controller = new Controller(SKTActivity.this);
					ret = controller.request(b_parameters, b_postFlag, b_serviceFile);
					*/
                }

                ex = null;
            } catch (SKTException e) {
                ex = e;
                SKTActivity2.this.exception = e;
            } catch (Exception e) {
                ex = new SKTException(e);
                SKTActivity2.this.exception = ex;
            }
            return ret;
        }

        @Override
        protected void onPostExecute(XMLData result) {
            long t = System.currentTimeMillis();
            Log.i(TAG, "onPostExecute @@@@@@@@@@@@@@@@@@@");
            super.onPostExecute(result);

            try {
                if (progressDlg.isShowing() == true) {
                    progressDlg.dismiss();
                }

            } catch (IllegalArgumentException ex) {
                SKTUtil.log("DEBUG", "[Comm] : dismiss exception: " + ex.toString());
            }

            onCommonError(ex);

            String b_primitive = primitive;
            if (PRIMITIVE_HISTORY.equals(primitive)) {
                b_primitive = b_parameters.getPrimitive();
            }

            try {
                onActionPost(b_primitive, result, ex);
            } catch (SKTException e) {
                e.alert(SKTActivity2.this);
            }

            SKTUtil.timestamp(SKTActivity2.this, t, b_primitive, "[ ACT-PST DONE ]");
        }

        @Override
        protected void onCancelled() {
            try {
                if (progressDlg.isShowing() == true) {
                    progressDlg.dismiss();
//					iscancelled = true;
//					publishProgress(0);
                }
            } catch (IllegalArgumentException ex) {
                SKTUtil.log("DEBUG", "[Comm] : Cancel Dismiss exception: " + ex.toString());
            }
        }

    }

    //ghchoi


    //종료를 위한 브로드캐스트 리시버 생성
    private BroadcastReceiver finishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SKTActivity2.this.finish();
        }
    };

    //종료를 위한 브로드캐스트 리시버 장착 메소드
    private void registerFinishedReceiver() {
        IntentFilter filter = new IntentFilter("com.ex.group.FINISH");
        registerReceiver(finishedReceiver, filter);
    }

    //종료를 위한 브로드캐스트 리시버 해제 메소드
    private void unregisterFinishedReceiver() {

        unregisterReceiver(finishedReceiver);
    }


}
