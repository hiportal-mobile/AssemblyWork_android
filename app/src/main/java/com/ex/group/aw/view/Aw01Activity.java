package com.ex.group.aw.view;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.UserInfoManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;

/*
	화면명: 국회 메인 
	작성자: 방종희
	DESC: 
	DATE: 2013.04.11
	VERSION: 0.1
*/
public class Aw01Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw01Activity";
	private Button mBtnCongress;
	private Button mBtnSmart;
	private Button mBtnRequest;
	private Button mBtnSearch;
	private boolean m_Flag = false;
	private AwCustomPopup popup;
	
	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_01);
		
		Map<String,String> gmpAuth = null;
		try {
			gmpAuth = SKTUtil.getGMPAuth(this);
			
			Global.LOGIN_ID = gmpAuth.get(AuthData.ID_ID);
			
		} catch (SKTException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다."+err);
		}
		
		
		Log.i(LOGTAG, "LOGIN_ID = "+Global.LOGIN_ID);
		mBtnCongress = (Button)findViewById(R.id.btn_aw_01_cngrssmn);
		mBtnCongress.setOnClickListener(this);
		
		mBtnSmart = (Button)findViewById(R.id.btn_aw_01_smart);
		mBtnSmart.setOnClickListener(this);
		
		mBtnRequest = (Button)findViewById(R.id.btn_aw_01_request);
		mBtnRequest.setOnClickListener(this);
		
		mBtnSearch = (Button)findViewById(R.id.btn_aw_01_search);
		mBtnSearch.setOnClickListener(this);
		
		Button btn_lev1	=	(Button)findViewById(R.id.btn_lev1);
		btn_lev1.setOnClickListener(this);
		Button btn_lev2	=	(Button)findViewById(R.id.btn_lev2);
		btn_lev2.setOnClickListener(this);
//		Button btn_lev3	=	(Button)findViewById(R.id.btn_lev3);
//		btn_lev3.setOnClickListener(this);
//		Button btn_lev4	=	(Button)findViewById(R.id.btn_lev4);
//		btn_lev4.setOnClickListener(this);
		
		 getData();
	}
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{
			case Global.MSG_FAILED:
			{
				String temp = "";
				switch(msg.arg1)
				{
				case Global.HM_ERR_NETWORK:
					temp = getApplication().getString(R.string.s_hm_network);
					break;
				case Global.HM_ERR_XML_PARSING:
					temp = getApplication().getString(R.string.s_hm_xml_parsing);
					break;
				case Global.HM_ERR_XML_RESULT:
					temp = getApplication().getString(R.string.s_hm_xml_result);
					break;
				}
				AlertDialog(temp);
			}
			break;
			case Global.MSG_SUCEEDED:
			{
			}
			case 10:
			{
				m_Flag = false;
			}
			break;
			}

			if( mDlgNetwork.isShowing())
			{
				mDlgNetwork.dismiss();
			}
		}

	};
	
	public void AlertDialog(String temp) 
	{

		popup = new AwCustomPopup(this);

		popup.mTvTitle.setText("Error");
		popup.mTvContents.setText(temp);
		popup.mBtnCancel.setVisibility(View.GONE);
		popup.mBtnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
				finish();
			}		
		});

		popup.show();
	}
	
	public void AlertDialog2(String temp) 
	{

		popup = new AwCustomPopup(this);

		popup.mTvTitle.setText("");
		popup.mTvContents.setText(temp);
		popup.mBtnCancel.setVisibility(View.GONE);
		popup.mBtnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
				//finish();
			}		
		});

		popup.show();
	}
	//사용자정보 
	public void getData(){
		
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw01Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		
		String temp = Global.SERVER_URL; //+ "/" + Global.JSP_USET_INFO;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_USET_INFO));
		list.add(new SendQueue("login_id",Global.LOGIN_ID));

		Log.i(LOGTAG, temp);
		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
			public void run()
			{
				http.SendData();

				String result = http.rcvString.replace("\n", "");

				Log.i(LOGTAG, "result : "+result);
				
				Message msg = mHandler.obtainMessage();
				if( result.equals("N/A"))
				{
					msg.what = Global.MSG_FAILED;
					msg.arg1 = Global.HM_ERR_NETWORK;
					mHandler.sendMessage(msg);
					return;
				}
				else
				{

					XmlParserManager.parsingLoginUserList(getApplicationContext(), result);

					if( UserInfoManager.getInstance() == null )
					{
						Log.i(LOGTAG, "manager is memory allocation failed");
						msg.what = Global.MSG_FAILED;
						msg.arg1 = Global.HM_ERR_XML_PARSING;
						mHandler.sendMessage(msg);
						return;

					}else{
						Log.i(LOGTAG, "UserInfoManager is not null \nresult code : "+UserInfoManager.getInstance().result_code);
						if( UserInfoManager.getInstance().result_code == 1000)
						{
							msg.what = Global.MSG_SUCEEDED;

						}
						else
						{
							msg.what = Global.MSG_FAILED;
							msg.arg1 = Global.HM_ERR_XML_RESULT;
						}
						mHandler.sendMessage(msg);
						return;
					}
				}
			}
		},LOGTAG+"thread");
		thread.setDaemon(true);
		thread.start();
	}
	
	
	@Override
	public void onClick(View v)
	{
		Intent intent = null;
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_aw_01_cngrssmn:
			//	서버에서 국회의원 관련 정보를 가져온다.
			{
				if(UserInfoManager.getInstance().auth_code.equals("")){
					AlertDialog2("사용 권한이 없습니다.");
				}else{
					startActivity(new Intent(Aw01Activity.this, Aw02Activity.class));
				}
				
			}
			break;
		case R.id.btn_aw_01_smart:
			{
				
				if(UserInfoManager.getInstance().auth_code.equals("")){
					AlertDialog2("사용 권한이 없습니다.");
				}else{
					if(!UserInfoManager.getInstance().smart_yn.equals("Y")){
						String temp	=	"사용 권한이 없습니다. ";
						AlertDialog2(temp) ;
					}else{
						startActivity(new Intent(Aw01Activity.this, Aw04Activity.class));					
					}

				}
				
//				startActivity(new Intent(Aw01Activity.this, Aw04Activity.class));		
			}
			break;
		case R.id.btn_aw_01_request:
			{
				if(UserInfoManager.getInstance().auth_code.equals("")){
					AlertDialog2("사용 권한이 없습니다.");
				}else{
				startActivity(new Intent(Aw01Activity.this, Aw05Activity.class));
				}
			}
			break;
		case R.id.btn_aw_01_search:
			{
				if(UserInfoManager.getInstance().auth_code.equals("")){
					AlertDialog2("사용 권한이 없습니다.");
				}else{
				startActivity(new Intent(Aw01Activity.this, Aw16Activity.class));
				}
			}
			break;
//		case R.id.btn_lev1:
//			{
//				Global.LOGIN_ID	="20500410";	
//				getData();
//			}
//			break;
//		case R.id.btn_lev2:
//			{
//				Global.LOGIN_ID = "20711512";
//				getData();
//			}
//			break;
//		case R.id.btn_lev3:
//			{
//				Global.LOGIN_ID = "20602819";
//				getData();
//			}
//			break;
//		case R.id.btn_lev4: //스마트 담당자 
//			{
//				Global.LOGIN_ID = "21103416";
//				getData();
//			}
//			break;
		}
		
		
	}
	
	
}
