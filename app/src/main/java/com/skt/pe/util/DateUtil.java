package com.skt.pe.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 날짜 유틸
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class DateUtil {

	public static final String HH24 = "HH24";
	public static final String H24  = "H24";
	
    public static final String FORMAT_DATETIME = "yyyy.MM.dd HH:mm:ss";
    public static final String ARRAY_DELEMETOR = ",";
    
    public static String[] getNowArray(String... formats) {
        String[] arr = null;
        String format = "";
        
        for(String s : formats)
            format += s + ARRAY_DELEMETOR;
        
        String buffer = getNow(format);
        if(buffer != null)
            arr = buffer.split(ARRAY_DELEMETOR);

        return arr;
    }
    
    public static String getNow() {
        return getNow(FORMAT_DATETIME);
    }

    public static String getNow(String format){
        Date date = new Date();
        return format(date, format);
    }

    public static String format(Date date) {
    	return format(date, FORMAT_DATETIME);
    }
    
	public static String format(Date date, String pattern) {
		return format(date, pattern, null);
	}
	
    public static String format(Date date, String pattern, Locale locale) {
    	if(date == null)
    		return null;

        try {
        	int hour = date.getHours();
    		if(hour == 0)
    			hour = 24;
        	if(pattern.indexOf(HH24) != -1) {
        		pattern = pattern.replaceAll(HH24, StringUtil.paddingLeftCharZero(hour+"", 2));
        	} else if(pattern.indexOf(H24) != -1) {
        		pattern = pattern.replaceAll(H24, hour+"");
        	}
        	
            if (locale == null) {
                return new SimpleDateFormat(pattern).format(date);
            }
            else {
                return new SimpleDateFormat(pattern, locale).format(date);
            }
        }
        catch (Exception e) {
            return date == null ? null : date.toString();
        }
    }
    
	public static Date formatDate(String date, String pattern) {
		return formatDate(date, pattern, null);
	}
	
    public static Date formatDate(String date, String pattern, Locale locale) {
    	if(date == null)
    		return null;

        try {
        	if(pattern.indexOf(HH24) != -1) {
        		pattern = pattern.replaceAll(HH24, "HH");
        	} else if(pattern.indexOf(H24) != -1) {
        		pattern = pattern.replaceAll(H24, "H");
        	}

            if (locale == null) {
                return new SimpleDateFormat(pattern).parse(date);
            }
            else {
                return new SimpleDateFormat(pattern, locale).parse(date);
            }
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static int diffDate(Date fromDate, Date untilDate) {
//    	if (fromDate.after(untilDate) == true)
//    		return -1;
//    	
//    	Calendar fromCal = Calendar.getInstance();
//    	fromCal.setTime(fromDate);
//    	
//    	int diff = 0;
//    	
//    	for (diff = 0;; diff++) {
//    		if (fromCal.getTime().compareTo(untilDate) >= 0)
//    			return diff;
//    		else
//    			fromCal.add(Calendar.DATE, 1);
//    	}
    	long diff = untilDate.getTime() - fromDate.getTime();
	    int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
	    
	    return diffDays;
    }
    
    public static Date addDate(Date date, int day) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.add(Calendar.DATE, day);
    	return cal.getTime();
    }
}