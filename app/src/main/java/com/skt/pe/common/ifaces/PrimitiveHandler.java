/**
 * 
 */
package com.skt.pe.common.ifaces;

import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Parameters;
import com.skt.pe.common.service.XMLData;

/**
 * @author pluto248
 *
 */
public interface PrimitiveHandler {
	public String getPrimitiveName();
	public int getDialogMessage();
	public String getUrlPath();
	public Parameters getParameters();
	public void convertXML(XMLData xmldata) throws SKTException;
}
