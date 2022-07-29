package com.skt.pe.common.service;

import java.util.List;
import java.util.Map;

import com.skt.pe.common.exception.ParsingException;


/**
 * 데이타 리스트
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class DataList {

	protected List<Map<String, Object>> list = null;
	
	public DataList() { }
	
    public String get(int index, String key) throws ParsingException {
    	if(list == null) {
    		throw new ParsingException("List isn't Assigned!");
    	}
    	
    	if(containsKey(index, key)) {
    		return list.get(index).get(key).toString();
    	} else {
    		return null;
    	}
    }
    
    public Object getObject(int index, String key) throws ParsingException {
    	if(list == null) {
    		throw new ParsingException("List isn't Assigned!");
    	}
    	
    	if(containsKey(index, key)) {
    		return list.get(index).get(key);
    	} else {
    		return null;
    	}
    }
    
    public Map<String, Object> getListItem(int index) throws ParsingException {
    	if(list == null) {
    		throw new ParsingException("List isn't Assigned!");
    	}
    	if(list.size() <= index) {
    		throw new ParsingException("Index Over!");
    	} 

    	return list.get(index);
    	
    }
    
    public int size() {
    	return list.size();
    }
    
    public boolean containsKey(int index, String key) {
    	if(list.size() <= index) {
    		return false;
    	} else { 
    		Map<String, Object> value = list.get(index);
    		return value.containsKey(key);
    	}
    }
    
    public String toString() {
    	return mapToString();
    }
    
    private String mapToString() {
		StringBuffer sb = new StringBuffer();  
	
		if (list != null) {
			for (int i=0; i<list.size(); i++) {
				Map<String, Object> map = list.get(i);
				for (String key : map.keySet()) {  
					if (sb.length() > 0) {  
						sb.append("&");  
					}
					
					/* 임시로 둔 디버깅 코드 삭제할 것 */
					if (key.equals("children") == true)
						continue;
					/*****************************/
					String value = map.get(key).toString();
					
					sb.append((key != null ? key : ""));  
					sb.append("=");  
					sb.append(value != null ? value : "");  
				}  
			}
		}
			  
		return sb.toString();  
	}

}
