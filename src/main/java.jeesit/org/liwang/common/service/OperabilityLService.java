package org.liwang.common.service;


import org.liwang.commen.entity.LEntity;
import org.liwang.entity.Operate;
import org.liwang.service.OperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 针对带操作的数据的Service(对数据库操作添加操作明细)
 * @author liwang
 *
 * @param <T>
 */
@Transactional(readOnly=false)
public class OperabilityLService<T extends LEntity> extends DefaultLService<T>{

	@Autowired
	protected OperateService oService;
	
	public int update(T entity){
		oService.createOperate(Operate.SOURCE_TYPE_DB, Operate.OPERATE_TYPE_UPDATE, entity);
		return daoManager.update(entity);
	}
	
	public int add(T entity){
		int i=daoManager.save(entity);
		oService.createOperate(Operate.SOURCE_TYPE_DB, Operate.OPERATE_TYPE_CREATE, entity);
		return i;
	}
	
	public int delete(T entity){
		oService.createOperate(Operate.SOURCE_TYPE_DB, Operate.OPERATE_TYPE_DELETE, entity);
		return daoManager.delete(entity);
	}
	
}
