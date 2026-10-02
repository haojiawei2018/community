package org.hopeframework.biz.api.model.booking;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("booking_category")
public class BookingCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private String categoryCode;
    private String categoryName;
    private String iconUrl;
    private Integer iconIndex;
    private String status;
    private Integer sortOrder;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
