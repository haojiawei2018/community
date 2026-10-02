package org.hopeframework.biz.api.mapper.booking;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.hopeframework.biz.api.model.booking.BookingStore;

@Mapper
public interface BookingStoreMapper extends BaseMapper<BookingStore> {
    @SqlParser(filter = true)
    @Select("SELECT * FROM booking_store WHERE tenant_id = #{tenantId} AND id = #{storeId} " +
            "AND status = 'ACTIVE' AND deleted = 0 LIMIT 1 FOR UPDATE")
    BookingStore lockActiveStore(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId);

    @SqlParser(filter = true)
    @Select("SELECT * FROM booking_store WHERE tenant_id = #{tenantId} AND id = #{storeId} " +
            "AND deleted = 0 LIMIT 1 FOR UPDATE")
    BookingStore lockById(@Param("tenantId") Long tenantId, @Param("storeId") Long storeId);
}

