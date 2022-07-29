package com.skt.pe.common.dialog;

import com.skt.pe.common.conf.Resource;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;


/**
 * 간단 다이얼로그
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class SKTDialog implements OnClickListener {

	public static final int    DLG_TYPE_1        = 1;
	public static final int    DLG_TYPE_2        = 2;
	public static final int    DLG_TYPE_3        = 3;	
	
	private int             dlgType       = 1;
	private OnClickListener positiveClick = null;
	private OnClickListener neutralClick  = null;
	private OnClickListener negativeClick = null;
	
	private AlertDialog.Builder alert   = null;

	public SKTDialog(Context context) {
		this(context, DLG_TYPE_1);
	}

	public SKTDialog(Context context, int dlgType) {
		this.dlgType = dlgType;
		alert = new AlertDialog.Builder(context);
		alert.setCancelable(false);

		switch(this.dlgType) {
		case DLG_TYPE_3 :
			alert.setNeutralButton(Resource.getString(context, Resource.RES_NEUTRAL_ID), this);
		case DLG_TYPE_2 :
			alert.setNegativeButton(Resource.getString(context, Resource.RES_NEGATIVE_ID), this);
		case DLG_TYPE_1 :
			alert.setPositiveButton(Resource.getString(context, Resource.RES_POSITIVE_ID), this);
			break;
		}
	}

	public void setTitle(int id) {
		alert.setTitle(id);
	}
	
	public void setTitle(String title) {
		alert.setTitle(title);
	}
	
	public void setContent(int id) {
		alert.setMessage(id);
	}
	
	public void setContent(String content) {
		alert.setMessage(content);
	}
	
	public void setPositiveButton(DialogButton dlgButton) {		
		if(dlgButton.getCaption()!=null && dlgButton.getCaption().trim().length()>0) {
			alert.setPositiveButton(dlgButton.getCaption(), this);
		} else {
			if (dlgButton != null) {
				int b_id = dlgButton.getCaptionResIdId();
				if(b_id != 0) {
					alert.setPositiveButton(b_id, this);
				}
			}
		}

		positiveClick = dlgButton;
	}
	
	public void setNegativeButton(DialogButton dlgButton) {
		if(this.dlgType >= DLG_TYPE_2) {
			if(dlgButton.getCaption()!=null && dlgButton.getCaption().trim().length()>0) {
				alert.setNegativeButton(dlgButton.getCaption(), this);
			} else {
				if (dlgButton != null) {
					int b_id = dlgButton.getCaptionResIdId();
					if(b_id != 0) {
						alert.setNegativeButton(b_id, this);
					}
				}
			}

			negativeClick = dlgButton;
		}
	}
	
	public void setNeutralButton(DialogButton dlgButton) {
		if(this.dlgType >= DLG_TYPE_3) {
			if(dlgButton.getCaption()!=null && dlgButton.getCaption().trim().length()>0) {
				alert.setNeutralButton(dlgButton.getCaption(), this);
			} else {
				if (dlgButton != null) {
					int b_id = dlgButton.getCaptionResIdId();
					if(b_id != 0) {
						alert.setNeutralButton(b_id, this);
					}
				}
			}

			neutralClick = dlgButton;
		}
	}	

	public AlertDialog getDialog(int contentId) {
		setTitle("");
		setContent(contentId);
		return alert.create();
	}
	
	public AlertDialog getDialog(String content) {
		setTitle("");
		setContent(content);
		return alert.create();
	}
	
	public AlertDialog getDialog(int titleId, int contentId) {
		setTitle(titleId);
		setContent(contentId);
		return alert.create();
	}
	
	public AlertDialog getDialog(String title, String content) {
		setTitle(title);
		setContent(content);
		return alert.create();
	}

	public AlertDialog getDialog(int titleId, int contentId, DialogButton positiveButton) {
		setTitle(titleId);
		setContent(contentId);
		setPositiveButton(positiveButton);
		return alert.create();
	}

	public AlertDialog getDialog(int contentId, DialogButton positiveButton) {
		setTitle("");
		setContent(contentId);
		setPositiveButton(positiveButton);
		return alert.create();
	}
	
	public AlertDialog getDialog(String content, DialogButton positiveButton) {
		setTitle("");
		setContent(content);
		setPositiveButton(positiveButton);
		return alert.create();
	}
	
	public AlertDialog getDialog(String title, String content, DialogButton positiveButton) {
		setTitle(title);
		setContent(content);
		setPositiveButton(positiveButton);
		return alert.create();
	}

	public AlertDialog getDialog(String title, String content, DialogButton positiveButton, DialogButton negaiveButton) {
		setTitle(title);
		setContent(content);
		setPositiveButton(positiveButton);
		setNegativeButton(negaiveButton);
		return alert.create();
	}

	public void onClick(DialogInterface arg0, int arg1) {
		switch(arg1) {
		case Dialog.BUTTON_POSITIVE :
			if(positiveClick != null)
				positiveClick.onClick(arg0, Dialog.BUTTON_POSITIVE);
			break;
		case Dialog.BUTTON_NEGATIVE :
			if(negativeClick != null)
				negativeClick.onClick(arg0, Dialog.BUTTON_NEGATIVE);
			break;
		case Dialog.BUTTON_NEUTRAL :
			if(neutralClick != null)
				neutralClick.onClick(arg0, Dialog.BUTTON_NEUTRAL);
			break;
		}
	}


}
