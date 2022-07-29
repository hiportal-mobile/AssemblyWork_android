package com.ex.group.aw.manager;
/*
 * 정당검색
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.AssemblyPartInfo;

public class AssemblyPartManager
{
	public int count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	private ArrayList<AssemblyPartInfo> mList = new ArrayList<AssemblyPartInfo>();
	public AssemblyPartManager(){}
	
	public ArrayList<AssemblyPartInfo> getList()
	{
		return mList;
	}
}
