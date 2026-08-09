package org.hopeframework.core.exception;

import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.error.Errors;

/**
 * 参数校验错误异常类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class ParamterValidException extends HopeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 错误代码 */
	private int code = ResponseConst.PARAMETER_CHECK_FAILED;

	/** 错误列表 */
	private Errors errors;

	public ParamterValidException(Errors errors) {
		this.errors = errors;
	}

	public int getCode() {
		return code;
	}

	public Errors getErrors() {
		return errors;
	}
}
