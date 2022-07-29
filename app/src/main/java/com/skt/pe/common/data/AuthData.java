package com.skt.pe.common.data;

import com.skt.pe.common.conf.Constants;


/**
 * 인증 데이타 형식
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class AuthData {
	public static final String RET_SUCCESS       = "SUCC";
	public static final String RET_NONE          = Constants.Status.E_AUTH_NULL;
	public static final String RET_AUTH_FAIL     = Constants.Status.E_AUTH_FAIL;
	public static final String RET_AUTH_GMP      = Constants.Status.E_AUTH_GMP;
	public static final String RET_AUTH_LEGACY   = Constants.Status.E_AUTH_LEGACY;

	public static final String ID_ROWID              = "_ID";
	public static final String ID_RESULT             = "RESULT";
	public static final String ID_MDN                = "MDN";
	public static final String ID_APP_ID             = "APPID";
	public static final String ID_SECRET_KEY         = "SECRETKEY";
	public static final String ID_NONCE              = "NONCE";
	public static final String ID_AUTH_KEY           = "AUTHKEY";
	public static final String ID_COMPANY_CD         = "COMPANY_CD";
	public static final String ID_ENC_PWD            = "ENC_PWD";
	public static final String ID_GMP_AUTH           = "GMP_AUTH";
	public static final String ID_GMP_AUTH_PWD       = "GMP_AUTH_PWD";
	public static final String ID_CHECKED_COMPANY_CD = "CHECKED_COMPANY_CD";
	public static final String ID_COMPANY_LIST       = "COMPANY_LIST";

	//CJ 추가
	public static final String ID_ID   = "ID";
	public static final String ID_PWD  = "PWD";
	public static final String ID_MAC  = "MAC";
	public static final String ID_LOGIN_DT = "LOGIN_DT";

}
