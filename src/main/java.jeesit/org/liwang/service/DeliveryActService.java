package org.liwang.service;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.liwang.entity.Deal;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;

/**
 * 到期交割activiti任务
 * @author liwang
 *
 */
public class DeliveryActService implements JavaDelegate{

	private static final long serialVersionUID = 1L;

	private ActTaskService service=SpringContextHolder.getBean(ActTaskService.class);
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Deal deal=(Deal)execution.getVariable("deal");
		
		Map<String,Object> vars=Maps.newHashMap();
		vars.put("deal",deal);
		//ISO 时间格式  TODO  时间转换有错差几个小时
		vars.put("date",new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(deal.getDeliveryDate()));
		
		service.startProcess("auto_approve", "GOLD_DEAL", deal.getId().toString(),"到期交割",vars);//TODO  流程javaService类和抛出已换天信号事件还没写
	}

}
