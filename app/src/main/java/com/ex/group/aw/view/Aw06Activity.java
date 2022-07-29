package com.ex.group.aw.view;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.InsertWriterManager;
import com.ex.group.aw.manager.RequestWriterInfoManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
/*
	화면명: 작성자지정
	작성자: 방종희
	DESC: 
	DATE: 2013.04.15
	VERSION: 0.1
 */
public class Aw06Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw06Activity";
	private Button mBtnPrev;
	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private RequestWriterInfoManager mManager = null;
	private InsertWriterManager mManager2	=	null;
	private static final int MAX_HEIGHT = 110;

	//layout item 
	private TextView mTv_aw_06_mbr;				//요청자
	private TextView mTv_aw_06_aside;			//보좌관
	private TextView mTv_aw_06_sdate;			//요청일
	private TextView mTv_aw_06_edate;			//제출기한
	private ListView mLv_aw_06_attfile_name;	//첨부파일
	private CheckBox mCk_aw_06_sms;				//SMS 발송
	private TextView mTv_aw_06_title_content;	//제목
	private ListView mLv_aw_06_subtitle	;		//세부항목
	private TextView mTv_aw_06_req_content;		//요청내용
	private Button mBtn_aw_06_add;				//추가버튼
	private Button mBtn_aw_06_recondider;		//재검토요청
	private Button mBtn_aw_06_cancel;			//취소
	private Button mBtn_aw_06_reg;				//확인
	private ScrollView main_box_scrollview;
	private AwCustomPopup popup;
	
	ArrayList<HashMap<String, String>> mSubData = new ArrayList<HashMap<String, String>>();		//	세부항목 

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
			break;
			case Global.MSG_SUCEEDED:
			{
				//작성자 지정조회 후 데이터 출력 
				setData();
				Log.i(LOGTAG, "setData");
			}
			break;
			case Global.MSG_SUCEEDED_2:
			{
				//작성자 지정 등록 후 이벤트
				startActivity(new Intent(Aw06Activity.this,Aw05Activity.class));
				Log.i(LOGTAG, "sentdate");
				finish();
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
		setContentView(R.layout.layout_aw_06);

		mBtnPrev 				=	(Button)findViewById(R.id.btn_06_back);
		mTv_aw_06_mbr			=	(TextView)findViewById(R.id.tv_aw_06_mbr);				//요청자
		mTv_aw_06_aside			=	(TextView)findViewById(R.id.tv_aw_06_aside);			//보좌관
		mTv_aw_06_sdate			=	(TextView)findViewById(R.id.tv_aw_06_sdate);			//요청일
		mTv_aw_06_edate			=	(TextView)findViewById(R.id.tv_aw_06_edate);			//제출기한
		mLv_aw_06_attfile_name	=	(ListView)findViewById(R.id.lv_aw_06_attfile_name);		//첨부파일
		mCk_aw_06_sms			=	(CheckBox)findViewById(R.id.ck_aw_06_sms);				//SMS 발송
		mTv_aw_06_title_content	=	(TextView)findViewById(R.id.tv_aw_06_title_content);	//제복	
		mLv_aw_06_subtitle		=	(ListView)findViewById(R.id.lv_aw_06_subtitle);			//세부항목
		mBtn_aw_06_add			=	(Button)findViewById(R.id.btn_aw_06_add);				//추가버튼
		mBtn_aw_06_recondider	=	(Button)findViewById(R.id.btn_aw_06_recondider);		//재검토요청
		mBtn_aw_06_cancel		=	(Button)findViewById(R.id.btn_aw_06_cancel);			//취소
		mBtn_aw_06_reg			=	(Button)findViewById(R.id.btn_aw_06_reg);				//확인
		mTv_aw_06_req_content	=	(TextView)findViewById(R.id.tv_aw_06_req_content);		//요청내용
		main_box_scrollview		=	(ScrollView)findViewById(R.id.ScrollView01);			//스크롤뷰 
		
		mBtn_aw_06_add.setOnClickListener(this);
		mBtn_aw_06_recondider.setOnClickListener(this);
		mBtn_aw_06_cancel.setOnClickListener(this);
		mBtn_aw_06_reg.setOnClickListener(this);
		mBtnPrev.setOnClickListener(this);

		// 첨부파일 선택
		mLv_aw_06_attfile_name.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
					//Log.i(LOGTAG, "file_uid="+mManager.getList2().get(position).file_uuid );
					String fileName	=	mManager.getList2().get(position).file_name;
					String filePath	=	mManager.getList2().get(position).file_path;
					
					Log.d(LOGTAG, "filePath="+filePath);
					Log.d(LOGTAG, "fileName="+fileName);
					
					DzImageViewer(fileName ,filePath);
				}
			}
		);
		//세부항목 선택
		mLv_aw_06_subtitle.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
					//Log.i(LOGTAG, "file_uid="+mManager.getList2().get(position).file_uuid );
				}
			}
		);

		// 첨부파일 스크롤 가능하게 
		mLv_aw_06_attfile_name.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				main_box_scrollview.requestDisallowInterceptTouchEvent(true);

				return false;

			}

		});

		getData();
	}

	public void setData(){
		mTv_aw_06_mbr.setText(mManager.assem_mbr_names);			//요청의원이름 
		mTv_aw_06_aside.setText(mManager.assem_aide_names);			//보좌관 이름
		mTv_aw_06_sdate.setText(mManager.mas_bgn_date);				//요청일
		mTv_aw_06_edate.setText(mManager.mas_end_date);				//제출기한
		mTv_aw_06_title_content.setText(mManager.main_tit);			//제목
		mTv_aw_06_req_content.setText(mManager.main_req_memo );		//요청내용
		String mGbncd	=	mManager.mas_type_code;

		TableRow tableRow1_dr_ip	=	(TableRow)findViewById(R.id.tableRow1_dr_ip);		//요구자료, 예상Q&A 요청자
		TableRow tableRow1_qr		=	(TableRow)findViewById(R.id.tableRow1_qr);			//질의답변~국감결과 요청자
		TableRow tableRow2_dp		=	(TableRow)findViewById(R.id.tableRow2_dp);			//질의답변을 제외한 요청/체출기한
		TableRow tableRow2_qa		=	(TableRow)findViewById(R.id.tableRow2_qa);			//질의답변 요청일
		TextView tv_aw_06_mbr_title	=	(TextView)findViewById(R.id.tv_aw_06_mbr_title);	// 요청자/해당부서 타이틀 
		TextView tv_aw_06_aside_title=	(TextView)findViewById(R.id.tv_aw_06_aside_title);	// 보좌관/관련의원 타이틀
		TextView tv_aw_06_mbr_title_qr	=	(TextView)findViewById(R.id.tv_aw_06_mbr_title_qr);	// 질의/지적의원 타이틀
		TextView tv_aw_06_mbr_qr	=	(TextView)findViewById(R.id.tv_aw_06_mbr_qr);		//질의/지적의원 
		TextView tv_aw_06_sdate_qa	=	(TextView)findViewById(R.id.tv_aw_06_sdate_qa);		//질의 답변 요청일 
		
		tableRow1_dr_ip.setVisibility(View.GONE);
		tableRow1_qr.setVisibility(View.GONE);
		tableRow2_dp.setVisibility(View.GONE);
		tableRow2_qa.setVisibility(View.GONE);
		
		//요청자설정
		if(mGbncd.equals("DR")||mGbncd.equals("IP")){
			tableRow1_dr_ip.setVisibility(View.VISIBLE);
			if (mGbncd.equals("IP")) {										//국감결과 
				tv_aw_06_mbr_title.setText(R.string.s_lb_ip_bonnu);
				tv_aw_06_aside_title.setText(R.string.s_lb_ip_re_assemb);
				mTv_aw_06_mbr.setText(mManager.ex_center_names);			//해당본부
				mTv_aw_06_aside.setText(mManager.rel_assem_mbr_names);		//관련의원
			}
		}else{
			tableRow1_qr.setVisibility(View.VISIBLE);
			tv_aw_06_mbr_qr.setText(mManager.assem_mbr_names);
			
			if(mGbncd.equals("IR")){
				tv_aw_06_mbr_title_qr.setText(R.string.s_lb_ir_assemb)	;				//국감결과 지적의원
			}
		}
		
		//요청일 설정
		if(mGbncd.equals("QA")){
			tableRow2_qa.setVisibility(View.VISIBLE);
			tv_aw_06_sdate_qa.setText(mManager.mas_bgn_date);
		}else{
			tableRow2_dp.setVisibility(View.VISIBLE);
		}
		
		
//		Toast.makeText(Aw06Activity.this, "mManager.mas_type_code;= " + mManager.mas_type_code, Toast.LENGTH_LONG).show();
		//첨부파일 리스트 뷰
		Aw06FileArrayAdapter adapter	=	new Aw06FileArrayAdapter(Aw06Activity.this);
		mLv_aw_06_attfile_name.setAdapter(adapter);
	}
	
	//	문서뷰어 연동 
	public void DzImageViewer(String fileName ,String filePath){
		
		Intent intent = new Intent(Aw06Activity.this, handyhis.dz.viewer.DzImageViewer.DzImageViewer.class);
	   String ext = fileName.substring(fileName.lastIndexOf("."));
	   long tmp = System.currentTimeMillis();
	   
	   String tmpName = String.valueOf(tmp) + ext;
	   Log.i(LOGTAG, tmpName);
	   
	   String temp = Global.DZCSURLPREFIX;
	   temp += "fileName=" + URLEncoder.encode(tmpName) + "&filePath=" + URLEncoder.encode(filePath);
	   Log.i(LOGTAG, temp);
	   Log.i(LOGTAG, "doc server is " + Global.DZCSURL);
	   intent.putExtra("baseDzcsUrl", Global.DZCSURL);
	   intent.putExtra("URL", temp);
	   startActivity(intent);

	}
	
	//	작성자 지정 조회 
	public void getData(){

		Bundle extra = getIntent().getExtras();

		String task_mas_uid	=	extra.getString("task_mas_uid");
		String task_dist_main_uid	=	extra.getString("task_dist_main_uid");
		Log.i(LOGTAG, "task_mas_uid ="+task_mas_uid);
		Log.i(LOGTAG, "task_dist_main_uid ="+task_dist_main_uid);

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw06Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.SERVER_URL;	// + "/" + Global.JSP_SET_WRITER_R;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SET_WRITER_R));
		list.add(new SendQueue("task_mas_uid", task_mas_uid));
		list.add(new SendQueue("task_dist_main_uid", task_dist_main_uid));
		list.add(new SendQueue("login_id", Global.LOGIN_ID));

		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
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
					mManager = XmlParserManager.parsingReqWriterInfo(getApplicationContext(), result);

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
	
	//작성자 지정 등록 
	public void sendData(){
	/*
		전달해줄변수 : 
			* sms_yn - SMS발송여부(Y/N)
			* login_id - 접속자ID
			* task_mas_uid - 국회업무마스터 키
			* mas_type_code - 요구자료(DR), 질의답변(QA), 서면답변(WR), 국감결과(IR), 예상Q&A(IP)
			* task_dist_main_uid - 주무자 배부정보 키
			* main_seq_no - 주무자 배부 순번
			* main_no - 요구문항 대항목 번호
			* main_tit - 요구문항 제목
			* main_req_memo - 국회업무 담당자 요청 내용
			* main_sec_code - 보안등급코드
			* main_qst_yn - 자체질문여부
			@배열
			*@ task_dist_sub_uid - 요구문항 작성자 배부 정보 키			
			*@ sub_tit - 요구문항 세부항목 제목
			*@ sub_req_wrt_uid - 요구문항 세부항목 작성자 키
			*@ sub_req_wrt_name - 요구문항 세부항목 작성자 이름
			*
	*/
		
		String sms_yn	=	"N";
		if(mCk_aw_06_sms.isChecked()){
			sms_yn	=	"Y";
		}
		String task_mas_uid		=	mManager.task_mas_uid ;
		String mas_type_code	=	mManager.mas_type_code;
		String task_dist_main_uid=	mManager.task_dist_main_uid;
		String main_seq_no		=	mManager.main_seq_no;
		String main_no			=	mManager.main_no;
		String main_tit			=	mManager.main_tit;
		String main_req_memo	=	mManager.main_req_memo;
		String main_sec_code	=	mManager.main_sec_code;
		String main_qst_yn		=	mManager.main_qst_yn;

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw06Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.SERVER_URL ;// + "/" + Global.JSP_SET_WRITER_C;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SET_WRITER_C));
		list.add(new SendQueue("sms_yn", sms_yn));
		list.add(new SendQueue("login_id", Global.LOGIN_ID));
		list.add(new SendQueue("task_mas_uid", task_mas_uid));
		list.add(new SendQueue("mas_type_code", mas_type_code));
		list.add(new SendQueue("task_dist_main_uid", task_dist_main_uid));
		list.add(new SendQueue("main_seq_no", main_seq_no));
		list.add(new SendQueue("main_no", main_no));
		list.add(new SendQueue("main_tit", main_tit));
		list.add(new SendQueue("main_req_memo", main_req_memo));
		list.add(new SendQueue("main_sec_code", main_sec_code));
		list.add(new SendQueue("main_qst_yn", main_qst_yn));
		
		//새부항목 배열 
		for(int i = 0 ; i < mSubData.size();i++){
			list.add(new SendQueue("task_dist_sub_uid","")); 
			list.add(new SendQueue("sub_tit", mSubData.get(i).get("sub_tit")));
			list.add(new SendQueue("sub_req_wrt_uid", mSubData.get(i).get("sub_req_wrt_uid")));
			list.add(new SendQueue("sub_req_wrt_name", mSubData.get(i).get("sub_req_wrt_name")));
		}
		
		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
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
					mManager2 = XmlParserManager.parsingInsertWrite(getApplicationContext(), result);

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
							msg.what = Global.MSG_SUCEEDED_2;
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

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.btn_06_back:
			finish();
			break;
		case R.id.btn_aw_06_add:		//추가
		{
			Intent addIntentd	=	new Intent(Aw06Activity.this,Aw07Activity.class);
			startActivityForResult(addIntentd, 1);
		}
		break;
		case R.id.btn_aw_06_recondider:	//재검토요청
		{
			String task_mas_uid 		=	mManager.task_mas_uid;			//	국회업무 마스터 키
			String task_dist_main_uid	=	mManager.task_dist_main_uid;	//	주무자 배부정보 키
			/*
			 	전달해줄변수 : 
			 * login_id - 접속자ID
			 * task_mas_uid - 국회업무 마스터 키
			 * task_dist_main_uid - 요구문항 주무자 배부정보 키
			 */	
			Intent rejectIntent	=	new Intent(Aw06Activity.this,Aw09Activity.class);
			rejectIntent.putExtra("task_mas_uid", task_mas_uid); 				//	국회업무 마스터 키
			rejectIntent.putExtra("task_dist_main_uid", task_dist_main_uid);	//	주무자 배부정보 키
			startActivityForResult(rejectIntent, 2);
		}
			break;
		case R.id.btn_aw_06_cancel:		//취소
			finish();
			break;
		case R.id.btn_aw_06_reg:		//등록
		{
			if(mSubData.size()<1){
				Toast.makeText(Aw06Activity.this, "세부항목을 한개 이상 등록해 주세요.", Toast.LENGTH_SHORT).show();
				return ;
			}
			sendData();
		}
			break;
		default:
			break;
		}
	}
	
	//	첨부파일 Adapter
	class Aw06FileArrayAdapter extends ArrayAdapter 
	{
		Context		context;
		TextView	TvTitle;

		@SuppressWarnings("unchecked")
		public Aw06FileArrayAdapter(Context context) 
		{
			super(context, R.layout.layout_aw_17_list, mManager.getList2());

			// TODO Auto-generated constructor stub

			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_17_list, null);
			}

			TvTitle 	= (TextView)row.findViewById(R.id.tv_aw_17_list_content);

			String file_uuid	= mManager.getList2().get(position).file_uuid ; 	//- 요구문항 작성자 배부 정보 키
			String file_name	= mManager.getList2().get(position).file_name ;		//- 요구문항 주무자 배부 정보 키

			//			Log.i(LOGTAG, "task_dist_sub_uid = "+task_dist_sub_uid);

			TvTitle.setText(file_name);

			return row;
		}
	}	

	//	세부항목 추가 Adapter
	class Aw06ArrayAdapter extends ArrayAdapter 
	{
		Context		context;
		TextView	TvTitle;
		TextView	UserName;
		ArrayList<HashMap<String, String>> subData ;
		@SuppressWarnings("unchecked")
		public Aw06ArrayAdapter(Context context,ArrayList<HashMap<String, String>> subData ) 
		{
			super(context, R.layout.layout_aw_06_list, subData);

			// TODO Auto-generated constructor stub

			this.context = context;
			this.subData	=	subData;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_06_list, null);
			}

			TvTitle 	= 	(TextView)row.findViewById(R.id.tv_aw_06_list_title);
			UserName	=	(TextView)row.findViewById(R.id.tv_aw_06_list_content);

			String	sub_tit				=	subData.get(position).get("sub_tit");
			String	sub_req_wrt_name	=	subData.get(position).get("sub_req_wrt_name");
			String tempName				=	String.format("담당자 : %S", sub_req_wrt_name)	;
			TvTitle.setText(sub_tit);
			UserName.setText(tempName);

			TvTitle.setText(sub_tit);

			return row;
		}
	}	
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){
			//		세부항목 추가
			if(requestCode==1){
				String sub_tit			=	data.getStringExtra("sub_tit");
				String sub_req_wrt_uid	=	data.getStringExtra("sub_req_wrt_uid");
				String sub_req_wrt_name	=	data.getStringExtra("sub_req_wrt_name");
			
				HashMap<String, String> items	=	new HashMap<String, String>();
				items.put("sub_tit", sub_tit);
				items.put("sub_req_wrt_uid", sub_req_wrt_uid);
				items.put("sub_req_wrt_name", sub_req_wrt_name);
				mSubData.add(items);
				
				//세부항목
				Aw06ArrayAdapter adapter2	=	new Aw06ArrayAdapter(Aw06Activity.this,mSubData);
				getTitleListheight(adapter2);
				mLv_aw_06_subtitle.setAdapter(adapter2);
			}

			//		재검토요청
			Log.d("test","resultCode="+resultCode);
			if(requestCode==2){
				
				finish();
			}

		}

	}
	
	// 세부항목 리스트뷰 높이 설정
	private void getTitleListheight(Adapter adapter)
	{
		if (adapter != null)
		{
			int count = adapter.getCount();
			if (count > 0)
			{
				int height = MAX_HEIGHT * count;	
				getTitleListheight(height);
			}
		}
	}

	private void getTitleListheight(int height)
	{
		final ListView layout = (ListView)findViewById(R.id.lv_aw_06_subtitle);

		if (layout != null) 
		{
			final ViewGroup.LayoutParams params = layout.getLayoutParams();
			if (params != null) 
			{
				// 현재 뷰의 margin 을 계산한다.
				MarginLayoutParams margin = (MarginLayoutParams)layout.getLayoutParams();
				params.height = height;
				height +=2;
				layout.post(new Runnable()
				{
					@Override
					public void run() 
					{
						layout.setLayoutParams(params);
					}
				});
			}
		}
	}



}
