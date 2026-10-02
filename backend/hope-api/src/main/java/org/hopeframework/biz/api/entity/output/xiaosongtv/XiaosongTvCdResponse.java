package org.hopeframework.biz.api.entity.output.xiaosongtv;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class XiaosongTvCdResponse {
    private Long id;
    private String name;
    private Map<String, Object> sounds;
    private Date createdAt;
    private Date updatedAt;
}
