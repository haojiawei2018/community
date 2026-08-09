package org.hopeframework.core.constant;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 框架常量类
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class Constants {

	/**
	 * 统一的Boolean(是，否)
	 */
	public static final String TRUE_1 = "1";
	public static final String FALSE_0 = "0";

	public static final String VERSION = "1.0.0";

	/**
	 * 默认编码
	 */
	public static final String ENCODING = "utf-8";
	/**
	 * json格式
	 */
	public static final String FORMATE_JSON = "json";
	/**
	 * xml格式
	 */
	public static final String FORMATE_XML = "xml";
	/**
	 * 文件上传最大值
	 */
	public static final int CONTENTLENGTH_MAX = 1073741824; // 1G
	/**
	 * 请求类型
	 */
	public static final String REQUEST_POST = "POST";

	/** 请求来源时间格式 */
	public static final String REQUEST_TIME_FORMAT = "yyyyMMddHHmmss";

	/** 请求来源时间格式(jodatime用) */
	public static final DateTimeFormatter REQUEST_TIME_FORMAT_FOR_JODA_TIME = DateTimeFormat
			.forPattern(REQUEST_TIME_FORMAT);

	// 文件上传下载
	/**
	 * 文件上传组件要求的回调函数地址
	 */
	public static final String CALLBACK_URI = "CALLBACK_URI";
	/**
	 * 文件上传组件要求的企业版回调函数地址
	 */
	public static final String ENT_CALLBACK_URI = "ENT_CALLBACK_URI";
	/**
	 * 文件上传组件访问地址
	 */
	public static final String UPLOAD_URI = "UPLOAD_URI";
	/**
	 * 文件上传成功临时目录
	 */
	public static final String SUCCESS_TEMP_FILE_PATH = "SUCCESS_TEMP_FILE_PATH";
	/**
	 * 文件上传失败临时目录
	 */
	public static final String FAIL_TEMP_FILE_PATH = "FAIL_TEMP_FILE_PATH";

	/**
	 * 基础数据模块(business,iplimit,access)
	 */
	public static final String BASIC_DATA_MODULE_BUSINESS = "business";
	public static final String BASIC_DATA_MODULE_IPLIMIT = "iplimit";
	public static final String BASIC_DATA_MODULE_ACCESS = "access";

	/**
	 * ip白名单类型（WEB）
	 */
	public static final String IPLIMIT_TYPE_WEB = "WEB";

	/**
	 * ACCESS_WEB
	 */
	public static final String ACCEESS_NAME_WEB = "ACCEESS_WEB";

	/**
	 * 允许连续登入失败次数
	 */
	public static final int LOG_ERROR_TIME = 10;

	/**
	 * 验证码允许连续验证失败次数
	 */
	public static final int SMS_CHECK_ERROR_TIME = 10;

	/**
	 * 加密方式
	 */
	public static final String MD5 = "md5";
	public static final String SHA256 = "SHA-256";

	/**
	 * 验证码
	 */
	public static final String VerifyCode = "CODE";

	/**
	 * 分割符
	 */
	public static final String SPLIT_CHAR = "_";

}
