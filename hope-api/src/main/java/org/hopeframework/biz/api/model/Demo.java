package org.hopeframework.biz.api.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 演示表 demo
 */
@Data
@TableName("demo")
public class Demo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 名称 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private Date createTime;
}