package com.ex.group.aw.view;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.SmQnatManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.util.BaseExpandableAdapter;
import com.ex.group.aw.vo.Global;

public class Aw04DtlActivity extends Activity implements OnClickListener {


	private AwCustomPopup popup;
	private final static String LOGTAG = "Aw04DtlActivity";
	private Button mBtnPrev	;
	private SmQnatManager mManager	=	new SmQnatManager();
	private Button mBtn_aw_04_dtl_list_open;		//펼치기
	private Button mBtn_aw_04_dtl_list_close;		//접기
	private Button mBtn_aw_04_dtl_seat;				//의원선택
	private Button mBtn_aw_04_dtl_qa;				//질의답변 
	private Button mBtn_aw_04_dtl_other;			//추게시판명
	private Drawable drawable=null;
	private TextView mTv_aw_04_dtl_mbrname;			//의원이름
	private TextView mTv_aw_04_dtl_part; 			//소속정당 , 출신구
	
	
	private String  mSmrt_mas_uid 	;	//- 스마트관리 마스터 키
	private String  mSmrt_arr_uid 	;	//- 스마트관리 좌석배치 키
	private String  mAssem_mbr_uid 	;	//- 국회의원 정보 마스터 키
	
	private boolean  mIsQaFlag 	=	true	;	//- 국회의원 정보 마스터 키

	private ArrayList<String> mGroupList = null;
	private ArrayList<String> mGroupFilePath = null;
	private ArrayList<String> mGroupFileName = null;
	private ArrayList<String> mGroupMasterDocId = null;
	private ArrayList<ArrayList<String>> mChildList = null;
	private ArrayList<ArrayList<String>> mChildFilePath = null;
	private ArrayList<ArrayList<String>> mChildFileName = null;
	private ArrayList<ArrayList<String>> mMasterDocId = null;
	private ArrayList<String> mChildListContent = null;
	private ArrayList<String> mChildListFilePath = null;
	private ArrayList<String> mChildListFileName = null;
	private ArrayList<String> mMasterDocIdList = null;
	private BaseExpandableAdapter mBaseExpandableAdapter = null;


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

	private void setData(){
		ImageView iv_assembly_photo	= (ImageView)findViewById(R.id.iv_assembly_photo)	;
		if(mManager.mbr_photo!=null){
			iv_assembly_photo.setImageDrawable(mManager.mbr_photo);
		}else{
			BitmapDrawable img	=	(BitmapDrawable)getResources().getDrawable(R.drawable.pic);
			iv_assembly_photo.setImageDrawable(img);
		}
		
		mTv_aw_04_dtl_mbrname.setText(mManager.mbr_name);	
		mTv_aw_04_dtl_part.setText(mManager.mbr_party_name+"/"+mManager.mbr_region);
		
		mBtn_aw_04_dtl_other.setVisibility(View.GONE);
		
		if(!mManager.mas_doc_dirt_yn.equals("")){
			mBtn_aw_04_dtl_other.setText(mManager.mas_dirt_tab_name);
			mBtn_aw_04_dtl_other.setVisibility(View.VISIBLE);
		}else{
			mBtn_aw_04_dtl_other.setVisibility(View.GONE);
		}
		
		mGroupList 			= new ArrayList<String>();					 
		mGroupFilePath		= new ArrayList<String>();					
		mGroupFileName		= new ArrayList<String>();
		mGroupMasterDocId		= new ArrayList<String>();
		mChildList 			= new ArrayList<ArrayList<String>>();
		mChildListContent 	= new ArrayList<String>();
		mChildFilePath 		= new ArrayList<ArrayList<String>>();
		mChildListFilePath 	= new ArrayList<String>();
		mChildFileName 		= new ArrayList<ArrayList<String>>();
		mChildListFileName  = new ArrayList<String>();
		mMasterDocId		= new ArrayList<ArrayList<String>>();
		mMasterDocIdList	= new ArrayList<String>();
		
		if(mIsQaFlag){
			for(int i = 0 ; i < mManager.getList().size() ; i++){
				if(mManager.getList().get(i).depth.equals("1")){
				
					mGroupList.add(mManager.getList().get(i).tit);
					mGroupFilePath.add(mManager.getList().get(i).file_src);
					mGroupFileName.add(mManager.getList().get(i).file_name);
				
				}else{
					mChildListContent.add(mManager.getList().get(i).tit);
					mChildListFilePath.add(mManager.getList().get(i).file_src);
					mChildListFileName.add(mManager.getList().get(i).file_name);
				}
				if(mManager.getList().get(i).depth.equals("1")&&!mManager.getList().get(i).sub_ord_no.equals("1")){
					mChildList.add(mChildListContent);
					mChildFilePath.add(mChildListFilePath);
					mChildFileName.add(mChildListFileName);
					mChildListContent = new ArrayList<String>();
					mChildListFilePath  = new ArrayList<String>();
					mChildListFileName	= new ArrayList<String>();
				}
				
				if(i==mManager.getList().size()-1){
					mChildList.add(mChildListContent);	
					mChildFilePath.add(mChildListFilePath);
					mChildFileName.add(mChildListFileName);
				}
			}
		}else{		//사용자 입력 게시판
			
			for(int i = 0 ; i < mManager.getList2().size() ; i++){
				if(mManager.getList2().get(i).depth2.equals("1")){
					mGroupList.add(mManager.getList2().get(i).tit2);
					mGroupMasterDocId.add(mManager.getList2().get(i).master_doc_id2);
				}else{
					mChildListContent.add(mManager.getList2().get(i).tit2);
					mMasterDocIdList.add(mManager.getList2().get(i).master_doc_id2);
				}
				if(mManager.getList2().get(i).depth2.equals("1")&&!mManager.getList2().get(i).sub_ord_no2.equals("0")){
					mChildList.add(mChildListContent);
					mMasterDocId.add(mMasterDocIdList);
					mChildListContent = new ArrayList<String>();
					mMasterDocIdList	=	new ArrayList<String>();
				}
				
				if(i==mManager.getList2().size()-1){
					mChildList.add(mChildListContent);	
					mChildFilePath.add(mChildListFilePath);
					mChildFileName.add(mChildListFileName);
					mMasterDocId.add(mMasterDocIdList);
				}
			}
			
		}
		Log.i(LOGTAG, "mGroupList="+mGroupList);
		mBaseExpandableAdapter = new BaseExpandableAdapter(this, mGroupList, mChildList);
		mListView.setAdapter(mBaseExpandableAdapter);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_04_dtl);

