<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.UUID"%>
<%@page import="java.security.interfaces.RSAPublicKey"%>
<%@page import="org.apache.commons.lang.ArrayUtils"%>
<%@page import="org.apache.commons.codec.binary.Base64"%>
<%@page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="net.web.base.util.SpringContextUtils"%>
<%@page import="net.web.base.service.IRSAService"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
String base = request.getContextPath();
ApplicationContext applicationContext = SpringContextUtils.getApplicationContext();
if (applicationContext != null) {
%>
<shiro:authenticated>
<%
response.sendRedirect(base + "/jsp/public/index.jsp");
%>
</shiro:authenticated>
<%
}
%>
<html>
<head>
<title>登录</title>
<%
if (applicationContext != null) {
	IRSAService rsaService = SpringContextUtils.getBean("rsaService", IRSAService.class);
	RSAPublicKey publicKey = rsaService.generateKey(request);
	String message = "";
	String modulus = Base64.encodeBase64String(publicKey.getModulus().toByteArray());
	String exponent = Base64.encodeBase64String(publicKey.getPublicExponent().toByteArray());
	String loginFailure = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
	if (loginFailure != null) {
		if (loginFailure.equals("org.apache.shiro.authc.pam.UnsupportedTokenException")) {
			message = "验证码错误";
		} else if (loginFailure.equals("org.apache.shiro.authc.UnknownAccountException")) {
			message = "无效的用户账号";
		} else if (loginFailure.equals("org.apache.shiro.authc.DisabledAccountException")) {
			message = "账号已被禁用";
		} else if (loginFailure.equals("org.apache.shiro.authc.LockedAccountException")) {
			message = "账号已被锁定";
		} else if (loginFailure.equals("org.apache.shiro.authc.IncorrectCredentialsException")) {
			message = "用户名或密码错误";
		} else if (loginFailure.equals("org.apache.shiro.authc.AuthenticationException")) {
			message = "账号认证失败";
		}
		request.setAttribute("msg", message);
	}
%>
<script src="${pageContext.request.contextPath}/skins/js/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/skins/js/jquery.custom.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/skins/js/public.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/skins/js/jsbn.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/skins/js/prng4.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/skins/js/rng.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/skins/js/rsa.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/skins/js/base64.js" type="text/javascript"></script>
<link href="${pageContext.request.contextPath}/skins/css/login.css" rel="stylesheet" type="text/css" />
<script>
if (self != top) {
	top.location = self.location;
}
$().ready( function() {
	var $loginForm = $("#loginForm");
	var $enPassword = $("#enPassword");
	var $username = $("#username");
	var $password = $("#password");
	// 记住用户名
	if(getCookie("j_username") != null) {
		$username.val(getCookie("j_username"));
		$password.focus();
	} else {
		$username.focus();
	}
	// 表单验证、记住用户名
	$loginForm.submit(function() {
		if ($username.val() == "") {
			$.custom.alert("请输入用户名");
			return false;
		}
		if ($password.val() == "") {
			$.custom.alert("请输入密码");
			return false;
		}
		addCookie("j_username", $username.val(), {expires: 7 * 24 * 60 * 60});
		var rsaKey = new RSAKey();
		rsaKey.setPublic(b64tohex("<%=modulus%>"), b64tohex("<%=exponent%>"));
		var enPassword = hex2b64(rsaKey.encrypt($password.val()));
		$enPassword.val(enPassword);
	});
});
</script>
<% } %>
</head>
<% if (applicationContext != null) { %>
<body>
	<form id="loginForm" method="post">
		<input type="hidden" id="enPassword" name="enPassword" />
		<div style="height: 145px;"></div>
		<table style="width: 100%; height: 100%;">
			<tr>
				<td align="center">
					<table style="width:570px; height: 355px; cellpadding: 0px; cellspacing: 0px; border: 0px; padding: 0px;" background="${pageContext.request.contextPath}/skins/images/login.png"> 
						<tr>
							<td style="padding-left: 150px; padding-top: 150px;">
								<table style="width: 100%;" class="loginTable">
									<tr>
										<td style="width:180px;" align="right"><strong>用户名: </strong></td>
										<td align="left"><input type="text" id="username" name="username" value="admin" class="textfield" size="20" /></td>
									</tr>
									<tr>
										<td style="width:180px;" align="right"><strong>密码: </strong></td>
										<td align="left"><input type="password" id="password" name="password" value="admin" class="textfield" size="20" /></td>
									</tr>
									<tr>
										<td align="right"></td>
										<td>
											<table style="width: 100%; border-spacing: 0px; border-collapse: collapse;">
												<tr>
													<td style="width: 90px; border-spacing: 0px;" align="left">
														<img style="cursor: pointer; border-style: none;" src="${pageContext.request.contextPath}/skins/images/login-ok.gif" onclick="$('#loginForm').submit();" />
													</td>
													<td align="left">
														<img style="cursor: pointer; border-style: none;" src="${pageContext.request.contextPath}/skins/images/login-cancel.gif" onclick="document.forms[0].reset();" />
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td height="86" align="center" colspan=2>
											<div unselectable="on" style="width: 400px; font-size: 12px; color: #FF9900; font-weight: bold; margin-bottom: 25;">
												${msg}
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</form>
</body>
<% } else { %>
<body>
	<fieldset>
		<legend>系统错误</legend>
		<p>
			<strong>提示: 系统出现异常，请联系系统管理员！</strong>
		</p>
	</fieldset>
</body>
<% } %>
</html>