package org.liwang.dao;

import org.liwang.commen.dao.StandardLDao;
import org.liwang.entity.Account;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;

/**
 * 流水账
 * @author liwang
 *
 */
@MyBatisDao
public interface AccountDao extends StandardLDao<Account>{

}
