package org.liwang.dao;

import org.apache.ibatis.annotations.Param;
import org.liwang.commen.dao.StandardLDao;
import org.liwang.entity.Subject;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;

/**
 * 科目
 * @author liwang
 *
 */
@MyBatisDao
public interface SubjectDao extends StandardLDao<Subject>{

	public int addBalance(Subject subject,@Param("balance")Double money);
	
}
