package org.liwang.entity;

import java.util.Set;

import org.liwang.annotion.Variabled;
import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;

/**
 * 交易对手实体类
 * @author liwang
 *
 */
@Variabled("交易对手")
public class Counterparty extends BaseLEntity<Counterparty>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 境内外标识
	 */
	private Integer placeFlag;
	
	/**
	 * 银行或机构财团编号
	 */
	@Variabled("交易对手编号")
	private Integer code;
	
	/**
	 * 名称
	 */
	private String name;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Counterparty.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;

	@Override
	public String alias() {
		return ALIAS;
	}

	public Integer getPlaceFlag() {
		return placeFlag;
	}

	public void setPlaceFlag(Integer placeFlag) {
		this.placeFlag = placeFlag;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
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
	public String viewPrefix() {
		return VIEW_PREFIX;
	}

	@Override
	public Counterparty initObject() {
		return new Counterparty();
	}

	@Override
	public String toString() {
		return "Counterparty [placeFlag=" + placeFlag + ", code=" + code + ", name=" + name + "]";
	}
	
}
