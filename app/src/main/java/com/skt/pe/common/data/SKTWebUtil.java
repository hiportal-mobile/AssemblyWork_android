package com.skt.pe.common.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.Resource;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.util.Base64Util;
import com.skt.pe.util.StringUtil;

/**
 * 웹뷰 유틸리티 클래스
 * @author june
 *
 */
public class SKTWebUtil {
	public static final String BASE_URL    = "x-data://base/";

	private static final FrameLayout.LayoutParams ZOOM_PARAMS = new FrameLayout.LayoutParams(   
			ViewGroup.LayoutParams.FILL_PARENT,   
			ViewGroup.LayoutParams.WRAP_CONTENT,   
			Gravity.BOTTOM   
	);   
	
	public static View zoom = null;

	/**
	 * 웹뷰를 리프레쉬한다.
	 * @param webView 웹뷰
	 * @param autoImageFlag 이미지자동로드여부
	 */
	public static void invalidate(WebView webView, boolean autoImageFlag) {
		WebSettings settings = webView.getSettings();
		settings.setLoadsImagesAutomatically(autoImageFlag);
		webView.invalidate();
	}

	/**
	 * 내용을 웹뷰에 로드한다.
	 * @param context context
	 * @param webView 웹뷰
	 * @param body 내용
	 * @throws SKTException
	 */
	public static void loadWebView(Context context, WebView webView, String body) throws SKTException {
		loadWebView(context, webView, true, body, true);
	}

