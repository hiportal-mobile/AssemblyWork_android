package com.ex.group.aw.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.AssemblyPartManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;


/*
	화면명: 자료요청 > 상세검색
	작성자: 방종희
	DESC: 
	DATE: 2013.04.17
	VERSION: 0.1
 */
public class Aw10Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw10Activity";
	private EditText 	mTvSdate;
	private EditText	mTvEdate;
	private Spinner		mSpAssmPart;
	private Button		mBtn_aw_10_search;
	private Button		mBtn_aw_10_cancel;

	private int         mYear;    
	private int         mMonth;    
	private int         mDay;
	private int         mSpAssmPartIndex;
	private String 		sMbrPartCode;
	private String 		sMbrPartName;

	private AssemblyPartManager mManager;
	static final int DATE_DIALOG_ID = 0;
	View tempV;

	private ProgressDialog 	mDlgNetwork;
	private AwCustomPopup popup;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			Log.i(LOGTAG, "Login message[" + msg.what + "] : [" + msg.arg1 + "]");

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


	private Spinner.OnItemSelectedListener onSpinnerAssmPart = new Spinner.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			sMbrPartCode = mManager.getList().get(position).mbr_party_code.trim();
			sMbrPartName = mManager.getList().get(position).mbr_party_name;

			//Toast toast	=	Toast.makeText(Aw10Activity.this, "mbr_party_code="+sMbrPartCode, 1000);
			//toast.show();
		}
		public void onNothingSelected(AdapterView<?> arg0) {}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_10);



		mTvSdate 		= 	(EditText)findViewById(R.id.et_aw_10_sdate);
		mTvEdate 		= 	(EditText)findViewById(R.id.et_aw_10_edate);
		mSpAssmPart		=	(Spinner)findViewById(R.id.sp_aw_10_part);
		mBtn_aw_10_search	=	(Button)findViewById(R.id.btn_aw_10_search);	
		mBtn_aw_10_search.setOnClickListener(this);
		mBtn_aw_10_cancel	=	(Button)findViewById(R.id.btn_aw_10_cancel);
		mBtn_aw_10_cancel.setOnClickListener(this);
		//검색기간 설정
		final Calendar c = Calendar.getInstance();    
		mYear = c.get(Calendar.YEAR);        
		mMonth = c.get(Calendar.MONTH);        
		mDay = c.get(Calendar.DAY_OF_MONTH); 

		mTvEdate.setText(String.format("%04d-%02d-%02d",mYear, mMonth+1, mDay));		
		
		c.add(Calendar.MONTH, -3);
		
		mYear = c.get(Calendar.YEAR);        
		mMonth = c.get(Calendar.MONTH);        
		mDay = c.get(Calendar.DAY_OF_MONTH);		
		
		mTvSdate.setText(String.format("%04d-%02d-%02d",mYear, mMonth+1, mDay));
		
		mSpAssmPart.setPrompt("정당선택");

		mSpAssmPart.setOnItemSelectedListener(onSpinnerAssmPart);

		mTvSdate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				tempV = v;
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});
		mTvEdate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				tempV = v;
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});

		getData();
	}

	private void setData()
	{
		List<String> arr = new ArrayList<String>();


		if( mManager != null )
		{
			for( int i=0 ; i<mManager.getList().size() ; i++ )
			{
				if(mManager.getList().get(i).mbr_party_name != null){
					arr.add(mManager.getList().get(i).mbr_party_name);	
				}
				
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arr);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpAssmPart.setAdapter(adapter);
		}


	}

	public void getData()
	{
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw10Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp;

		//	정당리스트
		temp = Global.SERVER_URL;	// + "/"+Global.JSP_SEARCH_ASSEMBLY_PART;	//

		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SEARCH_ASSEMBLY_PART));


		final ExNetwork http = new ExNetwork(list);

		Thread thread = new Thread(new Runnable(){
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


					mManager = XmlParserManager.parsingAssemblyPartList(getApplicationContext(), result);

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

	private DatePickerDialog.OnDateSetListener mDateSetListener = 
			new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			if(tempV.getId()==R.id.et_aw_10_sdate){
				setDateUpdate(mTvSdate);
			}else{
				setDateUpdate(mTvEdate);
			}
		}
	};

	public void setDateUpdate(EditText e){
		e.setText(String.format("%04d-%02d-%02d",mYear, mMonth+1, mDay));
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_aw_10_search:

			//검색조건을 받는다. 
			String searchSdate	=	mTvSdate.getText().toString();		//검색 시작일
			String searchEdate	=	mTvEdate.getText().toString();		//검색 종료일
			String searchPartCode=	sMbrPartCode;						//정당코드 

			Intent intent	=	getIntent();
			intent.putExtra("aw_10_searchSdate", searchSdate);
			intent.putExtra("aw_10_searchEdate", searchEdate);
			intent.putExtra("aw_10_searchPartCode", searchPartCode);
			setResult(RESULT_OK,intent);
			finish();
			break;

		case R.id.btn_aw_10_cancel:
			finish();

			break;
		default:
			break;
		}
	}

}
