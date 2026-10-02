package org.hopeframework.biz.api.mapper.booking;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.booking.BookingCategory;

@Mapper
public interface BookingCategoryMapper extends BaseMapper<BookingCategory> {
}
