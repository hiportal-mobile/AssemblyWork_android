package com.ex.group.aw.vo;

public class Global
{
	
//	테스트 
//	public final static String SERVER_URL 		= 	"http://172.6.16.37:8088/emp_ex/service.pe";
////	public final static String FILE_SERVER_URL 	= 	"http://172.6.16.38:8088/emp_sf/upload.pe";
//	public final static String FILE_SERVER_URL 	= 	"http://128.200.121.68:9000/emp_ex/upload.pe";
////	public final static String DZCSURL			=	"http://172.6.16.38:8088";
//	public final static String DZCSURL			=	"http://128.200.121.68:9000";

	//	도로공사 운영
	public final static String SERVER_URL 		= 	"http://128.200.121.68:9000/emp_ex/service.pe";
	public final static String FILE_SERVER_URL 	= 	"http://128.200.121.68:9000/emp_ex/upload.pe";
	public final static String DZCSURL			=	"http://128.200.121.68:9000";

	public final static String  DZCSURLPREFIX	=	"toiphoneapp://callDocumentFunction?";
	public final static String PRIMITIVE 				= 		"primitive"					;
	public final static String JSP_DZC					=		"COMMON_MO_ISSCONVERT"		;
	public final static String JSP_STCHRT 				= 		"AW_MO_AWSMSTCHRTR"			;		//"AwSm_StChrt_R.jsp";					//	좌석배치도		o
	public final static String JSP_SMQNA 				= 		"AW_MO_AWSMQALISTR"			;		//"AwSm_QAList_R.jsp";					//	의원질의답변		o
	public final static String JSP_SM_IMAGELIST 		= 		"AW_MO_AWSMIMAGELISTR"		;		//"AwSm_ImageList_R.jsp";				// 	스마트국회 이미지 리스트	
	
	public final static String JSP_ASSEMBLY_LIST 		= 		"AW_MO_AWGTCNGRSSMNLISTRNEW"	;		//"AwGt_CngrssmnList_R.jsp";			//	의원리스트		o
	public final static String JSP_ASSEMBLY_DTL 		= 		"AW_MO_AWGTCNGRSSMNDTLRNEW"	;		//"AwGt_CngrssmnDtl_R.jsp";				//	의원상세		 			2013.06.12
	public final static String JSP_SEARCH_REQ 			= 		"AW_MO_AWJGSRCHLISTR"		;		//"AwJg_SrchList_R.jsp"; 				//	자료검색 - 리스트 o
	public final static String JSP_SEARCH_DETAIL 		= 		"AW_MO_AWJGSRCHDTLR"		;		//"AwJg_SrchDtl_R.jsp";					//	자료검색 - 상세페이지 o
	public final static String JSP_REG_ASSEMBLY 		= 		"AW_MO_AWJYREQUESTMBRC"		;		//"AwJy_RequestMbr_C.jsp";				//	의원요청등록	o
	public final static String JSP_SEARCH_REQUESTOR 	= 		"AW_MO_AWJYSRCHMBRNAMER"	;		//"AwJy_SrchMbrName_R.jsp"; 			//	요청자 검색		o
	public final static String JSP_SEARCH_USER 			= 		"AW_MO_AWJYSRCHUSERNAMER"	;		//"AwJy_SrchUserName_R.jsp";			//	사용자(담당자)검색	o	
	public final static String JSP_SEARCH_ASSEMBLY_PART = 		"AW_MO_AWCOMBRPARTYLISTR"	;		//"AwCo_MbrPartyList_R.jsp";			//	정당조회		o
	public final static String JSP_SET_WRITER_R 		= 		"AW_MO_AWJYSRCHWRITERR"		;		//"AwJy_SrchWriter_R.jsp";				//	작성자지정 조회
	public final static String JSP_SET_WRITER_C 		= 		"AW_MO_AWJYINSERTWRITERC"	;		//"AwJy_InsertWriter_C.jsp";			//	작성자지정 등록
	public final static String JSP_REJECT_WRITER 		= 		"AW_MO_AWJYREJECTWRITERU"	;		//"AwJy_RejectWriter_U.jsp";			//	재검토요청		o
	public final static String JSP_FILE_UPLOAD 			= 		"AW_MO_AWCOFILEUPLOAD"		;		//"AwCo_FileUpload.jsp";				//	파일업로드
	public final static String JSP_REQUEST_LIST 		= 		"AW_MO_AWJYRQSTLISTR"		;		//"AwJy_RqstList_R.jsp";				// 	자료요청 목록  o
	//public final static String JSP_MST_REQUEST_LIST 	= 		"AW_MO_AWJYRQSTMSTLISTR"	;		//"AwJy_RqstMstList_R.jsp";				// 	자료요청 마스터 목록	o
	public final static String JSP_MST_REQUEST_LIST 	= 		"AW_MO_ASSEMBLELIST"	;		//"AwJy_RqstMstList_R.jsp";				// 	자료요청 마스터 목록	o
	
	public final static String JSP_GROUP_LIST 			= 		"AW_MO_AWJYSRCHGROUPR"		;		//"AwJy_SrchGroup_R.jsp";				// 	부서검색	o
	public final static String JSP_REL_ASSEMBLY_LIST 	=		"AW_MO_AWJYSRCHRELMBRR"		;		// "AwJy_SrchRelMbr_R.jsp";				// 	의원검색	o
	public final static String JSP_USET_INFO 			= 		"AW_MO_AWCOUSERINFOR"		;		//"AwCo_UserInfo_R.jsp";				// 	사용자정보	
	public final static String MSG_RESULT_CODE 			= 		"result";
	public final static String MSG_RESULT_MESSAGE 		= 		"resultMessage";
	
	
	//	Handler Message
	
	public final static int MSG_FAILED = 0;
	public final static int MSG_SUCEEDED = 1;
	public final static int MSG_SUCEEDED_2 = 2;

	
	public final static int MSG_RESULT_SUCCEEDED 	= 1000;
	public final static int HM_ERR_NETWORK 			= 1002;
	public final static int HM_ERR_XML_PARSING 		= 1003;
	public final static int HM_ERR_XML_RESULT 		= 1004;
	
	public final static int MSG_IMAGE_DOWNLOAD_COMPLETE = 200;
	public final static int MSG_IMAGE_DOWNLOAD_FAIL = 201;
	//login 정보 
	public static String LOGIN_ID	=	"";			//점속자 ID(국회)
//	public static String LOGIN_ID		=	"20500410";			//점속자 ID(국회)
//	public final static String LOGIN_ID	=	"20711512";			//점속자 ID(1차) 박준
}
