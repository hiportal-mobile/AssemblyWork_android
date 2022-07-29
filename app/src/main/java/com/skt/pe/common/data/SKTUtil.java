package com.skt.pe.common.data;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.conf.Resource;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.util.Base64Util;
import com.skt.pe.util.DateUtil;
import com.skt.pe.util.EncryptUtil;
import com.skt.pe.util.FileUtil;
import com.skt.pe.util.StringUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


/**
 * SKT 유틸리티
 *
 * @author : june
 * @date : $Date$
 * @id : $Id$
 */
public class SKTUtil {
    //	private final static String testSecretKey = "0601cccdfcf701c0885b4fe0b5917e96";
//	public SSMLib ssmLib;
//	public Context mContext;
    public static String TAG = "SKTUtil";
    //	public int code = -1;
    public static int installCode = -1;

//	public static Handler handler;

    /**
     * 문자열을 숫자로 변환한다.
     *
     * @param value 문자열
     * @return 숫자
     * @throws SKTException
     */
    public static int parseInt(String value) throws SKTException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new SKTException(e);
        }
    }

    static ProgressDialog mProgressDialog;


    public static void showProgress(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("설치 중입니다.");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
    }

    public static void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 로그를 출력한다.
     *
     * @param title   제목
     * @param content 내용
     */
    public static void log(String title, String content) {
        log(Log.DEBUG, title, content);
    }

    /**
     * 로그를 출력한다.
     *
     * @param level   로그레벨
     * @param title   제목
     * @param content 내용
     */
    public static void log(int level, String title, String content) {
        if (content == null)
            content = "content is null!";

        String[] contentArr = content.split("\n");

        switch (level) {
            case Log.DEBUG:
                if (EnvironManager.LOG_MODE) {
                    for (int i = 0; i < contentArr.length; i++) {
                        Log.d(title, contentArr[i]);
                    }
                }
                break;
            case Log.INFO:
                for (int i = 0; i < contentArr.length; i++) {
                    Log.i(title, contentArr[i]);
                }
                break;
            case Log.WARN:
                for (int i = 0; i < contentArr.length; i++) {
                    Log.w(title, contentArr[i]);
                }
                break;
            case Log.ERROR:
                for (int i = 0; i < contentArr.length; i++) {
                    Log.e(title, contentArr[i]);
                }
                break;
        }
    }

    /**
     * 리소스 식별자를 얻는다.
     *
     * @param context context
     * @param name    리소스 아이디
     * @param defType 리소스 타입
     * @return 리소스 식별자
     * @throws SKTException
     */
    public static int getResId(Context context, String name, String defType) throws SKTException {
        try {
            return context.getResources().getIdentifier(name, defType, context.getPackageName());
        } catch (Exception e) {
            throw new SKTException(e);
        }
    }

    /**
     * mdn을 얻는다
     *
     * @param context context
     * @return mdn
     */
    public static String getMdn(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mdn = tm.getLine1Number();
        if (mdn != null) {
            if (mdn.indexOf("+82") == 0) {
                mdn = mdn.replaceFirst("\\+82", "0");
            }
            /*
             * 에뮬레이터를 위해 강제 세팅...
             * 실제는 삭제되어야 함...
             */
//			mdn = "01011112222";
            /************************/
        } else {
            /*
             * by pluto248
             * 갤럭시탭 10.1때문에 mdn에 "" 이면 강제로 mdn을 설정함
             * 추후 이것을 mdn = ""로 바꾸어야 함...
             */
//			mdn = "01011112222";
//			SKTUtil.log("TEST", "No MDN, Replace Mac");
            mdn = getMdnMacAddr(context);
        }

//		SKTUtil.log("TEST", "MDN: " + mdn);
        return mdn;
    }

    public static String getMdnMacAddr(Context context) {
        StringBuffer mdnMacAddr = new StringBuffer();
        String macAddr = getMacAddr(context);
        String[] macHexs = macAddr.split(":");
        for (int i = 0; i < macHexs.length; i++) {
            mdnMacAddr.append(macHexs[i]);
        }
        return mdnMacAddr.toString().trim();
    }

    /**
     * Mac Address를 얻는다.
     *
     * @param context
     * @return
     */
    public static String getMacAddr(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    /**
     * imsi를 얻는다.
     *
     * @param context context
     * @return imsi
     */
    public static String getImsi(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * imei를 얻는다
     *
     * @param context context
     * @return imei
     */
    public static String getImei(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId();
    }

    /**
     * WIFI 네트웍 정보를 얻는다
     *
     * @param context context
     * @return 네트웍 정보
     */
    public static NetworkInfo getWifiNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 3G 네트웍 정보를 얻는다
     *
     * @param context context
     * @return 네트웍 정보
     */
    public static NetworkInfo getMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * 어플리케이션을 종료한다.
     *
     * @param context context
     */
    public static void closeApp(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.setResult(Constants.RES_CLOSE);
            activity.finish();

            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
            if (sdkVersion < 8) {
                ActivityManager am = (ActivityManager) activity.getSystemService(Activity.ACTIVITY_SERVICE);
                am.restartPackage(activity.getPackageName());
            }
			/*
			else {
		        PackageManager pm = context.getPackageManager();
		        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        context.startActivity(intent);

	        	android.os.Process.killProcess(android.os.Process.myPid());
			}
			*/
        }
    }

    public static void restartApp(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.moveTaskToBack(true);
            activity.setResult(Constants.RES_CLOSE);
            activity.finish();

            PackageManager pm = context.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    /**
     * 3DES 암호화를 한다
     *
     * @param context context
     * @param src     원본데이타
     * @return 암호화 결과
     * @throws SKTException
     */
    public static byte[] encrypt3DESBinay(Context context, byte[] src) throws SKTException {
        try {
            String secretKey = getSecretKey(context);
            byte[] data = EncryptUtil.generate3DesEncode(src, secretKey);
            return Base64Util.enc(data);
        } catch (SKTException e) {
            throw e;
        } catch (Exception e) {
            throw new SKTException(e);
        }
    }

    /**
     * 3DES 복호화를 한다
     *
     * @param context context
     * @param src     암호화데이타
     * @return 복호화 결과
     * @throws SKTException
     */
    public static byte[] decrypt3DESBinary(Context context, byte[] src) throws SKTException {
        return decrypt3DESBinary(context, src, true);
    }

    /**
     * 3DES 복호화를 한다
     *
     * @param context    context
     * @param src        암호화데이타
     * @param base64Flag base64여부
     * @return 복호화 결과
     * @throws SKTException
     */
    public static byte[] decrypt3DESBinary(Context context, byte[] src, boolean base64Flag) throws SKTException {
        try {
            String secretKey = getSecretKey(context);
            byte[] data = src;
            if (base64Flag)
                data = Base64Util.dec(src);
            return EncryptUtil.generate3DesDecode(data, secretKey);
        } catch (SKTException e) {
            throw e;
        } catch (Exception e) {
            throw new SKTException(e);
        }
    }

    /**
     * 3DES 복호화를 한다
     *
     * @param context context
     * @param src     암호화데이타
     * @return 복호화 결과
     * @throws SKTException
     */
    public static byte[] decrypt3DES(Context context, String src) throws SKTException {
        try {
            String secretKey = getSecretKey(context);
            byte[] data = Base64Util.decodeBinay(src);
            return EncryptUtil.generate3DesDecode(data, secretKey);
        } catch (SKTException e) {
            throw e;
        } catch (Exception e) {
            throw new SKTException(e);
        }
    }

    /**
     * AES 복호화를 한다
     *
     * @param context context
     * @param src     암호화데이타
     * @return 복호화 결과
     * @throws SKTException
     */
    public static byte[] decryptAESBinary(Context context, byte[] src) throws SKTException {
        return decryptAESBinary(context, src, true);
    }

    /**
     * AES 복호화를 한다
     *
     * @param context    context
     * @param src        암호화데이타
     * @param base64Flag base64여부
     * @return 복호화 결과
     * @throws SKTException
     */
    public static byte[] decryptAESBinary(Context context, byte[] src, boolean base64Flag) throws SKTException {
        try {
            String secretKey = getSecretKey(context);
//			String secretKey = testSecretKey;
            byte[] data = src;
            if (base64Flag)
                data = Base64Util.dec(src);
            return EncryptUtil.generateAesDecode(data, secretKey);
        } catch (SKTException e) {
            throw e;
        } catch (Exception e) {
            throw new SKTException(e);
        }
    }

    /**
     * ASE 복호화를 한다
     *
     * @param context context
     * @param src     암호화데이타
     * @return 복호화 결과
     * @throws SKTException
     */
    public static byte[] decryptAES(Context context, String src) throws SKTException {
        try {
            String secretKey = getSecretKey(context);
//			String secretKey = testSecretKey;
            byte[] data = Base64Util.decodeBinay(src);
            return EncryptUtil.generateAesDecode(data, secretKey);
        } catch (SKTException e) {
            throw e;
        } catch (Exception e) {
            throw new SKTException(e);
        }
    }

    /**
     * 어플아이디를 얻는다
     *
     * @param context context
     * @return 어플아이디
     */
    public static String getAppId(Context context) {
        String appId = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo i = pm.getPackageInfo(context.getPackageName(), 0);
            appId = i.applicationInfo.loadDescription(pm) + "";
        } catch (NameNotFoundException e) {
        }
        return appId;
    }

    /**
     * 버전을 얻는다
     *
     * @param context context
     * @return 버전
     */
    public static String getVersion(Context context) {
        String version = "";
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = i.versionName + "";
        } catch (NameNotFoundException e) {
        }
        return version;
    }

    /**
     * 버전코드를 얻는다
     *
     * @param context context
     * @return 버전코드
     */
    public static int getVersionCode(Context context) {
        int versionCode = 1;
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionCode = i.versionCode;
        } catch (NameNotFoundException e) {
        }
        return versionCode;
    }

    /**
     * 어플명을 얻는다
     *
     * @param context context
     * @return 어플명
     */
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo i = pm.getPackageInfo(context.getPackageName(), 0);
            appName = i.applicationInfo.loadLabel(pm) + "";
        } catch (NameNotFoundException e) {
        }
        return appName;
    }

    /**
     * 언어코드를 얻는다
     *
     * @param context context
     * @return 언어코드
     */
    public static String getLang(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 설치여부를 판단한다
     *
     * @param context context
     * @param appId   어플아이디
     * @return 설치여부
     */
    public static String isInstallPackage(Context context, String appId) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> packages = manager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pack : packages) {
            ApplicationInfo app = pack.applicationInfo;
            String b_appId = app.loadDescription(manager) + "";

            if (b_appId.equals(appId)) {
                SKTUtil.log(Log.DEBUG, "packageInfo", pack.packageName + "(" + app.loadLabel(manager) + "/" + b_appId + "/" + pack.versionName + ")");
                return pack.versionName + "";
            }
        }

        return null;
    }

    /**
     * 설치여부를 판단한다
     *
     * @param context context
     * @param pkgName 패키지명
     * @return 설치여부
     */
    public static String isInstallPackage_2(Context context, String pkgName) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> packages = manager.getInstalledPackages(0);
        for (PackageInfo pack : packages) {
            ApplicationInfo app = pack.applicationInfo;
            String b_pkgName = app.packageName + "";

            if (b_pkgName.equals(pkgName)) {
                SKTUtil.log(Log.DEBUG, "packageInfo", pack.packageName + "(" + app.loadLabel(manager) + "/" + app.loadDescription(manager) + "/" + pack.versionName + ")");
                return pack.versionName + "";
            }
        }

        return null;
    }

    /**
     * Launcher 로그인을 실행한다
     *
     * @param activity activity
     */
    public static void runGMPLogin(Activity activity) {
        Log.i("SKTUtil", "================runGMPLogin-============");
//		if(SKTUtil.isInstallPackage(activity, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        if (activity.getClass().getSimpleName().indexOf("LoginActivity") != -1) {
            Log.i("SKTUtil", "================LoginActivity --11111-============");
            return;
        }

        Intent intent = new Intent(Constants.Action.GMP_LOGIN);
