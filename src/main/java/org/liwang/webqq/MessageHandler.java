package org.liwang.webqq;

import java.io.Serializable;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 消息分发
 * @author Administrator
 *
 */
@Service
public class MessageHandler {

	/**存监听器*/
	private static List<MessageListener> list = new CopyOnWriteArrayList<MessageListener>();
	
	public void addListener(MessageListener listener){
		list.add(listener);
	}
	
	public void sendMessage(MessageData<Message> mes){
		for(MessageListener e : list)
			e.handler(mes);
	}
	
	
	public static class MessageData<T> extends EventObject{
		private static final long serialVersionUID = 1L;
		public MessageData(T source) {
			super(source);
		}
		@SuppressWarnings("unchecked")
		public T getSource() {
	        return (T)super.getSource();
	    }
	}
	
	public static interface MessageListener extends EventListener{
		void handler(MessageData<Message> mes);
	}
	
	public static class Message implements Serializable{
		private static final long serialVersionUID = 1L;
		private String ip;
		private String mes;
		public Message(String ip,String mes){
			this.ip = ip;
			this.mes = mes;
		}
		public String getIp() {
			return ip;
		}
		public String getMes() {
			return mes;
		}
		
	}
	
	public static class MessageListenerImpl implements MessageListener{
		private final DeferredResult<Message> result;
		public MessageListenerImpl(DeferredResult<Message> result) {
			this.result = result;
		}
		public DeferredResult<Message> getResult(){
			return result;
		}
		@Override
		public void handler(MessageData<Message> mes) {
			result.setResult(mes.getSource());
			list.remove(this);
		}
	}
	
	
	
	
}
