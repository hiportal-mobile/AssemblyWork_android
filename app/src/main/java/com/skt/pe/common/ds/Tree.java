package com.skt.pe.common.ds;

import java.io.Serializable;

import com.skt.pe.util.StringUtil;


@SuppressWarnings("serial")
public class Tree<T> implements Serializable {
	private Tree<T>[] tree = null;
	private T item = null;
	
	public void initTree(int listCnt) {
		tree = new Tree[listCnt];
		for (int i=0; i<listCnt; i++) {
			tree[i] = new Tree<T>();
		}
	}
	
	public Tree<T>[] getTree() {
		return this.tree;
	}
	
	public Tree<T> getTree(int index) {
		if (index >= this.tree.length)
			return null;
		
		return this.tree[index];
	}
	
	public int length() {
		return this.tree.length;
	}
	
	public void setItem(T item) {
		this.item = item;
	}
	
	public T getItem() {
		return this.item;
	}
	
	public T getItem(String index) {
		String treeIndex = index.substring(0, index.lastIndexOf("_"));
		Tree<T> currentTree = getTree(index);
		
		if (currentTree != null) 
			return currentTree.getItem();
		else
			return null;
//		String[] indexs = index.split("_");
//		int intIndex;
//		Tree<T> currentTree = this;
//		for (int i=0; i<indexs.length; i++) {
//			intIndex = StringUtil.intValue(indexs[i], 1) - 1;
//			currentTree = currentTree.getTree(intIndex);
//		}
//		return currentTree.getItem();
	}
	
	public Tree<T> getTree(String index) {
		String[] indexs = index.split("_");
		return getTree(indexs);
	}
	
	public Tree<T> getTree(String[] indexs) {
		Tree<T> currentTree = null;
		int intIndex;
		
		if (indexs.length > 0) {
			for (int i=0; i<indexs.length; i++) {
				if (i == 0) {
					currentTree = this;
					continue;
				}
				intIndex = StringUtil.intValue(indexs[i], 1) - 1;
				currentTree = currentTree.getTree(intIndex);
			}
		} 
		
		return currentTree;
	}
}
