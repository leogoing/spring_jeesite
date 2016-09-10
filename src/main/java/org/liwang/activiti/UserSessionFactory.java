package org.liwang.activiti;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户
 * @author liwang
 *
 */
public class UserSessionFactory implements SessionFactory{

	@Autowired
	private UserSession userSession;
	
	@Override
	public Class<?> getSessionType() {
		return UserIdentityManager.class;
	}

	@Override
	public Session openSession() {
		return userSession;
	}

}
