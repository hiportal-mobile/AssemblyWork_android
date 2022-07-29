package com.skt.pe.common.service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import com.skt.pe.common.activity.SKTActivity;
import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.Environ;
import com.skt.pe.common.conf.EnvironManager;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;


/**
 * 콘트롤러 클래스
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class Controller {

	public static final String GET            = "GET";
	public static final String POST           = "POST";

	public static final String ATT_ENCODING_START = "encoding=\"";
	public static final String ATT_ENCODING_END   = "\"";

	public static final String PARAM_AUTH_KEY   = "authKey";
	public static final String PARAM_COMPANY_CD = "companyCd";
	public static final String PARAM_ENC_PWD    = "encPwd";

	public static int   CONN_TIMEOUT_MULTIPART   = 60;
	public static int   CONN2_TIMEOUT_MULTIPART  = 10;
	public static int   READ_TIMEOUT_MULTIPART   = 60;
	
	public static int   CONN_TIMEOUT   = 15;
	public static int   CONN2_TIMEOUT  = 10;
	public static int   READ_TIMEOUT   = 15;
	public static int   RETRY          = 3;

	private Context context  = null;
	public static  boolean ROAMING = false; 

	static {
		try {
			trustHttpsCertificates();
		} catch(Exception e) { }
	}

	/**
	 * 연결 타임아웃을 설정한다
	 * @param connTimeout 시간
	 */
	public static void setConnTimeout(int connTimeout) {
		if(connTimeout < 1)
			return;
		CONN_TIMEOUT = connTimeout;
	}

	/**
	 * 읽기 타임아웃을 설정한다
	 * @param readTimeout 시간
	 */
	public static void setReadTimeout(int readTimeout) {
		if(readTimeout < 1)
			return;
		READ_TIMEOUT = readTimeout;
	}
	
	/**
	 * 재시도 횟수를 지정한다
	 * @param retry 재시도 횟수
	 */
	public static void setRetry(int retry) {
		if(retry < 1)
			return;
		RETRY = retry;
	}

	public Controller(Context context) {
		this.context = context;
		
		try {
			trustHttpsCertificates();
		} catch(Exception e) { }
	}

	/**
	 * 서비스 URL을 얻는다.
	 * @param params 파라미터
	 * @return 서비스 URL
	 * @throws SKTException
	 */
	public String getUri(Parameters params) throws SKTException {
		return getUri(params, Environ.FILE_SERVICE);
	}

	/**
	 * 서비스 URL을 얻는다.
	 * @param params 파라미터
	 * @param serviceFile 서비스파일명
	 * @return 서비스 URL
	 * @throws SKTException
	 */
	public String getUri(Parameters params, String serviceFile) throws SKTException {
		if(params == null) {
			throw new SKTException("Parameter is NULL!");
		} else if(params.getPrimitive()==null || params.getPrimitive().trim().length()==0) {
			throw new SKTException("Primitive is NULL!");
		}

		EnvironManager.setupEnviron(context);

    	Environ environ = EnvironManager.getEnviron(context);
    	
    	StringBuffer body = new StringBuffer();
    	
		if(!Environ.isAuthService(serviceFile)) {
			if(EnvironManager.PRODUCT_MODE) {
//    			try {
//    				if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {
//    					throw new SKTException("Auth must be installed!", Constants.Status.E_AUTH_NOT);
//    				}
//    			} catch(SKTException e) {
//    				throw e;
//    			}	
			}
			
			Map<String,String> gmpAuth = null; 
			if(!EnvironManager.getNeedEncPwd()) {
				gmpAuth = SKTUtil.getGMPAuth(context);
				
				body.append(PARAM_AUTH_KEY + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_AUTH_KEY)) + "&");
				body.append(PARAM_COMPANY_CD + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_COMPANY_CD)) + "&");
				//CJ 수정사항
//				body.append(Environ.PARAM_MDN + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ID)) + "&");
			} else {
				gmpAuth = SKTUtil.getGMPAuthPwd(context);
				
				body.append(PARAM_AUTH_KEY + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_AUTH_KEY)) + "&");
				body.append(PARAM_COMPANY_CD + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_COMPANY_CD)) + "&");
				body.append(PARAM_ENC_PWD + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ENC_PWD)) + "&");
				//CJ 수정사항
