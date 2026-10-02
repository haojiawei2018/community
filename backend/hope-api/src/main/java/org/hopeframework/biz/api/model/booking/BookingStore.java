package org.hopeframework.biz.api.model.booking;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("booking_store")
public class BookingStore {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private String storeName;
    private String logoUrl;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String businessHours;
    private String servicePhone;
    /** JSON 数组，例如 [{"time":"08:30","capacity":2}]。 */
    private String slotConfig;
    private Integer advanceMinutes;
    private String status;
    private Integer sortOrder;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
