package com.ex.group.aw.manager;
/*
 * 의원 목록
 * */
import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.ex.group.aw.vo.PersonDetailInfo;

public class PersonDetailManager
{
	
	public String assem_mbr_uid="";
	public String mbr_name="";
	public String mbr_party_code="";
	public String mbr_party_name="";
	public String mbr_commit_code="";
	public String mbr_commit_name="";
	public String mbr_region="";
	public String mbr_email="";
	public String mbr_tel="";
	public String mbr_info_url="";
	public String mbr_home_url="";
	public String mbr_birth="";
	public boolean mbr_use_yn;
	public String mbr_career="";
	public String mbr_hall="";
	public String mbr_ins_user_uid="";
	public String mbr_ins_date="";
	public String mbr_upd_user_uid="";
	public String mbr_upd_date="";
	public String mbr_file_name="";
	public String mbr_file_savename="";
	public String mbr_file_src="";
	public String photo_url="";
	public int count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	public Drawable drawable = null;
	
	private ArrayList<PersonDetailInfo> mList = new ArrayList<PersonDetailInfo>();
	public PersonDetailManager(){}

	public ArrayList<PersonDetailInfo> getList()
	{
		return mList;
	}
	
}
