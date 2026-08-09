package org.hopeframework.core.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hopeframework.core.constant.RequestConst;

import com.fasterxml.jackson.annotation.JsonRootName;


@JsonRootName(RequestConst.REQUEST_ROOT_NAME)
@XmlRootElement(name = RequestConst.REQUEST_ROOT_NAME)
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ReqBody<T> {

	@XmlElement
	private ReqCommon common;
	@XmlAnyElement(lax = true)
	private T content;

	public ReqBody() {
	}

	public ReqBody(T content) {
		if (null == this.common) {
			this.common = new ReqCommon();
		}
		this.content = content;
	}

	public ReqBody(ReqCommon common, T content) {
		this.common = common;
		this.content = content;
	}

	public ReqCommon getCommon() {
		return common;
	}

	public void setCommon(ReqCommon common) {
		this.common = common;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}
}
