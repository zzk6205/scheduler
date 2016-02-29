package net.web.base.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

public class AuthRealm extends AuthorizingRealm {

	/**
	 * 获取认证信息
	 * 
	 * @param token
	 *            令牌
	 * @return 认证信息
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		UsernamePasswordToken authToken = (UsernamePasswordToken) token;
		String username = authToken.getUsername();
		String password = new String(authToken.getPassword());
		if (username != null && password != null) {
			if (!"admin".equals(username)) {
				throw new UnknownAccountException();
			}
			if (!"admin".equals(password)) {
				throw new IncorrectCredentialsException();
			}
			return new SimpleAuthenticationInfo(new Principal(username), password, getName());
		}
		throw new UnknownAccountException();
	}

	/**
	 * 获取授权信息
	 * 
	 * @param principals
	 *            principals
	 * @return 授权信息
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		Principal principal = (Principal) principals.fromRealm(getName()).iterator().next();
		if (principal != null) {
			// 用户授权
			SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
			authorizationInfo.addStringPermission("admin");
			return authorizationInfo;
		}
		return null;
	}

}