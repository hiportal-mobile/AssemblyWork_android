package com.ex.group.aw.view;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import handyhis.dz.viewer.DzImageViewer.DzImageViewer;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.RequestMstManager;
import com.ex.group.aw.manager.UserInfoManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.RequestMstInfo;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;

/*
	화면명: 자료요청리스트
	작성자: 방종희
	DESC: 
	DATE: 2013.04.16
	VERSION: 0.1
 */
@SuppressLint("NewApi")
public class Aw05Activity extends Activity implements OnClickListener {

	private final String LOGTAG = "Aw05Activity";
	WebView webview;

	
	@Override
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(LOGTAG, "called onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_05_webview);

		webview = (WebView) findViewById(R.id.webview);
		
		//webview.clearCache(true); // WebView 사용 시 캐시 제거 구문1
		//webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // WebView 사용 시 캐시 제거 구문2
		webview.getSettings().setJavaScriptEnabled(true);
		// webview.addJavascriptInterface(new
		// JoinWebPhoneActivity.JavascriptInterface(this), "smsInterface"); // 서버 쪽에서
		// window.ipinInterface.setUserInfo(<%=rtn%>); 이런 식으로 호출
//		webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
//		webview.setInitialScale(getScale());
		webview.getSettings().setLoadWithOverviewMode(true); // WebView 화면크기에 맞춰주는 구문1 - 반드시 구문2와 같이 써야 맞춰짐
		webview.getSettings().setUseWideViewPort(true); // WebView 화면크기에 맞춰주는 구문2 - 반드시 구문1과 같이 써야 맞춰짐
		webview.getSettings().setBuiltInZoomControls(true); // 화면 확대/축소 버튼 여부
		webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
		webview.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND); // 플러그인을 사용할 수 있도록 설정
		webview.getSettings().setSupportMultipleWindows(true); // 여러개의 윈도우를 사용할 수 있도록 설정
		webview.getSettings().setLightTouchEnabled(true);
		webview.getSettings().setSavePassword(true);
		webview.getSettings().setSaveFormData(true);
		
		webview.setWebContentsDebuggingEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		
		webview.setWebChromeClient(new WebChromeClient()); // alert 창이 뜰 수 있도록 설정
		webview.setWebViewClient(new WebClient());
		webview.getSettings().setDefaultTextEncodingName("utf-8");
		webview.addJavascriptInterface(new WebClient(), "eventDetail");
		
		// webview.loadUrl("http://192.168.0.61:8080/mxstart.jsp?p_amt="+price+"&p_uname="+common.getPrefString(NfcCardNapbuActivity.this,
		// Configuration.SHARED_USER_NAME));

		//webview.loadUrl( "http://192.168.0.188:8080/assembleviewlist.jsp?primitive=AW_MO_ASSEMBLELIST&searchKeyword=&searchCode=&bscode=N02145");

//		webview.loadUrl( "http://192.168.0.188:8080/assembleviewlist.jsp?primitive=AW_MO_ASSEMBLEVIEWLIST&searchKeyword=&searchCode=&bscode=N02145");
		
