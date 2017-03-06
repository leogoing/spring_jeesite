package org.liwang.manager;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.liwang.util.ShiroUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;

/**
 * 权限管理类
 * @author liwang
 *
 */
@Component
public class AuthManager implements InitializingBean{

	private static final Logger log=LoggerFactory.getLogger(AuthManager.class);
	
	public static final String SHIRO_CACHE_NAME=SystemAuthorizingRealm.class.getName()+".authorizationCache";
	
	@Autowired
	public CacheManager shiroCacheManager;
	
	
	private Cache<Object,Object> shiroCache;
	
	
	/**
	 * 当前用户
	 * @return
	 */
	public Subject getUser(){
		return ShiroUtil.getUser();
	}
	
	/**
	 * 仅仅获取缓存中的用户凭证
	 * @param id
	 * @return
	 */
	public Object getUserPrincipal(String id){
		if(shiroCacheManager instanceof EhCacheManager){
			EhCacheManager ehCacheManager=(EhCacheManager)shiroCacheManager;
			net.sf.ehcache.CacheManager cacheManager=ehCacheManager.getCacheManager();
			List<Object> principalColls=cacheManager.getCache(SHIRO_CACHE_NAME).getKeys();
			for(Object priColl : principalColls){
				if(priColl instanceof PrincipalCollection){
					Principal p=(Principal)((PrincipalCollection)priColl).getPrimaryPrincipal();
					if(p.getId().equals(id)){
						return priColl;
					}
				}
			}
		}
		
		return null;
	}
	
	public Set<String> getAllPermission(){
		SimpleAuthorizationInfo info=getAuthInfo();
		if(info ==null){
			return null;
		}
		return info.getStringPermissions();
	}
	
	public Set<String> getAllPermission(String id){
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info ==null){
			return null;
		}
		return info.getStringPermissions();
	}
	
	public Set<String> getAllRole(String id){
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info ==null){
			return null;
		}
		return info.getRoles();
	}
	
	public Set<String> getAllRole(){
		SimpleAuthorizationInfo info=getAuthInfo();
		if(info ==null){
			return null;
		}
		return info.getRoles();
	}
	
	public SimpleAuthorizationInfo getAuthInfo(){
		return getAuthInfo(null);
	}
	
	/**
	 * 根据用户id获取权限容器
	 * @param id
	 * @return
	 */
	public SimpleAuthorizationInfo getAuthInfo(String id){
		
		Object principal=null;
		if(id != null){
			principal=getUserPrincipal(id);
		}else{
			principal=getUser().getPrincipals();//必须获取集合类型，缓存中存放的是user的凭证集合
		}
		
		Object info=shiroCache.get(principal);
		if(info == null){
			getUser().hasRole("Check");//触发加载权限信息到缓存
			info=shiroCache.get(principal);
		}
		
		if(info instanceof SimpleAuthorizationInfo){
			return (SimpleAuthorizationInfo)info;
		}
		return null;
	}
	
	public Cache<Object,Object> getShiroAuthCache(){
		
		return shiroCacheManager.getCache(SHIRO_CACHE_NAME);
	}
	
	public void addPermission(String id,String permission){
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info !=null){
			info.addStringPermission(permission);
		}
	}
	
	public void addRole(String id,String role){
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info !=null){
			info.addRole(role);
		}
	}
	
	public void clearPermission(String id,String permission){
		Set<String> permissions=getAllPermission(id);
		permissions.remove(permission);
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info !=null){
			info.setStringPermissions(permissions);
		}
	}
	
	public void clearPermission(String id,Set<String> permissions){
		Set<String> perms=getAllPermission(id);
		perms.removeAll(permissions);
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info !=null){
			info.setStringPermissions(perms);
		}
	}
	
	public void clearRole(String id,Set<String> roles){
		Set<String> rs=getAllRole(id);
		rs.removeAll(roles);
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info !=null){
			info.setRoles(rs);
		}
	}

	public void clearRole(String id,String role){
		Set<String> roles=getAllRole(id);
		roles.remove(role);
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info !=null){
			info.setRoles(roles);
		}
	}
	
	public void setRole(String id,Set<String> oldRoles,Set<String> newRoles){
		Set<String> roles=getAllRole(id);
		roles.removeAll(oldRoles);
		roles.addAll(newRoles);
		SimpleAuthorizationInfo info=getAuthInfo(id);
		if(info !=null){
			info.setRoles(roles);
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		shiroCache=getShiroAuthCache();
	}
	
}
