package org.liwang.entity;

import java.util.Set;

import org.liwang.annotion.Variabled;
import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;


/**
 * 品种实体bean
 * @author liwang
 *
 */
@Variabled("品种")
public class Variety extends BaseLEntity<Variety>{

	/**
	 * 版本
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 新增
	 */
	public static final int STATUS_NEW=0;
	
	/**
	 * 暂存
	 */
	public static final int STATUS_TRANSIENT=1;
	
	/**
	 * 删除
	 */
	public static final int STATUS_DELETE=2;
	
	/**
	 * 境外贵金属类型
	 */
	public static final int TYPE_FX_GOLD=0;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Variety.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;
	
	/**
	 * 币种
	 */
	@Variabled("币种")
	private String currency;
	
	/**
	 * 类别
	 */
	private Integer type;
	
	/**
	 * 品种号
	 */
	@Variabled("品种二级科目")
	private Integer variety;
	
	/**
	 * 品种名
	 */
	private String varietyName;

	/**
	 * 库存
	 */
	@Variabled("库存")
	private Long storage;
	
	/**
	 * 单位
	 */
	private String unit;
	
	/**
	 * 币种名
	 */
	private String currencyName;
	
	public Variety(){}
	
	public Variety(Integer id){
		setId(id);
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
	
	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getVariety() {
		return variety;
	}

	public void setVariety(int variety) {
		this.variety = variety;
	}

	public String getVarietyName() {
		return varietyName;
	}

	public void setVarietyName(String varietyName) {
		this.varietyName = varietyName;
	}

	public Long getStorage() {
		return storage;
	}

	public void setStorage(long storage) {
		this.storage = storage;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}


	public  Variety initObject(){
		Variety variety=new Variety();
		variety.setStatus(STATUS_NEW);
		variety.setType(TYPE_FX_GOLD);
		variety.setStorage(0);
		variety.setUnit("盎司(oz)");
		variety.setVariety(1);
		variety.setVarietyName("其它品种");
		return variety;
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
	public String toString() {
		return "Variety [currency=" + currency + ", type=" + type + ", variety=" + variety + ", varietyName="
				+ varietyName + ", storage=" + storage + ", unit=" + unit + ", currencyName=" + currencyName + "]";
	}

}
