package org.liwang.dao.manager;

import org.liwang.entity.Condition;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 条件
 * @author liwang
 *
 */
@Component
public class ConditionDaoManager extends DefaultDaoManager<Condition>{

	@Override
	protected void preOperate(Condition entity, String operatFlag) {
		setGroupStr(entity,operatFlag);
	}

}
