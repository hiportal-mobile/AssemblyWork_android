package com.skt.pe.common.exception;

import com.skt.pe.common.conf.Constants;


/**
 * 내부 에러
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class InternalException extends SKTException {

	private static final long serialVersionUID = 1L;

	public InternalException(String errMessage) {
		this(errMessage, Constants.Status.E_INTERNAL);
	}

	public InternalException(String errMessage, String errCode) {
		super(errMessage, errCode);
	}
	
	public InternalException(Exception e) {
		this(e, Constants.Status.E_INTERNAL);
	}
	
	public InternalException(Exception e, String errCode) {
		super(e, errCode);
	}

	public InternalException(Throwable e, String errCode) {
		super(e, errCode);
	}

}
