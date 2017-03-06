package org.liwang.commen.entity;

import java.io.Serializable;

/**
 * 实体bean基类
 * @author liwang
 *
 */
public abstract class LEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 编号
	 */
	private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
}
