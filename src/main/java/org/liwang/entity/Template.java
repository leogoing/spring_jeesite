package org.liwang.entity;

import java.util.Set;

import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;

/**
 * 模板
 * @author liwang
 *
 */
public class Template extends BaseLEntity<Template>{

	private static final long serialVersionUID = 1L;
	
	/**出当前模板所必要的条件*/
	private Condition conditionPass;
	
	/**模板名称*/
	private String name;
	
	/**科目*/
	private Subject subject;
	
	/**一级科目*/
	private Condition firstSubjectVariable;

	/**二级科目*/
	private Condition secondSubjectVariable;
	
	/**三级科目*/
	private Condition thirdSubjectVariable;
	
	/**对应科目,一般应该为空*/
	private Condition relativeSubjectVariable;
	
	/**金额*/
	private Condition moneyVariable;
	
	/**借贷标识*/
	private Condition dcFlagVariable;
	
	/**模块*/
	private Integer module;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Template.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/accounting/"+ALIAS;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Condition getConditionPass() {
		return conditionPass;
	}

	public void setConditionPass(Condition conditionPass) {
		this.conditionPass = conditionPass;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Condition getFirstSubjectVariable() {
		return firstSubjectVariable;
	}

	public void setFirstSubjectVariable(Condition firstSubjectVariable) {
		this.firstSubjectVariable = firstSubjectVariable;
	}

	public Condition getSecondSubjectVariable() {
		return secondSubjectVariable;
	}

	public void setSecondSubjectVariable(Condition secondSubjectVariable) {
		this.secondSubjectVariable = secondSubjectVariable;
	}

	public Condition getThirdSubjectVariable() {
		return thirdSubjectVariable;
	}

	public void setThirdSubjectVariable(Condition thirdSubjectVariable) {
		this.thirdSubjectVariable = thirdSubjectVariable;
	}

	public Condition getRelativeSubjectVariable() {
		return relativeSubjectVariable;
	}

	public void setRelativeSubjectVariable(Condition relativeSubjectVariable) {
		this.relativeSubjectVariable = relativeSubjectVariable;
	}

	public Condition getMoneyVariable() {
		return moneyVariable;
	}

	public void setMoneyVariable(Condition moneyVariable) {
		this.moneyVariable = moneyVariable;
	}

	public Condition getDcFlagVariable() {
		return dcFlagVariable;
	}

	public void setDcFlagVariable(Condition dcFlagVariable) {
		this.dcFlagVariable = dcFlagVariable;
	}

	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
	}

	/**
	 * 重写
	 */
	public void parseGroupStr(Set<String> permissions,String operatFlag){
		if(super.getUserGroupStr() ==null){
			super.setUserGroupStr(ShiroUtil.parseGroup(permissions,ALIAS, operatFlag));
			super.setOperat(operatFlag);
		}
	}
	
	@Override
	public String alias() {
		return ALIAS;
	}

	@Override
	public String viewPrefix() {
		return VIEW_PREFIX;
	}

	@Override
	public Template initObject() {
		return new Template();
	}

	@Override
	public String toString() {
		return "Template [conditionPass=" + conditionPass + ", name=" + name + ", subject=" + subject
				+ ", firstSubjectVariable=" + firstSubjectVariable + ", secondSubjectVariable=" + secondSubjectVariable
				+ ", thirdSubjectVariable=" + thirdSubjectVariable + ", relativeSubjectVariable="
				+ relativeSubjectVariable + ", moneyVariable=" + moneyVariable + ", dcFlagVariable=" + dcFlagVariable
				+ ", module=" + module + "]";
	}

}
