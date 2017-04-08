package org.liwang.webqq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.liwang.webqq.MessageHandler.Message;
import org.liwang.webqq.MessageHandler.MessageData;
import org.liwang.webqq.MessageHandler.MessageListenerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 简单网页qq案例
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/webqq")
public class WebQQController {

	
	@Autowired
	private ChannelFactory channelFactory;
	
	@Autowired
	private MessageHandler messageHandler;
	
	/**
	 * 群聊页面，创建群聊队列
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("/crowdTalk")
	public String crowdTalk(HttpServletRequest request) throws IOException{
		
		channelFactory.createCrowdTalkQueue();
		
		return "crowdTalk";
	}
	
	@ResponseBody
	@RequestMapping("/messageMonitor")
	public DeferredResult<MessageHandler.Message> messageMonitor(HttpServletRequest request) throws IOException{
		//将DeferredResult存到session中
		HttpSession session = request.getSession();
		MessageListenerImpl message = (MessageListenerImpl)session.getAttribute("messageListener"); 
		
		System.out.println("messageMonitor##:"+message);
		if(message == null){
			message = new MessageListenerImpl(new DeferredResult<MessageHandler.Message>());
			session.setAttribute("messageListener", message);
		}else{
			message.setResult(new DeferredResult<MessageHandler.Message>());
		}
		
		messageHandler.addListener(message);
		return message.getResult();
	}
	
	
	@ResponseBody
	@RequestMapping("/sendMessage")
	public Map<String,String> sendMessage(HttpServletRequest request,String mes) throws IOException{
		
		channelFactory.sendMessage(new Message(
				request.getHeader("x-forwarded-for") == null?request.getRemoteAddr():request.getHeader("x-forwarded-for"),mes));
		
		return new HashMap<String,String>();
	}
	
}
