package com.ex.group.aw.view;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.SearchDtlManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;

/*
 	화면명: 자료검색리스트 > 상세페이지
	작성자: 방종희
	DESC: 
	DATE: 2013.04.18
	VERSION: 0.1
 */
public class Aw17Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw17Activity";
	private Button mBtnPrev						;		//	이전버튼
	private TextView mTvTitleBar				;		//	타이틀바(요청구분 + 요청자명)
	
	private String mGbncd ="DR"					;		//	요청자료등록 기본코드
	private String mGbnName ="요구자료"			;		//	요청자료등록 기본명칭
	private String mTask_mas_uid				;		//	국회업무 마스터키
	private String mTask_dist_main_uid			;		//	대항목 배부정보 키
	private String mTask_dist_sub_uid			;		//	세부항목 배부정보 키
	
	private ProgressDialog 	mDlgNetwork			;
	private Thread  thread = null				;
	
	private TextView mTitle						;		//	제목 -
	private ListView mLv_aw_17_resfile			;		//	답변내용 - 답변서 리스트
	private ListView mLv_aw_17_admattcfile		;		//	답변내용 - 첨부파일 
	private TextView mtv_aw_17_sub_title		;		//	세부항목
	private TextView tv_aw_17_mbr 				;		//	요청자
	private TextView tv_aw_17_sdate 			;		//	요청일
	private TextView tv_aw_17_edate 			;		//	취합일자
	private TextView tv_aw_17_attfiles			;		// 	취합파일
	private TextView tv_aw_17_admname 			;		//	담당자이름
	private TextView tv_aw_17_admtel 			;		//	담당자 연락처
	private TextView tv_aw_17_relpart 			;		//	관련분야
	private SearchDtlManager mManager 			;
	private TableRow  reject_sec				;		//	재검토요청  layout
	private TextView tv_aw_17_reject			;		//	재검토메모
	
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
		setContentView(R.layout.layout_aw_17);
		
		Bundle extra = getIntent().getExtras();
		
		mGbncd				= extra.getString("mGbncd");				//	요청자료등록 기본코드
		mGbnName			= extra.getString("mGbnName");				// 	요청자료등록 기본명칭
		mTask_mas_uid		= extra.getString("task_mas_uid");			// 	국회업무 마스터키
		mTask_dist_main_uid	= extra.getString("task_dist_main_uid");	//	대항목 배부정보 키
		mTask_dist_sub_uid	= extra.getString("task_dist_sub_uid");		//	세부항목 배부정보 키
		
		mTvTitleBar 			= 	(TextView)findViewById(R.id.tv_aw_17_titlebar);			// 타이틀바 명칭
		mTitle					=	(TextView)findViewById(R.id.tv_aw_17_title);
		mLv_aw_17_resfile 		=	(ListView)findViewById(R.id.lv_aw_17_resfile);			// 답변서 파일 리스트 
		mLv_aw_17_admattcfile 	=	(ListView)findViewById(R.id.lv_aw_17_admattcfile);		// 첨부파일 리스트
		tv_aw_17_mbr 			= 	(TextView)findViewById(R.id.tv_aw_17_mbr);
		tv_aw_17_sdate 			= 	(TextView)findViewById(R.id.tv_aw_17_sdate);
		tv_aw_17_edate 			= 	(TextView)findViewById(R.id.tv_aw_17_edate);
		tv_aw_17_attfiles  		= 	(TextView)findViewById(R.id.tv_aw_17_attfiles);
		tv_aw_17_admname 		= 	(TextView)findViewById(R.id.tv_aw_17_admname);
		tv_aw_17_admtel			= 	(TextView)findViewById(R.id.tv_aw_17_admtel);
		tv_aw_17_relpart 		= 	(TextView)findViewById(R.id.tv_aw_17_relpart);
		mtv_aw_17_sub_title		=	(TextView)findViewById(R.id.tv_aw_17_sub_title);
		tv_aw_17_reject			=	(TextView)findViewById(R.id.tv_aw_17_reject);
		
		

//		전화걸기		
		tv_aw_17_admtel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mManager.telephone_no.equals("")){
					Intent telIntent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+mManager.telephone_no));
					startActivity(telIntent);
				}
				
			}
		});
