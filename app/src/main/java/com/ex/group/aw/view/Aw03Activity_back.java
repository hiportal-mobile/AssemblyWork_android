package com.ex.group.aw.view;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ex.group.aw.R;
import com.ex.group.aw.database.DatabaseConstants;
import com.ex.group.aw.database.DatabaseOpenHelper;
import com.ex.group.aw.manager.PersonDetailManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.util.CommonUtils;
import com.ex.group.aw.vo.Global;
/*
	화면명: 국토해양위원회 > 의원상세
	작성자: 방종희
	DESC: 
	DATE: 2013.04.15
	VERSION: 0.1
 */
public class Aw03Activity_back extends Activity
{
	private static final String LOGTAG = "Aw03Activity";

	private Button mBtnPrev;
	private ImageView mIvFace;
	private TextView mTvName;
	private TextView mTvParty;
	private TextView mTvRegion;
	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private String mAssem_mbr_uids;			//	국회의원코드 
	private Drawable drawable=null;
	private int densiDPI;

	private PersonDetailManager mManager = new PersonDetailManager();

	private AwCustomPopup popup;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			switch(msg.what)
			{
			case Global.MSG_FAILED:
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
				break;
			case Global.MSG_SUCEEDED:
			{
				//리스트뷰에 내용으 표시한다. 
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
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_03);

		Bundle extra = getIntent().getExtras();

		mAssem_mbr_uids	=	extra.getString("mAssem_mbr_uids");

		Log.i(LOGTAG, "mAssem_mbr_uids= " + mAssem_mbr_uids);
		
		//화면사이즈
		Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// 가로
		int displayWidth = display.getWidth();
		// 세로
		int displayHeight = display.getHeight();
		
		Log.d(LOGTAG, "displayWidth = "+displayWidth);
		Log.d(LOGTAG, "displayHeight = "+displayHeight);
		
		densiDPI = this.getResources().getDisplayMetrics().densityDpi;
		Log.d(LOGTAG, "densiDPI ="+densiDPI);
		
		//화면 해상도
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		// 가로
		int deviceWidth = displayMetrics.widthPixels;
		// 세로
		int deviceHeight = displayMetrics.heightPixels;
		
		Log.d(LOGTAG, "deviceWidth = "+deviceWidth);
		Log.d(LOGTAG, "deviceHeight = "+deviceHeight);

		//mLvInfo = (ListView)findViewById(R.id.lv_aw_03_assem_dtl);

//		mLvInfo.setOnItemClickListener
//		(
//				new OnItemClickListener()
//				{
//					@Override
//					public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
//					{
//						if(!mManager.getList().get(position).aide_tel.equals("")){
//							call(mManager.getList().get(position).aide_tel.toString());
//						}
//					}
//				}
//				);

		mBtnPrev = (Button)findViewById(R.id.btn_aw_03_back);

		mBtnPrev.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				//Intent intent = new Intent(Aw03Activity.this, Aw02Activity.class);
				//startActivity(intent);

				finish();
			}
		});
		
		
		getAssemList();
	}
	
	//전화걸기
	public void call(String phoneNumber){
		Intent telIntent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber));

		startActivity(telIntent);
	}

	public  void setData(){
		if(mManager != null){
		//		사진
		mIvFace = (ImageView)findViewById(R.id.iv_aw_03_assem_pic);
		TextView tv_aw_03_career=	(TextView)findViewById(R.id.tv_aw_03_career);
		
		TextView tv_aw_03_hall	=	(TextView)findViewById(R.id.tv_aw_03_hall);
		
		
		DatabaseOpenHelper dbOpenHelper = null;	
		dbOpenHelper = new DatabaseOpenHelper(Aw03Activity_back.this);
		dbOpenHelper.open();
		dbOpenHelper.getDistIMG_C(mManager.assem_mbr_uid);
		Cursor cursor	=	null;
		cursor	=	dbOpenHelper.getDistIMG_C(mManager.assem_mbr_uid);
		if ( cursor != null && cursor.getCount() > 0 ) {
		    if (cursor.moveToFirst()) {
		        // loop until it reach the end of the cursor
		        do {
		            // do something here
		        	drawable=	CommonUtils.ByteToDrawble(cursor.getBlob(cursor.getColumnIndex(DatabaseConstants.CreateDB.DRAWABLE_BYTE)));
		        	Log.i(LOGTAG, "DRAWABLE_BYTE:"+cursor.getBlob(cursor.getColumnIndex(DatabaseConstants.CreateDB.DRAWABLE_BYTE)));
		        	
		        } while (cursor.moveToNext());
		    }

		    // make sure to close the cursor
		    cursor.close();
		}
		if(drawable!=null){
			mIvFace.setImageDrawable(drawable);
		}else{
			BitmapDrawable img	=	(BitmapDrawable)getResources().getDrawable(R.drawable.pic);
			mIvFace.setImageDrawable(img);
		}
		//	이름
		mTvName = (TextView)findViewById(R.id.tv_aw_03_assembly_name);
		mTvName.setText(mManager.mbr_name);

		//	정당
		mTvParty = (TextView)findViewById(R.id.tv_aw_03_part_name);
		mTvParty.setText(mManager.mbr_party_name);
		
		//출생연도
		TextView tvBirth = (TextView)findViewById(R.id.tv_aw_03_birthday);
		tvBirth.setText(mManager.mbr_birth);
		//	출신구
		mTvRegion = (TextView)findViewById(R.id.tv_aw_03_region);
		mTvRegion.setText(mManager.mbr_region);
		
		//주요경력 
//		tv_aw_03_career.setText(StringUtils.defaultIfEmpty(mManager.mbr_career, ""));
		
		//tv_aw_03_career.setText(mManager.mbr_career.replace("\\n", System.getProperty("line.separator")));
		tv_aw_03_career.setText(Html.fromHtml(mManager.mbr_career));
		
		LinearLayout ll_aw_03_phone = (LinearLayout)findViewById(R.id.ll_aw_03_phone);
		//전화번호
		
		String temp	=	mManager.mbr_tel;
		String[] tempPhone = temp.split("/");
		int tSize = 0;
		if(densiDPI==160){
			 tSize	=	24;
		}else{
			 tSize	=	15;
		}
		
		for(int j = 0 ; j < tempPhone.length ; j++){
			
			LinearLayout.LayoutParams tvPhoneControll	=	new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			TextView tvPhone	=	new TextView(this);
			tvPhoneControll.setMargins(10, 10, 0, 0);
			tvPhone.setTextSize(tSize);
			tvPhone.setTextColor(Color.parseColor("#333333"));
			tvPhone.setLayoutParams(tvPhoneControll);
//			if(j==0){
//				tvPhone.setText(tempPhone[j]);
//			}else{
				//vPhone.setText(" / " + tempPhone[j]);
//			}
			
			tvPhone.setText(tempPhone[j]);
			
			tvPhone.setTag(tempPhone[j]);
			
			ll_aw_03_phone.addView(tvPhone);
			
			tvPhone.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String tempPhoneNum	=	(String)v.getTag();
					call(tempPhoneNum);
				}
			});
		}
		
				
		//의원회관
		tv_aw_03_hall.setText(mManager.mbr_hall);
		
		Log.d(LOGTAG, "list size = " + mManager.getList().size());
		//InfoArrayAdapter adapter = new InfoArrayAdapter(this, mManager.getList());

		//보좌관 정보 출력 
		setAsideIndo();
		}
		
		//mLvInfo.setAdapter(adapter);
	}
	
	public void setAsideIndo(){
		LinearLayout aw_03_asideinfo	=	(LinearLayout)findViewById(R.id.aw_03_asideinfo);
		
		int tSize = 0;
		if(densiDPI==160){
			 tSize	=	24;
		}else{
			 tSize	=	15;
		}
	
		for(int i = 0 ; i < mManager.getList().size();i++){
			LinearLayout aw_03_asideinfo_child = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT) ;
			params.setMargins(10, 10, 15, 0);
			aw_03_asideinfo_child.setLayoutParams(params);
			aw_03_asideinfo_child.setBackgroundResource(R.drawable.bg_con_bar_s6);
			aw_03_asideinfo_child.setOrientation(LinearLayout.VERTICAL);
			aw_03_asideinfo.addView(aw_03_asideinfo_child);
			
			LinearLayout.LayoutParams comParams	=	new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			comParams.setMargins(10, 10, 0, 0);
			TextView tvAsideName	=	new TextView(this);
			LinearLayout linearAsidePhone = new LinearLayout(this);
			TextView tvAsideEmail	=	new TextView(this);
			
			//보좌관 명
			tvAsideName.setLayoutParams(comParams);
			tvAsideName.setTextSize(tSize);
			tvAsideName.setTextColor(Color.parseColor("#333333"));
//			tvAsideName.setTypeface(null, Typeface.BOLD);
			tvAsideName.setText(mManager.getList().get(i).aide_name);
			
			//휴대전화정보 보기
			linearAsidePhone.setLayoutParams(comParams);
			linearAsidePhone.setOrientation(LinearLayout.VERTICAL);
			
			
			String temp	=	 mManager.getList().get(i).aide_tel;
			String[] tempPhone =	temp.split("/");
			
			for(int j = 0 ; j < tempPhone.length ; j++){
				LinearLayout.LayoutParams tvPhoneControll	=	new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				TextView tvPhone	=	new TextView(this);
				tvPhone.setTextSize(tSize);
				tvPhone.setTextColor(Color.parseColor("#333333"));
				tvPhone.setLayoutParams(tvPhoneControll);
//				if(j==0){
//					tvPhone.setText(tempPhone[j]);
//				}else{
					//vPhone.setText(" / " + tempPhone[j]);
//				}
				
				tvPhone.setText(tempPhone[j]);
				
				tvPhone.setTag(tempPhone[j]);
				
				linearAsidePhone.addView(tvPhone);
				
				tvPhone.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String tempPhoneNum	=	(String)v.getTag();
						call(tempPhoneNum);
					}
				});
			}
			//보좌관 이메일
			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT) ;
			params2.setMargins(10, 10, 0, 10);
			tvAsideEmail.setLayoutParams(params2);
			tvAsideEmail.setTextSize(tSize);
			tvAsideEmail.setTextColor(Color.parseColor("#333333"));
			tvAsideEmail.setText(mManager.getList().get(i).aide_email);
			
			aw_03_asideinfo_child.addView(tvAsideName);
			aw_03_asideinfo_child.addView(linearAsidePhone);
			aw_03_asideinfo_child.addView(tvAsideEmail);
		}
		
	}

	public void getAssemList(){
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw03Activity_back.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		//String  sParams	=	String.format("?assem_mbr_uid=%s",mAssem_mbr_uids);

		String temp = Global.SERVER_URL; // + "/" + Global.JSP_ASSEMBLY_LIST;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_ASSEMBLY_DTL));
		list.add(new SendQueue("assem_mbr_uid", mAssem_mbr_uids));

		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
			public void run()
			{
				http.SendData();

				String result = http.rcvString.replace("\n","");

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

					XmlParserManager.parsingAsmDetail(getApplicationContext(), result ,mManager);

					Log.i(LOGTAG, "mManager.result_code = "+mManager.result_code);
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



//	class InfoArrayAdapter extends ArrayAdapter 
//	{
//		Context context;
//		
//		TextView asName;
//		TextView asPhone1;
//		TextView asPhone2;
//		TextView asPhone3;
//		
//		TextView asEmail;
//		
//		ArrayList<PersonDetailInfo> list;
//
//		@SuppressWarnings("unchecked")
//		public InfoArrayAdapter(Context context, ArrayList<PersonDetailInfo> list) 
//		{
//			super(context, R.layout.layout_aw_03_list, list);
//
//			// TODO Auto-generated constructor stub
//			this.list = list;
//			this.context = context;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//
//			View row = convertView;
//
//			if(row == null)
//			{
//				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
//
//				row = (View)inflater.inflate(R.layout.layout_aw_03_list, null);
//			}
//
//			asName = (TextView)row.findViewById(R.id.tv_aw_03_list_name);
//			LinearLayout dtl_phone_scroll = (LinearLayout)row.findViewById(R.id.dtl_phone_scroll);
//			
//			
//			asEmail = (TextView)row.findViewById(R.id.tv_aw_03_list_email);
//			
//			PersonDetailInfo info = list.get(position);
//			String[] tempPhone = null;
//			String temp	= "";
//			temp	=	info.aide_tel;
//			
//			TextView titleTxt1	=	new TextView(context);
//			titleTxt1.setText(temp);
//			LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
//			titleTxt1.setLayoutParams(params1);
//			dtl_phone_scroll.addView(titleTxt1);
//			
//			Log.d(LOGTAG, "=====================position is " + position + ", tempPhone = "+temp);
//			
//			tempPhone	=	temp.split("/");
//			//Log.i(LOGTAG, "tempPhone="+tempPhone.length);
//			//Log.i("tetetet", temp);
//			for(int i = 0; i<tempPhone.length;i++){
//				TextView titleTxt	=	new TextView(context);
//				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				titleTxt.setLayoutParams(params);
//				titleTxt.setTextColor(Color.parseColor("#333333"));
//				if(i==0){
//					titleTxt.setText("전화 : "+tempPhone[i]);
//				}
//				titleTxt.setText(" / "+tempPhone[i]);
//				dtl_phone_scroll.addView(titleTxt);
//				//Log.i(LOGTAG, "tempPhone["+i+"] ="+tempPhone[i]);
//			}
//			
//			//Log.i(LOGTAG, "aide_name" +info.aide_name);
//			//Log.i(LOGTAG, "aide_email" +info.aide_email);
//			asName.setText(info.aide_name);
//			asEmail.setText("이메일 : "+info.aide_email);
//			
//			return row;
//		}
//	}
}
