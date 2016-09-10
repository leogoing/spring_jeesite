package org.liwang.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.liwang.common.service.DefaultLService;
import org.liwang.dao.manager.GroupDaoManager;
import org.liwang.entity.Group;
import org.liwang.util.ShiroUtil;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 分组service
 * @author liwang
 *
 */
@Service
public class GroupService extends DefaultLService<Group>{
	
	
	/**
	 * 根据传入角色添加权限到容器
	 * @param roles
	 * @param info
	 */
	public void addPermission(Collection<Role> roles,SimpleAuthorizationInfo info){
		ShiroUtil.addGroupPermission(daoManager().getPermissionWithParents(roles),info);
	}
	
	/**
	 * 查找当前用户角色
	 * @return
	 */
	public List<Group> findByRoles(){
		List<Role> roles=UserUtils.getRoleList();
		return daoManager().getGroupInfoByRoles(roles);
	}
	
	/**
	 * 根据当前用户角色和传入分组信息查找父级分组信息
	 * @param group
	 * @return
	 */
	public List<Group> findByGroupAndRoles(Group group){
		List<Role> roles=UserUtils.getRoleList();
		return daoManager().getPermissionWithParents(roles,group);
	}
	
	/**
	 * 根据当前用户角色和传入分组信息查找子级分组信息
	 * @param group
	 * @return
	 */
	public List<Group> findByGroupAndRolesDown(Group group){
		List<Role> roles=UserUtils.getRoleList();
		return daoManager().getPermissionWithSons(roles,group);
	}
	
	/**
	 * 根据角色获取分组信息
	 * @param role
	 * @return
	 */
	public List<Group> getGroupByRole(Role role){
		return daoManager().getGroupByRole(role);
	}
	
	public List<Map<String,Object>> treeData(List<Group> list){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for(Group group:list){
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", group.getId());
			map.put("pId", group.getParentId());
			map.put("group", StringUtils.trim(group.getGroupId()));
			map.put("name", group.getGroupName());
			map.put("isParent", false);
			mapList.add(map);
		}
		return mapList;
	}
	
	/**
	 * 重写操作两张表
	 */
	public int add(Group group){
		int i=daoManager().addGroup(group);
		if(i>0){
			
		}
		return i;
	}
	
	/**
	 * 重写(操作两张表)
	 */
	public int delete(Group group){
		int i=daoManager().delete(group);
		return i;
	}
	
	private GroupDaoManager daoManager(){
		return (GroupDaoManager)daoManager;
	}
	
}
