package com.ex.group.aw.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.AttachFileManager;
import com.ex.group.aw.manager.RegAssemblyManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;

/*
 	화면명: 의원요청등록
	작성자: 방종희
	DESC: 
	DATE: 2013.04.16
	VERSION: 0.1

	 질의답변 
	 - 요청일,  제출기한, 보좌관    없음 

	 예상 Q&A 
	  - 요청자 ==?해당본부 

 */


public class Aw11Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw11Activity";
	private static final int MAX_HEIGHT = 80;
	private static final int MAX_HEIGHT2 = 200;
	private EditText 	mEtSdate;
	private EditText	mEtEdate;
	private Button 		mBtnBack;
	private int         mYear;    
	
	private int         mMonth;    
	private int         mDay;    
	private String		mGbncd;					//	요청구분코드
	private String		mGbnName;				//	요청구분명
	private EditText 	mEt_aw_11_mbr;			//	요청자
	private EditText 	mEt_aw_11_sup;			//	보좌관
	private EditText 	mEt_aw_11_sdate;		//	요청일
	private EditText 	mEt_aw_11_edate;		//	제출기한
	private ListView 	mLv_aw_11_attfile;		//	첨부파일
	private Button 		mBtn_aw_11_search;		// 	검색
	private Button		mBtn_aw_11_filesearch ;	//	파일첨부
	private Button		mBtn_aw_11_cancel ;		// 	취소
	private Button		mBtn_aw_11_add;			//	제목추가
	private Button		mBtn_aw_11_reg;			// 	등록버튼
	private ListView	mLv_aw_11_title;		//	제목리스트
	private CheckBox	mCk_aw_11_sms;			//	sms발송

	//QR   질의답변 - 국감결과
	private TextView	mTv_aw_11_qr_mbr_title;	//	질의위원 타이틀
	private EditText	mEt_aw_11_qr_mbr;		//	질의위원	
	private Button		mBtn_aw_11_qr_search;	//	질의위원 검색
	//예상 Qna
	private EditText 	mEt_aw_11_ip_mbr;		//	해당본부
	private Button		mBtn_aw_11_ip_search;	//	해당본부 검색
	private EditText 	mEt_aw_11_ip_sup;		//	관련의원명
	private Button		mBtn_aw_11_ip_sup_search;//	해당본부 검색

	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private RegAssemblyManager mManager 		=	null;
	private HashMap<String, String> mManagerUpload	=	null;
	private String mAttcFilePath = ""					;	//	첨부파일 경로
	private AttachFileManager mFileManager	=	null	;	//  첨부파일 저장 정보

	ArrayList<HashMap<String, String>> mTitleData = new ArrayList<HashMap<String, String>>();		//	제목리스트 데이터
	//private Aw11ArrayAdapter mAdapter	;
	View tempV;
	ArrayList<HashMap<String, String>> mFileData = new ArrayList<HashMap<String, String>>();		//	첨부파일 데이터

	//요청일 제출기한 tableRow2
	private TableRow tableRow2;


	//  등록시 필요한데이터
	//	1.AW12Activity
	private String msMain_tit			=	""	;		//제목 
	private String msMain_req_memo		=	""	;		//요청내용
	private String msMain_rec_user_uids	=	""	;		//담당자키
	private String msMain_rec_user_names=	""	;		//담당자
	private String msMain_qst_yn		=	""	;		//자체질문여부

	//	2.AW13Activity
	private String msAssem_mbr_uid		=	""	;	//	국회의원키
	private String msMbr_name			=	""	;	//	국회의원이름
	private String msMbr_party_code		=	""	;	//	정당코드
	private String msMbr_party_name		=	""	;	//	정당이름
	private String msAssem_aide_uid 	=	""	;	//	보좌관코드
	private String msAide_name 			= 	""	;	//	보좌관이름

	private String msSms_yn				=	""	;	//	sms 발송 
	private String msMas_bgn_date 		=	""	;	//	요청일 (질의답변 (QA) 인 경우 SYSDATE로 등록)
	private String msMas_end_date 		=	""	;	//	제출기한 (질의답변 (QA) 인 경우 SYSDATE로 등록)
	private String msEx_center_uids 	=	""	;	//	해당본부 키 (예상Q&A (IP) 인 경우 등록)
	private String msEx_center_names 	=	""	;	//	해당본부 이름 (예상Q&A (IP) 인 경우 등록)
	private String msRel_assem_mbr_uids	=	""	; 	//	관련의원키 (예상Q&A (IP) 인 경우 등록)
	private String msRel_assem_mbr_names=	""	; 	//	관련의원이름 (예상Q&A (IP) 인 경우 등록)

	private AwCustomPopup popup;



	static final int DATE_DIALOG_ID = 0;
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
					temp = getApplication().getString(R.string.s_hm_xml_result_data);
					break;
				}
				AlertDialog(temp);
			}
			break;
			case Global.MSG_SUCEEDED:
			{
				switch (msg.arg1) {
				case 1:
				{
					Toast.makeText(Aw11Activity.this, "등록이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
					Intent intent =	getIntent();
					setResult(RESULT_OK,intent);
					finish();
				}
					break;
				case 2:		//첨부파일 등록
				{
					Log.i(LOGTAG,"fileName="+mManagerUpload.get("fileName"));
					Log.i(LOGTAG,"uploadFileKey="+ mManagerUpload.get("uploadFileKey"));
					
					if( mDlgNetwork.isShowing())
					{
						mDlgNetwork.dismiss();
					}
					
					saveResultData();	
					
				}
					break;
				case 3:
				{

					HashMap<String, String> fileMap	=	new HashMap<String, String>();
					String sFile_savename	=	mFileManager.file_savename;
					String sFile_name		=	mFileManager.file_name;
					String sFile_size		=	mFileManager.file_size;
					
					Log.i(LOGTAG,"sFile_savename="+sFile_savename);
					Log.i(LOGTAG,"sFile_name="+sFile_name);
					Log.i(LOGTAG,"sFile_size="+sFile_size);
					
					fileMap.put("msFile_savename",sFile_savename);
					fileMap.put("msFile_name",sFile_name);
					fileMap.put("msFile_size",sFile_size);
	
					mFileData.add(fileMap);
	
					Aw11FileArrayAdapter fileAdapter	=	new Aw11FileArrayAdapter(Aw11Activity.this,mFileData);
					getFileListheight(fileAdapter);
					mLv_aw_11_attfile.setAdapter(fileAdapter);
				}
					break;
					
				default:
					break;
				}
				//리스트뷰에 내용으 표시한다.
				
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
				//finish();
			}		
		});

		popup.show();
	}

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_11);

		mEtSdate = (EditText)findViewById(R.id.et_aw_11_sdate);
		mEtEdate = (EditText)findViewById(R.id.et_aw_11_edate);
		mBtnBack = (Button)findViewById(R.id.btn_11_back);
		TextView mRequestPart =  (TextView)findViewById(R.id.tv_aw_11_request_part);
		mBtn_aw_11_cancel = (Button)findViewById(R.id.btn_aw_11_cancel);
		mBtn_aw_11_cancel.setOnClickListener(this); 

		mBtn_aw_11_add	=	(Button)findViewById(R.id.btn_aw_11_add);
		mBtn_aw_11_add.setOnClickListener(this);

		mBtnBack.setOnClickListener(this);
		//요청, 제출기한 초기화
		final Calendar c = Calendar.getInstance();                 
		mYear = c.get(Calendar.YEAR);        
		mMonth = c.get(Calendar.MONTH);        
		mDay = c.get(Calendar.DAY_OF_MONTH); 
		mEtSdate.setText(String.format("%04d-%02d-%02d",mYear, mMonth+1, mDay));
		mEtEdate.setText(String.format("%04d-%02d-%02d",mYear, mMonth+1, mDay));

		Intent intent = getIntent();
		mGbncd	=	intent.getStringExtra("smGbncd");										//	요청구분
		mGbnName=	intent.getStringExtra("smGbnName");										// 	요청구분명
		//DR 요구자료
		mEt_aw_11_mbr		=	(EditText)findViewById(R.id.et_aw_11_mbr);					//	요청자(의원)
		mEt_aw_11_sup		=	(EditText)findViewById(R.id.et_aw_11_sup);					//	보좌관
		mEt_aw_11_sup.setFocusable(false);//보좌관 읽기전용
		//질의 - 국감
		mTv_aw_11_qr_mbr_title	=	(TextView)findViewById(R.id.tv_aw_11_qr_mbr_title);		//	질의/지적위원 타이틀
		mEt_aw_11_qr_mbr	=	(EditText)findViewById(R.id.et_aw_11_qr_mbr);				//	질의/지적위원
		mBtn_aw_11_qr_search=	(Button)findViewById(R.id.btn_aw_11_qr_search);				//	질의/지적의원검색 버튼
		mBtn_aw_11_qr_search.setOnClickListener(this);
		//IP 예상 Qna 
		mEt_aw_11_ip_mbr	=	(EditText)findViewById(R.id.et_aw_11_ip_mbr);				//	해당본부명
		mBtn_aw_11_ip_search=	(Button)findViewById(R.id.btn_aw_11_ip_search);				//	해당본부 검색버튼 
		mBtn_aw_11_ip_search.setOnClickListener(this);
		mEt_aw_11_ip_sup	=	(EditText)findViewById(R.id.et_aw_11_ip_sup);				// 	관련의원명 	
		mBtn_aw_11_ip_sup_search	=	(Button)findViewById(R.id.btn_aw_11_ip_sup_search);	//	관련의원검색 
		mBtn_aw_11_ip_sup_search.setOnClickListener(this);
		//공동 
		mEt_aw_11_sdate		=	(EditText)findViewById(R.id.et_aw_11_sdate);				//	요청일
		mEt_aw_11_edate		=	(EditText)findViewById(R.id.et_aw_11_edate);				//	제출기한
		mLv_aw_11_attfile	=	(ListView)findViewById(R.id.lv_aw_11_attfile);				//	첨부파일	 리스트
		mBtn_aw_11_search	=	(Button)findViewById(R.id.btn_aw_11_search);				//	검색
		mBtn_aw_11_search.setOnClickListener(this);

		mBtn_aw_11_filesearch	=	(Button)findViewById(R.id.btn_aw_11_filesearch);		//	파일첨부 버튼
		mBtn_aw_11_filesearch.setOnClickListener(this);
		mLv_aw_11_title	=	(ListView)findViewById(R.id.lv_aw_11_title);					// 	제목 리스트 
		mBtn_aw_11_reg	=	(Button)findViewById(R.id.btn_aw_11_reg);						//	등록버튼 
		mBtn_aw_11_reg.setOnClickListener(this);
		mCk_aw_11_sms	=	(CheckBox)findViewById(R.id.ck_aw_11_sms);						//	sms 발송 체크 박스


		//	요청일 ,제출기한  tableRow2  QA에서는 보이지 않는다. 
		tableRow2	=	(TableRow)findViewById(R.id.tableRow2);
		// 요구자료 요청자 TableRow
		TableRow tableRow1_dr	=	(TableRow)findViewById(R.id.tableRow1_dr);
		TableRow tableRow1_dr2	=	(TableRow)findViewById(R.id.tableRow1_dr2);
		//질의 - 국감
		TableRow tableRow1_qr	=	(TableRow)findViewById(R.id.tableRow1_qr);
		//예상 qna
		TableRow tableRow1_ip	=	(TableRow)findViewById(R.id.tableRow1_ip);
		TableRow tableRow1_ip1	=	(TableRow)findViewById(R.id.tableRow1_ip1);

		tableRow1_dr.setVisibility(View.GONE);
		tableRow1_dr2.setVisibility(View.GONE);
		tableRow1_qr.setVisibility(View.GONE);
		tableRow1_ip.setVisibility(View.GONE);
		tableRow1_ip1.setVisibility(View.GONE);
		tableRow2.setVisibility(View.GONE);

		if(mGbncd.equals("DR")){													//요구자료
			tableRow1_dr.setVisibility(View.VISIBLE);
			tableRow1_dr2.setVisibility(View.VISIBLE);
			tableRow2.setVisibility(View.VISIBLE);
		}else if(mGbncd.equals("QA")){												//질의답변	
			tableRow1_qr.setVisibility(View.VISIBLE);
			mEt_aw_11_qr_mbr.setHint("질의의원");
		}else if(mGbncd.equals("IP")){												//예상Q&A
			tableRow1_ip.setVisibility(View.VISIBLE);
			tableRow1_ip1.setVisibility(View.VISIBLE);
			tableRow2.setVisibility(View.VISIBLE);
			mEt_aw_11_ip_mbr.setHint("해당본부");
			mEt_aw_11_ip_sup.setFocusable(false);
		}else{																		//서면답변 , 국감결과
			tableRow1_qr.setVisibility(View.VISIBLE);
			tableRow2.setVisibility(View.VISIBLE);
			mEt_aw_11_qr_mbr.setHint("질의의원");
			if(mGbncd.equals("IR")){
				mTv_aw_11_qr_mbr_title.setText(R.string.s_lb_ir_assemb);			//국감결과 - 질의위원 --> 지적위원
				mEt_aw_11_qr_mbr.setHint("지적의원");
			}
		}

		if(mGbncd.equals("IP")){
			mRequestPart.setText("예상Q&A");
		}else{
			mRequestPart.setText(String.format("[ %s ]", mGbnName));
		}

		mEtSdate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				tempV = v;
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});
		mEtEdate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				tempV = v;
				showDialog(DATE_DIALOG_ID);
				return false;
			}
		});
		
		
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

			if(tempV.getId()==R.id.et_aw_11_sdate){
				setDateUpdate(mEtSdate);
			}else{
				setDateUpdate(mEtEdate);
			}
		}
	};

	public void setDateUpdate(EditText e){
		e.setText(String.format("%04d-%02d-%02d",mYear, mMonth+1, mDay));
	}

	//등록 
	public void sendData(){

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw11Activity.this, "모바일 국회", "등록중...");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.SERVER_URL ;	//+ "/" + Global.JSP_REG_ASSEMBLY;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_REG_ASSEMBLY));
		list.add(new SendQueue("sms_yn", msSms_yn));						//sms 발송여부
		list.add(new SendQueue("login_id", Global.LOGIN_ID));				//접속자 id
		list.add(new SendQueue("task_mas_uid", ""));						//국회업무마스터 키 (선택)	-- 수정이 없기때문에 보내지 않는다. 
		list.add(new SendQueue("mas_type_code", mGbncd));					//업무구분코드
		list.add(new SendQueue("assem_mbr_uids", msAssem_mbr_uid));			//요청자 키(국회의원)
		list.add(new SendQueue("assem_mbr_names", msMbr_name));				//요청자 이름(국회의원)
		list.add(new SendQueue("assem_mbr_party_codes", msMbr_party_code));	//정당 코드
		list.add(new SendQueue("assem_mbr_party_names", msMbr_party_name));	//정당 이름
		list.add(new SendQueue("assem_aide_uids", msAssem_aide_uid));		//보좌관 키
		list.add(new SendQueue("assem_aide_names", msAide_name));			//보좌관 이름
		list.add(new SendQueue("mas_bgn_date", msMas_bgn_date));			//요청일 (질의답변 (QA) 인 경우 SYSDATE로 등록)
		list.add(new SendQueue("mas_end_date", msMas_end_date));			//제출기한 (질의답변 (QA) 인 경우 SYSDATE로 등록)
		//예상 QA에서 사용
		list.add(new SendQueue("ex_center_uids", msEx_center_uids));		//해당본부 키 (예상Q&A (IP) 인 경우 등록)
		list.add(new SendQueue("ex_center_names", msEx_center_names));		//해당본부 이름 (예상Q&A (IP) 인 경우 등록)
		list.add(new SendQueue("rel_assem_mbr_uids", msRel_assem_mbr_uids));			//관련의원키 (예상Q&A (IP) 인 경우 등록)
		list.add(new SendQueue("rel_assem_mbr_names", msRel_assem_mbr_names));			//관련의원이름 (예상Q&A (IP) 인 경우 등록)



		for(int i = 0 ; i < mTitleData.size();i++){

			String sMain_tit			=	mTitleData.get(i).get("msMain_tit");
			String sMain_req_memo		=	mTitleData.get(i).get("msMain_req_memo");
			String sMain_rec_user_uids	=	mTitleData.get(i).get("msMain_rec_user_uids");
			String sMain_rec_user_names	=	mTitleData.get(i).get("msMain_rec_user_names");
			String sMain_qst_yn			=	mTitleData.get(i).get("msMain_qst_yn");

			if(sMain_qst_yn.equals("true")){
				sMain_qst_yn	=	"Y";
			}else{
				sMain_qst_yn	=	"N";
			}

			list.add(new SendQueue("main_tit", sMain_tit ));						//제목
			list.add(new SendQueue("main_req_memo", sMain_req_memo ));				//요청내용
			list.add(new SendQueue("main_rec_user_uids", sMain_rec_user_uids));		//주무자(담당자) 키 (여러 건일 경우 콤마(,)로 연결)
			list.add(new SendQueue("main_rec_user_names", sMain_rec_user_names));	//주무자(담당자) 이름 (여러 건일 경우 콤마(,)로 연결)
			list.add(new SendQueue("main_qst_yn", sMain_qst_yn));					//자체질문여부(Y/N)
		}	

		//첨부파일 추가 
		for(int i= 0 ; i<mFileData.size() ; i++){

			String sFile_savename	=	mFileData.get(i).get("msFile_savename");
			String sFile_name		=	mFileData.get(i).get("msFile_name");
			String sFile_size		=	mFileData.get(i).get("msFile_size");
			
			list.add(new SendQueue("file_uuid", "" ));								//파일키
			list.add(new SendQueue("file_savename", sFile_savename ));				//저장파일명
			list.add(new SendQueue("file_name", sFile_name));						//파일명
			list.add(new SendQueue("file_size", sFile_size));						//파일 사이즈
			
			Log.i(LOGTAG, "file_savename="+sFile_savename);
			Log.i(LOGTAG, "sFile_name="+sFile_name);
			Log.i(LOGTAG, "sFile_size="+sFile_size);

		}

		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
			public void run()
			{
				http.SendData();

				String result = http.rcvString;
				
				Log.i(LOGTAG, "11 result : "+result);
				Log.i(LOGTAG, "11 result : "+result);
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

					mManager = XmlParserManager.parsingRegAssembly(getApplicationContext(), result);

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
							msg.arg1 = 1;

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
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}

	//제목 추가 리스트 뷰 어답터
	class Aw11ArrayAdapter extends ArrayAdapter 
	{
		Context context;
		ArrayList<HashMap<String, String>> list;

		TextView title;
		TextView request_content;
		TextView username;
		TextView self_req;

		@SuppressWarnings("unchecked")
		public Aw11ArrayAdapter(Context context, ArrayList<HashMap<String, String>> list) 
		{
			super(context, R.layout.layout_aw_11_list, list);

			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_11_list, null);
			}

			title 			= (TextView)row.findViewById(R.id.tv_aw_11_list_title);
			request_content = (TextView)row.findViewById(R.id.tv_aw_11_list_req_content);
			username 		= (TextView)row.findViewById(R.id.tv_aw_11_list_username);
			self_req		= (TextView)row.findViewById(R.id.tv_aw_11_list_self_req);

			title.setText("제목 :" + list.get(position).get("msMain_tit"));
			request_content.setText("내용 :" + list.get(position).get("msMain_req_memo"));

			String userName		=	list.get(position).get("msMain_rec_user_names");
			String selgReqFlag	=	list.get(position).get("msMain_qst_yn").toString();
			if(selgReqFlag.equals("true")){
				username.setText("담당 :" + userName+" / [자체질문]");
				msMain_qst_yn	=	"Y";
			}else{
				username.setText("담당 :" +userName);
				msMain_qst_yn	=	"N";
			}

			return row;
		}
	}	

	//파일 추가 리스트 뷰 어답터
	class Aw11FileArrayAdapter extends ArrayAdapter 
	{
		Context context;
		ArrayList<HashMap<String, String>> list;

		TextView title;
		TextView request_content;
		TextView username;
		TextView self_req;

		@SuppressWarnings("unchecked")
		public Aw11FileArrayAdapter(Context context, ArrayList<HashMap<String, String>> list) 
		{
			super(context, R.layout.layout_aw_11_file_list, list);

			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_11_file_list, null);
			}

			title 			= (TextView)row.findViewById(R.id.TextView01);

			title.setText(list.get(position).get("msFile_name"));

			return row;
		}
	}	      

	// 질의 지적 의원검색
	public void searchRelAssembly(String assemblyName,boolean isIp){
		if(isIp){	// 예상 QA
			Intent intent = new Intent(Aw11Activity.this,Aw11RelAssemblyActivity.class);
			intent.putExtra("SelectedAssembly", msRel_assem_mbr_names);  	//선택의원 이름 보냄
			intent.putExtra("msRel_assem_mbr_uids", msRel_assem_mbr_uids);	//선택의원 정보기 보냄
			startActivityForResult(intent,14);
		}else{
			if(assemblyName.equals("")){
				Toast toast	=	Toast.makeText(Aw11Activity.this, "의원을 입력해주십시오.", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
			Intent intent = new Intent(Aw11Activity.this,Aw11AssemblyActivity.class);
			intent.putExtra("mbr_name", assemblyName);
			startActivityForResult(intent,14);
		}

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_11_back:			//이전
			finish();
			break;
		case R.id.btn_aw_11_search:		//요청자검색

			String mbrName	=	mEt_aw_11_mbr.getText().toString();
			if(mbrName.equals("")){
				Toast toast	=	Toast.makeText(Aw11Activity.this, "요청자를 입력해주십시오.", Toast.LENGTH_SHORT);
				toast.show();

			}else if(mbrName.length()<2){
				Toast toast	=	Toast.makeText(Aw11Activity.this, "검색어는 최소 2글자 입니다.", Toast.LENGTH_SHORT);
				toast.show();
				return;
			}else{
				Intent intent = new Intent(Aw11Activity.this,Aw13Activity.class);
				intent.putExtra("AssemblyName", mbrName);
				startActivityForResult(intent,12);
			}

			break;
		case R.id.btn_aw_11_ip_search:		//해당본부 

			String bonbuName	=	mEt_aw_11_ip_mbr.getText().toString();
			if(bonbuName.equals("")){
				Toast toast	=	Toast.makeText(Aw11Activity.this, "해당본부를 입력해주십시오.", Toast.LENGTH_SHORT);
				toast.show();

			}else{
				Intent intent = new Intent(Aw11Activity.this,Aw11GroupActivity.class);
				String group_name  = mEt_aw_11_ip_mbr.getText().toString();
				intent.putExtra("group_name", group_name);
				startActivityForResult(intent,13);
			}

			break;
		case R.id.btn_aw_11_ip_sup_search:		//관련의원

			String mbr_name  = mEt_aw_11_ip_sup.getText().toString();
			searchRelAssembly(mbr_name,true);

			break;

		case R.id.btn_aw_11_qr_search:			//질의 지적 위원

			String smbr_name  = mEt_aw_11_qr_mbr.getText().toString();
			searchRelAssembly(smbr_name,false);

			break;


		case R.id.btn_aw_11_filesearch:		//파일첨부
//			Intent intentfile	=	new Intent(Aw11Activity.this,Aw15FileActivity.class);
//			intentfile.putExtra("mGbnCd", mGbncd);
//			startActivityForResult(intentfile, 1);
			
			Intent fileIntent	=	new Intent(Intent.ACTION_PICK);
			fileIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,MediaStore.Images.Media.CONTENT_TYPE);
			startActivityForResult(fileIntent, 1);
			break;

		case R.id.btn_aw_11_cancel:			//취소
			finish();
			break;

		case R.id.btn_aw_11_add:			//제목추가

			Intent addIntent = new Intent(Aw11Activity.this,Aw12Activity.class);
			startActivityForResult(addIntent,11);

			break;

		case R.id.btn_aw_11_reg :			//등록 버튼 ;
		{
			/*
			 * 	전달해줄변수 : 
			 * sms_yn 					- SMS발송여부(Y/N)
			 * login_id 				- 접속자ID
	+ task_mas_uid 			- 국회업무마스터 키
			 * mas_type_code 			- 요구자료(DR), 질의답변(QA), 서면답변(WR), 국감결과(IR), 예상Q&A(IP)
			 * assem_mbr_uids 			- 요청자 키(국회의원)
			 * assem_mbr_names 			- 요청자 이름(국회의원)
			 * assem_mbr_party_codes 	- 요청자 소속 정당 코드
			 * assem_mbr_party_names 	- 요청자 소속 정당 이름
			 * assem_aide_uids 			- 보좌관 키 (여러 건일 경우 콤마(,)로 연결)
			 * assem_aide_names 		- 보좌관 이름 (여러 건일 경우 콤마(,)로 연결)
			 * mas_bgn_date 			- 요청일 (질의답변 (QA) 인 경우 SYSDATE로 등록)
			 * mas_bgn_date 			- 제출기한 (질의답변 (QA) 인 경우 SYSDATE로 등록)
	+ ex_center_uids 		- 해당본부 키 (예상Q&A (IP) 인 경우 등록)
	+ ex_center_names 		- 해당본부 이름 (예상Q&A (IP) 인 경우 등록)
	+ rel_assem_mbr_uids 	- 관련의원키 (예상Q&A (IP) 인 경우 등록)
	+ rel_assem_mbr_names 	- 관련의원이름 (예상Q&A (IP) 인 경우 등록)

	@배열===============>
			 *@ main_tit 				- 제목																	
			 *@ main_req_memo 			- 요청내용
			 *@ main_rec_user_uids 		- 주무자(담당자) 키 (여러 건일 경우 콤마(,)로 연결)		===>(Aw14Activity)
			 *@ main_rec_user_names 	- 주무자(담당자) 이름 (여러 건일 경우 콤마(,)로 연결)	===>(Aw14Activity)
			 *@ main_qst_yn 			- 자체질문여부(Y/N)
	@배열(첨부파일)
 +@ file_uuid 				- 첨부파일 고유아이디
			 *@ file_savename 			- 파일 저장이름
			 *@ file_name 				- 파일 이름
			 *@ file_size 				- 파일 크기
			 **/
			//	보좌관 등록확인
			if(mGbncd.equals("DR")){
				if(mEt_aw_11_sup.getText().toString().equals("")){
					Toast.makeText(this, "요청자를 검색하여 입력해 주세요.",Toast.LENGTH_SHORT).show();
					return;
				}
			}else if(mGbncd.equals("IP")){
				if(msEx_center_uids.equals("")){
					Toast.makeText(this, "해당본부를 검색하여 입력해 주세요.",Toast.LENGTH_SHORT).show();
					return;
				}
				if(msRel_assem_mbr_uids.equals("")){
					Toast.makeText(this, "관련의원을 검색하여 입력해 주세요.",Toast.LENGTH_SHORT).show();
					return;
				}
			}else{
				if(msAssem_mbr_uid.equals("")){
					Toast.makeText(this, "질의위원 검색하여 입력해 주세요.",Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if(!mGbncd.equals("QA")){
				//	요청일 등록확인
				if(mEt_aw_11_sdate.getText().toString().equals("")){
					Toast.makeText(this, "요청일을 입력해 주세요.", Toast.LENGTH_SHORT).show();
					return;
				}
				//	제출기한 등록확인
				if(mEt_aw_11_edate.getText().toString().equals("")){
					Toast.makeText(this, "제출기한을 입력해 주세요.", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			if(mGbnName.equals("QA")){
				msMas_bgn_date 		=	new SimpleDateFormat("yyyy-MM-dd").format(new Date())	;	//	요청일 (질의답변 (QA) 인 경우 SYSDATE로 등록)
				msMas_end_date		=	new SimpleDateFormat("yyyy-MM-dd").format(new Date())	;
			}else{
				msMas_bgn_date 		=	mEt_aw_11_sdate.getText().toString();
				msMas_end_date 		=	mEt_aw_11_edate.getText().toString();
			}

			if(mTitleData.size()<1){
				Toast.makeText(this, "제목을 1건 이상 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}

			if(mCk_aw_11_sms.isChecked()){
				msSms_yn	=	"Y";
			}else{
				msSms_yn	=	"N";
			}

			sendData();
		}
		break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){

			//		파일첨부
			if(requestCode==1){
//				
				Uri uri	=	data.getData();
				mAttcFilePath =	getRealImagePath(uri);
				
				
				sendFileData();
				
				Log.i(LOGTAG, "uri = "+uri);
				Log.i(LOGTAG, "mAttcFilePath = "+mAttcFilePath);
				
			}

			//		제목 입력
			if(requestCode==11){

				msMain_tit				=	data.getStringExtra("mEt_aw_12_title");		//제목 
				msMain_req_memo			=	data.getStringExtra("mEt_aw_12_request");	//요청내용
				msMain_rec_user_uids	=	data.getStringExtra("mEt_aw_12_mbr_uid");	//담당자키
				msMain_rec_user_names	=	data.getStringExtra("mEt_aw_12_mbr");		//담당자
				msMain_qst_yn			=	data.getStringExtra("self_req_flag");		//자체질문여부

				HashMap<String, String> item	=	new HashMap<String, String>();
				item.put("msMain_tit",msMain_tit);
				item.put("msMain_req_memo",msMain_req_memo);
				item.put("msMain_rec_user_uids",msMain_rec_user_uids);
				item.put("msMain_rec_user_names",msMain_rec_user_names);
				item.put("msMain_qst_yn",msMain_qst_yn);

				mTitleData.add(item);

				Aw11ArrayAdapter adapter = new Aw11ArrayAdapter(this, mTitleData);
				getTitleListheight(adapter);
				mLv_aw_11_title.setAdapter(adapter);

				Log.i(LOGTAG,"msMain_tit="+mTitleData.get(0).get("msMain_tit"));
				Log.i(LOGTAG,"msMain_req_memo="+mTitleData.get(0).get("msMain_req_memo"));
				Log.i(LOGTAG,"Size="+mTitleData.size());
			}

			//		요청자검색
			if(requestCode==12){

				msAide_name     	=	data.getStringExtra("msAide_name");
				msAssem_aide_uid	=	data.getStringExtra("msAssem_aide_uid");
				msAssem_mbr_uid 	=	data.getStringExtra("msAssem_mbr_uid");
				msMbr_name      	=	data.getStringExtra("msMbr_name");
				msMbr_party_code	=	data.getStringExtra("msMbr_party_code");
				msMbr_party_name	=	data.getStringExtra("msMbr_party_name");

				mEt_aw_11_mbr.setText(msMbr_name);
				mEt_aw_11_sup.setText(msAide_name);

				Log.i(LOGTAG,"msAide_name="+msAide_name);
				Log.i(LOGTAG,"msAssem_aide_uid="+msAssem_aide_uid);
			}
			//			해당본부 검색
			if(requestCode==13){

				msEx_center_uids 	=	data.getStringExtra("Ex_center_uids");	//	해당본부 키 (예상Q&A (IP) 인 경우 등록)
				msEx_center_names 	=	data.getStringExtra("Ex_center_names");	//	해당본부 이름 (예상Q&A (IP) 인 경우 등록)

				mEt_aw_11_ip_mbr.setText(msEx_center_names);

				Log.i(LOGTAG,"msAide_name="+msAide_name);
				Log.i(LOGTAG,"msAssem_aide_uid="+msAssem_aide_uid);
			}
			//		지적/질의 의원검색
			if(requestCode==14){		

				String subRel_assem_mbr_uids 		=	data.getStringExtra("Rel_assem_mbr_uids");			//	관련의원 키 (예상Q&A (IP) 인 경우 등록)
				String subRel_assem_mbr_names 		=	data.getStringExtra("Rel_assem_mbr_names");			//	관련의원 이름 (예상Q&A (IP) 인 경우 등록)
				String subRel_assem_mbr_party_code 	=	data.getStringExtra("Rel_assem_mbr_party_code");	//	관련의원 정당코드 (예상Q&A (IP) 인 경우 등록)
				String subRel_assem_mbr_party_name 	=	data.getStringExtra("Rel_assem_mbr_party_name");	//	관련의원 정당이름 (예상Q&A (IP) 인 경우 등록)
				
				boolean isIp					=	data.getBooleanExtra("isIp", true);		

				if(isIp){		//관련의원검색  - 멀티
					if(!subRel_assem_mbr_uids.equals("")){				//의원추가 
						if(msRel_assem_mbr_uids.equals("")){
							msRel_assem_mbr_uids 	=	subRel_assem_mbr_uids;	//	관련의원 키 (예상Q&A (IP) 인 경우 등록)
							msRel_assem_mbr_names 	=	subRel_assem_mbr_names;	//	관련의원 이름 (예상Q&A (IP) 인 경우 등록)
							msMbr_party_code		=	subRel_assem_mbr_party_code;
							msMbr_party_name		=	subRel_assem_mbr_party_name;
						}else{
							msRel_assem_mbr_uids 	+=	"/"+subRel_assem_mbr_uids;	//	관련의원 키 (예상Q&A (IP) 인 경우 등록)
							msRel_assem_mbr_names 	+=	"/"+subRel_assem_mbr_names;	//	관련의원 이름 (예상Q&A (IP) 인 경우 등록)
							msMbr_party_code		+=	"/"+subRel_assem_mbr_party_code;
							msMbr_party_name		+=	"/"+subRel_assem_mbr_party_name;
						}
						mEt_aw_11_ip_sup.setText(msRel_assem_mbr_names);
					}else{
						//의원초기화
						msRel_assem_mbr_names	=	"";
						msRel_assem_mbr_uids	=	"";
						mEt_aw_11_ip_sup.setText("");
					}
				}else{			//질의 의원 검색   - 단일
					msAssem_mbr_uid		=	subRel_assem_mbr_uids;						//요청자 키(국회의원)
					msMbr_name			=	subRel_assem_mbr_names;						//요청자 이름(국회의원)
					msMbr_party_code	=	data.getStringExtra("msMbr_party_code");	//정당코드
					msMbr_party_name	=	data.getStringExtra("msMbr_party_name");	//정당이름
					mEt_aw_11_qr_mbr.setText(msMbr_name);
				}

				Log.i(LOGTAG,"msRel_assem_mbr_names="+msRel_assem_mbr_names);
				Log.i(LOGTAG,"msRel_assem_mbr_uids="+msRel_assem_mbr_uids);
			}



		}

	}

	//이미지 경로 가져오기
	protected String getRealImagePath(Uri uriPath) {
		String[] proj = {MediaStore.Images.Media.DATA};
		Cursor c = managedQuery(uriPath, proj, null, null, null);
		int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		c.moveToFirst();
		String fullPath = null;
		try {
			fullPath = c.getString(index);  // 파일의 실제 경로			
		} catch(Exception e) {
			String err	=	e.toString();
			Log.e(LOGTAG, "Exception is " + "오류가 발생하였습니다. error : "+err);
//			return null;
		}
		startManagingCursor(c);
		return fullPath;
	}
	
	// 첨부파일 리스트뷰 높이 설정
	private void getFileListheight(Adapter adapter)
	{
		if (adapter != null)
		{
			int count = adapter.getCount();
			if (count > 0)
			{
				int height = MAX_HEIGHT * count;	
				getFileListheight(height);
			}
		}
	}

	private void getFileListheight(int height)
	{
		final ListView layout = (ListView)findViewById(R.id.lv_aw_11_attfile);

		if (layout != null) 
		{
			final ViewGroup.LayoutParams params = layout.getLayoutParams();
			if (params != null) 
			{
				// 현재 뷰의 margin 을 계산한다.
				MarginLayoutParams margin = (MarginLayoutParams)layout.getLayoutParams();
				params.height = height;
				height +=5;
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

	// 제목 리스트뷰 높이 설정
	private void getTitleListheight(Adapter adapter)
	{
		if (adapter != null)
		{
			int count = adapter.getCount();
			if (count > 0)
			{
				int height = MAX_HEIGHT2 * count;	
				getTitleListheight(height);
			}
		}
	}

	private void getTitleListheight(int height)
	{
		final ListView layout = (ListView)findViewById(R.id.lv_aw_11_title);

		if (layout != null) 
		{
			final ViewGroup.LayoutParams params = layout.getLayoutParams();
			if (params != null) 
			{
				// 현재 뷰의 margin 을 계산한다.
				MarginLayoutParams margin = (MarginLayoutParams)layout.getLayoutParams();
				params.height = height;
				height +=5;
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
	
	@Override
    protected void onDestroy() {
        Log.d(LOGTAG, "called onDestroy");
        if(mDlgNetwork!=null){
        	mDlgNetwork.dismiss();
        }
        super.onDestroy();
    }
	
	
	public void sendFileData(){			//첨부파일등록

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw11Activity.this, "첨부파일 저장", "등록중...");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.FILE_SERVER_URL;// + "/" + Global.JSP_FILE_UPLOAD;
		list.add(new SendQueue("", temp));

		final HashMap<String , Object> map = new HashMap<String, Object>();

		map.put("file", mAttcFilePath);

		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
			public void run()
			{
				http.fileUpload(map);

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
					mManagerUpload = XmlParserManager.parsingAttachFile1(getApplicationContext(), result);
					if( mManagerUpload == null )
					{
						Log.i(LOGTAG, "manager is memory allocation failed2");
						msg.what = Global.MSG_FAILED;
						msg.arg1 = Global.HM_ERR_XML_PARSING;
						mHandler.sendMessage(msg);
						return;

					}else{
						//Log.i(LOGTAG, mManager.get("uploadFileKey"));
						if( mManagerUpload.get("uploadFileKey")!=null)
						{
							msg.what = Global.MSG_SUCEEDED;
							msg.arg1 = 2;
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
	
	// 파일저장 결과 받기
		public void saveResultData(){

			if( mDlgNetwork == null || !mDlgNetwork.isShowing())
			{
				mDlgNetwork = ProgressDialog.show(Aw11Activity.this, "첨부파일 등록", "등록중...");
			}

			ArrayList<SendQueue> list = new ArrayList<SendQueue>();

			String temp = Global.SERVER_URL;// + "/" + Global.JSP_FILE_UPLOAD;
			list.add(new SendQueue("", temp));
			list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_FILE_UPLOAD));
			list.add(new SendQueue("file_key",  mManagerUpload.get("uploadFileKey")));
			list.add(new SendQueue("mas_type_code", mGbncd));

			final ExNetwork http = new ExNetwork(list);
			thread = new Thread(new Runnable(){
				public void run()
				{
					http.SendData();

					String result = http.rcvString.replace("\n", "");
					
					Log.i(LOGTAG, "result = "+result);
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

						mFileManager = XmlParserManager.parsingAttachFile(getApplicationContext(), result);
						if( mFileManager == null )
						{
							Log.i(LOGTAG, "manager is memory allocation failed3");
							msg.what = Global.MSG_FAILED;
							msg.arg1 = Global.HM_ERR_XML_PARSING;
							mHandler.sendMessage(msg);
							return;

						}else{
							if( mFileManager.result_code == 1000)
							{
								msg.what = Global.MSG_SUCEEDED;
								msg.arg1 = 3;
								
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
