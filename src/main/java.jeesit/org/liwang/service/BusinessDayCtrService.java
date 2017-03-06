package org.liwang.service;

import java.util.List;

import org.liwang.common.service.OperabilityLService;
import org.liwang.dao.manager.BusinessDayCtrDaoManager;
import org.liwang.entity.BusinessDayCtr;
import org.liwang.entity.BusinessDayDetail;
import org.springframework.stereotype.Service;

/**
 * 营业日管理service
 * @author liwang
 *
 */
@Service
public class BusinessDayCtrService extends OperabilityLService<BusinessDayCtr>{

	
	public  List<BusinessDayDetail> findBusinessHistory(BusinessDayDetail businessDayDetail){
		return daoManager().findBusinessHistory(businessDayDetail);
	}
	
	private BusinessDayCtrDaoManager daoManager(){
		return (BusinessDayCtrDaoManager)daoManager;
	}
}
