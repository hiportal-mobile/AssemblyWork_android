package com.ex.group.aw.manager;

/*
 * 요청자료
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.RequestInfo;

public class RequestManager
{
	public int count = 0;
	public int result_code = 0;
	public int total_count = 0;
	public String result_msg = "";
	
	private ArrayList<RequestInfo> mList = new ArrayList<RequestInfo>();
	public RequestManager(){}
	
	public ArrayList<RequestInfo> getList()
	{
		return mList;
	}
}
