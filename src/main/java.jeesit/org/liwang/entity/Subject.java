package org.liwang.entity;

import java.util.Set;

import org.liwang.annotion.Variabled;
import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;

/**
 * 科目
 * @author liwang
 *
 */
@Variabled("科目信息")
public class Subject extends BaseLEntity<Subject>{

	private static final long serialVersionUID = 1L;

	/**模块如铂钯类*/
	private Integer module;
	
	/**一级科目*/
	@Variabled("一级科目")
	private Integer firstSubject;
	
	/**二级科目*/
	@Variabled("二级科目")
	private Integer secondSubject;
	
	/**三级科目*/
	@Variabled("三级科目")
	private Integer thirdSubject;
	
	/**余额*/
	@Variabled("科目余额")
	private Double balance;
	
	/**科目种类如损益科目*/
	private Integer subjectType;
	
	/**名称*/
	private String name;
	
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Subject.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/accounting/"+ALIAS;
	
	public Subject(){};
	
	public Subject(Integer firstSubject,Integer secondSubject,Integer thirdSubject){
		this.firstSubject=firstSubject;
		this.secondSubject=secondSubject;
		this.thirdSubject=thirdSubject;
	}
	
	public Subject(Integer id){
		super.setId(id);
	}
	
	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
	}

	public Integer getFirstSubject() {
		return firstSubject;
	}

	public void setFirstSubject(Integer firstSubject) {
		this.firstSubject = firstSubject;
	}

	public Integer getSecondSubject() {
		return secondSubject;
	}

	public void setSecondSubject(Integer secondSubject) {
		this.secondSubject = secondSubject;
	}

	public Integer getThirdSubject() {
		return thirdSubject;
	}

	public void setThirdSubject(Integer thirdSubject) {
		this.thirdSubject = thirdSubject;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getSubjectType() {
		return subjectType;
	}

	public void setSubjectType(Integer subjectType) {
		this.subjectType = subjectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	public Subject initObject() {
		return new Subject();
	}

	@Override
	public String toString() {
		return "Subject [module=" + module + ", firstSubject=" + firstSubject + ", secondSubject=" + secondSubject
				+ ", thirdSubject=" + thirdSubject + ", balance=" + balance + ", subjectType=" + subjectType + ", name="
				+ name + "]";
	}

}
