package org.hopeframework.core.constant;

/**
 * 响应常量
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class ResponseConst {

	/** 下载用的contentType */
	public static final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";

	/** 成功 */
	public static final int SUCCESS = 200;

	/** token */
	public static final int NULL_TOKEN = 501;

	/** token */
	public static final int ACCESS_TOKEN = 502;

	/** ------------------------------------------------- */
	/** AccessId 不能为空 */
	public static final int ACCESS_ID_IS_NULL = 100010;

	/** session超时,请重新登陆 */
	public static final int SESSION_TIME_OUT = 100011;
	/** 签名不匹配 */
	public static final int SIGN_NAME_NOT_MATCH = 100012;

	/** 请求参数异常 */
	public static final int REQUEST_CONTENT_EXCEPTION = 100013;

	/** 签名不能为空 */
	public static final int SIGN_MUST_BE_NOT_NULL = 100014;

	/** ------------------------------------------------- */
	/** format不能为空 */
	public static final int FORMAT_NOT_FOUND = 200000;

	/** 详见AccessType支持的类型 */
	public static final int ACCESS_TYPE_NOT_SUPPORT = 200001;

	/** TOKEN不能为空 */
	public static final int NULL_NOT_TOKEN = 200014;

	/** 接口未指定AccessToken注解 */
	public static final int INTERFACE_UNSET_ACCESSTOKEN = 200002;

	/** format类型不支持，现在只支持JSON/XML */
	public static final int FORMAT_NOT_ALLOWED = 200003;

	/** 系统异常 */
	public static final int SYSTEM_EXCEPTION = 200004;

	/** 入口检查异常 */
	public static final int ENTRYACE_VALIDATE_EXCEPTION = 200005;

	/** 请求的HTTP METHOD不支持，必须为POST */
	public static final int HTTP_METHOD_MUST_BE_POST = 200006;

	/** 请求内容不能为空 */
	public static final int REQUEST_CONTENT_MUST_BE_NOT_NULL = 200007;

	/** 请求内容长度超过限制 */
	public static final int REQUEST_ENTITY_TOO_LARGE = 200008;

	/** 请求内容长度不匹配 */
	public static final int REQUEST_LENGTH_NOT_MATCH = 200009;

	/** 访问IP被拒绝 */
	public static final int IP_DENIED = 200010;

	/** 参数校验失败 */
	public static final int PARAMETER_CHECK_FAILED = 200011;

	/** 主键或唯一键约束失败 */
	public static final int DUPLICATE_KEY_FAILED = 200012;

	/** 数据库字段超出列的范围值 */
	public static final int DB_CLUMN_LENGTH_NOT_MATCH = 200013;

	/** id不能为空 */
	public static final int ID_CAN_NOT_BLANK = 201000;

	/** 上传文件名称不能为空 */
	public static final int UPLOAD_FILE_NAME_IS_NULL = 201001;

	/** 上传文件不存在 */
	public static final int UPLOAD_FILE_IS_NOT_EXISTS = 201002;

	/** 上传文件异常 */
	public static final int UPLOAD_FILE_PROCESS_EXCEPTION = 201003;
}
