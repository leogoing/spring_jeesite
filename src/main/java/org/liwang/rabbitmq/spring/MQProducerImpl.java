package org.liwang.rabbitmq.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 生产者
 * @author Administrator
 *
 */
@Service
public class MQProducerImpl implements MQProducer{
	
	private final static Logger log = LoggerFactory.getLogger(MQProducerImpl.class);

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Override
	public void sendDataToQueue(String queueKey, Object object) {
		log.info("开始发送消息。。。");
		amqpTemplate.convertAndSend(queueKey, object);
	}

}
