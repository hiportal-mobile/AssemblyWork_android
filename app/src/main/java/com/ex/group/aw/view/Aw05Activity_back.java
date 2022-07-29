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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.group.aw.R;
import com.ex.group.aw.manager.RequestMstManager;
import com.ex.group.aw.manager.UserInfoManager;
import com.ex.group.aw.manager.XmlParserManager;
import com.ex.group.aw.network.ExNetwork;
import com.ex.group.aw.network.SendQueue;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.RequestMstInfo;

/*
	화면명: 자료요청리스트
	작성자: 방종희
	DESC: 
	DATE: 2013.04.16
	VERSION: 0.1
 */
public class Aw05Activity_back extends Activity implements OnClickListener {
	private final static String LOGTAG = "Aw05Activity";
	// layout item
	private Spinner mSpSearch; // 검색구분
	private Spinner mSpRequestGbn; // 요청구분
	private Button mBtnRequest; // 요청자료 등록
	private Button mBtnPrev; // 이전
	private Button mBtnSearch; // 검색
	private Button mBtnSearchDtl; // 상세검색
	private ListView mLvList; // 요청 목록
	private EditText mEtsearch; // 검색어

	private Button mBtn_aw_05_dr;
	private Button mBtn_aw_05_qa;
	private Button mBtn_aw_05_wr;
	private Button mBtn_aw_05_ir;
	private Button mBtn_aw_05_ip;

	private int mSearchIndex = 0; // 검색구분 index
	private int mRequestGbnIndex = 0; // 요청구분 index
	private String mGbncd = "DR"; // 요청자료등록 기본코드
	private String mGbnName = "요구자료"; // 요청자료등록 기본명칭
	private ProgressDialog mDlgNetwork;
	private Thread thread = null;
	private RequestMstManager mManager = new RequestMstManager();
	private int mCurPageNo = 0;
	private AwArrayAdapter mAdapter;

	private String smGbncd = ""; // 요청자료등록 기본코드
	private String smGbnName = ""; // 요청자료등록 기본명칭
	// 검색 변수
	private String mSearchSDate = ""; // 상세검색 시작일
	private String mSearchEDate = ""; // 상세검색 종료일
	private String mSearchPartCode = ""; // 상세검색 정당코드
	private String mSearchKeyword = ""; // 검색어

	private AwCustomPopup popup;

