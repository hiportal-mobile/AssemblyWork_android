package com.skt.pe.common.primitive;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.util.StringUtil;
import com.skt.pe.util.XMLUtil;

public class PrimitiveInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4112250964429096938L;
	protected Map<String, String> map;
	
	public PrimitiveInfo() {
		map = new HashMap<String, String>();
	}
	
	public void setXml(XMLData node, String xpath) throws SKTException {
		node.setList(xpath);
		putMap(node.getListItem(0));
	}
	
	public void setXml(XMLData child, int index, String xpath) throws SKTException {
		child.setList(xpath);
		putMap(child.getListItem(index));
	}
	
	public void setXml(XMLData child, int index) throws SKTException {
		putMap(child.getListItem(index));
	}
	
	public void putXml(Element node) {
		NodeList items = node.getChildNodes();
		for (int i=0; i<items.getLength(); i++) {
			Node item = items.item(i);
			map.put(item.getNodeName(), XMLUtil.getTextContent(item));
		}
	}
	
	public void putXml(Node node) {
		NodeList items = node.getChildNodes();
		for (int i=0; i<items.getLength(); i++) {
			Node item = items.item(i);
			map.put(item.getNodeName(), XMLUtil.getTextContent(item));
		}
	}
	
	public void putString(String key, String value) {
		map.put(key, value);
	}
	
	public String getString(String key) {
		String value = (String) map.get(key);
		return value == null ? "" : value;
	}
	
	public int getInt(String key) {
		String value = (String) map.get(key);
//		return (value == null || value.length() == 0) ? "0" : value;
		return StringUtil.intValue(value, 0);
	}
	
	public Date getDate(String key, String dateFormat) {
		SimpleDateFormat sd = new SimpleDateFormat(dateFormat);
		String value = (String) map.get(key);
		
		try {
			return sd.parse(value);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public boolean getBoolean(String key, String yesValue) {
		String value = (String) map.get(key);
		return yesValue.equals(value);
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	
	public String toString() {
		return (map != null ? mapToString() : "");
	}

	
	private void putMap(Map<String, Object> map) {
		if (map != null) {
			for (String key : map.keySet()) {  
				if (Constants.ID_NODE.equals(key))
					continue;
				String value = (String) map.get(key);
				this.map.put(key, value);
			}
		}
	}
	
	private String mapToString() {
		StringBuffer sb = new StringBuffer();  
			  
		for (String key : map.keySet()) {  
			if (sb.length() > 0) {  
				sb.append("&");  
			}

			if (key.length() == 0) continue;
			
			String value = (String) map.get(key);
			sb.append((key != null ? key : ""));  
			sb.append("=");  
			sb.append(value != null ? value : "");  
		}  
			  
		return sb.toString();  
	}
}
