package org.hopeframework.biz.api.service.impl.xiaosongtv;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.security.XiaosongTvAccessTokenService;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvLoginRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvPhoneLoginRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvRegisterRequest;
import org.hopeframework.biz.api.entity.input.xiaosongtv.XiaosongTvSaveCdRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.entity.output.xiaosongtv.XiaosongTvCdResponse;
import org.hopeframework.biz.api.mapper.xiaosongtv.XiaosongTvCdMapper;
import org.hopeframework.biz.api.mapper.xiaosongtv.XiaosongTvUserMapper;
import org.hopeframework.biz.api.model.xiaosongtv.XiaosongTvCd;
import org.hopeframework.biz.api.model.xiaosongtv.XiaosongTvUser;
import org.hopeframework.biz.api.service.xiaosongtv.IXiaosongTvService;
import org.hopeframework.biz.api.service.xiaosongtv.IXiaosongTvPhoneCodeService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class XiaosongTvServiceImpl implements IXiaosongTvService {
    private static final int MAX_CD_COUNT = 5;
    private static final Set<String> SOUND_IDS = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "piano", "candy", "train", "rain", "planet", "wind", "river", "campfire", "noise"
    )));

    private final XiaosongTvUserMapper userMapper;
    private final XiaosongTvCdMapper cdMapper;
    private final XiaosongTvAccessTokenService accessTokenService;
    private final IXiaosongTvPhoneCodeService phoneCodeService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public XiaosongTvServiceImpl(XiaosongTvUserMapper userMapper,
                                 XiaosongTvCdMapper cdMapper,
                                 XiaosongTvAccessTokenService accessTokenService,
                                 IXiaosongTvPhoneCodeService phoneCodeService) {
        this.userMapper = userMapper;
        this.cdMapper = cdMapper;
        this.accessTokenService = accessTokenService;
        this.phoneCodeService = phoneCodeService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse register(XiaosongTvRegisterRequest request) {
        validateRegister(request);
        String username = request.getUsername().trim().toLowerCase(Locale.ROOT);
        if (findByUsername(username) != null) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "用户名已注册");
        }

        XiaosongTvUser user = new XiaosongTvUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(StringUtils.hasText(request.getNickname())
                ? request.getNickname().trim() : username);
        user.setStatus("ACTIVE");
        user.setLastLoginAt(new Date());
        userMapper.insert(user);
        return tokenResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse login(XiaosongTvLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            throw badRequest("用户名和密码不能为空");
        }
        XiaosongTvUser user = findByUsername(request.getUsername().trim().toLowerCase(Locale.ROOT));
        if (user == null || !StringUtils.hasText(user.getPasswordHash())
                || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误");
        }
        requireActive(user);
        user.setLastLoginAt(new Date());
        userMapper.updateById(user);
        return tokenResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse phoneLogin(XiaosongTvPhoneLoginRequest request) {
        if (request == null) throw badRequest("手机号和验证码不能为空");
        phoneCodeService.verifyAndConsume(request.getPhone(), request.getCode());
        String phone = request.getPhone().trim();
        XiaosongTvUser user = findByPhone(phone);
        if (user == null) {
            user = new XiaosongTvUser();
            user.setPhone(phone);
            user.setNickname(resolvePhoneNickname(request.getNickname(), phone));
            user.setStatus("ACTIVE");
            user.setLastLoginAt(new Date());
            userMapper.insert(user);
        } else {
            requireActive(user);
            user.setLastLoginAt(new Date());
            if (!StringUtils.hasText(user.getNickname())) {
                user.setNickname(resolvePhoneNickname(request.getNickname(), phone));
            }
            userMapper.updateById(user);
        }
        return tokenResponse(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public XiaosongTvCdResponse saveCd(XiaosongTvSaveCdRequest request) {
        if (request == null || !StringUtils.hasText(request.getName())) {
            throw badRequest("CD名称不能为空");
        }
        String name = request.getName().trim();
        if (name.codePointCount(0, name.length()) > 6) {
            throw badRequest("CD名称最多6个字符");
        }
        Map<String, Object> sounds = normalizeSounds(request.getSounds());
        Long userId = currentUserId();
        int count = cdMapper.selectCount(new LambdaQueryWrapper<XiaosongTvCd>()
                .eq(XiaosongTvCd::getUserId, userId));
        if (count >= MAX_CD_COUNT) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "收藏夹最多保存5条CD");
        }

        XiaosongTvCd cd = new XiaosongTvCd();
        cd.setUserId(userId);
        cd.setName(name);
        cd.setSoundsJson(JSON.toJSONString(sounds));
        cdMapper.insert(cd);
        return toCdResponse(cd);
    }

    @Override
    public List<XiaosongTvCdResponse> listCds() {
        List<XiaosongTvCd> cds = cdMapper.selectList(new LambdaQueryWrapper<XiaosongTvCd>()
                .eq(XiaosongTvCd::getUserId, currentUserId())
                .orderByDesc(XiaosongTvCd::getCreatedAt)
                .orderByDesc(XiaosongTvCd::getId));
        List<XiaosongTvCdResponse> result = new ArrayList<>();
        for (XiaosongTvCd cd : cds) result.add(toCdResponse(cd));
        return result;
    }

    private void validateRegister(XiaosongTvRegisterRequest request) {
        if (request == null || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getPassword())) {
            throw badRequest("用户名和密码不能为空");
        }
        String username = request.getUsername().trim();
        if (!username.matches("[A-Za-z0-9_]{4,32}")) {
            throw badRequest("用户名须为4至32位字母、数字或下划线");
        }
        int passwordLength = request.getPassword().length();
        if (passwordLength < 8 || passwordLength > 72) {
            throw badRequest("密码长度须为8至72位");
        }
        if (StringUtils.hasText(request.getNickname())
                && request.getNickname().trim().codePointCount(0, request.getNickname().trim().length()) > 20) {
            throw badRequest("昵称最多20个字符");
        }
    }

    private XiaosongTvUser findByUsername(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<XiaosongTvUser>()
                .eq(XiaosongTvUser::getUsername, username)
                .last("LIMIT 1"));
    }

    private XiaosongTvUser findByPhone(String phone) {
        return userMapper.selectOne(new LambdaQueryWrapper<XiaosongTvUser>()
                .eq(XiaosongTvUser::getPhone, phone)
                .last("LIMIT 1"));
    }

    private String resolvePhoneNickname(String nickname, String phone) {
        if (StringUtils.hasText(nickname)) {
            String clean = nickname.trim();
            if (clean.codePointCount(0, clean.length()) > 20) throw badRequest("昵称最多20个字符");
            return clean;
        }
        return "用户" + phone.substring(phone.length() - 4);
    }

    private void requireActive(XiaosongTvUser user) {
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "账号已被停用");
        }
    }

    private Long currentUserId() {
        AuthPrincipal principal = AuthContext.require();
        if (!principal.isXiaosongTvUser()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录身份无权访问小松的电视机");
        }
        XiaosongTvUser user = userMapper.selectById(principal.getUserId());
        if (user == null) throw new HopeException(HttpStatus.UNAUTHORIZED.value(), "用户不存在，请重新登录");
        requireActive(user);
        return user.getId();
    }

    private Map<String, Object> normalizeSounds(Map<String, Object> source) {
        if (source == null || source.isEmpty()) throw badRequest("请至少设置一个声音");
        if (source.size() > SOUND_IDS.size()) throw badRequest("声音配置数量不正确");
        Map<String, Object> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            if (!SOUND_IDS.contains(entry.getKey())) throw badRequest("不支持的声音：" + entry.getKey());
            if (!(entry.getValue() instanceof Map)) throw badRequest("声音配置格式不正确");
            Map<?, ?> setting = (Map<?, ?>) entry.getValue();
            Object rawVolume = setting.get("volume");
            if (!(rawVolume instanceof Number)) throw badRequest("声音音量必须是0至100的数字");
            int volume = ((Number) rawVolume).intValue();
            if (volume < 0 || volume > 100) throw badRequest("声音音量必须在0至100之间");
            Object rawActive = setting.get("active");
            boolean active = rawActive instanceof Boolean ? (Boolean) rawActive : volume > 0;
            Map<String, Object> cleanSetting = new LinkedHashMap<>();
            cleanSetting.put("volume", volume);
            cleanSetting.put("active", active && volume > 0);
            normalized.put(entry.getKey(), cleanSetting);
        }
        return normalized;
    }

    private TokenResponse tokenResponse(XiaosongTvUser user) {
        Long tenantId = TenantContext.requireTenantId();
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessTokenService.create(user.getId(), tenantId));
        response.setExpiresIn(0L);
        UserSessionResponse session = new UserSessionResponse();
        session.setUserId(user.getId());
        session.setMemberId(user.getId());
        session.setTenantId(tenantId);
        session.setUsername(user.getUsername());
        session.setNickname(user.getNickname());
        session.setDisplayName(user.getNickname());
        session.setMemberStatus(user.getStatus());
        session.setRoles(Collections.singletonList("XIAOSONG_TV_USER"));
        response.setUser(session);
        return response;
    }

    @SuppressWarnings("unchecked")
    private XiaosongTvCdResponse toCdResponse(XiaosongTvCd cd) {
        XiaosongTvCdResponse response = new XiaosongTvCdResponse();
        response.setId(cd.getId());
        response.setName(cd.getName());
        Map<String, Object> sounds = JSON.parseObject(cd.getSoundsJson(), LinkedHashMap.class);
        response.setSounds(sounds == null ? new LinkedHashMap<>() : sounds);
        response.setCreatedAt(cd.getCreatedAt());
        response.setUpdatedAt(cd.getUpdatedAt());
        return response;
    }

    private HopeException badRequest(String message) {
        return new HopeException(HttpStatus.BAD_REQUEST.value(), message);
    }
}