//				body.append(Environ.PARAM_MDN + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ID)) + "&");

				SKTUtil.log(Log.DEBUG, "Controller", "ENC_PWD: " + "[" + gmpAuth.get(AuthData.ID_ENC_PWD) + "]=[" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ENC_PWD)) + "]");
			}

			params.remove(Environ.PARAM_LANG);
			params.remove(Environ.PARAM_GROUP_CD);
			params.remove(PARAM_COMPANY_CD);
			params.remove(PARAM_ENC_PWD);
		}

		body.append(environ.getEnvironParam() + "&");
    	body.append(params.toString(false));

    	return environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort() + serviceFile + "?" + body.toString();
	}

	/**
	 * 서비스를 리퀘스트한다
	 * @param params 파라미터
	 * @return XML데이타
	 * @throws SKTException
	 */
	public XMLData request(Parameters params) throws SKTException {
		return request(params, false);
	}

	/**
	 * 서비스를 리퀘스트한다.
	 * @param params 파라미터
	 * @param postFlag post여부
	 * @return XML데이타
	 * @throws SKTException
	 */
	public XMLData request(Parameters params, boolean postFlag) throws SKTException {
		return request(params, postFlag, Environ.FILE_SERVICE);
	}

	/**
	 * 서비스를 리퀘스트한다
	 * @param params 파라미터
	 * @param postFlag post여부
	 * @param serviceFile 서비스파일명
	 * @return XML데이타
	 * @throws SKTException
	 */
	public XMLData request(Parameters params, boolean postFlag, String serviceFile) throws SKTException {

		long a = System.currentTimeMillis();
		long t = a;

		/*
		 * SSLVPN 접속체크 -- 한국도로공사 추가
		 * by pluto248 2011.10.24
		 */
//		SSLVPNController.checkConnectedSslVpn(this.context);

		if(params == null) {
			throw new SKTException("Parameter is NULL!");
		} else if(params.getPrimitive()==null || params.getPrimitive().trim().length()==0) {
			throw new SKTException("Primitive is NULL!");
		}

		if(context instanceof SKTActivity) {
			SKTActivity sktActivity = (SKTActivity)context;
			sktActivity.setParameters(params);
			sktActivity.setPostFlag(postFlag);
			sktActivity.setServiceFile(serviceFile);
		}

		NetworkInfo wifi   = SKTUtil.getWifiNetwork(context);
		NetworkInfo mobile = SKTUtil.getMobileNetwork(context);

		if(!wifi.isAvailable() && !mobile.isAvailable()) {
			throw new SKTException("Network-Setting is invalid! [available:" + wifi.isAvailable() + "/" + mobile.isAvailable() + "]", Constants.Status.E_NETWORK);
		}

		if(!wifi.isConnectedOrConnecting() && !mobile.isConnectedOrConnecting()) {
			throw new SKTException("Network isn't connected! [connected:" + wifi.isConnected() + "," + wifi.isConnectedOrConnecting() + "/" +
					mobile.isConnected() + "," + mobile.isConnectedOrConnecting() + "]", Constants.Status.E_NETWORK);
		}

		if(!ROAMING && !wifi.isRoaming() && mobile.isRoaming() && mobile.isConnectedOrConnecting()) {
			ROAMING = true;
			throw new SKTException("Roaming not allow When 3G! [roaming:" + wifi.isRoaming() + wifi.isConnected() + "," + wifi.isConnectedOrConnecting() + "/" +
					mobile.isRoaming() + mobile.isConnected() + "," + mobile.isConnectedOrConnecting() + "]", Constants.Status.E_ROAMING);
		}

		EnvironManager.setupEnviron(context);

        HttpURLConnection 	conn    = null;
        XMLData 			xmlData = null;

        try {
        	Environ environ = EnvironManager.getEnviron(context);

        	StringBuffer body = new StringBuffer();

    		if(!Environ.isAuthService(serviceFile)) {
    			if(EnvironManager.PRODUCT_MODE) {
        			/*try {
        				if(SKTUtil.isInstallPackage(context, Constants.CoreComponent.APP_ID_AUTH) == null) {

        					throw new SKTException("Auth must be installed!", Constants.Status.E_AUTH_NOT);
        				}
        			} catch(SKTException e) {
        				throw e;
        			}	*/
    			}

    			Map<String,String> gmpAuth = null;
				if(!EnvironManager.getNeedEncPwd()) {
					gmpAuth = SKTUtil.getGMPAuth(context);

    				body.append(PARAM_AUTH_KEY + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_AUTH_KEY)) + "&");
    				body.append(PARAM_COMPANY_CD + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_COMPANY_CD)) + "&");
    				//CJ 수정사항
