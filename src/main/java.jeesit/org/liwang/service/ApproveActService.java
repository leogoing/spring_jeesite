package org.liwang.service;

import java.util.List;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.liwang.entity.Deal;
import org.liwang.entity.Result;
import org.liwang.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;

/**
 * activiti放行java任务类
 * @author liwang
 *
 */
public class ApproveActService implements JavaDelegate{

	private static final long serialVersionUID = 1L;
	
	private static final Logger log=LoggerFactory.getLogger(ApproveActService.class);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		DealService service=SpringContextHolder.getBean(DealService.class);
		
		Deal deal=(Deal)execution.getVariable("deal");
		List<Deal> list=Lists.newArrayList();
		list.add(deal);
		
//		try {//	捕获异常会产生Transaction rolled back because it has been marked as rollback-only错误,
			 //改为手动抛出activiti异常触发事件则可以
		List<Result<String>> res=null;
		try{
			res=service.approves(list);
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
			execution.setVariable("approveAgain", "Y");
			throw new BpmnError("approveError");
		}
		
		if(res!=null && res.get(0).getStatus()==Result.SUCCESS){
			execution.setVariable("accountFlag", "noDeliveried");
			
			//是否已交割
			if(deal.getDeliveryDate().getDay()<=StrUtil.StrToDate("2016.09.12", null).getDay()//TODO  获取通用营业日
					 && deal.getEarlyPay()==Deal.EARLY_PAY){
				execution.setVariable("accountFlag", "deliveried");
			}
		}
		
	}

}
