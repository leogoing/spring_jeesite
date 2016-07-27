package com.thinkgem.jeesite.liwang.Main;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Named
class Try extends c implements Serializable{
	
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
		new c().b();
		
		String a="uu:ads:rt";
		System.out.println(a.matches("(\\S+:|)ads(:\\S+|)"));
		
	}
	
	public void a(){
		System.out.println(123);
	}
}
class c {
	public void b(){
		a();
	}
	public  void a() {
		System.out.println(456);
	}
}