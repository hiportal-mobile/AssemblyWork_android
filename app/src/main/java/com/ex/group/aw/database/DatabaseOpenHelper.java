package com.ex.group.aw.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.provider.BaseColumns;
import android.util.Log;

import com.ex.group.aw.util.CommonUtils;
import com.ex.group.aw.vo.PersonInfo;



public class DatabaseOpenHelper {

	
	private static final boolean DEBUG = true;
	private static final String TAG = "DataBase";
	
	private static final String DATABASE_NAME = "ex.db";	
	private static final int DATABASE_VERSION = 2;
	public static SQLiteDatabase mDB;
	private DatabaseHelper mDBHelper;
	private Context mContext;		
	
	public DatabaseOpenHelper(Context context)
	{
		this.mContext = context;
	}

	public DatabaseOpenHelper open() throws SQLException
	{
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper OPEN, getWritableDatabase:OK");
		
		mDBHelper = new DatabaseHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
		mDB = mDBHelper.getWritableDatabase();
		return this;
	}

	public void close()
	{
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper CLOSE");
		
		if(mDB.isOpen())
			mDB.close();		
	}


	//DB에 insert 하기
	public boolean insertColumn(String mbr_uid, String mbr_name, String mbr_party_code,
			String mbr_party_name, String mbr_commit_code, String mbr_commit_name,				
			String mbr_region, String mbr_email, String mbr_tel, String mbr_info_url,
			String mbr_home_url, String mbr_use_yn, String mbr_ins_user_uid, String mbr_ins_date,
			String mbr_upd_user_uid, String mbr_upd_date, String mbr_file_name, String mbr_file_save_name, String mbr_file_src,
			String file_src, String photo_url, Drawable drawable)
	{
			
		if(DEBUG)
			Log.d(TAG, "DataBase insertColumn { mbr_uid:"+mbr_uid+" mbr_name:"+mbr_name+" mbr_photo_url:"+photo_url+" }");
		
		ContentValues values = new ContentValues();
		values.put(DatabaseConstants.CreateDB.MBR_UID, mbr_uid);
		values.put(DatabaseConstants.CreateDB.MBR_NAME, mbr_name);
		values.put(DatabaseConstants.CreateDB.MBR_PARTY_CODE, mbr_party_code);
		values.put(DatabaseConstants.CreateDB.MBR_PARTY_NAME, mbr_party_name);
		values.put(DatabaseConstants.CreateDB.MBR_COMMIT_CODE, mbr_commit_code);			
		values.put(DatabaseConstants.CreateDB.MBR_COMMIT_NAME, mbr_commit_name);
		values.put(DatabaseConstants.CreateDB.MBR_REGION, mbr_region);
		values.put(DatabaseConstants.CreateDB.MBR_EMAIL, mbr_email);
		values.put(DatabaseConstants.CreateDB.MBR_TEL, mbr_tel);
		values.put(DatabaseConstants.CreateDB.MBR_INFO_URL, mbr_info_url);
		values.put(DatabaseConstants.CreateDB.MBR_HOME_URL, mbr_home_url);
		values.put(DatabaseConstants.CreateDB.MBR_USE_YN, mbr_use_yn);
		values.put(DatabaseConstants.CreateDB.MBR_INS_USER_UID, mbr_ins_user_uid);
		values.put(DatabaseConstants.CreateDB.MBR_INS_DATE, mbr_ins_date);
		values.put(DatabaseConstants.CreateDB.MBR_UPD_USER_UID, mbr_upd_user_uid);
		values.put(DatabaseConstants.CreateDB.MBR_UPD_DATE, mbr_upd_date);
		values.put(DatabaseConstants.CreateDB.MBR_FILE_NAME, mbr_file_name);
		values.put(DatabaseConstants.CreateDB.MBR_FILE_SAVE_NAME, mbr_file_save_name);
		values.put(DatabaseConstants.CreateDB.MBR_FILE_SRC, mbr_file_src);
		values.put(DatabaseConstants.CreateDB.FILE_SRC, file_src);
		values.put(DatabaseConstants.CreateDB.PHOTO_URL, photo_url);
		
		try{			 
			 return (mDB.insert(DatabaseConstants.CreateDB.PERSON_TABLENAME, null, values) > 0) ? true : false;
		}catch(SQLiteException e){
			String err = e.toString();
			Log.e("Sqlite Exception",  "오류가 발생하였습니다. :"+err);
			return false;
		}
		
	}
	
	
	//DB의 모든 값 가져오기 
	public Cursor getAllColumns()
	{
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper getAllColumns ");
		
		Cursor c = null;
		
		try{
			c = mDB.rawQuery("select * from mbr_person", null);
		}catch(SQLiteException e){
			String err	=	e.toString();
			Log.e("Sqlite Exception", "오류가 발생하였습니다. err : "+err);
		}	
		
		return c;
	}

