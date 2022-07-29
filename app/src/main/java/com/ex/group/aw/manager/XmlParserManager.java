package com.ex.group.aw.manager;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Xml;

import com.ex.group.aw.util.CommonUtils;
import com.ex.group.aw.vo.AssemblyPartInfo;
import com.ex.group.aw.vo.AssemblySearchInfo;
import com.ex.group.aw.vo.Global;
import com.ex.group.aw.vo.GroupInfo;
import com.ex.group.aw.vo.PersonDetailInfo;
import com.ex.group.aw.vo.PersonInfo;
import com.ex.group.aw.vo.RelAssemblyInfo;
import com.ex.group.aw.vo.RequestInfo;
import com.ex.group.aw.vo.RequestMstInfo;
import com.ex.group.aw.vo.RequestWriterFileInfo;
import com.ex.group.aw.vo.RequestWriterInfo;
import com.ex.group.aw.vo.SearchDtlInfo;
import com.ex.group.aw.vo.SearchDtlUserInfo;
import com.ex.group.aw.vo.SearchInfo;
import com.ex.group.aw.vo.SmImageListInfoIp;
import com.ex.group.aw.vo.SmQnaInfo;
import com.ex.group.aw.vo.SmQnaInfoIp;
import com.ex.group.aw.vo.SmStchrtInfo;
import com.ex.group.aw.vo.UserInfo;

