package com.skt.pe.common.exception;

import com.skt.pe.common.conf.Constants;


/**
 * 파싱 에러
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class ParsingException extends SKTException {

	private static final long serialVersionUID = 1L;

	public ParsingException(String errMessage) {
		this(errMessage, Constants.Status.E_PARSING);
	}

	public ParsingException(String errMessage, String errCode) {
		super(errMessage, errCode);
	}
	
	public ParsingException(Exception e) {
		this(e, Constants.Status.E_PARSING);
	}
	
	public ParsingException(Exception e, String errCode) {
		super(e, errCode);
	}

	public ParsingException(Throwable e, String errCode) {
		super(e, errCode);
	}

}