//    				body.append(Environ.PARAM_MDN + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ID)) + "&");
				} else {
					gmpAuth = SKTUtil.getGMPAuthPwd(context);

    				body.append(PARAM_AUTH_KEY + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_AUTH_KEY)) + "&");
    				body.append(PARAM_COMPANY_CD + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_COMPANY_CD)) + "&");
					body.append(PARAM_ENC_PWD + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ENC_PWD)) + "&");
					//CJ 수정사항
//					body.append(Environ.PARAM_MDN + "=" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ID)) + "&");

					SKTUtil.log(Log.DEBUG, "Controller", "ENC_PWD: " + "[" + gmpAuth.get(AuthData.ID_ENC_PWD) + "]=[" + URLEncoder.encode(gmpAuth.get(AuthData.ID_ENC_PWD)) + "]");
				}

				params.remove(Environ.PARAM_LANG);
				params.remove(Environ.PARAM_GROUP_CD);
    			params.remove(PARAM_COMPANY_CD);
    			params.remove(PARAM_ENC_PWD);
    		}

    		body.append(environ.getEnvironParam() + "&");
        	body.append(params.toString(postFlag));

        	// SKTUtil.log가 어떻게 동작하는 지 몰라 일단 이렇게 체인지
        	// by pluto248 2011-08-31
