package org.liwang.service;

import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

/**
 * 通知activiti任务
 * @author liwang
 *
 */
public class AdviceActService implements ActivityBehavior{

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ActivityExecution execution) throws Exception {
		//TODO  通知放行人交割失败
	}

}
