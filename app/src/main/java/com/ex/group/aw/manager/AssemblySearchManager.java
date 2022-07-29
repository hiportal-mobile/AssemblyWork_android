package com.ex.group.aw.manager;
/*
 * 요청자검색
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.AssemblySearchInfo;

public class AssemblySearchManager
{
	
	public String m_mbr_name;
	public String m_assem_mbr_uid;
	
	public int count = 0;
	public int result_code = 0;
	public int Total_count = 0;
	public String result_msg = "";
	
	private ArrayList<AssemblySearchInfo> mList = new ArrayList<AssemblySearchInfo>();
	public AssemblySearchManager(){}
	
	public ArrayList<AssemblySearchInfo> getList()
	{
		return mList;
	}
}
