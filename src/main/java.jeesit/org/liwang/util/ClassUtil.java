package org.liwang.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Date;

import org.liwang.commen.entity.LEntity;
import org.liwang.common.service.DefaultLService;
import org.liwang.entity.BusinessDayDetail;
import org.liwang.service.BusinessDayCtrService;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;

/**
 * class工具类
 * @author liwnag
 *
 */
public abstract class ClassUtil {

	/**
	 * 没有执行类的初始化
	 * @param classPath
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> loadClass(String classPath) throws ClassNotFoundException{
		return classLoader().loadClass(classPath);
	}
	
	/**当前类加载器*/
	public static ClassLoader classLoader(){
		return Thread.currentThread().getContextClassLoader();
	}
	
	/**根据路径创建对象*/
	public static <T> T  newObj(String classPath){
		try {
			Class<?> clazz=Class.forName(classPath);
			return (T)clazz.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**设置属性值*/
	public static void setPropertyVal(Object object,String field,Object value){
		Class<?> clazz=object.getClass();
		
		int index=field.indexOf(".");//处理嵌套路径的可能
		Object oldValue=null;
		Object newObject=null;
		Field f=null;
		
		try {
			if(index>=0){//存在嵌套路径则先设置默认值...
				String firstFieldStr=field.substring(0, index+1);
				f=getDeclaredField(clazz,firstFieldStr);
				Object firstObj=f.getType().newInstance();
				
				newObject=firstObj;
				oldValue=value;
				value=firstObj;
			}else{
				f=getDeclaredField(clazz,field);
				value=convertObj(f.getType(), value);
			}
			
			try {
				Method setMethod=getDeclaredMethod(clazz,"set"+StrUtil.firstUpper(field), 
										f.getType());
				publicMethod(setMethod);
				
				invokeMethod(setMethod, object, value);
			} catch (Exception e) {//没有setter方法则直接设置属性
				e.printStackTrace();
				publicField(f);
				f.set(object, value);
			}
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		
		if(index>=0){
			setPropertyVal(newObject, field.substring(index+1), oldValue);
		}
	}
	
	/**获取属性值*/
	public static Object getPropertyVal(Object object,String field){
		Class<?> clazz=object.getClass();
		
		int index=field.indexOf(".");//处理嵌套路径的可能
		Field f=null;
		
		Object value=null;
		try {
			if(index>=0){//存在嵌套路径则先设置默认值...
				String firstFieldStr=field.substring(0, index+1);
				f=getDeclaredField(clazz,firstFieldStr);
			}else{
				f=getDeclaredField(clazz,field);
			}
			
			value=getPropertyVal(object, f);
		
		} catch (Exception e){
			throw new RuntimeException(e);
		}
		
		if(index>=0){
			return getPropertyVal(value, field.substring(index+1));
		}
		
		return value;
	}
	
	public static Object getPropertyVal(Object object,Field field) {
		try{
		try {
			Method getMethod=getDeclaredMethod(object.getClass(),"get"+StrUtil.firstUpper(field.getName()));
			publicMethod(getMethod);
			
			return invokeMethod(getMethod, object, null);
		} catch (Exception e) {//没有setter方法则直接设置属性
			e.printStackTrace();
			publicField(field);
			return field.get(object);
		} }catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**公开方法的访问权限(当类不为public并字段不为public时)*/
	public static void publicMethod(final Method method){
		if (!Modifier.isPublic(method.getDeclaringClass().getModifiers()) &&
				!method.isAccessible()) {
			if (System.getSecurityManager()!= null) {//如果启用了系统安全权限
				AccessController.doPrivileged(new PrivilegedAction<Object>() {//在没有权限检查下设置访问权限为公开
					@Override
					public Object run() {
						method.setAccessible(true);
						return null;
					}
				});
			}
			else {
				method.setAccessible(true);//没有系统安全检查则直接设访问权限为公开
			}
		}
	}
	
	/**公开字段的访问权限*/
	public static void publicField(final Field field){
		if (!field.isAccessible()) {
			if (System.getSecurityManager()!= null) {//如果启用了系统安全权限
				AccessController.doPrivileged(new PrivilegedAction<Object>() {//在没有权限检查下设置访问权限为公开
					@Override
					public Object run() {
						field.setAccessible(true);
						return null;
					}
				});
			}
			else {
				field.setAccessible(true);//没有系统安全检查则直接设访问权限为公开
			}
		}
	}
	
	/**绕过权限调用方法*/
	public static Object invokeMethod(final Method method,final Object object,final Object ... params){
		if (System.getSecurityManager() != null) {//如果启动了权限管理则让系统在没有权限情况下调用属性的可读方法
			try {
				return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
					@Override
					public Object run() throws Exception {
						return method.invoke(object, params);
					}
				});
			}
			catch (PrivilegedActionException pae) {
				throw new RuntimeException(pae);
			}
		}
		else {
			try {
				return method.invoke(object, params);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	
	/**转换类型，把string转为基本类型*/
	public static Object convertObj(Class<?> clazz,Object object){
		if(clazz.isInstance(object)){
			return object;
		}
		
		if(object instanceof String){
			String str=(String)object;
			
			if(Integer.class.isAssignableFrom(clazz)){
				return Integer.parseInt(str);
			}
			if(Long.class.isAssignableFrom(clazz)){
				return Long.parseLong(str);
			}
			if(Byte.class.isAssignableFrom(clazz)){
				return Byte.parseByte(str);
			}
			if(Boolean.class.isAssignableFrom(clazz)){
				return Boolean.parseBoolean(str);
			}
			if(Double.class.isAssignableFrom(clazz)){
				return Double.parseDouble(str);
			}
			if(Float.class.isAssignableFrom(clazz)){
				return Float.parseFloat(str);
			}
			if(Short.class.isAssignableFrom(clazz)){
				return Short.parseShort(str);
			}
			if(Character.class.isAssignableFrom(clazz)){
				return str.toCharArray();
			}
		}
		
		throw new RuntimeException("无法转换! "+object.getClass().getSimpleName()+
									" to "+clazz.getSimpleName());
	}
	
	/**把对象转换为字符串(适用与js脚本拼接)*/
	public static String convertToStr(Object obj){
		if(obj instanceof String){
			return "'"+obj+"'";
		}
		if(obj instanceof Date){
			return "'"+StrUtil.dateToStr((Date)obj, null)+"'";
		}
		
		return String.valueOf(obj);
//		throw new RuntimeException("无法转换! "+obj.getClass().getSimpleName()+
//				" to String");
	}
	
	/**获取service并通过service从数据库找出实体bean*/
	public static <T>T getEntityByService(String classPath,LEntity param){
		
		if(classPath.endsWith("BusinessDayDetail")){//特殊情况
			BusinessDayCtrService businessDayCtrService=(BusinessDayCtrService)SpringContextHolder.
																				getBean("businessDayCtrService");
			return (T)businessDayCtrService.findBusinessHistory((BusinessDayDetail)param).get(0);
		}
		
		DefaultLService service=(DefaultLService)SpringContextHolder.getBean(//获取service组件
				StrUtil.firstLower(classPath.substring(classPath.lastIndexOf(".")+1))+"Service");
		
		return (T)service.get(param);//限制了只能使用实体bean为变量载体
	}
	
	/**获取指定字段包含所有父类*/
	public static Field getDeclaredField(Class<?> clazz,String fieldName){
		Exception e=null;
		
		for(;clazz!=Object.class;clazz=clazz.getSuperclass()){
			try {
				return clazz.getDeclaredField(fieldName);
			} catch (Exception ex) {
				e=ex;
				//doNothing...
			}
		}
		
		throw new RuntimeException(e);
	}
	
	/**获取指定方法包含所有父类*/
	public static Method getDeclaredMethod(Class<?> clazz,String fieldName,Class<?> ... params){
		Exception e=null;
		
		for(;clazz!=Object.class;clazz=clazz.getSuperclass()){
			try {
				return clazz.getDeclaredMethod(fieldName,params);
			} catch (Exception ex) {
				e=ex;
				//doNothing...
			}
		}
		
		throw new RuntimeException(e);
	}
	
}
