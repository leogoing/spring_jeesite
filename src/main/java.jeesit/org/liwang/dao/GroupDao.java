package org.liwang.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.liwang.commen.dao.StandardLDao;
import org.liwang.entity.Group;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.sys.entity.Role;

/**
 * 分组dao
 * @author liwang
 *
 */
@MyBatisDao
public interface GroupDao extends StandardLDao<Group>{

	/**
	 * 查找单个分组的所有父级层次关系
	 * @param group
	 * @return
	 */
	public List<String> getPermByGroup(Group group);
	
	/**
	 * 根据传入的分组id集合和分组信息查找所有符合的分组的父级层次关系
	 * @param coll
	 * @param group
	 * @return
	 */
	public List<Group> getPermByGroups(@Param("coll")Collection<Integer> coll,@Param("group")Group group);
	
	/**
	 * 根据传入的分组id集合和分组信息查找所有符合的分组的子级层次关系
	 * @param coll
	 * @param group
	 * @return
	 */
	public List<Group> getPermByGroupsDown(@Param("coll")Collection<Integer> coll,@Param("group")Group group);
	
	/**
	 * 根据传入角色获取分组集合
	 * @param role
	 * @return
	 */
	public List<Group> getGroupByRole(Role role);//从关联表找id
	
	/**
	 * 根据传入角色集合返回分组id集合
	 * @param roles
	 * @return
	 */
	public Set<Integer> getGroupIdsByRoles(@Param("roles")Collection<Role> roles);
	
	/**
	 * 根据传入角色集合返回分组信息集合,层次查询,根据角色查找拥有的所有子分组
	 * @param roles
	 * @return
	 */
	public List<Group> getPermByRolesDown(@Param("roles")Collection<Role> roles);
	
	/**
	 * 根据传入的单个角色返回分组id集合
	 * @param role
	 * @return
	 */
	public Set<Integer> getGroupIdsByRole(Role role);
	
	/**
	 * 根据group信息删除
	 * @param group
	 * @return
	 */
	public int deleteGroupRoleByGroup(Group group);
	
	/**
	 * 添加分组(关联表默认role_id为父级id)
	 * @param group
	 * @return
	 */
	public int saveGroupRole(Group group);
	
	/**
	 * 根据角色id删除关联表中的数据
	 * @param role
	 * @return
	 */
	public int deleteGroupRole(Role role);
	
	/**
	 * 新增关联表数据
	 * @param role
	 * @return
	 */
	public int insertGroupRole(Role role);
	
}
