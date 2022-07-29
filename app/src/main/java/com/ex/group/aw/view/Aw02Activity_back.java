package com.ex.group.aw.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.group.aw.R;
import com.ex.group.aw.database.PersonFaceImagerManager;
import com.ex.group.aw.manager.PersonManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
/*
	화면명: 국토해양위원회 
	작성자: 방종희
	DESC: 
	DATE: 2013.04.11
	VERSION: 0.1
 */
public class Aw02Activity_back extends Activity
{
	private final static String LOGTAG = "Aw02Activity";
	private Button mBtnPrev;
	private TextView mTvSubject;
	private ListView mLvPerson;
	private ProgressDialog 	mDlgNetwork;
	private int mCurPageNo	=	0;
	private AwCustomPopup popup;
	private PersonManager mManager = new PersonManager();
	private PersonArrayAdapter mAdapter;
	
	private View footer ;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_02);

		mBtnPrev = (Button)findViewById(R.id.btn_aw_02_back);

		mBtnPrev.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});

		mTvSubject = (TextView)findViewById(R.id.tv_aw_02_subject);

		mLvPerson = (ListView)findViewById(R.id.lv_aw_02_subject);
		
		// 푸터 생성
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer =  inflater.inflate(R.layout.layout_listview_footer_more, null);
		TextView moreBtn = (TextView)footer.findViewById(R.id.btn_more);

		mLvPerson.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
				
					Intent intent = new Intent(Aw02Activity_back.this, Aw03Activity.class);

					intent.putExtra("mAssem_mbr_uids", mManager.getList().get(position).assem_mbr_uid);

					Log.i(LOGTAG, "mAssem_mbr_uids = "+mManager.getList().get(position).assem_mbr_uid);
		
					startActivity(intent);
				}
			}
		);
		//		더보기 호출 	
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mCurPageNo++;	//	가져올 페이지 번호 
				getAssemList();	//	리스트 데이터 
			}
		});

		getAssemList();

	}

	@Override
	protected void onRestart()
	{
		Log.i(LOGTAG, "onRestart()");
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume()
	{
		Log.i(LOGTAG, "onResume()");
		// TODO Auto-generated method stub
		super.onResume();
	}
	
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
			//실패메시지 출력
			break;
			case Global.MSG_SUCEEDED:
			{
				Log.i(LOGTAG, "Global.MSG_SUCEEDED");
//				for (int i = 0; i < mManager.getList().size(); i++) {
//					Log.d("","ffffffffffffffffffff be1 = "+mManager.getList().get(i).mbr_name+"   "+mManager.getList().get(i).drawable);
//				}
				new PersonFaceImagerManager(getApplicationContext(), mManager, mHandler);
				
//				for (int i = 0; i < mManager.getList().size(); i++) {
//					Log.d("","ffffffffffffffffffff 1 = "+mManager.getList().get(i).mbr_name+"   "+mManager.getList().get(i).drawable);
//				}
				
//				//리스트뷰에 내용으 표시한다. 
//				//리스트뷰에 내용으 표시한다. 
//				if(mCurPageNo==1){
//					setData(false);
//				}else{
//					setData(true);
//				}
//				Log.i(LOGTAG, "Success");
			}
			break;
			//여기로 들어온다*********************************
			//*********************************
			//*********************************
			//*********************************
			//*********************************
			//*********************************
			//*********************************
			case Global.MSG_IMAGE_DOWNLOAD_COMPLETE:
			{
				Log.i(LOGTAG, "Global.MSG_IMAGE_DOWNLOAD_COMPLETE");
				for (int i = 0; i < mManager.getList().size(); i++) {
					Log.d("","ffffffffffffffffffff be2 = "+mManager.getList().get(i).mbr_name+"   "+mManager.getList().get(i).drawable);
				}
				new PersonFaceImagerManager(getApplicationContext(), mManager);
				for (int i = 0; i < mManager.getList().size(); i++) {
					Log.d("","ffffffffffffffffffff 2 = "+mManager.getList().get(i).mbr_name+"   "+mManager.getList().get(i).drawable);
				}
				//리스트뷰에 내용으 표시한다. 
				//리스트뷰에 내용으 표시한다. 
				if(mCurPageNo==1){
					setData(false);
				}else{
					setData(true);
				}
				Log.i(LOGTAG, "Success");
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

	

	private void setData(boolean more)
	{
		String subject = String.format("총 %d명의 의원이 있습니다.", mManager.total_count);

		mTvSubject.setText(subject);

		//	더보기 보이기 숨기기 
		mLvPerson.removeFooterView(footer);

		if(mManager.total_count>mCurPageNo*mManager.count){
			mLvPerson.addFooterView(footer);
		}
		
		mAdapter = new PersonArrayAdapter(Aw02Activity_back.this);
		if(! more ){
			mLvPerson.setAdapter(mAdapter);
		}
		// 더보기  리스트 
		mAdapter.notifyDataSetChanged();
		
	}
	public void getAssemList(){
		
		if(mCurPageNo==0){mCurPageNo=1;}
		
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw02Activity_back.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();
		String temp = Global.SERVER_URL; // + "/" + Global.JSP_ASSEMBLY_LIST;

		//Log.i(LOGTAG, "temp ; "+temp);
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_ASSEMBLY_LIST));
		list.add(new SendQueue("pageno",Integer.toString(mCurPageNo)));	
		
		Log.i(LOGTAG, "pageno="+mCurPageNo);

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
					//통신 후 목록 PersonManager 의 mList 에 저장 하는 부분
					XmlParserManager.parsingAsmList(getApplicationContext(), result ,mManager);
					
					//mManager = XmlParserManager.parsingAsmList(getApplicationContext(), "");

					//Log.i(LOGTAG, "mManager.result_code = "+mManager.result_code);
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

	class PersonArrayAdapter extends ArrayAdapter 
	{
		Context context;

		TextView title;
		TextView sub;
		ImageView face;

		@SuppressWarnings("unchecked")
		public PersonArrayAdapter(Context context) 
		{
			super(context, R.layout.layout_aw_02_list, mManager.getList());

			// TODO Auto-generated constructor stub

			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_02_list, null);
			}

			face = (ImageView)row.findViewById(R.id.iv_awgt_02_list_assemblyman_img);
			title = (TextView)row.findViewById(R.id.tv_awgt_02_list_assembly);
			sub = (TextView)row.findViewById(R.id.tv_awgt_02_list_assembly_dtl);
			Log.d("","Aw02Activity PersonArrayAdapter getView = "+mManager.getList().get(position).mbr_name+":"+mManager.getList().get(position).drawable);
			if(mManager.getList().get(position).drawable!=null){
				face.setImageDrawable(mManager.getList().get(position).drawable);
			}else{
				BitmapDrawable img	=	(BitmapDrawable)getResources().getDrawable(R.drawable.pic);
				face.setImageDrawable(img);
			}

			String temp = "";
			temp = mManager.getList().get(position).mbr_party_name + "/";
			temp += mManager.getList().get(position).mbr_region;
			title.setText(mManager.getList().get(position).mbr_name);
			
			
			Log.d("","test geview = "+mManager.getList().get(position).toString());
			sub.setText(temp);

			return row;
		}
	}
}
