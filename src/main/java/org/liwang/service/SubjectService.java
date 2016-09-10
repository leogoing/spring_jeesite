package org.liwang.service;

import java.util.List;
import java.util.Map;

import org.liwang.common.service.OperabilityLService;
import org.liwang.dao.manager.SubjectDaoManager;
import org.liwang.entity.Account;
import org.liwang.entity.Operate;
import org.liwang.entity.Subject;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 科目
 * @author liwang
 *
 */
@Service
public class SubjectService extends OperabilityLService<Subject>{

	/**添加科目余额*/
	public int addBalance(List<Map<String,Number>> subjectInfo){
		for(Map<String,Number> subject : subjectInfo){
			Number dcFlag=subject.get("dcFlag");
			Number firstSubject=subject.get("firstSubject");
			Number secondSubject=subject.get("secondSubject");
			Number thirdSubject=subject.get("thirdSubject");
			Number money=subject.get("money");
			
			Subject sub=new Subject((Integer)firstSubject, (Integer)secondSubject, (Integer)thirdSubject);
			if(dcFlag.equals(Account.DEBIT)){//借记
				//TODO: 待扩展根据方向处理金额
			}else if(dcFlag.equals(Account.CREDIT)){//贷记
				
			}
			daoManager().addBalance(sub, (Double)money);
			oService.createOperate(Operate.SOURCE_TYPE_DB, Operate.OPERATE_TYPE_UPDATE, sub);
		}
		
		return 0;
	}
	
	/**
	 * 将科目表信息按树状结构拼接(树状图选择时不允许选父级节点)
	 * @param list  必须要求按科目排序
	 * @return
	 */
	public List<Map<String,Object>> treeData(List<Subject> list){
		List<Map<String, Object>> mapList = Lists.newArrayList();
		
		Integer firstSubject=null;
		Integer secondSubject=null;
		for(Subject subject : list){
			Map<String,Object> firstMap=null;
			if(!subject.getFirstSubject().equals(firstSubject)){//若一级科目不重复则put
				firstMap= Maps.newHashMap();
				firstMap.put("id", subject.getId());
				firstMap.put("name", subject.getName());
				firstSubject=subject.getFirstSubject();
				mapList.add(firstMap);
			}
			
			if(subject.getThirdSubject()>=0){
				if(firstMap!=null){//存在子科目则修正id以便子科目pid指向
					firstMap.put("id",firstSubject);
					firstMap.put("name",firstSubject);
				}
				
				if(!subject.getSecondSubject().equals(secondSubject)){
					Map<String,Object> secondMap= Maps.newHashMap();
					secondMap.put("id", firstSubject+":"+subject.getSecondSubject());
					if(subject.getSecondSubject()>=0){
						secondMap.put("name", subject.getSecondSubject());
					}else{
						secondMap.put("name", "无");
					}
					secondMap.put("pId", firstSubject);
					secondSubject=subject.getSecondSubject();
					mapList.add(secondMap);
				}
				
				Map<String,Object> thirdMap= Maps.newHashMap();
				thirdMap.put("id", subject.getId());
				thirdMap.put("name", subject.getName());
				thirdMap.put("pId", firstSubject+":"+secondSubject);
				mapList.add(thirdMap);
			}else if(subject.getSecondSubject()>=0 && !subject.getSecondSubject().equals(secondSubject)){
				if(firstMap!=null){//存在子科目则修正id以便子科目pid指向
					firstMap.put("id",firstSubject);
					firstMap.put("name",firstSubject);
				}
				
				Map<String,Object> secondMap= Maps.newHashMap();
				secondMap.put("id", subject.getId());
				secondMap.put("name", subject.getName());
				secondMap.put("pId", firstSubject);
				mapList.add(secondMap);
			}
			
		}
		
		return mapList;
	}
	
	private SubjectDaoManager daoManager(){
		return (SubjectDaoManager)daoManager;
	}
	
}
