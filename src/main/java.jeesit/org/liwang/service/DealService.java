package org.liwang.service;

import java.util.List;
import java.util.Map;

import org.liwang.common.service.OperabilityLService;
import org.liwang.entity.Account;
import org.liwang.entity.Condition;
import org.liwang.entity.Deal;
import org.liwang.entity.Result;
import org.liwang.entity.Subject;
import org.liwang.entity.Template;
import org.liwang.entity.Variable;
import org.liwang.entity.Variety;
import org.liwang.util.Approve;
import org.liwang.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;

/**
 * 交易服务类
 * @author liwang
 *
 */
@Service
public class DealService extends OperabilityLService<Deal>{
	
	
	@Autowired
	private TemplateService templateService; 
	
	@Autowired
	private ConditionService conditionService;
	
	@Autowired
	private VariableService variableService;
	
	@Autowired
	private SubjectService subjectService;
	
	@Autowired
	private VarietyService varietyService;
	
	/**多笔放行*/
	@Transactional(readOnly=false,propagation=Propagation.REQUIRES_NEW)
	public List<Result<String>> approves(List<Deal> list){
		if(list==null || list.isEmpty()){
			return null;
		}
		
		List<Result<String>> listRes=Lists.newLinkedList();
		List<Template> templates=templateService.findAll();//TODO 都要缓存的
		List<Condition> conditions=conditionService.findAll();
		List<Variable> variables=variableService.findAll();
		
		List<Map<String,Number>> subjects=Lists.newLinkedList();
		List<Variety> varieties=Lists.newLinkedList();
		
		for(Deal deal : list){
			Result<String> res=new Result<String>();
			try {
				List<Account> accounts=SpringContextHolder.getBean(Approve.class)
														  .approve(deal, templates, conditions, variables);
			
				for(Account account : accounts){//回写库存和余额
					Map<String,Number> subjectMap=Maps.newHashMap();
					subjectMap.put("dcFlag", account.getDcFlag());
					subjectMap.put("firstSubject", account.getFirstSubject());
					subjectMap.put("secondSubject", account.getSecondSubject());
					subjectMap.put("thirdSubject", account.getThirdSubject());
					subjectMap.put("money", account.getMoney());
					subjects.add(subjectMap);
				}
				//是否交割
				boolean isDelivery=StrUtil.StrToDate("2016.08.28", null).getTime()>deal.getDeliveryDate().getTime();
				if(isDelivery){
					deal.setApprove(Deal.DELIVERED);
					Variety variety=new Variety();
					if("USD".equals(deal.getBuyCur())){//TODO 要封装
						variety.setCurrency(deal.getSellCur());
						variety.setStorage(-deal.getAmount());
					}else if("USD".equals(deal.getSellCur())){
						variety.setCurrency(deal.getBuyCur());
						variety.setStorage(deal.getAmount());
					}
					varieties.add(variety);
				}
					
				res.setStatus(Result.SUCCESS);
				res.setMessage("放行成功!");
			} catch (Exception e) {
				res.setStatus(Result.ERROR);
				res.setData("放行发生异常!  "+e.getMessage());
				throw new RuntimeException(e);
			}
			listRes.add(res);
		}
		
		subjectService.addBalance(subjects);
		varietyService.addStorage(varieties);
		
		return listRes;
	}
	
	
	
	
}
