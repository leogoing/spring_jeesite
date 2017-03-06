package org.liwang.manager;

import java.util.List;

import org.liwang.commen.dao.StandardLDao;
import org.liwang.commen.entity.LEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据库操作管理接口,用来协调数据库操作
 * @author liwang
 *
 */
public abstract class AbstractDaoManager<E extends LEntity> {
	
	@Autowired
	protected StandardLDao<E> dao;
	
	@Autowired
	protected AuthManager authManager;
	
	
	public abstract E get(E entity);
	
	public abstract List<E> find(E entity);
	
	public abstract int update(E entity);
	
	public abstract int save(E entity);
	
	public abstract int delete(E entity);
	
}

