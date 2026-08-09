package org.hopeframework.core.enums;

import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.exception.HopeException;

import com.google.common.base.MoreObjects;

/**
 * 访问类型枚举
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public enum AccessType {
	DEFAUT, WEB;

	/**
	 * 是否是系统支持的访问类型
	 *
	 * @param name
	 * @param types
	 * @return
	 */
	public static void checkAccessType(String name, AccessType[] types) {
		MoreObjects.firstNonNull(name, "");

		for (AccessType type : types) {
			if (name.equalsIgnoreCase(type.ordinal() + "")) {
				return;
			}
		}

		throw new HopeException(ResponseConst.ACCESS_TYPE_NOT_SUPPORT);
	}

	/**
	 * 根据下标取得访问类型
	 *
	 * @param index
	 *            下标
	 * @return 访问类型
	 */
	public static AccessType accessTypeByIndex(int index) {

		AccessType[] accessTypes = AccessType.values();

		AccessType result = DEFAUT;

		try {
			result = accessTypes[index];
		} catch (Exception e) {
			result = DEFAUT;
		}

		return result;
	}
}
