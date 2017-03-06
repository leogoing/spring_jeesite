package org.liwang.dao;

import org.liwang.commen.dao.StandardLDao;
import org.liwang.entity.Counterparty;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;

/**
 * 交易机构dao
 * @author liwang
 *
 */
@MyBatisDao
public interface CounterpartyDao extends StandardLDao<Counterparty>{

}
