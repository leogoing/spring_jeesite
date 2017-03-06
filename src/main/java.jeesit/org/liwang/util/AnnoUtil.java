package org.liwang.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.liwang.annotion.Variabled;
import org.liwang.entity.Variable;
import org.liwang.service.VariableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 注解工具类
 * @author liwang
 *
 */
public abstract class AnnoUtil {

	private static final Logger log =LoggerFactory.getLogger(VariableService.class);

	/**默认扫描的包前缀*/
	private static final String DEF_PAC_PREFIX="org.liwang";
	
	/**
	 * 扫描路径下的所有变量
	 * @param path 包路径(暂不支持文件路径)
	 * @return
	 */
	public static Map<String,Set<Field>> scanVariable(String path){
		ClassLoader classLoader=ClassUtil.classLoader();
		
		String packName=null;
		try {
			if(path!=null)
				packName=path;
			else
				packName=DEF_PAC_PREFIX;
			
			Enumeration<URL> urls=classLoader.getResources(packName.replace(".", "/"));
			
			Map<String,Set<Field>> map=Maps.newHashMap();
			while(urls.hasMoreElements()){
				URL url=urls.nextElement();
				if("file".equals(url.getProtocol())){
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					getVariableField(packName, filePath, map);
				}
			}
			
			return map;
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 获取包下所有变量（支持class文件）
	 * @param packName 包全名
	 * @param filePath 包路径
	 * @param map
	 */
	public static void getVariableField(String packName,String filePath,Map<String,Set<Field>> map){
		File dir= new File(filePath);
		if(dir.isFile()){
			getVariableFieldByFile(dir, packName,map);
			return ;
		}
		
		if(!dir.exists() || !dir.isDirectory()){
			log.error("包目录不存在!");
			return ;
		}
		
		File[] dirFiles = dir.listFiles(new FileFilter(){
			@Override
			public boolean accept(File pathname) {
				boolean isdir=pathname.isDirectory();
				boolean isFile=pathname.isFile();
				boolean isInnerClass=pathname.getName().contains("$");
				return (isdir || isFile) && !isInnerClass;
			}
		});
		
		for (File file : dirFiles) {
            if(file.isDirectory()){
            	getVariableField(packName + "." + file.getName(), file.getAbsolutePath(),map);
            }else{
            	String classPath=packName+"."+file.getName().substring(0, file.getName().length()-6);
            	getVariableFieldByFile(file, classPath,map);
            }
        }
		
	}
	
	/**获取class文件中的变量*/
	public static Set<Field> getVariableFieldByFile(File file,String classPath,Map<String,Set<Field>> map){

        try {
            Class<?> clazz=ClassUtil.loadClass(classPath);
            
            Variabled variabled=clazz.getAnnotation(Variabled.class);
            if(variabled==null)
    			return null;
            
            map.put(classPath+"."+variabled.value(), getField(clazz));
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
     
        return null;
	}
	
	/**获取变量注解的dict值*/
	public static String getVariableDict(String path){
		
		String className=path.substring(0, path.lastIndexOf("."));
		String fieldStr=path.substring(path.lastIndexOf(".")+1);
		try {
			Class<?> clazz=ClassUtil.loadClass(className);
			for(Field field : clazz.getDeclaredFields()){
				Variabled variabled=field.getAnnotation(Variabled.class);
				if(variabled!=null && field.getName().equals(fieldStr)){
					return variabled.dict();
				}
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		return null;
	}
	
	/**
	 * 获取class下的变量
	 * @param clazz
	 * @param fields
	 */
	public static Set<Field> getField(Class<?> clazz){
		
		Set<Field> fields=Sets.newHashSet();
		
		for(Field field : clazz.getDeclaredFields()){
			if(field.getAnnotation(Variabled.class)!=null){
				fields.add(field);
			}
		}
		
		return fields;
	}
	
	/**
	 * 获取变量注解中的名称
	 * @param classPath
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String getClassAlias(String classPath) throws ClassNotFoundException{
		Class<?> clazz=ClassUtil.loadClass(classPath);
		Variabled variabled = clazz.getAnnotation(Variabled.class);
		
		if(variabled!=null)
			return variabled.value();
		return null;
	}
	
}