//		Intent intent = new Intent("com.ex.group.folder.LoginActivity");
        intent.putExtra(Constants.ID_MDN, EnvironManager.getTestMdn());
        intent.putExtra(Constants.CHECK_AUTH, "RELOGIN");
//		activity.startActivityForResult(intent, Constants.REQ_AUTH);
        activity.startActivity(intent);
    }

    /**
     * Launcher 로그인을 실행한다
     *
     * @param activity activity
     */
    public static void runLauncherMain(Activity activity) {
        Log.i("SKTUtil", "================runLauncherMain-============");
//		if(SKTUtil.isInstallPackage(activity, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        if (activity.getClass().getSimpleName().indexOf("LoginActivity") != -1) {
            Log.i("SKTUtil", "================LoginActivity --11111-============");
            return;
        }

//		Intent intent = new Intent(Constants.Action.GMP_LOGIN);
        Intent intent = new Intent("android.intent.action.MAIN");
        activity.startActivity(intent);
//		intent.putExtra(Constants.ID_MDN, EnvironManager.getTestMdn());
//		activity.startActivityForResult(intent, Constants.REQ_AUTH);
//		activity.startActivity(intent);
    }


    /**
     * GMP 로그인을 실행한다
     *
     * @param activity activity
     */
    public static void runGMPLoginWithEncPwd(Activity activity, String id, String encPwd, String loginMethod) {
        Log.i("SKTUtil", "=================runGMPLoginWithEncPwd==================");
//		if(SKTUtil.isInstallPackage(activity, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        if (activity.getClass().getSimpleName().indexOf("LoginActivity") != -1) {
            Log.i("SKTUtil", "================LoginActivity -----1111111====================");
            return;
        }

        Intent intent = new Intent(Constants.Action.GMP_LOGIN);

        intent.putExtra("ID", id);
        intent.putExtra("PWD", encPwd);
        intent.putExtra("LOGIN_METHOD", loginMethod);

        intent.putExtra(Constants.ID_MDN, EnvironManager.getTestMdn()); //디버깅 가능
        activity.startActivityForResult(intent, Constants.REQ_AUTH);
