package org.liwang.controller;

import java.util.List;
import java.util.Map;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Condition;
import org.liwang.service.ConditionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 条件
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/condition")
public class ConditionController extends CommonLController<ConditionService, Condition>{

	
	@ResponseBody
	@RequestMapping("treeData")
	public List<Map<String,Object>> treeData(Condition condition){
		List<Condition> list=service.findAll(condition);
		return service.treeData(list);
	}
	
}
