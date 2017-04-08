package org.liwang.webqq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

import org.liwang.webqq.MessageHandler.Message;
import org.liwang.webqq.MessageHandler.MessageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;


/**
 * 创建信道连接
 * @author Administrator
 *
 */
@Service
public class ChannelFactory implements InitializingBean{

	private static final Logger log = LoggerFactory.getLogger(ChannelFactory.class);
	
	@Value("#{rabbitmq['mq.host']}")
	private String host;
	@Value("#{rabbitmq['mq.username']}")
	private String username;
	@Value("#{rabbitmq['mq.password']}")
	private String password;
	@Value("#{rabbitmq['mq.port']}")
	private int port;
	@Value("#{rabbitmq['mq.vhost']}")
	private String vhost;
	
	private ConnectionFactory connectionFactory = new ConnectionFactory();
	
	private Connection connection;
	
	private Channel crowdTalkProductChannel;
	
	private Channel crowdTalkConsumerChannel;
	
	private static final String crowdTalk = "crowdTalk";
	
	@Autowired
	private MessageHandler messageHandler;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		connectionFactory.setHost(host);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setPort(port);
		connectionFactory.setVirtualHost(vhost);
		connection = connectionFactory.newConnection();
		createCrowdTalkQueue();
		startMonitor();
	}

	/**
	 * 创建群队列
	 * @throws IOException 
	 */
	public void createCrowdTalkQueue() throws IOException{
		if(crowdTalkProductChannel == null || !crowdTalkProductChannel.isOpen())
			crowdTalkProductChannel = connection.createChannel();
		
		
		crowdTalkProductChannel.exchangeDeclare(crowdTalk+".exchange", "direct", true, false, false, null);
		DeclareOk qOk = crowdTalkProductChannel.queueDeclare(crowdTalk+".queue", true, false, false, null);
		crowdTalkProductChannel.queueBind(crowdTalk+".queue", crowdTalk+".exchange", crowdTalk+".route", null);
		
		log.info("##:"+qOk.getQueue());
	}
	
	/**监听消息*/
	public void startMonitor() throws IOException{
		if(crowdTalkConsumerChannel == null || !crowdTalkConsumerChannel.isOpen())
			crowdTalkConsumerChannel = connection.createChannel();
		
		Consumer consumer = new DefaultConsumer(crowdTalkConsumerChannel){
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
				String mes = new String(body,"UTF-8");
				log.info("handleDelivery:"+mes);
				String ip = mes.split("-")[0];
				String val = mes.split("-")[1];
				messageHandler.sendMessage(new MessageData<MessageHandler.Message>(new Message(ip, val)));
				crowdTalkConsumerChannel.basicAck(envelope.getDeliveryTag(), false);
			}
		};
		crowdTalkConsumerChannel.basicConsume(crowdTalk+".queue", false, crowdTalk+".consumer", false, false, null, consumer);
	}
	
	/**发送消息
	 * @throws IOException */
	public void sendMessage(Message mes) throws IOException{
		log.info(mes.getMes());
		/*ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oo =new ObjectOutputStream(os);
		oo.writeObject(mes.get);
		byte[] b = os.toByteArray();
		os.close();
		oo.close();*/
		String s = mes.getIp()+"-"+mes.getMes();
		crowdTalkProductChannel.basicPublish(crowdTalk+".exchange", crowdTalk+".route", true, false,MessageProperties.BASIC ,s.getBytes());
	}
	
}