		setLayout();
		
		Intent intent	=	getIntent();
		mSmrt_mas_uid	=	intent.getStringExtra("smrt_mas_uid");
		mSmrt_arr_uid	=	intent.getStringExtra("smrt_arr_uid");
		mAssem_mbr_uid	=	intent.getStringExtra("assem_mbr_uid");
		
		mBtnPrev = (Button)findViewById(R.id.btn_aw_04_back);
		mBtn_aw_04_dtl_list_open	=	(Button)findViewById(R.id.btn_aw_04_dtl_list_open);
		mBtn_aw_04_dtl_list_close	=	(Button)findViewById(R.id.btn_aw_04_dtl_list_close);
		mBtn_aw_04_dtl_seat			=	(Button)findViewById(R.id.btn_aw_04_dtl_seat);
		mBtn_aw_04_dtl_qa			=	(Button)findViewById(R.id.btn_aw_04_dtl_qa);			//질의답변 
		mBtn_aw_04_dtl_other		=	(Button)findViewById(R.id.btn_aw_04_dtl_other);			//추게시판명
		
		mTv_aw_04_dtl_mbrname		=	(TextView)findViewById(R.id.tv_aw_04_dtl_mbrname);		//의원이름
		mTv_aw_04_dtl_part			=	(TextView)findViewById(R.id.tv_aw_04_dtl_part); 		//소속정당, 출신구
		
		mBtnPrev.setOnClickListener(this);
		mBtn_aw_04_dtl_list_open.setOnClickListener(this);
		mBtn_aw_04_dtl_list_close.setOnClickListener(this);
		mBtn_aw_04_dtl_seat.setOnClickListener(this);
		mBtn_aw_04_dtl_qa.setOnClickListener(this);
		mBtn_aw_04_dtl_other.setOnClickListener(this);
		
		mBtn_aw_04_dtl_qa.setBackgroundResource(R.drawable.bt_s8_tab1_selected);

		// 그룹 클릭 했을 경우 이벤트
		mListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
//				Toast.makeText(getApplicationContext(), "g click = " + groupPosition, 
//						Toast.LENGTH_SHORT).show();
				
				if(mIsQaFlag){
					String groupFilePath	=	mGroupFilePath.get(groupPosition);
					String groupFileName	=	mGroupFileName.get(groupPosition);
					if(!groupFilePath.equals("")){
						DzImageViewer(groupFileName,groupFilePath);
						Log.i(LOGTAG,"GroupFilePath="+groupFilePath);	
					}
				}else{
					String groupMasterId	=	mGroupMasterDocId.get(groupPosition);
					if(mBaseExpandableAdapter.getChildrenCount(groupPosition)==0){
						Intent intent	=	new Intent(Aw04DtlActivity.this,Aw04AnswerActivity.class);
						intent.putExtra("masterDocId", groupMasterId);
						startActivity(intent);	
					}
					
					Log.i(LOGTAG,"groupMasterId="+groupMasterId);
				}
				
				
				return false;
			}
		});

		// 차일드 클릭 했을 경우 이벤트
		mListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				if(mIsQaFlag){
					ArrayList<String> arrFilePath	=	mChildFilePath.get(groupPosition);
					ArrayList<String> arrFileName	=	mChildFileName.get(groupPosition);
					String childFilePath	=	arrFilePath.get(childPosition);
					String childFileName	=	arrFileName.get(childPosition);
					if(!childFilePath.equals("")){
						DzImageViewer(childFileName ,childFilePath);
						Log.i(LOGTAG,"GroupFilePath="+childFilePath);	
					}
				}else{
					ArrayList<String> arrMasterDocId	=	mMasterDocId.get(groupPosition);
					String masterDocId	=	arrMasterDocId.get(childPosition);
					
					Intent intent	=	new Intent(Aw04DtlActivity.this,Aw04AnswerActivity.class);
					intent.putExtra("masterDocId", masterDocId);
					startActivity(intent);
					
					Log.i(LOGTAG, "masterDocId="+masterDocId);
				}
				
				return false;
			}
		});

		// 그룹이 닫힐 경우 이벤트
		mListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
