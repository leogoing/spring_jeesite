package org.liwang.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.liwang.manager.AuthManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.SystemService;

/**
 * 工作流工具类
 * @author liwang
 *
 */
public abstract class ActivitiUtil {
	
	private static SystemService systemService;
	
	private static AuthManager authManager;
	
	public static SystemService getSystemService() {
		if (systemService == null){
			systemService = SpringContextHolder.getBean(SystemService.class);
		}
		return systemService;
	}
	
	public static AuthManager getAuthManager(){
		if(authManager==null)
			authManager=SpringContextHolder.getBean(AuthManager.class);
		
		return authManager;
	}

	private static GroupEntity newGroup(String permission){
		if(permission==null || permission.trim().isEmpty()){
			return null;
		}
		
		GroupEntity group=new GroupEntity();
		group.setId(permission);
		group.setName(permission);
		group.setRevision(1);
		group.setType("assignment");
		return group;
	}
	
	/**
	 * 根据用户id查找该用户所属的角色(将权限字符串最后一段封装为Group)
	 * @param userId 用户登录名
	 */
	public static List<Group> findGroupsByUser(String userId) {
//		return getDbSqlSession().selectList("selectGroupsByUserId", userId);
		
		List<Group> list=Lists.newLinkedList();
		User user = getSystemService().getUserByLoginName(userId);
		Set<String> permissions=getAuthManager().getAllPermission(user.getId());
		
		for(String permission : permissions){
			
			String[] permArr=permission.split(ShiroUtil.PERMISSION_SEPARATOR);
			
			String lastPerm=permArr[permArr.length-1];
			if(ShiroUtil.APPROVE_PERMISSION_STR.equals(lastPerm) || 
					ShiroUtil.RECHECK_PERMISSION_STR.equals(lastPerm)){
				list.add(newGroup(permArr.length>1?permArr[permArr.length-2]+
													ShiroUtil.PERMISSION_SEPARATOR+lastPerm:lastPerm));
				continue;
			}
			
			if(!ShiroUtil.hasOperat(permission)){
				list.add(newGroup(lastPerm));
			}
		}
		
		return list;
	}
	
	/**
	 * 从数据权限字符串中提取放行和复核权限组
	 * @return
	 */
	public static Map<String,Object> extractGroupsByPerm(String groupStr){
		Map<String,Object> variable=Maps.newHashMap();
		
		if(groupStr==null || groupStr.trim().isEmpty()){
			variable.put("approveGroup", "");
			variable.put("recheckGroup", "");
			return variable;
		}
		
		String[] groupArr= groupStr.split(ShiroUtil.GROUP_ID_SEPARATOR);
		
		StringBuffer approveGroup=new StringBuffer();
		StringBuffer recheckGroup=new StringBuffer();
		
		for(String perm : groupArr){
			
			String[] permArr=perm.split(ShiroUtil.PERMISSION_SEPARATOR);
			String lastPerm=permArr[permArr.length-1];
			
			if(ShiroUtil.APPROVE_PERMISSION_STR.equals(lastPerm)){
				String newGroup=permArr.length>1?permArr[permArr.length-2]+
								ShiroUtil.PERMISSION_SEPARATOR+lastPerm:lastPerm;
				approveGroup.append(newGroup).append(ShiroUtil.GROUP_ID_SEPARATOR);
				continue;
			}
			
			if(ShiroUtil.RECHECK_PERMISSION_STR.equals(lastPerm)){
				String newGroup=permArr.length>1?permArr[permArr.length-2]+
						ShiroUtil.PERMISSION_SEPARATOR+lastPerm:lastPerm;
				recheckGroup.append(newGroup).append(ShiroUtil.GROUP_ID_SEPARATOR);
				continue;
			}
			
			if(!ShiroUtil.hasOperat(perm)){
				recheckGroup.append(lastPerm).append(ShiroUtil.GROUP_ID_SEPARATOR);
				approveGroup.append(lastPerm).append(ShiroUtil.GROUP_ID_SEPARATOR);
			}
		}
		
		variable.put("approveGroup", approveGroup.toString());
		variable.put("recheckGroup", recheckGroup.toString());
		
		return variable;
	}

	
}
