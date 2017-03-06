package org.liwang.util;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * 算数工具类
 * @author liwang
 *
 */
public abstract class NumUtil {

	private static final NumberFormat numFormat=NumberFormat.getNumberInstance();
	
	/**
	 * 四舍五入
	 * @param d
	 * @param size 保留几位小数
	 * @return
	 */
	public static double round(double d,int size){
		return  new BigDecimal(d).setScale(size, BigDecimal.ROUND_HALF_UP).doubleValue();  
	}
		
	/**
	 * 默认保留3位小数
	 * @param decimal
	 * @return
	 */
	public static String toMicrometer(double decimal){
		return numFormat.format(decimal);
	}
	
	
	public static void main(String[] args) {
		System.out.println(toMicrometer(123456789.125629));
	}
}
