package org.liwang.dao.manager;

import org.liwang.entity.Account;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 流水账
 * @author liwang
 *
 */
@Component
public class AccountDaoManager extends DefaultDaoManager<Account>{

	@Override
	protected void preOperate(Account entity, String operatFlag) {
	}

}
