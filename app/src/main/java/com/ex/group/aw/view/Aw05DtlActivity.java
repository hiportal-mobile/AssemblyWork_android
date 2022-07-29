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
import android.widget.ListView;
import android.widget.TextView;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.RequestManager;
import com.ex.group.aw.manager.UserInfoManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
/*
	화면명: 자료요청리스트 - 상세목록
	작성자: 방종희
	DESC: 
	DATE: 2013.04.16
	VERSION: 0.1
 */
public class Aw05DtlActivity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw05DtlActivity";
	//layout item
	private Button mBtnPrev;				//	이전
	private ListView mLvList;				//	요청 목록

	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private RequestManager mManager = new RequestManager();
	private int mCurPageNo	=	0;
	private AwArrayAdapter mAdapter;
	private AwCustomPopup popup;
	private View footer ;

	//parems
	private String mStatCode	=	"";		//	상태코드 
	private String mStatusName	=	"";		//	상태코드명
	private String mTask_mas_uid=	"";		//	국회업무마스터코드
	private String mGbncd		=	"";
	private String mGbnName		=	"";

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



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_05_dtl);

		mBtnPrev 		=	(Button)findViewById(R.id.btn_aw_05_back);

		mLvList 		=	(ListView)findViewById(R.id.lv_aw_05_request_list);

		mBtnPrev.setOnClickListener(this);

		Intent intent	=	getIntent();
		mStatCode		=	intent.getStringExtra("stat_code");
		mStatusName		=	intent.getStringExtra("statusName");
		mTask_mas_uid	=	intent.getStringExtra("task_mas_uid");
		mGbncd			= 	intent.getStringExtra("mGbncd");
		mGbnName		= 	intent.getStringExtra("mGbnName");

		// 푸터 생성
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footer =  inflater.inflate(R.layout.layout_listview_footer_more, null);
		TextView moreBtn = (TextView)footer.findViewById(R.id.btn_more);

		String tempTitle	=	String.format("%s %s", "자료요청",mStatusName);
		//타이틀변경 
		TextView	tv_aw_05_titlebar	=	(TextView)findViewById(R.id.tv_aw_05_titlebar);
		tv_aw_05_titlebar.setText(tempTitle);

		//	더보기 호출 	
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mCurPageNo++;	//	가져올 페이지 번호 
				getReqList();	//	리스트 데이터 
			}
		});


		//	리스트 아이템 클릭시 작성자 지정으로. 
		mLvList.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{

					//	어디로 이동하는지 정의 해야 함.

					//[A:국회 담당, C:본부 담당, F:1차 담당, S:2차 담당] 
					String auth_code	=	UserInfoManager.getInstance().auth_code;

					//A:국회 담당 ==>자료 검색뷰 페이지 이동

					if(auth_code.equals("A")){
						//자료검색 상세뷴
						moveSearchDtl(position);

					}else if(auth_code.equals("F")){
						if(mStatCode.equals("12_DR")){		//배부전
							//작성자 지정
							moveSrchWriter(position);
						}else{
							//자료검색 상세뷴
							moveSearchDtl(position);
						}
					}else{
						//자료검색 상세뷴
						moveSearchDtl(position);

					}
				}
			}
		);

		// 리스트 목록 
		getReqList();

	}

	// 자료 검색뷰로  이동
	public void moveSearchDtl(int position){
		/*
		 * 전달해줄변수 : 
		 * aams_task_mas_type_code - 업무구분코드(DR:요구자료, QA:질의답변, WR:서면답변, IR:국감결과, IP:예상Q&A)
		 * task_mas_uid - 국회업무 마스터 키
		 * task_dist_main_uid - 대항목 배부정보 키
		 * task_dist_sub_uid - 세부항목 배부정보 키
		 * */
		Intent intent = new Intent(Aw05DtlActivity.this, Aw17Activity.class);
		intent.putExtra("mGbncd", mGbncd);
		intent.putExtra("mGbnName", mGbnName);
		intent.putExtra("task_mas_uid", mManager.getList().get(position).task_mas_uid);
		intent.putExtra("task_dist_main_uid", mManager.getList().get(position).task_dist_main_uid);
		intent.putExtra("task_dist_sub_uid", mManager.getList().get(position).task_dist_sub_uid);
		startActivity(intent);
		
		
		Log.i(LOGTAG, "05dtl_mGbncd : "+mGbncd);
		Log.i(LOGTAG, "05dtl_mGbnName : "+mGbnName);
		Log.i(LOGTAG, "05dtl_mTask_mas_uid : "+mManager.getList().get(position).task_mas_uid);
		Log.i(LOGTAG, "05dtl_mTask_dist_main_uid : "+mManager.getList().get(position).task_dist_main_uid);
		Log.i(LOGTAG, "05dtl_mTask_dist_sub_uid : "+mManager.getList().get(position).task_dist_sub_uid);
	}

	//작성자지정으로 이동
	public void moveSrchWriter(int position){
		/*
		 * 전달해줄변수 : 
		 * login_id - 접속자ID
		 * task_mas_uid - 국회업무 마스터 키
		 * task_dist_main_uid - 요구문항 주무자 배부정보 키
		 * */
		Intent intent = new Intent(Aw05DtlActivity.this, Aw06Activity.class);
		intent.putExtra("task_mas_uid", mManager.getList().get(position).task_mas_uid);
		intent.putExtra("task_dist_main_uid", mManager.getList().get(position).task_dist_main_uid);
		startActivity(intent);
		
		
	}

	public void onClick(View v)
	{
		switch (v.getId()) 
		{
		case R.id.btn_aw_05_back:			// 	이전 버튼 
		{
			finish();
		}
		break;
		default:
			break;
		}
	}

	private void setData(boolean more)
	{
		//	더보기 보이기 숨기기 
		mLvList.removeFooterView(footer);

		if(mManager.total_count>mCurPageNo*mManager.count){
			mLvList.addFooterView(footer);
		}

		//	최초 리스트
		Log.i(LOGTAG, "more=="+more);
		
		mAdapter = new AwArrayAdapter(Aw05DtlActivity.this);
		if(! more ){
			mLvList.setAdapter(mAdapter);
		}
		// 더보기  리스트 
		mAdapter.notifyDataSetChanged();
	}

	//	자료요청 목록 
	public void getReqList(){

		if(mCurPageNo==0){mCurPageNo=1;}

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw05DtlActivity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();


		String temp = Global.SERVER_URL;	// + "/" + Global.JSP_REQUEST_LIST;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_REQUEST_LIST));
		list.add(new SendQueue("pageno",Integer.toString(mCurPageNo)));			//페이지 번호
		list.add(new SendQueue("task_mas_uid",mTask_mas_uid));								//국회업무 마스터키
		list.add(new SendQueue("stat_code",mStatCode));			//페이지 번호
		list.add(new SendQueue("login_id",Global.LOGIN_ID));

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

					XmlParserManager.parsingReqList(getApplicationContext(), result, mManager);

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

		Log.i(LOGTAG, "mCurPageNo="+mCurPageNo);

	}

	/**
	 * 자료요청 어댑터 
	 * @author 방종희
	 *
	 */
	class AwArrayAdapter extends ArrayAdapter 
	{
		Context context;

		TextView title;
		TextView summary;
		TextView name;
		TextView userName;
		int m_nCount = 0;

		@SuppressWarnings("unchecked")
		public AwArrayAdapter(Context context) 
		{
			super(context, R.layout.layout_aw_05_dtl_list, mManager.getList());

			// TODO Auto-generated constructor stub

			this.context = context;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_05_dtl_list, null);
			}

			title 	= (TextView)row.findViewById(R.id.tv_aw_05_dtl_list_title);
			summary = (TextView)row.findViewById(R.id.tv_aw_05_dtl_list_content);
			name 	= (TextView)row.findViewById(R.id.tv_aw_05_dtl_list_assemblyman);
			userName= (TextView)row.findViewById(R.id.tv_aw_05_dtl_list_user);

			String temp = "";
			if(!mManager.getList().get(position).mas_bgn_date.equals("")){ 
				temp = mManager.getList().get(position).mas_bgn_date+ "/";
			}
			if(!mManager.getList().get(position).mas_end_date.equals("")){
				temp	+= 	mManager.getList().get(position).mas_end_date ;
			}

			String temp2 = "";
			temp2	=	mManager.getList().get(position).tit;

			title.setText(temp2);
			summary.setText(temp);
			name.setText(mManager.getList().get(position).assem_mbr_names);
			userName.setText(mManager.getList().get(position).user_names);

			//Log.d("test", temp);

			return row;
		}
	}	

}
