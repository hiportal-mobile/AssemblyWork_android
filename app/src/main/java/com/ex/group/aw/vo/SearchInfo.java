package com.ex.group.aw.vo;



public class SearchInfo
{
	public String ord                  ;           		// 1차/2차 구분(1:1차, 2:2차) 
	public String mas_rec_no           ;             	// 관리번호
	public String main_no              ;             	// 대항목 번호
	public String sub_no               ;             	// 세부항목 번호
	public String task_mas_uid         ;             	// 국회업무 마스터 키
	public String task_dist_main_uid   ;             	// 대항목 배부정보 키
	public String task_dist_sub_uid    ;             	// 세부항목 배부정보 키
	public String assem_mbr_uids       ;             	// 요청자 키(국회의원 키)
	public String assem_mbr_names      ;             	// 요청자 이름(국회의원명)
	public String tit                  ;           	 	// 대항목/세부항목 제목
	public String main_rec_user_names  ;             	// 대항목/세부항목 작성자 이름
	public String mas_bgn_date         ;             	// 요청일(YYYY-MM-DD)
	public String mas_end_date         ;             	// 제출기한(YYYY-MM-DD)
	public String status               ;             	// 대항목/세부항목 상태
		
}