//        	SKTUtil.log(android.util.Log.DEBUG, "Controller : ResponseHeader", key + "=" + sb.toString().substring(1));
//        	if (EnvironManager.LOG_MODE == true)
//        		Log.d("TEST", "Controller : Request Parameter : " + environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort() + serviceFile + "?" + body.toString());

        	URL url = null;
        	if(!postFlag) {
        		String b_url = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort() + serviceFile + "?" + body.toString();
        		url = new URL(b_url);
        	} else {
        		url = new URL(environ.getProtocol(), environ.getHost(), environ.getPort(), serviceFile);
        	}

        	Log.i("Controller", "URL: " + url.toString());
        	Log.i("Controller", "Body: " + body.toString());

        	int retry = 0;

        	OutputStream          os   = null;
            InputStream           is   = null;
            ByteArrayOutputStream baos = null;

        	while(retry++ < RETRY) {
        		t = SKTUtil.timestamp(context, t, params.getPrimitive(), "N/W-REQ STT (" + retry + ")");

        		try {
					conn = (HttpURLConnection)url.openConnection();
					if(retry == 1) {
						conn.setConnectTimeout(CONN_TIMEOUT * 1000);
					} else {
						conn.setConnectTimeout(CONN2_TIMEOUT * 1000);
					}
    	        	conn.setReadTimeout(READ_TIMEOUT * 1000);
    	        	conn.setRequestMethod(postFlag ? POST : GET);
    	        	//TODO RequestProperty(Host, Content-Length / Refer, Accept-Language, Content-Type ...)
    	        	conn.setRequestProperty("Cache-Control","no-cache");
    	        	conn.setDoOutput(true);
    	        	conn.setDoInput(true);

    	        	if(postFlag) {
    	        		os = conn.getOutputStream();
    	        		os.write(body.toString().getBytes());
    	        		os.flush();

    	        		SKTUtil.log(Log.DEBUG, "Controller", "BODY: " + body.toString());
    	        	}

    	        	int responseCode = conn.getResponseCode();
    	        	SKTUtil.log(Log.DEBUG, "Controller", "ResponseCode: " + responseCode+"");

    	        	if(responseCode == HttpURLConnection.HTTP_OK) {

    	        		is = conn.getInputStream();
    	                baos = new ByteArrayOutputStream();
    	                byte[] byteBuffer = new byte[1024];
    	                byte[] byteData = null;
    	                int nLength = 0;
    	                while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
    	                    baos.write(byteBuffer, 0, nLength);
    	                }
    	                byteData = baos.toByteArray();
    	    		    //String response = new String(byteData, "euc-kr");
    	                String response = new String(byteData);

    	    	        if(response==null || response.equals("")) {
    	    	        	throw new SKTException("Response is NULL!", Constants.Status.E_RESPONSE);
    	    	        }

    	    	        t = SKTUtil.timestamp(context, t, params.getPrimitive(), "N/W-REQ END");

    	    	        Map<String,List<String>> headers = conn.getHeaderFields();
    	    	        Iterator<String> it = headers.keySet().iterator();
    	    	        while(it.hasNext()) {
    	    	        	String key = it.next();
    	    	        	List<String> values = headers.get(key);
    	    	        	StringBuffer sb = new StringBuffer();
    	    	        	for(int i=0; i<values.size(); i++) {
    	    	        		sb.append(";" + values.get(i));
    	    	        	}
    	    	        	SKTUtil.log(Log.DEBUG, "Controller", "ResponseHeader: " + key + "=" + sb.toString().substring(1));
    	    	        }

  	    	        	SKTUtil.log(Log.DEBUG, "Controller", "Response: " + response);
  	    	        	Log.d("Controller", "Response: " + response);

    	    	        t = SKTUtil.timestamp(context, t, params.getPrimitive(), "XML-PRS STT");

    	                xmlData = new XMLData(response);
    	                String ret = xmlData.get(Constants.TAG_RESULT);
    	                if(!Constants.Status.S_SUCCESS.equals(ret)) {
    	                	if(ret == null) {
    	                    	throw new SKTException("RESULT is null!");
    	                	} else {
    	                		String retMsg = xmlData.get(Constants.TAG_RESULT_MESSAGE);
    	                		throw new SKTException(retMsg, ret);
    	                	}
    	                }

    	                t = SKTUtil.timestamp(context, t, params.getPrimitive(), "XML-PRS END");

    	                if(!Environ.isAuthService(serviceFile)) {
    	                	SKTUtil.updateNonce(context, xmlData.get(Constants.TAG_NEXT_NONCE));
    	                }
    	        	} else {
    	        		if(responseCode == -1) {
    	        			if(retry < RETRY) {
    	        				Log.i("Controller", "RESPONSE-CODE : -1 (" + retry + ")");
    	        				continue;
    	        			}
    	        		}

    	        		throw new SKTException("Network Error![RESPONSE_CODE=" + responseCode + "]", Constants.Status.E_RESPONSE);
    	        	}
        		} catch(SKTException e) {
                	throw e;
                } catch(UnknownHostException e) {
                	throw e;
            	} catch(SocketTimeoutException e) {
            		if(isConnectionTimeout(e)) {
            			if(retry < RETRY) {
                			Log.i("Controller", "CONNECTION-TIMEOUT (" + retry + ")");
                			continue;
                		}
            		}

            		/*
        			 * SSLVPN 접속체크 -- 한국도로공사 추가
        			 * by pluto248 2011.10.24
        			 */
//        			SSLVPNController.checkConnectedSslVpn(this.context);
                	throw e;
                } catch (Exception e) {
                	throw e;
                } finally {
                	SKTUtil.log(Log.DEBUG, "Controller", "FINALLY");

                	if(os != null) {
                		try {
                			os.flush();
                			os.close();
                		} catch(Exception e) { }
                	}

                	if(is != null) {
                		try {
                			is.close();
                		} catch(Exception e) { }
                	}

                	if(baos != null) {
                		try {
                			baos.flush();
                			baos.close();
                		} catch(Exception e) { }
                	}

                	if(conn != null) {
                		conn.disconnect();
                	}
                }

        		break;
        	}

        } catch(SKTException e) {
        	throw e;
        } catch(UnknownHostException e) {
        	throw new SKTException(e, Constants.Status.E_NETWORK);
        } catch(SocketTimeoutException e) {
    		if(isConnectionTimeout(e)) {
        		throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
        	} else {
        		throw new SKTException(e, Constants.Status.E_READ_TIMEOUT);
        	}
        } catch (Exception e) {
        	throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
        }

        SKTUtil.timestamp(context, a, params.getPrimitive(), "[ CNT-REQ DONE ]");

        return xmlData;
	}

	/**
	 * 연결 타임아웃인지 구분한다
	 * @param e 타임아웃에러
	 * @return 연결타임아웃여부
	 */
	public static boolean isConnectionTimeout(SocketTimeoutException e) {
    	/*
    	Socket is not connected
    	Read timed out
    	The operation timed out
    	*/
		final String CONN_TIMEOUT_E = "Socket is not connected";

		SKTUtil.log(Log.DEBUG, "Controller", "SocketTimeoutException: " + "[" + e.getMessage() + "]");


		if(e.getMessage()==null || e.getMessage().indexOf(CONN_TIMEOUT_E)!=-1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 외부서버로 리퀘스트를 한다.
	 * @param address
	 * @return
	 * @throws SKTException
	 */
	public XMLData requestIF(String address) throws SKTException {
		NetworkInfo wifi   = SKTUtil.getWifiNetwork(context);
		NetworkInfo mobile = SKTUtil.getMobileNetwork(context);

		if(!wifi.isAvailable() && !mobile.isAvailable()) {
			throw new SKTException("Network-Setting is invalid!", Constants.Status.E_NETWORK);
		}

		if(!wifi.isConnectedOrConnecting() && !mobile.isConnectedOrConnecting()) {
			throw new SKTException("Network isn't connected!", Constants.Status.E_NETWORK);
		}

		if(!ROAMING && !wifi.isRoaming() && mobile.isRoaming() && mobile.isConnectedOrConnecting()) {
			ROAMING = true;
			throw new SKTException("Roaming not allow When 3G", Constants.Status.E_ROAMING);
		}

        HttpURLConnection 	conn    = null;

    	OutputStream          os   = null;
        InputStream           is   = null;
        ByteArrayOutputStream baos = null;

        XMLData 			xmlData = null;

        try {
        	URL url = null;
       		url = new URL(address);

       		Log.i("Controller : URL", url.toString());

        	conn = (HttpURLConnection)url.openConnection();
        	conn.setConnectTimeout(CONN_TIMEOUT * 1000);
        	conn.setReadTimeout(READ_TIMEOUT * 1000);
        	conn.setRequestMethod(GET);
        	//TODO RequestProperty(Host, Content-Length / Refer, Accept-Language, Content-Type ...)
        	conn.setRequestProperty("Cache-Control","no-cache");
        	conn.setDoOutput(true);
        	conn.setDoInput(true);

        	SKTUtil.log(Log.DEBUG, "Controller", "ResponseCode: " + conn.getResponseCode()+"");

        	if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        		is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer)) > 0) {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();

                String buffer = new String(byteData);
                int offset = buffer.indexOf("\n");
                String charset = "utf-8";
                if(offset != -1) {
                	String declare = buffer.substring(0, offset).toLowerCase();

                	int offset1 = declare.indexOf(ATT_ENCODING_START);
                	if(offset1 != -1) {
                		int offset2 = declare.indexOf(ATT_ENCODING_END, offset1 + ATT_ENCODING_START.length());
                		charset = declare.substring(offset1 + ATT_ENCODING_START.length(), offset2);
                	}
                }

                String response = new String(byteData, charset);

    	        if(response==null || response.equals("")) {
    	        	throw new SKTException("Response is NULL!", Constants.Status.E_RESPONSE);
    	        }

    	        Map<String,List<String>> headers = conn.getHeaderFields();
    	        Iterator<String> it = headers.keySet().iterator();
    	        while(it.hasNext()) {
    	        	String key = it.next();
    	        	List<String> values = headers.get(key);
    	        	StringBuffer sb = new StringBuffer();
    	        	for(int i=0; i<values.size(); i++) {
    	        		sb.append(";" + values.get(i));
    	        	}
    	        	SKTUtil.log(Log.DEBUG, "Controller", "ResponseHeader: " + key + "=" + sb.toString().substring(1));
    	        }

    	        SKTUtil.log(Log.DEBUG, "Controller", "Response: " + response);

                xmlData = new XMLData(response);
        	} else {
        		throw new SKTException("Network Error![RESPONSE_CODE=" + conn.getResponseCode() + "]", Constants.Status.E_RESPONSE);
        	}
        } catch(SKTException e) {
        	throw e;
        } catch(UnknownHostException e) {
        	throw new SKTException(e, Constants.Status.E_NETWORK);
        } catch(SocketTimeoutException e) {
        	if(isConnectionTimeout(e)) {
        		throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
        	} else {
        		throw new SKTException(e, Constants.Status.E_READ_TIMEOUT);
        	}
        } catch (Exception e) {
        	throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
        } finally {
        	if(os != null) {
        		try {
        			os.close();
        		} catch(Exception e) { }
        	}

        	if(is != null) {
        		try {
        			is.close();
        		} catch(Exception e) { }
        	}

        	if(baos != null) {
        		try {
        			baos.close();
        		} catch(Exception e) { }
        	}

        	if(conn != null) {
        		conn.disconnect();
        	}
        }

        return xmlData;
	}

	public XMLData MultiPartUpload(String fileName) throws SKTException {
		return MultiPartUpload(fileName, null);
	}

	public XMLData MultiPartUpload(String fileName, OnProgressChanged onProgressChanged) throws SKTException {
		String serviceFile = "/emp_ex/upload.pe";

		Environ environ = EnvironManager.getEnviron(this.context);
		String urlString = environ.getProtocol() + "://" + environ.getHost() + ":" + environ.getPort() + serviceFile;

		return MultiPartUpload(urlString, fileName, onProgressChanged);
	}

	private final String lineEnd = "\r\n";
	private final String twoHyphens = "--";
	private final String boundary = "*****";

	/*
	 * 멀티파트 파일 업로드
	 */
	public XMLData MultiPartUpload(String urlString, String fileName, OnProgressChanged onProgressChanged) throws SKTException {
		String result = null;
		XMLData xmlData = null;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;

		try {
			/*
			 * SSLVPN 접속체크 -- 한국도로공사 추가
			 * by pluto248 2011.10.24
			 */
//			SSLVPNController.checkConnectedSslVpn(this.context);

			File f = new File(fileName);
	        int fileSize = (int)f.length();

			FileInputStream mFileInputStream = new FileInputStream(fileName);
			URL connectUrl = new URL(urlString);
			Log.d("Controller", "multipart URL: " + connectUrl.toString());
//			Log.d("Controller", "mFileInputStream  is " + mFileInputStream);

			// open connection
			conn = (HttpURLConnection)connectUrl.openConnection();
			conn.setConnectTimeout(CONN_TIMEOUT_MULTIPART * 1000);
        	conn.setReadTimeout(READ_TIMEOUT_MULTIPART * 1000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			// write data
			dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			String extFileName = URLEncoder.encode(fileName.substring(fileName.lastIndexOf("/") + 1), "utf-8");
			dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + extFileName+"\"" + lineEnd);
			dos.writeBytes(lineEnd);

			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);

			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			Log.d("Controller","multipart step 1 " + bytesRead);
//			Log.d("Test", "image byte is " + bytesRead);

			// read image
			int totalWrite = 0;
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				totalWrite += bytesRead;
				if (onProgressChanged != null) {
					double progress = (double) totalWrite / (double) fileSize;
					onProgressChanged.onProgressChanged((int)(progress * 100));
				}
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
			}

			Log.d("Controller","multipart step 2 = " +bytesRead);

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			mFileInputStream.close();
			dos.flush(); // finish upload...
			Log.d("Controller","multipart step 3");
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				Log.d("Controller","multipart step ok 4");
				// get response
				int ch;
				InputStream is = conn.getInputStream();
				StringBuffer b =new StringBuffer();
				while( ( ch = is.read() ) != -1 ) {
					b.append( (char)ch );
				}
				result=b.toString();
				Log.e("Controller", "result = " + result);

				xmlData = new XMLData(result);
	            String ret = xmlData.get(Constants.TAG_RESULT);
			} else {
				Log.d("Controller","multipart step exception 5");
				throw new SKTException("Network Error![RESPONSE_CODE=" + conn.getResponseCode() + "]", Constants.Status.E_RESPONSE);
			}
		} catch (SKTException e1) {
			throw e1;
		} catch(UnknownHostException e) {
	    	throw new SKTException(e, Constants.Status.E_NETWORK);
	    } catch(SocketTimeoutException e) {
	    	if(isConnectionTimeout(e)) {
	    		throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
	    	} else {
	    		throw new SKTException(e, Constants.Status.E_READ_TIMEOUT);
	    	}
	    } catch (Exception e) {
	    	throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
		} finally {
			try {
				if (dos != null) dos.close();
			} catch (IOException e) {
				throw new SKTException(e);
			}
		}

		return xmlData;
	}

	public String requestToString(String address, String auth) throws SKTException {
		NetworkInfo wifi   = SKTUtil.getWifiNetwork(context);
		NetworkInfo mobile = SKTUtil.getMobileNetwork(context);

		if(!wifi.isAvailable() && !mobile.isAvailable()) {
			throw new SKTException("Network-Setting is invalid!", Constants.Status.E_NETWORK);
		}

		if(!wifi.isConnectedOrConnecting() && !mobile.isConnectedOrConnecting()) {
			throw new SKTException("Network isn't connected!", Constants.Status.E_NETWORK);
		}

		if(!ROAMING && !wifi.isRoaming() && mobile.isRoaming() && mobile.isConnectedOrConnecting()) {
			ROAMING = true;
			throw new SKTException("Roaming not allow When 3G", Constants.Status.E_ROAMING);
		}

        HttpURLConnection 	conn    = null;

    	OutputStream          os   = null;
        InputStream           is   = null;
        ByteArrayOutputStream baos = null;

        String ret = null;

        try {
        	URL url = null;
       		url = new URL(address);

       		Log.i("Controller : URL", url.toString());

        	conn = (HttpURLConnection)url.openConnection();
        	conn.setConnectTimeout(CONN_TIMEOUT * 1000);
        	conn.setReadTimeout(READ_TIMEOUT * 1000);
        	conn.setRequestMethod(GET);
        	//TODO RequestProperty(Host, Content-Length / Refer, Accept-Language, Content-Type ...)
        	conn.setRequestProperty("Cache-Control","no-cache");
        	if(auth != null)
        		conn.setRequestProperty("Authorization", "Basic " + auth);
        	conn.setDoOutput(true);
        	conn.setDoInput(true);

        	SKTUtil.log(Log.DEBUG, "Controller", "ResponseCode: " + conn.getResponseCode()+"");

        	if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
        		is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer)) > 0) {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();
    		    //String response = new String(byteData, "euc-kr");
                String response = new String(byteData);

    	        if(response==null || response.equals("")) {
    	        	throw new SKTException("Response is NULL!", Constants.Status.E_RESPONSE);
    	        }

    	        Map<String,List<String>> headers = conn.getHeaderFields();
    	        Iterator<String> it = headers.keySet().iterator();
    	        while(it.hasNext()) {
    	        	String key = it.next();
    	        	List<String> values = headers.get(key);
    	        	StringBuffer sb = new StringBuffer();
    	        	for(int i=0; i<values.size(); i++) {
    	        		sb.append(";" + values.get(i));
    	        	}
    	        	SKTUtil.log(Log.DEBUG, "Controller", "ResponseHeader : " + key + "=" + sb.toString().substring(1));
    	        }

    	        SKTUtil.log(Log.DEBUG, "Controller", "Response : " + response);
    	        
    	        ret = response;
        	} else {
        		throw new SKTException("Network Error![RESPONSE_CODE=" + conn.getResponseCode() + "]", Constants.Status.E_RESPONSE);
        	}
        } catch(SKTException e) {
        	throw e;
        } catch(UnknownHostException e) {
        	throw new SKTException(e, Constants.Status.E_NETWORK);
        } catch(SocketTimeoutException e) {
        	if(isConnectionTimeout(e)) {
        		throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
        	} else {
        		throw new SKTException(e, Constants.Status.E_READ_TIMEOUT);	
        	}
        } catch (Exception e) {
        	throw new SKTException(e, Constants.Status.E_CONN_TIMEOUT);
        } finally {
        	if(os != null) {
        		try {
        			os.close();
        		} catch(Exception e) { }
        	}

        	if(is != null) {
        		try {
        			is.close();
        		} catch(Exception e) { }
        	}

        	if(baos != null) {
        		try {
        			baos.close();
        		} catch(Exception e) { }
        	}

        	if(conn != null) {
        		conn.disconnect();
        	}
        }
		
        return ret;
	}

	/**
	 * 인증서를 등록한다.
	 * @throws Exception
	 */
	public static void trustHttpsCertificates() throws Exception {   
        //Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }
            }
        };

        //SSLContext sc = SSLContext.getInstance("SSL");
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier hv = new HostnameVerifier() {
           public boolean verify(String urlHostName, SSLSession session) {
               if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                   System.out.println("Warning: URL host '"+urlHostName+"' is different to SSLSession host '"+session.getPeerHost()+"'.");
               }
               return true;
           }
       };
       HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
	
	public interface OnProgressChanged {
		void onProgressChanged(int progress);
	}
}
