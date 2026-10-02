package org.hopeframework.biz.api.service.impl.flashcard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.hopeframework.biz.api.common.security.AccessTokenService;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.flashcard.CreateFlashcardGenerationRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardAppleLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardDeviceLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardPhoneLoginRequest;
import org.hopeframework.biz.api.entity.input.flashcard.FlashcardProfileUpdateRequest;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.auth.UserSessionResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardCardResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardCreateOptionsResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardGenerationResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardHomeResponse;
import org.hopeframework.biz.api.entity.output.flashcard.FlashcardProfileResponse;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardAppUserMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardCardMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardFavoriteMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardFileMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardGenerationTaskMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardHomeBannerMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardHomeCardMapper;
import org.hopeframework.biz.api.model.flashcard.FlashcardAppUser;
import org.hopeframework.biz.api.model.flashcard.FlashcardCard;
import org.hopeframework.biz.api.model.flashcard.FlashcardFavorite;
import org.hopeframework.biz.api.model.flashcard.FlashcardFile;
import org.hopeframework.biz.api.model.flashcard.FlashcardGenerationTask;
import org.hopeframework.biz.api.model.flashcard.FlashcardHomeBanner;
import org.hopeframework.biz.api.model.flashcard.FlashcardHomeCard;
import org.hopeframework.biz.api.service.flashcard.FlashcardAppleIdentity;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAppleIdentityVerifier;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAppService;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAppImageService;
import org.hopeframework.biz.api.service.flashcard.IFlashcardPhoneCodeService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FlashcardAppServiceImpl implements IFlashcardAppService {
    private static final Set<String> STYLE_CODES = new HashSet<>(
            Arrays.asList("HOLO_RAINBOW", "HOT_GOLD", "SILVER_FOIL", "PEARL", "MATTE",
                    "CHINESE", "FANTASY", "ANIME", "REALISTIC", "ACG"));
    private static final Set<String> RARITY_CODES = new HashSet<>(
            Arrays.asList("N", "R", "SR", "SSR", "UR"));
    private static final Set<String> CARD_TYPES = new HashSet<>(
            Arrays.asList("ALL", "CREATED", "FAVORITED"));
    private static final DateTimeFormatter TASK_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String DEFAULT_AVATAR = "/static/flashcards/fox-spirit.png";
    private static final Pattern MAINLAND_PHONE = Pattern.compile("^1[3-9]\\d{9}$");

    private final FlashcardAppUserMapper userMapper;
    private final FlashcardCardMapper cardMapper;
    private final FlashcardFavoriteMapper favoriteMapper;
    private final FlashcardFileMapper fileMapper;
    private final FlashcardGenerationTaskMapper generationMapper;
    private final FlashcardHomeBannerMapper homeBannerMapper;
    private final FlashcardHomeCardMapper homeCardMapper;
    private final AccessTokenService accessTokenService;
    private final IFlashcardAppImageService flashcardAppImageService;
    private final IFlashcardPhoneCodeService phoneCodeService;
    private final IFlashcardAppleIdentityVerifier appleIdentityVerifier;

    public FlashcardAppServiceImpl(FlashcardAppUserMapper userMapper,
                                   FlashcardCardMapper cardMapper,
                                   FlashcardFavoriteMapper favoriteMapper,
                                   FlashcardFileMapper fileMapper,
                                   FlashcardGenerationTaskMapper generationMapper,
                                   FlashcardHomeBannerMapper homeBannerMapper,
                                   FlashcardHomeCardMapper homeCardMapper,
                                   AccessTokenService accessTokenService,
                                   IFlashcardAppImageService flashcardAppImageService,
                                   IFlashcardPhoneCodeService phoneCodeService,
                                   IFlashcardAppleIdentityVerifier appleIdentityVerifier) {
        this.userMapper = userMapper;
        this.cardMapper = cardMapper;
        this.favoriteMapper = favoriteMapper;
        this.fileMapper = fileMapper;
        this.generationMapper = generationMapper;
        this.homeBannerMapper = homeBannerMapper;
        this.homeCardMapper = homeCardMapper;
        this.accessTokenService = accessTokenService;
        this.flashcardAppImageService = flashcardAppImageService;
        this.phoneCodeService = phoneCodeService;
        this.appleIdentityVerifier = appleIdentityVerifier;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse deviceLogin(FlashcardDeviceLoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getDeviceId())) {
            throw badRequest("设备标识不能为空");
        }
        String deviceId = request.getDeviceId().trim();
        if (deviceId.length() > 128) throw badRequest("设备标识长度不能超过128位");
        String clientType = normalizeClientType(request.getClientType());
        Date now = new Date();
        FlashcardAppUser user = userMapper.selectOne(new LambdaQueryWrapper<FlashcardAppUser>()
                .eq(FlashcardAppUser::getDeviceId, deviceId));
        if (user == null) {
            user = new FlashcardAppUser();
            user.setDeviceId(deviceId);
            user.setClientType(clientType);
            user.setNickname(resolveNickname(request));
            user.setAvatarUrl(StringUtils.hasText(request.getAvatarUrl()) ? request.getAvatarUrl().trim() : DEFAULT_AVATAR);
            user.setBio("收集美好想象，创造专属宇宙。");
            user.setStatus("ACTIVE");
            user.setLastLoginAt(now);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            user.setDeleted(0);
            userMapper.insert(user);
        } else {
            if (!"ACTIVE".equals(user.getStatus())) {
                throw new HopeException(HttpStatus.FORBIDDEN.value(), "闪卡APP账号已停用");
            }
            user.setClientType(clientType);
            if (StringUtils.hasText(request.getNickname())) user.setNickname(request.getNickname().trim());
            if (StringUtils.hasText(request.getAvatarUrl())) user.setAvatarUrl(request.getAvatarUrl().trim());
            user.setLastLoginAt(now);
            user.setUpdatedAt(now);
            userMapper.updateById(user);
        }

        return createSession(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse phoneLogin(FlashcardPhoneLoginRequest request) {
        if (request == null) throw badRequest("登录参数不能为空");
        String phone = normalizePhone(request.getPhone());
        phoneCodeService.verifyAndConsume(phone, request.getCode());

        Date now = new Date();
        FlashcardAppUser user = userMapper.selectOne(new LambdaQueryWrapper<FlashcardAppUser>()
                .eq(FlashcardAppUser::getPhone, phone));
        if (user == null) {
            user = new FlashcardAppUser();
            user.setDeviceId(null);
            user.setBio("收集美好想象，创造专属宇宙。");
            user.setStatus("ACTIVE");
            user.setCreatedAt(now);
            user.setDeleted(0);
            user.setPhone(phone);
        }
        ensureActive(user);
        user.setClientType(normalizeClientType(request.getClientType()));
        user.setNickname(resolveNickname(request.getNickname(), user.getNickname(), "星月旅人"));
        user.setAvatarUrl(resolveAvatar(request.getAvatarUrl(), user.getAvatarUrl()));
        user.setLastLoginAt(now);
        user.setUpdatedAt(now);
        try {
            if (user.getId() == null) userMapper.insert(user); else userMapper.updateById(user);
        } catch (DuplicateKeyException ex) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "手机号已注册其他闪卡账号", ex);
        }
        return createSession(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse appleLogin(FlashcardAppleLoginRequest request) {
        if (request == null) throw badRequest("Apple登录参数不能为空");
        FlashcardAppleIdentity identity = appleIdentityVerifier.verify(request.getIdentityToken(), request.getNonce());
        FlashcardAppUser user = userMapper.selectOne(new LambdaQueryWrapper<FlashcardAppUser>()
                .eq(FlashcardAppUser::getAppleSubject, identity.getSubject()));
        Date now = new Date();
        if (user == null) {
            user = new FlashcardAppUser();
            user.setDeviceId(null);
            user.setBio("收集美好想象，创造专属宇宙。");
            user.setStatus("ACTIVE");
            user.setCreatedAt(now);
            user.setDeleted(0);
            user.setAppleSubject(identity.getSubject());
        }
        ensureActive(user);
        if (StringUtils.hasText(identity.getEmail())) user.setAppleEmail(identity.getEmail().trim());
        user.setClientType(normalizeClientType(request.getClientType()));
        user.setNickname(resolveNickname(request.getNickname(), user.getNickname(), "Apple用户"));
        user.setAvatarUrl(resolveAvatar(request.getAvatarUrl(), user.getAvatarUrl()));
        user.setLastLoginAt(now);
        user.setUpdatedAt(now);
        try {
            if (user.getId() == null) userMapper.insert(user); else userMapper.updateById(user);
        } catch (DuplicateKeyException ex) {
            throw new HopeException(HttpStatus.CONFLICT.value(), "Apple账号已注册其他闪卡账号", ex);
        }
        return createSession(user);
    }

    @Override
    public FlashcardHomeResponse home(String keyword, String categoryCode, long page, long pageSize) {
        long safePage = normalizePage(page);
        long safePageSize = normalizePageSize(pageSize);
        boolean configuredHome = !StringUtils.hasText(keyword)
                && (!StringUtils.hasText(categoryCode) || "RECOMMENDED".equalsIgnoreCase(categoryCode.trim()));
        if (configuredHome) {
            FlashcardHomeResponse response = new FlashcardHomeResponse();
            response.setCategories(categories());
            response.setBanners(banners());
            response.setCards(configuredHomeCards(safePage, safePageSize));
            return response;
        }
        LambdaQueryWrapper<FlashcardCard> wrapper = new LambdaQueryWrapper<FlashcardCard>()
                .eq(FlashcardCard::getStatus, "ACTIVE")
                .eq(FlashcardCard::getVisibility, "PUBLIC")
                .orderByDesc(FlashcardCard::getFavoriteCount)
                .orderByDesc(FlashcardCard::getId);
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(query -> query.like(FlashcardCard::getCardName, value)
                    .or().like(FlashcardCard::getSeriesName, value)
                    .or().like(FlashcardCard::getDescription, value));
        }
        if (StringUtils.hasText(categoryCode) && !"RECOMMENDED".equalsIgnoreCase(categoryCode.trim())) {
            wrapper.eq(FlashcardCard::getCategoryCode, categoryCode.trim().toUpperCase(Locale.ROOT));
        }
        Page<FlashcardCard> result = cardMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);
        List<FlashcardCardResponse> rows = toCardResponses(result.getRecords());

        FlashcardHomeResponse response = new FlashcardHomeResponse();
        response.setCategories(categories());
        response.setBanners(banners());
        response.setCards(new PageResult<>(rows, result.getTotal(), result.getSize(), result.getCurrent()));
        return response;
    }

    @Override
    public FlashcardCardResponse cardDetail(Long cardId) {
        return toCardResponse(requireCard(cardId), currentFlashcardUserId(), true);
    }

    @Override
    public FlashcardCreateOptionsResponse createOptions() {
        FlashcardCreateOptionsResponse response = new FlashcardCreateOptionsResponse();
        response.setStyles(Arrays.asList(
                new FlashcardCreateOptionsResponse.StyleOption("HOLO_RAINBOW", "镭射彩虹", null),
                new FlashcardCreateOptionsResponse.StyleOption("HOT_GOLD", "烫金", null),
                new FlashcardCreateOptionsResponse.StyleOption("SILVER_FOIL", "银箔", null),
                new FlashcardCreateOptionsResponse.StyleOption("PEARL", "珠光", null),
                new FlashcardCreateOptionsResponse.StyleOption("MATTE", "原画无光", null)
        ));
        List<FlashcardCreateOptionsResponse.CodeNameOption> rarities = new ArrayList<>();
        for (String rarity : Arrays.asList("N", "R", "SR", "SSR", "UR")) {
            rarities.add(new FlashcardCreateOptionsResponse.CodeNameOption(rarity, rarity));
        }
        response.setRarities(rarities);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlashcardGenerationResponse createGeneration(CreateFlashcardGenerationRequest request) {
        AuthPrincipal principal = requireFlashcardUser();
        validateGeneration(request, principal.getUserId());
        Date now = new Date();
        FlashcardGenerationTask task = new FlashcardGenerationTask();
        task.setTaskNo(newTaskNo());
        task.setUserId(principal.getUserId());
        task.setSubjectFileId(request.getSubjectFileId());
        // 复用现有两个可选文件列，避免正式库为素材别名增加冗余字段。
        task.setForegroundFileId(request.getLineartFileId() != null
                ? request.getLineartFileId() : request.getForegroundFileId());
        task.setEffectFileId(request.getBackFileId() != null
                ? request.getBackFileId() : request.getEffectFileId());
        task.setPromptText(trimToNull(request.getPrompt()));
        task.setStyleCode(request.getStyleCode().trim().toUpperCase(Locale.ROOT));
        task.setRarityCode(request.getRarityCode().trim().toUpperCase(Locale.ROOT));
        task.setStatus("PENDING");
        task.setProgress(0);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setDeleted(0);
        generationMapper.insert(task);
        completeGeneration(task, principal.getUserId(), now);
        return toGenerationResponse(task);
    }

    @Override
    public FlashcardGenerationResponse generation(String taskId) {
        AuthPrincipal principal = requireFlashcardUser();
        if (!StringUtils.hasText(taskId)) throw badRequest("生成任务编号不能为空");
        FlashcardGenerationTask task = generationMapper.selectOne(
                new LambdaQueryWrapper<FlashcardGenerationTask>()
                        .eq(FlashcardGenerationTask::getTaskNo, taskId.trim())
                        .eq(FlashcardGenerationTask::getUserId, principal.getUserId()));
        if (task == null) throw new HopeException(HttpStatus.NOT_FOUND.value(), "闪卡生成任务不存在");
        return toGenerationResponse(task);
    }

    @Override
    public FlashcardProfileResponse profile() {
        AuthPrincipal principal = requireFlashcardUser();
        FlashcardAppUser user = userMapper.selectById(principal.getUserId());
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "闪卡APP用户不存在");
        }
        Integer collected = favoriteMapper.selectCount(new LambdaQueryWrapper<FlashcardFavorite>()
                .eq(FlashcardFavorite::getUserId, user.getId()));
        Integer created = cardMapper.selectCount(new LambdaQueryWrapper<FlashcardCard>()
                .eq(FlashcardCard::getCreatorUserId, user.getId())
                .eq(FlashcardCard::getStatus, "ACTIVE"));
        FlashcardProfileResponse response = new FlashcardProfileResponse();
        response.setUserId(user.getId());
        response.setDisplayId(String.format(Locale.ROOT, "%06d", user.getId()));
        response.setName(user.getNickname());
        response.setBio(user.getBio());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setCollectedCount(collected == null ? 0L : collected.longValue());
        response.setCreatedCount(created == null ? 0L : created.longValue());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlashcardProfileResponse updateProfile(FlashcardProfileUpdateRequest request) {
        if (request == null || !StringUtils.hasText(request.getNickname())) {
            throw badRequest("昵称不能为空");
        }
        String nickname = request.getNickname().trim();
        if (nickname.length() > 100) throw badRequest("昵称长度不能超过100位");

        AuthPrincipal principal = requireFlashcardUser();
        FlashcardAppUser user = userMapper.selectById(principal.getUserId());
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "闪卡APP用户不存在");
        }
        user.setNickname(nickname);
        if (request.getAvatarFileId() != null) {
            FlashcardFile avatar = fileMapper.selectById(request.getAvatarFileId());
            if (avatar == null || !principal.getUserId().equals(avatar.getUserId())
                    || !"PROFILE_AVATAR".equals(avatar.getLayerType())) {
                throw badRequest("头像文件无效");
            }
            user.setAvatarUrl(avatar.getFileUrl());
        }
        user.setUpdatedAt(new Date());
        userMapper.updateById(user);
        return profile();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PageResult<FlashcardCardResponse> myCards(String typeText, String rarity, long page, long pageSize) {
        AuthPrincipal principal = requireFlashcardUser();
        completeLegacyPendingGenerations(principal.getUserId());
        completeLegacyLayerComposites(principal.getUserId());
        String type = StringUtils.hasText(typeText) ? typeText.trim().toUpperCase(Locale.ROOT) : "ALL";
        if (!CARD_TYPES.contains(type)) throw badRequest("type只能是ALL、CREATED或FAVORITED");
        List<Long> favoriteIds = favoriteCardIds(principal.getUserId());
        if ("FAVORITED".equals(type) && favoriteIds.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, normalizePageSize(pageSize), normalizePage(page));
        }

        LambdaQueryWrapper<FlashcardCard> wrapper = new LambdaQueryWrapper<FlashcardCard>()
                .eq(FlashcardCard::getStatus, "ACTIVE")
                .orderByDesc(FlashcardCard::getCreatedAt)
                .orderByDesc(FlashcardCard::getId);
        if ("CREATED".equals(type)) {
            wrapper.eq(FlashcardCard::getCreatorUserId, principal.getUserId());
        } else if ("FAVORITED".equals(type)) {
            wrapper.in(FlashcardCard::getId, favoriteIds);
        } else if (favoriteIds.isEmpty()) {
            wrapper.eq(FlashcardCard::getCreatorUserId, principal.getUserId());
        } else {
            wrapper.and(query -> query.eq(FlashcardCard::getCreatorUserId, principal.getUserId())
                    .or().in(FlashcardCard::getId, favoriteIds));
        }
        if (StringUtils.hasText(rarity)) {
            String rarityCode = rarity.trim().toUpperCase(Locale.ROOT);
            if (!RARITY_CODES.contains(rarityCode)) throw badRequest("稀有度参数不正确");
            wrapper.eq(FlashcardCard::getRarity, rarityCode);
        }
        Page<FlashcardCard> result = cardMapper.selectPage(
                new Page<>(normalizePage(page), normalizePageSize(pageSize)), wrapper);
        return new PageResult<>(toCardResponses(result.getRecords()), result.getTotal(),
                result.getSize(), result.getCurrent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlashcardCardResponse favorite(Long cardId) {
        AuthPrincipal principal = requireFlashcardUser();
        FlashcardCard card = requireCard(cardId);
        Integer exists = favoriteMapper.selectCount(new LambdaQueryWrapper<FlashcardFavorite>()
                .eq(FlashcardFavorite::getUserId, principal.getUserId())
                .eq(FlashcardFavorite::getCardId, cardId));
        if (exists == null || exists == 0) {
            FlashcardFavorite favorite = new FlashcardFavorite();
            favorite.setUserId(principal.getUserId());
            favorite.setCardId(cardId);
            favorite.setCreatedAt(new Date());
            try {
                favoriteMapper.insert(favorite);
                cardMapper.update(null, new UpdateWrapper<FlashcardCard>()
                        .eq("id", cardId)
                        .setSql("favorite_count = favorite_count + 1"));
            } catch (DuplicateKeyException ignored) {
                // 并发重复收藏由唯一索引保证幂等。
            }
        }
        return toCardResponse(requireCard(cardId), principal.getUserId(), true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlashcardCardResponse unfavorite(Long cardId) {
        AuthPrincipal principal = requireFlashcardUser();
        requireCard(cardId);
        int deleted = favoriteMapper.delete(new QueryWrapper<FlashcardFavorite>()
                .eq("user_id", principal.getUserId()).eq("card_id", cardId));
        if (deleted > 0) {
            cardMapper.update(null, new UpdateWrapper<FlashcardCard>()
                    .eq("id", cardId)
                    .setSql("favorite_count = GREATEST(favorite_count - 1, 0)"));
        }
        return toCardResponse(requireCard(cardId), principal.getUserId(), true);
    }

    private void validateGeneration(CreateFlashcardGenerationRequest request, Long userId) {
        if (request == null || request.getSubjectFileId() == null) throw badRequest("主体图片不能为空");
        if (!StringUtils.hasText(request.getStyleCode())
                || !STYLE_CODES.contains(request.getStyleCode().trim().toUpperCase(Locale.ROOT))) {
            throw badRequest("卡面质感参数不正确");
        }
        if (!StringUtils.hasText(request.getRarityCode())
                || !RARITY_CODES.contains(request.getRarityCode().trim().toUpperCase(Locale.ROOT))) {
            throw badRequest("稀有度参数不正确");
        }
        if (request.getPrompt() != null && request.getPrompt().trim().length() > 500) {
            throw badRequest("闪卡描述不能超过500字");
        }
        requireOwnedFile(request.getSubjectFileId(), userId, "SUBJECT");
        if (request.getLineartFileId() != null) {
            requireOwnedFile(request.getLineartFileId(), userId, "LINEART");
        } else if (request.getForegroundFileId() != null) {
            requireOwnedFile(request.getForegroundFileId(), userId, "FOREGROUND");
        }
        if (request.getBackFileId() != null) {
            requireOwnedFile(request.getBackFileId(), userId, "BACK");
        } else if (request.getEffectFileId() != null) {
            requireOwnedFile(request.getEffectFileId(), userId, "EFFECT");
        }
    }

    private FlashcardFile requireOwnedFile(Long fileId, Long userId, String layerType) {
        FlashcardFile file = fileMapper.selectOne(new LambdaQueryWrapper<FlashcardFile>()
                .eq(FlashcardFile::getId, fileId)
                .eq(FlashcardFile::getUserId, userId)
                .eq(FlashcardFile::getLayerType, layerType));
        if (file == null) throw badRequest(layerType + "图片不存在或不属于当前用户");
        return file;
    }

    /**
     * 当前版本尚未接入外部AI绘图Worker，先使用已压缩并上传到OSS的主体图生成一张可用卡片，
     * 保证“提交生成 -> 查看详情 -> 我的卡册”业务链路完整。后续接入AI时替换本方法的卡面产出即可。
     */
    private void completeGeneration(FlashcardGenerationTask task, Long userId, Date now) {
        if (task.getCardId() != null) {
            task.setStatus("SUCCEEDED");
            task.setProgress(100);
            task.setErrorMessage(null);
            task.setUpdatedAt(now);
            generationMapper.updateById(task);
            return;
        }

        FlashcardFile subjectFile = fileMapper.selectOne(new LambdaQueryWrapper<FlashcardFile>()
                .eq(FlashcardFile::getId, task.getSubjectFileId())
                .eq(FlashcardFile::getUserId, userId)
                .eq(FlashcardFile::getLayerType, "SUBJECT"));
        if (subjectFile == null) {
            task.setStatus("FAILED");
            task.setProgress(0);
            task.setErrorMessage("主体图片不存在或不属于当前用户");
            task.setUpdatedAt(now);
            generationMapper.updateById(task);
            return;
        }

        FlashcardCard card = new FlashcardCard();
        card.setCreatorUserId(userId);
        card.setCategoryCode(task.getStyleCode());
        card.setCardName("我的" + task.getRarityCode() + "闪卡");
        card.setRarity(task.getRarityCode());
        card.setSeriesName(styleName(task.getStyleCode()) + "创作");
        card.setDescription(StringUtils.hasText(task.getPromptText())
                ? task.getPromptText() : "由闪卡APP生成的专属卡片。");
        FlashcardFile lineartFile = findOwnedFile(task.getForegroundFileId(), userId, "LINEART");
        FlashcardFile backFile = findOwnedFile(task.getEffectFileId(), userId, "BACK");
        FlashcardFile foregroundFile = lineartFile == null
                ? findOwnedFile(task.getForegroundFileId(), userId, "FOREGROUND") : null;
        FlashcardFile effectFile = backFile == null
                ? findOwnedFile(task.getEffectFileId(), userId, "EFFECT") : null;
        // 线稿是运行时景深层，卡背是独立面，均不烘焙进正面主图。
        card.setImageUrl(lineartFile != null || backFile != null
                ? subjectFile.getFileUrl()
                : (foregroundFile == null && effectFile == null
                ? subjectFile.getFileUrl()
                : flashcardAppImageService.composeAndUpload(subjectFile, foregroundFile, effectFile)));
        card.setEditionNo(taskEditionNo(task.getTaskNo()));
        card.setFavoriteCount(0L);
        card.setVisibility("PRIVATE");
        card.setStatus("ACTIVE");
        card.setCreatedAt(now);
        card.setUpdatedAt(now);
        card.setDeleted(0);
        cardMapper.insert(card);

        task.setCardId(card.getId());
        task.setStatus("SUCCEEDED");
        task.setProgress(100);
        task.setErrorMessage(null);
        task.setUpdatedAt(now);
        generationMapper.updateById(task);
    }

    /** 兼容修复升级前已经落库但没有产出卡片的PENDING任务。 */
    private void completeLegacyPendingGenerations(Long userId) {
        List<FlashcardGenerationTask> pendingTasks = generationMapper.selectList(
                new LambdaQueryWrapper<FlashcardGenerationTask>()
                        .eq(FlashcardGenerationTask::getUserId, userId)
                        .eq(FlashcardGenerationTask::getStatus, "PENDING")
                        .orderByAsc(FlashcardGenerationTask::getId)
                        .last("FOR UPDATE"));
        if (pendingTasks == null || pendingTasks.isEmpty()) return;
        for (FlashcardGenerationTask task : pendingTasks) {
            completeGeneration(task, userId, new Date());
        }
    }

    /** 把旧版本中已经成功但仍只使用主体图的多图卡片重新合成为真正的多图卡面。 */
    private void completeLegacyLayerComposites(Long userId) {
        List<FlashcardGenerationTask> layeredTasks = generationMapper.selectList(
                new LambdaQueryWrapper<FlashcardGenerationTask>()
                        .eq(FlashcardGenerationTask::getUserId, userId)
                        .eq(FlashcardGenerationTask::getStatus, "SUCCEEDED")
                        .isNotNull(FlashcardGenerationTask::getCardId)
                        .and(query -> query.isNotNull(FlashcardGenerationTask::getForegroundFileId)
                                .or().isNotNull(FlashcardGenerationTask::getEffectFileId))
                        .orderByAsc(FlashcardGenerationTask::getId)
                        .last("FOR UPDATE"));
        if (layeredTasks == null || layeredTasks.isEmpty()) return;
        for (FlashcardGenerationTask task : layeredTasks) {
            FlashcardCard card = cardMapper.selectById(task.getCardId());
            FlashcardFile subjectFile = findOwnedFile(task.getSubjectFileId(), userId, "SUBJECT");
            if (card == null || subjectFile == null
                    || (StringUtils.hasText(card.getImageUrl())
                    && card.getImageUrl().contains("/flashcard-app/generated/v3/"))) continue;
            FlashcardFile foregroundFile = findOwnedFile(
                    task.getForegroundFileId(), userId, "FOREGROUND");
            FlashcardFile effectFile = findOwnedFile(task.getEffectFileId(), userId, "EFFECT");
            if (foregroundFile == null && effectFile == null) continue;
            card.setImageUrl(flashcardAppImageService.composeAndUpload(
                    subjectFile, foregroundFile, effectFile));
            card.setUpdatedAt(new Date());
            cardMapper.updateById(card);
        }
    }

    private FlashcardFile findOwnedFile(Long fileId, Long userId, String layerType) {
        if (fileId == null) return null;
        return fileMapper.selectOne(new LambdaQueryWrapper<FlashcardFile>()
                .eq(FlashcardFile::getId, fileId)
                .eq(FlashcardFile::getUserId, userId)
                .eq(FlashcardFile::getLayerType, layerType));
    }

    private String styleName(String styleCode) {
        if ("HOLO_RAINBOW".equals(styleCode)) return "镭射彩虹";
        if ("HOT_GOLD".equals(styleCode)) return "烫金";
        if ("SILVER_FOIL".equals(styleCode)) return "银箔";
        if ("PEARL".equals(styleCode)) return "珠光";
        if ("MATTE".equals(styleCode)) return "原画无光";
        if ("CHINESE".equals(styleCode)) return "国风";
        if ("FANTASY".equals(styleCode)) return "幻想";
        if ("ANIME".equals(styleCode)) return "动漫";
        if ("REALISTIC".equals(styleCode)) return "写实";
        if ("ACG".equals(styleCode)) return "二次元";
        return "专属";
    }

    private String taskEditionNo(String taskNo) {
        if (!StringUtils.hasText(taskNo)) return null;
        int start = Math.max(0, taskNo.length() - 8);
        return taskNo.substring(start);
    }

    private FlashcardCard requireCard(Long cardId) {
        if (cardId == null) throw badRequest("卡牌ID不能为空");
        FlashcardCard card = cardMapper.selectOne(new LambdaQueryWrapper<FlashcardCard>()
                .eq(FlashcardCard::getId, cardId)
                .eq(FlashcardCard::getStatus, "ACTIVE"));
        if (card == null) throw new HopeException(HttpStatus.NOT_FOUND.value(), "闪卡不存在或已下架");
        return card;
    }

    private List<FlashcardCardResponse> toCardResponses(List<FlashcardCard> cards) {
        if (cards == null || cards.isEmpty()) return Collections.emptyList();
        Long userId = currentFlashcardUserId();
        return cards.stream().map(card -> toCardResponse(card, userId, false)).collect(Collectors.toList());
    }

    private FlashcardCardResponse toCardResponse(FlashcardCard card, Long userId) {
        return toCardResponse(card, userId, false);
    }

    private FlashcardCardResponse toCardResponse(FlashcardCard card, Long userId, boolean includeLayers) {
        FlashcardCardResponse response = new FlashcardCardResponse();
        response.setId(card.getId());
        response.setName(card.getCardName());
        response.setRarity(card.getRarity());
        response.setSeries(card.getSeriesName());
        response.setCategoryCode(card.getCategoryCode());
        response.setDescription(card.getDescription());
        response.setImageUrl(card.getImageUrl());
        response.setFinishCode(normalizeFinishCode(card.getCategoryCode()));
        response.setSubjectImageUrl(card.getImageUrl());
        if (includeLayers) applyGenerationLayers(response, card.getId());
        response.setEditionNo(card.getEditionNo());
        response.setFavoriteCount(card.getFavoriteCount() == null ? 0L : card.getFavoriteCount());
        response.setFavorited(userId != null && isFavorited(userId, card.getId()));
        response.setCreatorId(card.getCreatorUserId());
        return response;
    }

    private void applyGenerationLayers(FlashcardCardResponse response, Long cardId) {
        FlashcardGenerationTask task = generationMapper.selectOne(
                new LambdaQueryWrapper<FlashcardGenerationTask>()
                        .eq(FlashcardGenerationTask::getCardId, cardId)
                        .eq(FlashcardGenerationTask::getStatus, "SUCCEEDED")
                        .orderByDesc(FlashcardGenerationTask::getId)
                        .last("LIMIT 1"));
        if (task == null) return;
        response.setFinishCode(normalizeFinishCode(task.getStyleCode()));
        FlashcardFile subject = task.getSubjectFileId() == null
                ? null : fileMapper.selectById(task.getSubjectFileId());
        FlashcardFile foreground = task.getForegroundFileId() == null
                ? null : fileMapper.selectById(task.getForegroundFileId());
        FlashcardFile effect = task.getEffectFileId() == null
                ? null : fileMapper.selectById(task.getEffectFileId());
        if (subject != null) response.setSubjectImageUrl(subject.getFileUrl());
        if (foreground != null) {
            if ("LINEART".equals(foreground.getLayerType())) {
                response.setLineartImageUrl(foreground.getFileUrl());
            } else {
                response.setForegroundImageUrl(foreground.getFileUrl());
                response.setForegroundTransparent("PNG".equalsIgnoreCase(foreground.getImageFormat()));
            }
        }
        if (effect != null) {
            if ("BACK".equals(effect.getLayerType())) {
                response.setBackImageUrl(effect.getFileUrl());
            } else {
                response.setEffectImageUrl(effect.getFileUrl());
                response.setEffectTransparent("PNG".equalsIgnoreCase(effect.getImageFormat()));
            }
        }
    }

    private String normalizeFinishCode(String styleCode) {
        if ("HOLO_RAINBOW".equals(styleCode) || "HOT_GOLD".equals(styleCode)
                || "SILVER_FOIL".equals(styleCode) || "PEARL".equals(styleCode)
                || "MATTE".equals(styleCode)) return styleCode;
        return "HOLO_RAINBOW";
    }

    private boolean isFavorited(Long userId, Long cardId) {
        Integer count = favoriteMapper.selectCount(new LambdaQueryWrapper<FlashcardFavorite>()
                .eq(FlashcardFavorite::getUserId, userId)
                .eq(FlashcardFavorite::getCardId, cardId));
        return count != null && count > 0;
    }

    private List<Long> favoriteCardIds(Long userId) {
        return favoriteMapper.selectList(new LambdaQueryWrapper<FlashcardFavorite>()
                        .eq(FlashcardFavorite::getUserId, userId)
                        .orderByDesc(FlashcardFavorite::getId))
                .stream().map(FlashcardFavorite::getCardId).collect(Collectors.toList());
    }

    private List<FlashcardHomeResponse.Category> categories() {
        return Arrays.asList(
                new FlashcardHomeResponse.Category("RECOMMENDED", "推荐"),
                new FlashcardHomeResponse.Category("CHINESE", "国风"),
                new FlashcardHomeResponse.Category("MYTH", "神话"),
                new FlashcardHomeResponse.Category("ANIME", "动漫"),
                new FlashcardHomeResponse.Category("GAME", "游戏"),
                new FlashcardHomeResponse.Category("ORIGINAL", "原创")
        );
    }

    private List<FlashcardHomeResponse.Banner> banners() {
        List<FlashcardHomeBanner> banners = homeBannerMapper.selectList(new LambdaQueryWrapper<FlashcardHomeBanner>()
                .eq(FlashcardHomeBanner::getStatus, "ACTIVE")
                .orderByAsc(FlashcardHomeBanner::getSortOrder)
                .orderByAsc(FlashcardHomeBanner::getId));
        List<FlashcardHomeResponse.Banner> result = new ArrayList<>();
        for (FlashcardHomeBanner source : banners) {
            FlashcardHomeResponse.Banner banner = new FlashcardHomeResponse.Banner();
            banner.setId(source.getId());
            banner.setEyebrow(source.getEyebrow());
            banner.setTitle(source.getTitle());
            banner.setSubtitle(source.getSubtitle());
            banner.setImageUrl(source.getImageUrl());
            banner.setCardId(source.getCardId());
            result.add(banner);
        }
        return result;
    }

    private PageResult<FlashcardCardResponse> configuredHomeCards(long page, long pageSize) {
        List<FlashcardHomeCard> configured = homeCardMapper.selectList(new LambdaQueryWrapper<FlashcardHomeCard>()
                .eq(FlashcardHomeCard::getStatus, "ACTIVE")
                .orderByAsc(FlashcardHomeCard::getSortOrder)
                .orderByAsc(FlashcardHomeCard::getId));
        long total = configured.size();
        int from = (int) Math.min(total, (page - 1) * pageSize);
        int to = (int) Math.min(total, from + pageSize);
        List<Long> ids = configured.subList(from, to).stream()
                .map(FlashcardHomeCard::getCardId).collect(Collectors.toList());
        if (ids.isEmpty()) return new PageResult<>(Collections.emptyList(), total, pageSize, page);
        Map<Long, FlashcardCard> cardMap = cardMapper.selectList(new LambdaQueryWrapper<FlashcardCard>()
                        .in(FlashcardCard::getId, ids)
                        .eq(FlashcardCard::getStatus, "ACTIVE"))
                .stream().collect(Collectors.toMap(FlashcardCard::getId, item -> item,
                        (first, ignored) -> first, LinkedHashMap::new));
        List<FlashcardCard> cards = ids.stream().map(cardMap::get)
                .filter(java.util.Objects::nonNull).collect(Collectors.toList());
        return new PageResult<>(toCardResponses(cards), total, pageSize, page);
    }

    private FlashcardGenerationResponse toGenerationResponse(FlashcardGenerationTask task) {
        FlashcardGenerationResponse response = new FlashcardGenerationResponse();
        response.setTaskId(task.getTaskNo());
        response.setStatus(task.getStatus());
        response.setProgress(task.getProgress());
        response.setCardId(task.getCardId());
        response.setErrorMessage(task.getErrorMessage());
        return response;
    }

    private TokenResponse createSession(FlashcardAppUser user) {
        AuthPrincipal principal = new AuthPrincipal(
                user.getId(), user.getId(), TenantContext.requireTenantId(), "FLASHCARD_APP");
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessTokenService.createPermanent(principal));
        response.setExpiresIn(0);
        response.setUser(toSession(user));
        return response;
    }

    private String normalizePhone(String phone) {
        String value = StringUtils.hasText(phone) ? phone.trim() : "";
        if (!MAINLAND_PHONE.matcher(value).matches()) throw badRequest("请输入正确的11位手机号");
        return value;
    }

    private void ensureActive(FlashcardAppUser user) {
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "闪卡APP账号已停用");
        }
    }

    private String resolveNickname(String requested, String existing, String fallback) {
        if (StringUtils.hasText(requested)) {
            String value = requested.trim();
            if (value.length() > 100) throw badRequest("昵称长度不能超过100位");
            return value;
        }
        return StringUtils.hasText(existing) ? existing : fallback;
    }

    private String resolveAvatar(String requested, String existing) {
        if (StringUtils.hasText(requested)) {
            String value = requested.trim();
            if (value.length() > 500) throw badRequest("头像地址长度不能超过500位");
            return value;
        }
        return StringUtils.hasText(existing) ? existing : DEFAULT_AVATAR;
    }

    private UserSessionResponse toSession(FlashcardAppUser user) {
        UserSessionResponse response = new UserSessionResponse();
        response.setUserId(user.getId());
        response.setMemberId(user.getId());
        response.setTenantId(TenantContext.requireTenantId());
        response.setUsername("flashcard_user_" + user.getId());
        response.setNickname(user.getNickname());
        response.setDisplayName(user.getNickname());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setBio(user.getBio());
        response.setMemberStatus(user.getStatus());
        return response;
    }

    private AuthPrincipal requireFlashcardUser() {
        AuthPrincipal principal = AuthContext.require();
        if (!principal.isFlashcardUser()) {
            throw new HopeException(HttpStatus.FORBIDDEN.value(), "当前登录信息不属于闪卡APP");
        }
        return principal;
    }

    private Long currentFlashcardUserId() {
        AuthPrincipal principal = AuthContext.current();
        return principal != null && principal.isFlashcardUser() ? principal.getUserId() : null;
    }

    private String normalizeClientType(String clientType) {
        if (!StringUtils.hasText(clientType)) return "APP";
        String value = clientType.trim().toUpperCase(Locale.ROOT);
        if (value.length() > 30) throw badRequest("客户端类型长度不能超过30位");
        return value;
    }

    private String resolveNickname(FlashcardDeviceLoginRequest request) {
        if (StringUtils.hasText(request.getNickname())) {
            String nickname = request.getNickname().trim();
            if (nickname.length() > 100) throw badRequest("昵称长度不能超过100位");
            return nickname;
        }
        return "星月旅人";
    }

    private long normalizePage(long page) {
        return page <= 0 ? 1 : page;
    }

    private long normalizePageSize(long pageSize) {
        if (pageSize <= 0) return 20;
        return Math.min(pageSize, 50);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String newTaskNo() {
        return "FCG" + LocalDateTime.now().format(TASK_TIME)
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private HopeException badRequest(String message) {
        return new HopeException(HttpStatus.BAD_REQUEST.value(), message);
    }
}
