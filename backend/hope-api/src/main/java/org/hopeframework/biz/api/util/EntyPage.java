package org.hopeframework.biz.api.util;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class EntyPage implements java.io.Serializable {

    public EntyPage() {
    }

    public EntyPage(@NotNull(message = "请选择第几页") @Min(message = "分页参数错误", value = 1) int currentPage, @NotNull(message = "请填写每页查询数量") @Min(value = 1, message = "分页参数不能小于1") @Max(value = 50, message = "分页参数不能大于50") int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    /**
     *
     */
    private static final long serialVersionUID = 1173808L;
    /**
     * 总条数
     */
    private int totalNumber;
    /**
     * 当前第几页
     */
    @NotNull(message = "请选择第几页")
    @Min(message = "分页参数错误", value = 1)
    private int currentPage;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 每页显示条数
     */
    @NotNull(message = "请填写每页查询数量")
    @Min(value = 1, message = "分页参数不能小于1")
    @Max(value = 50, message = "分页参数不能大于50")
    private int pageSize = 10;
    /**
     * 数据库中limit的参数，从第几条开始取
     */
    private int dbOffset;
    /**
     * 数据库中limit的参数，一共取多少条
     */
    private int dbSize;
    /**
     * 有下一页
     */
    private Boolean hasNextPage;

    /**
     * 有上一页
     */
    private Boolean hasLastPage;
    /**
     * 下一页
     */
    private Integer nextPage;
    /**
     * 上一页
     */
    private Integer lastPage;

    // 该页是否有下一页.
    public Boolean getHasNextPage() {
        hasNextPage = currentPage < totalPage;
        return hasNextPage;
    }

    // 该页是否有上一页.
    public Boolean getHasLastPage() {
        hasLastPage = currentPage > 1;
        return hasLastPage;
    }

    // 获取上一页页数
    public int getLastPage() {
        if (getHasLastPage()) {
            lastPage = currentPage - 1;
        } else {
            lastPage = currentPage;
        }
        return lastPage;
    }

    public int getNextPage() {
        if (getHasNextPage()) {
            nextPage = currentPage + 1;
        } else {
            nextPage = currentPage;
        }
        return nextPage;
    }

    /**
     * 根据当前对象中属性值计算并设置相关属性值
     */

    public void count() {
        // 计算总页数
        int totalPageTemp = this.totalNumber / this.pageSize;
        int plus = (this.totalNumber % this.pageSize) == 0 ? 0 : 1;
        totalPageTemp = totalPageTemp + plus;
        if (totalPageTemp <= 0) {
            totalPageTemp = 1;
        }
        this.totalPage = totalPageTemp;

        // 当前页数小于1设置为1
        if (this.currentPage < 1) {
            this.currentPage = 1;
        }

        // 设置limit的参数
        this.dbOffset = (this.currentPage - 1) * this.pageSize;
        this.dbSize = this.pageSize;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
        this.count();
    }
}
