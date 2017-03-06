package org.liwang.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.liwang.commen.controller.DefaultLController;
import org.liwang.entity.Group;
import org.liwang.service.GroupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 分组controller
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/sys/group")
public class GroupController extends DefaultLController<GroupService>{

	@RequestMapping("")
	public String index(){
		return "gold/group/groupIndex";
	}
	
	@RequestMapping("list")
	public String list(Group group,Model model){
		model.addAttribute("list",service.findByRoles());
		return "gold/group/groupList";
	}
	
	@RequestMapping("form")
	public String form(Group group,Model model){
		model.addAttribute("group",service.get(group));
		model.addAttribute("flag","view");
		return "gold/group/groupForm";
	}
	
	@RequestMapping("preAdd")
	public String preAdd(Group group,Model model){
		model.addAttribute("flag","add");
		return "gold/group/groupForm";
	}
	
	@RequestMapping("add")
	public String add(Group group,RedirectAttributes redirectAttributes){
		int size=service.add(group);
		if(size==0){
			addMessage(redirectAttributes, "添加失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功添加"+size+"个!");
		}
		return "redirect:"+adminPath+"/sys/group/list";
	}
	
	@RequestMapping("delete")
	public String delete(Group group,RedirectAttributes redirectAttributes){
		int size=service.delete(group);
		if(size==0){
			addMessage(redirectAttributes, "删除失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功删除"+size+"个!");
		}
		return "redirect:"+adminPath+"/sys/group/list";
	}
	
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String node){
		List<Group> list=null;
		
		if(node!=null && node.trim().length()>0){
			list=service.findByGroupAndRolesDown(new Group(node));
		}else{
			list=service.findByRoles();
		}
		
		return service.treeData(list);
	}
	
}
