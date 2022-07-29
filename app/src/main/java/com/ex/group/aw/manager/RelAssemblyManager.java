package com.ex.group.aw.manager;

/*
 * 담당자검색
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.RelAssemblyInfo;

public class RelAssemblyManager
{
	public String m_assem_mbr_uid	="";
	public String m_mbr_name="";
	public int count = 0;
	public int total_count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	private ArrayList<RelAssemblyInfo> mList = new ArrayList<RelAssemblyInfo>();
	public RelAssemblyManager(){}
	
	public ArrayList<RelAssemblyInfo> getList()
	{
		return mList;
	}
}
