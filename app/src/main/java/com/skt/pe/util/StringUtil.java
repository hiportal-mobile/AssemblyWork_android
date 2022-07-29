package com.skt.pe.util;


/**
 * String 유틸리티
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class StringUtil {

    public static final String EXCEPTION = "Exception: ";

    public static String paddingLeftCharZero(String s, int len) {
        return paddingLeftChar(s, '0', len);
    }

    public static String paddingLeftCharSpace(String s, int len) {
        return paddingLeftChar(s, ' ', len);
    }

    public static String paddingLeftChar(String s, char c, int len) {
        while (s != null && s.length() < len) {
            s = c + s;
        }

        /*
        Formatter fmt = new Formatter();
        fmt.format("%-" + len + "s", s);
        s = fmt.toString().replaceAll(" ", c+"");
        */

        return s;
    }

    public static String paddingRightCharZero(String s, int len) {
        return paddingRightChar(s, '0', len);
    }

    public static String paddingRightCharSpace(String s, int len) {
        return paddingRightChar(s, ' ', len);
    }

    public static String paddingRightChar(String s, char c, int len) {
        while (s != null && s.length() < len) {
            s += c;
        }

        /*
        Formatter fmt = new Formatter();
        fmt.format("%" + len + "s", s);
        s = fmt.toString().replaceAll(" ", c+"");
        */

        return s;
    }

    /**
     * 에러메세지 얻기
     * 불필요한 Prefix 삭제
     *
     * @param errMessage
     * @return
     */
    public static String getErrorMessage(String errMessage) {
        if(errMessage == null)
            return "";

        int offset = errMessage.lastIndexOf(EXCEPTION);
        if(offset >= 0) {
            errMessage = errMessage.substring(offset + EXCEPTION.length());
        }
        return errMessage;
    }

    public static String getErrorMessage(Exception e) {
        if(e==null || e.getMessage()==null)
            return "";

        String errMessage = e.getMessage();
        errMessage = getErrorMessage(errMessage);

        if(errMessage.trim().length() == 0) {
            Throwable t = (Exception)e;
            while(true) {
                if(t.getCause() == null) {
                    break;
                }
                t = t.getCause();
            }
            errMessage = t.getClass().getName();
        }
        return errMessage;
    }

    public static String toString(Exception e) {
        StackTraceElement[] trace = null;
        Throwable t = (Exception)e;
        while(true) {
            if(t.getCause() == null) {
                break;
            }
            t = t.getCause();
        }
        trace = t.getStackTrace();  

        StringBuffer sb = new StringBuffer();
        sb.append(t.toString());
        for (int row=0; row<trace.length; row++) {
            if(trace[row].getFileName() == null || trace[row].getFileName().indexOf("null")>0) {
                continue;
            }
            sb.append("\n" +  "                                   " +
                      "at " + trace[row].getClassName() + "." + trace[row].getMethodName() + "(" + trace[row].getFileName() + ":" + trace[row].getLineNumber()+")");
        }
        return sb.toString();
    }
    
    /**
     * 문자열을 boolean 값으로 리턴(기본적으로 false)
     * 
     * @param value
     * @return
     */
    public static boolean booleanValue(String value) {
    	if("Y".equalsIgnoreCase(value)) {
    		return true;
    	} else if("N".equalsIgnoreCase(value)) {
    		return false;
    	} else {
    		return Boolean.parseBoolean(value);
    	}
    }
    
    public static int intValue(String value, int defaulz) {
        int v = defaulz;
        try {
            if(value!=null && value.trim().length()>0)
                v = Integer.parseInt(value);
        } catch(Exception e) {
            v = defaulz;
        }
        return v;
    }
    
    public static boolean checkKeyCode(String content) {
    	return content.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝| ]*"); 
    }

    public static int getSize(String content) {
    	int count = 0;
    	for(int i=0; i<content.length(); i++) {
    		if(Character.getType(content.charAt(i)) == Character.OTHER_LETTER)
    			count = count + 2;
    		else 
    			count++;
    	}

    	return count;
    }
    
    public static String getContent(String content) {
    	if(content == null)
    		return null;

    	return content.replaceAll("&amp", "&");
    }

    public static boolean isNull(String value) {
    	if(value==null || value.trim().length()==0) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public static String format(String fmt, String...params) {
    	if(fmt == null)
    		return null;

    	if(fmt.length()>0 && fmt.charAt(0)=='_') {
    		fmt = fmt.substring(1);
    	}
    	
    	final String SEP = "@@";
	
    	StringBuffer sb = new StringBuffer();
    	if(params!=null && params.length>0) {
    		int offset = 0;
    		for(int i=0; i<params.length; i++) {
    			int buffer = fmt.indexOf(SEP, offset);
    			if(buffer > -1) {
    				sb.append(fmt.substring(offset, buffer));
    				sb.append(params[i]);

    				offset = buffer + SEP.length();
    			} else {
    				return fmt;
    			}
    		}
    		sb.append(fmt.substring(offset));
    		return sb.toString();
    	} else {
    		return fmt;
    	}
    }
    
}
