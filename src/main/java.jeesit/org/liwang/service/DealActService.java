package org.liwang.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.liwang.common.service.OperabilityLService;
import org.liwang.dao.manager.DealDaoManager;
import org.liwang.entity.Deal;
import org.liwang.entity.Operate;
import org.liwang.entity.Result;
import org.liwang.util.ActivitiUtil;
import org.liwang.util.ShiroUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 交易工作流service
 * @author liwang
 *
 */
@Service
public class DealActService extends OperabilityLService<Deal>{

	@Autowired
	private ActTaskService actTaskService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private DealService dealService;
//	@Autowired
//	private RuntimeService runtimeService;
	@Autowired
	private UserDao userDao;
	
	/**流程key*/
	private static final String PROCESS_KEY="deal_approve";
	
	/**
	 * TODO  就是说用户修改了是否自动签核或变动了分组需要联动当前变量除了新增分组外
	 * 存放工作流用的组和用户的对应关系(根据组找用户)(并都是自动签收的用户)
	 */
	private Map<String,Set<String>> usersByGroup=Maps.newHashMap();
	
	/**
	 * 启动流程
	 */
	public Result<String> startProcess(Deal deal){
		Map<String,Object> variable=Maps.newHashMap(); 
		Result<String> result=new Result<String>();
		
		/*              先分组按放行组合复核组分 并存放到变量中                                   */
		String groupStr=deal.getGroupStr();
		
		variable.putAll(ActivitiUtil.extractGroupsByPerm(groupStr));
		
		variable.put("deal", deal);
		
		String approveGroup=(String)variable.get("approveGroup");
		String recheckGroup=(String)variable.get("recheckGroup");
		extractUserByGroup(approveGroup,recheckGroup,variable);
		
		checkVars(approveGroup, recheckGroup, variable);
		
		String processId=actTaskService.startProcess(PROCESS_KEY, "GOLD_DEAL", deal.getId().toString(), 
									"交易 "+deal.getDealNo()+" 审核", variable);
		
		oService.createOperate(Operate.SOURCE_TYPE_DB, Operate.OPERATE_TYPE_START_PROCESS, deal);
		
		if(processId!=null && !processId.trim().isEmpty()){
			result.setStatus(Result.SUCCESS);
			result.setData(processId);
			result.setMessage("流程发布成功!");
		}else{
			result.setStatus(Result.FAILED);
			result.setMessage("流程发布失败!");
		}
		return result;
	}
	
	/**
	 * 反签收
	 * @param taskId
	 * @return
	 */
	public Result<String> cancel(String taskId){
		List<IdentityLink> links=taskService.getIdentityLinksForTask(taskId);
		
		for(IdentityLink link : links){//如果该任务存在候选人或候选组
			if(IdentityLinkType.CANDIDATE.equals(link.getType())){
				taskService.claim(taskId, null);
				return new Result<String>(Result.SUCCESS, "取消办理成功!");
			}
		}
		
		return new Result<String>(Result.FAILED, "没有候选成员,取消办理失败!");
	}
	
	/**
	 * 签收并不允许同一人签收两个任务
	 * @param taskId
	 * @return
	 */
	public Result<String> claim(String taskId){
		Result<String> res=new Result<String>();
		String userId=UserUtils.getUser().getLoginName();
		
		Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
		long claimed=taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
								.taskAssignee(userId).active().count();
		
		if(claimed<=0){
			actTaskService.claim(taskId, UserUtils.getUser().getLoginName());
			res.setStatus(Result.SUCCESS);
			res.setMessage("签收成功!");
		}else{
			res.setStatus(Result.FAILED);
			res.setMessage("已签收该交易任务,签收失败!");
		}
		
		return res;
	}
	
	/**
	 * 放行和复核操作列表
	 * @param operateFlag
	 * @return
	 */
	public List<Act> operateList(String operateFlag){
		Act act=new Act();
		act.setProcDefKey(PROCESS_KEY);//流程模型
		act.setTaskDefKey(operateFlag);
		
		return actTaskService.todoList(act);
	}
	
