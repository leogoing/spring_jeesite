package org.liwang.entity;

import java.util.Set;

import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;

/**
 * 条件实体
 * @author liwang
 *
 */
public class Condition extends BaseLEntity<Condition>{

	private static final long serialVersionUID = 1L;

	/**表达式*/
	private String expression;
	
	/**名称*/
	private String name;
	
	/**可视化名称*/
	private String alias;
	
	/**模块*/
	private Integer module;
	
	/**备注*/
	private String remarks;
	
	/**表达式译文*/
	private String translation;
	
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Condition.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/accounting/"+ALIAS;
	
	/**
	 * 重写
	 */
	public void parseGroupStr(Set<String> permissions,String operatFlag){
		if(super.getUserGroupStr() ==null){
			super.setUserGroupStr(ShiroUtil.parseGroup(permissions,ALIAS, operatFlag));
			super.setOperat(operatFlag);
		}
	}
	
	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
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
	public Condition initObject() {
		return new Condition();
	}

	@Override
	public String toString() {
		return "Condition [expression=" + expression + ", name=" + name + ", alias=" + alias + ", module=" + module
				+ ", remarks=" + remarks + ", translation=" + translation + "]";
	}

}
