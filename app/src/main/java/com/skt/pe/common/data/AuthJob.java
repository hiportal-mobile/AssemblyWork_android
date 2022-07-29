package com.skt.pe.common.data;

import com.skt.pe.common.exception.ConfigException;


/**
 * 계정 정보
 * @author june
 *
 */
public class AuthJob {
	String companyCd = "";
	String companyNm = "";
	String encPwd    = "";

	public AuthJob(String companyCd, String companyNm, String encPwd) {
		this.companyCd = companyCd;
		this.companyNm = companyNm;
		this.encPwd    = encPwd;
	}

	public AuthJob(String jobValue) {
		if(jobValue == null)
			throw new ConfigException("Job-Value is invalid!");

		String buffer = jobValue;
		int offset = buffer.indexOf("|");
		if(offset < 0) {
			throw new ConfigException("Job-CompanyCd-Value is invalid!");
		}
		
		this.companyCd = buffer.substring(0, offset);
		
		buffer = buffer.substring(offset + 1);
		offset = buffer.indexOf("|");
		if(offset < 0) {
			throw new ConfigException("Job-CompanyNm-Value is invalid!");
		}
		
		this.companyNm = buffer.substring(0, offset);
		this.encPwd = buffer.substring(offset + 1);
	}
	
	public String getCompanyCd() {
		return companyCd;
	}
	public void setCompanyCd(String companyCd) {
		this.companyCd = companyCd;
	}
	public String getCompanyNm() {
		return companyNm;
	}
	public void setCompanyNm(String companyNm) {
		this.companyNm = companyNm;
	}
	public String getEncPwd() {
		return encPwd;
	}
	public void setEncPwd(String encPwd) {
		this.encPwd = encPwd;
	}
	
	public String toString() {
		return companyCd + "|" + companyNm + "|" + encPwd;
	}

}
