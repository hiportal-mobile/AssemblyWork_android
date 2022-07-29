package com.ex.group.aw.database;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ex.group.aw.manager.PersonManager;
import com.ex.group.aw.util.CommonUtils;
import com.ex.group.aw.util.DownloadImageTask;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.PersonInfo;

public class PersonFaceImagerManager {

	private static final boolean DEBUG = true;
	private static String LOGTAG = "PersonFaceImage";

	private Context mContext;

	/**
	 * IMAGE_DOWNLOAD_COMPLETE가 되었을때 handler를 받고 처리 함.
	 * 
	 * @param context
	 * @param pServer
	 */
	public PersonFaceImagerManager(Context context, PersonManager pServer) {
		this.mContext = context;

		//info.drawable == null 이 조건이 걸려서 문제가 발생됨. 업데이트를 하는데 데이터가 없음.
		for (PersonInfo info : pServer.getList()) {
			if (info.query_type == PersonInfo.INSERT || info.query_type == PersonInfo.UPDATE_URL || info.drawable == null) {
				if (info.photo_url != null) {
					updatePersonImageUpdateToDB(info);
				}
			}

		}
	}

	/**
	 * 다운로드 시작할때 구동 - init
	 * 
	 * @param context
	 * @param pServer
	 * @param handler
	 */
	public PersonFaceImagerManager(Context context, PersonManager pServer, Handler handler) {
		this.mContext = context;
//		for (int i = 0; i < pServer.getList().size(); i++) {
//			Log.d("","PersonFaceImagerManager ffffffffffffffffffff be1 = "+pServer.getList().get(i).mbr_name+"   "+pServer.getList().get(i).drawable);
//		}
		
		insertOrUpdate(pServer, getDBData());
//		for (int i = 0; i < pServer.getList().size(); i++) {
//			Log.d("","PersonFaceImagerManager ffffffffffffffffffff 1 = "+pServer.getList().get(i).mbr_name+"   "+pServer.getList().get(i).drawable);
//		}
		
		try {
			new DownloadImageTask(handler).execute(pServer).get();

		} catch (InterruptedException e) {
			String err = e.toString();
			if (DEBUG)
				Log.d(LOGTAG, "오류가 발생하였습니다.");

			Message msg = handler.obtainMessage();
			msg.what = Global.MSG_IMAGE_DOWNLOAD_FAIL;
			handler.sendMessage(msg);
		} catch (ExecutionException e) {
			String err = e.toString();
			if (DEBUG)
				Log.d(LOGTAG, "오류가 발생하였습니다.");

			Message msg = handler.obtainMessage();
			msg.what = Global.MSG_IMAGE_DOWNLOAD_FAIL;
			handler.sendMessage(msg);
		}

	}

