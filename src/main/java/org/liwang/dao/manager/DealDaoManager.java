package org.liwang.dao.manager;

import java.util.List;

import org.liwang.dao.DealDao;
import org.liwang.entity.Deal;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

import com.thinkgem.jeesite.modules.act.entity.Act;

/**
 * 交易dao管理类
 * @author liwang
 *
 */
@Component
public class DealDaoManager extends DefaultDaoManager<Deal>{

	@Override
	protected void preOperate(Deal entity, String operatFlag) {
		setGroupStr(entity, operatFlag);
	}

	public List<Deal> getTasks(List<Act> acts){
		return dao().getTasks(acts);
	}
	
	private DealDao dao(){
		return (DealDao)dao;
	}
}
