package org.liwang.dao.manager;

import org.liwang.dao.SubjectDao;
import org.liwang.entity.Result;
import org.liwang.entity.Subject;
import org.liwang.manager.DefaultDaoManager;
import org.springframework.stereotype.Component;

/**
 * 科目
 * @author liwang
 *
 */
@Component
public class SubjectDaoManager extends DefaultDaoManager<Subject>{

	@Override
	protected void preOperate(Subject entity, String operatFlag) {
		setGroupStr(entity, operatFlag);
	}

	/**
	 * TODO  : 可以利用多线程接收所有要操作余额的操作请求放入一个队列中当该操作还在队列中并没有被执行时这
	 * 段时间若有进入队列里的相同科目的余额操作则先不放入队列而是在已经在队列里的相同科目操作的金额基础上进行加减
	 * (可以用到所有的数据库操作中!!!)
	 * @param subject
	 * @param money
	 * @return
	 */
	public Result<String> addBalance(Subject subject,Double money){
		dao().addBalance(subject, money);
		return null;
	}
	
	private SubjectDao dao(){
		return (SubjectDao)dao;
	}
	
	
}
