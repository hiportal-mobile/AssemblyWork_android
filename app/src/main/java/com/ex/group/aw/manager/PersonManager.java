package com.ex.group.aw.manager;
/*
 * 의원 목록
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.PersonInfo;

public class PersonManager
{
	public int count = 0;
	public int total_count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	private ArrayList<PersonInfo> mList = new ArrayList<PersonInfo>();
	public PersonManager(){}
	
	/**
	 * 
	 * @param info
	 * @return boolean 
	 */
	public boolean isUidEquals(PersonInfo info)
	{
		for(PersonInfo temp : mList)
		{
			if(temp.isUidEquals(info))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<PersonInfo> getList()
	{
		return mList;
	}
	
}
