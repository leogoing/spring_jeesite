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

	public List<String> getPermByGroup(Group group);
	
	public List<Group> getPermByGroups(@Param("coll")Collection<Integer> coll,@Param("group")Group group);
	
	public List<Group> getGroupByRole(Role role);//从关联表找id
	
	public Set<Integer> getGroupIdsByRoles(@Param("roles")Collection<Role> roles);
	
	public List<Group> getGroupInfoByRoles(@Param("roles")Collection<Role> roles);
	
	public Set<Integer> getGroupIdsByRole(Role role);
	
	public int deleteGroupRole(Role role);
	
	public int insertGroupRole(Role role);
	
}
