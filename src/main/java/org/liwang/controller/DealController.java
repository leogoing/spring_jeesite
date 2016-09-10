package org.liwang.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liwang.commen.controller.CommonLController;
import org.liwang.entity.Counterparty;
import org.liwang.entity.Deal;
import org.liwang.entity.Result;
import org.liwang.service.CounterpartyService;
import org.liwang.service.DealActService;
import org.liwang.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * 交易controller
 * @author liwang
 *
 */
@Controller
@RequestMapping("${adminPath}/gold/deal")
public class DealController extends CommonLController<DealService,Deal>{

	@Autowired
	private CounterpartyService counterpartyService;
	
	@Autowired
	private DealActService dealActService;
	
	/**
	 * 加载所有权限内交易对手如果为添加新交易
	 * @param request
	 * @param model
	 */
	@ModelAttribute
	public void loadCounterparty(HttpServletRequest request,Model model){
		String sufix=getURLSufix(request);
		if(sufix!=null && !sufix.startsWith("delete") && !sufix.startsWith("list") && !sufix.startsWith("update")){
			model.addAttribute("counterparties",counterpartyService.findAll(new Counterparty()));
		}
	}
	
	@RequestMapping("operateDetail")
	public String operateDetail(Deal deal,Model model,String flag){
		detail(deal, model);
		model.addAttribute("flag", flag);
		return "/gold/deal/dealForm";
	}
	
	@RequestMapping("startProcess")
	public String startProcess(Deal deal,RedirectAttributes redirectAttributes){
		Deal fullDeal=service.get(deal);
		Result<String> result=dealActService.startProcess(fullDeal);
		
		if(result.getStatus()==Result.SUCCESS){
			fullDeal.setProcessId(result.getData());
			service.update(fullDeal);
		}
		addMessage(redirectAttributes, result.getMessage());
		
		return "redirect:"+adminPath+"/gold/deal/list";
	}
	
	@RequestMapping("updateNoRedirect")
	@ResponseBody
	public Result<String> updateByAjax(Deal deal){
		int num=service.update(deal);
		if(num>0){
			return new Result<String>(Result.SUCCESS,"修改成功!");
		}
		return new Result<String>(Result.FAILED, "修改失败!");
	}
	
	@RequestMapping("reCheckList")
	public String reCheckList(Deal deal,HttpServletRequest request, HttpServletResponse response, Model model){
		deal.setApprove(Deal.NON_RECHECK);
		model.addAttribute("flag", "reCheck");
		list(deal, request, response, model);
		return "/gold/deal/operateDealList";
	}
	
	@RequestMapping("approveList")
	public String approveList(Deal deal,HttpServletRequest request, HttpServletResponse response, Model model){
		model.addAttribute("flag", "approve");
		list(deal, request, response, model);
		return "/gold/deal/operateDealList";
	}
	
	@RequestMapping("approve")
	@ResponseBody
	public List<Result<String>> approve(Deal deal){
		List<Deal> list=service.findAll(deal);//可以放行多个
		List<Result<String>> res = service.approves(list);
		return res;
	}
	
	@RequestMapping("reCheck")
	public String reCheck(Deal deal,HttpServletRequest request, RedirectAttributes redirectAttributes){
		deal.setApprove(Deal.RECHECKED);
		update(deal, redirectAttributes, request);
		return "redirect:"+adminPath+"/gold/deal/reCheckList";
	}
	
	@RequestMapping("reBack")
	public String reBack(Deal deal,HttpServletRequest request,RedirectAttributes redirectAttributes){
		deal.setApprove(Deal.NON_RECHECK);
		update(deal, redirectAttributes, request);
		return "redirect:"+adminPath+"/gold/deal/approveList";
	}
	
}
