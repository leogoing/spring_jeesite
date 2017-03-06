package org.liwang.controller;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Variety;
import org.liwang.service.VarietyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 贵金属品种controller
 * @author liwang
 *
 */

@Controller
@RequestMapping("${adminPath}/gold/variety")
public class VarietyController extends CommonLController<VarietyService,Variety>{

	
	
}
