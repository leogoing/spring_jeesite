package org.liwang.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.dom4j.Element;
import org.liwang.commen.entity.LEntity;
import org.liwang.entity.Account;
import org.liwang.entity.Condition;
import org.liwang.entity.Deal;
import org.liwang.entity.Subject;
import org.liwang.entity.Template;
import org.liwang.entity.Variable;
import org.liwang.service.SubjectService;
import org.liwang.service.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 放行类
 * @author liwang
 *
 */
@Component
@Scope("prototype")
public class Approve {

	private Deal deal;//当前交易
	
	private List<Template> templates;//所有的模板
	
	private Map<Integer,Condition> conditions=Maps.newHashMap();//过滤后的条件
	
	private Map<Integer,Variable> variables=Maps.newHashMap();//过滤后的变量
	
//	private Map<Integer,?> bufConfitions=Maps.newHashMap();//当前缓存条件
	
	private Map<String,Object> bufVariables=Maps.newHashMap();//当前缓存变量(存放的不是数据库中的变量而是带有注解的变量)
	
	@Autowired
	private VariableService variableService;
	@Autowired
	private SubjectService subjectService;
	
	public void init(Deal deal,List<Template> templates,List<Condition> conditions,List<Variable> variables){
		this.deal=deal;
		this.templates=templates;
		init( conditions, variables);
	}
	
	/**把相同模块的筛选出来*/
	private void init(List<Condition> conditions,List<Variable> variables){
		
		for(Condition condition : conditions){
			Integer module=condition.getModule();
			if(deal.getModule().equals(module) || 0==module){
				this.conditions.put(condition.getId(),condition);
			}
		}
		
		for(Variable variable : variables){
			Integer module=variable.getModule();
			if(deal.getModule().equals(module) || 0==module){
				this.variables.put(variable.getId(),variable);
			}
		}
		
		//加载deal中的变量到当前变量池
		String dealClass=deal.getClass().getName();
		for(Field field : AnnoUtil.getField(deal.getClass())){
			bufVariables.put(dealClass+"."+field.getName(),ClassUtil.getPropertyVal(deal, field));
		}
	}
	
	/**放行入口*/
	public List<Account> approve(Deal deal,List<Template> templates,List<Condition> conditions,List<Variable> variables){
		init(deal, templates, conditions, variables);
		
		List<Account> accouts=Lists.newLinkedList();
		
		for(Template template : templates){
			
			if(template.getModule().equals(deal.getModule())){//必须属于同一个模块
				
				Boolean bur=getConditionRes(template.getConditionPass());

				if(bur){
					Account account=new Account();
					account.setDealNo(deal.getDealNo());
					
					if(template.getSubject()!=null){//先看科目对象有无若没有或为-1(相当于空)则根据科目变量来获得
						Subject subject=subjectService.get(template.getSubject());
						account.setFirstSubject(subject.getFirstSubject());
						account.setSecondSubject(subject.getSecondSubject());
						account.setThirdSubject(subject.getThirdSubject());
					}
					if(account.getFirstSubject()==null || account.getFirstSubject()==-1)
						account.setFirstSubject((Integer)getConditionRes(template.getFirstSubjectVariable()));
					if(account.getSecondSubject()==null || account.getSecondSubject()==-1)
						account.setSecondSubject((Integer)getConditionRes(template.getSecondSubjectVariable()));
					if(account.getThirdSubject()==null || account.getThirdSubject()==-1)
						account.setThirdSubject((Integer)getConditionRes(template.getThirdSubjectVariable()));
					
					account.setRelativeSubject((Integer)getConditionRes(template.getRelativeSubjectVariable()));
					
					account.setMoney((Double)getConditionRes(template.getMoneyVariable()));
					account.setDcFlag((Integer)getConditionRes(template.getDcFlagVariable()));
					account.setBusinessDay("2016.08.26");//TODO:营业日的获取
					
					accouts.add(account);
				}
				
			}
		}
		
		return accouts;
	}
	
	private <T> T getConditionRes(Condition condition){
		if(condition==null)
			return null;
		
		String script=getScript(condition.getId());
		return (T)JavaScriptUtil.valuation(script,condition.getName());
	}
	
