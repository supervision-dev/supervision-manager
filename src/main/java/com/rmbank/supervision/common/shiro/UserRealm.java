package com.rmbank.supervision.common.shiro;

import com.rmbank.supervision.common.utils.Constants;
import com.rmbank.supervision.common.utils.StringUtil;
import com.rmbank.supervision.model.*; 
import com.rmbank.supervision.service.ResourceService;
import com.rmbank.supervision.service.RoleService;
import com.rmbank.supervision.service.UserService;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.Sha256CredentialsMatcher;
import org.apache.shiro.authz.*;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;


public class UserRealm extends AuthorizingRealm {
	private static Logger logger = Logger.getLogger(UserRealm.class);

	@Resource(name="userService")
	private UserService userService;

	@Resource(name="roleService")
	private RoleService roleService; 

	@Resource(name="resourceService")
	private ResourceService resourceService;

	 public UserRealm() {
	        super.setName(Constants.THE_REALM_NAME);
	        super.setCredentialsMatcher(new Sha256CredentialsMatcher());
	 }

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
	   System.out.println(" doGetAuthorizationInfo.................");
	   String username = (String)principals.getPrimaryPrincipal();

		User user = userService.getUserByAccount(username); //
		if(null != user){
			SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
			List<ResourceConfig> resourceConfigs = null;
			List<Role> roleList = roleService.getRolesByUserId(user.getId());
			resourceConfigs = resourceService.getResourceConfigsByUserRoles(roleList);
			if(null !=  resourceConfigs && resourceConfigs.size()> 0 ){
				for(ResourceConfig resourceConfig : resourceConfigs){
					if(null != resourceConfig ){
						authorizationInfo.addStringPermission(resourceConfig.getResource());
					}
				}
			}
			return authorizationInfo;
		}
		return  null;
	}


	/**
	 * AuthenticationInfo represents a Subject's (aka user's) stored account information
	 * relevant to the authentication/log-in process only.
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			SimpleAuthenticationInfo authenticationInfo = null;
			ShiroUsernamePasswordToken usernamePasswordToke =(ShiroUsernamePasswordToken)token;
			String username = usernamePasswordToke.getUsername();
			  if( username != null && !"".equals(username) ){
					  authenticationInfo = new SimpleAuthenticationInfo(username, usernamePasswordToke.getBase64Password(), getName());
		              authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(username+usernamePasswordToke.getSalt()));
			  }
	        return authenticationInfo;
	}
    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

	@Override
	public boolean isPermitted(PrincipalCollection principals, String permission) {
		return true;
//		String userAccount= (String)principals.getPrimaryPrincipal();
//		if(!StringUtil.isEmpty(userAccount) && ( 
//				userAccount.equals(Constants.USER_SUPER_ADMIN_ACCOUNT))){
//			return true;
//		}
//		org.apache.shiro.authz.Permission p = this.getPermissionResolver().resolvePermission(permission);
//		return this.isPermitted(principals, p);
	}

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }
	/**
	 * 鏇存柊鐢ㄦ埛鎺堟潈淇℃伅缂撳瓨.
	 */
	public void clearCachedAuthorizationInfo(String principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}

}
