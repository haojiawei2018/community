package org.hopeframework.biz.api.module.system.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class CommunityBootstrapResponse {
    private Long communityId;
    private String communityCode;
    private String communityName;
    private String logoUrl;
    private String edition = "COMMUNITY";
    private Map<String, Boolean> features = new LinkedHashMap<>();
}
