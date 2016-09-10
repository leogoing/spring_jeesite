package org.liwang.dao;

import org.apache.ibatis.annotations.Param;
import org.liwang.commen.dao.StandardLDao;
import org.liwang.entity.Variety;

import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;

/**
 * 品种dao
 * @author liwang
 *
 */
@MyBatisDao
public interface VarietyDao extends StandardLDao<Variety>{

	
	public int addStorage(Variety variety,@Param("storage")Long storage);
	
}
