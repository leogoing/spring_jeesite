package org.liwang.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liwang.commen.controller.DefaultController;
import org.liwang.dao.VarietyDao;
import org.liwang.entity.Variety;
import org.liwang.manager.AuthManager;
import org.liwang.manager.DefaultDaoManager;
import org.liwang.service.VarietyService;
import org.liwang.util.ShiroUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;

/**
 * 贵金属品种controller
 * @author liwang
 *
 */

@Controller
@RequestMapping("${adminPath}/gold/variety")
public class VarietyController extends DefaultController<VarietyService>{

	
	@RequestMapping("list")
	public String list(Variety variety,HttpServletRequest request, HttpServletResponse response, Model model){
//		manager.getAuth();
		Page<Variety> page=new Page<Variety>(request, response);
		variety.setPage(page);
		page.setList(service.findAll(variety));
		model.addAttribute("page", page);
		return "gold/deal/varietyList";
	}
	
	@RequestMapping("detail")
	public String detail(Variety variety,Model model){
		model.addAttribute("variety",service.get(variety));
		model.addAttribute("flag","view");
		return "gold/deal/varietyForm";
	}
	
	@RequestMapping("update")
	public String update(Variety variety,RedirectAttributes redirectAttributes){
		int size=service.update(variety);
		if(size==0){
			addMessage(redirectAttributes, "修改失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功修改"+size+"个!");
		}
		return "redirect:"+adminPath+"/gold/variety/list";
	}
	
	@RequestMapping("delete")
	public String delete(Variety variety,RedirectAttributes redirectAttributes){
		int size=service.delete(variety);
		if(size==0){
			addMessage(redirectAttributes, "删除失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功删除"+size+"个!");
		}
		return "redirect:"+adminPath+"/gold/variety/list";
	}
	
	@RequestMapping("preAdd")
	public String preAdd(Model model){
		model.addAttribute("variety", Variety.defVariety());
		model.addAttribute("flag","add");
		return "gold/deal/varietyForm";
	}
	
	@RequestMapping("preUpdate")
	public String preUpdate(Model model,String id){
		model.addAttribute("variety",service.get(id));
		model.addAttribute("flag","update");
		return "gold/deal/varietyForm";
	}
	
	@RequestMapping("add")
	public String add(Variety variety,RedirectAttributes redirectAttributes){
		int size=service.add(variety);
		if(size==0){
			addMessage(redirectAttributes, "添加失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功添加"+size+"个!");
		}
		return "redirect:"+adminPath+"/gold/variety/list";
	}
	
}
