package org.liwang.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 消费者
 * @author Administrator
 *
 */
public class MQConsumer extends EndPoint implements Runnable,Consumer{

	public MQConsumer(String endPointName) throws IOException {
		super(endPointName);
	}

	/**
	 * 注册成功回调方法
	 */
	@Override
	public void handleConsumeOk(String consumerTag) {
		
	}

	@Override
	public void handleCancelOk(String consumerTag) {
		
	}

	@Override
	public void handleCancel(String consumerTag) throws IOException {
		
	}

	/**
	 * 消息到达的回调方法
	 */
	@Override
	public void handleDelivery(String arg0, Envelope arg1,
			BasicProperties arg2, byte[] arg3) throws IOException {
		
	}

	@Override
	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException sig) {
		
	}

	@Override
	public void handleRecoverOk(String consumerTag) {
		
	}

	@Override
	public void run() {
		
	}
	
	
	
	

}
