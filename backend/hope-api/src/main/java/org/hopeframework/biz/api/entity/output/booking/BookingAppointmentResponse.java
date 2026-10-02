package org.hopeframework.biz.api.entity.output.booking;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class BookingAppointmentResponse {
    private Long id;
    private String appointmentNo;
    private Long storeId;
    private String storeName;
    private Long productId;
    private String productName;
    private String appointmentDate;
    private String slotTime;
    private Integer peopleCount;
    private BigDecimal saleAmount;
    private BigDecimal depositAmount;
    private String remark;
    private String status;
    private String statusText;
    private String paymentStatus;
    private String createdAt;
    private String cancelReason;
    private Boolean canReschedule;
    private String rescheduleReason;
    private String servicePhone;
    private Boolean paymentRequired;
    /** 微信支付接口接入后返回 timeStamp、nonceStr、package、signType、paySign。 */
    private Map<String, String> paymentParams;
}
