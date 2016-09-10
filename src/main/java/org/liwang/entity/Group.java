package org.liwang.entity;

import org.liwang.commen.entity.BaseLEntity;

/**
 * 分组bean
 * @author liwang
 *
 */
public class Group extends BaseLEntity<Group>{

	/**
	 * 版本
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 别名
	 */
	public static final String ALIAS=Group.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;

	/**
	 * 代号实际应用中使用的唯一单词
	 */
	private String groupId;
	
	/**
	 * 名称
	 */
	private String groupName;
	
	/**
	 * 上级分组id
	 */
	private Integer parentId;

	/**
	 * 上级分组名
	 */
	private String parentName;
	
	public Group(){}
	
	public Group(String group_id){
		this.groupId=group_id;
	}
	
	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
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
	public Group initObject() {
		return new Group();
	}

	@Override
	public String toString() {
		return "Group [groupId=" + groupId + ", groupName=" + groupName + ", parentId=" + parentId + ", parentName="
				+ parentName + "]";
	}
	
}
