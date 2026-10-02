package org.hopeframework.biz.api.service.booking;

import org.hopeframework.biz.api.mapper.booking.BookingAppointmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 定时作废超过支付期限的预约单，并立即释放对应档期名额。 */
@Component
public class BookingPendingAppointmentScheduler {
    private static final Logger log = LoggerFactory.getLogger(BookingPendingAppointmentScheduler.class);

    private final BookingAppointmentMapper appointmentMapper;

    public BookingPendingAppointmentScheduler(BookingAppointmentMapper appointmentMapper) {
        this.appointmentMapper = appointmentMapper;
    }

    @Scheduled(fixedDelay = 10000L, initialDelay = 10000L)
    public void expireUnpaidAppointments() {
        int count = appointmentMapper.expireAllPending();
        if (count > 0) {
            log.info("已自动作废 {} 个超过5分钟未支付的预约订单", count);
        }
    }
}
