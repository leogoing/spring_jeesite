package org.liwang.dao.manager;

import org.liwang.entity.Variable;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 变量
 * @author liwang
 *
 */
@Component
public class VariableDaoManager extends DefaultDaoManager<Variable>{

	@Override
	protected void preOperate(Variable entity, String operatFlag) {
		setGroupStr(entity,operatFlag);
	}

}
