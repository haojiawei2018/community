package org.hopeframework.biz.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
public class PageResult2 <T> implements Serializable {

    private List<T> rows;

    private long total;
    private long pageSize;
    private long page;

    //总价
    private BigDecimal tagPrice;
}
