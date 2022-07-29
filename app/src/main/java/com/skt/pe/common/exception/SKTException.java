package com.skt.pe.common.exception;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.Resource;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.dialog.DialogButton;
import com.skt.pe.common.dialog.SKTDialog;
import com.skt.pe.util.StringUtil;


/**
 * SKT 예외
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class SKTException extends Exception {

	private static final long serialVersionUID = 1L;

	protected String  errCode   = "";
	protected boolean alertFlag = false;
	protected List<String> paramList = new ArrayList<String>(); 

	public SKTException(String errMessage) {
		this(errMessage, Constants.Status.E_NORMAL);
	}

	public SKTException(String errMessage, String errCode) {
		super(errMessage);
		this.errCode = errCode;
		Log.e(this.getClass().getSimpleName() + "[ERR_CODE=" + errCode + "]", errMessage);
		if(("E_response").equals(errCode) || ("e_network").equals(errCode)){
			Log.e("SKTException", "VPN DISCONNECTED");
		}
	}

	public SKTException(Exception e) {
		this(e, Constants.Status.E_NORMAL);
	}

	public SKTException(Exception e, String errCode) {
		super(e);
		this.errCode = errCode;
		Log.e(this.getClass().getSimpleName() + "[ERR_CODE=" + errCode + "]", e.getMessage(), e);
	}

	public SKTException(Throwable e, String errCode) {
		super(e);
		this.errCode = errCode;
		Log.e(this.getClass().getSimpleName() + "[ERR_CODE=" + errCode + "]", e.getMessage(), e);
	}
	
	public SKTException addParam(String...params) {
		if(params != null) {
			for(String param : params) {
				paramList.add(param);
			}
		}
		return this;
	}
	
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getEncodedMessage(Context context) {
		String message = getMessage();

		String errCode = getErrCode(); 
		if(errCode!=null && errCode.trim().length()>0) {
			if(Constants.Status.errList.contains(errCode)) {
				// 서버쪽 오류이면 서버 메시지를 그대로 보낸다.
				String buffer = null;
				if (errCode.substring(0, 1).equals("E") == true) 
					buffer = Resource.getString(context, "_" + errCode);
				else
					buffer = this.getMessage();
				if(buffer!=null && !"NULL".equals(buffer)) {
					message = buffer;
				} else {
					try {
						Resources res = context.getResources();
						message = res.getString(res.getIdentifier("_" + errCode, Constants.TYPE_STRING, context.getPackageName()));
					} catch(Exception e) { }
				}
			}
		}

		if(paramList.size() > 0) {
			String[] params = new String[paramList.size()];
			params = paramList.toArray(params);
			message = StringUtil.format(message, params);
		}

		//TODO DEBUG
		//message += " [" + errCode + " / " + this.getMessage() + "]";
 
		//return message.replaceAll("\\\\n", "\n");
		return message.replaceAll("\\\\n", "");
	}
	
	public void alert(Context context) {
		
		if(!alertFlag && !getErrCode().equals("7001") && !getErrCode().equals("7009")) {
			try {
				SKTDialog dlg = new SKTDialog(context, SKTDialog.DLG_TYPE_1);
				dlg.getDialog(Resource.getString(context, Resource.RES_POSITIVE_ID), getEncodedMessage(context)).show();
				
				alertFlag = true;
			} catch (Exception e) {
				SKTUtil.log("Error", "SKDialog show error");
			}
		}
	}

	public void alert(Context context, DialogButton positiveButton) {

		if(!alertFlag && !getErrCode().equals("7001") && !getErrCode().equals("7009")) {
			try {
				SKTDialog dlg = new SKTDialog(context, SKTDialog.DLG_TYPE_1);
				dlg.getDialog(Resource.getString(context, Resource.RES_POSITIVE_ID), getEncodedMessage(context), positiveButton).show();
	
				alertFlag = true;
			} catch (Exception e) {
				SKTUtil.log("Error", "SKDialog show error");
			}
		}
	}

	public void alert(Context context, DialogButton positiveButton, DialogButton negativeButton) {
		if(!alertFlag && !getErrCode().equals("7001") && !getErrCode().equals("7009")) {
			try {
				SKTDialog dlg = new SKTDialog(context, SKTDialog.DLG_TYPE_2);
				dlg.getDialog(Resource.getString(context, Resource.RES_POSITIVE_ID), getEncodedMessage(context), positiveButton, negativeButton).show();
	
				alertFlag = true;
			} catch (Exception e) {
				SKTUtil.log("Error", "SKDialog show error");
			}
		}
	}

	public void alert(String title, Context context) {
		if(!alertFlag && !getErrCode().equals("7001") && !getErrCode().equals("7009")) {
			try {
				SKTDialog dlg = new SKTDialog(context, SKTDialog.DLG_TYPE_1);
				dlg.getDialog(title, getEncodedMessage(context)).show();
				
				alertFlag = true;
			} catch (Exception e) {
				SKTUtil.log("Error", "SKDialog show error");
			}
		}
	}

	public void alert(String title, Context context, DialogButton positiveButton) {
		if(!alertFlag && !getErrCode().equals("7001") && !getErrCode().equals("7009")) {
			try {
				SKTDialog dlg = new SKTDialog(context, SKTDialog.DLG_TYPE_1);
				dlg.getDialog(title, getEncodedMessage(context), positiveButton).show();
	
				alertFlag = true;
			} catch (Exception e) {
				SKTUtil.log("Error", "SKDialog show error");
			}
		}
	}

	public void alert(String title, Context context, DialogButton positiveButton, DialogButton negativeButton) {
		if(!alertFlag && !getErrCode().equals("7001") && !getErrCode().equals("7009")) {
			try {
				SKTDialog dlg = new SKTDialog(context, SKTDialog.DLG_TYPE_2);
				dlg.getDialog(title, getEncodedMessage(context), positiveButton, negativeButton).show();
	
				alertFlag = true;
			} catch (Exception e) {
				SKTUtil.log("Error", "SKDialog show error");
			}
		}
	}

	public void setAlertReset() {
		alertFlag = false;
	}
	
	public boolean getAlertReset() {
		return alertFlag;
	}

}
