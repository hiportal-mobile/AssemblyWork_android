package com.skt.pe.common.service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.skt.pe.common.exception.ParsingException;
import com.skt.pe.util.XMLUtil;


/**
 * XML 테이타
 *
 * @author  : june
 * @date    : $Date$
 * @id      : $Id$
 */
public class XMLData extends DataList {
	
	public static final String ID_NODE = "_NODE_";

	private Node node = null;
	
	public XMLData(File file) throws ParsingException {
		try {
			node = XMLUtil.parse(file);
		} catch(Exception e) {
			throw new ParsingException("Data Parsing Error!");
		}
	}

	public XMLData(InputStream is) throws ParsingException {
		try {
			node = XMLUtil.parse(is);
		} catch(Exception e) {
			throw new ParsingException("Data Parsing Error!");
		}
	}
	
	public XMLData(String xmlContent) throws ParsingException {
		try {
			node = XMLUtil.parseContent(xmlContent);
		} catch(Exception e) {
			throw new ParsingException("Data Parsing Error!");
		}
	}
	
	public XMLData(Node node) throws ParsingException {
		//TODO NodeType Check
		if(node == null)
			throw new ParsingException("Node is null!");
		
		this.node = node;
	}

	public void setList(String xpathExpression) throws ParsingException {
		if(list == null)
			list = new ArrayList<Map<String, Object>>();
		else
			list.clear();

		try {
			NodeList nodeList = null;
			if(xpathExpression.charAt(0) != '/') {
				switch(node.getNodeType()) {
					case Node.DOCUMENT_NODE :
						Document document = (Document)node;
						nodeList = document.getElementsByTagName(xpathExpression);
						break;
					case Node.ELEMENT_NODE :
						Element element = (Element)node;
						nodeList = element.getElementsByTagName(xpathExpression);
						break;
					default :
						throw new ParsingException("Node-Type is invalid!");
				}
			} else {
				nodeList = XMLUtil.selectNodeList(node, xpathExpression);
			}

			for(int i=0; i<nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				Map<String, Object> value = new HashMap<String, Object>();

				value.put(ID_NODE, node);

				NodeList childNodeList = node.getChildNodes();
				for(int j=0; j<childNodeList.getLength(); j++) {
					Node childNode = childNodeList.item(j);
					
					if(childNode.getLocalName() != null)
						value.put(childNode.getLocalName(), XMLUtil.getTextContent(childNode));
				}

				list.add(value);
			}
		} catch(ParsingException e) {
			throw e;
		} catch(Exception e) {
			throw new ParsingException(e);
		}
	}
	
	public Node getRoot() {
		return this.node;
	}
    
	public Node getNode(String xpathExpression) throws ParsingException {
		try {
			if(xpathExpression.charAt(0) != '/') {
				NodeList nodeList = null;
				switch(node.getNodeType()) {
					case Node.DOCUMENT_NODE :
						Document document = (Document)node;
						nodeList = document.getElementsByTagName(xpathExpression);
						break;
					case Node.ELEMENT_NODE :
						Element element = (Element)node;
						nodeList = element.getElementsByTagName(xpathExpression);
						break;
					default :
						throw new ParsingException("Node-Type is invalid!");
				}

				if(nodeList.getLength() > 0) {
					return nodeList.item(0);
				} else {
					return null;
				}
			} else {
				return XMLUtil.getSingleNode(node, xpathExpression);
			}
		} catch(ParsingException e) {
			throw e;
		} catch(Exception e) {
			throw new ParsingException(e);
		}
	}

	public String get(String xpathExpression) throws ParsingException {
		return XMLUtil.getTextContent(getNode(xpathExpression));
	}

	public XMLData getChild(String xpathExpression) throws ParsingException {
		return new XMLData(getNode(xpathExpression));
	}

	public XMLData getChild(int i) throws ParsingException {
		if(list == null)
			throw new ParsingException("List is null!");

		return new XMLData((Node)getObject(i, ID_NODE));		
	}
	
	/*
	 * XPath로 오면 안되고 하나의 노드 이름으로 와야 한다.
	 */
	public XMLData getChild(int i, String nodeName) throws ParsingException {
		XMLData xmlData = getChild(nodeName);
		xmlData.setList(nodeName);
		Map<String, Object> result = xmlData.getListItem(i); 
		return (XMLData) result.get(nodeName);
	}

	public String getContent(int i) throws ParsingException {
		if(list == null)
			throw new ParsingException("List is null!");

		String ret = "";
		try {
			ret = XMLUtil.getTextContent((Node)getObject(i, ID_NODE)); 
		} catch(Exception e) { }
		return ret;
	}
	
}
