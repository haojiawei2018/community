package org.hopeframework.core.error;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 参数校验错误信息列表
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class Errors {

	/** 错误信息列表 */
	private List<Error> errors = Lists.newArrayList();

	/**
	 * 添加错误信息
	 *
	 * @param field
	 *            字段名
	 * @param errorCode
	 *            错误编号
	 * @param errorMsg
	 *            错误信息
	 * @return 错误对象
	 */
	public Errors addError(String field, String errorCode, String errorMsg) {
		this.errors.add(new Error(field, errorCode, errorMsg));
		return this;
	}

	/**
	 * 添加一组错误信息
	 *
	 * @param errors
	 *            错误信息列表
	 * @return 错误对象
	 */
	public Errors addErrors(List<Error> errors) {
		this.errors.addAll(errors);
		return this;
	}

	/**
	 * 取得错误信息列表
	 *
	 * @return 错误信息列表
	 */
	public List<Error> getErrors() {
		return errors;
	}

	/**
	 * 是否有错误
	 *
	 * @return true:有/false:没有
	 */
	public boolean hasErrors() {
		return this.errors.size() > 0;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("Errors").add("errors", errors).toString();
	}
}
