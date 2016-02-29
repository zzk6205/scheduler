package net.web.base.entity;

import java.io.Serializable;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	public final static String SUCCESS = "0000"; // 成功
	public final static String ERROR = "0001"; // 失败
	public final static String UNKNOWN = "9999"; // 未知

	private String code;
	private String msg;
	private Object result;

	public Message(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Message(String code, String msg, Object result) {
		this.code = code;
		this.msg = msg;
		this.result = result;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static Message success(String msg) {
		return new Message(SUCCESS, msg, null);
	}

	public static Message success(String msg, Object rs) {
		return new Message(SUCCESS, msg, rs);
	}

	public static Message error(String msg) {
		return new Message(ERROR, msg);
	}

	public static Message unknown(String msg) {
		return new Message(UNKNOWN, msg);
	}

}
