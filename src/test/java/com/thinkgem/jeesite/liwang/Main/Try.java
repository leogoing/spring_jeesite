package com.thinkgem.jeesite.liwang.Main;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;

public class Try {
	
	public static void main(String[] args) {
		System.out.println("null".equals(null));
//		for(String s:StringUtils.delimitedListToStringArray("http://nishui/kkj..//sa", "/"))
//			System.out.println(s);;
	}
	
	public static String subTime(String firstTime,String lastTime){
		return subTime(firstTime,lastTime,"hhmm");
	}
	public static String subTime(String firstTime,String lastTime,String reg){
		SimpleDateFormat sdf=new SimpleDateFormat(reg);
		try {
			long dif=sdf.parse(lastTime).getTime()-sdf.parse(firstTime).getTime();
			return dif/1000/60/60+"个小时"+(dif/1000/60-(dif/1000/60/60)*60)+"分钟";
		} catch (ParseException e) {
			return "";
		}
	}
}
