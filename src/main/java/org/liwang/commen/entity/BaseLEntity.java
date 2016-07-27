package org.liwang.commen.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.liwang.util.ShiroUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thinkgem.jeesite.common.persistence.Page;

/**
 * 通用实体bean类
 * @author liwang
 *
 */
public abstract class BaseLEntity<T> extends LEntity implements Serializable{

	/**
	 * 版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private Page<T> page;
	
	private Date sysTime;
	
	private Integer status;
	
	private String groupStr;
	

	public String getGroupStr() {
		return groupStr;
	}

	public void setGroupId(String groupStr) {
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
	 * 由子类重写,解析权限为组
	 * @param permissions
	 */
	public void parseGroupStr(Set<String> permissions,String operatFlg){
		
	}

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
