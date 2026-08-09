package org.hopeframework.test.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "info")
public class RespInfo {
	
	private int SUCCESS = 100000;
	
	private int code;
	private String msg;
//	private String logno;
//	private String serversion;
	
	public RespInfo() {
		super();
		this.code = SUCCESS;
		this.msg = "成功";
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		if(code==501){
			this.msg ="无token";
		}else if(code==502){
			this.msg ="token认证失败";
		}else {
			this.msg = msg;
		}
	}
	
}
