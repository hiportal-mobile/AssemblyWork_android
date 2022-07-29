package com.ex.group.aw.view;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.SmStchrtManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
/*
	화면명: 스마트 국감 > 좌석배치도
	작성자: 방종희
	DESC: 
	DATE: 2013.04.16
	VERSION: 0.1
 */
public class Aw04Activity extends Activity
{
	private final static String LOGTAG = "Aw04Activity";

	private Button mBtnPrev	;
	private AwCustomPopup popup;
	private int densiDPI;
	private int  displayWidth ; 
	private int  displayHeight ;

	protected SmStchrtManager mManager = new SmStchrtManager();

	private ProgressDialog 	mDlgNetwork;

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
				finish();
			}		
		});

		popup.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_04);
		
		//화면사이즈
		Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// 가로
		displayWidth = display.getWidth();
		// 세로
		displayHeight = display.getHeight();

		mBtnPrev = (Button)findViewById(R.id.btn_aw_04_back);
		
		densiDPI = this.getResources().getDisplayMetrics().densityDpi;

		mBtnPrev.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				//Intent intent = new Intent(Aw04Activity.this, Aw01Activity.class);
				//startActivity(intent);
				finish();
			}
		});
		
//		Toast.makeText(this, "displayWidth=" +displayWidth, Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "displayHeight=" +displayHeight, Toast.LENGTH_LONG).show();
//		Toast.makeText(this, "densiDPI=" +densiDPI, Toast.LENGTH_LONG).show();
		getURL();
	}
	public void setData(){
		
		try{
	
		//타이트 설정 
		TextView aw_04_subtitle	=	(TextView)findViewById(R.id.aw_04_subtitle);
		aw_04_subtitle.setText(mManager.mas_tit);
		//총 열수 
		int colCnt		=	Integer.parseInt(mManager.col_count);
		//천체 카운트
		int totalCnt	=	mManager.getList().size();
		LinearLayout aw_04_layout_seat	=	(LinearLayout)findViewById(R.id.aw_04_layout_seat);
		//나머지확인
		int tempCalc	=	totalCnt%colCnt;
		int rowCnt = totalCnt/colCnt;
		
		
		//글씨크기
		int tSize = 0;
		int setMargin	=	0;
		int setMarginTop	=	0;
		if(densiDPI==160){		// 탭사이즈 
			 tSize	=	22;	
			 setMargin	=	15;
			 setMarginTop	=	10;
		}else{					// 폰 사이즈
			 tSize	=	13;
			 setMargin	=	7;
			 setMarginTop	=	10;
		}
		
		//열 추가
		if(tempCalc>0){
			rowCnt++; 			
		}
		//데이터 POSITION
		int tempDisCnt = 0;		
		for(int i = 0;i<rowCnt;i++){
			
			LinearLayout linearRootRow	=	new LinearLayout(Aw04Activity.this);
			LinearLayout.LayoutParams rootParams	=	new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			rootParams.setMargins(setMargin, setMargin,0, 0);
			linearRootRow.setOrientation(0);
			linearRootRow.setLayoutParams(rootParams);
			
			//열수만큼 반복실행한다. 
			for(int j=0 ;j<colCnt;j++){
				
				String tempMbName	=	"";
				int tempRowNum = 0;
				int tempColNum = 0;
				tempRowNum	=	Integer.parseInt(mManager.getList().get(tempDisCnt).row_num);
				tempColNum	=	Integer.parseInt(mManager.getList().get(tempDisCnt).col_num);
				
				
				//데이트 존재 항목 
				if( tempRowNum==i && tempColNum==j){
					
					//체크박스와 이미지를 감싼다.
					LinearLayout linearSub	=	new LinearLayout(Aw04Activity.this);
					
					LinearLayout.LayoutParams subParams	= null;
					if(densiDPI==160){		// 탭사이즈 
						subParams	=	new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,55);
					}else{
						
						if(densiDPI==320&& displayHeight==1280 && displayWidth==800){		//노트1 
							subParams	=	new LinearLayout.LayoutParams(151,LayoutParams.WRAP_CONTENT);
						}else{
							subParams	=	new LinearLayout.LayoutParams(136,LayoutParams.WRAP_CONTENT);
						}
						
					}
					
					subParams.setMargins(0, 0, setMargin, 0);
					linearSub.setLayoutParams(subParams);
					
					linearSub.setOrientation(LinearLayout.HORIZONTAL);
					linearSub.setGravity(Gravity.CENTER_VERTICAL);
					linearSub.setBackgroundResource(R.drawable.img_seat);
					
					
					//이미지뷰 생성
					ImageView chimg	=	new ImageView(Aw04Activity.this);
					LinearLayout.LayoutParams imgParams	=	new LinearLayout.LayoutParams(40,40);
					
					chimg.setPadding(setMarginTop, 0, 0, 0);
					chimg.setLayoutParams(imgParams);
					//의원사진 
//					ImageView faceImg	=	new ImageView(Aw04Activity.this);
//					LinearLayout.LayoutParams imgParams	=	new LinearLayout.LayoutParams(80,80);
//					faceImg.setLayoutParams(imgParams);
					
					//의원이름 
					TextView tempSeat	=	new TextView(Aw04Activity.this);
					LinearLayout.LayoutParams textParams	=	new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
					tempSeat.setTextColor(Color.parseColor("#333333"));
					tempSeat.setTextSize(tSize);
					tempSeat.setLayoutParams(textParams);
					tempMbName = mManager.getList().get(tempDisCnt).mbr_name;
					
					if(!tempMbName.equals("")){
						
						//linearSub.setBackgroundColor(Color.GRAY);
						
						tempSeat.setGravity(Gravity.CENTER_HORIZONTAL);
						textParams.setMargins(setMarginTop, 0, 0, 0);
						
						//qna 유무에 따른 BG변경 
						if(mManager.getList().get(tempDisCnt).qa_yn.equals("Y")){
							linearSub.setBackgroundResource(R.drawable.img_seat_check);
							//chimg.setImageResource(i);
						}else{
							linearSub.setBackgroundResource(R.drawable.img_seat);
							//chimg.setImageResource(R.drawable.checkbox_off);
						}
						chimg.setImageDrawable(mManager.getList().get(tempDisCnt).drawable);
//						faceImg.setImageResource(R.drawable.checkbox_off);
						
						linearSub.setTag(Integer.toString(tempDisCnt));
						linearSub.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String sTempPos	=	(String)v.getTag();
								int spos	=	Integer.parseInt(sTempPos);
								Log.d(LOGTAG, sTempPos);
								
								String tempAssemUid		=	mManager.getList().get(spos).assem_mbr_uid ; 	//	의원정보기
								String tempSmrtArrUid	=	mManager.getList().get(spos).smrt_arr_uid ; 	//	스마트관리 좌석배치키
								String tempMasUid		=	mManager.getList().get(spos).smrt_mas_uid ; 	//	스마트관리 마스터키
								String tempQaYn			=	mManager.getList().get(spos).qa_yn ; 			//	스마트관리 마스터키
								
								if(tempQaYn.equals("Y")){
									Intent intent	=	new Intent(Aw04Activity.this,Aw04DtlActivity.class);
									intent.putExtra("smrt_mas_uid", tempMasUid);
									intent.putExtra("smrt_arr_uid", tempSmrtArrUid);
									intent.putExtra("assem_mbr_uid", tempAssemUid);
									startActivity(intent);
								}else{
									Toast.makeText(Aw04Activity.this, "질의답변 자료가 없습니다.", Toast.LENGTH_SHORT).show();
								}
									
							}
						});
						
	
					}else{
						subParams.weight=1;
					}
					
					tempSeat.setText(tempMbName);
					//이지지뷰 추가 
					linearSub.addView(chimg);
					
					//텍스트뷰 추가 
					linearSub.addView(tempSeat);
					//레이아웃 추가
					linearRootRow.addView(linearSub);
				}
				
				tempDisCnt++;
			}
			aw_04_layout_seat.addView(linearRootRow);
		}
		
		}catch(Exception e){
			String err	=	e.toString();
			AlertDialog2("진행중인 회기가 존재하지 않습니다.");
		}
		
	}

	public void getURL()
	{
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw04Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.SERVER_URL; 	// + "/" + Global.JSP_STCHRT;	//http://128.200.121.68:9000/emp_ex/service.pe
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_STCHRT));

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

					//mMapRet = XmlParserManager.parsingStChrt(getApplicationContext(), "");
					mManager = XmlParserManager.parsingStChrt(getApplicationContext(), result);

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
}
