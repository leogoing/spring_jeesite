package org.liwang.common.service;

import java.util.List;

import org.liwang.commen.dao.LDao;
import org.liwang.commen.entity.LEntity;
import org.liwang.manager.AbstractDaoManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 默认service基本的数据库操作
 * @author liwang
 *
 * @param <D>
 * @param <T>
 */
public class DefaultLService<T extends LEntity> extends AbstractLService{

	@Autowired
	protected AbstractDaoManager<T> daoManager;
	
	public List<T> findAll(T entity){
		return daoManager.find(entity);
	}
	
	public List<T> findAll(){
		return daoManager.find(null);
	}
	
	public T get(T entity){
		return daoManager.get(entity);
	}
	
	public int delete(T entity){
		return daoManager.delete(entity);
	}
	
	public int update(T entity){
		return daoManager.update(entity);
	}
	
	public int add(T entity){
		return daoManager.save(entity);
	}
	
}
