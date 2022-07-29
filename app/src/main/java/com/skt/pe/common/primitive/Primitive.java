package com.skt.pe.common.primitive;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.skt.pe.common.conf.Constants;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.ifaces.PrimitiveHandler;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;
import com.skt.pe.util.StringUtil;

public abstract class Primitive implements PrimitiveHandler, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1978501845167239813L;
//	private static final String PRIMITIVE_KEY = "primitive";
	
	private String name;
	private Parameters params;
	private int dialogMessage;
	private String path = "/emp_ex/service.pe";
	private String returnPrimitive = null;
	private String resultCode = null;
	private String resultMessage = null;
//	private String returnMobilePoc = null;
	
	private String[] paramNames;
	
	private Map<String, Object> tagMap = new HashMap<String, Object>();
	
//	abstract protected String toPrimitiveString();

	public Primitive(String name, String[] paramNames, int dialogMessage) {
		this.init(name, paramNames, dialogMessage);
	}
	
	private void init(String name, String[] paramNames, int dialogMessage) {
		this.paramNames = paramNames;
		this.name = name;
		this.dialogMessage = dialogMessage;
		params = new Parameters(name);
//		this.addParameter(Primitive.PRIMITIVE_KEY, name);
	}
	
	public String getParamName(int index) {
		return this.paramNames[index];
	}
	
	public void setParameters(String[] args) {
		for (int i=0; i<paramNames.length; i++) {
			this.addParameter(paramNames[i], args[i]);
		}
	}
	
	public Object getTag(String key) {
		return this.tagMap.get(key);
	}
	
	public void setTag(String key, Object value) {
		this.tagMap.put(key,  value);
	}
	
	public void removeTag(String key) {
		this.tagMap.remove(key);
	}
	
	/*
	 * 통신 파라메터에서 암호화 규격이 있을 때 아래의 항목을 추가한다.
	 * by pluto248
	 * 
	
	public Primitive(String name, String dialogMessage) {
		this.init(name, dialogMessage, null);
	}
	
	public Primitive(String name, String dialogMessage, SymCryptMethod method) {
		this.init(name, dialogMessage, method);
	}
	
	private void init(String name, String dialogMessage, SymCryptMethod method) {
		this.name = name;
		this.dialogMessage = dialogMessage;
		params = new Parameters(method);
		this.addParameter(MisoSession.KeyNames.PRIMITIVE, name);
	}
	
	public void addParameter(String key, String value, boolean isEncode) {
		params.put(key, value, isEncode);
	}
	*/
	
	public void addParameter(String key, String value) {
		params.put(key, value);
	}
	
	public void addParameter(String key, int value) {
		params.put(key, String.valueOf(value));
	}

	public String getPrimitiveName() {
		return this.name;
	}
	
	public Parameters getParameters() {
		return this.params;
	}
	
	public String getParameter(String key) {
		return params.get(key);
	}

	public int getDialogMessage() {
		return this.dialogMessage;
	}
	
	public String getUrlPath() {
		return this.path;
	}
	
	public void convertXML(XMLData xmldata) throws SKTException {
		this.returnPrimitive = xmldata.get("primitive");
		this.resultCode = xmldata.get("result");
		this.resultMessage = xmldata.get("resultMessage");
//		this.returnMobilePoc = xmldata.get("returnMobilePoc");
	}
	
	public boolean isOK() {
		return Constants.Status.S_SUCCESS.equals(resultCode);
//		return CommManager.ErrCode.SUCCESS.equals(resultCode);
	}
	
	public String getReturnPrimitive() {
		return this.returnPrimitive;
	}
	
	public String getResultCode() {
		return this.resultCode;
	}
	
	public String getResultMessage() {
		return this.resultMessage;
	}
	
//	public String getReturnMobilePoc() {
//		return this.returnMobilePoc;
//	}
	
	protected String toPrimitiveString() {
		return "";
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String primtiveString = toPrimitiveString();
		if (primtiveString != null) {
			sb.append(primtiveString);
			sb.append(",");
		}
		sb.append("Return Primitive=" + this.returnPrimitive + ",Result Code=" + this.resultCode + ",Result Message=" + this.resultMessage);
//				+ ",ReturnMobilePoc=" + this.returnMobilePoc);
		return sb.toString();
	}
	
	public String toParameterString() {
		return params.toString();  
	}
	
	public int parseInt(String value) {
//		return StringUtil.intValue(value.trim(), 0);
		return parseInt(value, 0);
	}
	
	public int parseInt(String value, int defaultValue) {
		String intValue = null; 
		if (value != null) 
			intValue = value.trim();
		else 
			intValue = value;
		return StringUtil.intValue(intValue, defaultValue);
	}
	
	public Date parseDate(String value, String dateFormat) {
		SimpleDateFormat sd = new SimpleDateFormat(dateFormat);
		
		try {
			return sd.parse(value);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public boolean parseBoolean(String value, String yesValue) {
		return yesValue.equals(value);
	}
/*
	public class DialogMessage {
		public final static String RETRIEVING = "자료를 가져오고 있는 중입니다. 잠시만 기다려 주십시오.";
		public final static String SENDING = "자료 전송 중입니다. 잠시만 기다려 주십시오.";
		public final static String LOGIN = "로그인 중입니다.\n잠시만 기다리십시오.";
	}
*/
}
