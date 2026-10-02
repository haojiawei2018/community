package org.hopeframework.biz.api.service.impl.xiaosongtv;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.hopeframework.biz.api.service.xiaosongtv.IXiaosongTvPhoneCodeService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class XiaosongTvPhoneCodeServiceImpl implements IXiaosongTvPhoneCodeService {
    private static final Pattern MAINLAND_PHONE = Pattern.compile("^1[3-9]\\d{9}$");
    private static final long CODE_TTL_MILLIS = 5L * 60L * 1000L;
    private static final long SEND_INTERVAL_MILLIS = 60L * 1000L;
    private static final int MAX_ATTEMPTS = 5;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, CodeState> codes = new ConcurrentHashMap<>();
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String regionId;
    private final String signName;
    private final String templateCode;
    private final String hashSalt;

    public XiaosongTvPhoneCodeServiceImpl(
            @Value("${xiaosong-tv.sms.access-key-id:}") String accessKeyId,
            @Value("${xiaosong-tv.sms.access-key-secret:}") String accessKeySecret,
            @Value("${xiaosong-tv.sms.region-id:default}") String regionId,
            @Value("${xiaosong-tv.sms.sign-name:成都市斤为科技}") String signName,
            @Value("${xiaosong-tv.sms.template-code:SMS_317060033}") String templateCode,
            @Value("${xiaosong-tv.security.access-token-secret}") String hashSalt) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.regionId = regionId;
        this.signName = signName;
        this.templateCode = templateCode;
        this.hashSalt = hashSalt;
    }

    @Override
    public void send(String rawPhone) {
        String phone = normalizePhone(rawPhone);
        ensureConfigured();
        long now = System.currentTimeMillis();
        CodeState old = codes.get(phone);
        if (old != null && now - old.sentAt < SEND_INTERVAL_MILLIS) {
            throw new HopeException(HttpStatus.TOO_MANY_REQUESTS.value(), "验证码发送过于频繁，请稍后再试");
        }
        String code = String.format("%04d", secureRandom.nextInt(10000));
        sendByAliyun(phone, code);
        codes.put(phone, new CodeState(hash(phone, code), now, now + CODE_TTL_MILLIS));
    }

    @Override
    public void verifyAndConsume(String rawPhone, String rawCode) {
        String phone = normalizePhone(rawPhone);
        String code = StringUtils.hasText(rawCode) ? rawCode.trim() : "";
        if (!code.matches("^\\d{4}$")) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "请输入4位短信验证码");
        }
        CodeState state = codes.get(phone);
        long now = System.currentTimeMillis();
        if (state == null || now > state.expiresAt) {
            codes.remove(phone);
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "验证码不存在或已过期");
        }
        if (state.attempts >= MAX_ATTEMPTS) {
            codes.remove(phone);
            throw new HopeException(HttpStatus.TOO_MANY_REQUESTS.value(), "验证码错误次数过多，请重新获取");
        }
        if (!MessageDigest.isEqual(state.codeHash, hash(phone, code))) {
            state.attempts++;
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "验证码错误");
        }
        codes.remove(phone);
    }

    private void sendByAliyun(String phone, String code) {
        try {
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);
            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
            request.putQueryParameter("RegionId", regionId);
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
            CommonResponse response = client.getCommonResponse(request);
            JSONObject body = JSON.parseObject(response.getData());
            if (body == null || !"OK".equals(body.getString("Code"))) {
                String message = body == null ? null : body.getString("Message");
                throw new HopeException(HttpStatus.BAD_GATEWAY.value(),
                        StringUtils.hasText(message) ? "短信发送失败：" + message : "短信发送失败");
            }
        } catch (HopeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new HopeException(HttpStatus.BAD_GATEWAY.value(), "暂时无法发送短信验证码", exception);
        }
    }

    private byte[] hash(String phone, String code) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest((phone + ':' + code + ':' + hashSalt).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private String normalizePhone(String rawPhone) {
        String phone = StringUtils.hasText(rawPhone) ? rawPhone.trim() : "";
        if (!MAINLAND_PHONE.matcher(phone).matches()) {
            throw new HopeException(HttpStatus.BAD_REQUEST.value(), "请输入正确的11位手机号");
        }
        return phone;
    }

    private void ensureConfigured() {
        if (!StringUtils.hasText(accessKeyId) || !StringUtils.hasText(accessKeySecret)
                || !StringUtils.hasText(signName) || !StringUtils.hasText(templateCode)) {
            throw new HopeException(HttpStatus.SERVICE_UNAVAILABLE.value(), "小松的电视机短信服务尚未配置");
        }
    }

    private static final class CodeState {
        private final byte[] codeHash;
        private final long sentAt;
        private final long expiresAt;
        private int attempts;

        private CodeState(byte[] codeHash, long sentAt, long expiresAt) {
            this.codeHash = codeHash;
            this.sentAt = sentAt;
            this.expiresAt = expiresAt;
        }
    }
}
