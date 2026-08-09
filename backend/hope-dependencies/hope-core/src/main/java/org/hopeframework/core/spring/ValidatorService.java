package org.hopeframework.core.spring;

import org.hopeframework.core.cache.AccessDao;
import org.hopeframework.core.cache.AccessInfo;
import org.hopeframework.core.cache.BasicDataDao;
import org.hopeframework.core.cache.SystemBasicData;
import org.hopeframework.core.constant.Constants;
import org.hopeframework.core.constant.RequestConst;
import org.hopeframework.core.constant.ResponseConst;
import org.hopeframework.core.enums.AccessType;
import org.hopeframework.core.enums.SignType;
import org.hopeframework.core.exception.HopeException;
import org.hopeframework.utils.StringLocalUtils;
import org.hopeframework.utils.encrypt.MD5Utils;
import org.hopeframework.utils.sign.HmacSha1Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 相关校验
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
@Component
public class ValidatorService {


	private Logger logger = LoggerFactory.getLogger(ValidatorService.class);
	/**
	 * 文件上传
	 */
	private static final String FILE_UPLOAD_YES = "1";

	@Autowired
	private AccessDao accessDao;

	@Autowired
	private BasicDataDao basicDataDao;

	/*@Autowired
	private AlluserMapper alluserMapper;*/



	/**
	 * 用户访问控制校验
	 *
	 * @param target
	 * @param bean
	 */
	public void doRequestTypeValid(Object target, ValidatorBean bean) {
		// 获取请求的request
		HttpServletRequest request = bean.getRequest();
		// 获取注解中指定的签名加密方式
		SignType signType = bean.getSignType();
		// 获取请求request的签名
		String sign = (String) request.getAttribute(RequestConst.REQUEST_SIGN);
		// 获取请求request的内容
		String content = bean.getContent();
		// 获取请求request的编码格式
		String encoding = bean.getEncoding();
		AccessType[] accessTypes = bean.getAccessTypes();

		String token=request.getHeader("token");
		//System.out.println("====token:"+token);
		/*if(null == token){
			throw new HopeException(ResponseConst.NULL_NOT_TOKEN);
		}*/
		if (null == accessTypes) {
			throw new HopeException(ResponseConst.ACCESS_TYPE_NOT_SUPPORT);
		}
		// 获取是否要进行ACCESSID,KEY检查
		boolean checkAccess = bean.isCheckAccess();
		String fileupload = request.getHeader(RequestConst.FILE_UPLOAD);
		String accessId = "";
		String userNo = "";
		String requestType = "";
		// 校验Access
		if (checkAccess) {
			if (FILE_UPLOAD_YES.equals(fileupload)) {
				// 取得请求request里的accessid
				accessId = request.getHeader(RequestConst.ACCESS_ID);
				// 设置用户编号
				userNo = request.getHeader(RequestConst.USER_NO);
				requestType = request.getHeader(RequestConst.REQ_TYPE);
			} else {
				BeanWrapper jim = new BeanWrapperImpl(target);
				// 取得请求request里的accessid
				accessId = (String) jim.getPropertyValue(RequestConst.ACCESS_ID);
				// 设置用户编号
				userNo = (String) jim.getPropertyValue(RequestConst.USER_NO);
				requestType = (String) jim.getPropertyValue(RequestConst.REQ_TYPE);
			}

			logger.info("reuqest accessId == {}, userNo == {},requestType=={}", accessId, userNo, requestType);
			if (StringUtils.isEmpty(accessId)) {
				throw new HopeException(ResponseConst.ACCESS_ID_IS_NULL);
			}

			// 校验requestType
			if (StringUtils.isEmpty(requestType)) {
				throw new HopeException(ResponseConst.ACCESS_TYPE_NOT_SUPPORT);
			}
			//bean.setRequestType(requestType);

			AccessType.checkAccessType(requestType, accessTypes);

			// 根据请求类型 获取ACCESS信息
			String accessRedisKey = getAccessKey(requestType, userNo);

			AccessInfo accessInfo = accessDao.get(accessRedisKey);
			if (null == accessInfo || StringUtils.isEmpty(accessInfo.getUserNo())) {
				logger.info("accessRedisKey ={},userNo={}", accessRedisKey, userNo);
				throw new HopeException(ResponseConst.SESSION_TIME_OUT);
			}

			// userNo作为key:动态Access
			if (accessRedisKey.equals(userNo)) {
				// 更新redis
				accessDao.putRedis(accessInfo);
			}

			doSignCheck(signType, accessInfo.getAccessKey(), sign, content, encoding);

		}
	}

