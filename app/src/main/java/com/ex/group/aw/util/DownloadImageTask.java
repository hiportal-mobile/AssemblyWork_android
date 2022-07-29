package com.ex.group.aw.util;

import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ex.group.aw.manager.PersonManager;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.PersonInfo;

public class DownloadImageTask extends AsyncTask<PersonManager, Void, Void>
{
	private final static boolean DEBUG = false;
	private final static String LOGTAG = "DownloadImageTask";	
	private Handler mHandler = null;



	public DownloadImageTask(Handler handler)
	{	
		this.mHandler = handler;	

	}

	@Override
	protected void onPreExecute() {
		if(DEBUG)
			Log.d(LOGTAG, "Image DownLoad START");
	}


	@Override
	protected Void doInBackground(PersonManager... pServer) {

		Bitmap bitmap = null;
		String temp = "";


		for(int i=0; i < pServer[0].getList().size(); i++)
		{
			PersonInfo info = pServer[0].getList().get(i);

			if ( info.query_type == PersonInfo.INSERT
					|| info.query_type == PersonInfo.UPDATE_URL
					/*|| info.drawable == null*/)
			{


				if(info.photo_url != null)
				{
					if( info.photo_url.contains("http://"))
					{
						temp = info.photo_url;
					}
					else
					{
						temp = "http://" + info.photo_url.trim();
					}		
					
					Log.d("","DownloadImageTask java = ##################");
					Log.d("","DownloadImageTask java = "+info.mbr_name);
					Log.d("","DownloadImageTask java = " + temp);
					Log.d("","DownloadImageTask ecoFilename = " + info.mbr_file_name);
					Log.d("","DownloadImageTask photo_url = " + info.photo_url);

					String encoTemp	=	URLEncoder.encode(temp);
					String encoFilename=info.mbr_file_name;

					bitmap = CommonUtils.getRemoteImage(info.photo_url,encoFilename);

					if(bitmap != null)
					{
						if(DEBUG)
							Log.d(LOGTAG, info.mbr_name+" is Face Image DownLoad Complete");



						byte[] imageBinary = CommonUtils.BitmapToByte(bitmap);							
						info.drawable = CommonUtils.ByteToDrawble(imageBinary);	



						pServer[0].getList().set(i, info);
					}						

				}
			}			
		}
		return null;
	}


	@Override
	protected void onPostExecute(Void result) {
		if(DEBUG)
			Log.d(LOGTAG, "Image DownLoad End ");

		Message msg = mHandler.obtainMessage();
		msg.what = Global.MSG_IMAGE_DOWNLOAD_COMPLETE;

		mHandler.sendMessage(msg);
	}

}
