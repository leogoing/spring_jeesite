package org.liwang.dao.manager;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.liwang.dao.GroupDao;
import org.liwang.entity.Group;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

import com.thinkgem.jeesite.modules.sys.entity.Role;

/**
 * 分组dao管理类
 * @author liwang
 *
 */
@Component
public class GroupDaoManager extends DefaultDaoManager<Group>{

	
	public int updateGroupRole(Role role){
		dao().deleteGroupRole(role);
		if(role.getGroupList().size()>0)
			return dao().insertGroupRole(role);
		return 0;
	}
	
	public List<Group> getGroupByRole(Role role){
		return dao().getGroupByRole(role);
	}
	
	public List<Group> getGroupInfoByRoles(Collection<Role> roles){
		return dao().getGroupInfoByRoles(roles);
	}
	
	public List<Group> getPermissionWithParents(Role role){
		
		Set<Integer> coll=dao().getGroupIdsByRole(role);
		if(coll!=null && coll.size()>0)
			return dao().getPermByGroups(coll,null);
		return null;
	}
	
	public List<Group> getPermissionWithParents(Collection<Role> roles,Group group){
		
		Set<Integer> coll=dao().getGroupIdsByRoles(roles);
		if(coll!=null && coll.size()>0)
			return dao().getPermByGroups(coll,group);
		return null;
	}
	
	public List<Group> getPermissionWithParents(Collection<Role> roles){
		return getPermissionWithParents(roles,null);
	}
	
	public List<String> getPermissionWithParents(Group group){
		
		return dao().getPermByGroup(group);
	}
	
	@Override
	protected void preOperate(Group entity, String operatFlag) {
		//doNothing...
	}

	private GroupDao dao(){
		return (GroupDao)dao;
	}
}
