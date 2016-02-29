package net.web.base.shiro;

import java.io.Serializable;

public class Principal implements Serializable {

	private static final long serialVersionUID = 1L;
	private String userName;

	public Principal(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}