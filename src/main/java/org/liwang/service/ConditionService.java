package org.liwang.service;

import java.util.List;
import java.util.Map;

import org.liwang.common.service.OperabilityLService;
import org.liwang.entity.Condition;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 条件
 * @author liwang
 *
 */
@Service
public class ConditionService extends OperabilityLService<Condition>{

	
	public List<Map<String,Object>> treeData(List<Condition> list){
		List<Map<String,Object>> mapList=Lists.newArrayList();
		
		for(Condition condition : list){
			Map<String,Object> map=Maps.newHashMap();
			map.put("id", condition.getId());
			map.put("name", condition.getName());
			mapList.add(map);
		}
		
		return mapList;
	}
	
}
