package org.hopeframework.core.exception;

import org.hopeframework.core.error.Errors;

/**
 * 异常类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class HopeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 错误代码 */
	private int code;

	/** 错误参数 */
	private Object[] params;

	/** 错误列表 */
	private Errors errors;

	public HopeException() {
		super();
	}

	public HopeException(int code) {
		super(String.valueOf(code));
		this.code = code;
	}

	public HopeException(String msg) {
		super(msg);
	}

	public HopeException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public HopeException(int code, Errors errors) {
		this.code = code;
		this.errors = errors;
	}

	public HopeException(int code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}

	public HopeException(int code, String msg, Object... params) {
		super(msg);
		this.code = code;
		this.params = params;
	}

	public HopeException(Throwable cause) {
		super(cause);
	}

	public HopeException(String message, Throwable cause) {
		super(message, cause);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object... params) {
		this.params = params;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
