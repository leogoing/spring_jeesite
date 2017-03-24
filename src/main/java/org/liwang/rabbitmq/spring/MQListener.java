package org.liwang.rabbitmq.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Service;

/**
 * 监听器
 * @author Administrator
 *
 */
//@Service("MQListener")
public class MQListener implements MessageListener{

	private static final Logger log = LoggerFactory.getLogger(MQListener.class);
	
	
	@Override
	public void onMessage(Message message) {
		log.info("接收的消息："+message);
	}

}