	/**根据条件id获取脚本内容，包括变量初始化*/
	private String getScript(Integer id){
		Condition condition=conditions.get(id);
		if(condition!=null){
			String  expression=condition.getExpression();
			if(!"".equals(expression)){
				return replaceId(0, expression);
			}
		}
		
		throw new RuntimeException("找不到表达式!");
	}
	
	/**替换变量id为变量值*/
	private String replaceId(int left,String origin){
		if(origin.indexOf("#")==-1)
			return origin;
		
		Integer id=StrUtil.extract(origin, left-1);
		Variable variable=variables.get(id);
		
		if(variable==null)
			throw new RuntimeException("变量不存在!");
		
		Object value=getVariableVal(variable);
		String strValue=ClassUtil.convertToStr(value);
		String script=origin.replaceFirst("#\\d+#", strValue);
		
		int next=script.indexOf("#");
		if(next!=-1){
			return replaceId(next, script);
		}
			
		return script;
		
	}
	
	/**获取变量值*/
	private Object getVariableVal(Variable variable){
		String variablePath=variable.getVariablePath();
		
		String resultField=variablePath.substring(variablePath.lastIndexOf(".")+1);//要返回的结果字段名
		
		Object bufVar=getBufVariableField(variablePath);
		
		if(bufVar==null){
			String className=variablePath.substring(0, variablePath.lastIndexOf("."));//只适用于带变量名的path
			String depend=variable.getDepend();
			
			Object entity=ClassUtil.newObj(className);
			
			if(depend!=null && !depend.isEmpty()){
				if(depend.contains("=")){//如果是手输的条件
					String[] condition=depend.split("=");
					String field=condition[0].trim();//暂时限一个条件
					String val=condition[1].trim();
					
					ClassUtil.setPropertyVal(entity, field, val);
					return ClassUtil.getEntityByService(className, (LEntity)entity);
					
				}else if(depend.contains("#")){//如果是依赖变量
					String[] depends=depend.split(",");
					
					for(int i=0;i<depends.length;i++){//或关系
						List<Integer> arr=Lists.newLinkedList();
						StrUtil.extract(depends[i],arr, -1);
						
						for(int j=0;j<arr.size();j++){//并关系
							Variable var=variables.get(arr.get(j));
							String varPath=var.getVariablePath();
							Object val=getBufVariableField(varPath);
							if(val==null){
								val=getVariableVal(var);
							}
							
							String field=null;//源变量的字段,指xml中的variable元素的field属性
							String depField=null;//依赖变量的字段
							
							Element root=variableService.getRootElement();
							List<Element> elements=root.elements("variable");
							for(Element ele : elements){
								if(className.equals(ele.attributeValue("classPath"))){//depend栏位与xml匹配
									field=ele.attributeValue("field");
									List<Element> eles=ele.elements("depend");
									
									for(Element el : eles){//是否有对应的依赖变量，如果类能匹配上则有
										String depF=el.attributeValue("field");
										if(varPath.equals(el.attributeValue("classPath")+"."+depF)){
											depField=depF;
											break;
										}
									}
									if(depField!=null)
										break;
								}
							}
							if(depField==null)
								throw new RuntimeException("无匹配变量字段!");
							
							//对应的字段不相同则先通过该值获取实体类再通过实体类获取对应的字段
							if(!varPath.endsWith("."+depField)){
								String varClassName=varPath.substring(0, varPath.lastIndexOf("."));
								LEntity varEntity=ClassUtil.getEntityByService(
													varClassName, (LEntity)ClassUtil.newObj(varClassName));
								val=ClassUtil.getPropertyVal(varEntity, depField);
							}
							//若对应的字段相同就直接开始设置
							ClassUtil.setPropertyVal(entity, field, val);
							
						}
						
						LEntity dbEntity=ClassUtil.getEntityByService(className, (LEntity)entity);
						if(dbEntity!=null)
							return ClassUtil.getPropertyVal(dbEntity, resultField);
						
					}
				}
			}else{
				Object bufObj=getBufVariableField(className);//从变量池中按类匹配查找
				bufVar=ClassUtil.getPropertyVal(bufObj, resultField);
			}
			
		}
		
		return bufVar;
	}
	
	/**从变量池获取变量根据类字段全名查找*/
	public Object getBufVariableField(String variablePath){
		Object bufVar=bufVariables.get(variablePath);
		if(bufVar==null){
			bufVar=variableService.getVariableVal(variablePath);
		}
		return bufVar;
	}
	
}
