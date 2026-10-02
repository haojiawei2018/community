package org.hopeframework.biz.api.service.xiaosongtv;

public interface IXiaosongTvPhoneCodeService {
    void send(String phone);

    void verifyAndConsume(String phone, String code);
}
