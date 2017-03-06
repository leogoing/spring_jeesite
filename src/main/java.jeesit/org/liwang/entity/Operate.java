package org.liwang.entity;

import java.util.Date;

import org.liwang.commen.entity.BaseLEntity;
import org.springframework.format.annotation.DateTimeFormat;

import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 操作明细实体类
 * @author liwang
 *
 */
public class Operate extends BaseLEntity<Operate>{

	private static final long serialVersionUID = 1L;

	/**
	 * 操作员id
	 */
	private User user;
	
	/**
	 * 操作模块
	 */
	private String module;
	
	/**
	 * 所操作的源(数据表或java类等)
	 */
	private String operateSource;
	
	/**
	 * 操作类型
	 */
	private Integer operateType;
	
	/**
	 * 操作时间
	 */
	@DateTimeFormat(pattern="yyyy.MM.dd")
	private Date operateTime;
	
	/**
	 * 营业日期
	 */
	private String businessDay;
	
	/**
	 * 操作对象id
	 */
	private Integer objectId;
	
	/**
	 * 备注
	 */
	private String remarks;
	
	/**
	 * 对象信息(当对象没有id时插入)
	 */
	private String objectInfo;
	
	/**
	 * 别名代表当前数据类别的权限的字符串
	 */
	public static final String ALIAS=Operate.class.getSimpleName().toLowerCase();
	
	/**
	 * 视图路径前缀
	 */
	public static final String VIEW_PREFIX="gold/deal/"+ALIAS;
	
	/**
	 * 操作资源类别为数据库表
	 */
	public static final String SOURCE_TYPE_DB="db";
	
	/**添加操作*/
	public static final int OPERATE_TYPE_CREATE=5;
	/**修改操作*/
	public static final int OPERATE_TYPE_UPDATE=4;
	/**删除操作*/
	public static final int OPERATE_TYPE_DELETE=3;
	/**放行操作*/
	public static final int OPERATE_TYPE_APPROVE=0;
	/**放行操作*/
	public static final int OPERATE_TYPE_RECHECK=1;
	/**踢回操作*/
	public static final int OPERATE_TYPE_REBACK=2;
	/**启动流程操作*/
	public static final int OPERATE_TYPE_START_PROCESS=6;
	
	public Operate(){}
	
	public Operate(User user,String module,String operateSource,Integer operateType,Date operateTime,
					String businessDay,Integer objectId,String remarks,String objectInfo){
		this.user=user;
		this.module=module;
		this.operateSource=operateSource;
		this.operateType=operateType;
		this.operateTime=operateTime;
		this.remarks=remarks;
		this.businessDay=businessDay;
		this.objectId=objectId;
		this.objectInfo=objectInfo;
	}
	
	
	public String getObjectInfo() {
		return objectInfo;
	}

	public void setObjectInfo(String objectInfo) {
		this.objectInfo = objectInfo;
	}

	public String getBusinessDay() {
		return businessDay;
	}

	public void setBusinessDay(String businessDay) {
		this.businessDay = businessDay;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getOperateSource() {
		return operateSource;
	}

	public void setOperateSource(String operateSource) {
		this.operateSource = operateSource;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getObjectId() {
		return objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	public Operate initObject() {
		return new Operate();
	}

}
