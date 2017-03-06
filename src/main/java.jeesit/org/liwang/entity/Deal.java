package org.liwang.entity;

import java.util.Date;
import java.util.Set;

import org.liwang.annotion.Variabled;
import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

/**
 * 交易实体类
 * @author liwang
 *
 */
@Variabled("交易")
@Component
public class Deal extends BaseLEntity<Deal>{

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 1L;
	
	@Variabled("交易编号")
	private String dealNo;
	
	/**
	 * 近远端标识
	 */
	@Variabled(value="近远端标识",dict="gold_deal_sfFlag")
	private Integer sfFlag;
	
	@Variabled("买入币种")
	private String buyCur;
	
	@Variabled("卖出币种")
	private String sellCur;
	
	@Variabled("交易数量")
	private Long amount;
	
	@Variabled("交易金额")
	private Double money;
	
	@DateTimeFormat(pattern="yyyy.MM.dd")
	@Variabled("交易日")
	private Date dealDate;//交易日
	
	private Integer dealType;//交易类型,即远掉
	
	/**
	 * 交割日
	 */
	@DateTimeFormat(pattern="yyyy.MM.dd")
	@Variabled("交割日")
	private Date deliveryDate;
	
	/**
	 * 流程实例id
	 */
	private String procInsId;
	
	/**
	 * 交易来源
	 */
	private Integer bornOf;
	
	private Integer earlyPay;//是否提前付款
	
	/**
	 * 交易对手
	 */
	private Counterparty counterparty;
	
	/**
	 * 本行账户行id(都在交易对手表中)
	 */
	private Counterparty dealAccount;
	
	private Integer approve;//放行状态
	
	/**模块*/
	private Integer module;
	
	/**未复核*/
	public static final int NON_RECHECK=0;
	/**已复核为放行*/
	public static final int RECHECKED=1;
	/**已放行*/
	public static final int APPROVED=2;
	/**已放行未交割*/
	public static final int DELIVERED=3;
	
	/**提前付款*/
	public static final int EARLY_PAY=0;
	/**正常付款*/
	public static final int COMMON_PAY=1;
	
	public Deal(){}
	
	public Deal(int id){
		setId(id);
	}
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Deal.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;
	
	/**
	 * 重写
	 */
	public void parseGroupStr(Set<String> permissions,String operatFlag){
		if(super.getUserGroupStr() ==null){
			super.setUserGroupStr(ShiroUtil.parseGroup(permissions,ALIAS, operatFlag));
			super.setOperat(operatFlag);
		}
	}
	
	public String getprocInsId() {
		return procInsId;
	}

	public void setprocInsId(String procInsId) {
		this.procInsId = procInsId;
	}

	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
	}

	public String getDealNo() {
		return dealNo;
	}

	public void setDealNo(String dealNo) {
		this.dealNo = dealNo;
	}

	public Integer getSfFlag() {
		return sfFlag;
	}

	public void setSfFlag(Integer sfFlag) {
		this.sfFlag = sfFlag;
	}

	public String getBuyCur() {
		return buyCur;
	}

	public void setBuyCur(String buyCur) {
		this.buyCur = buyCur;
	}

	public String getSellCur() {
		return sellCur;
	}

	public void setSellCur(String sellCur) {
		this.sellCur = sellCur;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Date getDealDate() {
		return dealDate;
	}

	public void setDealDate(Date dealDate) {
		this.dealDate = dealDate;
	}

	public Integer getDealType() {
		return dealType;
	}

	public void setDealType(Integer dealType) {
		this.dealType = dealType;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Integer getBornOf() {
		return bornOf;
	}

	public void setBornOf(Integer bornOf) {
		this.bornOf = bornOf;
	}

	public Integer getEarlyPay() {
		return earlyPay;
	}

	public void setEarlyPay(Integer earlyPay) {
		this.earlyPay = earlyPay;
	}

	public Counterparty getCounterparty() {
		return counterparty;
	}

	public void setCounterparty(Counterparty counterparty) {
		this.counterparty = counterparty;
	}

	public Counterparty getDealAccount() {
		return dealAccount;
	}

	public void setDealAccount(Counterparty dealAccount) {
		this.dealAccount = dealAccount;
	}

	public Integer getApprove() {
		return approve;
	}

	public void setApprove(Integer approve) {
		this.approve = approve;
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
	public Deal initObject() {
		return new Deal();
	}

	@Override
	public String toString() {
		return "Deal [dealNo=" + dealNo + ", sfFlag=" + sfFlag + ", buyCur=" + buyCur + ", sellCur=" + sellCur
				+ ", amount=" + amount + ", money=" + money + ", dealDate=" + dealDate + ", dealType=" + dealType
				+ ", deliveryDate=" + deliveryDate + ", bornOf=" + bornOf + ", earlyPay=" + earlyPay + ", counterparty="
				+ counterparty + ", dealAccount=" + dealAccount + ", approve=" + approve + ", module=" + module + "]";
	}
	
}
