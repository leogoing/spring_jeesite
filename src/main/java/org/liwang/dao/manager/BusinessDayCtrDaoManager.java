package org.liwang.dao.manager;

import java.util.List;

import org.liwang.dao.BusinessDayCtrDao;
import org.liwang.entity.BusinessDayCtr;
import org.liwang.entity.BusinessDayDetail;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 营业日期控制daomanager
 * @author liwang
 *
 */
@Component
public class BusinessDayCtrDaoManager extends DefaultDaoManager<BusinessDayCtr>{

	@Override
	protected void preOperate(BusinessDayCtr entity, String operatFlag) {
		setGroupStr(entity, operatFlag);
	}
	
	public List<BusinessDayDetail> findBusinessHistory(BusinessDayDetail businessDayDetail){
		
		return dao().findBusinessHistory(businessDayDetail);
	}
	
	public int addHistory(BusinessDayDetail businessDayDetail){
		return dao().addHistory(businessDayDetail);
	}
	
	
	private BusinessDayCtrDao dao(){
		return (BusinessDayCtrDao)dao;
	}

}
