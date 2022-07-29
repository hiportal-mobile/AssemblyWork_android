package com.ex.group.aw.vo;



public class AssemblySearchInfo
{
	public String assem_mbr_uid ="" ;
	public String mbr_name  ="";
	public String mbr_party_code ="" ;
	public String mbr_party_name ="" ;
	public String mbr_email ="" ;
	public String mbr_tel ="" ;
	public String assem_aide_uid =""  ;
	public String aide_name ="" ;
	public String aide_tel ="" ;
	public String aide_email ="" ;
	
	public boolean is_checked = false;
	
	
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.assem_mbr_uid).append(',');
		sb.append(this.mbr_name).append(',');
		sb.append(this.mbr_party_code).append(',');
		sb.append(this.mbr_party_name).append(',');
		sb.append(this.mbr_email).append(',');
		sb.append(this.mbr_tel).append(',');
		sb.append(this.assem_aide_uid).append(',');
		sb.append(this.aide_name).append(',');
		sb.append(this.aide_tel).append(',');
		sb.append(this.aide_email).append(',');
		return sb.toString();
	}
}
