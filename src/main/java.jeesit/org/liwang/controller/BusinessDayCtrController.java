package org.liwang.controller;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.BusinessDayCtr;
import org.liwang.entity.BusinessDayDetail;
import org.liwang.service.BusinessDayCtrService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 营业日管理
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/businessDayCtr")
public class BusinessDayCtrController extends CommonLController<BusinessDayCtrService,BusinessDayCtr>{

	@RequestMapping("historyList")
	public String history(BusinessDayDetail businessDayDetail,Model model){
		model.addAttribute("BusinessDayDetail",service.findBusinessHistory(businessDayDetail));
		return "gold/deal/businessDayDetailList";
	}
	
}
