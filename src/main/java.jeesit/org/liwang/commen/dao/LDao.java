package org.liwang.commen.dao;

import java.util.List;

import org.liwang.commen.entity.LEntity;

/**
 * 数据源接口,根接口只有查询方法
 * @author liwang
 *
 */
 public interface LDao<T extends LEntity> {

	 /**
	  * 根据id查找
	  * @param id
	  * @return
	  */
	public T get(String id);
	 
	 /**
	  * 根据实体bean查找
	  * @param entity
	  * @return
	  */
	public T get(T entity);
	 
	 /**
	  * 批量查找
	  * @param entity
	  * @return
	  */
	public List<T> find(T entity);

}
