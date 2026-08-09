package org.hopeframework.core.cache;

import java.io.Serializable;

/**
 * 系统基础数据
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class SystemBasicData implements Serializable {

	private static final long serialVersionUID = -1570322392769430774L;
	/**
	 * 模块(business,iplimit)
	 */
	private String module;

	/**
	 * 配置键
	 */
	private String confKey;

	/**
	 * 配置值
	 */
	private String confValue;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 获取模块(business,iplimit)
	 *
	 * @return MODULE - 模块(business,iplimit)
	 */
	public String getModule() {
		return module;
	}

	/**
	 * 设置模块(business,iplimit)
	 *
	 * @param module
	 *            模块(business,iplimit)
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * 获取配置键
	 *
	 * @return CONF_KEY - 配置键
	 */
	public String getConfKey() {
		return confKey;
	}

	/**
	 * 设置配置键
	 *
	 * @param confKey
	 *            配置键
	 */
	public void setConfKey(String confKey) {
		this.confKey = confKey;
	}

	/**
	 * 获取配置值
	 *
	 * @return CONF_VALUE - 配置值
	 */
	public String getConfValue() {
		return confValue;
	}

	/**
	 * 设置配置值
	 *
	 * @param confValue
	 *            配置值
	 */
	public void setConfValue(String confValue) {
		this.confValue = confValue;
	}

	/**
	 * 获取描述
	 *
	 * @return DESCRIPTION - 描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置描述
	 *
	 * @param description
	 *            描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}