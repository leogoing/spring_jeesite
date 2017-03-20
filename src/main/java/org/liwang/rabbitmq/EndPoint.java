package org.liwang.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 初始化队列
 * @author Administrator
 *
 */
public abstract class EndPoint {

	protected Channel channel;
	
	protected Connection connection;
	
	protected String endPointName;
	
	public EndPoint(String endPointName) throws IOException {
		this.endPointName = endPointName;
		
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost("localhost");
		
		connection = connectionFactory.newConnection();
		
		channel = connection.createChannel();
		
		channel.queueDeclare(endPointName, false, false, false, null);
		
	}
	
	/**关闭连接
	 * @throws IOException 
	 */
	public void close() throws IOException {
		channel.close();
		connection.close();
	}
	
}
