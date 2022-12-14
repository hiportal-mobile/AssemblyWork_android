package com.skt.pe.util;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;


/**
 * XML 유틸
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class XMLUtil {
    
	public static Document parse(File file) throws Exception {
		return parse(file, false);
	}
	
	public static Document parse(File file, boolean validation) throws Exception {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        if(file == null)
        	throw new Exception("File is null!");

        factory.setValidating(validation);
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            return document;
        } catch (Exception e) {
            throw e;
        }
	}

	public static Document parse(InputStream is) throws Exception {
		return parse(is, false);
	}
	
	public static Document parse(InputStream is, boolean validation) throws Exception {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        if(is == null)
        	throw new Exception("InputStream is null!");

        factory.setValidating(validation);
        factory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(is);
            return document;
        } catch (Exception e) {
            throw e;
        }
	}

    public static Document parseContent(String content) throws Exception {
    	return parseContent(content, false);
    }
    
    public static Document parseContent(String content, boolean validation) throws Exception {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        if(content==null || content.trim().length()==0)
        	throw new Exception("Content is null!");
        
        factory.setValidating(validation);
        factory.setNamespaceAware(true);

        Reader r = new StringReader(content.trim());
        InputSource is = new InputSource(r);
        
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(is);
            return document;
        } catch (Exception e) {
            throw e;
        }
    }
    
    public static NodeList selectNodeList(Node node, String xpathExpression) throws Exception {
    	return null;
    }

    public static Node getSingleNode(Node node, String xpathExpression) throws Exception {
    	if(xpathExpression==null || node==null)
    		return null;
    	
    	String[] xpath = xpathExpression.split("/");
    	Node offset = node;
    	
    	for(int i=0; i<xpath.length; i++) {
    		if(xpath[i].length() == 0)
    			continue;

    		NodeList nodeList = offset.getChildNodes();
    		boolean checked = false;
    		for(int j=0; j<nodeList.getLength(); j++) {
    			Node buffer = nodeList.item(j);
    			if(xpath[i].equals(buffer.getNodeName())) {
    				offset = buffer;
    				checked = true;
    				break;
    			}
    		}

    		if(!checked) {
    			return null;
    		}
    	}

    	return offset;
    }

    public static String getTextContent(Node node) throws DOMException {
    	String textContent = "";
    	
    	if(node == null) {
    		return null;
    	}

    	if(node.getNodeType() == Node.ATTRIBUTE_NODE) {
    		textContent = node.getNodeValue();
    	} else {
    		NodeList nodeList = node.getChildNodes(); 
    		for(int i=0; i<nodeList.getLength(); i++) {
    			Node child = nodeList.item(i);
   			
        		if(child != null) {
        			if(child.getNodeType() == Node.CDATA_SECTION_NODE) {
        				CDATASection cdata = (CDATASection)child;
        				textContent += cdata.getData();
        			} else if(child.getNodeType() == Node.TEXT_NODE) {
        				Text data = (Text)child;
        				textContent += data.getData();
        			}
        		}    	
    		}		
    	}
    	
    	return textContent;
    }
    
    public static String getAttributeValue(Element element, String name) {
        return element.getAttribute(name);
    }

    public static String getAttributeValue(Element element, String name, String defaultValue) {
        return (element.getAttribute(name) != null) ? element.getAttribute(name) : defaultValue;
    }
    
    public static boolean getAttributeBooleanValue(Element element, String name) {
        return (new Boolean(element.getAttribute(name))).booleanValue();
    }

    public static double getAttributeDoubleValue(Element element, String name) {
        return (new Double(element.getAttribute(name))).doubleValue();
    }

    public static float getAttributeFloatValue(Element element, String name) {
        return (new Float(element.getAttribute(name))).floatValue();
    }

    public static int getAttributeIntValue(Element element, String name) {
        return (new Integer(element.getAttribute(name))).intValue();
    }

}