	/**
	 * 根据requestType获取访问类型
	 *
	 * @param requestType
	 * @param userNo
	 * @return
	 */

	private String getAccessKey(String requestType, String userNo) {

		switch (StringLocalUtils.nullToIntZero(requestType)) {
		case 1:
			return Constants.ACCEESS_NAME_WEB;// 固定ACCESS
		default:
			return userNo;
		}
	}

	/**
	 * 用户访问IP校验
	 *
	 * @param bean
	 */
	public void doIpLimitValid(ValidatorBean bean) {
		try {
			// 从db中获取ip过滤规则
			//String requestType = bean.getRequestType();
			String ip = bean.getIp();
			String ipFilter = "";

			if (StringUtils.isEmpty(ip)) {
				//logger.error("访问IP为空，服务器拒绝");
				throw new HopeException(ResponseConst.IP_DENIED);
			}
			//logger.debug("请求IP:" + ip);
			//
			//String confKey = getIpLimitConfKey(requestType);
			//SystemBasicData data = basicDataDao.getIplimit(confKey);
			// 白名单过滤
			/*if (null != data && !StringUtils.isEmpty(data.getModule())) {
				ipFilter = data.getConfValue();
				if (!StringLocalUtils.checkConfig(ipFilter, ip)) {
					throw new HopeException(ResponseConst.IP_DENIED);
				}
			}*/
			//logger.debug("白名单认证成功");
		} catch (BeansException e) {
			//logger.error("IP验证失败，服务器异常" + e.getMessage());
		}
	}

	/**
	 * 根据requestType获取访问类型
	 *
	 * @param requestType
	 * @return
	 */

	private String getIpLimitConfKey(String requestType) {

		switch (StringLocalUtils.nullToIntZero(requestType)) {
		case 1:
			return Constants.IPLIMIT_TYPE_WEB;
		}
		return null;
	}

	/**
	 * 签名
	 *
	 * @param signType
	 *            签名方式
	 * @param accessKey
	 *            后台类型
	 * @param sign
	 *            签名
	 * @param content
	 *            请求体
	 * @param encoding
	 *            编码方式
	 * @return
	 */
	private void doSignCheck(SignType signType, String accessKey, String sign, String content, String encoding) {

		// 验证MD5加密的签名
		if (SignType.MD5 == signType) {
			this.md5Sign(content, sign);
		}
		// 验证HMACSHA加密的签名
		else {
			this.hmacShaSign(accessKey, sign, content, encoding);
		}

	}

	/**
	 * 验证MD5加密的签名
	 *
	 * @param content
	 * @param sign
	 */
	private void md5Sign(String content, String sign) {
		String encryptContent = MD5Utils.md5(content).toLowerCase();
		if (!sign.equals(encryptContent)) {
			throw new HopeException(ResponseConst.SIGN_NAME_NOT_MATCH);
		}
	}

	/**
	 * 验证HMACSHA加密的签名
	 *
	 * @param accessKey
	 * @param sign
	 * @param sign
	 * @param content
	 * @param encoding
	 * @throws HopeException
	 */
	private void hmacShaSign(String accessKey, String sign, String content, String encoding) {

		boolean bool = HmacSha1Util.signCheck(MD5Utils.md5(content).toLowerCase(), accessKey, sign, encoding);
		if (!bool) {
			throw new HopeException(ResponseConst.SIGN_NAME_NOT_MATCH);
		}
	}
}
