package org.liwang.dao;

import java.util.List;

import org.liwang.commen.dao.StandardLDao;
import org.liwang.entity.Deal;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.act.entity.Act;

/**
 * 交易dao
 * @author liwang
 *
 */
@MyBatisDao
public interface DealDao extends StandardLDao<Deal>{

	
}
