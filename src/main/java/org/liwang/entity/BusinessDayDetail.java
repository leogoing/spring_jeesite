package org.liwang.entity;

import java.util.Date;
import java.util.Set;

import org.liwang.annotion.Variabled;
import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;
import org.liwang.util.StrUtil;
import org.springframework.format.annotation.DateTimeFormat;

import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 营业日明细历史
 * @author liwang
 *
 */
@Variabled("营业管理")
public class BusinessDayDetail extends BaseLEntity<BusinessDayDetail>{

	private static final long serialVersionUID = 1L;
	
	/**营业管理*/
	private BusinessDayCtr businessDayCtr;
	
	/**营业开始时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date businessStart;
	
	/**营业结束时间*/
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date businessEnd;
	
	/**换天的用户*/
	private User changeDayUser;
	
	/**关账用户*/
	private User closeAccountUser;
	
	/**营业日*/
	@Variabled("营业日")
	private String businessDay;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=StrUtil.firstLower(BusinessDayDetail.class);
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;
	
	
	public BusinessDayCtr getBusinessDayCtr() {
		return businessDayCtr;
	}

	public void setBusinessDayCtr(BusinessDayCtr businessDayCtr) {
		this.businessDayCtr = businessDayCtr;
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

	public User getChangeDayUser() {
		return changeDayUser;
	}

	public void setChangeDayUser(User changeDayUser) {
		this.changeDayUser = changeDayUser;
	}

	public User getCloseAccountUser() {
		return closeAccountUser;
	}

	public void setCloseAccountUser(User closeAccountUser) {
		this.closeAccountUser = closeAccountUser;
	}

	public String getBusinessDay() {
		return businessDay;
	}

	public void setBusinessDay(String businessDay) {
		this.businessDay = businessDay;
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
	public BusinessDayDetail initObject() {
		return new BusinessDayDetail();
	}

	@Override
	public String toString() {
		return "BusinessDayDetail [businessDayCtr=" + businessDayCtr + ", businessStart=" + businessStart
				+ ", businessEnd=" + businessEnd + ", changeDayUser=" + changeDayUser + ", closeAccountUser="
				+ closeAccountUser + ", businessDay=" + businessDay + "]";
	}

}