//		취합파일  - 문서뷰어 연결 
				tv_aw_17_attfiles.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String mgre_file_name	=	mManager.mgre_file_name;
						String mgre_file_path	=	mManager.mgre_file_path;
//						Log.i(LOGTAG, "mgre_file_path="+mgre_file_path);
						// 문서뷰어 연동
						if(!mgre_file_path.equals("")){
							DzImageViewer(mgre_file_name ,mgre_file_path);
						}
						
						
						
					}
				});
		
//		답변서 문서뷰어 
		mLv_aw_17_resfile.setOnItemClickListener
			(
					new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
						{
							String reffileName	=	mManager.getList().get(position).refile_name.toString();
							String reffilePath	=	mManager.getList().get(position).refile_path.toString();
//							Log.i(LOGTAG, "reffilePath="+reffilePath);
							if(!reffilePath.equals("")){
								DzImageViewer(reffileName ,reffilePath);
							}
						}
					}
					);
//		첨부파일 문서뷰어
		mLv_aw_17_admattcfile.setOnItemClickListener
			(
					new OnItemClickListener()
					
					{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
						{
							String attcfileName	=	mManager.getList2().get(position).file_name.toString();
							String attcfilePath	=	mManager.getList2().get(position).file_path.toString();
//							Log.i(LOGTAG, "attcfilePath="+attcfilePath);
							if(!attcfileName.equals("")){
								DzImageViewer(attcfileName ,attcfilePath);
							}
							
						}
					}
					);
		mBtnPrev = (Button)findViewById(R.id.btn_17_back);
		mBtnPrev.setOnClickListener(this);
				
		getSearchDtl();
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_17_back:
				finish();
			break;

		default:
			break;
		}
	}
	
	private void setData()
	{
		
		if(mGbncd.equals("IP")){
			tv_aw_17_mbr.setText(mManager.ex_center_names);
		}else{
			//요청자
			tv_aw_17_mbr.setText(mManager.assem_mbr_names);
		}
		//요청일
		tv_aw_17_sdate.setText(mManager.mas_bgn_date);
		//취합일자
		tv_aw_17_edate.setText(mManager.hist_ins_date);
		Log.i(LOGTAG, "hist_ins_date="+mManager.hist_ins_date);
		//취합파일
		tv_aw_17_attfiles.setText(mManager.mgre_file_name);
		//제목
		mTitle.setText(mManager.main_tit);
		//담당자
		tv_aw_17_admname.setText(mManager.re_user_name);
		//담당자연락처
		tv_aw_17_admtel.setText(mManager.telephone_no);
		//관련분야
		tv_aw_17_relpart.setText(mManager.sub_rel_fld_name_path	);
		//타이틀 바 설정
		String TitlebarTxt	=	String.format("%s(%s)",mGbnName,mManager.assem_mbr_names);	
		mTvTitleBar.setText(TitlebarTxt);
		
		TextView tv_aw_17_mbr_title		=	(TextView)findViewById(R.id.tv_aw_17_mbr_title);	//요청자 title
	    reject_sec						=	(TableRow)findViewById(R.id.reject_sec);			//제검토요청 항목 tableRow		
	    TableRow layout_tablerow		=	(TableRow)findViewById(R.id.layout_tablerow);		//질의답변외 요청/취합일자  tableRow
	    TableRow layout_tablerow_qa		=	(TableRow)findViewById(R.id.layout_tablerow_qa);	//질의답변 요청일자 tableRow.
	    TextView tv_aw_17_sdate_qa		=	(TextView)findViewById(R.id.tv_aw_17_sdate_qa);		//질의답변 요청일자 content.
		reject_sec.setVisibility(View.GONE);
		layout_tablerow.setVisibility(View.GONE);
		layout_tablerow_qa.setVisibility(View.GONE);
		
		// 요청자 타이틀 설정
		if(mGbncd.equals("DR")){//요구 자료
			tv_aw_17_mbr_title.setText("요청자");
		}else if(mGbncd.equals("QA")){//질의 답변
			tv_aw_17_mbr_title.setText("질의의원");
		}else if(mGbncd.equals("IR")) {//국감결과
			tv_aw_17_mbr_title.setText("지적의원");
		}else if(mGbncd.equals("IP")) {//예상QA
			tv_aw_17_mbr_title.setText("해당본부");
		}else{
			tv_aw_17_mbr_title.setText("질의의원");
		}
		
		//요청일 설정
		if(mGbncd.equals("QA")){
			layout_tablerow_qa.setVisibility(View.VISIBLE);
			tv_aw_17_sdate_qa.setText(mManager.mas_bgn_date);
		}else{
			layout_tablerow.setVisibility(View.VISIBLE);
		}
		
		//답변서 목록
		Aw17ArrayAdapter adapter	=	new Aw17ArrayAdapter(Aw17Activity.this);
		mLv_aw_17_resfile.setAdapter(adapter);
		
		//첨부파일
		Aw17ArrayAdapter2 adapter1	=	new Aw17ArrayAdapter2(Aw17Activity.this);
		mLv_aw_17_admattcfile.setAdapter(adapter1);
		
		mtv_aw_17_sub_title.setText(mManager.sub_tit );
		
		if(!mManager.main_rej_memo.equals("")){
			reject_sec.setVisibility(View.VISIBLE);
			tv_aw_17_reject.setText(mManager.main_rej_memo);
		}

		Log.i(LOGTAG, mManager.mgre_file_path);
	}
	
	//	문서뷰어 연동 
	public void DzImageViewer(String fileName ,String filePath){
		
		Intent intent = new Intent(Aw17Activity.this, handyhis.dz.viewer.DzImageViewer.DzImageViewer.class);
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
	
	// 상세페이지 데이터
	public void getSearchDtl(){
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw17Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();
		
		list.clear();
		String temp = Global.SERVER_URL	;	// + "/" + Global.JSP_SEARCH_DETAIL;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SEARCH_DETAIL));
		list.add(new SendQueue("aams_task_mas_type_code", mGbncd));
		list.add(new SendQueue("task_mas_uid", mTask_mas_uid));
		list.add(new SendQueue("task_dist_main_uid", mTask_dist_main_uid));
		list.add(new SendQueue("task_dist_sub_uid", mTask_dist_sub_uid));

		Log.i(LOGTAG, "mTask_mas_uid="+mTask_mas_uid);
		Log.i(LOGTAG, "mTask_dist_main_uid="+mTask_dist_main_uid);
		Log.i(LOGTAG, "mTask_dist_sub_uid="+mTask_dist_sub_uid);
		
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

					mManager	=	XmlParserManager.parsingSearchDtl(getApplicationContext(), result);
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

	class Aw17ArrayAdapter extends ArrayAdapter 
	{
    	Context		context;
    	TextView	TvTitle;
    	
		@SuppressWarnings("unchecked")
		public Aw17ArrayAdapter(Context context) 
		{
			super(context, R.layout.layout_aw_17_list, mManager.getList());
			
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
			
			TvTitle	=	(TextView)row.findViewById(R.id.tv_aw_17_list_content) ;
			String contents	=	""	;
			contents	=	mManager.getList().get(position).refile_name;
			TvTitle.setText(contents);
			return row;
		}
	}	
	
	class Aw17ArrayAdapter2 extends ArrayAdapter 
	{
    	Context		context;
    	TextView	TvTitle;
    	
		@SuppressWarnings("unchecked")
		public Aw17ArrayAdapter2(Context context) 
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
			
			TvTitle	=	(TextView)row.findViewById(R.id.tv_aw_17_list_content) ;
			String contents	=	""	;
			contents	=	mManager.getList2().get(position).file_name.toString();
			
			TvTitle.setText(contents);
			
			
			return row;
		}
	}	
	

	@Override
	protected void onDestroy()
	{
		
		if( mDlgNetwork!=null)
		{
			mDlgNetwork.dismiss();
		}
		
		super.onDestroy();
		Log.i(LOGTAG, "onDestroy()");
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i(LOGTAG, "onResume()");
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		Log.i(LOGTAG, "onRestart()");
		// TODO Auto-generated method stub
	}
	
}
