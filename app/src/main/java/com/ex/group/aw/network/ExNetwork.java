package com.ex.group.aw.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

import com.ex.group.aw.vo.UserData;

public class ExNetwork
{
	private static final String LOGTAG = "EtNetwork";
	
	private static final String BOUNDARY = "*****";
	private static final String ENDLINE = "\r\n";
	private static final String HYPHENS = "--";
	
	private static final String CS_UTF_8 = "UTF-8";
	private static final String CS_EUC_KR = "EUC-KR";
	
	public String rcvString = "N/A";
	public StringBuffer rcvBuffer;
	public ArrayList<SendQueue> sndBuffer;
	private String charset;
	public ExNetwork(){}
	
	public ExNetwork(ArrayList<SendQueue> _sndBuffer)
	{
		this.sndBuffer = _sndBuffer;
		
		rcvBuffer = new StringBuffer(5000);
		rcvString = "";
		charset = CS_UTF_8;
	}
	
	public void SendData()
	{
		try{
			URL url = new URL(sndBuffer.get(0).value);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			
			if( http == null )
			{
				Log.i(LOGTAG, "httpURLConnection is null");
				return;
			}
			
			//	set send mode
			http.setConnectTimeout(5000);  // 5초
			http.setReadTimeout(10000) ;  // 10초
			http.setDefaultUseCaches(false);                                            
			http.setDoInput(true);            // 서버에서 읽기 모드 지정 
			http.setDoOutput(true);           // 서버로 쓰기 모드 지정  
			http.setRequestMethod("POST");    // 전송 방식은 POST 
			
			// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다 
			http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
			
			//	send to server
			StringBuffer buffer = new StringBuffer();
			for( int i=1 ; i<sndBuffer.size() ; i++ )
			{
				buffer.append(sndBuffer.get(i).key).append('=');
				buffer.append(sndBuffer.get(i).value).append('&');
				
				Log.i(LOGTAG, "sndBuffer.get(i).key="+sndBuffer.get(i).key);
				Log.i(LOGTAG, "sndBuffer.get(i).value="+sndBuffer.get(i).value);
			}

			//서비스 요청 시 파라미터 추가
			//StartActivity 관련 파라미터 인스턴스 항목 추가
			//전화번호
			buffer.append("mdn=");
			buffer.append(UserData.getInstance().PHONE_NO + "&");
			//앱 ID
			buffer.append("appId=");
			buffer.append(UserData.getInstance().APP_ID + "&");
			//앱 버전
			buffer.append("appVer=");
			buffer.append(UserData.getInstance().APP_VER + "&");

			//서버 Validation Check 제외
			//하드코딩 가능 필드
			//AuthKey, nonce, encPwd(서버 공통 예외 적용)
			buffer.append("authKey=");
			buffer.append("c068e13c3f04ddf1acaa020f88504edd767bc3c0&");
			buffer.append("nonce=");
			buffer.append("20130901014410927&");
			buffer.append("encPwd=");
			buffer.append("YSE1XSIvMSYlYSXxLlPzLSJg&");
			//고정값
			buffer.append("companyCd=");
			buffer.append("EX&");
			buffer.append("groupCd=");
			buffer.append("EX&");
			buffer.append("infoCompanyCd=");
			buffer.append("EX&");			
			
			
			OutputStream os = http.getOutputStream();
			if( os == null )
			{
				Log.i(LOGTAG, "OutputStream is null");
				return;
			}
			
			OutputStreamWriter osw = new OutputStreamWriter(os, CS_UTF_8);
			PrintWriter writer = new PrintWriter(osw);
			writer.write(buffer.toString());
			writer.flush();
			Log.i(LOGTAG, "buffer.toString()="+buffer.toString());
			//	receive from server
			InputStream is = http.getInputStream();
			if( is == null )
			{
				Log.i(LOGTAG, "InputStream is null");
				return;
			}
			InputStreamReader isr = new InputStreamReader(is, CS_UTF_8);
			BufferedReader reader = new BufferedReader(isr);
			
			String str;
			while((str = reader.readLine()) != null )
			{
				rcvBuffer.append(str + "\n");
			}
			rcvString = rcvBuffer.toString().trim();
		}catch(MalformedURLException e){
			String err	=	e.toString();
			Log.e(LOGTAG, "MalformedURLException is " + "오류가 발생하였습니다.");
			rcvString = "N/A";
		}catch(IOException e){
			String err	=	e.toString();
			Log.e(LOGTAG, "IOException is " + "오류가 발생하였습니다.\n"+err);
			rcvString = "N/A";
		}catch(Exception e){
			String err	=	e.toString();
			Log.e(LOGTAG, "Excetion is " +"오류가 발생하였습니다.");
			rcvString = "N/A";
		}
//		finally{
//			Log.e(LOGTAG, "Excetion is " +"오류가 발생하였습니다.");
//		}
	}
	
