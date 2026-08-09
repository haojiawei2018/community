package org.hopeframework.core.request;

/**
 * 分页请求信息
 *
 * @author 摇光 [NO.0146]
 */
public class PagePojo extends BasePojo {

    /** 每页记录数(默认每页20条) */
    private int pageSize =20;

    /** 当前页码(默认第一页) */
    private int currentPage =1;


    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

}
