package org.hopeframework.biz.api.entity.input;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hopeframework.biz.api.entity.PageQo;

/**
 * Demo 入参
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemoInput extends PageQo {

    private Long id;

    /** 名称 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 备注 */
    private String remark;
}