package org.liwang.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.liwang.commen.controller.DefaultController;
import org.liwang.entity.Group;
import org.liwang.service.GroupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 分组controller
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/sys/group")
public class GroupController extends DefaultController<GroupService>{

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
		return "gold/group/groupForm";
	}
	
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(String node){
		List<Group> list=null;
		
		if(node!=null && node.trim().length()>0){
			list=service.findByGroupAndRoles(new Group(node));
		}else{
			list=service.findByRoles();
		}
		
		List<Map<String, Object>> mapList = Lists.newArrayList();
		for(Group group:list){
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", group.getId());
			map.put("pId", group.getParentId());
			map.put("group", StringUtils.trim(group.getGroupId()));
			map.put("name", group.getGroupName());
			map.put("isParent", false);
			mapList.add(map);
		}
		
		return mapList;
	}
	
}
