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
import android.widget.ImageView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.AttachFileManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;

/*
 	화면명: 요청자료 > 의원요청등록 > 파일첨부
	작성자: 방종희
	DESC: 
	DATE: 2013.04.18
	VERSION: 0.1
	
	설명 : 첨부파일 업로드

	전송방식 : POST
	송신데이터형식 : File
	수신데이터형식 : Xml
	
	전달해줄변수 :  filename, file 항목은 한쌍으로 항목명은 무엇으로하든 무관합니다. 
					단, 순서를 반드시 지켜야하며 여러쌍을 입력하여도 됩니다.
	* 전송데이터 --->  mas_type_code : 국회 업무 유형(필수)  // 요구자료(DR), 질의답변(QA), 서면답변(WR), 국감결과(IR), 예상Q&A(IP)
	* filename - 이미지명(필수)
	* file - 이미지(필수)
	
	전달받을변수 : 
	* file_savename	- 파일 저장 이름
	* file_name -  파일 이름
	* file_size -  파일 크기
 */
public class Aw15Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw15Activity";
	private Button mBtn_aw_15_cancel;		//	취소
	private Button mBtn_aw_15_ok;			//	확인
	private Button mBtn_aw_15_filesearch;	// 	찾아보기
	private ImageView mIv_aw_15_insertpic;	//	이미지 선택시 - 확대보기 
	private EditText mEt_aw_15_filename;	//	파일명
	
	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private AttachFileManager mManager	=	null;

	private String picturePath;				//	이미지경로
	private static final int RESULT_LOAD_IMAGE = 0; //파일첨부결과
	private String mFileKey = 	""	;		//첨부차일키
	private String mGbnCd	=	""	;		//업무구분코드
	
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{
				case Global.MSG_FAILED:
					//	실패에 따른 메세지 출력
					break;
				case Global.MSG_SUCEEDED:
				{
					Intent intent	=	getIntent();
					intent.putExtra("sFile_savename", mManager.file_savename);
					intent.putExtra("sFile_name", mManager.file_name);
					intent.putExtra("sFile_size", mManager.file_size);
					setResult(RESULT_OK,intent);
					
					Log.i(LOGTAG, "result : "+mManager.result_code);
					Log.i(LOGTAG, "file_name : "+mManager.file_name);
					Log.i(LOGTAG, "file_savename : "+mManager.file_savename);
					
				}
				finish();
					break;
			}

			if( mDlgNetwork.isShowing())
			{
				mDlgNetwork.dismiss();
			}
		}

	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_15);
		
		mBtn_aw_15_cancel	=	(Button)findViewById(R.id.btn_aw_15_cancel);
		mBtn_aw_15_cancel.setOnClickListener(this);
		
		mBtn_aw_15_ok	=	(Button)findViewById(R.id.btn_aw_15_ok);
		mBtn_aw_15_ok.setOnClickListener(this);
		
		mBtn_aw_15_filesearch = (Button)findViewById(R.id.btn_aw_15_filesearch);
		mBtn_aw_15_filesearch.setOnClickListener(this);
		
		mEt_aw_15_filename = (EditText)findViewById(R.id.et_aw_15_filename);
		
		mIv_aw_15_insertpic	=	(ImageView)findViewById(R.id.iv_aw_15_insertpic);
		
		Intent intent	=	getIntent();
		mGbnCd	=	intent.getStringExtra("mGbnCd");
		
	}
	
		//등록 
		public void sendData(){

			if( mDlgNetwork == null || !mDlgNetwork.isShowing())
			{
				mDlgNetwork = ProgressDialog.show(Aw15Activity.this, "모바일 국회", "등록중...");
			}

			ArrayList<SendQueue> list = new ArrayList<SendQueue>();

			String temp = Global.SERVER_URL;// + "/" + Global.JSP_FILE_UPLOAD;
			list.add(new SendQueue("", temp));
			list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_FILE_UPLOAD));
			list.add(new SendQueue("file_key ", mFileKey));
			list.add(new SendQueue("mas_type_code", mGbnCd));
			
			final ExNetwork http = new ExNetwork(list);
			thread = new Thread(new Runnable(){
				public void run()
				{
					http.SendData();

					String result = http.rcvString;

					mManager = XmlParserManager.parsingAttachFile(getApplicationContext(), "");

					if( mManager.result_code == 1000)
					{
						Message msg = mHandler.obtainMessage();
						msg.what = Global.MSG_SUCEEDED;
						mHandler.sendMessage(msg);
						return;
					}
					else
					{
						Message msg = mHandler.obtainMessage();
						msg.what = Global.MSG_FAILED;
						mHandler.sendMessage(msg);
						return;
					}
				}

			},LOGTAG+"thread");
			thread.setDaemon(true);
			thread.start();
		}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         
        if (resultCode == RESULT_OK) {
        	if(requestCode == RESULT_LOAD_IMAGE){
        		
        	}
          
        }
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_aw_15_filesearch:
//			Intent i = new Intent(
//			Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			startActivityForResult(i, RESULT_LOAD_IMAGE);
			
			Intent i	=	new Intent(Aw15Activity.this,Aw15FileActivity.class);
			startActivityForResult(i, RESULT_LOAD_IMAGE);
			break;
		case R.id.btn_aw_15_cancel: //취소버튼
			Intent intentc	=	getIntent();
			setResult(RESULT_CANCELED,intentc);
			finish();
			break;

		case R.id.btn_aw_15_ok: //파일업로드
			
			//이미지 체크 
			if(picturePath==null){
				Toast toast = Toast.makeText(Aw15Activity.this, "첨부파일을 입력해 주세요.", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}

			//파일명 체크 
			if(mEt_aw_15_filename.getText().length()==0){
				Toast toast = Toast.makeText(Aw15Activity.this, "파일이름을 입력해 주세요.", Toast.LENGTH_SHORT);
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
