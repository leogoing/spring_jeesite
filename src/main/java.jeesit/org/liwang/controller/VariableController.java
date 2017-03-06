package org.liwang.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Variable;
import org.liwang.service.VariableService;
import org.liwang.util.AnnoUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
 * 变量
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/variable")
public class VariableController extends CommonLController<VariableService, Variable>{

	@RequestMapping("add")
	public String add(Variable variable,RedirectAttributes redirectAttributes,HttpServletRequest request){
		String dict=variable.getDict();
		if(dict==null || "".equals(dict)){
			String path=variable.getVariablePath();
			variable.setDict(AnnoUtil.getVariableDict(path));
		}
		
		int size=service.add(variable);
		if(size==0){
			addMessage(redirectAttributes, "添加失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功添加"+size+"个!");
		}
		return "redirect:"+redirectSelfPath(request)+"/list";
	}
	
	/**条件页面显示*/
	@RequestMapping("jsonList")
	@ResponseBody
	public List<Variable> jsonList(Variable variable){
		List<Variable> variables=service.findAll(variable);
		for(Variable var : variables){
			List<Dict> dicts=DictUtils.getDictList(var.getDict());
			var.setDictVal(dicts);
		}
		
		return variables;
	}
	
	/**添加变量的树*/
	@RequestMapping("treeData")
	@ResponseBody
	public List<Map<String,Object>> treeData(Variable variable){
		return service.treeData(variable);
	}
	
	/**依赖变量的树*/
	@ResponseBody
	@RequestMapping("variableTree")
	public List<Map<String,Object>> variableTree(Variable variable){
		return service.variableTree(variable);
	}
	
}
