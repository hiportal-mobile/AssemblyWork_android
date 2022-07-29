package com.ex.group.aw.manager;

/*
 * 사용자 정보
 * */

public class UserInfoManager {
	public String auth_code = "";
	public String auth_name = "";
	public String smart_yn = "";
	public int result_code = 0;
	public String result_msg = "";
	
	static UserInfoManager instance = null;
	private UserInfoManager(){}
	
	public static UserInfoManager getInstance()
	{
		if( instance == null )
		{
			instance = new UserInfoManager();
		}
		
		return instance;
	}
	
	public void destroy()
	{
		instance = null;
	}
	
}

