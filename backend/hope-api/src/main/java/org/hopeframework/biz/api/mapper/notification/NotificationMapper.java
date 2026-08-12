package org.hopeframework.biz.api.mapper.notification;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.hopeframework.biz.api.model.notification.CommunityNotification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificationMapper extends BaseMapper<CommunityNotification> {
}
