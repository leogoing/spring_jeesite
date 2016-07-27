package org.liwang.commen.dao;

import java.util.List;

import org.liwang.commen.entity.LEntity;

/**
 * 标准dao接口,带crud操作(批量)
 * @author liwang
 *
 */
public interface StandardLDao<T extends LEntity> extends LDao<T>{

	public int save(T t);
	
	public int save(List<T> list);
	
	public int update(T t);
	
	public int update(List<T> list);
	
	public int delete(T t);
	
	public int delete(String id);
	
	public int delete(List<T> list);
	
	public int delete(String ... ids);
}