	//mbr_uid(primary key)값 조회 
	public boolean getMbr_uid(String mbr_uid)
	{
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper getMbr_uid ");
		
		boolean ret = false;
		Cursor c = null;
		try{
			c = mDB.rawQuery("select count(*) from mbr_person where assem_mbr_uid=" + "'" + mbr_uid + "'", null);
			ret = true;
		}catch(SQLiteException e){
			String err	=	e.toString();
			Log.e("Sqlite Exception", "오류가 발생하였습니다. err : "+err);
		}finally{
			if(c != null)
				c.close();
		}
			
		return ret;
	}
	
	public int getID(String mbr_uid)
	{
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper getID ");
		int ret = 0;
		Cursor c = null;
		try{
			
			c = mDB.rawQuery("select _id from mbr_person where assem_mbr_uid=" + "'" + mbr_uid + "'", null);
			try{
				c.moveToFirst();
				ret = c.getInt(c.getColumnIndex(DatabaseConstants.CreateDB._ID));
			}catch(CursorIndexOutOfBoundsException e){
				String err	=	e.toString();
				Log.e("CursorIndexOutOfBoundsException", "오류가 발생하였습니다.  err : "+err);
			}
			
		}catch(SQLiteException e){
			String err	=	e.toString();
			Log.e("Sqlite Exception", "오류가 발생하였습니다. error : "+err);
		}finally{
			if(c != null)
				c.close();
		}
			
		return ret;
	}
	
	
	public Cursor getDistIMG_C(String mbr_uid)
	{
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper getDistIMG_C ");
		//DB의 모든 값 가져오기 
			Cursor c = null;
			
			try{
				c = mDB.rawQuery("select DRAWABLE_BYTE from mbr_person where assem_mbr_uid=" + "'" + mbr_uid + "'", null);
			}catch(SQLiteException e){
				String err	=	e.toString();
				Log.e("Sqlite Exception", "오류가 발생하였습니다. err : "+err);
			}	
			
			return c;
	}
	
	
	public int getDistIMG(String mbr_uid)
	{
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper getDistIMG ");
		int ret = 0;
		Cursor c = null;
		try{
			
			c = mDB.rawQuery("select DRAWABLE_BYTE from mbr_person where assem_mbr_uid=" + "'" + mbr_uid + "'", null);
			try{
				c.moveToFirst();
				ret = c.getInt(c.getColumnIndex(DatabaseConstants.CreateDB._ID));
			}catch(CursorIndexOutOfBoundsException e){
				String err	=	e.toString();
				Log.e("CursorIndexOutOfBoundsException", "오류가 발생하였습니다. error : "+err);
			}
			
		}catch(SQLiteException e){
			String err	=	e.toString();
			Log.e("Sqlite Exception", "오류가 발생하였습니다. error : "+err);
		}finally{
			if(c != null)
				c.close();
		}
			
		return ret;
	}

	
	public boolean updateDrawableByte(String mbr_uid, byte[] imageBinary){
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper updateDrawableByte ");
		int count = 0;
		ContentValues values = new ContentValues();
		
		if(null == imageBinary){
			Log.d("","DatabaseOpenHelper updateDrawableByte  = null");
		}else{
			Log.d("","DatabaseOpenHelper updateDrawableByte  = "+imageBinary.length);
		}
		
		values.put(DatabaseConstants.CreateDB.DRAWABLE_BYTE, imageBinary);
		try{
			count = mDB.update(DatabaseConstants.CreateDB.PERSON_TABLENAME, values, BaseColumns._ID+ "="+getID(mbr_uid), null);
		}catch(SQLiteException e){
			String err	=	e.toString();
			Log.e("SqliteException", "오류가 발생하였습니다. error : "+err);
		}
		
		return ( count == 0 ) ? false : true;
	}

	
	public boolean updatePhotoURL(int index, String photoURL){
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper updatePhotoURL ");
		int count = 0;
		ContentValues values = new ContentValues();
		
		
		values.put(DatabaseConstants.CreateDB.PHOTO_URL, photoURL);
		
		try{
			count = mDB.update(DatabaseConstants.CreateDB.PERSON_TABLENAME, values, BaseColumns._ID+ "="+index, null);
		}catch(SQLiteException e){
			String err	=	e.toString();
			Log.e("SqliteException", "오류가 발생하였습니다. error : "+err);
		}
		
		return ( count == 0 ) ? false : true;
	}
	
	
	public boolean updatePersonInfoAllColumn(PersonInfo info){		
		if(DEBUG)
			Log.d(TAG, " DatabaseOpenHelper updatePersonInfoAllColumn ");
		int count = 0;
		try{
			ContentValues values = new ContentValues();
			if(DEBUG){
				Log.d("","DatabaseOpenHelper updatePersonInfoAllColumn name = "+info.mbr_name);
				Log.d("","DatabaseOpenHelper updatePersonInfoAllColumn url = "+info.photo_url);
			}
			
			
			values.put(DatabaseConstants.CreateDB.MBR_NAME, info.mbr_name);
			values.put(DatabaseConstants.CreateDB.MBR_PARTY_CODE, info.mbr_party_code);
			values.put(DatabaseConstants.CreateDB.MBR_PARTY_NAME, info.mbr_party_name);
			values.put(DatabaseConstants.CreateDB.MBR_COMMIT_CODE, info.mbr_commit_code);			
			values.put(DatabaseConstants.CreateDB.MBR_COMMIT_NAME, info.mbr_commit_name);
			values.put(DatabaseConstants.CreateDB.MBR_REGION, info.mbr_region);
			values.put(DatabaseConstants.CreateDB.MBR_EMAIL, info.mbr_email);
			values.put(DatabaseConstants.CreateDB.MBR_TEL, info.mbr_tel);
			values.put(DatabaseConstants.CreateDB.MBR_INFO_URL, info.mbr_info_url);
			values.put(DatabaseConstants.CreateDB.MBR_HOME_URL, info.mbr_home_url);
			values.put(DatabaseConstants.CreateDB.MBR_USE_YN, info.mbr_use_yn);
			values.put(DatabaseConstants.CreateDB.MBR_INS_USER_UID, info.mbr_ins_user_uid);
			values.put(DatabaseConstants.CreateDB.MBR_INS_DATE, info.mbr_ins_date);
			values.put(DatabaseConstants.CreateDB.MBR_UPD_USER_UID, info.mbr_upd_user_uid);
			values.put(DatabaseConstants.CreateDB.MBR_UPD_DATE, info.mbr_upd_date);
			values.put(DatabaseConstants.CreateDB.MBR_FILE_NAME, info.mbr_file_name);
			values.put(DatabaseConstants.CreateDB.MBR_FILE_SAVE_NAME, info.mbr_file_savename);
			values.put(DatabaseConstants.CreateDB.MBR_FILE_SRC, info.mbr_file_src);
			values.put(DatabaseConstants.CreateDB.FILE_SRC, info.file_src);
			values.put(DatabaseConstants.CreateDB.PHOTO_URL, info.photo_url);
			
			count = mDB.update(DatabaseConstants.CreateDB.PERSON_TABLENAME, values, BaseColumns._ID+ "="+getID(info.assem_mbr_uid), null);
		}
		catch(SQLiteException e)
		{
			String err	=	e.toString();
			Log.e("SqliteException", "오류가 발생하였습니다.");
		}	
		
		return ( count == 0 ) ? false : true;
		
	}
	
	
	/***********************************************************************/
	//DB 초기 생성 및 업그레이드 
	private class DatabaseHelper extends SQLiteOpenHelper
	{

		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) 
		{
			super(context, name, factory, version);
		}

	

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			if(DEBUG)
				Log.d(TAG, "DataBase Create");
			
			db.execSQL(DatabaseConstants.CreateDB.PERSON_TABLE_CREATE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			if(DEBUG)
				Log.d(TAG, "DataBase Upgrade");
			
			db.execSQL("DROP TABLE mbr_person");
			onCreate(db);
			
		}
	
	}
}
