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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.AssemblySearchManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.AssemblySearchInfo;
import com.ex.group.aw.vo.Global;

/*
	화면명: 자료요청 > 의원요청등록 > 요청자검색(의원검색)
	작성자: 방종희
	DESC: 
	DATE: 2013.04.18
	VERSION: 0.1
 */

public class Aw13Activity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw13Activity";
	private ListView mLv_aw_13_mbr;
	private Button  mBtn_aw_13_cancel;
	private Button  mBtn_aw_13_ok;

	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private AwCustomPopup popup;
	private View notThingFooter;				//	자료 없음 Footer
	private AssemblySearchManager mManager;

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
				Log.i(LOGTAG, "test");
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
		setContentView(R.layout.layout_aw_13);

		mLv_aw_13_mbr	=	(ListView)findViewById(R.id.lv_aw_13_mbr);
		mBtn_aw_13_cancel=	(Button)findViewById(R.id.btn_aw_13_cancel);
		mBtn_aw_13_ok	=	(Button)findViewById(R.id.btn_aw_13_ok);
		mBtn_aw_13_cancel.setOnClickListener(this);
		mBtn_aw_13_ok.setOnClickListener(this);
		//		자료 없음 푸터
		LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		notThingFooter =  inflater2.inflate(R.layout.layout_listview_footer_notthing, null);

		mLv_aw_13_mbr.setChoiceMode(mLv_aw_13_mbr.CHOICE_MODE_MULTIPLE);
		mLv_aw_13_mbr.setOnItemClickListener
		(
				new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
					{
						Log.i(LOGTAG, "onItemClick() position is " + position);
						AssemblySearchInfo info =  mManager.getList().get(position);
						if( info.is_checked )
						{
							info.is_checked = false;
//							return;
						}
						else
						{
							info.is_checked = true;
//								mManager.getList().set(position, info);
//								for( int i=0 ; i<mManager.getList().size() ; i++ )
//								{
//									if( i != position )
//									{
//										UserInfo temp = (UserInfo)mManager.getList().get(i);
//										temp.is_checked = false;
//										mManager.getList().set(i, temp);
//									}
//								}
						}
						UserArrayAdapter adapter = new UserArrayAdapter(Aw13Activity.this);
						mLv_aw_13_mbr.setAdapter(adapter);
						mLv_aw_13_mbr.setSelection(position-1);
					}
				}
				);

		Intent intent	=	getIntent();
		String AssemblyName	=	intent.getStringExtra("AssemblyName");

		Log.i(LOGTAG, "AssemblyName="+AssemblyName);
		getDate(AssemblyName);
	}

	public void setData(){
		
		mLv_aw_13_mbr.removeFooterView(notThingFooter);
		
		if(mManager.count<1){
			mLv_aw_13_mbr.addFooterView(notThingFooter);
		}

		UserArrayAdapter myAapter = new UserArrayAdapter(this);

		mLv_aw_13_mbr.setAdapter(myAapter);

	}

	public void getDate(String AssemblyName){
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw13Activity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		

		String temp = Global.SERVER_URL	;	// + "/" + Global.JSP_SEARCH_REQUESTOR;

		Log.i(LOGTAG, "temp= "+temp);

		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_SEARCH_REQUESTOR));
		list.add(new SendQueue("mbr_name", AssemblyName));
		
		
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

					mManager = XmlParserManager.parsingAssemblyList(getApplicationContext(), result);

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

	class UserArrayAdapter extends ArrayAdapter 
	{
		Context context;
		ImageView check_off;
		ImageView check_on;
		TextView tv;

		@SuppressWarnings("unchecked")
		public UserArrayAdapter(Context context) 
		{
			super(context, R.layout.layout_aw_13_list, mManager.getList());

			// TODO Auto-generated constructor stub

			this.context = context;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_13_list, null);
			}

			AssemblySearchInfo info =  mManager.getList().get(position);

			String temp = "";
			temp = info.mbr_name + "/";
			temp += info.mbr_party_name.toString().replace("\n", "") + "/";
			temp += info.aide_name ;
			
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
			/*
				private String mAssem_mbr_uid 	;	//국회의원키
				private String mMbr_name      	;	//국회의원이름
				private String mMbr_party_code	;	//정당코드
				private String mMbr_party_name	;	//정당이름
				private String mAssem_aide_uid	;	//보좌관코드
				private String mAide_name     	;	//보좌관이름
			 */

			tv.setText(temp);
			return row;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_aw_13_cancel:
			finish();
			break;
		case R.id.btn_aw_13_ok:

			String splitPoint		=	""	;	//	구분자
			String msAssem_mbr_uid	=	""	;	//	국회의원키
			String msMbr_name		=	""	;	//	국회의원이름
			String msMbr_party_code	=	""	;	//	정당코드
			String msMbr_party_name	=	""	;	//	정당이름
			String msAssem_aide_uid =	""	;	//	보좌관코드
			String msAide_name 		= 	""	;	//	보좌관이름
			boolean chk_mbr_uid 	=	true;


			String tempAssem_mbr_uid		=	""	;//	의원코드 검증
			String tempAssem_mbr_uid_comp	=	""	;//	의원코드 검증
			int splitIndex	=	0;
			for(int i = 0 ;i<mManager.getList().size();i++){
				if(mManager.getList().get(i).is_checked){


					if(splitIndex==0){
						splitPoint = "";
					}else{
						splitPoint = ",";
					}
					tempAssem_mbr_uid	=	mManager.getList().get(i).assem_mbr_uid.trim();
					msAide_name += 		splitPoint+mManager.getList().get(i).aide_name.trim()		;	//	보좌관키
					msAssem_aide_uid +=	splitPoint+mManager.getList().get(i).assem_aide_uid.trim()	;	//	보좌관이름


					msAssem_mbr_uid	= mManager.getList().get(i).assem_mbr_uid	;
					msMbr_name		= mManager.getList().get(i).mbr_name		;

					msMbr_party_code= mManager.getList().get(i).mbr_party_code		;
					msMbr_party_name= mManager.getList().get(i).mbr_party_name		;

					//1명의 의원만 선택하기 위해 선택한 의원코드를 검증한다. 
					if(!tempAssem_mbr_uid.equals("")&&!tempAssem_mbr_uid_comp.equals("")){
						if(!tempAssem_mbr_uid_comp.equals(tempAssem_mbr_uid)){
							Toast toast	=	Toast.makeText(Aw13Activity.this, "1명의 의원만 선택할수 있습니다.", 1000);
							toast.show();
							chk_mbr_uid = false;
							break;
						}
					}else{
						tempAssem_mbr_uid_comp	=	msAssem_mbr_uid;

					}
					splitIndex++;
				}
			}
			
			if(chk_mbr_uid){
				Intent intent =	getIntent();
				intent.putExtra("msAide_name", msAide_name);
				intent.putExtra("msAssem_aide_uid", msAssem_aide_uid);
				intent.putExtra("msAssem_mbr_uid", msAssem_mbr_uid);
				intent.putExtra("msMbr_name", msMbr_name);
				intent.putExtra("msMbr_party_code", msMbr_party_code);
				intent.putExtra("msMbr_party_name", msMbr_party_name);
				setResult(RESULT_OK,intent);

				finish();
			}
			
			break;
		default:
			break;
		}
	}

}
