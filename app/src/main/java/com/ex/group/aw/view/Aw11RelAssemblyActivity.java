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
import com.ex.group.aw.manager.RelAssemblyManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.RelAssemblyInfo;

/*
	화면명: 자료요청 > 의원요청등록 > 관련의원검색 복수
	작성자: 방종희
	DESC: 
	DATE: 2013.05.15
	VERSION: 0.1
 */

public class Aw11RelAssemblyActivity extends Activity implements OnClickListener
{
	private final static String LOGTAG = "Aw11RelAssemblyActivity";
	private ListView mLv_aw_13_mbr;
	private Button  mBtn_aw_13_cancel;
	private Button  mBtn_aw_13_ok;
	private EditText mEt_aw_13_search;		//검색어
	private Button mBtn_aw_13_search;		//검색어 버튼


	private ProgressDialog 	mDlgNetwork;
	private Thread  thread = null;
	private AwCustomPopup popup;
	private View notThingFooter;				//	자료 없음 Footer
	private RelAssemblyManager mManager = new RelAssemblyManager();
	private View moreFooter ;				//	더보기 리스트 Footer
	private int mCurPageNo	=	0;
	private String mbr_name ="";

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
		setContentView(R.layout.layout_aw_11_relassembly);

		mLv_aw_13_mbr	=	(ListView)findViewById(R.id.lv_aw_13_mbr);
		mBtn_aw_13_cancel=	(Button)findViewById(R.id.btn_aw_13_cancel);
		mBtn_aw_13_ok	=	(Button)findViewById(R.id.btn_aw_13_ok);
		mEt_aw_13_search=	(EditText)findViewById(R.id.et_aw_13_search);	//검색어
		mBtn_aw_13_search=	(Button)findViewById(R.id.btn_aw_13_search);	//검색어 버튼

		mBtn_aw_13_cancel.setOnClickListener(this);
		mBtn_aw_13_ok.setOnClickListener(this);
		mBtn_aw_13_search.setOnClickListener(this);

		//		자료 없음 푸터
		LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		notThingFooter =  inflater2.inflate(R.layout.layout_listview_footer_notthing, null);
		
		// 더보기 푸터 생성
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		moreFooter =  inflater.inflate(R.layout.layout_listview_footer_more, null);
		TextView moreBtn = (TextView)moreFooter.findViewById(R.id.btn_more);

		//		더보기 호출 	
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mCurPageNo++;	//	가져올 페이지 번호 
				getDate(mbr_name);	//	리스트 데이터 
			}
		});

		mLv_aw_13_mbr.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
					RelAssemblyInfo info =  mManager.getList().get(position);

					//이미 선택되었는지 확인 
