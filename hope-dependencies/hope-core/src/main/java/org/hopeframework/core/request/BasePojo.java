package org.hopeframework.core.request;

/**
 * 公共请求信息
 *
 * @author mif [NO.0212]
 */
public abstract class BasePojo {

    /**
     * 权限ID
     */
    private String accessId;
    /**
     * 请求类型 (1:WEB)
     */
    private String requestType;

    /**
     * 用户编号
     */
    private String userNo;

    private String ip; // 用户ip编号

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public abstract int getPageSize();

    public abstract void setPageSize(int pageSize);

    public abstract int getCurrentPage();

    public abstract void setCurrentPage(int currentPage);
}