	/**
	 * 내용을 웹뷰에 로드한다.
	 * @param context cotext
	 * @param webView 웹뷰
	 * @param htmlFlag html여부
	 * @param body 내용
	 * @param autoImageFlag 이미지자동로드여부
	 * @throws SKTException
	 */
	public static void loadWebView(Context context, WebView webView, boolean htmlFlag, String body, boolean autoImageFlag) throws SKTException {
		// Setting
		WebSettings settings = webView.getSettings();
		settings.setLoadsImagesAutomatically(autoImageFlag);
		settings.setSavePassword(false); 
        settings.setSaveFormData(false);
		settings.setJavaScriptEnabled(false);
		settings.setSupportZoom(true);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 	
//		settings.setBuiltInZoomControls(true);
		
		webView.setWebViewClient(new SKTWebViewClient(context));
		//webView.setWebChromeClient(new SKTWebViewClient(context));
		
		FrameLayout mContentView = (FrameLayout)((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
		// zoom = webView.getZoomControls();
//		검색을 해보니 이것을 해야지 어떤 오류가 안난다고 했는데 이것 안해도 지금껏 오류가 난적이 없음
//		mContentView.removeView(zoom);
		if(mContentView.indexOfChild(zoom) == -1) {;
			mContentView.addView(zoom, ZOOM_PARAMS);
		}
		zoom.setVisibility(View.GONE);

		String mimeType = htmlFlag ? "text/html" : "text/plain";
		webView.loadDataWithBaseURL(BASE_URL, body, mimeType, "utf-8", null);
	}

	/**
	 * 내용을 웹뷰에 로드한다. -- 확대하지 않은 버전
	 * @param context cotext
	 * @param webView 웹뷰
	 * @param htmlFlag html여부
	 * @param body 내용
	 * @param autoImageFlag 이미지자동로드여부
	 * @throws SKTException
	 */
	public static void loadWebViewWithoutZoom(Context context, WebView webView, boolean htmlFlag, String body, boolean autoImageFlag) throws SKTException {
		// Setting
		WebSettings settings = webView.getSettings();
		settings.setLoadsImagesAutomatically(autoImageFlag);
		settings.setSavePassword(false); 
        settings.setSaveFormData(false);
		settings.setJavaScriptEnabled(false);
		settings.setSupportZoom(false);
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 	
//		settings.setBuiltInZoomControls(true);
		
		webView.setWebViewClient(new SKTWebViewClient(context));
		//webView.setWebChromeClient(new SKTWebViewClient(context));
		
//		FrameLayout mContentView = (FrameLayout)((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content);
//		zoom = webView.getZoomControls();
//		if(mContentView.indexOfChild(zoom) == -1) {;
//			mContentView.addView(zoom, ZOOM_PARAMS);
//		}
//		zoom.setVisibility(View.GONE);

		String mimeType = htmlFlag ? "text/html" : "text/plain";
		webView.loadDataWithBaseURL(BASE_URL, body, mimeType, "utf-8", null);
	}

	
	/**
	 * 인라인 태그 및 이미지를 파싱하여 내용을 리턴한다.
	 * @param context context
	 * @param webView 웹뷰
	 * @param body 내용
	 * @param attachMap 첨부목록
	 * @param ecmCheckFlag ecm체크여부
	 * @return 결과문자열
	 * @throws SKTException
	 */
	public static String prepareWebView(Context context, WebView webView, String body, Map<String,String> attachMap, boolean ecmCheckFlag) throws SKTException {
		final String CID_PREFIX  = "cid:";
		final String ECM_PREFIX  = "http://ecm.sktelecom.com";
		final String ECM_SUFFIX  = "</a>"; 
		final String ECM_COMMENT = "<br><font color='#4A9DD3'>(ECM 문서입니다. 첨부파일 버튼을 클릭하세요.)</font>";
		//final String ECM_COMMENT = "<br><a href='http://andappstore.com/AndroidApplications/apps/662080!getApp'><font color='#4A9DD3'>(ECM 문서입니다. 첨부파일 버튼을 클릭하세요.)</font></a>";

		String dataDir = "";
		try {
			PackageInfo pack = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			ApplicationInfo app = pack.applicationInfo;
			dataDir = app.dataDir; 
		} catch(NameNotFoundException e) { }

		// 0. 임시파일 삭제
		clearApplicationCache(context, null);
		String[] list = context.fileList();
		if(list != null) {
			for(String file : list) {
				context.deleteFile(file);
			}
		}
		webView.clearCache(true);

		// 1. 인라인 태그 처리
		if(attachMap != null) {
			Iterator<String> it = attachMap.keySet().iterator();

			while(it.hasNext()) {
				String cid = it.next();
				String en_img = attachMap.get(cid);

				int offset = cid.indexOf("@");
				String imgName = cid;
				if(offset != -1)
					imgName = cid.substring(0, offset);

				FileOutputStream fos  = null;
				try {
					fos = context.openFileOutput(imgName, Context.MODE_PRIVATE);
					//fos = context.openFileOutput(imgName, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
					byte[] de_img = Base64Util.decodeBinay(en_img);
					fos.write(de_img);
				} catch(Exception e) {
					Log.e("file", e.getMessage());
				} finally {
					if(fos != null) {
						try {
							fos.close();
						} catch(Exception e) { }
					}
				}

				body = body.replaceAll(CID_PREFIX + cid, "file://" + dataDir + "/files/" + imgName);
			}
		}

		// 2. ECM 처리
		if(ecmCheckFlag) {
			StringBuffer b_body = new StringBuffer(body);
			int offset = 0;
			while(offset != -1) {
				offset = b_body.indexOf(ECM_PREFIX);
				if(offset != -1) {
					int z_offset = b_body.indexOf(ECM_SUFFIX, offset);
					if(z_offset != -1) {
						b_body.replace(offset, z_offset, ECM_COMMENT);
						offset = z_offset;
					} else {
						offset = -1;
					}
				}
			}
			body = b_body.toString();
		}

		return body;
	}

	/**
	 * 캐쉬를 삭제한다
	 * @param context context
	 * @param dir 캐쉬폴더
	 */
    public static void clearApplicationCache(Context context, File dir){
        if(dir==null)
            dir = context.getCacheDir();

        if(dir==null)
            return;

        File[] children = dir.listFiles();
        try{
            for(int i=0; i<children.length; i++) {
                if(children[i].isDirectory())
                	clearApplicationCache(context, children[i]);
                else children[i].delete();
            }
        }
        catch(Exception e){}
    }

    /**
     * 웹뷰 클라이언트 클래스
     * @author june
     *
     */
    private static class SKTWebViewClient extends WebViewClient {

    	private Context context = null;

    	public SKTWebViewClient(Context context) {
    		this.context = context;
    	}

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	Log.d("WebView", url);
        	if(url.startsWith(WebView.SCHEME_TEL)) {
        		//TODO
        		return true;
        	} else if (url.startsWith(WebView.SCHEME_MAILTO)) {
        		url = url.replaceAll(WebView.SCHEME_MAILTO, "");

        		if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_EMAIL) != null) {
        			SKTUtil.runMailWrite(context, url);
        		} else {
        			String msg = StringUtil.format(Resource.getString(context, "_E034"), Constants.CoreComponent.APP_NM_EMAIL);

    				SKTDialog dlg = new SKTDialog(context);
    				dlg.getDialog(msg, new DialogButton(0) {
    					public void onClick(DialogInterface dialog, int which) {
    						SKTUtil.goMobileOffice(context, Constants.CoreComponent.APP_ID_EMAIL);
    						SKTUtil.closeApp(context);
    					}
    				}).show();
        		}
        		return true;
        	} else if (url.startsWith(WebView.SCHEME_GEO)) {
        		//TODO
        		return true;
        	} else {
        		int offset = url.indexOf(Constants.PREFIX_TODAY);

        		if(offset != -1) {
        			String id = url.substring(offset + Constants.PREFIX_TODAY.length());
        			SKTUtil.runToday(context, id);
        			return true;
        		} else {
            		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            		Log.d("WebView", "extension[" + extension + "]");
            		Log.d("WebView", "mimeType[" + mimeType + "]");

            		if (mimeType != null) {				
            			return shouldHandleMimeType(mimeType, url);
            		} else {
            			StringBuffer sb = new StringBuffer(url); 
            			if(sb.indexOf(BASE_URL) == 0) {
            				sb.replace(0, BASE_URL.length(), Environ.HTTP +"://");
            			}
            			
            			Intent i = new Intent(Intent.ACTION_VIEW); 
            			Uri u = Uri.parse(sb.toString());  
            			i.setData(u); 
            			context.startActivity(i);
            			return true;
            		}
            		//return super.shouldOverrideUrlLoading(view, url);        			
        		}
        	}
        } 

    	private boolean shouldHandleMimeType(String mimeType, String url) {
    		//TODO
    		if (mimeType.startsWith("video/")) {			
    			return true;		
    		}		
    		return false;	
    	}
   } 

}
