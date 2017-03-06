package org.liwang.dao.manager;

import org.liwang.entity.SoundPrice;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 
 * @author liwang
 *
 */
@Component
public class SoundPriceDaoManager extends DefaultDaoManager<SoundPrice>{

	@Override
	protected void preOperate(SoundPrice entity, String operatFlag) {
		setGroupStr(entity, operatFlag);
	}

}
