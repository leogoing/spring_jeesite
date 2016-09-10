package org.liwang.service;

import java.util.List;

import org.liwang.common.service.DefaultLService;
import org.liwang.dao.manager.VarietyDaoManager;
import org.liwang.entity.Variety;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 品种service
 * @author liwang
 *
 */
@Service
@Transactional(readOnly=true)
public class VarietyService extends DefaultLService<Variety>{

	/**添加品种库存*/
	public int addStorage(List<Variety> varieties){
		
		for(Variety variety : varieties){
			daoManager().addStorage(variety, variety.getStorage());
		}
		
		return 0;
	}
	
	
	//TODO:...clear
	public int delete(Variety variety){
		variety.setStatus(2);
		return daoManager.update(variety);
	}
	
	public Variety get(String id){
		List<Variety> list=findAll(new Variety(Integer.parseInt(id)));
		if(list ==null || list.size()==0){
			return new Variety();
		}
		return list.get(0);
	}
	
	public Variety get(Variety variety){
		List<Variety> list=findAll(variety);
		if(list ==null || list.size()==0){
			return variety;
		}
		return list.get(0);
	}
	
	private VarietyDaoManager daoManager(){
		return (VarietyDaoManager)daoManager;
	}
	
}
