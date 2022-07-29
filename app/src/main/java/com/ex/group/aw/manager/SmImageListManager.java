package com.ex.group.aw.manager;
/*
 * 의원 목록
 * */
import java.util.ArrayList;

import com.ex.group.aw.vo.SmImageListInfoIp;

public class SmImageListManager
{
	
	public int count	=	0;	
	public int result_code	=	0;
	public String result_msg	=	"";

	
	private ArrayList<SmImageListInfoIp> mList = new ArrayList<SmImageListInfoIp>();
	public SmImageListManager(){}

	public ArrayList<SmImageListInfoIp> getList()
	{
		return mList;
	}
	
	
}
