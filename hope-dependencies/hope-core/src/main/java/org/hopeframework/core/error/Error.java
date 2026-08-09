package org.hopeframework.core.error;

import com.google.common.base.MoreObjects;

/**
 * 参数校验错误信息
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class Error {
	/* 字段名 */
	private String field;

	/** 错误编号 */
	private String errorCode;

	/** 错误信息 */
	private String errorMsg;

	/**
	 * 构造错误信息
	 *
	 * @param errorCode
	 *            错误编号
	 */
	public Error(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * 构造错误信息
	 *
	 * @param field
	 *            字段名
	 * @param errorCode
	 *            错误编号
	 */
	public Error(String field, String errorCode) {
		this.field = field;
		this.errorCode = errorCode;
	}

	/**
	 * 构造错误信息
	 *
	 * @param field
	 *            字段名
	 * @param errorCode
	 *            错误编号
	 * @param errorMsg
	 *            错误信息
	 */
	public Error(String field, String errorCode, String errorMsg) {
		this.field = field;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("Error").add("field", field).add("errorCode", errorCode)
				.add("errorMsg", errorMsg).toString();
	}
}
