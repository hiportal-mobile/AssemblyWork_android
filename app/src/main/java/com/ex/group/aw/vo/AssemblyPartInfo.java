package com.ex.group.aw.vo;

import android.graphics.drawable.Drawable;


public class AssemblyPartInfo
{
	public String mbr_party_code;
	public String mbr_party_name;
	
	public Drawable drawable = null;
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.mbr_party_code).append(',');
		sb.append(this.mbr_party_name).append(',');
		return sb.toString();
	}
}
