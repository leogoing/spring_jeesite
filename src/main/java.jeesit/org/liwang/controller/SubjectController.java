package org.liwang.controller;

import java.util.List;
import java.util.Map;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Subject;
import org.liwang.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 科目
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/subject")
public class SubjectController extends CommonLController<SubjectService, Subject>{

	@ResponseBody
	@RequestMapping("treeData")
	public List<Map<String,Object>> treeData(Subject subject){
		List<Subject> list=service.findAll(subject);
		return service.treeData(list);
	}
	
}
