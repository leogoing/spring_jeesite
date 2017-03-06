package org.liwang.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.Subject;
import org.liwang.entity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkgem.jeesite.common.utils.StringUtils;


/**
 * 权限工具类
 * @author liwang
 *
 */
public abstract class ShiroUtil {

	private static final Logger log=LoggerFactory.getLogger(ShiroUtil.class);
	
	/**
	 * 权限字符串分隔符
	 */
	public static final String PERMISSION_SEPARATOR=":";
	
	/**
	 * 查看权限字符串
	 */
	public static final String VIEW_PERMISSION_STR="view";
	public static final String CREATE_PERMISSION_STR="create";
	public static final String UPDATE_PERMISSION_STR="update";
	public static final String DELETE_PERMISSION_STR="delete";
	/**放行*/
	public static final String APPROVE_PERMISSION_STR="approve";
	/**复核*/
	public static final String RECHECK_PERMISSION_STR="recheck";
	
	/**
	 * 根节点无实际分组意义
	 */
	public static final String ROOT_NODE="root";
	
	/**
	 * 代表所有权限
	 */
	public static final String ALL_PERMISSION_STR="*";
	
	/**
	 * 表示id的前缀(用于数据库查询来鉴别是否为id)统一的格式
	 */
	public static final String ID_WRAPPER_PREIX="{";
	/**
	 * 表示id的后缀(用于数据库查询来鉴别是否为id)统一的格式
	 */
	public static final String ID_WRAPPER_SUFFIX="}";
	
	/**
	 * 是否存在id的正则表达式
	 */
	public static final String HAS_ID_REGEX="^([^\\{\\}\\s]+:|\\s*)\\{\\w+\\}$";

	/**
	 * 分组的分隔符
	 */
	public static final String GROUP_ID_SEPARATOR=",";
	
	/**
	 * 表示拥有该数据的权限
	 */
	public static final String PASS_PERMISSION_STR="pass";
	
	/**
	 * 是否存在操作标识的正则表达式
	 */
	public static final String HAS_OPERAT_REGEX="(\\S+:|)"+VIEW_PERMISSION_STR+"(:\\S+|)|(\\S+:|)"+CREATE_PERMISSION_STR+
												"(:\\S+|)|(\\S+:|)"+UPDATE_PERMISSION_STR+"(:\\S+|)|(\\S+:|)"+DELETE_PERMISSION_STR+
												"(:\\S+|)|(\\S+:|)"+APPROVE_PERMISSION_STR+"(:\\S+|)|(\\S+:|)"+RECHECK_PERMISSION_STR;
	
	/**
	 * 根权限
	 */
	public static final String FIRST_PERMISSION="gold";
	
	/**
	 * 贵金属权限字符串前缀
	 */
	public static final String GOLD_PERMISSION_PREFIX=FIRST_PERMISSION+PERMISSION_SEPARATOR;
	
	
	public static final Subject getUser(){
		return SecurityUtils.getSubject();
	}
	
	
	/**
	 * 把权限字符串集合提取出分组的信息(强制要求分组每个层级之间不能有相同的字符串,如 variety:variety...)
	 * @param permissions
	 * @param prefix
	 * @return
	 */
	public static String parseGroup(Set<String> permissions,String prefix,String operatFlag){
		StringBuffer buf=new StringBuffer();
		int prefixLength= (prefix==null?0:prefix.length())+GOLD_PERMISSION_PREFIX.length();
		
		for(String perm : permissions){
			int index=perm.indexOf(GOLD_PERMISSION_PREFIX+prefix);
			if(index<0){//前缀不对
				if(FIRST_PERMISSION.equals(perm) || perm.equals(FIRST_PERMISSION+PERMISSION_SEPARATOR+operatFlag)){
					return PASS_PERMISSION_STR;
				}
				continue;
			}else if(index==0){
				return PASS_PERMISSION_STR;
			}
			if(!perm.contains(operatFlag) && hasOperat(perm)){//操作权限不对应
				continue;
			}
			
			String suffix= perm.substring(index+prefixLength+1);
			
			suffix=suffix.replaceAll("\\s+","");//去掉空格
			if(suffix.isEmpty() || suffix.equals(operatFlag)){
				return PASS_PERMISSION_STR;
			}
			
			if(suffix.matches(HAS_ID_REGEX)){//若存在id,则把id放前面
				int bracket_index=suffix.indexOf(ID_WRAPPER_PREIX);
				String id=suffix.substring(bracket_index-1, suffix.indexOf(ID_WRAPPER_SUFFIX)+1);
				buf.append(id);
				suffix=suffix.substring(0,bracket_index-1);
			}
			
			buf.append(suffix.replaceFirst(PERMISSION_SEPARATOR+operatFlag+"|"+operatFlag, ""));//去掉操作字符如view等
			
			buf.append(GROUP_ID_SEPARATOR);
			
		}
		
		return buf.toString();//不用去掉最后一个,号
	}
	
