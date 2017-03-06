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

	/**
	 * 刷新关联表对应关系
	 * @param role
	 * @return
	 */
	public int updateGroupRole(Role role){
		dao().deleteGroupRole(role);
		if(role.getGroupList().size()>0)
			return dao().insertGroupRole(role);
		return 0;
	}
	
	/**
	 * 删除分组
	 * @param group
	 * @return
	 */
	public int deleteGroupByGroup(Group group){
		int i=dao().delete(group);
		int j=dao().deleteGroupRoleByGroup(group);
		return i==j?i:-1;
	}
	
	/**
	 * 添加分组
	 * @param group
	 * @return
	 */
	public int addGroup(Group group){
		int i=dao().save(group);
		int j=dao().saveGroupRole(group);
		return i==j?i:-1;
	}
	
	/**
	 * 获取分组信息
	 * @param role
	 * @return
	 */
	public List<Group> getGroupByRole(Role role){
		return dao().getGroupByRole(role);
	}
	
	/**
	 * 根据角色获取分组信息(不带层次查询)
	 * @param roles
	 * @return
	 */
	public List<Group> getGroupInfoByRoles(Collection<Role> roles){
		return dao().getPermByRolesDown(roles);
	}
	
	/**
	 * 根据单个角色获取所有符合分组的所有父级信息
	 * @param role
	 * @return
	 */
	public List<Group> getPermissionWithParents(Role role){
		Set<Integer> coll=dao().getGroupIdsByRole(role);
		if(coll!=null && coll.size()>0)
			return dao().getPermByGroups(coll,null);
		return null;
	}
	
	/**
	 * 根据角色和分组条件获取所有符合的分组的所有父级信息
	 * @param roles
	 * @param group
	 * @return
	 */
	public List<Group> getPermissionWithParents(Collection<Role> roles,Group group){
		Set<Integer> coll=dao().getGroupIdsByRoles(roles);
		if(coll!=null && coll.size()>0)
			return dao().getPermByGroups(coll,group);
		return null;
	}
	
	/**
	 * 根据角色和分组条件获取所有符合的分组的所有子级信息
	 * @param roles
	 * @param group
	 * @return
	 */
	public List<Group> getPermissionWithSons(Collection<Role> roles,Group group){
		Set<Integer> coll=dao().getGroupIdsByRoles(roles);
		if(coll!=null && coll.size()>0)
			return dao().getPermByGroupsDown(coll,group);
		return null;
	}
	
	/**
	 * 根据角色获取所有符合分组的所有父级信息
	 * @param roles
	 * @return
	 */
	public List<Group> getPermissionWithParents(Collection<Role> roles){
		return getPermissionWithParents(roles,null);
	}
	
	/**
	 * 获取单个分组的所有父级的权限字符串
	 * @param group
	 * @return
	 */
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
