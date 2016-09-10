package org.liwang.commen.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liwang.commen.entity.BaseLEntity;
import org.liwang.common.service.DefaultLService;
import org.liwang.entity.Variety;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.persistence.Page;

/**
 * 通用的controller用来放常用的list,form,update,delete等方法
 * @author liwang
 *
 * @param <T>
 */
public abstract class CommonLController<T extends DefaultLService<E>,E extends BaseLEntity<E>> 
																extends DefaultLController<T>{

	
	private static final Logger log=LoggerFactory.getLogger(CommonLController.class);
	
	/**
	 * RequestMapping注解的value值
	 */
	private String[] requestMappingVal;
	
	@RequestMapping("list")
	public String list(
			E entity,HttpServletRequest request, HttpServletResponse response, Model model){
		Page<E> page=new Page<E>(request, response);
		entity.setPage(page);
		page.setList(service.findAll(entity));
		model.addAttribute("page", page);
		
		return entity.viewPrefix()+"List";
	}
	
	@RequestMapping("detail")
	public String detail(E entity,Model model){
		model.addAttribute(entity.alias(),service.get(entity));
		model.addAttribute("flag","view");
		return entity.viewPrefix()+"Form";
	}
	
	@RequestMapping("update")
	public String update(E entity,RedirectAttributes redirectAttributes,HttpServletRequest request){
		int size=service.update(entity);
		if(size==0){
			addMessage(redirectAttributes, "修改失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功修改"+size+"个!");
		}
		
		return "redirect:"+redirectSelfPath(request)+"/list";
	}
	
	@RequestMapping("delete")
	public String delete(E entity,RedirectAttributes redirectAttributes,HttpServletRequest request){
		int size=service.delete(entity);
		if(size==0){
			addMessage(redirectAttributes, "删除失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功删除"+size+"个!");
		}
		return "redirect:"+redirectSelfPath(request)+"/list";
	}
	
	@RequestMapping("preAdd")
	public String preAdd(Model model,E entity){
		model.addAttribute(entity.alias(), entity.initObject());
		model.addAttribute("flag","add");
		return entity.viewPrefix()+"Form";
	}
	
	@RequestMapping("preUpdate")
	public String preUpdate(Model model,E entity){
		model.addAttribute(entity.alias(),service.get(entity));
		model.addAttribute("flag","update");
		return entity.viewPrefix()+"Form";
	}
	
	@RequestMapping("add")
	public String add(E entity,RedirectAttributes redirectAttributes,HttpServletRequest request){
		int size=service.add(entity);
		if(size==0){
			addMessage(redirectAttributes, "添加失败!");
		}else if(size>0){
			addMessage(redirectAttributes, "成功添加"+size+"个!");
		}
		return "redirect:"+redirectSelfPath(request)+"/list";
	}
	
	/**
	 * 获取用于重定向的当前的url(不包含最后一个/)
	 * @param request
	 * @return
	 */
	protected String redirectSelfPath(HttpServletRequest request){
		if(requestMappingVal == null){
			String[] annoVal=getClass().getAnnotation(RequestMapping.class).value();
			if(log.isDebugEnabled()){
				for(String str : annoVal){
					log.debug("当前controller的请求注解值: "+str);
				}
			}
			requestMappingVal=annoVal;
		}
		
		if(requestMappingVal !=null && requestMappingVal.length==1){
			return adminPath+requestMappingVal[0].substring(requestMappingVal[0].indexOf("/"));
		}else{
			String url=request.getRequestURL().toString();
			return url.substring(0,url.lastIndexOf("/"));
		}
		
	}
	
	/**获取访问url的后缀*/
	protected String getURLSufix(HttpServletRequest request){
		String url=request.getRequestURL().toString();
		return url.substring(url.lastIndexOf("/")+1);
	}
	
}
