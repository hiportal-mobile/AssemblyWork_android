package com.ex.group.aw.manager;

/*
 * 담당자검색
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.GroupInfo;

public class GroupManager
{
	public int count = 0;
	public int total_count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	private ArrayList<GroupInfo> mList = new ArrayList<GroupInfo>();
	public GroupManager(){}
	
	public ArrayList<GroupInfo> getList()
	{
		return mList;
	}
}
