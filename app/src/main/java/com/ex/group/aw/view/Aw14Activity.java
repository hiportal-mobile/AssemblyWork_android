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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.UserManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.UserInfo;

/*
	화면명: 의원요청등록 > 제목입력 > 담당자검색
	작성자: 방종희
	DESC: 
	DATE: 2013.04.19
	VERSION: 0.1
 */
public class Aw14Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw14Activity";
	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private EditText mEt_aw_14_search	;	// 	검색어(담당자명)
	private Button mBtn_aw_14_search	;	//	검색버튼 
	private ListView mLv_aw_14_mbr		;	//	담당자목록
	private Button mBtn_aw_14_cancel	;	//	취소
	private Button mBtn_aw_14_ok		;	//	확인

	private String mMain_rec_user_uids	;	//	담당자키 (여러 건일 경우 콤마(,)로 연결)
	private String mMain_rec_user_names ;	//	담당자이름 (여러 건일 경우 콤마(,)로 연결)
	private View notThingFooter;				//	자료 없음 Footer
	private UserManager mManager	= new UserManager();		
	private AwCustomPopup popup;
	private int mCurPageNo	=	0;
	private View moreFooter ;				//	더보기 리스트 Footer
	private String searchKeyWord="";	//검색어
	private Aw14ArrayAdapter mAdapter;

	private Handler mHandler =new Handler(){

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
		setContentView(R.layout.layout_aw_14);

		mBtn_aw_14_search=	(Button)findViewById(R.id.btn_aw_14_search);
		mEt_aw_14_search=	(EditText)findViewById(R.id.et_aw_14_search);
		mLv_aw_14_mbr	=	(ListView)findViewById(R.id.lv_aw_14_mbr);
		mBtn_aw_14_cancel=	(Button)findViewById(R.id.btn_aw_14_cancel);
		mBtn_aw_14_ok	=	(Button)findViewById(R.id.btn_aw_14_ok);

		mBtn_aw_14_cancel.setOnClickListener(this);
		mBtn_aw_14_ok.setOnClickListener(this);
		mBtn_aw_14_search.setOnClickListener(this);

		//		자료 없음 푸터
		LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		notThingFooter =  inflater2.inflate(R.layout.layout_listview_footer_notthing, null);

		// 더보기 푸터 생성
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		moreFooter =  inflater.inflate(R.layout.layout_listview_footer_more, null);
		TextView moreBtn = (TextView)moreFooter.findViewById(R.id.btn_more);

		//			더보기 호출 	
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mCurPageNo++;	//	가져올 페이지 번호 
				getUserList();	//	리스트 데이터 
			}
		});

		mLv_aw_14_mbr.setOnItemClickListener
		(
				new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
					{
						Log.i(LOGTAG, "onItemClick() position is " + position);
						UserInfo info =  mManager.getList().get(position);
						if( info.is_checked )
						{
							info.is_checked = false;
							//						return;
						}
						else
						{
							info.is_checked = true;
							//							mManager.getList().set(position, info);
							//							for( int i=0 ; i<mManager.getList().size() ; i++ )
							//							{
							//								if( i != position )
							//								{
							//									UserInfo temp = (UserInfo)mManager.getList().get(i);
							//									temp.is_checked = false;
							//									mManager.getList().set(i, temp);
							//								}
							//							}
						}
						mAdapter = new Aw14ArrayAdapter(Aw14Activity.this);
						mLv_aw_14_mbr.setAdapter(mAdapter);
						mLv_aw_14_mbr.setSelection(position-1);
					}
				}
				);

		String mEt_aw_12_mbr = "";
		Intent	intent	=	 getIntent();
		mEt_aw_12_mbr	=	intent.getStringExtra("mEt_aw_12_mbr");
		Log.i(LOGTAG, "mEt_aw_12_mbr="+mEt_aw_12_mbr);

		//getUserList(mEt_aw_12_mbr);

	}

	private void setData(boolean more){
		mLv_aw_14_mbr.removeFooterView(moreFooter);
		mLv_aw_14_mbr.removeFooterView(notThingFooter);

		if(mManager.total_count>mCurPageNo*mManager.count){
			mLv_aw_14_mbr.addFooterView(moreFooter);
		}

		if(mManager.total_count<1){
			try {
				mLv_aw_14_mbr.addHeaderView(notThingFooter);
			}catch (Exception e) {
				// TODO: handle exception
				String err	=	e.toString();
				Log.e(LOGTAG, "notthingFoorer add fail.. error : "+err);
			}
		}

		mAdapter  = new Aw14ArrayAdapter(Aw14Activity.this);
		if(! more ){
			mLv_aw_14_mbr.setAdapter(mAdapter);
		}
		// 더보기  리스트 
		mAdapter.notifyDataSetChanged();
	}

	private void getUserList(){

		if(mCurPageNo==0){mCurPageNo=1;}

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw14Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}
		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.SERVER_URL	;// + "/" + Global.JSP_SEARCH_USER;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SEARCH_USER));
		list.add(new SendQueue("user_name", searchKeyWord));
		list.add(new SendQueue("pageno",Integer.toString(mCurPageNo)));


		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable(){
			public void run()
			{
				http.SendData();

				String result = http.rcvString.replace("\n", "");

				Log.i(LOGTAG, "result : "+result);

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

					XmlParserManager.parsingUserList(getApplicationContext(), result,mManager);

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

	class Aw14ArrayAdapter extends ArrayAdapter 
	{
		Context context;
		ImageView check_off;
		ImageView check_on;
		TextView tv;

		@SuppressWarnings("unchecked")
		public Aw14ArrayAdapter(Context context) 
		{
			super(context,R.layout.layout_aw_13_list, mManager.getList());

			// TODO Auto-generated constructor stub

			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row = inflater.inflate(R.layout.layout_aw_13_list, null);
			}

			UserInfo info	=	 mManager.getList().get(position);

			mMain_rec_user_uids		=	info.uuid;
			mMain_rec_user_names	=	info.name_ko;
			String group_name_ko	=	info.group_name_ko;

			String temp = "";
			temp	+= 	mMain_rec_user_names+"/";
			temp	+= 	group_name_ko;

			check_off = (ImageView)row.findViewById(R.id.iv_aw_13_list_check_off);
			check_on = (ImageView)row.findViewById(R.id.iv_aw_13_list_check_on);
			tv = (TextView)row.findViewById(R.id.tv_aw_13_list_content);

			if(info.is_checked)
			{
				check_on.setVisibility(View.VISIBLE);
				check_off.setVisibility(View.GONE);
			}
			else
			{
				check_on.setVisibility(View.GONE);
				check_off.setVisibility(View.VISIBLE);
			}

			tv.setText(temp);
			Log.i("test", "getView() postion[" + position + "] : checked[" + info.is_checked + "]");

			return row;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_aw_14_cancel:
		{
			finish();
		}
		break;
		case R.id.btn_aw_14_ok:				//담당자 설정 
		{

			//요청제목 입력 팝업에 담당자를 지정한다. 담당자명과 , 담당자키를 ,로 구분하여 보내준다. 
			String splitPoint		=	""	;	//	구분자
			int splitIndex	=	0;
			String msUserUid	=	"";
			String msUserName	=	"";	
			if(mManager!=null){
				for(int i = 0 ;i<mManager.getList().size();i++){
					if(mManager.getList().get(i).is_checked){
						if(splitIndex==0){
							splitPoint = "";
						}else{
							splitPoint = ",";
						}
						msUserUid += 		splitPoint+mManager.getList().get(i).uuid.trim()		;	//	보좌관키
						msUserName +=	splitPoint+mManager.getList().get(i).name_ko.trim()	;	//	보좌관이름

						splitIndex++;
					}
				}
			}
			if(splitIndex<1){
				Toast.makeText(this, "담당자를 선택해 주세요.",Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent =	getIntent();
			intent.putExtra("msUserUid", msUserUid);
			intent.putExtra("msUserName", msUserName);
			setResult(RESULT_OK,intent);

			finish();
		}
		break;
		case R.id.btn_aw_14_search:
		{
			
			mCurPageNo = 0;
			searchKeyWord	=	mEt_aw_14_search.getText().toString();
			if(searchKeyWord.equals("")){
				Toast.makeText(Aw14Activity.this, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return ;
			}else{
				mManager = new UserManager();
				getUserList();
			}
		}
		break;
		default:
			break;
		}

	}	

}
