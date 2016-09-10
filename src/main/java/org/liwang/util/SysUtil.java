package org.liwang.util;

/**
 * 系统工具(包含系统资源硬件资源线程信息等)
 * @author liwang
 *
 */
public abstract class SysUtil {

	
	private final static Runtime runtime=Runtime.getRuntime();
	
	/**最大内存*/
	private static long maxMemory= runtime.maxMemory();
	
	private static int nucleus=runtime.availableProcessors();
	
	/**
	 * 获取处理器核心数
	 * @return
	 */
	public static int getNucleus(){
		return nucleus;
	}
	
	/**已分配内存*/
	public static long totalMemory(){
		return runtime.totalMemory();
	}
	
	/**已分配内存中的剩余空间*/
	public static long freeMemory(){
		return runtime.freeMemory();
	}

	/**最大可用内存*/
	public static long usableMemory(){
		return maxMemory-totalMemory()+freeMemory();
	}
	
	/**最大内存*/
	public static long maxMemory(){
		return maxMemory;
	}
	
}