	/**
	 * TODO  使用缓存框架缓存
	 * 提取拥有传入放行或复核权限的用户
	 * @param approveStr
	 * @param recheckStr
	 * @param variables
	 */
	public void extractUserByGroup(String approveStr,String recheckStr,Map<String,Object> variables){
		if(approveStr==null){
			approveStr="";
		}
		if(recheckStr==null){
			recheckStr="";
		}
		
		String[] approveArr=approveStr.split(ShiroUtil.GROUP_ID_SEPARATOR);//放行组
		String[] recheckArr=recheckStr.split(ShiroUtil.GROUP_ID_SEPARATOR);//复核组
		
		Set<String> approveUsers=Sets.newHashSet();
		Set<String> recheckUsers=Sets.newHashSet();
		
		for(String approveGroup : approveArr){
			Set<String> app=getAcceptUsers(approveGroup);
			if(app!=null)
				approveUsers.addAll(app);
		}
		for(String recheckGroup : recheckArr){
			Set<String> rec=getAcceptUsers(recheckGroup);
			if(rec!=null)
				recheckUsers.addAll(rec);
		}
		
		variables.put("approveUsers", approveUsers.toString().replaceFirst("\\[","").replaceFirst("\\]",""));
		variables.put("recheckUsers", recheckUsers.toString().replaceFirst("\\[","").replaceFirst("\\]",""));
		
	}
	
	/**
	 * 若放行复核的人或候选组为空则设操作人为*表示所有人均可
	 * @param vars
	 */
	private void checkVars(String approveGroup,String recheckGroup,Map<String,Object> vars){
		if(approveGroup==null || approveGroup.isEmpty()){
			vars.put("approveUsers", "*");
		}
		if(recheckGroup==null || recheckGroup.isEmpty()){
			vars.put("recheckUsers", "*");
		}
	}
	
	/**放行*/
	public Result<String> approve(Deal oldDeal,String taskId,String procInsId){
		Deal newDeal=dealService.get(oldDeal);//查询和该deal相同id的数据库中的deal
		
		if(!compareDeal(newDeal, oldDeal))
			return new Result<String>(Result.FAILED,"前后数据有变动!");
		
		Map<String,Object> vars=Maps.newHashMap();
		
		Object var=taskService.getVariable(taskId, "recheckFlag");//获取是否复核过有无复核标识
		if(var==null){//没有复核标识则添加一个空标识
			vars.put("recheckFlag", "");
		}
		
		vars.put("approveFlag", "approve");
		vars.put("approveAgain", "Y");
		actTaskService.complete(taskId, procInsId, "", vars);
		
		return new Result<String>(Result.SUCCESS,"已开始放行!");
	}
	
	/**获取属于所传入的组的自动签收的用户*/
	public Set<String> getAcceptUsers(String group){
		Set<String> users=usersByGroup.get(group);
		if(users==null && group!=null && !group.isEmpty()){
			
			String[] groupArr=group.split(ShiroUtil.PERMISSION_SEPARATOR);
			String firstGroup = groupArr[0];
			String secondGroup = null;
			if(groupArr.length>1)
				secondGroup=groupArr[1];
			
			List<User> acceptUsers=userDao.extractAutoSignByGroup(firstGroup, secondGroup);
			
			users=Sets.newHashSet();
			for(User user : acceptUsers){
				users.add(user.getId());
			}
			usersByGroup.put(group, users);
		}
		return users;
	}
	
	/**比较放行数据是否与数据库数据一致*/
	private boolean compareDeal(Deal newDeal ,Deal oldDeal){
		if(newDeal==null || oldDeal==null)
			return false;
		
		if(newDeal.equals(oldDeal))
			return true;
		
		if(newDeal.getId().equals(oldDeal.getId())
			&& newDeal.getAmount().equals(oldDeal.getAmount())
			&& newDeal.getBornOf().equals(oldDeal.getBornOf())
			&& newDeal.getBuyCur().equals(oldDeal.getBuyCur())
			&& newDeal.getCounterparty().getId().equals(oldDeal.getCounterparty().getId()) 
			&& newDeal.getDealAccount().getId().equals(oldDeal.getDealAccount().getId())
			&& newDeal.getDealDate().compareTo(oldDeal.getDealDate())==0
			&& newDeal.getDealNo().equals(oldDeal.getDealNo())
			&& newDeal.getDealType().equals(oldDeal.getDealType())
//			&& newDeal.getDeliveryDate().compareTo(oldDeal.getDeliveryDate())==0 TODO  要去掉小时以下的判断因为页面上只精确到天
			&& newDeal.getEarlyPay().equals(oldDeal.getEarlyPay())
			&& newDeal.getMoney().equals(oldDeal.getMoney())
			&& newDeal.getSellCur().equals(oldDeal.getSellCur())
			&& newDeal.getSfFlag().equals(oldDeal.getSfFlag())
			&& newDeal.getStatus().equals(oldDeal.getStatus())
		    ){
			return true;
		}
		
		return false;
	}
	
	private DealDaoManager daoManager(){
		return (DealDaoManager)daoManager;
	}
}
