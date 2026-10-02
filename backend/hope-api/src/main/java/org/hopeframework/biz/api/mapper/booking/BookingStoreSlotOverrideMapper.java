package org.hopeframework.biz.api.mapper.booking;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.hopeframework.biz.api.model.booking.BookingStoreSlotOverride;

import java.sql.Date;

@Mapper
public interface BookingStoreSlotOverrideMapper extends BaseMapper<BookingStoreSlotOverride> {
    @SqlParser(filter = true)
    @Select("SELECT * FROM booking_store_slot_override WHERE tenant_id = #{tenantId} " +
            "AND store_id = #{storeId} AND slot_date = #{slotDate} LIMIT 1")
    BookingStoreSlotOverride findByStoreAndDate(@Param("tenantId") Long tenantId,
                                                @Param("storeId") Long storeId,
                                                @Param("slotDate") Date slotDate);
}
