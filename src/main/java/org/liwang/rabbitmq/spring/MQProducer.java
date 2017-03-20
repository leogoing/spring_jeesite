package org.liwang.rabbitmq.spring;

/**
 * rabbitmq生产者接口
 * @author Administrator
 *
 */
public interface MQProducer {

	
	
	/**
     * 发送消息到指定队列
     * @param queueKey
     * @param object
     */
    public void sendDataToQueue(String queueKey, Object object);
	
}
