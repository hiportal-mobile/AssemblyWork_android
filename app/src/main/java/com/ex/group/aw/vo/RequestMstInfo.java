package com.ex.group.aw.vo;

/*
 * 자료 요청 리스트  - 마스터
 * */
public class RequestMstInfo
{
	public String task_mas_uid;						//	국회업무 마스터 키           
	public String mas_type_code;                    //	국회업무 마스터 유형         
	public String mas_rec_no;                       //	국회업무 마스터 관리번호          
	public String assem_mbr_uids;                   //	요청자 키(국회의원)        
	public String assem_mbr_names;                  //	요청자 이름(국회의원)      
	public String mas_bgn_date;                     //	요청일                  
	public String mas_end_date;                     //	제출기한                 
	public String rel_assem_mbr_uids;               //	관련 의원 키        
	public String rel_assem_mbr_names;              //	관련 의원 이름      
	public String ex_center_uids;                   //	해당본부 키             
	public String ex_center_names;                  //	해당본부 이름           
	public String assem_mbr_party_codes;            //	소속정당 코드     
	public String assem_mbr_party_names;            //	소속정당 명      
	public String status;                           //	취합현황                       
	public String status11_yn;                      //	처리현황 조회 가능 여부(1차 배부전) 
	public String status11;                         //	처리현황 (1차 배부전)            
	public String status12_yn;                      //	처리현황 조회 가능 여부(1차 배부)  
	public String status12;                         //	처리현황 (1차 배부)             
	public String status13_yn;                      //	처리현황 조회 가능 여부(1차 재검토) 
	public String status13;                         //	처리현황 (1차 재검토)            
	public String status21_yn;                      //	처리현황 조회 가능 여부(2차 작성전) 
	public String status21;                         //	처리현황 (1차 작성전)            
	public String status22_yn;                      //	처리현황 조회 가능 여부(2차 재검토) 
	public String status22;                         //	처리현황 (2차 재검토)            
	public String status23_yn;                      //	처리현황 조회 가능 여부(2차 작성중) 
	public String status23;                         //	처리현황 (2차 작성중)            
	public String status24_yn;                      //	처리현황 조회 가능 여부(2차 결재중) 
	public String status24;                         //	처리현황 (2차 결재중)            
	public String status25_yn;                      //	처리현황 조회 가능 여부(2차 결재완료)
	public String status25;                         //	처리현황 (2차 결재완료)
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(status11_yn).append(',');
		buffer.append(status11).append(',');
		buffer.append(status12_yn).append(',');
		buffer.append(status12).append(',');
		buffer.append(status13_yn).append(',');
		buffer.append(status13).append(',');
		buffer.append(status21_yn).append(',');
		buffer.append(status21).append(',');
		buffer.append(status22).append(',');
		buffer.append(status23_yn).append(',');
		buffer.append(status23).append(',');
		buffer.append(status24_yn).append(',');
		buffer.append(status24).append(',');
		buffer.append(status25_yn).append(',');
		buffer.append(status25);
	
		return buffer.toString();
	}
}
