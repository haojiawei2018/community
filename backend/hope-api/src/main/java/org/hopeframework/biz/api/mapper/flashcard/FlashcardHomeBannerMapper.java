package org.hopeframework.biz.api.mapper.flashcard;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.hopeframework.biz.api.model.flashcard.FlashcardHomeBanner;

@Mapper
public interface FlashcardHomeBannerMapper extends BaseMapper<FlashcardHomeBanner> {
    @Delete("DELETE FROM flashcard_app_home_banner WHERE tenant_id = #{tenantId}")
    int deleteAllByTenant(@Param("tenantId") Long tenantId);
}
