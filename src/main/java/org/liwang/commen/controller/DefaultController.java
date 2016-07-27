package org.liwang.commen.controller;

import org.liwang.common.service.AbstractLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 默认controller
 * @author liwang
 *
 * @param <T>
 */
public class DefaultController<T extends AbstractLService> extends AbstractLController{

	@Autowired
	protected T service;
	
	@Value("${adminPath}")
	protected String adminPath;
	
	/**
	 * 添加Flash消息,添加消息到flash保存用于重定向获取
	 * @param message
	 */
	protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
		StringBuilder sb = new StringBuilder();
		for (String message : messages){
			sb.append(message).append(messages.length>1?"<br/>":"");
		}
		redirectAttributes.addFlashAttribute("message", sb.toString());
	}
	
}
