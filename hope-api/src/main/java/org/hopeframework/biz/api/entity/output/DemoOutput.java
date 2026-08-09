package org.hopeframework.biz.api.entity.output;

import lombok.Data;

import java.util.Date;

/**
 * Demo 出参
 */
@Data
public class DemoOutput {

    private Long id;

    private String name;

    private String phone;

    private String remark;

    private Date createTime;
}