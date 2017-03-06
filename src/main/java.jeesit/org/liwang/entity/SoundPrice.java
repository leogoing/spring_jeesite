package org.liwang.entity;

import java.util.Date;
import java.util.Set;

import org.liwang.annotion.Variabled;
import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;
import org.liwang.util.StrUtil;

/**
 * 公允价格实体类
 * @author liwang
 *
 */
@Variabled("公允价格信息")
public class SoundPrice extends BaseLEntity<SoundPrice>{

	private static final long serialVersionUID = 1L;

	/**
	 * 营业日
	 */
	private String businessDay;
	
	/**
	 * 品种id
	 */
	private Variety variety;
	
	@Variabled("公允价格")
	private Double soundPrice;//公允价格
	
	/**
	 * 价格来源
	 */
	private String priceSource;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=StrUtil.firstLower(SoundPrice.class);
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;
	
	public String getBusinessDay() {
		return businessDay;
	}

	public void setBusinessDay(String businessDay) {
		this.businessDay = businessDay;
	}

	public Variety getVariety() {
		return variety;
	}

	public void setVariety(Variety variety) {
		this.variety = variety;
	}

	public Double getSoundPrice() {
		return soundPrice;
	}

	public void setSoundPrice(Double soundPrice) {
		this.soundPrice = soundPrice;
	}

	public String getPriceSource() {
		return priceSource;
	}

	public void setPriceSource(String priceSource) {
		this.priceSource = priceSource;
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
	public SoundPrice initObject() {
		return new SoundPrice();
	}

	@Override
	public String toString() {
		return "SoundPrice [businessDay=" + businessDay + ", variety=" + variety + ", soundPrice=" + soundPrice
				+ ", priceSource=" + priceSource + "]";
	}

}
