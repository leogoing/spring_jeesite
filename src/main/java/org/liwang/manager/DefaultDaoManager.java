package org.liwang.manager;

import java.util.List;
import java.util.Set;

import org.liwang.commen.entity.BaseLEntity;
import org.liwang.commen.entity.LEntity;
import org.liwang.util.ShiroUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认dao管理类实现
 * @author liwang
 *
 */
public abstract class DefaultDaoManager<E extends LEntity> extends AbstractDaoManager<E>{

	
	private static final Logger log=LoggerFactory.getLogger(DefaultDaoManager.class);

	 /**
	  * 根据实体bean查找
	  * @param entity
	  * @return
	  */
	public E get(E entity){
		preOperate(entity, ShiroUtil.VIEW_PERMISSION_STR);
		return dao.get(entity);
	}
	 
	 /**
	  * 批量查找
	  * @param entity
	  * @return
	  */
	public List<E> find(E entity){
		preOperate(entity, ShiroUtil.VIEW_PERMISSION_STR);
		if(log.isDebugEnabled() && entity!=null){
			log.debug("find-设置完实体中的groupStr: {}",((BaseLEntity)entity).getGroupStr());
		}
		return dao.find(entity);
	}
	
	protected abstract void preOperate(E entity,String operatFlag);
	
	protected void setGroupStr(E entity,String operatFlag){
		if(entity!=null && entity instanceof BaseLEntity){
			Set<String> permissions=authManager.getAllPermission();
			
			if(log.isDebugEnabled()){
				for(String s:permissions){
					log.debug("设置当前用户的 groupStr {}",s);
				}
			}
			((BaseLEntity)entity).parseGroupStr(permissions,operatFlag);
		}
	}

	@Override
	public int update(E entity) {
		preOperate(entity,ShiroUtil.UPDATE_PERMISSION_STR);
		
		return dao.update(entity);
	}

	@Override
	public int save(E entity) {
		
		return dao.save(entity);
	}
	
	public int delete(E entity){
		return dao.delete(entity);
	}
}
