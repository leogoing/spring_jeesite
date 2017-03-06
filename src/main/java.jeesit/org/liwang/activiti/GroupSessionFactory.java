package org.liwang.activiti;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * activiti角色管理工厂类
 * @author liwang
 *
 */
public class GroupSessionFactory implements SessionFactory{

	@Autowired
	private GroupSession session;
	
	@Override
	public Class<?> getSessionType() {
		return GroupIdentityManager.class;
	}

	@Override
	public Session openSession() {
		return session;
	}

}
