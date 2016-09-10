package org.liwang.entity;

import java.io.Serializable;

/**
 * 结果bean
 * @author liwang
 *
 */
public class Result<T> implements Serializable{

	private static final long serialVersionUID = 1L;

	/**成功*/
	public static final int SUCCESS=0;
	/**失败*/
	public static final int FAILED=1;
	/**错误*/
	public static final int ERROR=2;
	
	/**状态*/
	private int status;

	/**信息*/
	private String message;

	/**内容*/
	private T data;
	
	public Result(){}
	public Result(int status){
		this.status=status;
	}
	
	public Result(int status,String message){
		this(status);
		this.message=message;
	}
	
	public Result(int status,String message,T data){
		this(status, message);
		this.data=data;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "Result [status=" + status + ", message=" + message + ", data=" + data + "]";
	}
	
	
	
}
