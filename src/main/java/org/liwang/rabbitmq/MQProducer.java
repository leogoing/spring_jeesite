package org.liwang.rabbitmq;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

/**
 * 生产者
 * @author Administrator
 *
 */
public class MQProducer extends EndPoint{

	public MQProducer(String endPointName) throws IOException {
		super(endPointName);
	}

	public void sendMessage(Serializable object) throws IOException{
		channel.basicPublish("", endPointName, null , SerializationUtils.serialize(object));
	}
	
}
