package org.hopeframework.biz.api.entity.input.xiaosongtv;

import lombok.Data;

import java.util.Map;

@Data
public class XiaosongTvSaveCdRequest {
    private String name;
    private Map<String, Object> sounds;
}