		String user_id = "";
		Map<String,String> gmpAuth = null;
		try {
			gmpAuth = SKTUtil.getGMPAuth(this);
			
			user_id = gmpAuth.get(AuthData.ID_ID);
			
		} catch (SKTException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다."+err);
		}
//		user_id = "21714624";//테스트 testN02145
		webview.loadUrl( "http://128.200.121.68:9000/emp_ex/service.pe?primitive=AW_MO_ASSEMBLEVIEWLIST&searchKeyword=&searchCode=&bscode=&userId="+user_id);//운영
	}
	
	class WebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		
		 @JavascriptInterface
	        public boolean successPage(String aaa){

	            setResult(RESULT_OK);
	            finish();
	            return true;
	        }

		 @JavascriptInterface
	        public void  goAttach(String file_name, String file_path){
			 System.out.println("goAttach = "+file_path+file_name);
				if(!file_name.equals("")){
//					DzImageViewer(file_name ,file_path);
					file_path = file_path.replace("/", "\\");
					DocuzenViewLauncher((Activity)Aw05Activity.this, file_name ,file_path);
				}
	        }
		 
		

	        @JavascriptInterface
	        public void backPage(String resultMsg)
	        {
	        	try {
	        		if(webview.canGoBack()) {
		    			webview.goBack();
		    		}else {
		    			onBackPressed();	
		    		}	
				} catch (Exception e) {
					finish();
				}
	        	
	        }
	        
	        
	        @JavascriptInterface
	        public void backPage()
	        {
	        	try {
	        		if(webview.canGoBack()) {
		    			webview.goBack();
		    		}else {
		    			finish();
		    		}	
				} catch (Exception e) {
					finish();
				}
	        }
	        
	        
	}
	public static void DocuzenViewLauncher(Activity activity, String file_name, String file_url) {
		Intent intent = new Intent(activity, handyhis.dz.viewer.DzImageViewer.DzImageViewer.class);

		Log.d("DocuzenViewLauncher","DocuzenViewLauncher file_url = " + file_url);
		Log.d("DocuzenViewLauncher","DocuzenViewLauncher file_name = " + file_name);


		try{
			file_url = "\\\\" + file_url;
			String ext = file_name.substring(file_name.lastIndexOf("."));
			long tmp = System.currentTimeMillis();
			String tmpName = String.valueOf(tmp) + ext;
			String url = Global.DZCSURLPREFIX;
			url += "fileName=" + URLEncoder.encode(tmpName, "euc-kr") + "&filePath=" + URLEncoder.encode(file_url, "euc-kr")+file_name;

			intent.putExtra("baseDzcsUrl", Global.DZCSURL);
			intent.putExtra("URL", url);
			
			Log.d("DocuzenViewLauncher","DocuzenViewLauncher base = " + Global.DZCSURL);
			Log.d("DocuzenViewLauncher","DocuzenViewLauncher url = " + URLDecoder.decode(url, "euc-kr"));
			
			activity.startActivity(intent);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	문서뷰어 연동 
	public void DzImageViewer(String fileName ,String filePath){
		
		Intent intent = new Intent(Aw05Activity.this, handyhis.dz.viewer.DzImageViewer.DzImageViewer.class);
	   String ext = fileName.substring(fileName.lastIndexOf("."));
	   long tmp = System.currentTimeMillis();
	   
	   String tmpName = String.valueOf(tmp) + ext;
	   Log.i(LOGTAG, tmpName);
	   
//	   filePath = "http://nams.ex.co.kr:8020/nams/comm/attach/attachBbsDown.do?atch_file_sno=4764&atch_file_no=1";
	   String temp = Global.DZCSURLPREFIX;
	   temp += "fileName=" + URLEncoder.encode(fileName) + "&filePath=" + URLEncoder.encode("http://hiportal.ex.co.kr/hiportal/board/BBS_1/2019/09/30/20190930113947_92297.hwp");
	   Log.i(LOGTAG, temp);
	   Log.i(LOGTAG, "doc server is " + Global.DZCSURL);
	   
//	   toiphoneapp://callDocumentFunction?fileName=1568093699594.hwp&filePath=http%3A%2F%2Fhiportal.ex.co.kr%2Fhiportal%2Fboard%2FBBS_1%2F2019%2F09%2F10%2F20190910091507_4329.hwp
	   intent.putExtra("baseDzcsUrl", Global.DZCSURL);
	   intent.putExtra("URL", temp);
	   
	   Log.i(LOGTAG, "URL = " + URLDecoder.decode(temp));
	   
//	   toiphoneapp://callDocumentFunction?fileName=1564555945646.hwp&filePath=%2FEAI_FILE7%2Faams%2Frequest%2Fhwp%2F201907
//	   toiphoneapp://callDocumentFunction?fileName=1569902914382.hwp&filePath=\\192.168.53.10//EAI_FILE7/aams/request/hwp/201909/
	   startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {

		String click_url = webview.getUrl();
		if(click_url.indexOf("ASSEMBLEVIEWLIST&") > -1){
			webview.loadUrl("javascript:localStorage.clear();");
		}

		if(webview.canGoBack()) {
			webview.goBack();
		}else {
			super.onBackPressed();	
		}
		
	}

	@Override
	protected void onDestroy() {
		Log.d(LOGTAG, "called onDestroy");
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

	}
}
