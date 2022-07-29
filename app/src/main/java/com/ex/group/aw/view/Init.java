package com.ex.group.aw.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.ex.group.aw.R;
import com.ex.group.aw.vo.UserData;
import com.skt.pe.common.activity.SKTActivity;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.XMLData;

public class Init extends SKTActivity{
	
	@Override
	protected void onCreateX(Bundle arg0) {
		// TODO Auto-generated method stub
//		LogUtil.e("KKK1", "0");
		
		UserData.getInstance().PHONE_NO = SKTUtil.getMdn(this); // 전화번호
		UserData.getInstance().APP_ID = SKTUtil.getAppId(this); // 앱 아이디
		UserData.getInstance().APP_VER = SKTUtil.getVersion(this); // 앱 버전
		Log.i("Init", "Init onCreate");
		startActivity(new Intent(Init.this,Aw01Activity.class));
		finish();
	}

	@Override
	protected int assignLayout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// TODO Auto-generated method stub
//		Intent intent = new Intent(Init.this, Lm01.class);
//		startActivity(intent);
//		LogUtil.e("KKK1", "1");
		return R.layout.layout_aw_01;
	}

	@Override
	protected XMLData onAction(String arg0, String... arg1) throws SKTException {
		// TODO Auto-generated method stub
//		LogUtil.e("KKK1", "2");
		return null;
	}

	@Override
	protected void onActionPost(String arg0, XMLData arg1, SKTException arg2)
			throws SKTException {
//		LogUtil.e("KKK1", "4");
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int onActionPre(String arg0) {
		// TODO Auto-generated method stub
//		LogUtil.e("KKK1", "4");
		return Action.SERVICE_RETRIEVING;
	}
}
