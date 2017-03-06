package org.liwang.controller;

import javax.servlet.http.HttpServletRequest;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.SoundPrice;
import org.liwang.entity.Variety;
import org.liwang.service.SoundPriceService;
import org.liwang.service.VarietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 公允价格controller
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/soundPrice")
public class SoundPriceController extends CommonLController<SoundPriceService, SoundPrice>{

	@Autowired
	private VarietyService varietyService;
	
	/**
	 * 加载所有权限内交易对手如果为添加新交易
	 * @param request
	 * @param model
	 */
	@ModelAttribute
	public void loadCounterparty(HttpServletRequest request,Model model){
		String url=request.getRequestURL().toString();
		String sufix=url.substring(url.lastIndexOf("/")+1);
		if(sufix!=null && !sufix.startsWith("delete") && !sufix.startsWith("update")){
			model.addAttribute("varieties",varietyService.findAll(new Variety()));
		}
	}
	
}
