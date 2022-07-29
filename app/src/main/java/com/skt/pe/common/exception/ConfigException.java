package com.skt.pe.common.exception;

public class ConfigException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Exception err) {
		super(err);
	}

}
