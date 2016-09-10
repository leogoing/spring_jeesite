package com.thinkgem.jeesite.liwang.Main;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.inject.Named;

import org.dom4j.DocumentException;
import org.liwang.entity.BusinessDayDetail;

@Named
class Try  implements Serializable{
	
	private static final long serialVersionUID = 124L;
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, 
													SecurityException, NoSuchFieldException, IllegalArgumentException, 
													IllegalAccessException, DocumentException {
//		Element root=DomUtil.loadElement("src/main/resources/variable-matches.xml");
//		Element a=root.element("communal");
//		System.out.println(root.elementText("prefix"));
		
		Number n=new Integer(2);
		
		System.out.println(n.equals(2));
	}
	private String abc;
	
	public String getName(){return "";}
}
