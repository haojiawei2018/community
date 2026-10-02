package org.hopeframework.biz.api.mapper.flashcard;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.hopeframework.biz.api.model.flashcard.FlashcardHomeCard;

@Mapper
public interface FlashcardHomeCardMapper extends BaseMapper<FlashcardHomeCard> {
    @Delete("DELETE FROM flashcard_app_home_card WHERE tenant_id = #{tenantId}")
    int deleteAllByTenant(@Param("tenantId") Long tenantId);
}
