package com.ex.group.aw.manager;
/*
 * 자료검색
*/
import java.util.ArrayList;

import com.ex.group.aw.vo.SearchInfo;

public class SearchManager
{
	public int count = 0;
	public int total_count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	
	private ArrayList<SearchInfo> mList = new ArrayList<SearchInfo>();
	public SearchManager(){}
	
	public ArrayList<SearchInfo> getList()
	{
		return mList;
	}
}
