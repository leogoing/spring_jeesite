package org.liwang.dao.manager;

import org.liwang.entity.Template;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 模板
 * @author liwang
 *
 */
@Component
public class TemplateDaoManager extends DefaultDaoManager<Template>{

	@Override
	protected void preOperate(Template entity, String operatFlag) {
		setGroupStr(entity, operatFlag);
	}

}
