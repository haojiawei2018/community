package org.hopeframework.biz.api.service.xiaosongtv;

import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvLoginRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvPhoneLoginRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvRegisterRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvSaveCdRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.xiaosongtv.XiaosongTvCdResponse;

import java.util.List;

public interface IXiaosongTvService {
    TokenResponse register(XiaosongTvRegisterRequest request);

    TokenResponse login(XiaosongTvLoginRequest request);

    TokenResponse phoneLogin(XiaosongTvPhoneLoginRequest request);

    XiaosongTvCdResponse saveCd(XiaosongTvSaveCdRequest request);

    List<XiaosongTvCdResponse> listCds();
}
