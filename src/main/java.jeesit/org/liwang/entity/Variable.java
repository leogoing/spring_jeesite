package org.liwang.entity;

import java.util.List;
import java.util.Set;

import org.liwang.commen.entity.BaseLEntity;
import org.liwang.util.ShiroUtil;

import com.thinkgem.jeesite.modules.sys.entity.Dict;

/**
 * 变量实体类
 * @author liwang
 *
 */
public class Variable extends BaseLEntity<Variable>{

	private static final long serialVersionUID = 1L;
	
	/**变量来源类型*/
	private Integer dataSource;
	
	/**变量路径*/
	private String variablePath;
	
	/**变量名*/
	private String variableName;
	
	/**可视化名*/
	private String visualName;
	
	/**变量类型*/
	private String variableType;
	
	/**变量值的字典名(对应sys_dict中的type字段)*/
	private String dict;
	
	private List<Dict> dictVal;
	
	/**模块*/
	private Integer module;
	
	/**变量依赖的其他变量(可以有多个)或直接赋值*/
	private String depend;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Variable.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/accounting/"+ALIAS;

	
	public List<Dict> getDictVal() {
		return dictVal;
	}

	public void setDictVal(List<Dict> dictVal) {
		this.dictVal = dictVal;
	}

	public String getDict() {
		return dict;
	}

	public void setDict(String dict) {
		this.dict = dict;
	}

	public String getDepend() {
		return depend;
	}

	public void setDepend(String depend) {
		this.depend = depend;
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
	
	public Integer getDataSource() {
		return dataSource;
	}

	public void setDataSource(Integer dataSource) {
		this.dataSource = dataSource;
	}

	public String getVariablePath() {
		return variablePath;
	}

	public void setVariablePath(String variablePath) {
		this.variablePath = variablePath;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getVisualName() {
		return visualName;
	}

	public void setVisualName(String visualName) {
		this.visualName = visualName;
	}

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
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
	public Variable initObject() {
		return new Variable();
	}

	@Override
	public String toString() {
		return "Variable [dataSource=" + dataSource + ", variablePath=" + variablePath + ", variableName="
				+ variableName + ", visualName=" + visualName + ", variableType=" + variableType + ", dict=" + dict
				+ ", dictVal=" + dictVal + ", module=" + module + ", depend=" + depend + "]";
	}

}
