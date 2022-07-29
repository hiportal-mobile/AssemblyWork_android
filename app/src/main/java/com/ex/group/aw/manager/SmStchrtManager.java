package com.ex.group.aw.manager;
/*
 * 의원 목록
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.SmStchrtInfo;

public class SmStchrtManager
{
	public String mas_tit = "";
	public String mas_man_cnt = "";
	public String col_count = "";
	public String row_count = "";
	public int count = 0;
	public int result_code = 0;
	public String result_msg = "";
	
	private ArrayList<SmStchrtInfo> mList = new ArrayList<SmStchrtInfo>();
	public SmStchrtManager(){}

	public ArrayList<SmStchrtInfo> getList()
	{
		return mList;
	}
	
}
