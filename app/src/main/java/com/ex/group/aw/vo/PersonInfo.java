package com.ex.group.aw.vo;

import android.graphics.drawable.Drawable;


public class PersonInfo
{
	public static final int NONE = 0;
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int UPDATE_URL = 3;
	
	public String assem_mbr_uid;
	public String mbr_name;
	public String mbr_party_code;
	public String mbr_party_name;
	public String mbr_commit_code;
	public String mbr_commit_name;
	public String mbr_region;
	public String mbr_email;
	public String mbr_tel;
	public String mbr_info_url;
	public String mbr_home_url;
	public boolean mbr_use_yn;
	public String mbr_ins_user_uid;
	public String mbr_ins_date;
	public String mbr_upd_user_uid;
	public String mbr_upd_date;
	public String mbr_file_name;
	public String mbr_file_savename;
	public String mbr_file_src;
	public String file_src;
	public String photo_url;
	
	//	2: insert
	//	1: update
	//	3: image update
	//	0: none
	public int query_type;
	
	public Drawable drawable = null;
	
	//	return values
	//	0: 모든 값이 같을 때...
	//	1: uid, photo_url 을 제외한 다른 값이 다를 때..
	//	2: uid는 같고, photo_url은 다를 때...

	public int isEquals(PersonInfo info)
	{
		if( !this.photo_url.equals(info.photo_url)) 
		{
			return UPDATE_URL; 
		}

		if(!this.mbr_name.equals(info.mbr_name)
			|| !checkNull(this.mbr_party_code).equals(checkNull(info.mbr_party_code))
			|| !checkNull(this.mbr_party_name).equals(checkNull(info.mbr_party_name))
			|| !checkNull(this.mbr_commit_code).equals(checkNull(info.mbr_commit_code))
			|| !checkNull(this.mbr_commit_name).equals(checkNull(info.mbr_commit_name))
			|| !checkNull(this.mbr_region).equals(checkNull(info.mbr_region))
			|| !checkNull(this.mbr_email).equals(checkNull(info.mbr_email))
			|| !checkNull(this.mbr_tel).equals(checkNull(info.mbr_tel))
			|| !checkNull(this.mbr_info_url).equals(checkNull(info.mbr_info_url))
			|| !this.mbr_use_yn == info.mbr_use_yn
			|| !checkNull(this.mbr_ins_user_uid).equals(checkNull(info.mbr_ins_user_uid))
			|| !checkNull(this.mbr_ins_date).equals(checkNull(info.mbr_ins_date))
			|| !checkNull(this.mbr_upd_user_uid).equals(checkNull(info.mbr_upd_user_uid))
			|| !checkNull(this.mbr_upd_date).equals(checkNull(info.mbr_upd_date))
			|| !checkNull(this.mbr_file_name).equals(checkNull(info.mbr_file_name))
			|| !checkNull(this.mbr_file_savename).equals(checkNull(info.mbr_file_savename))
			|| !checkNull(this.mbr_file_src).equals(checkNull(info.mbr_file_src))
			|| !checkNull(this.file_src).equals(checkNull(info.file_src)) 
			|| !checkNull(this.photo_url).equals(checkNull(info.photo_url) ))
		{
			
			return UPDATE;
			
		}
		
		return NONE;
		
	}

	
	/**
	 * @param info
	 * @return boolean - true : uid값이 같은경우, false : uid 값이 다른경우
	 */
	public boolean isUidEquals(PersonInfo info){
		
		return info.assem_mbr_uid.equals(this.assem_mbr_uid);
	}
	
	/**
	 * Null 체크 함수
	 * @param str
	 * @return
	 */
	private String checkNull(String str)
	{
		if( str == null )
		{
			return "";
		}
		
		return str;
	}
	

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.assem_mbr_uid).append(',');
		sb.append(this.mbr_name).append(',');
		sb.append(this.mbr_party_code).append(',');
		sb.append(this.mbr_party_name).append(',');
		sb.append(this.mbr_commit_code).append(',');
		sb.append(this.mbr_commit_name).append(',');
		sb.append(this.mbr_region).append(',');
		sb.append(this.mbr_email).append(',');
		sb.append(this.mbr_tel).append(',');
		sb.append(this.mbr_info_url).append(',');
		sb.append(this.mbr_home_url).append(',');
		sb.append(this.mbr_use_yn).append(',');
		sb.append(this.mbr_ins_user_uid).append(',');
		sb.append(this.mbr_ins_date).append(',');
		sb.append(this.mbr_upd_user_uid).append(',');
		sb.append(this.mbr_upd_date).append(',');
		sb.append(this.mbr_file_name).append(',');
		sb.append(this.mbr_file_savename).append(',');
		sb.append(this.mbr_file_src).append(',');
		sb.append(this.file_src).append(',');
		sb.append(this.photo_url);
		return sb.toString();
	}
}
