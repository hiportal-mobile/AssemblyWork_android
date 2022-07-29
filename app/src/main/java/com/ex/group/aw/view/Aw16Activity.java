package com.ex.group.aw.view;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.SearchManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.SearchInfo;

/*
 	화면명: 자료검색리스트
	작성자: 방종희
	DESC: 
	DATE: 2013.04.17
	VERSION: 0.1
 */
public class Aw16Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw16Activity";
	private int mTotalPageCnt	=	0	;
	private int mPageSize	=	0	;
	private Spinner mSpSearch			;
	private int mSearchIndex			;
	private Button mBtnPrev				;	//	이전버튼
	private String mGbncd =""			;	//	요청자료등록 기본코드 : aams_task_mas_type_code	
	private String mGbnName =""			;	// 	요청자료등록 기본명칭
	private String mTask_mas_uid		;	//	국회업무 마스터키
	private String mAssem_mbr_uids		;	//	국회의원키
	
	private TextView mTv_aw_16_titlebar	;	// 	상단타이틀바
	private EditText mEt_aw_16_search	;	// 	검색키워드
	private Button mBtn_aw_16_search	;	//	검색버튼
	private Button mBtn_aw_16_dr		;	//	요구자료
	private Button mBtn_aw_16_qa		;	//	질의답변
	private Button mBtn_aw_16_wr		;	//	서면답변
	private Button mBtn_aw_16_ir		;	//	국감결과
	private Button mBtn_aw_16_ip		;	//	예상QNA
	private Button mㅠtn_aw_16_search_dtl;	//	상세검색

	private int mCurPageNo	=	0;
	
	private String mTaskDistMainUid	=	"";		//	대항목 배부정보키
	private String mSearchSDate		=	"";		//	상세검색 시작일
	private String mSearchEDate		=	"";		//	상세검색 종료일
	private String mSearchPartCode	=	"";		//	상세검색 정당코드 
	private String mSearchKeyword	=	"";		//	검색어	

	private SearchManager mManager	= new SearchManager();

	private AwCustomPopup popup;				//	에러 팝업 

	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	
	private Aw16ArrayAdapter mAdapter;			//	어뎁터
	ListView mLvList;							//	자료검색 목록
	private View footer ;						// 	푸터
	private View notThingFooter;				//	자료 없음 Footer
	private TextView tv_empty;
	
	public void initData(){
		mManager			=	new SearchManager();
		mSearchSDate		=	"";		//	상세검색 시작일
		mSearchEDate		=	"";		//	상세검색 종료일
		mSearchPartCode		=	"";		//	상세검색 정당코드 
		mSearchKeyword		=	"";		//	검색어	
		mTaskDistMainUid	=	"";		//	대항목 배부정보키
//		mSearchIndex 		= 	0;		//	검색구분 index
		mCurPageNo			=	0;		//	현재페이지 번호.
		mEt_aw_16_search.setText("");
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
			break;
			case Global.MSG_SUCEEDED:
			{
				//리스트뷰에 내용으 표시한다. 
				if(mCurPageNo==1){
					setData(false);
				}else{
					setData(true);
				}
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


	private Spinner.OnItemSelectedListener onSpinnerSearch = new Spinner.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg2) {

			case 0:
				mSearchIndex = 0;		//요청자
				break;		
			case 1:
				mSearchIndex = 1;		//제목 내용
				break;
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_16);
		Log.d("test","test1");
		
		Bundle extra = getIntent().getExtras();
		if(extra!=null){
			mTaskDistMainUid	= extra.getString("task_dist_main_uid");
			mGbncd				= extra.getString("mGbncd");
			mGbnName			= extra.getString("mGbnName");
			Log.i(LOGTAG, "mTaskDistMainUid111="+mTaskDistMainUid);
			Log.i(LOGTAG, "mGbncd="+mGbncd);
			Log.i(LOGTAG, "mGbnName="+mGbnName);
		}

		mSpSearch		= 	(Spinner)findViewById(R.id.sp_aw_16_search);			
		mLvList 		=	(ListView)findViewById(R.id.lv_aw_16_request_list);	
		mBtnPrev		=	(Button)findViewById(R.id.btn_16_back);				
		mBtn_aw_16_dr	=	(Button)findViewById(R.id.btn_aw_16_dr);
		mBtn_aw_16_qa	=	(Button)findViewById(R.id.btn_aw_16_qa);	
		mBtn_aw_16_wr	=	(Button)findViewById(R.id.btn_aw_16_wr);	
		mBtn_aw_16_ir	=	(Button)findViewById(R.id.btn_aw_16_ir);	
		mBtn_aw_16_ip	=	(Button)findViewById(R.id.btn_aw_16_ip);	
		mEt_aw_16_search=	(EditText)findViewById(R.id.et_aw_16_search);
		mBtn_aw_16_search	=	(Button)findViewById(R.id.btn_aw_16_search);	
		mTv_aw_16_titlebar	=	(TextView)findViewById(R.id.tv_aw_16_titlebar);
		mㅠtn_aw_16_search_dtl	=	(Button)findViewById(R.id.btn_aw_16_search_dtl);	

		mBtnPrev.setOnClickListener(this);
		mBtn_aw_16_dr.setOnClickListener(this);
		mBtn_aw_16_qa.setOnClickListener(this);
		mBtn_aw_16_wr.setOnClickListener(this);
		mBtn_aw_16_ir.setOnClickListener(this);
		mBtn_aw_16_ip.setOnClickListener(this);
		mBtn_aw_16_search.setOnClickListener(this);
		mㅠtn_aw_16_search_dtl.setOnClickListener(this);

		mSpSearch.setPrompt("검색구분");
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.arr_aw_search, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpSearch.setAdapter(adapter);
		mSpSearch.setOnItemSelectedListener(onSpinnerSearch);
		mSpSearch.setSelection(1);
		
//		자료 없음 푸터
		
		LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		notThingFooter =  inflater2.inflate(R.layout.layout_listview_footer_notthing, null);
		
		tv_empty = (TextView) findViewById(R.id.tv_empty);
		
		/*
		  세부항목 출력시 타이틀을 숨긴다. 
		  요청구분 버튼을 숨긴다.
		 */
		
		if(mTaskDistMainUid.equals("")){
			
			mTv_aw_16_titlebar.setText(R.string.s_search);
		
		}else{
		
			mTv_aw_16_titlebar.setText("자료검색 세부항목");
			
			//검색창 숨김
			LinearLayout LinearLayout02	=	(LinearLayout)findViewById(R.id.LinearLayout02);
			LinearLayout02.setVisibility(View.GONE);
			
			//상단 요청항목 버튼 숨김
			LinearLayout LinearLayout16	=	(LinearLayout)findViewById(R.id.LinearLayout16);
			LinearLayout16.setVisibility(View.GONE);
		}
		// 푸터 생성
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer =  inflater.inflate(R.layout.layout_listview_footer_more, null);
		TextView moreBtn = (TextView)footer.findViewById(R.id.btn_more);
		
//		더보기 호출 	
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mCurPageNo++;	//	가져올 페이지 번호 
				getSearchList();	//	리스트 데이터 
			}
		});
		
		mLvList.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{

				}
			}
		);
		
		//
		mBtn_aw_16_dr.setBackgroundResource(R.drawable.yogu_on);
		
		

		getSearchList();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_16_back:				//	이전
			finish();
			break;
		case R.id.btn_aw_16_search:			//	검색
		{
			mSearchKeyword	=	mEt_aw_16_search.getText().toString().trim();
			if(mSearchIndex==-1){
				Toast.makeText(Aw16Activity.this, "검색구분을 선택해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(mSearchKeyword.equals("")){
				Toast.makeText(Aw16Activity.this, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			mManager	=	new SearchManager();
			mCurPageNo = 1;
			getSearchList();
		}
		break;
		case R.id.btn_aw_16_search_dtl:		// 상세검색
		{
			startActivityForResult((new Intent(Aw16Activity.this, Aw10Activity.class)),10);
		}
		break;
		case R.id.btn_aw_16_dr:
			initData();
			mGbncd		=	"DR";
			mGbnName	=	"요구자료";
			getSearchList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_16_qa:
			initData();
			mGbncd		=	"QA";
			mGbnName	=	"질의답변";
			getSearchList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_16_wr:
			initData();
			mGbncd		=	"WR";
			mGbnName	=	"서면답변";
			getSearchList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_16_ir:
			initData();
			mGbncd		=	"IR";
			mGbnName	=	"국감결과";
			getSearchList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_16_ip:
			initData();
			mGbncd	=	"IP";
			mGbnName	=	"예상Q&ampA;";
			getSearchList();
			setBtnImg(mGbncd);
			break;

		default:
			break;
		}
	}
	
	private void setBtnImg(String Gubun){
		//이미지 변경 
		mBtn_aw_16_dr.setBackgroundResource(R.drawable.yogu_off);
		mBtn_aw_16_qa.setBackgroundResource(R.drawable.qna_off);
		mBtn_aw_16_wr.setBackgroundResource(R.drawable.seomyun_off);
		mBtn_aw_16_ir.setBackgroundResource(R.drawable.kukgam_off);
		mBtn_aw_16_ip.setBackgroundResource(R.drawable.yesang_off);
		if(Gubun.equals("DR")){
			mBtn_aw_16_dr.setBackgroundResource(R.drawable.yogu_on);
		}else if(Gubun.equals("QA")){
			mBtn_aw_16_qa.setBackgroundResource(R.drawable.qna_on);
		}else if(Gubun.equals("WR")){
			mBtn_aw_16_wr.setBackgroundResource(R.drawable.seomyun_on);
		}else if(Gubun.equals("IR")){
			mBtn_aw_16_ir.setBackgroundResource(R.drawable.kukgam_on);
		}else if(Gubun.equals("IP")){
			mBtn_aw_16_ip.setBackgroundResource(R.drawable.yesang_on);
		}
	}
	
	private void setData(boolean more)
	{	//1:false 2> true
		Log.i(LOGTAG, "more : "+more);
		Log.i(LOGTAG, "total count : "+mManager.total_count);
		Log.i(LOGTAG, "mManager.count : "+mManager.count);
		
//		더보기 보이기 숨기기 
			mLvList.removeFooterView(footer);	
			mLvList.removeFooterView(notThingFooter);

//			if(mManager.total_count>mCurPageNo*mManager.count){
//				Log.i(LOGTAG, "더보기....");
//				mLvList.addFooterView(footer);
//			}
			
//		if(more){ //2페이지 이상	
//			Log.i(LOGTAG, "more == true");
			if(mManager.total_count<1){
				try {
					Log.i(LOGTAG, "total count < 1");
					mLvList.removeFooterView(footer);
					mLvList.setVisibility(View.GONE);
					tv_empty.setVisibility(View.VISIBLE);
					
				}catch (Exception e) {
					// TODO: handle exception
					String err	=	e.toString();
					Log.e(LOGTAG, "notthingFoorer add fail.. error : "+err);
				}
			}
			
			else{
				mLvList.setVisibility(View.VISIBLE);
				tv_empty.setVisibility(View.GONE);
				if(mManager.total_count>mCurPageNo*mManager.count){
					mLvList.addFooterView(footer);
				}
			}
//		}
			mAdapter = new Aw16ArrayAdapter(Aw16Activity.this, mManager.getList());
			if(! more ){		//false : 1, true : 더보기 버튼
				mLvList.setAdapter(mAdapter);
			}
			
//			mLvList.setAdapter(mAdapter);
			// 더보기  리스트 
			mAdapter.notifyDataSetChanged();
	}

	//목록 검색
	/* 
	 * pageno - 페이지 번호
	+ aams_task_mas_type_code - 업무구분코드(DR:요구자료, QA:질의답변, WR:서면답변, IR:국감결과, IP:예상Q&A)
	+ assem_mbr_names - 요청자 or 해당본부
	+ assem_mbr_party_codes - 정당코드
	+ mas_bgn_date_from - 요청일 FROM
	+ mas_bgn_date_to - 요청일TO
	+ title_contents - 제목/내용
	+ task_dist_main_uid - 대항목 배부정보 키
	+ login_id - 사원번호

	 */

	public void getSearchList(){
		
		if(mGbncd.equals(""))mGbncd		=	"DR";
		if(mGbnName.equals(""))mGbnName	=	"요구자료";
		if(mCurPageNo==0){mCurPageNo=1;}
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw16Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		// 자료 검색 API 호출 
		String temp = Global.SERVER_URL ;	//+ "/" + Global.JSP_SEARCH_REQ;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SEARCH_REQ));
		list.add(new SendQueue("pageno",Integer.toString(mCurPageNo)));			//페이지 번호
		
		list.add(new SendQueue("aams_task_mas_type_code", mGbncd));				//업무구부분코드
		if(mSearchIndex == 0){
			list.add(new SendQueue("assem_mbr_names", mSearchKeyword));			//요청자 or 해당본부
		}
		if(!mSearchPartCode.equals("")){
			list.add(new SendQueue("assem_mbr_party_codes", mSearchPartCode));	//정당코드
		}
		if(!mSearchSDate.equals("")){	
			list.add(new SendQueue("mas_bgn_date_from", mSearchSDate));			//상세검색 시작일
		}
		if(!mSearchEDate.equals("")){
			list.add(new SendQueue("mas_bgn_date_to", mSearchEDate));			//상세검색 종료일
		}
		if(mSearchIndex==1){
			list.add(new SendQueue("title_contents", mSearchKeyword));			//내용 제목 
		}
		if(!mTaskDistMainUid.equals("")){
			list.add(new SendQueue("task_dist_main_uid",mTaskDistMainUid));			//대항목 구분 
		}
		
		list.add(new SendQueue("login_id",Global.LOGIN_ID));

		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
			public void run()
			{
				http.SendData();

				String result = http.rcvString.replace("\n", "");
				
//				Log.i(LOGTAG, result);
				
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

					XmlParserManager.parsingSearchList(getApplicationContext(),  result , mManager);

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

	/*
	 리스트 항목 성택시. 
	 1. 대항목을 선택하면 세부항목 검색목록으로.   			Aw16Activity
	 2. 소항목을 선택하면 자료검색 상세 화면으로 이동한다. Aw
	 */	

	
	public void linkDtl(int pos){
		
		int ord						=	Integer.parseInt(mManager.getList().get(pos).ord);
		String task_mas_uid  		=	mManager.getList().get(pos).task_mas_uid ;
		String task_dist_main_uid 	=	mManager.getList().get(pos).task_dist_main_uid;	
		String task_dist_sub_uid  	=	mManager.getList().get(pos).task_dist_sub_uid;	
		
		if(ord==2){
			Intent intent = new Intent(Aw16Activity.this, Aw17Activity.class);
			intent.putExtra("mGbncd", mGbncd);
			intent.putExtra("mGbnName", mGbnName);
			intent.putExtra("task_dist_main_uid",task_dist_main_uid);
			intent.putExtra("task_dist_sub_uid",task_dist_sub_uid);
			intent.putExtra("task_mas_uid", task_mas_uid);
			
			Log.i(LOGTAG, "task_mas_uid = "+task_mas_uid);
			Log.i(LOGTAG, "task_dist_main_uid = "+task_dist_main_uid);
			Log.i(LOGTAG, "task_dist_sub_uid = "+task_dist_sub_uid);

			startActivity(intent);
		}else{
			Intent intent = new Intent(Aw16Activity.this, Aw16Activity.class);
			intent.putExtra("mGbncd", mGbncd);
			intent.putExtra("mGbnName", mGbnName);
			intent.putExtra("task_dist_main_uid",task_dist_main_uid);
			//intent.putExtra("mGbncd",mGbncd);
			
			startActivity(intent);

		}
	}

	class Aw16ArrayAdapter extends ArrayAdapter 
	{
		Context context;
		ArrayList<SearchInfo> list;

		TextView title;
		TextView summary;
		TextView name;
		TextView username;
		

		@SuppressWarnings("unchecked")
		public Aw16ArrayAdapter(Context context, ArrayList<SearchInfo> list) 
		{
			super(context, R.layout.layout_aw_16_list, list);

			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			
			Log.i(LOGTAG, "Aw16ArrayAdapter list size ::: "+ list.size());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(list.size() == 0 ){
				Log.i(LOGTAG, "00000000000000");
				mLvList.removeFooterView(footer);
				mLvList.setVisibility(View.GONE);
				tv_empty.setVisibility(View.VISIBLE);
				return null;
			}
			else{
				tv_empty.setVisibility(View.GONE);
				mLvList.setVisibility(View.VISIBLE);
				
				View row = convertView;
	
				if(row == null)
				{
					LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	
					row =  inflater.inflate(R.layout.layout_aw_16_list, null);
				}
	
				title 	= (TextView)row.findViewById(R.id.tv_aw_05_list_title);
				summary = (TextView)row.findViewById(R.id.tv_aw_05_list_content);
				name 	= (TextView)row.findViewById(R.id.tv_aw_05_list_assemblyman);
				username= (TextView)row.findViewById(R.id.tv_aw_05_list_username);
	
				String tempTitle = "";
				
				SearchInfo info =  list.get(position);
				if( info == null )
					return row;
				
				tempTitle	=	info.tit;
	
				String tempSummary = "";
				tempSummary = info.mas_bgn_date+ "/";
				tempSummary	+= 	info.mas_end_date + "/";
				tempSummary	+= 	info.status;
	
				mTask_mas_uid	=	info.task_mas_uid;
	
				title.setText(tempTitle);
				summary.setText(tempSummary);
				name.setText(info.assem_mbr_names);
				username.setText(info.main_rec_user_names);
				
				name.setTag(info.assem_mbr_uids);
				summary.setTag(position);
				title.setTag(position);
				//			의원명 --> 의원 상세정보
				name.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						String assem_mbr_uids	=	(String) v.getTag();
						Intent intent	=	new Intent(Aw16Activity.this,Aw03Activity.class);
						intent.putExtra("mAssem_mbr_uids",assem_mbr_uids);
						startActivity(intent);
					}
				});
	
				//	내용 --> 자료검색 상세화면
				summary.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int position = Integer.parseInt( (v.getTag().toString()) );
						linkDtl(position);
					}
				});
	
				//	제목 --> 자료검색 상세화면
				title.setOnClickListener(new OnClickListener() {
	
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int position = Integer.parseInt( (v.getTag().toString()) );
						linkDtl(position);
					}
				});
				
				return row;
			}
		}
	}	
	
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){
			//		상세검색
			if(requestCode==10){
				mSearchSDate	=	data.getStringExtra("aw_10_searchSdate");  		//	상세검색 시작일
				mSearchEDate	=	data.getStringExtra("aw_10_searchEdate"); 		//	상세검색 종료일
				mSearchPartCode	=	data.getStringExtra("aw_10_searchPartCode"); 	//	상세검색 정당코드 
			
				mManager	=	new SearchManager();
				getSearchList();
			}
		}
	}

}
