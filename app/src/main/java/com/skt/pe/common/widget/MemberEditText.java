package com.skt.pe.common.widget;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;

public class MemberEditText extends EditText implements OnKeyListener, TextWatcher, OnFocusChangeListener {
	private Map<String, String> map = new HashMap<String, String>();
	
	public MemberEditText(Context context) {
		super(context);
		init();
	}

	public MemberEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public MemberEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		this.addTextChangedListener(this);
		this.setOnKeyListener(this);
		this.setOnFocusChangeListener(this);
	}
	
	public void addText(String name, String value) {
		map.put(name,  value);
		this.append(name + ";");
	}
	
	public String[] getNames() {
		int size = map.size();
		String[] result = new String[size];
		
		int i=0;
		for (String key : map.keySet()) {
			result[i++] = key;
		}
		
		return result;
	}
	public void clearData() {
		map = new HashMap<String, String>(); 
	}
	public String[] getValues() {
		int size = map.size();
		String[] result = new String[size];
		
		int i=0;
		for (String key : map.keySet()) {
			result[i++] = map.get(key);
		}
		
		return result;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String key : map.keySet()) {
			sb.append(key);
			sb.append(";");
		}
		return sb.toString();
	}
	
	public String toShortString() {
		String result = ""; 
		if (map.size() > 0) {
			if (map.size() > 1) {
				result = map.keySet().iterator().next() + "외" + (map.size()-1) + "명"; 
			} else {
				result = map.keySet().iterator().next();
			}
		}
		return result;
	}
	
	public boolean isExistValue(String value) {
		boolean result = false;

		for (String key : map.keySet()) {
			if (value.equals(map.get(key))) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public int length() {
		if (map == null) 
			return 0;
		return map.size();
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		EditText textTo = (EditText)v;
		
		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			if(keyCode == KeyEvent.KEYCODE_DEL ) {
				String curText = textTo.getText().toString();
				String newText = "";
				String selText = "";

				if(curText != null && curText.length()>0) {
					if(curText.indexOf(";") < 0) {
						return false;
					} else {
						int prevPos = 0;
						int nextPos = 0;

						int curCsPos = textTo.getSelectionEnd(); //cursor의 현재위치.

						if(curCsPos == 0)
							return false;

						String lastToken = curText.substring(curCsPos-1,curCsPos);


						if(lastToken.equals(";")) {
							nextPos = curCsPos - 1;
							if(curCsPos == 1 )
								return false;

							prevPos = curText.substring(0, curCsPos-2).lastIndexOf(";");
						} else {

							prevPos = curText.substring(0, curCsPos).lastIndexOf(";");
							nextPos = curText.substring(curCsPos).indexOf(";");
							if(nextPos<0) nextPos = curText.length();                				
							else {
								nextPos += curCsPos ;
							}

						}
						selText = curText.substring(prevPos+1,nextPos);
						this.map.remove(selText);
						
						newText = curText.substring(0,prevPos+1) + curText.substring(nextPos+1);
						this.setText(newText);
//						onFocusChangeTextView(newText);


						Editable etext = textTo.getText();            						  
						Selection.setSelection(etext, prevPos+1);  

						return true;                					
					}
				}

				return false;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
		this.setFilters(new InputFilter[] {new InputFilter() {		
			public CharSequence filter(CharSequence source,int start,int end, Spanned dest,int dstart,int dend) {
				if(source.toString().getBytes().length > 3){
					return source;
				} else {
					return "";
				}
			 } 
		}});
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
	
//	private void onFocusChangeTextView(String list) {
//		this.setText(list);
//	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus == true) {
			this.setText(this.toString());
		} else {
			this.setText(this.toShortString());
		}
	}
}
