package org.hopeframework.biz.api.mapper.booking;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.hopeframework.biz.api.model.booking.BookingAppointment;

import java.sql.Date;
import java.sql.Time;

@Mapper
public interface BookingAppointmentMapper extends BaseMapper<BookingAppointment> {
    @SqlParser(filter = true)
    @Select("SELECT * FROM booking_appointment WHERE tenant_id = #{tenantId} " +
            "AND appointment_no = #{appointmentNo} AND deleted = 0 FOR UPDATE")
    BookingAppointment lockByAppointmentNo(@Param("tenantId") Long tenantId,
                                           @Param("appointmentNo") String appointmentNo);

    @SqlParser(filter = true)
    @Select("SELECT * FROM booking_appointment WHERE tenant_id = #{tenantId} " +
            "AND id = #{appointmentId} AND member_id = #{memberId} AND deleted = 0 FOR UPDATE")
    BookingAppointment lockByIdForMember(@Param("tenantId") Long tenantId,
                                         @Param("appointmentId") Long appointmentId,
                                         @Param("memberId") Long memberId);

    @SqlParser(filter = true)
    @Select("SELECT * FROM booking_appointment WHERE tenant_id = #{tenantId} " +
            "AND id = #{appointmentId} AND deleted = 0 FOR UPDATE")
    BookingAppointment lockByIdForAdmin(@Param("tenantId") Long tenantId,
                                        @Param("appointmentId") Long appointmentId);

    @SqlParser(filter = true)
    @Select("SELECT COALESCE(SUM(people_count), 0) FROM booking_appointment " +
            "WHERE tenant_id = #{tenantId} AND store_id = #{storeId} " +
            "AND appointment_date = #{appointmentDate} AND slot_time = #{slotTime} AND deleted = 0 " +
            "AND (status = 'CONFIRMED' OR (status = 'PENDING_PAYMENT' AND expires_at > NOW()))")
    Integer sumReservedPeople(@Param("tenantId") Long tenantId,
                              @Param("storeId") Long storeId,
                              @Param("appointmentDate") Date appointmentDate,
                              @Param("slotTime") Time slotTime);

    @SqlParser(filter = true)
    @Update("UPDATE booking_appointment SET status = 'CANCELLED', cancelled_at = NOW(), updated_at = NOW() " +
            "WHERE tenant_id = #{tenantId} AND member_id = #{memberId} AND status = 'PENDING_PAYMENT' " +
            "AND expires_at <= NOW() AND deleted = 0")
    int expirePending(@Param("tenantId") Long tenantId, @Param("memberId") Long memberId);

    @SqlParser(filter = true)
    @Update("UPDATE booking_appointment SET status = 'CANCELLED', cancelled_at = NOW(), updated_at = NOW() " +
            "WHERE status = 'PENDING_PAYMENT' AND payment_status = 'UNPAID' " +
            "AND expires_at <= NOW() AND deleted = 0")
    int expireAllPending();
}
