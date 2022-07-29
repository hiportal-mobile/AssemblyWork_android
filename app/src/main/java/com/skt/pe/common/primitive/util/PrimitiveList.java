package com.skt.pe.common.primitive.util;

import java.util.ArrayList;
import java.util.List;

public class PrimitiveList {
	private List<String> list = new ArrayList<String>();
	
	public void add(String data) {
		list.add(data);
	}
	
	public String get(int index) {
		return list.get(index);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		for (int i=0; i<list.size(); i++) {
			sb.append(list.get(i));
			sb.append("|");
		}
		if (sb.toString().length() > 0)
			return sb.substring(0, sb.length()-1);
		
		return sb.toString();
	}
}