//		activity.startActivity(intent);
    }


    /**
     * 레가시 로그인을 실행한다
     *
     * @param activity  activity
     * @param companyCd companyCd
     */
    public static void runLegacyLogin(Activity activity, String companyCd) {
//		if(SKTUtil.isInstallPackage(activity, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        if (activity.getClass().getSimpleName().indexOf("LoginActivity") != -1) {
            return;
        }

        Intent intent = new Intent(Constants.Action.LEGACY_LOGIN);
        intent.putExtra(Constants.ID_MDN, EnvironManager.getTestMdn()); //디버깅 가능
        intent.putExtra(AuthData.ID_COMPANY_CD, companyCd);
        activity.startActivityForResult(intent, Constants.REQ_AUTH);
    }

    /**
     * GMP 로그인을 실행한다
     *
     * @param context context
     */
    public static void runGMPLogin_2(Context context) {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        Intent intent = new Intent(Constants.Action.GMP_LOGIN);
        intent.putExtra(Constants.ID_MDN, EnvironManager.getTestMdn()); //디버깅 가능
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 레가시 로그인을 실행한다
     *
     * @param context   context
     * @param companyCd companyCd
     */
    public static void runLegacyLogin_2(Context context, String companyCd) {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        Intent intent = new Intent(Constants.Action.LEGACY_LOGIN);
        intent.putExtra(Constants.ID_MDN, EnvironManager.getTestMdn()); //디버깅 가능
        intent.putExtra(AuthData.ID_COMPANY_CD, companyCd);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 로그아웃한다 URI에 저장된 값 삭제
     *
     * @param context context
     * @throws SKTException
     */
    public static void logout(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}
        Log.i(TAG, "-------------------------SKTUtil-----------------------logout");
        ContentResolver cr = context.getContentResolver();
        cr.update(Constants.Auth.CONTENT_URI, new ContentValues(), null, null);
    }

    /**
     * 로그인여부를 체크한다
     *
     * @param context context
     * @throws SKTException
     */
    public static void checkLogin(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        Log.i(TAG, "====================CHECK LOGIN=========================");
        if (EnvironManager.PRODUCT_MODE) {
            Environ environ = EnvironManager.getEnviron(context);
            ContentValues values = new ContentValues();
            values.put(AuthData.ID_MDN, environ.getMdn());
            values.put(AuthData.ID_APP_ID, environ.getAppId());

            ContentResolver cr = context.getContentResolver();
            Uri uri = cr.insert(Constants.Auth.CONTENT_GMP_AUTH_PWD_URI, values);
            List<String> authValues = uri.getPathSegments();
            String returnId = authValues.get(1);

            if (AuthData.RET_NONE.equals(returnId)) {
                throw new SKTException("GMP_AUTH is null!", Constants.Status.E_AUTH_NULL);
            } else if (AuthData.RET_AUTH_FAIL.equals(returnId)) {
                throw new SKTException("GMP_AUTH is fail!", Constants.Status.E_AUTH_FAIL);
            } else if (AuthData.RET_AUTH_GMP.equals(returnId)) {
                throw new SKTException("GMP_AUTH is fail!", Constants.Status.E_AUTH_GMP);
            } else if (AuthData.RET_AUTH_LEGACY.equals(returnId)) {
                throw new SKTException("LEGACY is fail!", Constants.Status.E_AUTH_LEGACY);
            }
        }
    }

    /**
     * authKey를 얻는다
     *
     * @param context context
     * @return authKey
     * @throws SKTException
     */
    public static String getAuthKey(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return null;
//		}

        if (EnvironManager.PRODUCT_MODE) {
            Environ environ = EnvironManager.getEnviron(context);
            ContentValues values = new ContentValues();
            values.put(AuthData.ID_MDN, environ.getMdn());
            values.put(AuthData.ID_APP_ID, environ.getAppId());

            ContentResolver cr = context.getContentResolver();
            Uri uri = cr.insert(Constants.Auth.CONTENT_AUTHKEY_URI, values);
            List<String> authValues = uri.getPathSegments();
            String returnId = authValues.get(1);

            SKTUtil.log(Log.DEBUG, "AUTHKEY_RESULT", returnId);

            if (AuthData.RET_NONE.equals(returnId)) {
                throw new SKTException("AuthKey is null!", Constants.Status.E_AUTH_NULL);
            } else if (AuthData.RET_AUTH_FAIL.equals(returnId)) {
                throw new SKTException("AuthKey is fail!", Constants.Status.E_AUTH_FAIL);
            }

            return authValues.get(2);
        } else {
            return EnvironManager.MASTER_KEY;
        }
    }

    /**
     * secretKey를 얻는다
     *
     * @param context context
     * @return secretkey
     * @throws SKTException
     */
    public static String getSecretKey(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return null;
//		}

        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(Constants.Auth.CONTENT_SECRETKEY_URI, new ContentValues());
        List<String> authValues = uri.getPathSegments();
        String returnId = authValues.get(1);

        SKTUtil.log(Log.DEBUG, "SECRETKEY_RESULT", returnId);

        if (AuthData.RET_NONE.equals(returnId)) {
            throw new SKTException("Secret-Value is null!", Constants.Status.E_AUTH_NULL);
        }

        return authValues.get(2);
    }

    /**
     * nonce값을 얻는다
     *
     * @param context context
     * @return nonce
     * @throws SKTException
     */
    public static String getNonce(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return null;
//		}

        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(Constants.Auth.CONTENT_NONCE_URI, new ContentValues());
        List<String> authValues = uri.getPathSegments();
        String returnId = authValues.get(1);

        SKTUtil.log(Log.DEBUG, "NONCE_RESULT", returnId);

        if (AuthData.RET_NONE.equals(returnId)) {
            throw new SKTException("Nonce-Value is null!", Constants.Status.E_AUTH_NULL);
        }

        return authValues.get(2);
    }

    /**
     * GMP 정보를 얻는다(encPwd 제외)
     *
     * @param context context
     * @return GMP 정보
     * @throws SKTException
     */
    public static Map<String, String> getGMPAuth(Context context) throws SKTException {
		/*if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
			return null;
		}*/
        Log.i(TAG, "===============getGMPAuth================");
        Map<String, String> map = new HashMap<String, String>();

        if (EnvironManager.PRODUCT_MODE) {
            Environ environ = EnvironManager.getEnviron(context);
            ContentValues values = new ContentValues();
            values.put(AuthData.ID_MDN, environ.getMdn());
            values.put(AuthData.ID_APP_ID, environ.getAppId());

            ContentResolver cr = context.getContentResolver();
            Uri uri = cr.insert(Constants.Auth.CONTENT_GMP_AUTH_URI, values);

            List<String> authValues = uri.getPathSegments();


            String returnId = authValues.get(1);

            Log.i(TAG, "returnId..............." + returnId);

            SKTUtil.log(Log.DEBUG, "[GMP_AUTH_RESULT]", returnId);

            if (AuthData.RET_NONE.equals(returnId)) {
//				throw new SKTException("GMP_AUTH is null!", Constants.Status.E_AUTH_NULL);
                Log.i(TAG, " --------------------RET_NONE-------------------");
                throw new SKTException("GMP_AUTH is fail!", Constants.Status.E_AUTH_NULL);


            } else if (AuthData.RET_AUTH_FAIL.equals(returnId) || AuthData.RET_AUTH_GMP.equals(returnId)) {
                throw new SKTException("GMP_AUTH is fail!", returnId);
            }

            /*
             * 서버 세팅이 되기전까진 AuthKey는 마스터키로 강제로 세팅
             * by pluto248
             */
            map.put(AuthData.ID_AUTH_KEY, authValues.get(2));
//			map.put(AuthData.ID_AUTH_KEY  , EnvironManager.MASTER_KEY);

            map.put(AuthData.ID_ID, authValues.get(3));
            map.put(AuthData.ID_ID, authValues.get(4));
            Log.i(TAG, "authKey : " + authValues.get(2));
            Log.i(TAG, "ID_COMPANY_CD : " + authValues.get(3));
            Log.i(TAG, "ID_ID : " + authValues.get(4));
        } else {
            map.put(AuthData.ID_AUTH_KEY, EnvironManager.MASTER_KEY);
            map.put(AuthData.ID_COMPANY_CD, EnvironManager.getTestCompanyCd());
        }

        return map;
    }

    /**
     * GMP 정보를 얻는다
     *
     * @param context context
     * @return GMP 정보
     * @throws SKTException
     */
    public static Map<String, String> getGMPAuthPwd(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return null;
//		}

        Map<String, String> map = new HashMap<String, String>();

        if (EnvironManager.PRODUCT_MODE) {
            Environ environ = EnvironManager.getEnviron(context);
            ContentValues values = new ContentValues();
            values.put(AuthData.ID_MDN, environ.getMdn());
            values.put(AuthData.ID_APP_ID, environ.getAppId());

            ContentResolver cr = context.getContentResolver();
            Uri uri = cr.insert(Constants.Auth.CONTENT_GMP_AUTH_PWD_URI, values);
            List<String> authValues = uri.getPathSegments();
            String returnId = authValues.get(1);

            SKTUtil.log(Log.DEBUG, "[GMP_AUTH_PWD_RESULT]", returnId);

            if (AuthData.RET_NONE.equals(returnId)) {
                throw new SKTException("GMP_AUTH is null!", Constants.Status.E_AUTH_NULL);
            } else if (AuthData.RET_AUTH_FAIL.equals(returnId)) {
                throw new SKTException("GMP_AUTH is fail!", Constants.Status.E_AUTH_FAIL);
            } else if (AuthData.RET_AUTH_GMP.equals(returnId)) {
                throw new SKTException("GMP_AUTH is fail!", Constants.Status.E_AUTH_GMP);
            } else if (AuthData.RET_AUTH_LEGACY.equals(returnId)) {
                throw new SKTException("LEGACY is fail!", Constants.Status.E_AUTH_LEGACY);
            }

            /*
             * 서버 세팅이 되기전까진 AuthKey는 마스터키로 강제로 세팅
             * by pluto248
             */
            map.put(AuthData.ID_AUTH_KEY, authValues.get(2));
//			map.put(AuthData.ID_AUTH_KEY  , EnvironManager.MASTER_KEY);
            map.put(AuthData.ID_COMPANY_CD, authValues.get(3));
            map.put(AuthData.ID_ENC_PWD, authValues.get(4));
            map.put(AuthData.ID_ID, authValues.get(5));
        } else {
            map.put(AuthData.ID_AUTH_KEY, EnvironManager.MASTER_KEY);
            map.put(AuthData.ID_COMPANY_CD, EnvironManager.getTestCompanyCd());
            map.put(AuthData.ID_ENC_PWD, EnvironManager.getTestEncPwd());
        }

        return map;
    }

    /**
     * 선택된 회사코드를 얻는다
     *
     * @param context context
     * @return 회사코드
     * @throws SKTException
     */
    public static String getCheckedCompanyCd(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return null;
//		}

        if (EnvironManager.PRODUCT_MODE) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = cr.insert(Constants.Auth.CONTENT_CHECKED_COMPANY_CD_URI, new ContentValues());
            List<String> authValues = uri.getPathSegments();
            String returnId = authValues.get(1);

            SKTUtil.log(Log.DEBUG, "CHECKED_COMPANY_CD_RESULT", returnId);

            if (AuthData.RET_NONE.equals(returnId)) {
                throw new SKTException("CheckedCompanyCd-Value is null!", Constants.Status.E_AUTH_NULL);
            }

            return authValues.get(2);
        } else {
            return EnvironManager.getTestCompanyCd();
        }
    }

    //CJ 추가
    public static Map<String, String> getCheckedCompanyCd_DiffId(Context context) throws SKTException {

        Map<String, String> map = new HashMap<String, String>();

        ContentResolver cr = context.getContentResolver();
//		Uri uri = cr.insert(Constants.Auth.CONTENT_CHECKED_COMPANY_CD_URI, new ContentValues());
//		Log.i(TAG, "company_checked_company_cd_uri============"+Constants.Auth.CONTENT_CHECKED_COMPANY_CD_URI);
//		List<String> authValues = uri.getPathSegments();
//		String returnId = authValues.get(1);
//
//		Log.i("SKTUtil", "returnId : " + returnId);	//E001, E007
//		SKTUtil.log(android.util.Log.DEBUG, "CHECKED_COMPANY_CD_RESULT", returnId);
//
//		if(AuthData.RET_NONE.equals(returnId)|| AuthData.RET_AUTH_GMP.equals(returnId)) {
//			throw new SKTException("CheckedCompanyCd-Value is null!", Constants.Status.E_AUTH_NULL);
//
//		}
//		else{
//			map.put(AuthData.ID_CHECKED_COMPANY_CD, authValues.get(2));
//			map.put(AuthData.ID_ID, authValues.get(3));
//		}


        return map;
    }

    /**
     * 회사코드 목록을 얻는다
     *
     * @param context context
     * @return 회사코드 목록
     * @throws SKTException
     */
    public static Map<String, String> getCompanyList(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return null;
//		}

        Map<String, String> companyMap = new LinkedHashMap<String, String>();

        if (EnvironManager.PRODUCT_MODE) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = cr.insert(Constants.Auth.CONTENT_COMPANY_LIST_URI, new ContentValues());
            List<String> authValues = uri.getPathSegments();
            String returnId = authValues.get(1);

            SKTUtil.log(Log.DEBUG, "COMPANY_LIST_RESULT", returnId);

            if (AuthData.RET_NONE.equals(returnId)) {
                throw new SKTException("CompanyList-Value is null!", Constants.Status.E_AUTH_NULL);
            }

            // 한글의 공백 때문에 추가적으로 decode를 함
            String buffer = URLDecoder.decode(authValues.get(2));
            String[] company = buffer.split("\\|");


            for (int i = 0; i < company.length; i = i + 2) {
                companyMap.put(company[i], company[i + 1]);
            }
        } else {
            companyMap.put(EnvironManager.getTestCompanyCd(), EnvironManager.getTestCompanyCd());
        }

        return companyMap;
    }

    //CJ 추가
    public static Map<String, String> getAuthId(Context context) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return null;
