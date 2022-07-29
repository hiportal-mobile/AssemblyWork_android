package com.ex.group.aw.database;

import android.provider.BaseColumns;


public class DatabaseConstants {
	
	public static final class CreateDB implements BaseColumns
	{
		public static final String MBR_UID = "assem_mbr_uid";		
		public static final String MBR_NAME = "mbr_name";		
		public static final String MBR_PARTY_CODE = "mbr_party_code";
		public static final String MBR_PARTY_NAME = "mbr_party_name";
		public static final String MBR_COMMIT_CODE = "mbr_commit_code";
		public static final String MBR_COMMIT_NAME = "mbr_commit_name";
		public static final String MBR_REGION = "mbr_region";
		public static final String MBR_EMAIL = "mbr_email";
		public static final String MBR_TEL = "mbr_tel";
		public static final String MBR_INFO_URL = "mbr_info_url";
		public static final String MBR_HOME_URL = "mbr_home_url";
		public static final String MBR_USE_YN = "mbr_use_yn";
		public static final String MBR_INS_USER_UID = "mbr_ins_user_uid";
		public static final String MBR_INS_DATE = "mbr_ins_date";
		public static final String MBR_UPD_USER_UID = "mbr_upd_user_uid";
		public static final String MBR_UPD_DATE = "mbr_upd_date";		
		public static final String MBR_FILE_NAME = "mbr_file_name";
		public static final String MBR_FILE_SAVE_NAME = "mbr_file_savename";
		public static final String MBR_FILE_SRC = "mbr_file_src";
		public static final String FILE_SRC = "file_src";
		public static final String PHOTO_URL = "photo_url";
		public static final String DRAWABLE_BYTE = "drawable_byte";
		
		public static final String PERSON_TABLENAME = "mbr_person";

		public static final String PERSON_TABLE_CREATE = 
			"create table "+PERSON_TABLENAME+
				"("					
					+_ID+" integer primary key autoincrement, "
					+MBR_UID+" text not null Unique, " 
					+MBR_NAME+" text, "
					+MBR_PARTY_CODE+" text, " 
					+MBR_PARTY_NAME+" text, " 
					+MBR_COMMIT_CODE+" text, " 
					+MBR_COMMIT_NAME+" text, " 
					+MBR_REGION+" text, " 
					+MBR_EMAIL+" text, " 
					+MBR_TEL+" text, " 
					+MBR_INFO_URL+" text, " 
					+MBR_HOME_URL+" text, " 
					+MBR_USE_YN+" text, " 
					+MBR_INS_USER_UID+" text, "
					+MBR_INS_DATE+" text, " 
					+MBR_UPD_USER_UID+" text, "
					+MBR_UPD_DATE+" text, " 
					+MBR_FILE_NAME+" tex, "
					+MBR_FILE_SAVE_NAME+" text , " 
					+MBR_FILE_SRC+" text, " 
					+FILE_SRC+" text, " 
					+PHOTO_URL+" text, "
					+DRAWABLE_BYTE+" BLOB " +
			");";
		
	}
		
	
}
