package com.skt.pe.common.dialog;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.conf.Resource;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


/**
 * SKT 기본 다이얼로그
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class SKDialog extends Dialog implements OnClickListener {
	
	public static final String DLG_LAYOUT = "dialog_";
	public static final int    DLG_TYPE_1        = 1;
	public static final int    DLG_TYPE_2        = 2;	

	public static final String LAYOUT_TITLE     = "layoutDlgTitle";
	public static final String TXT_TITLE_VIEW   = "txtDlgTitle";
	public static final String TXT_CONTENT_VIEW = "txtDlgContent";
	public static final String BTN_OK_VIEW      = "btnDlgOk";
	public static final String BTN_CANCEL_VIEW  = "btnDlgCancel";

	private int             dlgType       = 1;
	private OnClickListener positiveClick = null;
	private OnClickListener negativeClick = null;

	public SKDialog(Context context) {
		this(context, DLG_TYPE_1);
	}

	public SKDialog(Context context, int dlgType) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);

		WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();    
		lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		lpWindow.dimAmount = 0.8f;
		//lpWindow.windowAnimations = android.R.anim.accelerate_interpolator | android.R.anim.fade_in | android.R.anim.fade_out;
		getWindow().setAttributes(lpWindow);

		this.dlgType = dlgType;
		setCancelable(false);
		setContentView(getResId(DLG_LAYOUT + this.dlgType, Constants.TYPE_LAYOUT));

		switch(this.dlgType) {
			case DLG_TYPE_2 :
				Button negativeButton = (Button)findViewById(getResId(BTN_CANCEL_VIEW, Constants.TYPE_ID));
				negativeButton.setText(Resource.getString(context, Resource.RES_NEGATIVE_ID));
				negativeButton.setOnClickListener(this);
			case DLG_TYPE_1 :
				Button positiveButton = (Button)findViewById(getResId(BTN_OK_VIEW, Constants.TYPE_ID));
				positiveButton.setText(Resource.getString(context, Resource.RES_POSITIVE_ID));
				positiveButton.setOnClickListener(this);
				break;
		}

		TextView titleView = (TextView)findViewById(getResId(TXT_TITLE_VIEW, Constants.TYPE_ID));
		titleView.setSelected(true);
		TextView contentView = (TextView)findViewById(getResId(TXT_CONTENT_VIEW, Constants.TYPE_ID));
		contentView.setSelected(true);
	}

	private int getResId(String id, String type) {
		return getContext().getResources().getIdentifier(id, type, getContext().getPackageName());
	}
	
	public void setTitle(int id) {
		TextView title = (TextView)findViewById(getResId(TXT_TITLE_VIEW, Constants.TYPE_ID));
		title.setText(id);
	}
	
	public void setTitle(String title) {
		TextView titleView = (TextView)findViewById(getResId(TXT_TITLE_VIEW, Constants.TYPE_ID));
		titleView.setText(title);

		View layoutTitle = findViewById(getResId(LAYOUT_TITLE, Constants.TYPE_ID));
		if(title!=null && title.trim().length()>0) {
			layoutTitle.setVisibility(View.VISIBLE);
		} else {
			layoutTitle.setVisibility(View.GONE);
		}
	}
	
	public void setContent(int id) {
		TextView contentView = (TextView)findViewById(getResId(TXT_CONTENT_VIEW, Constants.TYPE_ID));
		contentView.setText(id);
	}
	
	public void setContent(String content) {
		TextView contentView = (TextView)findViewById(getResId(TXT_CONTENT_VIEW, Constants.TYPE_ID));
		contentView.setText(content);
	}
	
	public void setPositiveButton(DialogButton dlgButton) {
		Button positiveButton = (Button)findViewById(getResId(BTN_OK_VIEW, Constants.TYPE_ID));
		
		if(dlgButton.getCaption()!=null && dlgButton.getCaption().trim().length()>0) {
			positiveButton.setText(dlgButton.getCaption());
		} else {
			if (dlgButton != null) {
				int b_id = dlgButton.getCaptionResIdId();
				if(b_id != 0) {
					positiveButton.setText(b_id);
				}
			}
		}

		positiveClick = dlgButton;
	}

	public void setNegativeButton(DialogButton dlgButton) {
		if(this.dlgType >= DLG_TYPE_2) {
			Button negativeButton = (Button)findViewById(getResId(BTN_CANCEL_VIEW, Constants.TYPE_ID));
			
			if(dlgButton.getCaption()!=null && dlgButton.getCaption().trim().length()>0) {
				negativeButton.setText(dlgButton.getCaption());
			} else {
				if (dlgButton != null) {
					int b_id = dlgButton.getCaptionResIdId();
					if(b_id != 0) {
						negativeButton.setText(b_id);
					}
				}
			}

			negativeClick = dlgButton;
		}
	}

	public void onClick(View arg0) {
		if(arg0.getId() == getResId(BTN_OK_VIEW, Constants.TYPE_ID)) {
			if(positiveClick != null)
				positiveClick.onClick(this, 0);
			dismiss();
		} else if(arg0.getId() == getResId(BTN_CANCEL_VIEW, Constants.TYPE_ID)) {
			if(negativeClick != null)
				negativeClick.onClick(this, 0);
			dismiss();
		}
	}

	public Dialog getDialog(int contentId) {
		return getDialog("", getContext().getResources().getString(contentId));
	}
	
	public Dialog getDialog(String content) {
		return getDialog("", content);
	}
	
	public Dialog getDialog(int titleId, int contentId) {
		Resources res = getContext().getResources();
		return getDialog(res.getString(titleId), res.getString(contentId));
	}
	
	public Dialog getDialog(String title, String content) {
		setTitle(title);
		setContent(content);
		setPositiveButton(new DialogButton(0) {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		return this;
	}

	public Dialog getDialog(int contentId, DialogButton positiveButton) {
		return getDialog("", getContext().getResources().getString(contentId), positiveButton);
	}
	
	public Dialog getDialog(String content, DialogButton positiveButton) {
		return getDialog("", content, positiveButton);
	}
	
	public Dialog getDialog(String title, String content, DialogButton positiveButton) {
		setTitle(title);
		setContent(content);
		setPositiveButton(positiveButton);
		return this;
	}

	public Dialog getDialog(String title, String content, DialogButton positiveButton, DialogButton negaiveButton) {
		setTitle(title);
		setContent(content);
		setPositiveButton(positiveButton);
		setNegativeButton(negaiveButton);
		return this;
	}

}
