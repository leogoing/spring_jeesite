package org.liwang.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.liwang.commen.entity.BaseLEntity;
import org.liwang.commen.entity.LEntity;
import org.liwang.common.service.DefaultLService;
import org.liwang.entity.Operate;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 操作明细service
 * @author liwang
 *
 */
@Service
public class OperateService extends DefaultLService<Operate>{

	
	private static final SimpleDateFormat DATA_FORMAT=new SimpleDateFormat("yyyy.MM.dd");
	
	/**
	 * 创建操作明细并保存
	 * @param source_type 最好的办法是让实体类实现不同种类的资源接口类<br>那么就可以根据类型来判断资源类型
	 * @param operateType
	 * @param entity
	 * @param remarks
	 * @return
	 */
	public int createOperate(String source_type,int operateType,LEntity entity){
		if(Operate.SOURCE_TYPE_DB.equals(source_type)){
			String module=null;
			if(entity instanceof BaseLEntity){
				module=((BaseLEntity)entity).alias();
			}else{
				module=entity.getClass().getSimpleName();
			}
			
			Date date=new Date();
			return add(
						new Operate(UserUtils.getUser(), module, source_type, 
						operateType, date, DATA_FORMAT.format(date), entity.getId(), "",entity.toString())
						);
		}
		return 0;
	}
	
	public int update(Operate operate){
		throw new RuntimeException("操作明细无法修改!!!");
	}
	
}
