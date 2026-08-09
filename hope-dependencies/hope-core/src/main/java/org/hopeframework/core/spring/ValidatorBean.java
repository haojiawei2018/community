package org.hopeframework.core.spring;

import javax.servlet.http.HttpServletRequest;

import org.hopeframework.core.enums.AccessType;
import org.hopeframework.core.enums.SignType;
import org.springframework.validation.Errors;

/**
 * 请求检查
 * 
 * @author haojiawei
 *
 * @version hopeframework-1.0.0
 * 
 * @since 1.0.0
 */
public class ValidatorBean {
	private HttpServletRequest request;
	private AccessType[] accessTypes;
	private SignType signType;
	private String content;
	private String encoding;
	private Object target;
	private Errors errors;
	private String ip;
	private boolean checkAccess;

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public AccessType[] getAccessTypes() {
		return accessTypes;
	}

	public void setAccessTypes(AccessType[] accessTypes) {
		this.accessTypes = accessTypes;
	}

	public SignType getSignType() {
		return signType;
	}

	public void setSignType(SignType signType) {
		this.signType = signType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isCheckAccess() {
		return checkAccess;
	}

	public void setCheckAccess(boolean checkAccess) {
		this.checkAccess = checkAccess;
	}

}
