package org.liwang.entity;

import org.liwang.commen.entity.BaseLEntity;

/**
 * 流水账
 * @author liwang
 *
 */
public class Account extends BaseLEntity<Account>{

	private static final long serialVersionUID = 1L;

	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Account.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/accounting/"+ALIAS;
	
	/**借记*/
	public static final int DEBIT=0;
	/**贷记*/
	public static final int CREDIT=1;
	
	/**业务种类*/
	private Integer businessLines;
	
	/**交易编号*/
	private String dealNo;
	
	/**一级科目*/
	private Integer firstSubject;
	/**二级科目*/
	private Integer secondSubject;
	/**三级科目*/
	private Integer thirdSubject;
	
	/**金额*/
	private Double money;
	
	/**借贷标识*/
	private Integer dcFlag;
	
	/**对应科目*/
	private Integer relativeSubject;
	
	/**营业日*/
	private String businessDay;
	
	
	public Integer getBusinessLines() {
		return businessLines;
	}

	public void setBusinessLines(Integer businessLines) {
		this.businessLines = businessLines;
	}

	public String getDealNo() {
		return dealNo;
	}

	public void setDealNo(String dealNo) {
		this.dealNo = dealNo;
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

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getDcFlag() {
		return dcFlag;
	}

	public void setDcFlag(Integer dcFlag) {
		this.dcFlag = dcFlag;
	}

	public Integer getRelativeSubject() {
		return relativeSubject;
	}

	public void setRelativeSubject(Integer relativeSubject) {
		this.relativeSubject = relativeSubject;
	}

	public String getBusinessDay() {
		return businessDay;
	}

	public void setBusinessDay(String businessDay) {
		this.businessDay = businessDay;
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
	public Account initObject() {
		return new Account();
	}

	@Override
	public String toString() {
		return "Account [businessLines=" + businessLines + ", dealNo=" + dealNo + ", firstSubject=" + firstSubject
				+ ", secondSubject=" + secondSubject + ", thirdSubject=" + thirdSubject + ", money=" + money
				+ ", dcFlag=" + dcFlag + ", relativeSubject=" + relativeSubject + ", businessDay=" + businessDay + "]";
	}

}