	private View moreFooter; // 더보기 리스트 Footer
	private View notThingFooter; // 자료 없음 Footer

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Global.MSG_FAILED: {
				Log.i("Aw05Activity", "MSG_FAILED : " + msg.arg1);
				String temp = "";
				switch (msg.arg1) {
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
			case Global.MSG_SUCEEDED: {
				// 리스트뷰에 내용으 표시한다.
				if (mCurPageNo == 1) {
					setData(false);
				} else {
					setData(true);
				}
			}
				break;
			}

			if (mDlgNetwork.isShowing()) {
				mDlgNetwork.dismiss();
			}
		}

	};

	public void AlertDialog(String temp) {

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

	public void initData() {
		mManager = new RequestMstManager();
		mSearchSDate = ""; // 상세검색 시작일
		mSearchEDate = ""; // 상세검색 종료일
		mSearchPartCode = ""; // 상세검색 정당코드
		mSearchKeyword = ""; // 검색어
		// mSearchIndex = 0; // 검색구분 index
		// mRequestGbnIndex = 0; // 자료요청구분 index
		mCurPageNo = 0; // 현재페이지 번호.
		mEtsearch.setText("");
	}

	private Spinner.OnItemSelectedListener onSpinnerSearch = new Spinner.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg2) {
			case 0:
				mSearchIndex = 0; // 요청자
				break;
			case 1:
				mSearchIndex = 1; // 제목 내용
				break;
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private Spinner.OnItemSelectedListener onSpinnerRequestGbn = new Spinner.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			switch (arg2) {
			case 0:
				mRequestGbnIndex = 0;
				smGbncd = "DR";
				smGbnName = "요구자료";
				break;
			case 1:
				mRequestGbnIndex = 1;
				smGbncd = "QA";
				smGbnName = "질의답변";
				break;
			case 2:
				mRequestGbnIndex = 2;
				smGbncd = "WR";
				smGbnName = "서면답변";
				break;
			case 3:
				mRequestGbnIndex = 3;
				smGbncd = "IR";
				smGbnName = "국감결과";
				break;
			case 4:
				mRequestGbnIndex = 4;
				smGbncd = "IP";
				smGbnName = "예상Q&amp;A";
				break;
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aw_05);

		mBtnPrev = (Button) findViewById(R.id.btn_aw_05_back);
		mBtnRequest = (Button) findViewById(R.id.btn_aw_05_request);
		mBtnSearch = (Button) findViewById(R.id.btn_aw_05_search);
		mBtnSearchDtl = (Button) findViewById(R.id.btn_aw_05_search_dtl);
		mEtsearch = (EditText) findViewById(R.id.et_aw_05_search);
		mBtn_aw_05_dr = (Button) findViewById(R.id.btn_aw_05_dr);
		mBtn_aw_05_qa = (Button) findViewById(R.id.btn_aw_05_qa);
		mBtn_aw_05_wr = (Button) findViewById(R.id.btn_aw_05_wr);
		mBtn_aw_05_ir = (Button) findViewById(R.id.btn_aw_05_ir);
		mBtn_aw_05_ip = (Button) findViewById(R.id.btn_aw_05_ip);
		RelativeLayout RelativeLayout03 = (RelativeLayout) findViewById(R.id.RelativeLayout03);

		// 담당자별 권한 국회담당자만 의원 자료요청등록이 표시되어야 한다.
		if (!UserInfoManager.getInstance().auth_code.equals("A")) {
			RelativeLayout03.setVisibility(View.GONE);
		}
		mSpSearch = (Spinner) findViewById(R.id.sp_aw_05_search);
		mSpRequestGbn = (Spinner) findViewById(R.id.sp_aw_05_request);
		mLvList = (ListView) findViewById(R.id.lv_aw_05_request_list);

		mBtnPrev.setOnClickListener(this);
		mBtnRequest.setOnClickListener(this);
		mBtnSearch.setOnClickListener(this);
		mBtnSearchDtl.setOnClickListener(this);
		mBtn_aw_05_dr.setOnClickListener(this);
		mBtn_aw_05_qa.setOnClickListener(this);
		mBtn_aw_05_wr.setOnClickListener(this);
		mBtn_aw_05_ir.setOnClickListener(this);
		mBtn_aw_05_ip.setOnClickListener(this);

		// Spinner Initialize
		// 검색구분
		mSpSearch.setPrompt("검색구분");
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.arr_aw_search,
				R.layout.layout_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpSearch.setAdapter(adapter);
		mSpSearch.setOnItemSelectedListener(onSpinnerSearch);
		mSpSearch.setSelection(1);

		// 요청구분
		mSpRequestGbn.setPrompt("요청항목");
		ArrayAdapter<?> adapter1 = ArrayAdapter.createFromResource(this, R.array.arr_aw_05_request_gbn,
				R.layout.layout_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpRequestGbn.setAdapter(adapter1);
		mSpRequestGbn.setOnItemSelectedListener(onSpinnerRequestGbn);

		// 더보기 푸터 생성
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		moreFooter = inflater.inflate(R.layout.layout_listview_footer_more, null);
		TextView moreBtn = (TextView) moreFooter.findViewById(R.id.btn_more);

		// 자료 없음 푸터
		LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		notThingFooter = inflater2.inflate(R.layout.layout_listview_footer_notthing, null);

		// 더보기 호출
		moreBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				mCurPageNo++; // 가져올 페이지 번호
				getReqList(); // 리스트 데이터
			}
		});

		// 리스트 아이템 클릭시 작성자 지정으로.
		mLvList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				// 상태 팝업
				RequestMstInfo info = mManager.getList().get(position);
				Intent Aw05popupIntent = new Intent(Aw05Activity_back.this, Aw05PopupActivity.class);
				Aw05popupIntent.putExtra("status11", mManager.getList().get(position).status11);

				Aw05popupIntent.putExtra("status11_yn", mManager.getList().get(position).status11_yn);

				Aw05popupIntent.putExtra("status12", mManager.getList().get(position).status12);

				Aw05popupIntent.putExtra("status12_yn", mManager.getList().get(position).status12_yn);

				Aw05popupIntent.putExtra("status13", mManager.getList().get(position).status13);

				Aw05popupIntent.putExtra("status13_yn", mManager.getList().get(position).status13_yn);

				Aw05popupIntent.putExtra("status21", mManager.getList().get(position).status21);

				Aw05popupIntent.putExtra("status21_yn", mManager.getList().get(position).status21_yn);

				Aw05popupIntent.putExtra("status22", mManager.getList().get(position).status22);

				Aw05popupIntent.putExtra("status22_yn", mManager.getList().get(position).status22_yn);

				Aw05popupIntent.putExtra("status23", mManager.getList().get(position).status23);

				Aw05popupIntent.putExtra("status23_yn", mManager.getList().get(position).status23_yn);

				Aw05popupIntent.putExtra("status24", mManager.getList().get(position).status24);

				Aw05popupIntent.putExtra("status24_yn", mManager.getList().get(position).status24_yn);

				Aw05popupIntent.putExtra("status25", mManager.getList().get(position).status25);

				Aw05popupIntent.putExtra("status25_yn", mManager.getList().get(position).status25_yn);

				Aw05popupIntent.putExtra("task_mas_uid", mManager.getList().get(position).task_mas_uid);
				Aw05popupIntent.putExtra("mGbncd", mGbncd);
				Aw05popupIntent.putExtra("mGbnName", mGbnName);
				startActivity(Aw05popupIntent);
				Log.i(LOGTAG, mManager.getList().get(position).task_mas_uid);
			}
		});
		mBtn_aw_05_dr.setBackgroundResource(R.drawable.yogu_on);
		// 리스트 목록
		getReqList();

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_aw_05_back: // 이전 버튼
		{
			finish();
		}
			break;
		case R.id.btn_aw_05_search: // 검색버튼
		{
			if (mSearchIndex == -1) {
				Toast.makeText(this, "검색구분을 선택해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			mSearchKeyword = mEtsearch.getText().toString();
			if (mSearchKeyword.equals("")) {
				Toast.makeText(this, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			mManager = new RequestMstManager();
			mCurPageNo = 1;
			getReqList();
		}
			break;
		case R.id.btn_aw_05_search_dtl: // 상세검색
		{
			startActivityForResult((new Intent(Aw05Activity_back.this, Aw10Activity.class)), 10);
		}
			break;
		case R.id.btn_aw_05_request: // 의원자료요청
		{
			// 요청구분 선택여부 확인
			if (mRequestGbnIndex == -1) {
				Toast.makeText(this, "요청구분을 선택해 주세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			// 의원자료
			Intent intent = new Intent(Aw05Activity_back.this, Aw11Activity.class);
			intent.putExtra("smGbncd", smGbncd);
			intent.putExtra("smGbnName", smGbnName);
			startActivityForResult(intent, 11);
		}
			break;
		case R.id.btn_aw_05_dr: // 요구자료
			mGbncd = "DR";
			mGbnName = "요구자료";
			initData(); // 데이터 초기화
			getReqList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_05_qa: // 질의답변
			mGbncd = "QA";
			mGbnName = "질의답변";
			initData(); // 데이터 초기화
			getReqList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_05_wr: // 서면답변
			mGbncd = "WR";
			mGbnName = "서면답변";
			initData(); // 데이터 초기화
			getReqList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_05_ir: // 국감결과
			mGbncd = "IR";
			mGbnName = "국감결과";
			initData(); // 데이터 초기화
			getReqList();
			setBtnImg(mGbncd);
			break;
		case R.id.btn_aw_05_ip: // 예상 Q&A
			mGbncd = "IP";
			mGbnName = "예상 Q&A";
			initData(); // 데이터 초기화
			getReqList();
			setBtnImg(mGbncd);
			break;
		default:
			break;
		}
	}

	private void setBtnImg(String Gubun) {
		// 이미지 변경
		mBtn_aw_05_dr.setBackgroundResource(R.drawable.yogu_off);
		mBtn_aw_05_qa.setBackgroundResource(R.drawable.qna_off);
		mBtn_aw_05_wr.setBackgroundResource(R.drawable.seomyun_off);
		mBtn_aw_05_ir.setBackgroundResource(R.drawable.kukgam_off);
		mBtn_aw_05_ip.setBackgroundResource(R.drawable.yesang_off);
		if (Gubun.equals("DR")) {
			mBtn_aw_05_dr.setBackgroundResource(R.drawable.yogu_on);
		} else if (Gubun.equals("QA")) {
			mBtn_aw_05_qa.setBackgroundResource(R.drawable.qna_on);
		} else if (Gubun.equals("WR")) {
			mBtn_aw_05_wr.setBackgroundResource(R.drawable.seomyun_on);
		} else if (Gubun.equals("IR")) {
			mBtn_aw_05_ir.setBackgroundResource(R.drawable.kukgam_on);
		} else if (Gubun.equals("IP")) {
			mBtn_aw_05_ip.setBackgroundResource(R.drawable.yesang_on);
		}
	}

	private void setData(boolean more) {
		// 더보기 보이기 숨기기
		mLvList.removeFooterView(moreFooter);
		mLvList.removeFooterView(notThingFooter);

		if (mManager.total_count > mCurPageNo * mManager.count) {
			mLvList.addFooterView(moreFooter);
		}

		mLvList.removeHeaderView(notThingFooter);

		if (mManager.total_count < 1) {
			try {
				mLvList.addHeaderView(notThingFooter);
			} catch (Exception e) {
				String err = e.toString();
			}
		}

		mAdapter = new AwArrayAdapter(Aw05Activity_back.this);
		if (!more) {
			mLvList.setAdapter(mAdapter);
		}
		// 더보기 리스트
		mAdapter.notifyDataSetChanged();
	}

	// 자료요청 목록
	public void getReqList() {

		if (mCurPageNo == 0) {
			mCurPageNo = 1;
		}

		if (mDlgNetwork == null || !mDlgNetwork.isShowing()) {
			mDlgNetwork = ProgressDialog.show(Aw05Activity_back.this, "모바일 국회", "데이터 검색중입니다.");
		}

		ArrayList<SendQueue> list = new ArrayList<SendQueue>();

		String temp = Global.SERVER_URL; // + "/" + Global.JSP_MST_REQUEST_LIST;
		list.add(new SendQueue("", temp));
		list.add(new SendQueue(Global.PRIMITIVE, Global.JSP_MST_REQUEST_LIST));
//		http://localhost:8080/assemblelist.jsp?searchKeyword=&searchCode=2&pageNo=1&bscode=N02145
		list.add(new SendQueue("searchKeyword", ""));
		list.add(new SendQueue("searchCode", "2"));
		list.add(new SendQueue("pageNo", Integer.toString(mCurPageNo)));
		list.add(new SendQueue("bscode", "N02145"));
		
		
		list.add(new SendQueue("pageno", Integer.toString(mCurPageNo))); // 페이지
																			// 번호

		list.add(new SendQueue("aams_task_mas_type_code", mGbncd)); // 업무구부분코드
		if (mSearchIndex == 0) {
			list.add(new SendQueue("assem_mbr_names", mSearchKeyword)); // 요청자
																		// or
																		// 해당본부
		}
		if (!mSearchPartCode.equals("")) {
			list.add(new SendQueue("assem_mbr_party_codes", mSearchPartCode)); // 정당코드
		}
		if (!mSearchSDate.equals("")) {
			list.add(new SendQueue("mas_bgn_date_from", mSearchSDate)); // 상세검색
																		// 시작일
		}
		if (!mSearchEDate.equals("")) {
			list.add(new SendQueue("mas_bgn_date_to", mSearchEDate)); // 상세검색
																		// 종료일
		}
		if (mSearchIndex == 1) {
			list.add(new SendQueue("title_contents", mSearchKeyword)); // 내용 제목
		}

		Log.i(LOGTAG, "title_contents=" + mSearchKeyword);

		list.add(new SendQueue("login_id", Global.LOGIN_ID));

		final ExNetwork http = new ExNetwork(list);
		thread = new Thread(new Runnable() {
			public void run() {
				http.SendData();

				String result = http.rcvString.replace("\n", "");

				Log.i(LOGTAG, result);
				Message msg = mHandler.obtainMessage();
				if (result.equals("N/A")) {
					msg.what = Global.MSG_FAILED;
					msg.arg1 = Global.HM_ERR_NETWORK;
					mHandler.sendMessage(msg);
					return;
				} else {

					XmlParserManager.parsingReqMstList(getApplicationContext(), result, mManager);

					if (mManager == null) {
						Log.i(LOGTAG, "manager is memory allocation failed");
						msg.what = Global.MSG_FAILED;
						msg.arg1 = Global.HM_ERR_XML_PARSING;
						mHandler.sendMessage(msg);
						return;

					} else {
						if (mManager.result_code == 1000) {
							msg.what = Global.MSG_SUCEEDED;

						} else {
							msg.what = Global.MSG_FAILED;
							msg.arg1 = Global.HM_ERR_XML_RESULT;
						}
						mHandler.sendMessage(msg);
						return;
					}
				}
			}
		}, LOGTAG + "thread");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 자료요청 어댑터
	 * 
	 * @author 방종희
	 *
	 */
	class AwArrayAdapter extends ArrayAdapter {
		Context context;

		TextView title;
		TextView Status1;
		TextView Status2;
		int m_nCount = 0;

		@SuppressWarnings("unchecked")
		public AwArrayAdapter(Context context) {
			super(context, R.layout.layout_aw_05_list, mManager.getList());

			// TODO Auto-generated constructor stub

			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = ((Activity) context).getLayoutInflater();

				row = inflater.inflate(R.layout.layout_aw_05_list, null);
			}

			title = (TextView) row.findViewById(R.id.tv_aw_05_list_title);
			Status1 = (TextView) row.findViewById(R.id.tv_aw_05_list_content);
			Status2 = (TextView) row.findViewById(R.id.tv_aw_05_list_content_st);

			String mas_rec_no = mManager.getList().get(position).mas_rec_no; // 국회업무
																				// 마스터관리번호
			String assem_mbr_names = mManager.getList().get(position).assem_mbr_names; // 요청자이름
			String mas_bgn_date = mManager.getList().get(position).mas_bgn_date; // 요청일
			String mas_end_date = mManager.getList().get(position).mas_end_date; // 제출기한
			String status = mManager.getList().get(position).status; // 취합현황
			String ex_center_names = mManager.getList().get(position).ex_center_names; // 해당본부
																						// 이름

			String tempTitle = "";
			if (mGbncd.equals("IP")) {
				tempTitle = String.format("%s %s %s/%s %s", mas_rec_no, ex_center_names, mas_bgn_date, mas_end_date,
						status);
			} else {
				tempTitle = String.format("%s %s %s/%s %s", mas_rec_no, assem_mbr_names, mas_bgn_date, mas_end_date,
						status);
			}
			title.setText(tempTitle);

			String status11 = mManager.getList().get(position).status11;
			String status12 = mManager.getList().get(position).status12;
			String status13 = mManager.getList().get(position).status13;
			String status21 = mManager.getList().get(position).status21;
			String status22 = mManager.getList().get(position).status22;
			String status23 = mManager.getList().get(position).status23;
			String status24 = mManager.getList().get(position).status24;
			String status25 = mManager.getList().get(position).status25;

			String tempStatus1 = String.format("1차 : %s %s %s", status11, status12, status13);

			Status1.setText(tempStatus1);

			String tempStatus2 = String.format("2차 : %s %s %s %s %s", status21, status22, status23, status24, status25);

			Status2.setText(tempStatus2);

			return row;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			// 상세검색
			if (requestCode == 10) {
				mSearchSDate = data.getStringExtra("aw_10_searchSdate"); // 상세검색
																			// 시작일
				mSearchEDate = data.getStringExtra("aw_10_searchEdate"); // 상세검색
																			// 종료일
				mSearchPartCode = data.getStringExtra("aw_10_searchPartCode"); // 상세검색
																				// 정당코드

				mManager = new RequestMstManager();
				getReqList();
			}

			// 요청자등록후 리스트 갱신
			if (requestCode == 11) {

				mManager = new RequestMstManager();
				getReqList();
			}

		}
	}

	@Override
	protected void onDestroy() {
		Log.d(LOGTAG, "called onDestroy");
		if (mDlgNetwork != null) {
			mDlgNetwork.dismiss();
		}
		super.onDestroy();
	}
}
