package org.hopeframework.biz.api.controller.system;

import org.hopeframework.biz.api.auto.PassToken;
import org.hopeframework.biz.api.common.tenant.SingleCommunityProperties;
import org.hopeframework.biz.api.entity.output.system.CommunityBootstrapResponse;
import org.hopeframework.core.response.RespBody;
import org.hopeframework.core.response.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PassToken
@RestController
@RequestMapping("/api/v1")
public class CommunityBootstrapController {
    private final SingleCommunityProperties properties;

    public CommunityBootstrapController(SingleCommunityProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/bootstrap")
    public RespBody<CommunityBootstrapResponse> bootstrap() {
        CommunityBootstrapResponse response = new CommunityBootstrapResponse();
        response.setCommunityId(properties.getId());
        response.setCommunityCode(properties.getCode());
        response.setCommunityName(properties.getName());
        response.setLogoUrl(properties.getLogoUrl());
        response.getFeatures().put("circle", true);
        response.getFeatures().put("post", true);
        response.getFeatures().put("comment", true);
        response.getFeatures().put("moderation", true);
        return ResultUtil.success(response);
    }
}
