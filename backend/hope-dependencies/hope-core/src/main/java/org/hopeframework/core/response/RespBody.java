package org.hopeframework.core.response;

import lombok.Data;
import org.hopeframework.core.constant.ResponseConst;

import java.io.Serializable;

/**
 * 返回信息
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Data
public class RespBody<T> implements Serializable {

	private int code;
	private String message;
	private T data;


	public RespBody() {
		super();
		this.code = ResponseConst.SUCCESS;
		this.message = "成功";
	}

	public RespBody(T content) {
		super();
		this.code = ResponseConst.SUCCESS;
		this.message = "成功";
		this.data = content;
	}

	public RespBody(int code, String msg){
		this.code = code;
		this.message = msg;
	}

	public RespBody(int code, String msg, T data){
		this.code = code;
		this.message = msg;
		this.data = data;
	}
}
