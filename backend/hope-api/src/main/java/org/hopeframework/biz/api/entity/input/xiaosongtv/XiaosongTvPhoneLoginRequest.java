package org.hopeframework.biz.api.entity.input.xiaosongtv;

import lombok.Data;

@Data
public class XiaosongTvPhoneLoginRequest {
    private String phone;
    private String code;
    private String nickname;
}
