package com.skt.pe.common.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;

import com.skt.pe.common.conf.Constants;


/**
 * 파라미터
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class Parameters implements Serializable{

	private static final long serialVersionUID = 1L;

	private String primitive = "";
	private Map<String, Object> params = new HashMap<String, Object>();

	@SuppressWarnings("unused")
	private Parameters() { };
	
	public Parameters(String primitive) {
		this.primitive = primitive;
	}
	
	public boolean containsKey(String key) {
		return params.containsKey(key);
	}
	
	public void put(String key, String value) {
		params.put(key, value);
	}
	
	public void put(String key, String[] values) {
		params.put(key, values);
	}
	
	public String get(String key) {
		Object value = params.get(key);
		if(value instanceof String) {
			return (String)params.get(key);
		} else {
			return value.toString();
		}
	}

	public String[] getArray(String key) {
		Object value = params.get(key);
		if(value instanceof String[]) {
			return (String[])params.get(key);
		} else if(value instanceof String) {
			return new String[] { (String)value };
		} else {
			return null;
		}
	}
	
	private Object getObject(String key) {
		return params.get(key);
	}

	public void append(Parameters p) {
		Iterator<String> it = p.iterator();
		while(it.hasNext()) {
			String key   = it.next();
			Object value = p.getObject(key);
			params.put(key, value);
		}
	}

	public void remove(String key) {
		params.remove(key);
	}

	public Iterator<String> iterator() {
		return params.keySet().iterator();
	}

	public String getPrimitive() {
		return primitive;
	}
	
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean postFlag) {
		StringBuffer buf = new StringBuffer();
		buf.append(Constants.TAG_PRIMITIVE + "=" + getPrimitive());
		Iterator<String> it = iterator();
		while(it.hasNext()) {
			String key   = it.next();
			Object value = params.get(key);

			if(value == null) {
				buf.append("&" + key + "=");
				Log.w("Parameter", "KEY[=" + key + "] is null");
				continue;
			}

			String[] values = null;
			if(value instanceof String) {
				values = new String[] {(String)value};
			} else if(value instanceof String[]) {
				values = (String[])value;
			}

			for(int i=0; i<values.length; i++) {
				if(values[i]==null || values[i].length()==0) {
					Log.w("Parameter", "KEY[=" + key + "] is null");
					values[i] = "";
				} else {
					try {
						values[i] = URLEncoder.encode(values[i], "utf-8");
					} catch (UnsupportedEncodingException e) {
						Log.w("Parameter", e.toString());
					}
				}
				buf.append("&" + key + "=" + values[i]);
			}
		}

		return buf.toString();
	}
}
