package com.thinkgem.jeesite.liwang.Main;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.FrameworkServlet;

class Try implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 124L;
	public static void main(String[] args) {
//		System.out.println("null".equals(null));
//		System.out.println(char.class.getName().length());
//		for(String s:StringUtils.delimitedListToStringArray("http://nishui/kkj..//sa", "/"))
//			System.out.println(s);;
//		Try t=new Try();
//		for(Method m:t.getClass().getMethods()){
//			System.out.println(m.getName()+": "+m.getDeclaringClass().getModifiers());
//		}
		/*String[][] s=new String[][]{new String[]{"1"},new String[]{"0"}};
		String[] d=new String[]{"2"};
		Array.set(s, 0, d);
		for(String a[]:s)
		System.out.println(a[0]);
		System.out.println(888);
		for(String a:d)
			System.out.println(a);*/
		System.out.println( );
		
	}
	
	  String subTime(String firstTime,String lastTime){
		return subTime(firstTime,lastTime,"hhmm");
	}
	protected  String subTime(String firstTime,String lastTime,String reg){
		SimpleDateFormat sdf=new SimpleDateFormat(reg);
		try {
			long dif=sdf.parse(lastTime).getTime()-sdf.parse(firstTime).getTime();
			return dif/1000/60/60+"个小时"+(dif/1000/60-(dif/1000/60/60)*60)+"分钟";
		} catch (ParseException e) {
			return "";
		}
	}
}
