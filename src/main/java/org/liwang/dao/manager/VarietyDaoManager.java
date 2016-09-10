package org.liwang.dao.manager;

import org.liwang.dao.VarietyDao;
import org.liwang.entity.Result;
import org.liwang.entity.Variety;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * dao管理类(没办法必须要确定泛型否则无法自动装配)
 * @author liwang
 *
 */
@Component
public class VarietyDaoManager extends DefaultDaoManager<Variety>{

	@Override
	protected void preOperate(Variety entity, String operatFlag) {
		setGroupStr(entity,operatFlag);
	}

	/**
	 * TODO  : 同添加科目余额
	 * @param variety
	 * @param storage
	 * @return
	 */
	public Result<String> addStorage(Variety variety,Long storage){
		dao().addStorage(variety, storage);
		return null;
	}
	
	private VarietyDao dao(){
		return (VarietyDao)dao;
	}
	
}