	/**
	 * 组成权限字符串并添加到容器中
	 * @param groups
	 * @param info
	 */
	public static void addGroupPermission(List<Group> groups,SimpleAuthorizationInfo info){
		if(groups==null || info==null){
			return;
		}
		
		StringBuilder buid=new StringBuilder();
		int temp_id=-1;
		
		for(Group group : groups){
			int id=group.getId();
			if(temp_id!=-1 && buid.length()>0){
				if(temp_id!=id){
					if(log.isDebugEnabled()){
						log.debug("addGroupPermission to info: "+buid.toString());
					}
					info.addStringPermission(buid.toString());
					temp_id=id;
					buid=new StringBuilder();
				}else{
					buid.append(PERMISSION_SEPARATOR);
				}
			}else if(temp_id==-1){
				temp_id=id;
			}
			
			String groupId=StringUtils.trim(group.getGroupId());
			if(!ROOT_NODE.equals(groupId))
				buid.append(groupId);
			else
				temp_id=id;
			
		}
		
		if(buid.length()>0){
			if(log.isDebugEnabled()){
				log.debug("addGroupPermission: "+buid.toString());
			}
			info.addStringPermission(buid.toString());
		}
		
	}
	
	/**
	 * 判断是否拥有该数据的任何一个权限<br>
	 * 可以使用多线程先渲染列表再渲染操作按钮
	 * @param groupStr 当前用户拥有的权限
	 * @param operat	数据被允许的权限
	 * @return
	 */
	public static boolean hasAnyPermission(String groupStr,String operat,String id,String prefix){
		if(groupStr ==null || groupStr.trim().isEmpty()){
			return true;
		}
		
		Set<String> perms=screenByOperat(groupStr,id,operat);
		
		Subject user=getUser();
		for(String perm : perms){
			if(user.isPermitted(prefix+perm)){
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * 筛选出含指定操作符的权限和所有操作符都不含的权限(现在hasPermission标签不支持多个权限所以已经无用了)
	 * @param permssions
	 * @param operat
	 * @return
	 */
	public static Set<String> screenByOperat(String permssions,String id,String operat){
		if(permssions ==null || permssions.trim().isEmpty()){
			return null;
		}
		
		Set<String> set=new HashSet<String>();
		String[] perms=permssions.split(GROUP_ID_SEPARATOR);
		for(String perm : perms){
			if(hasOperat(perm) && !perm.contains(operat)){
				continue;
			}
			
			if(perm.endsWith(ID_WRAPPER_SUFFIX))
				set.add(perm);
			else
				set.add(perm+PERMISSION_SEPARATOR+ID_WRAPPER_PREIX+id+ID_WRAPPER_SUFFIX);
		}
		
		return set;
	}
	
	/**
	 * 判断是否拥有该数据的任何一个权限<br>
	 * 适用与多线程！！！多操作符
	 * @param groupStr 当前用户拥有的权限
	 * @param operat	数据被允许的权限
	 * @return
	 */
	public static int[] hasAnyPermssion2(String groupStr,String id,String ... operats){
		int[] arr=new int[operats.length];
		if(groupStr ==null || groupStr.trim().isEmpty()){
			return arr;
		}
		
		List<Set<String>> perms=screenByOperat2(groupStr,id,operats);
		
		Subject user=getUser();
		
		for(int i=perms.size()-1;i>=0;i--){
			Set<String> set=perms.get(i);
			for(String perm : set){
				if(user.isPermitted(perm)){
					arr[i]=1;
					if(i+1==operats.length){
						return arr;
					}
				}
			}
		}
		
		return arr;
	}
	
	
	/**
	 * 筛选出含指定操作符的权限和所有操作符都不含的权限(现在hasPermission标签不支持多个权限所以已经无用了)
	 * @param permssions
	 * @param operat
	 * @return
	 */
	public static List<Set<String>> screenByOperat2(String permssions,String id,String ... operats){
		if(permssions ==null || permssions.trim().isEmpty()){
			return null;
		}
		
		int length=operats.length;
		List<Set<String>> list =new ArrayList<Set<String>>(length+1);
		
		for(int i=0;i<=length;i++){
			list.add(new HashSet<String>());
		}
		
		String[] perms=permssions.split(GROUP_ID_SEPARATOR);
		for(String perm : perms){
			if(hasOperat(perm)){
				for(int i=0;i<length;i++){
					if(perm.contains(operats[i])){
						Set<String> set=list.get(i);
						if(perm.endsWith(ID_WRAPPER_SUFFIX))
							set.add(perm);
						else
							set.add(perm+PERMISSION_SEPARATOR+ID_WRAPPER_PREIX+id+ID_WRAPPER_SUFFIX);
						break;
					}
				}
			}else{
				Set<String> set=list.get(list.size());
				if(perm.endsWith(ID_WRAPPER_SUFFIX))
					set.add(perm);
				else
					set.add(perm+PERMISSION_SEPARATOR+ID_WRAPPER_PREIX+id+ID_WRAPPER_SUFFIX);
			}
			
			
		}
		
		return list;
	}
	
	/**
	 * 存在操作标识(view,update等)
	 * @param str
	 * @return
	 */
	public static boolean hasOperat(String str){
		return str.matches(HAS_OPERAT_REGEX);
	}
	
}
