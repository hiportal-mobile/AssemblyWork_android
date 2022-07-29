package com.skt.pe.common.dialog;

import android.content.DialogInterface.OnClickListener;

/**
 * 다이얼로그 버튼 이벤트 핸들러
 * @author june
 *
 */
public abstract class DialogButton implements OnClickListener {

	private int    captionResId = -1;
	private String caption      = "";
	private Object tag = null; 
	
	public int getCaptionResIdId() {
		return captionResId;
	}
	public String getCaption() {
		return caption;
	}
	
	public DialogButton(int captionResId) {
		this.captionResId = captionResId;
	}

	public DialogButton(int captionResId, Object tag) {
		this.captionResId = captionResId;
		this.tag = tag;
	}

	public DialogButton(String caption) {
		this.caption = caption;
	}

	public Object getTag() {
		return tag;
	}

}
