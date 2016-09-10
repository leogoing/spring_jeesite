package org.liwang.activiti;

import java.util.List;

import org.activiti.engine.identity.Group;
import org.liwang.util.ActivitiUtil;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.modules.act.service.ext.ActUserEntityService;

/**
 * activiti用户
 * @author liwang
 *
 */
@Service
public class UserSession extends ActUserEntityService{

	/**
	 * 根据用户id查找该用户所属的角色(将权限字符串最后一段封装为Group)
	 * @param userId 用户登录名
	 */
	public List<Group> findGroupsByUser(String userId) {
//		return getDbSqlSession().selectList("selectGroupsByUserId", userId);
		
		return ActivitiUtil.findGroupsByUser(userId);
	}
	
	
}
