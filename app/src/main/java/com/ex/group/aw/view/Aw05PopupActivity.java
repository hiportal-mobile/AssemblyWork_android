package com.ex.group.aw.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.group.aw.R;
/*
	화면명: 자료요청리스트 - 상태 팝업 
	작성자: 방종희
	DESC: 
	DATE: 2013.04.16
	VERSION: 0.1
 */
public class Aw05PopupActivity extends Activity 
{
	private final static String LOGTAG = "Aw05PopupActivity";
	private ArrayList<String> list = new ArrayList<String>();
	private ArrayList<HashMap<String, String>> index_list = new ArrayList<HashMap<String, String>>();
	private String task_mas_uid=	"";
	private String mGbncd=	"";
	private String mGbnName=	"";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_05_popup);
		
		ListView lv_aw_05_popup_list	=	(ListView)findViewById(R.id.lv_aw_05_popup_list);
		Intent intent	=	getIntent();
		 
		String status11_yn	= 	intent.getStringExtra("status11_yn");
		String status12_yn	= 	intent.getStringExtra("status12_yn");
		String status13_yn	= 	intent.getStringExtra("status13_yn");
		String status21_yn	= 	intent.getStringExtra("status21_yn");
		String status22_yn	= 	intent.getStringExtra("status22_yn");
		String status23_yn	= 	intent.getStringExtra("status23_yn");
		String status24_yn	= 	intent.getStringExtra("status24_yn");
		String status25_yn	= 	intent.getStringExtra("status25_yn");
		
		task_mas_uid	= 	intent.getStringExtra("task_mas_uid");
		mGbncd			= 	intent.getStringExtra("mGbncd");
		mGbnName		= 	intent.getStringExtra("mGbnName");
		Log.i(LOGTAG, task_mas_uid);
		if(status11_yn.equals("Y")){		//1차 배부전
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status11_yn");
			item.put("stat_code", "12_DR");
			item.put("statusName","1차 배부전");
			index_list.add(item);
		}
		if(status12_yn.equals("Y")){		//1차 배부
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status12_yn");
			item.put("stat_code", "10_DT");
			item.put("statusName","1차 배부");
			index_list.add(item);
		}
		if(status13_yn.equals("Y")){		//1차 재검토
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status13_yn");
			item.put("stat_code", "11_RJ");
			item.put("statusName","1차 재검토");
			index_list.add(item);
		}
		if(status21_yn.equals("Y")){		//2차 작성전
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status21_yn");
			item.put("stat_code", "20_WR");
			item.put("statusName","2차 작성전");
			index_list.add(item);
		}
		if(status22_yn.equals("Y")){		//2차 재검토
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status22_yn");
			item.put("stat_code", "21_RJ");
			item.put("statusName","2차 재검토");
			index_list.add(item);
		}
		if(status23_yn.equals("Y")){		//2차 작성중
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status23_yn");
			item.put("stat_code", "24_WT");	
			item.put("statusName","2차 작성중");
			index_list.add(item);
		}
		if(status24_yn.equals("Y")){		//2차 결재중
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status24_yn");
			item.put("stat_code", "22_AP");
			item.put("statusName","2차 결재중");
			index_list.add(item);
		}
		if(status25_yn.equals("Y")){		//2차 결재완료
			HashMap<String, String> item = new HashMap<String, String>();
			Log.i(LOGTAG, "status25_yn");
			item.put("stat_code", "23_AC");
			item.put("statusName","2차 결재완료");
			index_list.add(item);
		}
		
		for( int i=0 ; i<index_list.size() ; i++)
		{
			HashMap<String, String> map =  index_list.get(i);
			Log.i(LOGTAG, map.get("stat_code") + ":" + map.get("statusName"));
		}
		
		AwArrayAdapter adapter	=	new AwArrayAdapter(Aw05PopupActivity.this);
		lv_aw_05_popup_list.setAdapter(adapter);
		lv_aw_05_popup_list.setDivider(null);
		lv_aw_05_popup_list.setSelector(R.drawable.selector_listview_bg);
		
		lv_aw_05_popup_list.setOnItemClickListener
		(
			new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
					String stat_code	=	"";
					String statusName	=	"";
					
					stat_code	=	index_list.get(position).get("stat_code");
					statusName	=	index_list.get(position).get("statusName");
				
					Intent	intent	=	new Intent(Aw05PopupActivity.this,Aw05DtlActivity.class);
					intent.putExtra("stat_code", stat_code);
					intent.putExtra("statusName", statusName);
					intent.putExtra("task_mas_uid",task_mas_uid);
					intent.putExtra("mGbncd", mGbncd);
					intent.putExtra("mGbnName", mGbnName);
					startActivity(intent);
					
					Log.i(LOGTAG, "mGbncd : "+mGbncd);
					Log.i(LOGTAG, "mGbnName : "+mGbnName);
					
					finish();
				}
			}
		);
		
	}
	
	class AwArrayAdapter extends ArrayAdapter 
	{
		Context context;
		TextView title;

		@SuppressWarnings("unchecked")
		public AwArrayAdapter(Context context) 
		{
			super(context, R.layout.layout_aw_05_popup_list, index_list);

			// TODO Auto-generated constructor stub

			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if(row == null)
			{
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();

				row =  inflater.inflate(R.layout.layout_aw_05_popup_list, null);
			}

			title 	= (TextView)row.findViewById(R.id.TextView01);
			
			//Log.i(LOGTAG, "position[" + position + "] : name[" + index_list.get(position).get("statusName") + "]");

			title.setText(index_list.get(position).get("statusName"));
			return row;
		}
	}	
	
}