//		}

        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(Constants.Auth.CONTENT_ID_URI, new ContentValues());
        List<String> authValues = uri.getPathSegments();
        String returnId = authValues.get(1);

        if (AuthData.RET_NONE.equals(returnId)) {
            throw new SKTException("ID-Value is null!", Constants.Status.E_AUTH_NULL);
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put(AuthData.ID_MAC, authValues.get(2));
        map.put(AuthData.ID_ID, authValues.get(3));
        map.put(AuthData.ID_PWD, authValues.get(4));
        map.put(AuthData.ID_LOGIN_DT, authValues.get(5));

        return map;
    }

    /**
     * nonce값을 업데이트한다
     *
     * @param context context
     * @param nonce   nonce
     */
    public static void updateNonce(Context context, String nonce) {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        if (EnvironManager.PRODUCT_MODE) {
            ContentResolver cr = context.getContentResolver();
            ContentValues cv = new ContentValues();
            cv.put(AuthData.ID_NONCE, nonce);
            cr.update(Constants.Auth.CONTENT_URI, cv, null, null);
        }
    }

    /**
     * 회사를 선택한다
     *
     * @param context   context
     * @param companyCd companyCd
     * @throws SKTException
     */
    public static void updateCheckedCompanyCd(Context context, String companyCd) throws SKTException {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//			return;
//		}

        if (EnvironManager.PRODUCT_MODE) {
            ContentResolver cr = context.getContentResolver();
            ContentValues cv = new ContentValues();
            cv.put(AuthData.ID_CHECKED_COMPANY_CD, companyCd);
            cr.update(Constants.Auth.CONTENT_URI, cv, null, null);

            Map<String, String> authParam = getGMPAuthPwd(context);
            if (authParam.get(AuthData.ID_ENC_PWD) == null)
                throw new SKTException(Constants.Status.E_AUTH_LEGACY);
        }
    }

    /**
     * 다운로드를 요청한다
     *
     * @param context      context
     * @param download_url url
     */
    public static void downloadPush(Context context, String download_url) {
        ContentValues values = new ContentValues();
        values.put(Constants.Download.KEY_INSTALL_FLAG, Constants.Download.INST_TYPE_NONE);
        values.put(Constants.Download.KEY_DOWNLOAD_URL, download_url);

        ContentResolver cr = context.getContentResolver();
        cr.insert(Constants.Download.CONTENT_URI, values);
    }

    public static void downloadPush(Context context, String download_url, String fileName, String tmpPath, String downPath, String auth) {
        ContentValues values = new ContentValues();
        values.put(Constants.Download.KEY_INSTALL_FLAG, Constants.Download.INST_TYPE_NONE);
        values.put(Constants.Download.KEY_DOWNLOAD_URL, download_url);
        values.put(Constants.Download.KEY_APP_NAME, fileName);
        values.put(Constants.Download.KEY_APP_TMP_PATH, tmpPath);
        values.put(Constants.Download.KEY_APP_DOWN_PATH, downPath);
        values.put(Constants.Download.KEY_APP_AUTH, auth);

        ContentResolver cr = context.getContentResolver();
        cr.insert(Constants.Download.CONTENT_URI, values);
    }

    /**
     * 다운로드를 요청한다
     *
     * @param context      context
     * @param download_url url
     * @param appId        어플아이디
     * @param appName      어플명
     * @param appIcon      어플아이콘
     * @param appIconUrl   어플아이콘 url
     */
    public static void downloadPush(Context context, String download_url, String appId, String appName, Bitmap appIcon, String appIconUrl) {
        ContentValues values = new ContentValues();
        values.put(Constants.Download.KEY_INSTALL_FLAG, Constants.Download.INST_TYPE_NONE);
        values.put(Constants.Download.KEY_DOWNLOAD_URL, download_url);
        values.put(Constants.Download.KEY_APP_ID, appId);
        values.put(Constants.Download.KEY_APP_NAME, appName);
        values.put(Constants.Download.KEY_APP_ICON_URL, appIconUrl);

        ByteArrayOutputStream bos = null;
        try {
            if (appIcon != null) {
                bos = new ByteArrayOutputStream();
                appIcon.compress(CompressFormat.PNG, 100, bos);
                byte[] appIconArr = bos.toByteArray();
                values.put(Constants.Download.KEY_APP_ICON, appIconArr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                }
            }
        }

        ContentResolver cr = context.getContentResolver();
        cr.insert(Constants.Download.CONTENT_URI, values);
    }

    /**
     * 설치삭제한다
     *
     * @param context     context
     * @param packageName 패키지명
     */
    public static void uninstall(Context context, String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent it = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(it);
    }

    /**
     * 설치한다
     *
     * @param context context
     * @param apk     설치파일
     */
    public static void install(Context context, File apk) {
        Uri uri = Uri.fromFile(apk);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.setData(uri);
        it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        it.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
        context.startActivity(it);
    }

    /**
     * SSM으로 부터 설치한다
     *
     * @param context context
     * @param apk     설치파일
     */
    public static void installFromSSM(final Context context, final String apkPath) {
//		public static void installFromSSM(Context context, String apkPath) {
//		public void installFromSSM(final Context context, final String apkPath) {
//			public void installFromSSM(Context context, String apkPath) {

		/* 기존 코드
		 * Intent intent = new Intent();
		intent.setAction("com.sktelecom.ssm.action.ACTION_FORCE_APP_INSTALL");
		intent.putExtra("APK_PATH", apkPath);
		context.sendBroadcast(intent);*/

//			if(ssmLib != null && ssmLib.doingBind()){
//				ssmLib.unbindAIDL();
//			}

        Log.e(TAG, "apkPath=========================== " + apkPath);


		/*installCode = ssmLib.forceAppInstall(apkPath);
		Log.e(TAG, "apkPath : "+apkPath);
		Log.e(TAG, "installCode======="+installCode);
		if(installCode != SSMLib.OK){
			installCode = ssmLib.appInstall(context, apkPath, 0);
			Log.e(TAG, "installCode--------"+installCode);
		}	*/


	/*ssmLib.registerSSMListener(new SSMLibListener() {

	// 명령 처음 실행 시 실패가 떨어졌을때 라이브러리 내부에서 3번을 더 시도한다.
    // 3번을 시도하는 동안 나오는 리턴값
			@Override
			public void onSSMResult(String key, Object returnValue) {
				Log.e(TAG, "onSSMResult");
				 if (key.equals(SSMLib.KEY_FORCE_APP_INSTALL)) {
			            int code = (Integer)returnValue;
			            Log.e(TAG, ".KEY_FORCE_APP_INSTALL return code : "+code);

			            if(code != SSMLib.OK){
			            	code = ssmLib.appInstall(context, apkPath, 0);
			            	Log.e(TAG, " appInstall installCode=========================="+code);
			            	if(code == SSMLib.OK){
			            		ssmLib.release();
			            		Log.e(TAG, "SSMLIB RELEASE");
			            	}
			            }
			        }
				    else if(key.equals(SSMLib.KEY_APP_INSTALL)){
			            int code = (Integer)returnValue;
			            Log.e(TAG, "KEY_APP_INSTALL return code : " + code);
				    	if(code != SSMLib.OK){
				    		Log.e(TAG, "return appinstall code : " + code);
				    	}
				    }
			}

			@Override
			public void onSSMRemoved() {
				// TODO Auto-generated method stub
				Log.e(TAG, "onSSMRemoved");
			}

			@Override
			public void onSSMInstalled() {
				// TODO Auto-generated method stub
				ssmLib.initialize();
				Log.e(TAG, "onSSMInstalled");
			}

			//Lib와 binding 성공 시 호출 됨 (보통 5초 내로)
			// binding 요청을 Library 내부적으로 3번 실행함
			@Override
			public void onSSMConnected() {
				Log.e(TAG, "onSSMConnected");
				Log.e(TAG, "SSMConncected doingBind............" + ssmLib.doingBind());

				installCode = ssmLib.forceAppInstall(apkPath);
				Log.e(TAG, "forceAppInstall apkPath >>>>>>>>>>>>>>>>>>"+apkPath);
				if(installCode == SSMLib.OK){
					ssmLib.release();
					Log.e(TAG, "installCode == 0 ssmLib release");
				}
				else{
					Log.e(TAG, "forceAppInstall is not OK installCode======"+installCode);
					installCode = ssmLib.appInstall(context, apkPath, 0);
				}
				Log.e("SKTUTil", "installCode======="+installCode);
			}
		});*/

    }

    /**
     * SK인스톨러로 설치한다
     *
     * @param context context
     * @param apk     설치파일
     */
    public static void installSilent(Context context, File apk) {
        Intent intnt = new Intent(Intent.ACTION_VIEW);
        intnt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intnt.setDataAndType(Uri.fromFile(apk), "application/x-skaf-app");
        context.startActivity(intnt);
    }

    /**
     * 설치 요청한다
     *
     * @param context      context
     * @param download_url url
     * @param appId        어플아이디
     * @param appName      어플명
     * @param appIcon      어플아이콘
     * @param appIconUrl   어플아이콘 url
     */
    public static void installSSMPush(Context context, String download_url, String appId, String appName, Bitmap appIcon, String appIconUrl) {
        Intent intent = new Intent();
        intent.setAction("com.sktelecom.ssm.action.ACTION_FORCE_APP_INSTALL");
        intent.putExtra("APK_PATH", download_url);

        Toast.makeText(context, "APK_PATH : " + download_url, Toast.LENGTH_SHORT);

        context.sendBroadcast(intent);
    }


    /**
     * 설치 요청한다
     *
     * @param context      context
     * @param download_url url
     * @param appId        어플아이디
     * @param appName      어플명
     * @param appIcon      어플아이콘
     * @param appIconUrl   어플아이콘 url
     */
    public static void installPush(Context context, String download_url, String appId, String appName, Bitmap appIcon, String appIconUrl) {


        ContentValues values = new ContentValues();
        values.put(Constants.Download.KEY_INSTALL_FLAG, Constants.Download.INST_TYPE_MANUAL);
        values.put(Constants.Download.KEY_DOWNLOAD_URL, download_url);
        values.put(Constants.Download.KEY_APP_ID, appId);
        values.put(Constants.Download.KEY_APP_NAME, appName);
        values.put(Constants.Download.KEY_APP_ICON_URL, appIconUrl);

        ByteArrayOutputStream bos = null;
        try {
            if (appIcon != null) {
                bos = new ByteArrayOutputStream();
                appIcon.compress(CompressFormat.PNG, 100, bos);
                byte[] appIconArr = bos.toByteArray();
                values.put(Constants.Download.KEY_APP_ICON, appIconArr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                }
            }
        }
        Log.e(TAG, "------------------------installPush----------------------------");
        ContentResolver cr = context.getContentResolver();
        cr.insert(Constants.Download.CONTENT_URI, values);
    }

    /**
     * SK인스톨러에 설치요청한다
     *
     * @param context      context
     * @param download_url url
     * @param appId        어플아이디
     * @param appName      어플명
     * @param appIcon      어플아이콘
     * @param appIconUrl   어플아이콘 url
     */
    public static void installSilentPush(Context context, String download_url, String appId, String appName, Bitmap appIcon, String appIconUrl) {
        ContentValues values = new ContentValues();
        values.put(Constants.Download.KEY_INSTALL_FLAG, Constants.Download.INST_TYPE_SILENT);
        values.put(Constants.Download.KEY_DOWNLOAD_URL, download_url);
        values.put(Constants.Download.KEY_APP_ID, appId);
        values.put(Constants.Download.KEY_APP_NAME, appName);
        values.put(Constants.Download.KEY_APP_ICON_URL, appIconUrl);

        ByteArrayOutputStream bos = null;
        try {
            if (appIcon != null) {
                bos = new ByteArrayOutputStream();
                appIcon.compress(CompressFormat.PNG, 100, bos);
                byte[] appIconArr = bos.toByteArray();
                values.put(Constants.Download.KEY_APP_ICON, appIconArr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                }
            }
        }

        ContentResolver cr = context.getContentResolver();
        cr.insert(Constants.Download.CONTENT_URI, values);
    }

    /**
     * 목록을 설치요청한다
     *
     * @param context   context
     * @param appIdList 어플아이디 리스트
     */
    public static void installListPush(Context context, List<String> appIdList) {
        SKTUtil.unRunningInstallService(context);
        for (int i = 0; i < appIdList.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(Constants.Download.KEY_INSTALL_FLAG, Constants.Download.INST_TYPE_SILENT);
            values.put(Constants.Download.KEY_APP_ID, appIdList.get(i));
            ContentResolver cr = context.getContentResolver();
            cr.insert(Constants.Download.CONTENT_URI, values);
        }
    }

    /**
     * 설치서비스를 초기화한다
     *
     * @param context context
     */
    public static void unRunningInstallService(Context context) {
        File bufferDir = new File(Constants.Download.BUFFER_DIR);
        if (bufferDir.exists()) {
            File[] files = bufferDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileUtil.deleteQuietly(files[i]);
            }
        }

        File installDir = new File(Constants.Download.INSTALL_DIR);
        if (installDir.exists()) {
            File[] files = installDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileUtil.deleteQuietly(files[i]);
            }
        }

        ContentResolver cr = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cr.update(Constants.Download.CONTENT_URI, cv, null, null);
    }

    public static void unRunningDownloadService(Context context, String tmpPath) {
        File bufferDir = new File(tmpPath);
        if (bufferDir.exists()) {
            File[] files = bufferDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                FileUtil.deleteQuietly(files[i]);
            }
        }

        ContentResolver cr = context.getContentResolver();
        ContentValues cv = new ContentValues();
        cr.update(Constants.Download.CONTENT_URI, cv, null, null);
    }

    /**
     * 검색 문자열을 체크한다
     *
     * @param content 검색문자열
     * @return 정합성여부
     * @throws SKTException
     */
    public static boolean checkKeyword(String content) throws SKTException {
        if (!StringUtil.checkKeyCode(content)) {
            throw new SKTException("SP-Char not allowed!", Constants.Status.E_KEYCODE);
        }
        return true;
    }

    /**
     * 날짜 문자열을 리턴한다
     *
     * @param source        문자열
     * @param targetPattern 날짜포맷
     * @return
     */
    public static String formatDate(String source, String targetPattern) {
        Date date = DateUtil.formatDate(source, Constants.FMT_DATE_SOURCE);
        if (date != null) {
            return DateUtil.format(date, targetPattern);
        } else {
            return source;
        }
    }

    /**
     * 날짜 문자열을 리턴한다
     *
     * @param source          문자열
     * @param context         context
     * @param targetPatternId 날짜포맷 문자열 아이디
     * @return
     */
    public static String formatDate(String source, Context context, int targetPatternId) {
        Resources res = context.getResources();
        Date date = DateUtil.formatDate(source, Constants.FMT_DATE_SOURCE);
        if (date != null) {
            return DateUtil.format(date, res.getString(targetPatternId));
        } else {
            return source;
        }
    }

    /**
     * 이미지뷰어를 실행한다.
     *
     * @param context  context
     * @param fileName fileName
     * @param docId    docId
     */
    public static void viewSecurityImage(Context context, String fileName, String docId) {
        viewSecurityImage(context, fileName, docId, 1f);
    }

    /**
     * 이미지뷰어를 실행한다
     *
     * @param context  context
     * @param fileName fileName
     * @param params   params
     */
    public static void viewImage(Context context, String fileName, Parameters params) {
        viewImage(context, fileName, params, -1);
    }

    /**
     * 이미지뷰어를 실행한다
     *
     * @param context  context
     * @param fileName fileName
     * @param params   params
     */
    public static void viewCompanyImage(Context context, String fileName, Parameters params) {
        viewCompanyImage(context, fileName, params, -1);
    }

    /**
     * 이미지뷰어를 실행한다
     *
     * @param context  context
     * @param fileName fileName
     * @param docId    docId
     * @param zoom     zoom
     */
    public static void viewSecurityImage(Context context, String fileName, String docId, float zoom) {
//		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_VIEWER) != null) {
        if (docId == null || docId.trim().length() == 0) {
            try {
                throw new SKTException("Image-Transfer Fail!", Constants.Status.E_IMG_VIEW);

            } catch (SKTException e) {
                e.alert(context);
            }
            return;
        }

        final String PARAM_TYPE = "type";
        final String PARAM_FILENAME = "fileName";
        final String PARAM_PARAMS = "params";
        final String PARAM_DOCID = "docId";
        final String PARAM_ZOOM = "zoom";

        Parameters params = new Parameters(Constants.PRIMITIVE_ATTACH_VIEW);
        params.put(PARAM_DOCID, docId);

        Intent intent = new Intent(Constants.Action.IMAGE_VIEW);
        intent.putExtra(PARAM_TYPE, 1);
        intent.putExtra(PARAM_FILENAME, fileName);
        intent.putExtra(PARAM_PARAMS, params);

        if (zoom != -1) {
            intent.putExtra(PARAM_ZOOM, zoom);
        }

        ((Activity) context).startActivityForResult(intent, Constants.REQ_IMAGE);

//		}
		/*else {
			String msg = StringUtil.format(Resource.getString(context, "_E034"), Constants.CoreComponent.APP_NM_VIEWER);
			SKTDialog dlg = new SKTDialog(context);
			dlg.getDialog(msg, new DialogButton(0, context) {
				public void onClick(DialogInterface dialog, int which) {
					SKTUtil.goMobileOffice((Context)getTag(), Constants.CoreComponent.APP_ID_VIEWER);
				}
			}).show();
		}*/
    }

    /**
     * 이미지뷰어를 실행한다
     *
     * @param context  context
     * @param fileName fileName
     * @param docKey   docId
     * @param zoom     zoom
     */
    public static void viewImage(Context context, String fileName, String docKey, float zoom) {
        if (SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_VIEWER) != null) {
            if (docKey == null || docKey.trim().length() == 0) {
                try {
                    throw new SKTException("Image-Transfer Fail!", Constants.Status.E_IMG_VIEW);
                } catch (SKTException e) {
                    e.alert(context);
                }
                return;
            }

            final String PARAM_TYPE = "type";
            final String PARAM_FILENAME = "fileName";
            final String PARAM_PARAMS = "params";
            final String PARAM_DOCKEY = "docKey";
            final String PARAM_ZOOM = "zoom";

            Parameters params = new Parameters(Constants.PRIMITIVE_ORIGINAL_VIEW);
            params.put(PARAM_DOCKEY, docKey);

            Intent intent = new Intent(Constants.Action.IMAGE_VIEW);

            intent.putExtra(PARAM_TYPE, 0);
            intent.putExtra(PARAM_FILENAME, fileName);
            intent.putExtra(PARAM_PARAMS, params);
            if (zoom != -1)
                intent.putExtra(PARAM_ZOOM, zoom);

            ((Activity) context).startActivityForResult(intent, Constants.REQ_IMAGE);

        } else {
            String msg = StringUtil.format(Resource.getString(context, "_E034"), Constants.CoreComponent.APP_NM_VIEWER);
            SKTDialog dlg = new SKTDialog(context);
            dlg.getDialog(msg, new DialogButton(0, context) {
                public void onClick(DialogInterface dialog, int which) {
                    SKTUtil.goMobileOffice((Context) getTag(), Constants.CoreComponent.APP_ID_VIEWER);
                }
            }).show();
        }
    }

    /**
     * 이미지뷰어를 실행한다
     *
     * @param context  context
     * @param fileName fileName
     * @param params   params
     * @param zoom     zoom
     */
    public static void viewImage(Context context, String fileName, Parameters params, float zoom) {
        if (SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_VIEWER) != null) {
            final String PARAM_TYPE = "type";
            final String PARAM_FILENAME = "fileName";
            final String PARAM_PARAMS = "params";
            final String PARAM_ZOOM = "zoom";

            Intent intent = new Intent(Constants.Action.IMAGE_VIEW);
            intent.putExtra(PARAM_TYPE, 0);
            intent.putExtra(PARAM_FILENAME, fileName);
            intent.putExtra(PARAM_PARAMS, params);
            if (zoom != -1)
                intent.putExtra(PARAM_ZOOM, zoom);
            ((Activity) context).startActivityForResult(intent, Constants.REQ_IMAGE);
        } else {
            String msg = StringUtil.format(Resource.getString(context, "_E034"), Constants.CoreComponent.APP_NM_VIEWER);
            SKTDialog dlg = new SKTDialog(context);
            dlg.getDialog(msg, new DialogButton(0, context) {
                public void onClick(DialogInterface dialog, int which) {
                    SKTUtil.goMobileOffice((Context) getTag(), Constants.CoreComponent.APP_ID_VIEWER);
                }
            }).show();
        }
    }

    /**
     * 이미지뷰어를 실행한다
     *
     * @param context  context
     * @param fileName fileName
     * @param params   params
     * @param zoom     zoom
     */
    public static void viewCompanyImage(Context context, String fileName, Parameters params, float zoom) {
        if (SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_VIEWER) != null) {
            final String PARAM_TYPE = "type";
            final String PARAM_FILENAME = "fileName";
            final String PARAM_PARAMS = "params";
            final String PARAM_ZOOM = "zoom";

            Intent intent = new Intent(Constants.Action.IMAGE_VIEW);
            intent.putExtra(PARAM_TYPE, 2);
            intent.putExtra(PARAM_FILENAME, fileName);
            intent.putExtra(PARAM_PARAMS, params);
            if (zoom != -1)
                intent.putExtra(PARAM_ZOOM, zoom);
            ((Activity) context).startActivityForResult(intent, Constants.REQ_IMAGE);
        } else {
            String msg = StringUtil.format(Resource.getString(context, "_E034"), Constants.CoreComponent.APP_NM_VIEWER);
            SKTDialog dlg = new SKTDialog(context);
            dlg.getDialog(msg, new DialogButton(0, context) {
                public void onClick(DialogInterface dialog, int which) {
                    SKTUtil.goMobileOffice((Context) getTag(), Constants.CoreComponent.APP_ID_VIEWER);
                }
            }).show();
        }
    }

    /**
     * URL을 실행한다
     *
     * @param context context
     * @param url     url
     */
    public static void openUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 어플리케이션 실행한다
     *
     * @param context     context
     * @param packageName 패키지명
     */
    public static boolean runApp(Context context, String packageName) {
        Log.i(TAG, "runApp packageName ::: " + packageName);
        Context appContext = context.getApplicationContext();
        PackageManager pm = appContext.getPackageManager();

        SKTUtil.log("TEST", "runApp: " + packageName);
        Intent intent = null;
        if (pm != null) {
            intent = pm.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                return false;
            }
        } else {
            return false;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        return true;
    }

    /**
     * 어플리케이션 실행한다
     *
     * @param context     context
     * @param packageName 패키지명
     */

    public static boolean runAppAsIntent(Context context, String packageName) {
        try {
            Log.d(TAG, "-----------------runAppAsIntent------------------");
            Log.d(TAG, "package name =========== " + packageName);

            Intent intent = new Intent(packageName + ".LAUNCH_MAIN");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);


        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

	/*public static void runMCuvic(Context context){
		Log.i(TAG, "----------------runMCUVIC----------------");
		try{
//			String mCuvicPackage = "com.aircuve.mcuvic";
			String launcherLogin = "com.ex.group.folder.LOGIN";

			Intent intent = new Intent(launcherLogin);

			if(intent != null){
				context.startActivity( intent );
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/


    /**
     * my toktok 폴더를 실행한다.
     * @param context context
     */
	/*public static void runFolder(Context context) {
		Intent intent = new Intent(Constants.Action.FOLDER_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}*/

    /**
     * 톡톡 모바일 사이트로 연결된다
     *
     * @param context context
     */
    public static void goToffice(Context context) {
        openUrl(context, Constants.URL_T_OFFICE);
    }

    /**
     * 스토어를 실행한다
     *
     * @param context context
     */
    public static void goMobileOffice(Context context) {
        runApp(context, Constants.PKG_T_OFFICE);
    }

    /**
     * 스토어를 실행한다
     *
     * @param context context
     * @param appId   어플아이디
     */
    public static void goMobileOffice(Context context, String appId) {
        /* PE에서 사용하는 방식 코드삭제하지 않음. 참조. 파라미터 : Constants.KEY_DOWNLOAD_APPID */
		/*
		final String PARAM_APPID = "DOWNLOAD_APPID";

        Context appContext = context.getApplicationContext();
        PackageManager pm = appContext.getPackageManager();

        Intent intent = null;
        if(pm != null) {
        	intent = pm.getLaunchIntentForPackage(Constants.PKG_T_OFFICE);
        	if(intent == null) {
                   return;
        	}
        } else {
        	return;
        }

        intent.putExtra(PARAM_APPID, appId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        */

        Intent intent = new Intent(Constants.Action.STORE_DETAIL_VIEW);
        intent.putExtra(Constants.KEY_APP_ID, appId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    /**
     * 스토어를 실행한다
     *
     * @param context context
     * @param appId   어플아이디
     */
    public static void goMobileOfficeWithExtra(Context context, String appId, String packageName) {
        Log.i(TAG, "goMobileOfficeWithExtra");
        /* PE에서 사용하는 방식 코드삭제하지 않음. 참조. 파라미터 : Constants.KEY_DOWNLOAD_APPID */
		/*
		final String PARAM_APPID = "DOWNLOAD_APPID";

        Context appContext = context.getApplicationContext();
        PackageManager pm = appContext.getPackageManager();

        Intent intent = null;
        if(pm != null) {
        	intent = pm.getLaunchIntentForPackage(Constants.PKG_T_OFFICE);
        	if(intent == null) {
                   return;
        	}
        } else {
        	return;
        }

        intent.putExtra(PARAM_APPID, appId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        */

        Intent intent = new Intent(Constants.Action.STORE_DETAIL_VIEW);
        intent.putExtra(Constants.KEY_APP_ID, appId);
        intent.putExtra("FROM_LAUNCHER", "Y");
        intent.putExtra("PACKAGE_NAME", packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }


    public static void goStore(Context context, String appId, String packageName, String serverAppVer, String currAppVer) {
        Log.i(TAG, "goStore===========" + packageName + serverAppVer + currAppVer);
        Intent intent = new Intent(Constants.Action.STORE_DETAIL_VIEW);
        intent.putExtra(Constants.KEY_APP_ID, appId);
        intent.putExtra("FROM_LAUNCHER", "Y");
        intent.putExtra("PACKAGE_NAME", packageName);
        intent.putExtra("APPVER", serverAppVer);            //서버에 올라가 있는 최신 앱 버전
        intent.putExtra("currentVer", currAppVer);            //단말기에 설치 되어있는 앱 버전
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    /**
     * 메일쓰기를 실행한다
     *
     * @param context context
     * @param email   이메일주소
     */
    public static void runMailWrite(Context context, String email) {
        Intent intent = new Intent(Constants.Action.EMAIL_WRITE);
        intent.putExtra(Constants.KEY_EMAIL_TO, email);
        context.startActivity(intent);
    }

    /**
     * 투데이를 실행한다
     *
     * @param context context
     * @param id      투데이아이디
     */
    public static void runToday(Context context, String id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName("com.gw.sk.today", "com.gw.sk.today.SK_TodayDetail"));
        intent.putExtra(Constants.KEY_TODAY_ID, id);
        context.startActivity(intent);
    }

    /**
     * 다운로드 URL을 얻는다
     *
     * @param context context
     * @param url     url
     * @return 다운로드 URL
     * @throws SKTException
     */
    public static String getDownUrl(Context context, String url) throws SKTException {
        Environ environ = EnvironManager.getEnviron(context);
        String realUrl = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort();
        String downUrl = Environ.PROTOCOL + "://" + Environ.HOST + ":" + Environ.PORT;

        return url.replaceAll(realUrl, downUrl);
    }

    /**
     * 타임스탬프 로그를 찍는다
     *
     * @param context   context
     * @param startTime 시작시간
     * @param primitive 액션명
     * @param msg       문자열
     * @return 타임스탬프
     */
    public static long timestamp(Context context, long startTime, String primitive, String msg) {
        long t = System.currentTimeMillis();

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(t);

            String mdn = EnvironManager.getEnviron(context).getMdn();

            DateUtil.format(cal.getTime(), "yyyy.MM.dd HH:mm:ss.SSS");

            Log.i(primitive + " > " + mdn + " > " + msg, "timestamp > " + DateUtil.format(cal.getTime(), "yyyy.MM.dd HH:mm:ss.SSS") + " / " + (t - startTime));
        } catch (SKTException e) {
        }

        return System.currentTimeMillis();
    }

    /**
     * 키보드를 숨긴다
     *
     * @param a_oView view
     * @return 키보드숨김여부
     */
    public static boolean hideKeyboard(View a_oView) {
        InputMethodManager imm = (InputMethodManager) a_oView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(a_oView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showKeyboard(View a_oView) {
        InputMethodManager imm = (InputMethodManager) a_oView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        return imm.showSoftInput(a_oView, InputMethodManager.SHOW_FORCED);
//        imm.showSoftInputFromInputMethod(a_oView.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);
        imm.toggleSoftInputFromWindow(a_oView.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    // 키보드를 토글한다.
//    public static void toggleKeyboard(Context context) {
//    	InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//    	m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//    }

    /**
     * 키보드를 숨긴다
     *
     * @param activity activity
     */
    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public static boolean isRunningProcess(Context context, String packageName) {

        boolean isRunning = false;

        ActivityManager actMng = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> list = actMng.getRunningAppProcesses();

        for (RunningAppProcessInfo rap : list) {
            if (rap.processName.equals(packageName)) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    public static int getIMEIAccess(Context context) {
        int certify = 3;

        try {
//    		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.sktelecom.ssm.provider.SSMProvider/ssm"), null, null, null, null);
//        	cursor.moveToFirst();
//    		certify = cursor.getInt(cursor.getColumnIndex("CERTIFY"));
//    		SKTUtil.log("IMEICHECK", "certify = " + certify);com.sktelecom.ssm_go
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.sktelecom.ssm.provider.SSMProvider/ssm"), null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    certify = cursor.getInt(cursor.getColumnIndex("CERTIFY"));
                }

                cursor.close();
            }
            SKTUtil.log("IMEICHECK", "certify = " + certify);
        } catch (Exception e) {
            e.printStackTrace();
            SKTUtil.log("IMEICHECK", "e message = " + e.getMessage());
        }

        return certify;
    }

    public static int getV3Status(Context context) {
        int certify = 4;

        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.sktelecom.ssm.provider.SSMProvider/v3"), null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    certify = cursor.getInt(cursor.getColumnIndex("CERTIFY"));
                }

                cursor.close();
            }
            SKTUtil.log("V3CHECK", "certify = " + certify);
        } catch (Exception e) {
            e.printStackTrace();
            SKTUtil.log("V3CHECK", "e message = " + e.getMessage());
        }

        return certify;
    }

  /*  public static class installTask extends AsyncTask<String, Void, Boolean>{
    	
    	String apk_path;
    	
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				Thread.sleep(5000);

				apk_path = arg0[0];
				Log.e(TAG, "apk_PATH++++++++++++++++++"+apk_path);
				int code = ssmLib.forceAppInstall(apk_path);
				Log.e(TAG, "BackGround install result code : "+code);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			return null;
		}
    }
    */
/*    
@Override
	public void onSSMConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSSMInstalled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSSMRemoved() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSSMResult(String arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}*/
}
