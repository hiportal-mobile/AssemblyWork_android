package com.skt.pe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;


/**
 * 리소스 관련 유틸
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class ResourceUtil {

	public static Bitmap decodeBitmap(byte[] dec) throws SKTException {
		return decodeBitmap(dec, null);
	}
	
	public static Bitmap decodeBitmap(byte[] dec, BitmapFactory.Options opts) throws SKTException {
		try {			
			if(dec==null || dec.length==0) {
				throw new SKTException("Decoding Fail!");
			}

			Bitmap img = null;
			if(opts != null) {
				img = BitmapFactory.decodeByteArray(dec, 0, dec.length, opts); 
			} else {
				img = BitmapFactory.decodeByteArray(dec, 0, dec.length);
			}
			if(img == null) {
				throw new SKTException("Bitmap Fail!");
			}

			return  img;	
		} catch(SKTException e) {
			throw e;
		} catch(Exception e) {
			throw new SKTException(e);
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		}
	}

	public static Drawable decodeDrawable(byte[] dec) throws SKTException {
		InputStream is = null;
		try {			
			if(dec==null || dec.length==0) {
				throw new SKTException("Decoding Fail!");
			}

			is = new ByteArrayInputStream(dec); 
			
			Drawable img = Drawable.createFromStream(is, "src"); 
			if(img == null) {
				throw new SKTException("Drawable Fail!");
			}

			return  img;	
		} catch(SKTException e) {
			throw e;
		} catch(Exception e) {
			throw new SKTException(e);
		} catch(Throwable e) {
			throw new SKTException(new Exception(e));
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch(Exception e) { }
			}
		}
		
	}

	public static Bitmap decodeBitmap(String src) throws SKTException {
		return decodeBitmap(Base64Util.decodeBinay(src));
	}
	
	public static Bitmap decodeBitmap(String src, BitmapFactory.Options opts) throws SKTException {
		return decodeBitmap(Base64Util.decodeBinay(src), opts);
	}

	public static Drawable decodeDrawable(String src) throws SKTException {
		return decodeDrawable(Base64Util.decodeBinay(src));
	}
	
	public static Bitmap getBitmapByUrl(String address) throws SKTException {
		InputStream           is   = null;
		ByteArrayOutputStream baos = null;

		try {
			SKTUtil.log(android.util.Log.DEBUG, "GetBitmapByUrl", "[" + address + "]");

			URL url = new URL(address);

			Object content = url.getContent();
			is = (InputStream)content;
			//is = url.openStream();

			//Bitmap img = BitmapFactory.decodeStream(is);

            baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData = null;
            int nLength = 0;
            while((nLength = is.read(byteBuffer)) > 0) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();
            Bitmap img = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
			 
			if(img == null) {
				throw new SKTException("Bitmap Fail!" + " - [" + address + "]");
			}

			return img;
		} catch(SKTException e) {
			throw e;
		} catch(Exception e) {
			throw new SKTException(e);
		} finally {
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
		}
	}
	
	public static Drawable getDrawableByUrl(String address) throws SKTException {
		InputStream           is   = null;
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream  bais = null;

		try {
//			SKTUtil.log("Controller", "Start GetDrawableByUrl : [" + address + "]");

			URL url = new URL(address);

			Object content = url.getContent();
			is = (InputStream)content;
			
            baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData = null;
            int nLength = 0;
            while((nLength = is.read(byteBuffer)) > 0) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();
            bais = new ByteArrayInputStream(byteData);

            Drawable img = Drawable.createFromStream(bais, "src");
            
			if(img == null) {
				throw new SKTException("Drawable Fail!" + " - [" + address + "]");
			}

//			SKTUtil.log("Controller", "End GetDrawableByUrl : [" + address + "]");
			
			return img;
		} catch(SKTException e) {
			throw e;
		} catch(Exception e) {
			throw new SKTException(e);
		} finally {
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

        	if(bais != null) {
        		try {
        			bais.close();
        		} catch(Exception e) { }
        	}
		}
	}
	
	public static byte[] getDrawableByteByUrl(String address) throws SKTException {
		InputStream           is   = null;
		ByteArrayOutputStream baos = null;
		
		try {
			SKTUtil.log(android.util.Log.DEBUG, "GetDrawableByteByUrl", "[" + address + "]");

			URL url = new URL(address);

			Object content = url.getContent();
			is = (InputStream)content;
			
            baos = new ByteArrayOutputStream();
            byte[] byteBuffer = new byte[1024];
            byte[] byteData = null;
            int nLength = 0;
            while((nLength = is.read(byteBuffer)) > 0) {
                baos.write(byteBuffer, 0, nLength);
            }
            byteData = baos.toByteArray();

			return byteData;
		} catch(Exception e) {
			throw new SKTException(e);
		} finally {
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
		}
	}

	public static Drawable getDrawable(byte[] src) throws SKTException {
		ByteArrayInputStream  bais = null;

		try {
            bais = new ByteArrayInputStream(src);

            Drawable img = Drawable.createFromStream(bais, "src");
			if(img == null) {
				throw new SKTException("Drawable Fail!");
			}

			return img;
		} catch(Exception e) {
			throw new SKTException(e);
		} finally {
        	if(bais != null) {
        		try {
        			bais.close();
        		} catch(Exception e) { }
        	}
		}
	}
	
}
