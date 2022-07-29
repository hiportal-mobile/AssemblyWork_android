package com.ex.group.aw.manager;
/*
 * 작성자지정 기본 정보
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.RequestWriterFileInfo;
import com.ex.group.aw.vo.RequestWriterInfo;

public class RequestWriterInfoManager
{
	public String task_mas_uid="";			//국회업무 마스터 키
	public String mas_type_code="";			//요구자료(DR), 질의답변(QA), 서면답변(WR), 국감결과(IR), 예상Q&A(IP)
	public String assem_mbr_names="";		//국회의원 이름
	public String assem_aide_names="";		//보좌관 이름
	public String mas_bgn_date="";			//요청일
	public String mas_end_date="";			//제출기한
	
	public String rel_assem_mbr_uids ; 		//관련의원 이름s (여러 건인 경우 /로 연결)
	public String rel_assem_mbr_names;		// 관련의원 키s (여러 건인 경우 /로 연결)
	public String ex_center_uids; 			// 해당본부 키
	public String ex_center_names;			//해당본부 이름
	
	public String task_dist_main_uid="";	//주무자 배부정보 키
	public String main_seq_no="";			//요구문항 담당자 배부순번
	public String main_no="";				//요구문항 대항목번호
	public String main_tit="";				//제목
	public String main_req_memo="";			//요청내용
	public String main_rec_user_names="";	//담당자 이름(1차)
	public String main_qst_yn="";			//대항목 자체질문 여부(Y/N)
	public String main_sec_code="";			//담당자 보안등급(1차)
	
	public int count = 0;
	public int count2 = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	private ArrayList<RequestWriterInfo> mList = new ArrayList<RequestWriterInfo>();
	private ArrayList<RequestWriterFileInfo> mList2 = new ArrayList<RequestWriterFileInfo>();
	public RequestWriterInfoManager(){}
	
	public ArrayList<RequestWriterInfo> getList()
	{
		return mList;
	}
	
	public ArrayList<RequestWriterFileInfo> getList2()
	{
		return mList2;
	}
}
