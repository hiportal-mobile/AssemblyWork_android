package com.ex.group.aw.manager;

/*
 * 요청자료
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.RequestMstInfo;

public class RequestMstManager
{
	public int count = 0;
	public int result_code = 0;
	public int total_count = 0;
	public String result_msg = "";
	
	private ArrayList<RequestMstInfo> mList = new ArrayList<RequestMstInfo>();
	public RequestMstManager(){}
	
	public ArrayList<RequestMstInfo> getList()
	{
		return mList;
	}
}