	/**
	 * DB의 PersonManager 정보를 가져와 PersonManager로 리턴 하는함수
	 * 
	 * @return
	 */
	public PersonManager getDBData() {

		DatabaseOpenHelper dbOpenHelper = null;
		PersonManager retPersonManager = new PersonManager();

		try {
			dbOpenHelper = new DatabaseOpenHelper(mContext);
			dbOpenHelper.open();

			Cursor cursor = null;

			try {
				cursor = dbOpenHelper.getAllColumns();

				if (cursor != null) {
					cursor.moveToFirst();
				}

				while (!cursor.isAfterLast()) {
					PersonInfo info = new PersonInfo();

					info.assem_mbr_uid = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_UID)));
					info.mbr_name = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_NAME)));
					info.mbr_party_code = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_PARTY_CODE)));
					info.mbr_party_name = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_PARTY_NAME)));
					info.mbr_commit_code = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_COMMIT_CODE)));
					info.mbr_commit_name = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_COMMIT_NAME)));
					info.mbr_region = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_REGION)));
					info.mbr_email = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_EMAIL)));
					info.mbr_tel = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_TEL)));
					info.mbr_info_url = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_INFO_URL)));
					info.mbr_home_url = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_HOME_URL)));
					info.mbr_use_yn = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_USE_YN))).equals("Y") ? true : false;
					info.mbr_ins_user_uid = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_INS_USER_UID)));
					info.mbr_ins_date = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_INS_DATE)));
					info.mbr_upd_user_uid = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_UPD_USER_UID)));
					info.mbr_upd_date = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_UPD_DATE)));
					info.mbr_file_name = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_FILE_NAME)));
					info.mbr_file_savename = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_FILE_SAVE_NAME)));
					info.mbr_file_src = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.MBR_FILE_SRC)));
					info.file_src = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.FILE_SRC)));
					info.photo_url = checkNull( cursor.getString(cursor.getColumnIndex(DatabaseConstants.CreateDB.PHOTO_URL)));

					// byte[] drawable_byte =
					// cursor.getBlob(cursor.getColumnIndex(DatabaseConstants.CreateDB.DRAWABLE_BYTE));
					if (info.photo_url != null) {
						info.drawable = CommonUtils.ByteToDrawble(
								cursor.getBlob(cursor.getColumnIndex(DatabaseConstants.CreateDB.DRAWABLE_BYTE)));
					}
					Log.d("", "PersonFaceImageManager #################");
					Log.d("", "PersonFaceImageManager photo_url check = " + info.photo_url);
					Log.d("", "PersonFaceImageManager file_src check = " + info.file_src);
					Log.d("", "PersonFaceImageManager drawable check = " + info.drawable);

					if (DEBUG) {
						Log.d(LOGTAG, info.mbr_name + "의 정보를 DB에서 가져왔습니다. drawable:" + info.drawable);
					}

					retPersonManager.getList().add(info);
					cursor.moveToNext();
				}

			} finally {
				if (cursor != null)
					cursor.close();
			}

		} finally {
			if (dbOpenHelper != null)
				dbOpenHelper.close();
		}

		return retPersonManager;

	}

	/**
	 * pDB에 UID가 동일한 것이 있는지 확인 하는 함수
	 * 
	 * @param info
	 * @param pDB
	 * @return
	 */
	private boolean isExist(PersonInfo info, PersonManager pDB) {
		return pDB.isUidEquals(info);
	}

	/**
	 * pDB와 info 정보 비교 , 0 : pDB에 동일한 info가 있으며 내용이 모두 동일하다. 1 : pDB에 동일한 info가
	 * 있는데 Photo_URL을 제외한 내용이 틀리다. 2: pDB에 동일한 info가 있으며 photo_url이 내용만 다르다.
	 * 
	 * @param info
	 * @param pDB
	 * @return - 0: none, 1: update, 2: image update : 3
	 * 
	 */
	private int isEquals(PersonInfo info, PersonManager pDB) {
		int ret = 0;
		// 0: none
		// 1: update
		// 2: image update
		for (PersonInfo temp : pDB.getList()) {
			if (temp.assem_mbr_uid.equals(info.assem_mbr_uid)) {
				ret = temp.isEquals(info);
				break;
			}
		}

		return ret;
	}

	/**
	 * Server에서 가져온 xml을 파싱한 값과 데이터 베이스에 있는 값을 비교한다. 비교시의 기준은 Server에서 가져온 값으로
	 * 한다. 
	 * server에서 가져온 값이 DB에 없을 시 insert 
	 * server에서 가져온 값과 DB의 값 중 photo_url이 다를
	 * 경우 update_url 후 download image server에서 가져온 값과 DB의 값 중 photo_url은 같으나 다른
	 * 값이 다를 경우 update를 한다.
	 * 
	 * @param pServer
	 * @param pDB
	 */
	public void insertOrUpdate(PersonManager pServer, PersonManager pDB) {
		if (pDB.getList().size() == 0) {
			for (int i = 0; i < pServer.getList().size(); i++) {
				PersonInfo info = pServer.getList().get(i);
				info.query_type = PersonInfo.INSERT;
				pServer.getList().set(i, info);
			}

		} else {
			for (int i = 0; i < pServer.getList().size(); i++) {
				PersonInfo info = pServer.getList().get(i);
				if (isExist(info, pDB)) {
					int ret = isEquals(info, pDB);
					info.query_type = ret;

				} else {
					info.query_type = PersonInfo.INSERT;
				}

				//2018-07-19 JSJ 
				if(i <  pDB.getList().size()){
					info.drawable = pDB.getList().get(i).drawable;	
				}
				
				
				pServer.getList().set(i, info);
			}
		}

		for (int i = 0; i < pServer.getList().size(); i++) {
			PersonInfo info = pServer.getList().get(i);

			Log.d("","insertOrUpdate = "+pServer.getList().get(i).mbr_name+":"+info.query_type);
			
			switch (info.query_type) {
			case PersonInfo.INSERT:
				// insert
				insertDBFromPersonInfo(info);
				break;
			case PersonInfo.UPDATE:
				updatePersonInfo(info);
//				updatePersonImageUpdateToDB(info);
				break;
			case PersonInfo.UPDATE_URL:
				// update_url
				updatePersonInfo(info);
				break;
			case PersonInfo.NONE:
				for (PersonInfo temp : pDB.getList()) {
					if (info.isUidEquals(temp)) {
						pServer.getList().set(i, temp);
					}

				}
				break;
			}
		}

	}

	/**
	 * PersonInfo의 Photo_URL을 DB에 업데이트 하는 함수
	 * 
	 * @param info
	 * @return
	 */
	public boolean UpdatePhotoURLtoDB(PersonInfo info) {
		DatabaseOpenHelper dbOpenHelper = null;

		try {

			dbOpenHelper = new DatabaseOpenHelper(mContext);
			dbOpenHelper.open();

			if (info == null) {
				if (DEBUG)
					Log.d(LOGTAG, "personInfo is null, So PersonInfo not Update");

				return false;
			}

			int index = dbOpenHelper.getID(info.assem_mbr_uid);

			if (index != 0) {
				return dbOpenHelper.updatePhotoURL(index, info.photo_url);
			} else {
				if (DEBUG)
					Log.d(LOGTAG, info.mbr_name + " record is not avaliable, So not update, insert please");

				return false;
			}

		} finally {
			if (dbOpenHelper != null)
				dbOpenHelper.close();
		}

	}

	/**
	 * PersonInfo 를 DB에 insert 하는 함수
	 * 
	 * @param info
	 */
	public boolean insertDBFromPersonInfo(PersonInfo info) {

		DatabaseOpenHelper dbOpenHelper = null;
		boolean ret = false;

		try {
			dbOpenHelper = new DatabaseOpenHelper(mContext);
			dbOpenHelper.open();

			ret = dbOpenHelper.insertColumn(info.assem_mbr_uid, info.mbr_name, info.mbr_party_code, info.mbr_party_name,
					info.mbr_commit_code, info.mbr_commit_name, info.mbr_region, info.mbr_email, info.mbr_tel,
					info.mbr_info_url, info.mbr_home_url, info.mbr_use_yn ? "Y" : "N", info.mbr_ins_user_uid,
					info.mbr_ins_date, info.mbr_upd_user_uid, info.mbr_upd_date, info.mbr_file_name,
					info.mbr_file_savename, info.mbr_file_src, info.file_src, info.photo_url, info.drawable);

			if (ret) {
				if (DEBUG)
					Log.d(LOGTAG, info.mbr_name + " DB insert Success");
			} else {
				if (DEBUG)
					Log.d(LOGTAG, info.mbr_name + " DB insert Fail");
			}
		} finally {
			if (dbOpenHelper != null)
				dbOpenHelper.close();
		}

		return ret;

	}

	/**
	 * 다운로드된 이미지를 DB에 업데이트
	 * 
	 * @param manager
	 */
	public void updatePersonImageUpdateToDB(PersonInfo info) {
		Log.i(LOGTAG, "updatePersonImageUpdateToDB "+info.mbr_name +":"+info.drawable);
		
		DatabaseOpenHelper dbOpenHelper = null;

		try {

			dbOpenHelper = new DatabaseOpenHelper(mContext);
			dbOpenHelper.open();

			if (info.drawable != null) {
				if (dbOpenHelper.getMbr_uid(info.assem_mbr_uid)) {
					if (dbOpenHelper.updateDrawableByte(info.assem_mbr_uid, CommonUtils.DrawbleToByte(info.drawable))) {
						Log.i(LOGTAG, info.mbr_name + " image update success");
					} else {
						Log.i(LOGTAG, info.mbr_name + " image update failed");
					}
				}
			}

		} finally {
			if (dbOpenHelper != null)
				dbOpenHelper.close();
		}

	}

	/**
	 * 
	 * pDB의 PersonInfo와 pServer의 PersonInfo의 Uid가 같은 경우 데이터를 Database에
	 * pServer값으로 업데이트
	 * 
	 * @param info
	 * @return true
	 */
	public boolean updatePersonInfo(PersonInfo info) {
		DatabaseOpenHelper DBHelper = null;
		boolean ret = true;

		try {
			DBHelper = new DatabaseOpenHelper(mContext);
			DBHelper.open();

			ret = DBHelper.updatePersonInfoAllColumn(info);

		} finally {
			if (DBHelper != null)
				DBHelper.close();
		}

		return ret;
	}

	/**
	 * PersonManager 서버의 totalCount 와 Count를 가져오는 함수
	 * 
	 * @param personManager
	 * @param personManagerServer
	 * @return
	 */
	public PersonManager setCountFromServer(PersonManager pDB, PersonManager pServer) {

		pDB.count = pServer.count;
		pDB.total_count = pServer.total_count;

		return pDB;
	}

	/**
	 * Null 체크 함수
	 * 
	 * @param str
	 * @return
	 */
	private String checkNull(String str) {
		if (str == null) {
			return "";
		}

		return str;
	}

}
