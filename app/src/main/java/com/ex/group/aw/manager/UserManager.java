package com.ex.group.aw.manager;

/*
 * 담당자검색
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.UserInfo;

public class UserManager
{
	public int total_count = 0; 
	public int count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	private ArrayList<UserInfo> mList = new ArrayList<UserInfo>();
	public UserManager(){}
	
	public ArrayList<UserInfo> getList()
	{
		return mList;
	}
}
