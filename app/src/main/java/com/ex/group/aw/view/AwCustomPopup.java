package com.ex.group.aw.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ex.group.aw.R;

public class AwCustomPopup extends Dialog implements OnClickListener{
	
	
	public TextView mTvTitle;
	public TextView mTvContents;
	public Button mBtnCancel;
	public Button mBtnOk;


	public AwCustomPopup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_aw_common_popup);
		
		mTvTitle = (TextView)findViewById(R.id.tv_pw_popup_title);
		
		mTvContents = (TextView)findViewById(R.id.tv_pw_popup_contents);
		
		mBtnCancel = (Button)findViewById(R.id.btn_pw_popup_cancel);
		mBtnCancel.setOnClickListener(this);
		
		mBtnOk = (Button)findViewById(R.id.btn_pw_popup_ok);		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()) {
			case R.id.btn_pw_popup_cancel:
				cancel();
			break;
		
		}		
	}	

}