//						if(tempSaveAssem_mbr_uid.matches(".*"+info.assem_mbr_uid+".*")){
//							Toast.makeText(Aw11RelAssemblyActivity.this, "이미 선택된 의원입니다.", Toast.LENGTH_SHORT).show();
//							return;
//						}
					if( info.is_checked )
					{
						info.is_checked = false;
					}
					else
					{
						info.is_checked = true;
					}

					
					UserArrayAdapter adapter = new UserArrayAdapter(Aw11RelAssemblyActivity.this);
					mLv_aw_13_mbr.setAdapter(adapter);
					mLv_aw_13_mbr.setSelection(position-2);
					
					
					
				}
			}
		);

		//getDate(mbr_name);
	}

	public void setData(boolean more){
		
		mLv_aw_13_mbr.removeFooterView(moreFooter);
		mLv_aw_13_mbr.removeFooterView(notThingFooter);

		if(mManager.total_count>mCurPageNo*mManager.count){
			mLv_aw_13_mbr.addFooterView(moreFooter);
		}

		if(mManager.total_count<1){
			try {
				mLv_aw_13_mbr.addHeaderView(notThingFooter);
			}catch (Exception e) {
				// TODO: handle exception
				String err	=	e.toString();
				Log.e(LOGTAG, "notthingFoorer add fail.. error : "+err);
			}
		}
		UserArrayAdapter myAapter = new UserArrayAdapter(this);

		if(! more ){
			mLv_aw_13_mbr.setAdapter(myAapter);
		}
		// 더보기  리스트 
		myAapter.notifyDataSetChanged();

	}

	public void getDate(String mbr_name){
		
		if(mCurPageNo==0){mCurPageNo=1;}
		
		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw11RelAssemblyActivity.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();



		String temp = Global.SERVER_URL	;	// + "/" + Global.JSP_REL_ASSEMBLY_LIST;

		Log.i(LOGTAG, "temp= "+temp);

		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_REL_ASSEMBLY_LIST));
		list.add(new SendQueue("mbr_name", mbr_name));
		list.add(new SendQueue("pageno",Integer.toString(mCurPageNo)));


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

					XmlParserManager.parsingRelAssemblyList(getApplicationContext(), result,mManager);

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
			super(context, R.layout.layout_aw_11_relassembly_list, mManager.getList());

			// TODO Auto-generated constructor stub

			this.context = context;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {


			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_11_relassembly_list, null);

			}
			check_off 	= (ImageView)row.findViewById(R.id.iv_aw_common_list_check_off);
			check_on 	= (ImageView)row.findViewById(R.id.iv_aw_common_list_check_on);
			tv			= (TextView)row.findViewById(R.id.tv_aw_common_list_content);

			RelAssemblyInfo info =  mManager.getList().get(position);

			String mbr_name			=	info.mbr_name;
			String mbr_part_name	=	info.mbr_party_name;

			String temp = "";
			temp = mbr_name + "/";
			temp += mbr_part_name;

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

			return row;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		String splitPoint		=	""	;	//	구분자
		int splitIndex		=	0;
		String Rel_assem_mbr_uids	=	"";
		String Rel_assem_mbr_names	=	"";	
		String Rel_assem_mbr_party_code	=	"";
		String Rel_assem_mbr_party_name	=	"";	
		
		switch (v.getId()) {
		case R.id.btn_aw_13_cancel:
			finish();
			break;
		case R.id.btn_aw_13_ok:
			if(mManager!=null){
				for(int i = 0 ;i<mManager.getList().size();i++){
					if(mManager.getList().get(i).is_checked){
						if(splitIndex==0){
							splitPoint = "";
						}else{
							splitPoint = "/";
						}
						Rel_assem_mbr_uids 		+= 		splitPoint+mManager.getList().get(i).assem_mbr_uid.trim()		;	//	보좌관키
						Rel_assem_mbr_names 	+=		splitPoint+mManager.getList().get(i).mbr_name.trim()			;	//	보좌관이름
						Rel_assem_mbr_party_code+= 		splitPoint+mManager.getList().get(i).mbr_party_code.trim()		;	//	정당코드
						Rel_assem_mbr_party_name+= 		splitPoint+mManager.getList().get(i).mbr_party_name.trim()		;	//	정당이름

						splitIndex++;
					}
				}
				}
			
			if(Rel_assem_mbr_uids.equals("")){
				Toast.makeText(Aw11RelAssemblyActivity.this, "의원을 한 명 이상 선택해 주세요.", Toast.LENGTH_SHORT).show();
				return ;
			}
			Intent intent =	getIntent();
			intent.putExtra("Rel_assem_mbr_uids", Rel_assem_mbr_uids);
			intent.putExtra("Rel_assem_mbr_names", Rel_assem_mbr_names);
			
			intent.putExtra("Rel_assem_mbr_party_code", Rel_assem_mbr_party_code);
			intent.putExtra("Rel_assem_mbr_party_name", Rel_assem_mbr_party_name);
			
			intent.putExtra("isIp", true);
			setResult(RESULT_OK,intent);
			finish();
			break;
		case R.id.btn_aw_13_search:
		{
			 mbr_name	=	mEt_aw_13_search.getText().toString();
			if (mbr_name.equals("")){
				Toast.makeText(Aw11RelAssemblyActivity.this, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			mManager = new RelAssemblyManager();
			getDate(mbr_name);

		}
		break;
		default:
			break;
		}
	}

}