	public void fileUpload(HashMap<String, Object> files)
	{
		try{
			
			URL url = new URL(sndBuffer.get(0).value);
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			
			if( http == null )
			{
				Log.i(LOGTAG, "httpURLConnection is null");
				return;
			}
			
			//	set send mode
			http.setConnectTimeout(5000);  // 5초
			http.setReadTimeout(10000) ;  // 10초
			http.setDefaultUseCaches(false);                                            
			http.setDoInput(true);            // 서버에서 읽기 모드 지정 
			http.setDoOutput(true);           // 서버로 쓰기 모드 지정
			http.setUseCaches(false);
			http.setRequestMethod("POST");    // 전송 방식은 POST
			http.setRequestProperty("Connection", "Keep-Alive");
			http.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			
			DataOutputStream dos = new DataOutputStream(http.getOutputStream());
			
			/**	parameter setting	*/
			
			FileInputStream fis = null;
			/**	file setting	*/
			for( Iterator<String> i = files.keySet().iterator() ; i.hasNext() ; )
			{
				String key =  i.next();
				String fileNm = String.valueOf(files.get(key));
				
				fis = new FileInputStream(fileNm);
				
				
				StringBuffer sb = new StringBuffer();  
				sb.append(HYPHENS + BOUNDARY + ENDLINE);
				sb.append("Content-Disposition: form-datal; name=\"" + key + "\";filename=\"" + fileNm +"\"" + ENDLINE);
				sb.append(ENDLINE);
				dos.writeUTF(sb.toString());
				
				int bytesAvailable = fis.available();
				int maxSize = 1024;
				int bufferSize = Math.min(bytesAvailable, maxSize);
				
				byte[] buffer = new byte[bufferSize];
				int bytesRead = fis.read(buffer, 0, bufferSize);
				
				while(bytesRead > 0 )
				{
					dos.write(buffer);
					bytesAvailable = fis.available();
					bufferSize = Math.min(bytesAvailable, maxSize);
					bytesRead = fis.read(buffer, 0, bufferSize);
				}
				
				dos.writeBytes(ENDLINE + HYPHENS + BOUNDARY + HYPHENS + ENDLINE);
				
				fis.close();
				dos.flush();
			}
			
			InputStream is = http.getInputStream();
			if( is == null )
			{
				Log.i(LOGTAG, "InputStream is null");
				return;
			}
			InputStreamReader isr = new InputStreamReader(is, CS_UTF_8);
			BufferedReader reader = new BufferedReader(isr);
			
			String str;
			while((str = reader.readLine()) != null )
			{
				rcvBuffer.append(str + "\n");
			}
			rcvString = rcvBuffer.toString().trim();
		}catch(MalformedURLException e){
			String err	=	e.toString();
			Log.i(LOGTAG, "MalformedURLException is " +"오류가 발생하였습니다.");
			rcvString = "N/A";
		}catch(IOException e){
			String err	=	e.toString();
			Log.i(LOGTAG, "IOException is " + "오류가 발생하였습니다.");
			rcvString = "N/A";
		}catch(Exception e){
			String err	=	e.toString();
			Log.i(LOGTAG, "Excetion is " + "오류가 발생하였습니다.");
			rcvString = "N/A";
		}
//		finally{
//			Log.i(LOGTAG, rcvString);
//		}	
	}
	
}