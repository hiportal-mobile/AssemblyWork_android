package com.ex.group.aw.manager;
/*
 * 의원 목록
 * */
import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.ex.group.aw.vo.SmQnaInfo;
import com.ex.group.aw.vo.SmQnaInfoIp;

public class SmQnatManager
{
	public String assem_mbr_uid  = "";			//의원정보 마스트킼
	public String mbr_name  = "";				//의원이름
	public String mbr_party_name  = "";			//소속정당
	public String mbr_region  = "";				//지역구
	public String mbr_file_src  = "";			//사진경로
	public String mas_doc_dirt_yn = "";			//문서직접입력유무
	public String mas_dirt_tab_name = "";		//문서직접입력탭명
	public Drawable mbr_photo	=	null;		//의원 사진 이미지
	
	public int count	=	0;	
	public int result_code	=	0;
	public String result_msg	=	"";

	
	private ArrayList<SmQnaInfo> mList = new ArrayList<SmQnaInfo>();
	private ArrayList<SmQnaInfoIp> mList2 = new ArrayList<SmQnaInfoIp>();
	public SmQnatManager(){}

	public ArrayList<SmQnaInfo> getList()
	{
		return mList;
	}
	
	public ArrayList<SmQnaInfoIp> getList2()
	{
		return mList2;
	}
	
}
