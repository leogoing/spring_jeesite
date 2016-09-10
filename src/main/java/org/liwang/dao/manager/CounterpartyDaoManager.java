package org.liwang.dao.manager;

import org.liwang.entity.Counterparty;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 交易同业dao管理类
 * @author liwang
 *
 */
@Component
public class CounterpartyDaoManager extends DefaultDaoManager<Counterparty>{

	@Override
	protected void preOperate(Counterparty entity, String operatFlag) {
		setGroupStr(entity, operatFlag);
	}

}
