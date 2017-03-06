package org.liwang.entity;

import java.io.Serializable;

/**
 * 只保存key和对应val的对象
 * @author liwang
 *
 */
public class KeyVal<K,V> implements Serializable{

	private static final long serialVersionUID = 1L;

	private K key;
	
	private V val;
	
	public KeyVal(){}
	
	public KeyVal(K key,V val){
		this.key=key;
		this.val=val;
	}

	public K getKey() {
		return key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getVal() {
		return val;
	}

	public void setVal(V val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return "KeyVal [key=" + key + ", val=" + val + "]";
	}
	
}
