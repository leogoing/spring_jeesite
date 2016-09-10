package org.liwang.controller;

import javax.servlet.http.HttpServletRequest;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Operate;
import org.liwang.service.OperateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 操作明细controller
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/operate")
public class OperateController extends CommonLController<OperateService, Operate>{

	/**
	 * 拦截不为查询的请求
	 */
	@ModelAttribute
	public void intercetor(HttpServletRequest request){
		String url=request.getRequestURL().toString();
		String sufix=url.substring(url.lastIndexOf("/")+1);
		if(sufix!=null && !sufix.startsWith("list") && !sufix.startsWith("detail")){
			throw new RuntimeException("不允许操作明细数据!!!");
		}
	}
	
}
