package org.hopeframework.biz.api.service.impl.booking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.booking.BookingWechatLoginRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.mapper.booking.BookingUserMapper;
import org.hopeframework.biz.api.model.booking.BookingUser;
import org.hopeframework.biz.api.service.booking.IBookingWechatAuthService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class BookingWechatAuthServiceImpl implements IBookingWechatAuthService {

    private final BookingUserMapper bookingUserMapper;
    private final AccessTokenService accessTokenService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String appId;
    private final String appSecret;

    public BookingWechatAuthServiceImpl(BookingUserMapper bookingUserMapper,
                                        AccessTokenService accessTokenService,
                                        @Value("${booking.wechat.app-id:}") String appId,
                                        @Value("${booking.wechat.app-secret:}") String appSecret) {
        this.bookingUserMapper = bookingUserMapper;
        this.accessTokenService = accessTokenService;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse login(BookingWechatLoginRequest request, String ip) {
        if (request == null || !StringUtils.hasText(request.getCode())) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "微信登录code不能为空");
        }
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(appSecret)) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "微信登录尚未配置AppID或AppSecret");
        }
        JSONObject session = code2Session(request.getCode().trim());
        String openid = session.getString("openid");
        if (!StringUtils.hasText(openid)) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "微信登录未返回openid");
        }

        Date now = new Date();
        BookingUser bookingUser = bookingUserMapper.selectOne(new LambdaQueryWrapper<BookingUser>()
                .eq(BookingUser::getAppId, appId)
                .eq(BookingUser::getOpenid, openid));
        if (bookingUser == null) {
            bookingUser = new BookingUser();
            long compatibilityId = compatibilityId(openid);
            // 旧库保留两个非空列；仅写唯一兼容值，不再读写 iam_user / tenant_member。
            bookingUser.setIamUserId(compatibilityId);
            bookingUser.setMemberId(compatibilityId);
            bookingUser.setAppId(appId);
            bookingUser.setOpenid(openid);
            bookingUser.setUnionid(session.getString("unionid"));
            bookingUser.setNickname(resolveNickname(request, openid));
            bookingUser.setAvatarUrl(request.getAvatarUrl());
            bookingUser.setStatus("ACTIVE");
            bookingUser.setLastLoginAt(now);
            bookingUser.setCreatedAt(now);
            bookingUser.setUpdatedAt(now);
            bookingUser.setDeleted(0);
            bookingUserMapper.insert(bookingUser);
        } else {
            if (StringUtils.hasText(request.getNickname())) bookingUser.setNickname(request.getNickname().trim());
            if (StringUtils.hasText(request.getAvatarUrl())) bookingUser.setAvatarUrl(request.getAvatarUrl());
            if (StringUtils.hasText(session.getString("unionid"))) bookingUser.setUnionid(session.getString("unionid"));
            bookingUser.setLastLoginAt(now);
            bookingUser.setUpdatedAt(now);
            bookingUserMapper.updateById(bookingUser);
        }

        Long bookingUserId = bookingUser.getId();
        AuthPrincipal principal = new AuthPrincipal(
                bookingUserId, bookingUserId, TenantContext.requireTenantId(), "BOOKING");
        TokenResponse response = new TokenResponse();
        // 预约小程序保持长期登录：令牌不设置过期时间，仅在用户主动退出、
        // 令牌被篡改或服务端更换签名密钥时需要重新登录。
        response.setAccessToken(accessTokenService.createPermanent(principal));
        response.setExpiresIn(0L);
        response.setUser(toSession(bookingUser));
        return response;
    }

    private JSONObject code2Session(String code) {
        try {
            String result = restTemplate.getForObject(
                    "https://api.weixin.qq.com/sns/jscode2session?appid={appId}&secret={secret}&js_code={code}&grant_type=authorization_code",
                    String.class, appId, appSecret, code);
            JSONObject body = JSON.parseObject(result);
            Integer errorCode = body.getInteger("errcode");
            if (errorCode != null && errorCode != 0) {
                throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "微信登录失败：" + body.getString("errmsg"));
            }
            return body;
        } catch (HopeException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new HopeException(HttpStatus.BAD_GATEWAY.value(), "调用微信登录服务失败");
        }
    }

    private UserSessionResponse toSession(BookingUser user) {
        UserSessionResponse response = new UserSessionResponse();
        response.setUserId(user.getId());
        response.setMemberId(user.getId());
        response.setTenantId(TenantContext.requireTenantId());
        response.setUsername("booking_user_" + user.getId());
        response.setNickname(user.getNickname());
        response.setDisplayName(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setMemberStatus(user.getStatus());
        return response;
    }

    private String resolveNickname(BookingWechatLoginRequest request, String openid) {
        if (request != null && StringUtils.hasText(request.getNickname())) return request.getNickname().trim();
        return "微信用户" + openid.substring(Math.max(0, openid.length() - 6));
    }

    private long compatibilityId(String openid) {
        long value = 1125899906842597L;
        for (int index = 0; index < openid.length(); index++) value = 31L * value + openid.charAt(index);
        if (value == Long.MIN_VALUE) return Long.MAX_VALUE;
        return Math.abs(value);
    }
}
