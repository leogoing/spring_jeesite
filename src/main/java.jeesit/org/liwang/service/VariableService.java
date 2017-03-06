package org.liwang.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.liwang.annotion.Variabled;
import org.liwang.commen.entity.LEntity;
import org.liwang.common.service.DefaultLService;
import org.liwang.common.service.OperabilityLService;
import org.liwang.entity.Variable;
import org.liwang.util.AnnoUtil;
import org.liwang.util.ClassUtil;
import org.liwang.util.DomUtil;
import org.liwang.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;

/**
 * 变量
 * @author liwang
 *
 */
@Service
public class VariableService extends OperabilityLService<Variable>{
	
	private static final Logger log =LoggerFactory.getLogger(VariableService.class);
	
	/**公用变量节点*/
	private static final String COMMUNAL_NODE="communal";
	/**类路径前缀节点名*/
	private static final String CLASSPATH_PREFIX_NODE="prefix";
	
	/**公共变量池*/
	private Map<String,Object> variablePool;
	
	@Value("${variable.xml.path}")
	private String xmlPath;
	
	private Element rootElement;
	
	/**
	 * 数据库中的变量全部加载出来
	 * @param variable
	 * @return
	 */
	public List<Map<String,Object>> variableTree(Variable variable){
		List<Variable> list=findAll(variable);
		List<Map<String,Object>> listMap=Lists.newLinkedList();
		
		Set<String> tempClassName=Sets.newHashSet();
		for(Variable varia : list){
			String path=varia.getVariablePath();
			String className=path.substring(0, path.lastIndexOf("."));
			
			if(!tempClassName.contains(className)){//不存在类则添加变量的父级-类
				Map<String,Object> map=Maps.newHashMap();
				map.put("id", className);
				try {
					map.put("name", AnnoUtil.getClassAlias(className));
				} catch (ClassNotFoundException e) {
					log.warn(e.getMessage());
					map.put("name", className.substring(className.lastIndexOf(".")+1));
				}
				map.put("isParent", true);
				listMap.add(map);
				tempClassName.add(className);
			}
			
			Map<String,Object> map=Maps.newHashMap();
			map.put("id", "#"+varia.getId()+"#");
			map.put("name", varia.getVisualName());
			map.put("pId", className);
			
			listMap.add(map);
		}
		
		return listMap;
	}
	
	/**
	 * 应用中可以作为变量的字段全部扫描出来
	 * @param variable 暂时无用
	 * @return
	 */
	public List<Map<String,Object>> treeData(Variable variable){
		List<Map<String,Object>> list=Lists.newLinkedList();
		Map<String, Set<Field>> fields = AnnoUtil.scanVariable("org.liwang.entity");
		if(fields==null)
			return list;
		
		Set<String> keys=fields.keySet();
		for(String key : keys){
			Map<String,Object> mapFather = Maps.newHashMap();
			mapFather.put("name",key.substring(key.lastIndexOf(".")+1));
			mapFather.put("id", key);
			mapFather.put("isParent", true);
			
			list.add(mapFather);
			
			Set<Field> sons=fields.get(key);
			for(Field field : sons){
				Map<String,Object> mapSon = Maps.newHashMap();
				
				Variabled anno=field.getAnnotation(Variabled.class);
				if(!"".equals(anno.value()))
					mapSon.put("name", anno.value());
				else
					mapSon.put("name", field.getName());
				
				mapSon.put("id",key.substring(0, key.lastIndexOf("."))+"."+field.getName());
				mapSon.put("pId",key);
				list.add(mapSon);
			}
		}
		
		return list;
	}
	
	/**公共变量池中查找变量*/
	public Object getVariableVal(String name){
		if(variablePool==null){
			loadCommonVariable();
		}
		
		return variablePool.get(name);
	}
	
	private void loadCommonVariable(){
		initElement();
		
		List<Element> elements=rootElement.elements(COMMUNAL_NODE);
		for(Element ele : elements){
			String className=ele.attributeValue("classPath");//必须为类全名不含变量
			Object obj=ClassUtil.newObj(className);
			
			Element condition=ele.element("condition");
			Element property=condition.element("property");
			String field=property.attributeValue("field");
			String value=property.attributeValue("value");
			ClassUtil.setPropertyVal(obj, field, value);
			
			LEntity entity=ClassUtil.getEntityByService(className, (LEntity)obj);
			
			String selfField=ele.attributeValue("feild");
			if(selfField!=null && !selfField.isEmpty()){//存在对应的字段而不是整个对象
				Object val=ClassUtil.getPropertyVal(entity,selfField);
				variablePool.put(className+"."+selfField, val);
				continue;
			}
			
			variablePool.put(className, entity);
		}
		
	}
	
	
	public Element getRootElement(){
		initElement();
		return rootElement;
	}
	
	private void initElement(){
		if(rootElement==null)
			rootElement=DomUtil.loadElement(xmlPath);
		if(variablePool==null)
			variablePool=Maps.newHashMap();
	}
	
}
