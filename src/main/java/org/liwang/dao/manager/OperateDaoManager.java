package org.liwang.dao.manager;

import org.liwang.entity.Operate;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 操作明细daomanager
 * @author liwang
 *
 */
@Component
public class OperateDaoManager extends DefaultDaoManager<Operate>{

	/**
	 * 加权限因为在新增时数据的权限默认与操作对象的权限一致
	 */
	@Override
	protected void preOperate(Operate entity, String operatFlag) {
		setGroupStr(entity, operatFlag);
	}

}
