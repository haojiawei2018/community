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
@TableName("booking_product")
public class BookingProduct {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private Long storeId;
    private Long categoryId;
    private String productName;
    private String coverUrl;
    private Integer coverIndex;
    private String galleryUrls;
    private String description;
    private String noticeContent;
    private String unavailableContent;
    private java.math.BigDecimal salePrice;
    private java.math.BigDecimal depositAmount;
    private Integer featured;
    private String status;
    private Integer sortOrder;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