public class XmlParserManager
{
	private final static String LOGTAG = "XmlParserManager";
	public XmlParserManager()
	{

	}
	//국토해양위원회
	public static void  parsingAsmList(Context context, String xml , PersonManager manager)
	{
		Log.d("","XmlParserManager parsingAsmList start!");
		try {
			String tag = "";

			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	
			PersonInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new PersonInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return;
					
					
					Log.i(LOGTAG, tag+"  xml: " +parser.getText());

					if(tag.equals("assem_mbr_uid")) info.assem_mbr_uid = parser.getText();
					if(tag.equals("mbr_name")) info.mbr_name = parser.getText();
					if(tag.equals("mbr_party_code")) info.mbr_party_code = parser.getText();
					if(tag.equals("mbr_party_name")) info.mbr_party_name = parser.getText();
					if(tag.equals("mbr_commit_code")) info.mbr_commit_code = parser.getText();
					if(tag.equals("mbr_commit_name")) info.mbr_commit_name = parser.getText();
					if(tag.equals("mbr_region")) info.mbr_region = parser.getText();
					if(tag.equals("mbr_email")) info.mbr_email = parser.getText();
					if(tag.equals("mbr_tel")) info.mbr_tel = parser.getText();
					if(tag.equals("mbr_info_url")) info.mbr_info_url = parser.getText();
					if(tag.equals("mbr_home_url")) info.mbr_home_url = parser.getText();
					if(tag.equals("mbr_use_yn")) info.mbr_use_yn = parser.getText().equals("Y") ? true : false;
					if(tag.equals("mbr_ins_user_uid")) info.mbr_ins_user_uid = parser.getText();
					if(tag.equals("mbr_ins_date")) info.mbr_ins_date = parser.getText();
					if(tag.equals("mbr_upd_user_uid")) info.mbr_upd_user_uid = parser.getText();
					if(tag.equals("mbr_upd_date")) info.mbr_upd_date = parser.getText();
					if(tag.equals("mbr_file_name")) info.mbr_file_name = parser.getText();
					if(tag.equals("mbr_file_savename")) info.mbr_file_savename = parser.getText();
					if(tag.equals("mbr_file_src")) info.mbr_file_src = parser.getText();
					if(tag.equals("file_src")) info.file_src = parser.getText();
					if(tag.equals("photo_url")) 
					{
						info.photo_url = parser.getText();
						
						/*String encoTemp		=	URLEncoder.encode(info.photo_url);
						String encoFilename	=	info.mbr_file_name;
						Bitmap bitmap = CommonUtils.getRemoteImage(encoTemp,encoFilename);
						
						if(bitmap != null)
						{
							byte[] imageBinary = CommonUtils.BitmapToByte(bitmap);							
							info.drawable = CommonUtils.ByteToDrawble(imageBinary);
							Log.d("","dddddddddddddd 1  " + info.mbr_name);
						}else{
							Log.d("","dddddddddddddd  2 " + info.mbr_name);
						}*/
						
						
					}

					
					if(tag.equals("total_count")){
						if( manager.total_count == 0 ) manager.total_count = Integer.parseInt(parser.getText());
					}

					if(tag.equals("list_record_count")){
						if( manager.count == 0 ) manager.count = Integer.parseInt(parser.getText());
					}

					if(tag.equals("result")){
						if(manager.result_code==0) manager.result_code = Integer.parseInt(parser.getText());
						//						Log.i(LOGTAG , "1xml_result = "+parser.getText());
					}

					if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();

					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
		Log.d("","XmlParserManager parsingAsmList end!");
		return;
	}

	//국토해양위원회 - 의원상세페이지
		public static void  parsingAsmDetail(Context context, String xml , PersonDetailManager manager)
		{
			try {
				String tag = "";

				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(new StringReader(xml));

				int parserEvent = parser.getEventType();	    	
				PersonDetailInfo info = null;

				while (parserEvent != XmlPullParser.END_DOCUMENT) 
				{
					switch (parserEvent) 
					{
					case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
						tag = parser.getName();
						if( tag.compareTo("record") == 0)
						{
							info = new PersonDetailInfo();
						}

						break;
					case XmlPullParser.TEXT:
						if(tag == null) return;
						Log.i(LOGTAG, tag+"  xml: " +parser.getText());
						//manager 
						if(tag.equals("assem_mbr_uid")) manager.assem_mbr_uid = parser.getText();
						if(tag.equals("mbr_name")) manager.mbr_name = parser.getText();
						if(tag.equals("mbr_party_code")) manager.mbr_party_code = parser.getText();
						if(tag.equals("mbr_party_name")) manager.mbr_party_name = parser.getText();
						if(tag.equals("mbr_commit_code")) manager.mbr_commit_code = parser.getText();
						if(tag.equals("mbr_commit_name")) manager.mbr_commit_name = parser.getText();
						if(tag.equals("mbr_region")) manager.mbr_region = parser.getText();
						if(tag.equals("mbr_email")) manager.mbr_email = parser.getText();
						if(tag.equals("mbr_tel")) manager.mbr_tel = parser.getText();
						if(tag.equals("mbr_info_url")) manager.mbr_info_url = parser.getText();
						if(tag.equals("mbr_home_url")) manager.mbr_home_url = parser.getText();
						if(tag.equals("mbr_birth")) manager.mbr_birth = parser.getText();
						if(tag.equals("mbr_career")) manager.mbr_career = parser.getText();				//주요경력
						if(tag.equals("mbr_hall")) manager.mbr_hall = parser.getText();					//의원회관
						if(tag.equals("mbr_use_yn")) manager.mbr_use_yn = parser.getText().equals("Y") ? true : false;
						if(tag.equals("mbr_ins_user_uid")) manager.mbr_ins_user_uid = parser.getText();
						if(tag.equals("mbr_ins_date")) manager.mbr_ins_date = parser.getText();
						if(tag.equals("mbr_upd_user_uid")) manager.mbr_upd_user_uid = parser.getText();
						if(tag.equals("mbr_upd_date")) manager.mbr_upd_date = parser.getText();
						if(tag.equals("mbr_file_name")) manager.mbr_file_name = parser.getText();
						if(tag.equals("mbr_file_savename")) manager.mbr_file_savename = parser.getText();
						if(tag.equals("mbr_file_src")) manager.mbr_file_src = parser.getText();
						
						//info 
						if(tag.equals("aide_name")) info.aide_name = parser.getText();
						if(tag.equals("aide_tel")) info.aide_tel = parser.getText();
						if(tag.equals("aide_email")) info.aide_email = parser.getText();
						
						if(tag.equals("photo_url")) 
						{
							manager.photo_url = parser.getText();
							
							String encoTemp		=	URLEncoder.encode(manager.photo_url);
							String encoFilename	=	manager.mbr_file_name;
							Bitmap bitmap = CommonUtils.getRemoteImage(encoTemp,encoFilename);
							
							if(bitmap != null)
							{
								byte[] imageBinary = CommonUtils.BitmapToByte(bitmap);							
								manager.drawable = CommonUtils.ByteToDrawble(imageBinary);	
							}
							
						}

						if(tag.equals("list_record_count")){
							if( manager.count == 0 ) manager.count = Integer.parseInt(parser.getText());
						}

						if(tag.equals("result")){
							if(manager.result_code==0) manager.result_code = Integer.parseInt(parser.getText());
							//						Log.i(LOGTAG , "1xml_result = "+parser.getText());
						}

						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();

						break;
					case XmlPullParser.END_TAG:
						tag = parser.getName();
						if( tag.equals("record") )
						{
							manager.getList().add(info);
							info = null;
						}

						break;
					}

					try {
						parserEvent = parser.next();
					} catch (IOException e) {
						String err	=	e.toString();
						//e.printStackTrace();
						Log.e(LOGTAG, "오류가 발생하였습니다.");
					}
				}

			} catch (XmlPullParserException e) {
				//e.printStackTrace();
				String err	=	e.toString();
				Log.e(LOGTAG, "오류가 발생하였습니다.");
			}

			return;
		}


	//스마트 국감  > 좌석배치도
	public static SmStchrtManager parsingStChrt(Context context, String xml)
	{
		XmlPullParser parser;
		SmStchrtManager manager =null;
		try {
			manager	=	new SmStchrtManager();
			String tag = "";
			parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();
			SmStchrtInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						// 각 리스트에 해당하는 객체에 값 담기
						info = new SmStchrtInfo();
					}
					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;
					if(!parser.getText().toString().trim().replace("\n", "").equals("")){
						Log.i(LOGTAG, "tagName= :"+tag);
						Log.i(LOGTAG, "value="+parser.getText());
						if(tag.equals("mas_tit")) manager.mas_tit = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("mas_man_cnt")) manager.mas_man_cnt = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("col_count")) manager.col_count = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("row_count")) manager.row_count = parser.getText() == null ? "" : parser.getText();
						
