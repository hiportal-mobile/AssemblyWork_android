package com.ex.group.aw.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ex.group.aw.R;

/*
	화면명: 자료요청 > 제목추가
	작성자: 방종희
	DESC: 
	DATE: 2013.04.17
	VERSION: 0.1
*/
public class Aw12Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw12Activity";
	private EditText mEt_aw_12_title	;	//제목 
	private EditText mEt_aw_12_request	;	//요청내용
	private CheckBox mCk_aw_12_self_req	;	//자체질문여부
	private EditText mEt_aw_12_mbr		;	//담당자
	private Button mBtn_aw_12_search	;	//담당자검색
	private Button mBtn_aw_12_cancel	;	//취소
	private Button mbtn_aw_12_reg		;	//등록
	
	//부모로 전달할 변수    Aw11Activity
	private String mUserUid	=	""	;		//	담당자 코드 여러명일 경우 (,)로 구분 
	private String mUserName=	""	;		//	담당자 이름 여러명일 경우 (,)로 구분
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_12);
		mEt_aw_12_title		=(EditText)findViewById(R.id.et_aw_12_title)	;
		mEt_aw_12_request	=(EditText)findViewById(R.id.et_aw_12_request)	;		
		mCk_aw_12_self_req	=(CheckBox)findViewById(R.id.ck_aw_12_self_req)	;	     
		mEt_aw_12_mbr		=(EditText)findViewById(R.id.et_aw_12_mbr)		;		
		mBtn_aw_12_search	=(Button)findViewById(R.id.btn_aw_12_search)	;	
		mBtn_aw_12_cancel	=(Button)findViewById(R.id.btn_aw_12_cancel)	;	
		mbtn_aw_12_reg		=(Button)findViewById(R.id.btn_aw_12_reg)		;
		
		mbtn_aw_12_reg.setOnClickListener(this);
		mBtn_aw_12_cancel.setOnClickListener(this);
		mBtn_aw_12_search.setOnClickListener(this);
		mEt_aw_12_mbr.setFocusable(false);
		Log.i(LOGTAG,"mEt_aw_12_title="+mEt_aw_12_title.getText().toString());
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_aw_12_search:
		{
//			if(mEt_aw_12_mbr.getText().toString().equals("")){
//				Toast toast = Toast.makeText(this, "담당자를 입력해 주세요.", 1000);
//				toast.show();
//			}else{
				Intent intent	=	new Intent(Aw12Activity.this,Aw14Activity.class);
				intent.putExtra("mEt_aw_12_mbr", mEt_aw_12_mbr.getText().toString());
				startActivityForResult(intent, 12);
//			}
		}
			break;
		case R.id.btn_aw_12_cancel:
			finish();
			break;
		case R.id.btn_aw_12_reg:
			
			//자체질문여부
			String self_req_flag	=	"false";
			
			if(mEt_aw_12_title.getText().toString().equals("")){
				Toast toast = Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			if(mEt_aw_12_request.getText().toString().equals("")){
				Toast toast = Toast.makeText(this, "요청내용을 입력해 주세요.", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			if(mEt_aw_12_mbr.getText().toString().equals("")){
				Toast toast = Toast.makeText(this, "담당자를 입력해 주세요.", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			if(mUserUid.equals("")){
				Toast.makeText(Aw12Activity.this, "담당자를 검색하여 선택해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(mCk_aw_12_self_req.isChecked()){
				self_req_flag	=	"true";
			}
			
			//부모의 제목 리스트뷰에 내용을 추가한다. 
			Intent intent	=	getIntent();
			intent.putExtra("mEt_aw_12_title", mEt_aw_12_title.getText().toString());
			intent.putExtra("mEt_aw_12_request", mEt_aw_12_request.getText().toString());
			intent.putExtra("self_req_flag", self_req_flag.toString());
			intent.putExtra("mEt_aw_12_mbr", mUserName.toString());
			intent.putExtra("mEt_aw_12_mbr_uid", mUserUid.toString());
			
			setResult(RESULT_OK,intent);
			//Log.i("test","mEt_aw_12_title="+mEt_aw_12_title.getText().toString());
			finish();
			break;
			
		

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK){
			
			if(requestCode==12){		//	담당자 검색 결과 
				
				if(mUserUid.equals("")){
					mUserUid	=	data.getStringExtra("msUserUid") ;
					mUserName	=	data.getStringExtra("msUserName") ;
				}else{
					mUserUid	=	mUserUid+","+data.getStringExtra("msUserUid") ;
					mUserName	=	mUserName+","+data.getStringExtra("msUserName") ;
				}
				
				mEt_aw_12_mbr.setText(mUserName);
		
			}
			
		}
		
	}

}
