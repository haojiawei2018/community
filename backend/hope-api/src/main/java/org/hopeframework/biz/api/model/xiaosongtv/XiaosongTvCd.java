package org.hopeframework.biz.api.model.xiaosongtv;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("xiaosong_tv_cd")
public class XiaosongTvCd {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long userId;
    private String name;
    private String soundsJson;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
