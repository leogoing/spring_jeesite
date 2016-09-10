package org.liwang.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
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
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.User;

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
	private IdentityService identityService;
	@Autowired
	private UserDao userDao;
	
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
		
		if(groupStr!=null && !groupStr.trim().isEmpty()){
			variable.putAll(ActivitiUtil.extractGroupsByPerm(groupStr));
		}
		
		variable.put("deal", deal);
		
		extractUserByGroup((String)variable.get("approveGroup"),
						   (String)variable.get("recheckGroup"),variable);
		
		String processId=actTaskService.startProcess("", "GOLD_DEAL", deal.getId().toString(), 
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
	
	
	public Page<Deal> operateList(String operateFlag){
		Act act=new Act();
		act.setProcDefKey("");//流程模型
		act.setTaskName(operateFlag);
		
		List<Act> acts=actTaskService.todoList(act);
		
		
		return null;
	}
	
	/**
	 * TODO  使用缓存框架缓存
	 * 提取拥有传入放行或复核权限的用户
	 * @param approveStr
	 * @param recheckStr
	 * @param variables
	 */
	public void extractUserByGroup(String approveStr,String recheckStr,Map<String,Object> variables){
		String[] approveArr=approveStr.split(ShiroUtil.GROUP_ID_SEPARATOR);//放行组
		String[] recheckArr=recheckStr.split(ShiroUtil.GROUP_ID_SEPARATOR);//复核组
		
		Set<String> approveUsers=Sets.newHashSet();
		Set<String> recheckUsers=Sets.newHashSet();
		
		for(String approveGroup : approveArr){
			approveUsers.addAll(getAcceptUsers(approveGroup));
		}
		for(String recheckGroup : recheckArr){
			recheckUsers.addAll(getAcceptUsers(recheckGroup));
		}
		
		variables.put("approveUsers", approveUsers.toString().replaceFirst("[","").replaceFirst("]",""));
		variables.put("recheckUsers", recheckUsers.toString().replaceFirst("[","").replaceFirst("]",""));
		
	}
	
	/**获取属于所传入的组的自动签收的用户*/
	public Set<String> getAcceptUsers(String group){
		Set<String> users=usersByGroup.get(group);
		if(users==null){
			List<org.activiti.engine.identity.User> userList=
					identityService.createUserQuery().memberOfGroup(group).list();
			List<User> acceptUsers=userDao.extractAutoSign(userList);
			
			users=Sets.newHashSet();
			for(User user : acceptUsers){
				users.add(user.getId());
			}
			usersByGroup.put(group, users);
		}
		return users;
	}
	
	private DealDaoManager daoManager(){
		return (DealDaoManager)daoManager;
	}
}
