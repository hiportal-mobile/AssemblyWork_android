package com.ex.group.aw.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class CommonUtils
{
	public static final String LOGTAG = "CommonUtils";
	
	public static void CopyStream(InputStream is, OutputStream os)
	{
		final int buffer_size=1024;
		try
		{
			byte[] bytes=new byte[buffer_size];
			for(;;)
			{
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){
			String err	=	ex.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
	}
   
	public static Drawable loadDrawable(String urlStr, String name)
	{
		Drawable drawable = null; 
		try{
			urlStr = "http://" + urlStr;
			InputStream is = (InputStream) new URL(urlStr).getContent(); 
			drawable = Drawable.createFromStream(is, name); 
		}catch(Exception e){ 
			String err	=	e.toString();
			Log.e(LOGTAG, "Exception is " + "오류가 발생하였습니다. err : "+err);
		}
		return drawable; 
	} 
	
	/**
	 * Bitmap을 Byte로 변환하여 리턴
	 * @param bitmap
	 * @return
	 */
	public static byte[] BitmapToByte(Bitmap bitmap)
	{			
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] bitmapdata = stream.toByteArray();
        
        return bitmapdata;		
		
	}
	
	/**
	 * Byte를 Drawable로 변환하여 리턴
	 * @param ImageByte
	 * @return
	 */
	public static Drawable ByteToDrawble(byte[] ImageByte){
		if(ImageByte != null){
			Bitmap bitmap = BitmapFactory.decodeByteArray( ImageByte, 0, ImageByte.length );  
    		
			Drawable drawable = new BitmapDrawable(bitmap);
			return drawable;
		}
		return null;
		
	}
	
	/**
	 * Drawable를 Byte로 변환하여 리턴
	 * @param drawable
	 * @return
	 */
	public static byte[] DrawbleToByte(Drawable drawable)
	{	
		Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] bitmapdata = stream.toByteArray();
		return bitmapdata;
	}

	//이미지 다운로드 
	/**
	public static Bitmap getRemoteImage(String imgPath, String fileName) {
		
		Bitmap rtnBitmap	= null;
		
		Log.e(LOGTAG, "imgPath = "+imgPath);

		String apiUrl		= "http://128.200.121.68:9000/emp_ex/downloadProxy.pe";
//		String apiUrl		= "http://172.6.16.38:8088/emp_sf/upload.pe";
		String type			= "http";
		String inlineId		=  imgPath;
		String mdn			= "";	//MainMenuActivity.mUserTel;
		String encPwd		= "";
		String attachName	= fileName;
		
		String isResize		= "N";
		String width		= "512";
		String size			= "1000";
		
		try {
			
			StringBuffer sb = new StringBuffer();
			
			sb.append(apiUrl);
			sb.append("?type=" 		+ type);
			sb.append("&inlineId="	+ inlineId);
			sb.append("&mdn="		+ mdn);
			sb.append("&encPwd="	+ encPwd);
			sb.append("&attachName="+ attachName);
			sb.append("&isResize="	+ isResize);
			sb.append("&width="		+ width);
			sb.append("&size="		+ size);
			
			
			Log.d(LOGTAG, sb.toString());
			URL url = new URL(sb.toString());
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedInputStream bis = new BufferedInputStream(
							conn.getInputStream());
					BitmapFactory.Options optn	=	new BitmapFactory.Options();
					optn.inSampleSize = 1;
					rtnBitmap = BitmapFactory.decodeStream(bis, null	, optn);
					bis.close();
				}
				conn.disconnect();
			}
		} catch (IOException e) {
			String err	=	e.toString();
			//e.printStackTrace();
			Log.e(LOGTAG, "오류가 발생하였습니다. err : "+err);
		}

		return rtnBitmap;
	}	
	**/
	
	//이미지 다운로드 
	public static Bitmap getRemoteImage(String imgPath, String fileName) {
		
		Bitmap rtnBitmap	= null;
		
		String apiUrl		= "http://128.200.121.68:9000/emp_ex/downloadProxy.pe";
//		String apiUrl		= "http://172.6.16.38:8088/emp_sf/upload.pe";
		String type			= "http";
		String inlineId		=  imgPath;
		String mdn			= "";	//MainMenuActivity.mUserTel;
		String encPwd		= "";
		String attachName	= fileName;
		
		String isResize		= "N";
		String width		= "512";
		String size			= "1000";
		
		try {
			
			StringBuffer sb = new StringBuffer();
			
			//2018.05.14.
			//국회시스템 연계불가로 로컬 이미지 및 데이터 수신 처리
			//sb.append(apiUrl);
			sb.append("http://"+imgPath);
			/*sb.append("?type=" 		+ type);
			sb.append("&inlineId="	+ inlineId);
			sb.append("&mdn="		+ mdn);
			sb.append("&encPwd="	+ encPwd);
			sb.append("&attachName="+ attachName);
			sb.append("&isResize="	+ isResize);
			sb.append("&width="		+ width);
			sb.append("&size="		+ size);*/
			
			Log.d(LOGTAG, sb.toString());
			URL url = new URL(sb.toString());
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if (conn != null) {
				conn.setConnectTimeout(10000);
				conn.setUseCaches(false);
				if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedInputStream bis = new BufferedInputStream(
							conn.getInputStream());
					BitmapFactory.Options optn	=	new BitmapFactory.Options();
					optn.inSampleSize = 1;
					rtnBitmap = BitmapFactory.decodeStream(bis, null	, optn);
					bis.close();
				}
				conn.disconnect();
			}
		} catch (IOException e) {
			String err	=	e.toString();
			//e.printStackTrace();
			Log.e(LOGTAG, "오류가 발생하였습니다. err : "+err);
		}

		return rtnBitmap;
	}
	
	//이미지 다운로드 
		public static Bitmap getRemoteImageQna(String imgPath, String fileName) {
			
			Bitmap rtnBitmap	= null;
			
			Log.e(LOGTAG, "imgPath = "+imgPath);

			String apiUrl		= "http://128.200.121.68:9000/emp_ex/downloadProxy.pe";
//			String apiUrl		= "http://172.6.16.38:8088/emp_sf/upload.pe";
			String type			= "http";
			String inlineId		=  imgPath;
			String mdn			= "";	//MainMenuActivity.mUserTel;
			String encPwd		= "";
			String attachName	= fileName;
			
			String isResize		= "Y";
			String width		= "512";
			String size			= "1000";
			
			try {
				
				StringBuffer sb = new StringBuffer();
				
				sb.append(apiUrl);
				sb.append("?type=" 		+ type);
				sb.append("&inlineId="	+ inlineId);
				sb.append("&mdn="		+ mdn);
				sb.append("&encPwd="	+ encPwd);
				sb.append("&attachName="+ attachName);
				sb.append("&isResize="	+ isResize);
				sb.append("&width="		+ width);
				sb.append("&size="		+ size);
				
				
				Log.d(LOGTAG, sb.toString());
				URL url = new URL(sb.toString());
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				if (conn != null) {
					conn.setConnectTimeout(10000);
					conn.setUseCaches(false);
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						BufferedInputStream bis = new BufferedInputStream(
								conn.getInputStream());
						BitmapFactory.Options optn	=	new BitmapFactory.Options();
						optn.inSampleSize = 1;
						rtnBitmap = BitmapFactory.decodeStream(bis, null	, optn);
						bis.close();
					}
					conn.disconnect();
				}
			} catch (IOException e) {
				String err	=	e.toString();
				//e.printStackTrace();
				Log.e(LOGTAG, "오류가 발생하였습니다. err : "+err);
			}

			return rtnBitmap;
		}
		
	
	
}
