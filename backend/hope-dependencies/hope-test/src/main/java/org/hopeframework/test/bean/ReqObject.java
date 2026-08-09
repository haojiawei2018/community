package org.hopeframework.test.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="request")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ReqObject<T> {
	@XmlElement
	private ReqCommon common;
	@XmlAnyElement(lax=true)
	private T content;
  
	public ReqObject() {
	}

	public ReqObject(T content) {
		if(null == this.common){
			this.common = new ReqCommon();
		}
		this.content = content;
	}
	
	public ReqObject(ReqCommon common, T content) {
		this.common = new ReqCommon();
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
