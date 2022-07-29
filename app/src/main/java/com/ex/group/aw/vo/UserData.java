package com.ex.group.aw.vo;

import android.graphics.drawable.Drawable;


public class UserData
{
	public String uuid    ;
	public String login_id    ;
	public String name_ko    ;
	public String position_code    ;
	public String position_name_ko    ;
	public String group_uuid    ;
	public String group_name_ko    ;
	public String auth    ;
	public String PHONE_NO    ;
	public String APP_ID    ;
	public String APP_VER    ;
	
	
	public Drawable drawable = null;
	public boolean is_checked = false;

    private static UserData instance = null;
    private UserData(){};
    
    public static UserData getInstance()
    {
    	if( instance == null )
    	{
    		instance = new UserData();
    	}
    	
    	return instance;
    }	
	
}
