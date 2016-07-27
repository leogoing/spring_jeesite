package org.liwang.service;

import java.util.Collection;
import java.util.List;

import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.liwang.common.service.DefaultLService;
import org.liwang.dao.manager.GroupDaoManager;
import org.liwang.entity.Group;
import org.liwang.util.ShiroUtil;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 分组service
 * @author liwang
 *
 */
@Service
public class GroupService extends DefaultLService<Group>{
	
	
	
	public void addPermission(Collection<Role> roles,SimpleAuthorizationInfo info){
		ShiroUtil.addGroupPermission(daoManager().getPermissionWithParents(roles),info);
	}
	
	public List<Group> findByRoles(){
		List<Role> roles=UserUtils.getRoleList();
		return daoManager().getGroupInfoByRoles(roles);
	}
	
	public List<Group> findByGroupAndRoles(Group group){
		List<Role> roles=UserUtils.getRoleList();
		return daoManager().getPermissionWithParents(roles,group);
	}
	
	public List<Group> getGroupByRole(Role role){
		return daoManager().getGroupByRole(role);
	}
	
	private GroupDaoManager daoManager(){
		return (GroupDaoManager)daoManager;
	}
	
}
