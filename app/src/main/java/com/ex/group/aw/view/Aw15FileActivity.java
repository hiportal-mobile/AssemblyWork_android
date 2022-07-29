package com.ex.group.aw.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.AttachFileManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;


public class Aw15FileActivity extends Activity {

	static final String ROOT_PATH = "/sdcard/download"			;
	static final String LOGTAG	=	"Aw15FileActivity"	;
	ListView mListView									;
	List<String> mItem									;
	List<String> mPath									;
	List<Boolean> mFileGbn								;
	String installFilePath								;
	private ProgressDialog 	mDlgNetwork					;
	private Thread  thread = null						;
	private HashMap<String, String> mManager	=	null;
	private String mAttcFilePath = ""					;	//	첨부파일 경로
	private AwCustomPopup popup							;
	private AttachFileManager mFileManager	=	null	;
	private String mGbnCd	=	""						;	//업무구분코드

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
				
				Log.i(LOGTAG, temp);
				AlertDialog(temp);
			}
			//	실패에 따른 메세지 출력
			break;
			case Global.MSG_SUCEEDED:
			{
				switch( msg.arg1 )
				{
				case 1:
					Log.i(LOGTAG,"fileName="+mManager.get("fileName"));
					Log.i(LOGTAG,"uploadFileKey="+ mManager.get("uploadFileKey"));

					if( mDlgNetwork.isShowing())
					{
						mDlgNetwork.dismiss();
					}
					
					saveResultData();	
					break;
				case 2:

					Intent intent	=	getIntent();
					intent.putExtra("sFile_savename", mFileManager.file_savename);
					intent.putExtra("sFile_name", mFileManager.file_name);
					intent.putExtra("sFile_size", mFileManager.file_size);
					setResult(RESULT_OK,intent);

					Log.i(LOGTAG, "result : "+mFileManager.result_code);
					Log.i(LOGTAG, "file_name : "+mFileManager.file_name);
					Log.i(LOGTAG, "file_savename : "+mFileManager.file_savename);
					Log.i(LOGTAG, "file_size : "+mFileManager.file_size);
					finish();
					break;
					
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
		popup=null;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_15_file);

		//mTextPath = (TextView)findViewById(R.id.text_path);
		mListView = (ListView)findViewById(R.id.list_dir);
		mListView.setOnItemClickListener(mItemClickListener);
		mItem = new ArrayList<String>();
		mPath = new ArrayList<String>();
		mFileGbn	=	new ArrayList<Boolean>();
		Intent intent	=	getIntent();
		mGbnCd	=	intent.getStringExtra("mGbnCd");
		installFilePath = null;
		Log.i(LOGTAG, "mGbnCd="+mGbnCd);
		getDir(ROOT_PATH);


	}

	OnItemClickListener mItemClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			File file = new File(mPath.get(position));
			String filepath = file.getPath().toLowerCase();
			if (file.isDirectory()) {
				if(file.canRead())
					getDir(mPath.get(position));
				else{
					new AlertDialog.Builder(Aw15FileActivity.this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[ " + file.getName() + " ] folder can't be read.")
					.setPositiveButton("OK", null)
					.show();
				}
			}
			else{
				//1차 파일 업로드 
				mAttcFilePath = filepath;	
				sendData();
				Log.i("FilePath", filepath);
				//Log.i("fileName", mManager.get("fileName"));
			}           
		}
	};


	public static String getExtension(String fileStr) {
		return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
	}

	void getDir(String dirPath) {
		//mTextPath.setText(dirPath);

		mItem.clear();
		mPath.clear();
		mFileGbn.clear();
		File f = new File(dirPath);
		File[] files = f.listFiles();
		if(!dirPath.equals(ROOT_PATH)) {
			mItem.add("뒤로가기");
			mPath.add(f.getParent());
			mFileGbn.add(false);
		}
		for(int i=0; i<files.length; i++) {
			File file = files[i];
			mPath.add(file.getPath());
			if(file.isDirectory()){
				mItem.add(file.getName());
				mFileGbn.add(true);
			}else{
				mItem.add(file.getName());
				mFileGbn.add(false);
			}
		}
		//Collections.sort(mItem);
		AwArrayAdapter fileList = new AwArrayAdapter(Aw15FileActivity.this);
		mListView.setAdapter(fileList);

	}

	//등록 
	public void sendData(){

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw15FileActivity.this, "첨부파일 저장", "등록중...");
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
					mManager = XmlParserManager.parsingAttachFile1(getApplicationContext(), result);
					if( mManager == null )
					{
						Log.i(LOGTAG, "manager is memory allocation failed");
						msg.what = Global.MSG_FAILED;
						msg.arg1 = Global.HM_ERR_XML_PARSING;
						mHandler.sendMessage(msg);
						return;

					}else{
						//Log.i(LOGTAG, mManager.get("uploadFileKey"));
						if( mManager.get("uploadFileKey")!=null)
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

	// 파일저장 결과 받기
	public void saveResultData(){

		if( mDlgNetwork == null || !mDlgNetwork.isShowing())
		{
			mDlgNetwork = ProgressDialog.show(Aw15FileActivity.this, "첨부파일 등록", "등록중...");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.SERVER_URL;// + "/" + Global.JSP_FILE_UPLOAD;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_FILE_UPLOAD));
		list.add(new SendQueue("file_key",  mManager.get("uploadFileKey")));
		list.add(new SendQueue("mas_type_code", mGbnCd));

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
					if( mManager == null )
					{
						Log.i(LOGTAG, "manager is memory allocation failed");
						msg.what = Global.MSG_FAILED;
						msg.arg1 = Global.HM_ERR_XML_PARSING;
						mHandler.sendMessage(msg);
						return;

					}else{
						if( mFileManager.result_code == 1000)
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

	class AwArrayAdapter extends ArrayAdapter 
	{
		Context context;

		TextView title;
		ImageView folderIco;

		@SuppressWarnings("unchecked")
		public AwArrayAdapter(Context context) 
		{
			super(context, R.layout.layout_aw_15_file_list, mItem);

			// TODO Auto-generated constructor stub

			this.context = context;
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_15_file_list, null);
			}

			title 	= (TextView)row.findViewById(R.id.track);
			folderIco = (ImageView)row.findViewById(R.id.nowplay);
			folderIco.setVisibility(View.GONE);

			if(mFileGbn.get(position))
				folderIco.setVisibility(View.VISIBLE);
			title.setText(mItem.get(position));
			return row;
		}
	}

}
