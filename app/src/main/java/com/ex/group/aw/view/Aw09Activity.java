package com.ex.group.aw.view;

import java.util.ArrayList;

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
import android.widget.EditText;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.RejectWriteManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;

/*
	화면명: 작성자지정 > 재검토요청
	작성자: 방종희
	DESC: 
	DATE: 2013.04.18
	VERSION: 0.1

	Request params: 
 * login_id - 접속자ID
 * task_mas_uid - 국회업무마스터 키
 * task_dist_main_uid - 주무자 배부정보 키
 * main_rej_memo - 주무자 재검토요청 메모

	Response params :
 * result - 결과코드
 * resultMessage - 결과메세지
 */
public class Aw09Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw09Activity";
	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private RejectWriteManager mManager;
	private Bundle extra;

	//layout item 
	private EditText mEt_aw_09_dtl;		// 재검토요청사항
	private Button mBtn_aw_09_cancel;	// 취소
	private Button mBtn_aw_09_ok;		// 확인

	//전달해줄 변수 
	private String mTask_mas_uid;						//	국회업무 마스터 키
	private String mTask_dist_main_uid;					//	주무자 배부정보 키
	private String mMain_rej_memo;						//	검토요청사항 
	private AwCustomPopup popup;


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
				Log.i(LOGTAG, "result"+mManager.result_code);
				startActivity(new Intent(Aw09Activity.this,Aw05Activity.class));
				finish();
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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_09);

		extra = getIntent().getExtras();
		mTask_mas_uid	=	extra.getString("task_mas_uid");							//	국회업무 마스터 키
		mTask_dist_main_uid=	extra.getString("task_dist_main_uid");					//	주무자 배부정보 키


		mEt_aw_09_dtl		=	(EditText)findViewById(R.id.et_aw_09_dtl);
		mBtn_aw_09_cancel	=	(Button)findViewById(R.id.btn_aw_09_cancel);
		mBtn_aw_09_ok		=	(Button)findViewById(R.id.btn_aw_09_ok);

		mBtn_aw_09_cancel.setOnClickListener(this);
		mBtn_aw_09_ok.setOnClickListener(this);

	}

	public void sendData(){

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw09Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		//	서비스 호출 
		String temp = Global.SERVER_URL;	// + "/" + Global.JSP_REJECT_WRITER;
		Log.i(LOGTAG, temp);

		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_REJECT_WRITER));
		list.add(new SendQueue("login_id", Global.LOGIN_ID));
		list.add(new SendQueue("task_mas_uid", mTask_mas_uid));
		list.add(new SendQueue("task_dist_main_uid", mTask_dist_main_uid));
		list.add(new SendQueue("main_rej_memo", mMain_rej_memo));



		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
			public void run()
			{
				http.SendData();

				String result = http.rcvString;

				Log.i(LOGTAG, result);
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


					mManager = XmlParserManager.parsingRejectWrite(getApplicationContext(), result);

					if( mManager == null )
					{
						Log.i(LOGTAG, "manager is memory allocation failed");
						msg.what = Global.MSG_FAILED;
						msg.arg1 = Global.HM_ERR_XML_PARSING;
						mHandler.sendMessage(msg);
						return;

					}else{

						if( mManager.result_code == 1000)
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_aw_09_cancel:
			finish();
			break;
		case R.id.btn_aw_09_ok:

			mMain_rej_memo	=	mEt_aw_09_dtl.getText().toString(); 	//	요청내용
			if(mMain_rej_memo.equals("")){
				Toast toast	=	Toast.makeText(this, "요청내용을 입력해주세요.", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			sendData();
			break;

		default:
			break;
		}

	}

}