						if(tag.equals("row_num")) info.row_num = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("col_num")) info.col_num= parser.getText() == null ? "" : parser.getText();
						if(tag.equals("mbr_name")) info.mbr_name= parser.getText() == null ? "" : parser.getText();
						if(tag.equals("assem_mbr_uid")) info.assem_mbr_uid= parser.getText() == null ? "" : parser.getText();
						if(tag.equals("smrt_arr_uid")) info.smrt_arr_uid= parser.getText() == null ? "" : parser.getText();
						if(tag.equals("smrt_mas_uid")) info.smrt_mas_uid= parser.getText() == null ? "" : parser.getText();
						if(tag.equals("qa_yn")) info.qa_yn= parser.getText() == null ? "" : parser.getText();
						if(tag.equals("file_name")) info.file_name= parser.getText() == null ? "" : parser.getText();
						if(tag.equals("file_src")) 
						{
							info.file_src = parser.getText();
							
							String encoTemp		=	URLEncoder.encode("http://aams.ex.co.kr:8084"+info.file_src);
							String encoFilename	=	info.file_name;
							Bitmap bitmap = CommonUtils.getRemoteImage(encoTemp,encoFilename);
							
							if(bitmap != null)
							{
								byte[] imageBinary = CommonUtils.BitmapToByte(bitmap);							
								info.drawable = CommonUtils.ByteToDrawble(imageBinary);	
							}
							
						}
						
						if(tag.equals("list_record_count")) {
							if(manager.count == 0)manager.count=Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_CODE)){ 
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					}
					break;

				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						// 각 리스트에 해당하는 객체에 값 담기
							manager.getList().add(info);
							info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}
		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return manager;
	}
	
	//스마트 국감  > 의원질의 답변 
		public static SmQnatManager parsingSmQna(Context context, String xml)
		{
			XmlPullParser parser;
			SmQnatManager manager =null;
			try {
				manager	=	new SmQnatManager();
				String tag = "";
				parser	=	Xml.newPullParser();
				parser.setInput(new StringReader(xml));

				int parserEvent = parser.getEventType();
				
				SmQnaInfo info = null;
				SmQnaInfoIp info2 = null;
				
				String twoDepthName = "";  // 2depth 태그명
				
				while (parserEvent != XmlPullParser.END_DOCUMENT) 
				{
					
					// 2depth인 경우, 태그명 담아두기
					if(2 ==parser.getDepth()) {
						twoDepthName = parser.getName();
					}
					
					switch (parserEvent) 
					{
					case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
						tag = parser.getName();
						if( tag.compareTo("record") == 0)
						{
							// 각 리스트에 해당하는 객체에 값 담기
							if("list".equals(twoDepthName)) {
								info = new SmQnaInfo();
							} else if("list2".equals(twoDepthName)) {
								info2 = new SmQnaInfoIp();
							}
						}
						break;
					case XmlPullParser.TEXT:
						if(tag == null) return null;
						if(!parser.getText().toString().trim().replace("\n", "").equals("")){
							Log.i(LOGTAG, "tagName= :"+tag);
							Log.i(LOGTAG, "value="+parser.getText());
							if(tag.equals("assem_mbr_uid")) manager.assem_mbr_uid = parser.getText() == null ? "" : parser.getText();
							if(tag.equals("mbr_name")) manager.mbr_name = parser.getText() == null ? "" : parser.getText();
							if(tag.equals("mbr_party_name")) manager.mbr_party_name = parser.getText() == null ? "" : parser.getText();
							if(tag.equals("mbr_region")) manager.mbr_region = parser.getText() == null ? "" : parser.getText();
							if(tag.equals("mbr_file_src")) manager.mbr_file_src = parser.getText() == null ? "" : parser.getText();
							if(tag.equals("mas_doc_dirt_yn")) manager.mas_doc_dirt_yn= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("mas_dirt_tab_name")) manager.mas_dirt_tab_name= parser.getText() == null ? "" : parser.getText();
							
							if(tag.equals("mbr_file_src")){
								Bitmap bitmap	=	CommonUtils.getRemoteImage(URLEncoder.encode(parser.getText()), "image.jpg");
								if(bitmap != null)
								{
									byte[] imageBinary = CommonUtils.BitmapToByte(bitmap);							
									manager.mbr_photo = CommonUtils.ByteToDrawble(imageBinary);	
								}	
							}
							
							if(tag.equals("sub_ord_no")) info.sub_ord_no = parser.getText() == null ? "" : parser.getText();
							if(tag.equals("depth")) info.depth= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("tit")) info.tit= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("child_yn")) info.child_yn= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("file_src")) info.file_src= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("file_name")) info.file_name= parser.getText() == null ? "" : parser.getText();
							
							if(tag.equals("sub_ord_no2")) info2.sub_ord_no2 = parser.getText() == null ? "" : parser.getText();
							if(tag.equals("depth2")) info2.depth2= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("tit2")) info2.tit2= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("child_yn2")) info2.child_yn2= parser.getText() == null ? "" : parser.getText();
							if(tag.equals("master_doc_id2")) info2.master_doc_id2= parser.getText() == null ? "" : parser.getText();
							
							
							if(tag.equals("list_record_count")) {
								if(manager.count == 0)manager.count=Integer.parseInt(parser.getText().trim());
							}
							if(tag.equals(Global.MSG_RESULT_CODE)){ 
								if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
							}
							if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
						}
						break;

					case XmlPullParser.END_TAG:
						tag = parser.getName();
						if( tag.equals("record") )
						{
							if("list".equals(twoDepthName)) {
								manager.getList().add(info);
								info = null;
							} else if("list2".equals(twoDepthName)) {
								manager.getList2().add(info2);
								info2 = null;
							}
						}

						break;
					}

					try {
						parserEvent = parser.next();
					} catch (IOException e) {
						String err	=	e.toString();
						//e.printStackTrace();
						Log.e(LOGTAG, "오류가 발생하였습니다.");
					}
				}
			} catch (XmlPullParserException e) {
				//e.printStackTrace();
				String err	=	e.toString();
				Log.e(LOGTAG, "오류가 발생하였습니다.");
			}

			return manager;
		}
		
		//스마트 국감  > 이미지리스트 
				public static SmImageListManager parsingSmImageList(Context context, String xml)
				{
					XmlPullParser parser;
					SmImageListManager manager =null;
					try {
						manager	=	new SmImageListManager();
						String tag = "";
						parser	=	Xml.newPullParser();
						parser.setInput(new StringReader(xml));

						int parserEvent = parser.getEventType();
						
						SmImageListInfoIp info = null;
						
						
						while (parserEvent != XmlPullParser.END_DOCUMENT) 
						{
							switch (parserEvent) 
							{
							case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
								tag = parser.getName();
								if( tag.compareTo("record") == 0)
								{
									info = new SmImageListInfoIp();
								}
								break;
							case XmlPullParser.TEXT:
								if(tag == null) return null;
								if(!parser.getText().toString().trim().replace("\n", "").equals("")){
									Log.i(LOGTAG, "tagName= :"+tag);
									Log.i(LOGTAG, "value="+parser.getText());
									if(tag.equals("file_src")) info.file_src = parser.getText() == null ? "" : parser.getText();
									if(tag.equals("file_name")) info.file_name= parser.getText() == null ? "" : parser.getText();
									
									if(tag.equals("file_src")){
										Bitmap bitmap	=	CommonUtils.getRemoteImageQna(URLEncoder.encode(info.file_src), info.file_name);
										if(bitmap != null)
										{
											byte[] imageBinary = CommonUtils.BitmapToByte(bitmap);							
											info.drawable = CommonUtils.ByteToDrawble(imageBinary);	
	
										}	
										//bitmap.recycle();
									}
									
									if(tag.equals("list_record_count")) {
										if(manager.count == 0)manager.count=Integer.parseInt(parser.getText().trim());
									}
									if(tag.equals(Global.MSG_RESULT_CODE)){ 
										if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
									}
									if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
								}
								break;

							case XmlPullParser.END_TAG:
								tag = parser.getName();
								if( tag.equals("record") )
								{
										manager.getList().add(info);
										info = null;
								}

								break;
							}

							try {
								parserEvent = parser.next();
							} catch (IOException e) {
								//e.printStackTrace();
								String err	=	e.toString();
								Log.e(LOGTAG, "오류가 발생하였습니다.");
							}
						}
					} catch (XmlPullParserException e) {
						//e.printStackTrace();
						String err	=	e.toString();
						Log.e(LOGTAG, "오류가 발생하였습니다.");
					}

					return manager;
				}

	//자료요청 목록 - 마스터
	public static void parsingReqMstList(Context context, String xml, RequestMstManager manager)
	{
		XmlPullParser parser;
		try {

			String tag = "";

			parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	
			RequestMstInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new RequestMstInfo();
					}
					break;
				case XmlPullParser.TEXT:
					if(tag == null) return;

					if(!parser.getText().trim().replace("/n", "").equals("")){

						if(tag.equals("task_mas_uid"))info.task_mas_uid=parser.getText();
						if(tag.equals("mas_type_code"))info.mas_type_code=parser.getText();
						if(tag.equals("mas_rec_no"))info.mas_rec_no=parser.getText();
						if(tag.equals("assem_mbr_uids"))info.assem_mbr_uids=parser.getText();
						if(tag.equals("assem_mbr_names"))info.assem_mbr_names=parser.getText();
						if(tag.equals("mas_bgn_date"))info.mas_bgn_date=parser.getText();
						if(tag.equals("mas_end_date"))info.mas_end_date=parser.getText();
						if(tag.equals("rel_assem_mbr_uids"))info.rel_assem_mbr_uids=parser.getText();
						if(tag.equals("rel_assem_mbr_names"))info.rel_assem_mbr_names=parser.getText();
						if(tag.equals("ex_center_uids"))info.ex_center_uids=parser.getText();
						if(tag.equals("ex_center_names"))info.ex_center_names=parser.getText();
						if(tag.equals("assem_mbr_party_codes"))info.assem_mbr_party_codes=parser.getText();
						if(tag.equals("assem_mbr_party_names"))info.assem_mbr_party_names=parser.getText();
						if(tag.equals("status"))info.status=parser.getText();
						if(tag.equals("status11_yn"))info.status11_yn=parser.getText();
						if(tag.equals("status11"))info.status11=parser.getText();
						if(tag.equals("status12_yn"))info.status12_yn=parser.getText();
						if(tag.equals("status12"))info.status12=parser.getText();
						if(tag.equals("status13_yn"))info.status13_yn=parser.getText();
						if(tag.equals("status13"))info.status13=parser.getText();
						if(tag.equals("status21_yn"))info.status21_yn=parser.getText();
						if(tag.equals("status21"))info.status21=parser.getText();
						if(tag.equals("status22_yn"))info.status22_yn=parser.getText();
						if(tag.equals("status22"))info.status22=parser.getText();
						if(tag.equals("status23_yn"))info.status23_yn=parser.getText();
						if(tag.equals("status23"))info.status23=parser.getText();
						if(tag.equals("status24_yn"))info.status24_yn=parser.getText();
						if(tag.equals("status24"))info.status24=parser.getText();
						if(tag.equals("status25_yn"))info.status25_yn=parser.getText();
						if(tag.equals("status25"))info.status25=parser.getText();


						if(tag.equals("list_record_count")){
							if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
						}

						if(tag.equals("total_count")){
							if(manager.total_count==0)manager.total_count = Integer.parseInt(parser.getText().toString().trim());
						}

						if(tag.equals(Global.MSG_RESULT_CODE)){
							Log.i("xmlparser", parser.getText().trim());
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}

						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					}	
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return;
	}

	//자료요청 상세목록
	public static void parsingReqList(Context context, String xml, RequestManager manager)
	{
		XmlPullParser parser;
		try {

			String tag = "";

			parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	
			RequestInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new RequestInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return;

					if(!parser.getText().trim().equals("")){

						if(tag.equals("ord"))info.ord=parser.getText();
						if(tag.equals("mas_rec_no"))info.mas_rec_no=parser.getText();
						if(tag.equals("main_no"))info.main_no=parser.getText();
						if(tag.equals("sub_no"))info.sub_no=parser.getText();
						if(tag.equals("task_mas_uid"))info.task_mas_uid=parser.getText();
						if(tag.equals("task_dist_main_uid"))info.task_dist_main_uid=parser.getText();
						if(tag.equals("task_dist_sub_uid"))info.task_dist_sub_uid=parser.getText();
						if(tag.equals("assem_mbr_uids"))info.assem_mbr_uids=parser.getText();
						if(tag.equals("assem_mbr_names"))info.assem_mbr_names=parser.getText();
						if(tag.equals("tit"))info.tit=parser.getText();
						if(tag.equals("user_names"))info.user_names=parser.getText();
						if(tag.equals("mas_bgn_date"))info.mas_bgn_date=parser.getText();
						if(tag.equals("mas_end_date"))info.mas_end_date=parser.getText();
						if(tag.equals("status_code"))info.status_code=parser.getText();
						if(tag.equals("status"))info.status=parser.getText();

						if(tag.equals("list_record_count")){
							if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
						}

						if(tag.equals("total_count")){
							if(manager.total_count==0){
								manager.total_count = Integer.parseInt(parser.getText().toString().trim());
							}
						}

						if(tag.equals(Global.MSG_RESULT_CODE)){
							Log.i("xmlparser", parser.getText().trim());
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}

						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					}	
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return;
	}


	//자료검색
	public static void parsingSearchList(Context context, String xml , SearchManager manager)
	{
		XmlPullParser parser;
		try {

			String tag = "";

			parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	
			SearchInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new SearchInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return;

					if(!parser.getText().trim().equals("")){

						if(tag.equals("ord"))info.ord=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("mas_rec_no"))info.mas_rec_no=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("main_no"))info.main_no=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("sub_no"))info.sub_no=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("task_mas_uid"))info.task_mas_uid=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("task_dist_main_uid"))info.task_dist_main_uid=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("task_dist_sub_uid"))info.task_dist_sub_uid=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("assem_mbr_uids"))info.assem_mbr_uids=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("assem_mbr_names"))info.assem_mbr_names=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("tit"))info.tit=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("main_rec_user_names"))info.main_rec_user_names=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("mas_bgn_date"))info.mas_bgn_date=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("mas_end_date"))info.mas_end_date=parser.getText()== null ? "" : parser.getText();
						if(tag.equals("status"))info.status=parser.getText()== null ? "" : parser.getText();

						if(tag.equals("list_record_count")){
							if(parser.getText().trim()==null||parser.getText().trim().equals("")){
								manager.count=0;
							}else{
								if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
							}
						}
						if(tag.equals("total_count")){
							if(parser.getText().trim()==null||parser.getText().trim().equals("")){
								manager.total_count=0;
							}else{
								if(manager.total_count==0)manager.total_count = Integer.parseInt(parser.getText().trim());
							}
						}

						if(tag.equals(Global.MSG_RESULT_CODE)){

							if(parser.getText().trim()==null||parser.getText().trim().equals("")){
								manager.result_code=0;
							}else{
								if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
							}
						}

						if(tag.equals(Global.MSG_RESULT_MESSAGE)){ 
							manager.result_msg = parser.getText()==null ? "":parser.getText();
						}
					}
					//Log.i(LOGTAG, tag +":" +parser.getText());
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return;
	}

	//자료검색 - 상세페이지
	public static SearchDtlManager parsingSearchDtl(Context context, String xml)
	{
		//XmlPullParserFactory parserCreator;
		XmlPullParser parser;

		SearchDtlManager manager =null;

		try {
			manager	=	new SearchDtlManager();
			String tag = "";
			parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	
			SearchDtlInfo info = null;
			SearchDtlUserInfo info2 = null;

			String twoDepthName = "";  // 2depth 태그명

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				// 2depth인 경우, 태그명 담아두기
				if(2 ==parser.getDepth()) {
					twoDepthName = parser.getName();
				}
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						// 각 리스트에 해당하는 객체에 값 담기
						if("refile_list".equals(twoDepthName)) {
							info = new SearchDtlInfo();
						} else if("file_list".equals(twoDepthName)) {
							info2 = new SearchDtlUserInfo();
						}
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;

					if(!parser.getText().equals("")){

						if("refile_list".equals(twoDepthName)) {
							if(tag.equals("refile_uuid"))info.refile_uuid=parser.getText()== null ? "" : parser.getText();
							if(tag.equals("refile_path"))info.refile_path=parser.getText()== null ? "" : parser.getText();
							if(tag.equals("refile_name"))info.refile_name=parser.getText()== null ? "" : parser.getText();
							if(tag.equals("refile_count"))info.refile_count=parser.getText();
						}else{
							if(tag.equals("file_uuid"))info2.file_uuid=parser.getText()== null ? "" : parser.getText();
							if(tag.equals("file_path"))info2.file_path=parser.getText()== null ? "" : parser.getText();
							if(tag.equals("file_name"))info2.file_name=parser.getText()== null ? "" : parser.getText();
							if(tag.equals("file_count"))info2.file_count=parser.getText()== null ? "" : parser.getText();
						}

						if(tag.equals("assem_mbr_names")) manager.assem_mbr_names = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("ex_center_names")) manager.ex_center_names = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("mas_bgn_date")) manager.mas_bgn_date = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("hist_ins_date")) manager.hist_ins_date = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("mgre_file_path")) manager.mgre_file_path = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("mgre_file_name")) manager.mgre_file_name = parser.getText() == null ? "" : parser.getText();

						if(tag.equals("main_tit")) manager.main_tit = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("main_rej_memo")) manager.main_rej_memo = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("sub_tit")) manager.sub_tit = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("re_user_name")) manager.re_user_name = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("telephone_no")) manager.telephone_no = parser.getText() == null ? "" : parser.getText();
						if(tag.equals("sub_rel_fld_name_path")) manager.sub_rel_fld_name_path = parser.getText() == null ? "" : parser.getText();

						if(tag.equals("refile_list_count")){
							if(manager.refile_list_count==0)manager.refile_list_count = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals("file_list_count")){
							if(manager.file_list_count==0)manager.file_list_count = Integer.parseInt(parser.getText().trim());
						}

						if(tag.equals(Global.MSG_RESULT_CODE)){ 
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{

						// 각 리스트에 해당하는 객체에 값 담기
						if("refile_list".equals(twoDepthName)) {
							manager.getList().add(info);
							info = null;
						} else if("file_list".equals(twoDepthName)) {
							manager.getList2().add(info2);
							info2 = null;
						}
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return manager;
	}


	//정당 검색
	public static AssemblyPartManager parsingAssemblyPartList(Context context, String xml)
	{
		//XmlPullParserFactory parserCreator;

		AssemblyPartManager manager = null;
		try {
			/* URL
	    	URL url = new URL("");
	    	XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
	    	XmlPullParser parser = parserCreator.newPullParser();

			parser.setInput(url.openStream(), null);
			 */
			String tag = "";

			manager	=	new AssemblyPartManager();

			//parser = context.getResources().getXml(R.xml.assembly_part_list);
			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	
			AssemblyPartInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new AssemblyPartInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;

					if(!(parser.getText().equals(""))){


						if(tag.equals("mbr_party_code"))info.mbr_party_code=parser.getText();
						if(tag.equals("mbr_party_name"))info.mbr_party_name=parser.getText();

						if(tag.equals("list_record_count"))
						{
							if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_CODE)) 
						{
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return manager;
	}

	//담당자 검색
	public static void parsingUserList(Context context, String xml,UserManager manager)
	{


		try {
			String tag = "";

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			//parser = context.getResources().getXml(R.xml.user_search_list);

			int parserEvent = parser.getEventType();	    	
			UserInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new UserInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return ;

					if(tag.equals("uuid"))info.uuid=parser.getText();
					if(tag.equals("login_id"))info.login_id=parser.getText();
					if(tag.equals("name_ko"))info.name_ko=parser.getText();
					if(tag.equals("position_code"))info.position_code=parser.getText();
					if(tag.equals("position_name_ko"))info.position_name_ko=parser.getText();
					if(tag.equals("group_uuid"))info.group_uuid=parser.getText();
					if(tag.equals("group_name_ko"))info.group_name_ko=parser.getText();
					if(tag.equals("auth"))info.auth=parser.getText();
					
					if(tag.equals("total_count")){
						if(manager.total_count==0)manager.total_count = Integer.parseInt(parser.getText().trim());
					}
					
					if(tag.equals("list_record_count")){
						if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals(Global.MSG_RESULT_CODE)){
						if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return;
	}

	//요청자(의원)검색
	public static AssemblySearchManager parsingAssemblyList(Context context, String xml)
	{
		AssemblySearchManager manager = null;
		try {

			String tag = "";

			manager =new AssemblySearchManager();

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));
			//parser = context.getResources().getXml(R.xml.assembly_search_list);

			int parserEvent = parser.getEventType();	    	
			AssemblySearchInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new AssemblySearchInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;
				if(!parser.getText().trim().replace("\n", "").equals("")){
					Log.d(LOGTAG, "tag = "+tag);
					Log.d(LOGTAG, "value="+parser.getText());
					if(tag.equals("assem_mbr_uid"))info.assem_mbr_uid=parser.getText().trim();
					if(tag.equals("mbr_name"))info.mbr_name=parser.getText().trim();
					if(tag.equals("mbr_party_code"))info.mbr_party_code=parser.getText();
					if(tag.equals("mbr_party_name"))info.mbr_party_name=parser.getText();
					if(tag.equals("mbr_email"))info.mbr_email=parser.getText();
					if(tag.equals("mbr_tel"))info.mbr_tel=parser.getText();
					if(tag.equals("assem_aide_uid"))info.assem_aide_uid=parser.getText();
					if(tag.equals("aide_name"))info.aide_name=parser.getText();
					if(tag.equals("aide_tel"))info.aide_tel=parser.getText();
					if(tag.equals("aide_email"))info.aide_email=parser.getText();

					if(tag.equals("m_mbr_name"))manager.m_mbr_name = parser.getText();
					if(tag.equals("m_assem_mbr_uid"))manager.m_assem_mbr_uid = parser.getText();

					if(tag.equals("list_record_count")){
						if(manager.count==0)manager.count = Integer.parseInt(parser.getText().replace("\n", "").replace(" ", ""));
					}
					if(tag.equals(Global.MSG_RESULT_CODE)){
						if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
					}

					if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
				}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
		return manager;
	}

	//자료요청 - 작성자 지정 조회
	public static RequestWriterInfoManager parsingReqWriterInfo(Context context, String xml)
	{
		//XmlPullParserFactory parserCreator;

		RequestWriterInfoManager manager = null;

		try {
			String tag = "";

			manager	=	new RequestWriterInfoManager();

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	
			RequestWriterInfo info = null;
			RequestWriterFileInfo info2 = null;

			String twoDepthName = "";  // 2depth 태그명

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				// 2depth인 경우, 태그명 담아두기
				if(2 ==parser.getDepth()) {
					twoDepthName = parser.getName();
				}

				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						// 각 리스트에 해당하는 객체에 값 담기
						if("list".equals(twoDepthName)) {
							info = new RequestWriterInfo();
						} else if("file_list".equals(twoDepthName)) {
							info2 = new RequestWriterFileInfo();
						}

					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;
					if("list".equals(twoDepthName)) {
						if(tag.equals("task_dist_sub_uid"))info.task_dist_sub_uid=parser.getText(); 
						if(tag.equals("sub_tit"))info.sub_tit=parser.getText();
						if(tag.equals("sub_req_wrt_uid"))info.sub_req_wrt_uid=parser.getText();
						if(tag.equals("sub_req_wrt_name"))info.sub_req_wrt_name=parser.getText();
						if(tag.equals("sub_sec_code"))info.sub_sec_code=parser.getText();
					} else if("file_list".equals(twoDepthName)) {
						if(tag.equals("file_uuid"))info2.file_uuid=parser.getText(); 
						if(tag.equals("file_name"))info2.file_name=parser.getText();
						if(tag.equals("file_path"))info2.file_path=parser.getText();

						Log.i(LOGTAG, "file_uuid = "+info2.file_uuid);
						Log.i(LOGTAG, "file_name = "+info2.file_name);
					}

					if(tag.equals("task_mas_uid")) manager.task_mas_uid = parser.getText().trim();
					if(tag.equals("mas_type_code")) manager.mas_type_code = parser.getText().trim();
					if(tag.equals("assem_mbr_names")) manager.assem_mbr_names = parser.getText().trim();
					if(tag.equals("assem_aide_names")) manager.assem_aide_names = parser.getText().trim();
					if(tag.equals("mas_bgn_date")) manager.mas_bgn_date = parser.getText().trim();
					if(tag.equals("mas_end_date")) manager.mas_end_date = parser.getText().trim();
					
					if(tag.equals("rel_assem_mbr_uids")) manager.rel_assem_mbr_uids = parser.getText().trim();
					if(tag.equals("rel_assem_mbr_names")) manager.rel_assem_mbr_names = parser.getText().trim();
					if(tag.equals("ex_center_uids")) manager.ex_center_uids = parser.getText().trim();
					if(tag.equals("ex_center_names")) manager.ex_center_names = parser.getText().trim();
					
					if(tag.equals("task_dist_main_uid")) manager.task_dist_main_uid = parser.getText().trim();
					if(tag.equals("main_seq_no")) manager.main_seq_no = parser.getText().trim();
					if(tag.equals("main_no")) manager.main_no = parser.getText().trim();
					if(tag.equals("main_tit")) manager.main_tit = parser.getText().trim();
					if(tag.equals("main_req_memo")) manager.main_req_memo = parser.getText().trim();
					if(tag.equals("main_rec_user_names")) manager.main_rec_user_names = parser.getText().trim();
					if(tag.equals("main_qst_yn")) manager.main_qst_yn = parser.getText().trim();
					if(tag.equals("main_sec_code")) manager.main_sec_code = parser.getText().trim();

					if(tag.equals("list_record_count")){
						if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals("file_list_record_count")){
						if(parser.getText().trim().equals("")){
							manager.count2=0;
						}else{
							if(manager.count2==0)manager.count2 = Integer.parseInt(parser.getText().trim());

						}
					}
					if(tag.equals(Global.MSG_RESULT_CODE)){
						if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());

					}
					if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") ) 
					{
						// 각 리스트에 해당하는 객체에 값 담기
						if("list".equals(twoDepthName)) {
							manager.getList().add(info);
							info = null;
						} else if("file_list".equals(twoDepthName)) {
							manager.getList2().add(info2);
							info2 = null;
						}
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					String err	=	e.toString();
					//e.printStackTrace();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return manager;
	}

	//자료요청 - 작성자 지정  - 재검토요청 
	public static RejectWriteManager parsingRejectWrite(Context context, String xml)
	{
		RejectWriteManager manager = null;
		try {
			String tag = "";

			manager =new RejectWriteManager();

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: 		// parser가 시작 태그를 만나면 실행
					tag = parser.getName();

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;

					if(tag.equals(Global.MSG_RESULT_CODE)){
						if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					break;
				}
				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err	=	e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err	=	e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
		return manager;
	}

	//자료요청 - 작성자 지정 등록 
	public static InsertWriterManager parsingInsertWrite(Context context, String xml)
	{
		InsertWriterManager manager = null;
		try {
			String tag = "";

			manager =new InsertWriterManager();

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: 		// parser가 시작 태그를 만나면 실행
					tag = parser.getName();

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;

					if(tag.equals(Global.MSG_RESULT_CODE)){
						if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					break;
				}
				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err = e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err = e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
		return manager;
	}

	//자료요청 - 의원요청등록 
	public static RegAssemblyManager parsingRegAssembly(Context context, String xml)
	{
		//XmlPullParserFactory parserCreator;
		//XmlPullParser parser;
		RegAssemblyManager manager = null;
		try {
			/* URL
				    	URL url = new URL("");
				    	XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
				    	XmlPullParser parser = parserCreator.newPullParser();

						parser.setInput(url.openStream(), null);
			 */
			String tag = "";

			manager =new RegAssemblyManager();
			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));
			//parser = context.getResources().getXml(R.xml.reg_assembly_list);

			int parserEvent = parser.getEventType();	    	

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;
					if(!parser.getText().equals("")){
						//					if(tag.equals("file_list_result_count")){
						//						if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
						//					}
						if(tag.equals(Global.MSG_RESULT_CODE)) {
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					break;
				}
				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err = e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err = e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
		return manager;
	}

	//자료요청 - 파일첨부 
	public static AttachFileManager parsingAttachFile(Context context, String xml)
	{
		//XmlPullParserFactory parserCreator;
		AttachFileManager manager = null;
		try {
			/* URL
					    	URL url = new URL("");
					    	XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
					    	XmlPullParser parser = parserCreator.newPullParser();

							parser.setInput(url.openStream(), null);
			 */
			String tag = "";

			manager =new AttachFileManager();

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;
					if(!parser.getText().equals("")){
						//Log.i(LOGTAG, "[" + tag + ":" + parser.getText() +"]");
						if(tag.equals("file_savename")) manager.file_savename = parser.getText().trim();
						if(tag.equals("file_name")) manager.file_name = parser.getText().trim();
						if(tag.equals("file_size")) manager.file_size = parser.getText().trim();

						if(tag.equals(Global.MSG_RESULT_CODE)){
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					}
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					break;
				}
				try {
					parserEvent = parser.next();
				} catch (IOException e) {
//					e.printStackTrace();
					String err = e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
//			e.printStackTrace();
			String err = e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
		return manager;
	}

	//자료요청 - 파일첨부 - 중계서버
	public static HashMap<String, String> parsingAttachFile1(Context context, String xml)
	{
		HashMap<String, String> map	=	new HashMap<String, String>();
		try {
			String tag = "";
			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			int parserEvent = parser.getEventType();	    	

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return null;
					if(tag.equals("fileName")) map.put("fileName",parser.getText().trim()); 
					if(tag.equals("uploadFileKey")) map.put("uploadFileKey", parser.getText().trim());
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					break;
				}
				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					String err = e.toString();
					//e.printStackTrace();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err = e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
		return map;
	}

	//부서 검색(해당 본부)
	public static void parsingGroupList(Context context, String xml,GroupManager manager)
	{


		try {
			String tag = "";

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			//parser = context.getResources().getXml(R.xml.user_search_list);

			int parserEvent = parser.getEventType();	    	
			GroupInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new GroupInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return  ;

					if(tag.equals("group_uuid"))info.group_uuid=parser.getText();
					if(tag.equals("group_code"))info.group_code=parser.getText();
					if(tag.equals("group_name"))info.group_name=parser.getText();
					if(tag.equals("group_code_path"))info.group_code_path=parser.getText();
					if(tag.equals("group_name_path"))info.group_name_path=parser.getText();

					if(tag.equals("list_record_count")){
						if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals("total_count")){
						if(manager.total_count==0)manager.total_count = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals(Global.MSG_RESULT_CODE)){
						if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
					}
					if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err = e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err = e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return  ;
	}
	//과련의원검색 
	public static void parsingRelAssemblyList(Context context, String xml, RelAssemblyManager manager)
	{


		try {
			String tag = "";

			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			//parser = context.getResources().getXml(R.xml.user_search_list);

			int parserEvent = parser.getEventType();	    	
			RelAssemblyInfo info = null;

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();
					if( tag.compareTo("record") == 0)
					{
						info = new RelAssemblyInfo();
					}

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return ;
						if(tag.equals("assem_mbr_uid"))info.assem_mbr_uid=parser.getText();
						if(tag.equals("mbr_name"))info.mbr_name=parser.getText();
						if(tag.equals("mbr_party_code"))info.mbr_party_code=parser.getText();
						if(tag.equals("mbr_party_name"))info.mbr_party_name=parser.getText();
						if(tag.equals("mbr_region"))info.mbr_region=parser.getText();
						if(tag.equals("mbr_commit_name"))info.mbr_commit_name=parser.getText();


						if(tag.equals("m_assem_mbr_uid")) manager.m_assem_mbr_uid = parser.getText();

						if(tag.equals("m_mbr_name")) manager.m_mbr_name = parser.getText();

						if(tag.equals("list_record_count")){
							if(manager.count==0)manager.count = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals("total_count")){
							if(manager.total_count==0)manager.total_count = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_CODE)){
							if(manager.result_code==0)manager.result_code = Integer.parseInt(parser.getText().trim());
						}
						if(tag.equals(Global.MSG_RESULT_MESSAGE)) manager.result_msg = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();
					if( tag.equals("record") )
					{
						manager.getList().add(info);
						info = null;
					}

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err = e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err = e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}

		return  ;
	}

	//사용자 정보 - 로그인 
	public static void parsingLoginUserList(Context context, String xml)
	{

		try {
			String tag = "";

			Log.d("parsingLoginUserList","parsingLoginUserList step1 = " + xml);
			XmlPullParser parser	=	Xml.newPullParser();
			parser.setInput(new StringReader(xml));

			
			//parser = context.getResources().getXml(R.xml.user_search_list);

			int parserEvent = parser.getEventType();	    	

			while (parserEvent != XmlPullParser.END_DOCUMENT) 
			{
				switch (parserEvent) 
				{
				case XmlPullParser.START_TAG: // parser가 시작 태그를 만나면 실행
					tag = parser.getName();

					break;
				case XmlPullParser.TEXT:
					if(tag == null) return;

					if(tag.equals("auth_code")) UserInfoManager.getInstance().auth_code = parser.getText();
					if(tag.equals("smart_yn")) UserInfoManager.getInstance().smart_yn = parser.getText();
					
					if(tag.equals("auth_name")) UserInfoManager.getInstance().auth_name = parser.getText();
					Log.d("parsingLoginUserList","parsingLoginUserList step2 tag= " + tag);
					if(tag.equals(Global.MSG_RESULT_CODE)){
						if(UserInfoManager.getInstance().result_code==0)UserInfoManager.getInstance().result_code = Integer.parseInt(parser.getText().trim());
					}
					Log.d("parsingLoginUserList","parsingLoginUserList step3 resultcode= " + UserInfoManager.getInstance().result_code);
					
					if(tag.equals(Global.MSG_RESULT_MESSAGE)) UserInfoManager.getInstance().result_msg = parser.getText();
					break;
				case XmlPullParser.END_TAG:
					tag = parser.getName();

					break;
				}

				try {
					parserEvent = parser.next();
				} catch (IOException e) {
					//e.printStackTrace();
					String err = e.toString();
					Log.e(LOGTAG, "오류가 발생하였습니다.");
				}
			}

		} catch (XmlPullParserException e) {
			//e.printStackTrace();
			String err = e.toString();
			Log.e(LOGTAG, "오류가 발생하였습니다.");
		}
	}
}
