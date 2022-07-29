package com.ex.group.aw.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.SmImageListManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;

public class Aw04AnswerActivity extends Activity implements OnClickListener {


	private AwCustomPopup popup;
	private final static String LOGTAG = "Aw04AnswerActivity";
	private Button mBtnPrev	;
	private String mMasterDocId	=	"";
	

	private ProgressDialog 	mDlgNetwork;
	private SmImageListManager mManager	=	null;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			Log.i(LOGTAG, "Login message[" + msg.what + "]");

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
				setData();
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_04_answer);
		mBtnPrev	=	(Button)findViewById(R.id.btn_aw_04_answer_back);
		mBtnPrev.setOnClickListener(this);
		Intent intent	=	getIntent();
		mMasterDocId	=	intent.getStringExtra("masterDocId");
		
		getData();
	}
	
	private void setData(){
		LinearLayout aw_04_layout_image	=	(LinearLayout)findViewById(R.id.aw_04_layout_image);
		
		aw_04_layout_image.setBackgroundColor(Color.BLACK);
		for(int i = 0 ; i < mManager.getList().size() ;i++){
//			WebView webview = new WebView(Aw04AnswerActivity.this);
//			webview.loadUrl(mManager.getList().get(i).file_src);
			ImageView imgv	=	new ImageView(Aw04AnswerActivity.this);
			LinearLayout.LayoutParams subParams	=	new LinearLayout.LayoutParams(720,1200);
			imgv.setLayoutParams(subParams);
			
			if(mManager.getList().get(i).drawable==null){
				imgv.setBackgroundResource(R.drawable.pic);
			}else{
				imgv.setImageDrawable(mManager.getList().get(i).drawable);
			}
			aw_04_layout_image.addView(imgv);
			
		}
	}
	
	

	//	데이터받기
	public void getData()
	{
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw04AnswerActivity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();
		
		String temp = Global.SERVER_URL; 	// + "/" + Global.JSP_STCHRT;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SM_IMAGELIST));
		list.add(new SendQueue("master_doc_id", mMasterDocId));

		final ExNetwork http = new ExNetwork(list);

		Thread thread = new Thread(new Runnable(){
			public void run()
			{
				http.SendData();

				String result = http.rcvString.replace("\n", "");

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
					mManager = XmlParserManager.parsingSmImageList(getApplicationContext(), result);

					if( mManager == null )
					{
						Log.i(LOGTAG, "manager is memory allocation failed");
						msg.what = Global.MSG_FAILED;
						msg.arg1 = Global.HM_ERR_XML_PARSING;
						mHandler.sendMessage(msg);
						return;

					}else{

						Log.i(LOGTAG, "result = "+mManager.result_code);
						if( mManager.result_code==1000)
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
	
	public void onClick(View v)
	{
		switch (v.getId()) 
		{
			case R.id.btn_aw_04_answer_back:			// 	이전 버튼 
			{
				finish();
			}
			break;
			
		}
	}
}