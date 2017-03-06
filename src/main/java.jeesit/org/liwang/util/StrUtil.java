package org.liwang.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 字符串工具类
 * @author liwnag
 *
 */
public abstract class StrUtil {

	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	public static String firstLower(String str){
		if(str==null){
			return null;
		}
		return str.replaceFirst(String.valueOf(str.charAt(0)),
				  String.valueOf(str.charAt(0)).toLowerCase());
	}
	
	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String firstUpper(String str){
		if(str==null){
			return null;
		}
		return str.replaceFirst(String.valueOf(str.charAt(0)),
				  String.valueOf(str.charAt(0)).toUpperCase());
	}
	
	/**
	 * 返回传入class名称的首字母小写
	 * @param clazz
	 * @return
	 */
	public static String firstLower(Class<?> clazz){
		return firstLower(clazz.getSimpleName());
	}
	
	/**把日期转为字符串*/
	public static String dateToStr(Date date,String pattern){
		if(pattern==null || pattern.isEmpty())
			pattern="yyyy.MM.dd";
		
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	/**把字符串转为日期*/
	public static Date StrToDate(String date,String pattern){
		if(pattern==null || pattern.isEmpty())
			pattern="yyyy.MM.dd";
		
		SimpleDateFormat sdf=new SimpleDateFormat(pattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**例如把#23#提取为23,多个则放入数组中*/
	public static void extract(String origin,List<Integer> list,int left){
		int start=indexOfId(origin, left);
		int end=indexOfId(origin, start);
		String variableId=origin.substring(start+1,end);
		list.add(Integer.parseInt(variableId));
		
		int next=indexOfId(origin, end);
		if(next>=0)
			extract(origin, list, next-1);
		
	}
	
	/**例如把#23#提取为23(传入的left必须-1)*/
	public static Integer extract(String origin,int left){
		int right=indexOfId(origin, left);
		String str=origin.substring(right+1, indexOfId(origin, right));
		return Integer.parseInt(str);
	}
	/**确定#的下标*/
	public static int indexOfId(String origin,int start){
		return origin.indexOf("#",start+1);
	}
	
}
