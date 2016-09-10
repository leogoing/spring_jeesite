package org.liwang.activiti;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.liwang.util.ActivitiUtil;
import org.springframework.stereotype.Service;

/**
 * activiti角色分组类
 * @author liwang
 *
 */
@Service
public class GroupSession extends GroupEntityManager{


	/**
	 * 根据用户id查找该用户所属的角色(将权限字符串最后一段封装为Group)
	 * @param userId 用户登录名
	 */
	public List<Group> findGroupsByUser(String userId) {
//		return getDbSqlSession().selectList("selectGroupsByUserId", userId);
		
		return ActivitiUtil.findGroupsByUser(userId);
	}
	
}
