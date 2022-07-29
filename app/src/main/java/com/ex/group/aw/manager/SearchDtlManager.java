package com.ex.group.aw.manager;
/*
 * 자료검색 > 상세
 *  */
import java.util.ArrayList;

import com.ex.group.aw.vo.SearchDtlInfo;
import com.ex.group.aw.vo.SearchDtlUserInfo;

public class SearchDtlManager
{
	public int count = 0;
	public int result_code = 0;
	public String result_msg = "";
	public String assem_mbr_names	="";
	public String ex_center_names	="";
	public String mas_bgn_date	="";
	public String hist_ins_date	="";
	public String mgre_file_path="";
	public String mgre_file_name="";
	public String main_tit="";
	public String sub_tit="";
	public String main_rej_memo =""	;
	public String re_user_name="";
	public String telephone_no="";
	public String sub_rel_fld_name_path="";
	public int refile_list_count=0;
	public int file_list_count=0;
	
	private ArrayList<SearchDtlInfo> mList = new ArrayList<SearchDtlInfo>();
	private ArrayList<SearchDtlUserInfo> mList2 = new ArrayList<SearchDtlUserInfo>();
	public SearchDtlManager(){}
	
	public ArrayList<SearchDtlInfo> getList()
	{
		return mList;
	}
	public ArrayList<SearchDtlUserInfo> getList2()
	{
		return mList2;
	}
}

