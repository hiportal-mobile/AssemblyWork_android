package com.ex.group.aw.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ex.group.aw.R;

/*
	화면명: 작성자지정 > 세부항목 입력
	작성자: 방종희
	DESC: 
	DATE: 2013.04.18
	VERSION: 0.1
*/
public class Aw07Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw07Activity";
	
	//layout item
	private EditText mEt_aw_07_dtl		;	//세부항목
	private EditText mEt_aw_07_user		;	//담당자
	private Button	mBtn_aw_07_search	;	//검색
	private Button mBtn_aw_07_cancel	;	//취소
	private Button mBtn_aw_07_reg		;	//확인
	
	//부모로 전달할 변수    Aw11Activity
	private String mUserUid	=	""	;		//	담당자 코드 
	private String mUserName=	""	;		//	담당자 이름 
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_07);
		mEt_aw_07_dtl		=	(EditText)findViewById(R.id.et_aw_07_dtl);
		mEt_aw_07_user		=	(EditText)findViewById(R.id.et_aw_07_user);
		mBtn_aw_07_search	=	(Button)findViewById(R.id.btn_aw_07_search);
		mBtn_aw_07_cancel	=	(Button)findViewById(R.id.btn_aw_07_cancel);
		mBtn_aw_07_reg		=	(Button)findViewById(R.id.btn_aw_07_reg);
		
		mBtn_aw_07_search.setOnClickListener(this);
		mBtn_aw_07_reg.setOnClickListener(this);
		mBtn_aw_07_cancel.setOnClickListener(this);
		
		mEt_aw_07_user.setFocusable(false);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_aw_07_cancel:			//취소버튼
			finish();
			break;
			
		case R.id.btn_aw_07_reg:			//확인버튼
			/*
			 * 전달해줄 값
			 * *@ task_dist_sub_uid - 요구문항 작성자 배부 정보 키			
			*@ sub_tit - 요구문항 세부항목 제목
			*@ sub_req_wrt_uid - 요구문항 세부항목 작성자 키
			*@ sub_req_wrt_name - 요구문항 세부항목 작성자 이름
			*
			**/
			String sub_tit			=	mEt_aw_07_dtl.getText().toString();
			
			if(sub_tit.equals("")){
				Toast.makeText(Aw07Activity.this, "세부항목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(mUserUid.equals("")){
				Toast.makeText(Aw07Activity.this, "담당자를 검색해서 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent aw06Intent	=	getIntent();
			aw06Intent.putExtra("sub_tit", sub_tit);
			aw06Intent.putExtra("sub_req_wrt_uid", mUserUid);
			aw06Intent.putExtra("sub_req_wrt_name", mUserName);
			setResult(RESULT_OK,aw06Intent);
			finish();
			break;
		
		case R.id.btn_aw_07_search:			//검색
		{
//			if(mEt_aw_07_user.getText().toString().equals("")){
//				Toast toast = Toast.makeText(this, "담당자를 입력해 주세요.", 1000);
//				toast.show();
//			}else{
				Intent intent	=	new Intent(Aw07Activity.this,Aw08Activity.class);
				intent.putExtra("mEt_aw_07_mbr", mEt_aw_07_user.getText().toString());
				startActivityForResult(intent, 12);
//			}
		}
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
				
				mEt_aw_07_user.setText(mUserName);
				
		
			}
			
		}
		
	}

}
