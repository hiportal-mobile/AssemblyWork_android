package com.skt.pe.common.service;

import java.io.File;

import com.skt.pe.common.exception.ParsingException;


public interface IParser {
	
	public IDao parse(XMLData xmlData) throws ParsingException;
	
	public IDao parser(File conf) throws ParsingException;

}
