package org.hopeframework.biz.api.model.booking;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

@Data
@TableName("booking_appointment")
public class BookingAppointment {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Long tenantId;
    private String appointmentNo;
    private Long userId;
    private Long memberId;
    private Long storeId;
    private Long productId;
    private java.sql.Date appointmentDate;
    private Time slotTime;
    private Integer peopleCount;
    private BigDecimal saleAmount;
    private BigDecimal depositAmount;
    private String remark;
    private String status;
    private String paymentStatus;
    private String transactionId;
    private Date expiresAt;
    private Date paidAt;
    private Date cancelledAt;
    private Date createdAt;
    private Date updatedAt;
    @TableLogic
    private Integer deleted;
}