//				Toast.makeText(getApplicationContext(), "g Collapse = " + groupPosition, 
//						Toast.LENGTH_SHORT).show();
			}
		});

		// 그룹이 열릴 경우 이벤트
		mListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
//				Toast.makeText(getApplicationContext(), "g Expand = " + groupPosition, 
//						Toast.LENGTH_SHORT).show();
			}
		});
		
		getData();
	}
	
	
	//	문서뷰어 연동 
	public void DzImageViewer(String fileName ,String filePath){
		
		Intent intent = new Intent(Aw04DtlActivity.this, handyhis.dz.viewer.DzImageViewer.DzImageViewer.class);
	   String ext = fileName.substring(fileName.lastIndexOf("."));
	   long tmp = System.currentTimeMillis();
	   
	   String tmpName = String.valueOf(tmp) + ext;
	   Log.i(LOGTAG, tmpName);
	   
	   Log.i(LOGTAG, "filePath= "+ filePath);
	   
	   String temp = Global.DZCSURLPREFIX;
	   temp += "fileName=" + URLEncoder.encode(tmpName) + "&filePath=" + URLEncoder.encode(filePath);
	   Log.i(LOGTAG, temp);
	   Log.i(LOGTAG, "doc server is " + Global.DZCSURL);
	   intent.putExtra("baseDzcsUrl", Global.DZCSURL);
	   intent.putExtra("URL", temp);
	   startActivity(intent);
	}
	
	public void getData()
	{
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw04DtlActivity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();
		
		String temp = Global.SERVER_URL; 	// + "/" + Global.JSP_STCHRT;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SMQNA));
		list.add(new SendQueue("smrt_mas_uid", mSmrt_mas_uid));
		list.add(new SendQueue("smrt_arr_uid", mSmrt_arr_uid));
		list.add(new SendQueue("assem_mbr_uid", mAssem_mbr_uid));

		Log.i(LOGTAG, "mSmrt_mas_uid = "+mSmrt_mas_uid);
		Log.i(LOGTAG, "mSmrt_arr_uid = "+mSmrt_arr_uid);
		Log.i(LOGTAG, "mAssem_mbr_uid = "+mAssem_mbr_uid);

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
					mManager = XmlParserManager.parsingSmQna(getApplicationContext(), result);

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
			case R.id.btn_aw_04_back:			// 	이전 버튼 
			{
				finish();
			}
			break;
			case R.id.btn_aw_04_dtl_list_open:
			{
				int groupCount =  mBaseExpandableAdapter.getGroupCount();
				for (int i = 0; i < groupCount; i++) {
					mListView.expandGroup(i);
				}
			}
			break;
			case R.id.btn_aw_04_dtl_list_close:
			{
				int groupCount =  mBaseExpandableAdapter.getGroupCount();
				for (int i = 0; i < groupCount; i++) {
					mListView.collapseGroup(i);
				}
			}
			break;
			case R.id.btn_aw_04_dtl_seat:		//의원선택
			{
				finish();
			}
			break;
			case R.id.btn_aw_04_dtl_qa:		//질의답변
			{
				mIsQaFlag	=	true;
				mBtn_aw_04_dtl_qa.setBackgroundResource(R.drawable.bt_s8_tab1_selected);
				mBtn_aw_04_dtl_other.setBackgroundResource(R.drawable.bt_s8_tab1_def);
				getData();
				
			}
			break;
			case R.id.btn_aw_04_dtl_other:		//다른 게시판. 
			{
				mIsQaFlag	=	false;
				mBtn_aw_04_dtl_qa.setBackgroundResource(R.drawable.bt_s8_tab1_def);
				mBtn_aw_04_dtl_other.setBackgroundResource(R.drawable.bt_s8_tab1_selected);
				getData();
			}
			break;
			
		}
	}
	/*
	 * Layout
	 */
	private ExpandableListView mListView;

	private void setLayout(){
		mListView = (ExpandableListView) findViewById(R.id.elv_list);
	}
}