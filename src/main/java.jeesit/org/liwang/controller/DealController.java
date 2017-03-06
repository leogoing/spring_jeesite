package org.liwang.controller;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.liwang.commen.controller.CommonLController;
import org.liwang.commen.entity.BaseLEntity;
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

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.modules.act.entity.Act;
import com.thinkgem.jeesite.modules.act.service.ActTaskService;


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
	
	@Autowired
	private ActTaskService actTaskService;
	
	/**
	 * 加载所有权限内交易对手如果为添加新交易
	 * @param request
	 * @param model
	 */
	@ModelAttribute
	public void loadCounterparty(HttpServletRequest request,Model model){
		String sufix=getURLSufix(request);
		if(sufix!=null && !sufix.startsWith("add")){
			model.addAttribute("counterparties",counterpartyService.findAll(new Counterparty()));
		}
	}
	
	/**放行复核明细页面*/
	@RequestMapping("operateDetail")
	public String operateDetail(Deal deal,Model model,String flag,String taskId,String procInsId,String actStatus){
		detail(deal, model);
		model.addAttribute("flag", flag);
		model.addAttribute("taskId", taskId);
		model.addAttribute("procInsId",procInsId);
		model.addAttribute("actStatus",actStatus);
		return "/gold/deal/dealForm";
	}
	
	/**启动流程*/
	@RequestMapping("startProcess")
	public String startProcess(Deal deal,RedirectAttributes redirectAttributes){
		Deal fullDeal=service.get(deal);
		Result<String> result=dealActService.startProcess(fullDeal);
		
		if(result.getStatus()==Result.SUCCESS){
			fullDeal.setprocInsId(result.getData());
			service.update(fullDeal);
		}
		addMessage(redirectAttributes, result.getMessage());
		
		return "redirect:"+adminPath+"/gold/deal/list";
	}
	
	/**
	 * 签收交易
	 * @param taskId
	 * @return
	 */
	@RequestMapping("claim")
	@ResponseBody
	public Result<String> claim(String taskId){
		return dealActService.claim(taskId);
	}
	
	/**
	 * 委派交易任务
	 * @return
	 */
	@RequestMapping("appoint")
	@ResponseBody
	public Result<String> appoint(String appointId,String taskId){
		actTaskService.delegateTask(taskId, appointId);
		return new Result<String>(Result.SUCCESS,"委派成功!");
	}
	
	/**
	 * 反签收，取消办理
	 * @param taskId
	 * @return
	 */
	@RequestMapping("cancel")
	@ResponseBody
	public Result<String> cancel(String taskId){
		return dealActService.cancel(taskId);
	}
	
	@RequestMapping("updateNoRedirect")
	@ResponseBody
	public Result<String> updateByAjax(Deal deal,String taskId,String procInsId){
		int num=service.update(deal);
		if(num>0){
			Map<String,Object> var=Maps.newHashMap();
			var.put("recheckFlag", "update");
			var.put("taskDefKey","recheck");
			var.put("recheckAgain", "Y");
			var.put("deal", deal);
			actTaskService.complete(taskId, procInsId, "", var);
			return new Result<String>(Result.SUCCESS,"修改成功!");
		}
		return new Result<String>(Result.FAILED, "修改失败!");
	}
	
	@RequestMapping("reCheckList")
	public String reCheckList(Deal deal,HttpServletRequest request, HttpServletResponse response, Model model){
		model.addAttribute("flag", "reCheck");
		model.addAttribute("page", operateList(deal, request, response, "recheck"));
		return "/gold/deal/operateDealList";
	}
	
	@RequestMapping("approveList")
	public String approveList(Deal deal,HttpServletRequest request, HttpServletResponse response, Model model){
		model.addAttribute("flag", "approve");
		model.addAttribute("page", operateList(deal, request, response, "approve"));
		return "/gold/deal/operateDealList";
	}
	
	private Page<Act> operateList(Deal deal,HttpServletRequest request, HttpServletResponse response,String flag){
		Page<Act> page=new Page<Act>(request, response);
		page.setList(dealActService.operateList(flag));
		return page;
	}
	
	/**
	 * TODO  加提交意见
	 * 取消流程
	 * @param taskId
	 * @param procInsId
	 * @return
	 */
	@RequestMapping("stop")
	@ResponseBody
	public Result<String> stop(String taskId,String procInsId,Deal deal){
		Map<String,Object> var=Maps.newHashMap();
		var.put("recheckFlag", "stop");
		actTaskService.complete(taskId, procInsId, "", var);
		deal.setStatus(BaseLEntity.STATUS_DELETE);
		service.update(deal);
		return new Result<String>(Result.SUCCESS,"正在停止!");
	}
	
	@RequestMapping("approve")
	@ResponseBody
	public Result<String> approve(Deal deal,String taskId,String procInsId){
		return dealActService.approve(deal,taskId, procInsId);
	}
	
	@RequestMapping("reCheck")
	public String reCheck(Integer id,String taskId,String procInsId,
							HttpServletRequest request, RedirectAttributes redirectAttributes){
		Deal deal=new Deal(id);
		deal.setApprove(Deal.RECHECKED);
		int num =service.update(deal);
		if(num>0){
			Map<String,Object> vars=Maps.newHashMap();
			vars.put("recheckFlag", "rechecked");
			actTaskService.complete(taskId, procInsId, "", vars);
			addMessage(redirectAttributes, "复核成功!");
		}else{
			addMessage(redirectAttributes, "复核失败!");
		}
		return "redirect:"+adminPath+"/gold/deal/reCheckList";
	}
	
	@RequestMapping("reBack")
	public String reBack(Deal deal,String taskId,String procInsId,
						HttpServletRequest request,RedirectAttributes redirectAttributes){
		deal.setApprove(Deal.NON_RECHECK);
		update(deal, redirectAttributes, request);
		
		Map<String,Object> vars=Maps.newHashMap();
		vars.put("approveFlag", "reback");
		vars.put("recheckAgain", "Y");
		actTaskService.complete(taskId, procInsId, "", vars);
		
		return "redirect:"+adminPath+"/gold/deal/approveList";
	}
	
}
