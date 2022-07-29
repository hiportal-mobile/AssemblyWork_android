package com.skt.pe.common.service;

import java.io.Serializable;

public interface IDao extends Serializable {

	public Parameters setParameter(int len, String... param);
	
}
