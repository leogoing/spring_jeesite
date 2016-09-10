package org.liwang.entity;

import java.util.Date;
import java.util.Set;

import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;
import org.liwang.util.StrUtil;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 营业日管理实体类
 * @author liwnag
 *
 */
public class BusinessDayCtr extends BaseLEntity<BusinessDayCtr>{

	private static final long serialVersionUID = 1L;
	
	/**日期正则表达式*/
	private String businessRegex;
	
	/**营业开始时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date businessStart;
	
	/**营业结束时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date businessEnd;
	
	/**优先级别,数值越高越优先*/
	private Integer priority;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=StrUtil.firstLower(BusinessDayCtr.class);
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;
	

	public String getBusinessRegex() {
		return businessRegex;
	}

	public void setBusinessRegex(String businessRegex) {
		this.businessRegex = businessRegex;
	}

	public Date getBusinessStart() {
		return businessStart;
	}

	public void setBusinessStart(Date businessStart) {
		this.businessStart = businessStart;
	}

	public Date getBusinessEnd() {
		return businessEnd;
	}

	public void setBusinessEnd(Date businessEnd) {
		this.businessEnd = businessEnd;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
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
	public BusinessDayCtr initObject() {
		return new BusinessDayCtr();
	}

	@Override
	public String toString() {
		return "BusinessDayCtr [businessRegex=" + businessRegex + ", businessStart=" + businessStart + ", businessEnd="
				+ businessEnd + ", priority=" + priority + "]";
	}

}
