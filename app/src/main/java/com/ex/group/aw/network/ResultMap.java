package com.ex.group.aw.network;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class ResultMap
{
	private final String LOGTAG = "ResultMap";
	private Map<String, String> map;
	
	public ResultMap()
	{
		map = new HashMap<String, String>();
	}
	
	public ResultMap(String str)
	{
		map = new HashMap<String, String>();
		
		Parse(str);
	}
	
	public void Parse(String str)
	{
		Log.i(LOGTAG, str);
		String[] buffer = str.split("&");
		
		for( int i=0 ; i<buffer.length ; i++ )
		{
			String[] temp = buffer[i].split("=");
			Log.i(LOGTAG, temp[0] + "," + temp[1]);
			map.put(temp[0], temp[1]);
		}
	}
	
	public String getValue(String key)
	{
		return map.get(key);
	}
}
