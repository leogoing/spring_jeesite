package org.liwang.dao;

import java.util.List;

import org.liwang.commen.dao.StandardLDao;
import org.liwang.entity.BusinessDayCtr;
import org.liwang.entity.BusinessDayDetail;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;

/**
 * 营业日期管理dao
 * @author liwang
 *
 */
@MyBatisDao
public interface BusinessDayCtrDao extends StandardLDao<BusinessDayCtr>{

	public List<BusinessDayDetail> findBusinessHistory(BusinessDayDetail businessDayDetail);
	
	public int addHistory(BusinessDayDetail businessDayDetail);
	
}
