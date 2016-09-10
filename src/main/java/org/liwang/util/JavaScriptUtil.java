package org.liwang.util;

import java.util.UUID;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * js工具类
 * @author liwang
 *
 */
public abstract class JavaScriptUtil {

	/**js引擎*/
	private static ScriptEngine engin =new ScriptEngineManager().getEngineByName("javascript");
	
	/**执行脚本结果(先拼接为js方法再调用)*/
	public static Object valuation(String jsText,String functionName){
		if(functionName==null || functionName.isEmpty())
			functionName="Script"+UUID.randomUUID().toString().replace("-", "");
		
		String function="function "+functionName+"(){ "+jsText+"}";
		try {
			engin.eval(function);
			Invocable invacable=(Invocable)engin;
			return invacable.invokeFunction(functionName);
			
		}  catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
