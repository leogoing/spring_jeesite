package org.liwang.main;

import org.liwang.rabbitmq.spring.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * rabbitmq
 * @author Administrator
 *
 */
//@Controller
@RequestMapping("/rabbitmq")
public class RabbitMQController {

	
	@Autowired
	private MQProducer producer;
	
	
	@ResponseBody
	@RequestMapping("/hellowWorld")
	public String hellowWorld(){
		
		producer.sendDataToQueue("test_queue_key", "hellowWorld!");
		return "ok";
	}
	
}
