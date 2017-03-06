package org.liwang.commen.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.liwang.util.ShiroUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thinkgem.jeesite.common.persistence.Page;

/**
 * 通用实体bean类(包含基本属性和权限部分)<br>
 * 可以考虑将权限部分分开再加个抽象类
 * @author liwang
 *
 */
public abstract class BaseLEntity<T> extends LEntity{

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger log=LoggerFactory.getLogger(BaseLEntity.class);
	
	/**新增状态*/
	public static final int STATUS_ADD=0;
	/**删除状态*/
	public static final int STATUS_DELETE=2;
	/**暂存状态*/
	public static final int STATUS_TRANSIENT=1;
	
	private Page<T> page;
	
	private Date sysTime;
	
	private Integer status;
	
	private String groupStr;
	
	/**
	 * 当前用户所有的权限字符窜用来传进函数比较
	 */
	private String userGroupStr;
	
	private String operat;
	
	public String getUserGroupStr() {
		return userGroupStr;
	}

	public void setUserGroupStr(String userGroupStr) {
		this.userGroupStr = userGroupStr;
	}

	public String getGroupStr() {
		return groupStr;
	}

	public String getOperat() {
		return operat;
	}

	public void setOperat(String operat) {
		this.operat = operat;
	}

	public void setGroupStr(String groupStr) {
		this.groupStr = groupStr;
	}
	
	/**
	 * 获取每个分组权限的第一个词
	 * @return
	 */
	public String getGroupStrNames(){
		if(groupStr==null || groupStr.isEmpty()){
			return null;
		}
		StringBuilder buid=new StringBuilder();
		String[] groups=groupStr.split(ShiroUtil.GROUP_ID_SEPARATOR);
		
		Map<String,Object> map=new HashMap<String,Object>();
		for(String group : groups){
			String first=group.split(ShiroUtil.PERMISSION_SEPARATOR)[0];
			
			if(!map.containsKey(first)){
				buid.append(first).append(ShiroUtil.GROUP_ID_SEPARATOR);
				map.put(first, new Object());
			}
		}
		
		return buid.toString();
	}
	
	/**
	 * 由子类重写,解析权限集合为组字符串
	 * @param permissions
	 */
	public void parseGroupStr(Set<String> permissions,String operatFlg){
		
	}
	
	/**
	 * 获取别名,一般为类名小写,用于通用controller
	 * @return
	 */
	public abstract String alias();
	
	/**
	 * 视图路径前缀,用于通用controller
	 * @return
	 */
	public abstract String viewPrefix();
	
	/**
	 * 创建默认的初始化实体对象
	 * @return
	 */
	public abstract T initObject();

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@JsonIgnore
	@XmlTransient
	public Page<T> getPage() {
		return page;
	}

	public void setPage(Page<T> page) {
		this.page = page;
	}

	public Date getSysTime() {
		return sysTime;
	}

	public void setSysTime(Date sysTime) {
		this.sysTime = sysTime;
	}
	
}
