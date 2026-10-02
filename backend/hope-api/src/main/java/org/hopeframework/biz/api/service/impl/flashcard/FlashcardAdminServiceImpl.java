package org.hopeframework.biz.api.service.impl.flashcard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.flashcard.admin.UpdateFlashcardHomeConfigRequest;
import org.hopeframework.biz.api.entity.output.flashcard.admin.FlashcardAdminDataResponse;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardAppUserMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardCardMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardHomeBannerMapper;
import org.hopeframework.biz.api.mapper.flashcard.FlashcardHomeCardMapper;
import org.hopeframework.biz.api.model.flashcard.FlashcardAppUser;
import org.hopeframework.biz.api.model.flashcard.FlashcardCard;
import org.hopeframework.biz.api.model.flashcard.FlashcardHomeBanner;
import org.hopeframework.biz.api.model.flashcard.FlashcardHomeCard;
import org.hopeframework.biz.api.service.flashcard.IFlashcardAdminService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FlashcardAdminServiceImpl implements IFlashcardAdminService {
    private static final int MAX_BANNERS = 5;
    private static final int MAX_HOME_CARDS = 50;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final FlashcardAppUserMapper userMapper;
    private final FlashcardCardMapper cardMapper;
    private final FlashcardHomeBannerMapper bannerMapper;
    private final FlashcardHomeCardMapper homeCardMapper;

    public FlashcardAdminServiceImpl(FlashcardAppUserMapper userMapper,
                                     FlashcardCardMapper cardMapper,
                                     FlashcardHomeBannerMapper bannerMapper,
                                     FlashcardHomeCardMapper homeCardMapper) {
        this.userMapper = userMapper;
        this.cardMapper = cardMapper;
        this.bannerMapper = bannerMapper;
        this.homeCardMapper = homeCardMapper;
    }

    @Override
    public FlashcardAdminDataResponse.Overview overview() {
        Date today = java.sql.Timestamp.valueOf(java.time.LocalDate.now().atStartOfDay());
        FlashcardAdminDataResponse.Overview result = new FlashcardAdminDataResponse.Overview();
        result.setUserCount(count(userMapper.selectCount(new LambdaQueryWrapper<FlashcardAppUser>())));
        result.setTodayUserCount(count(userMapper.selectCount(new LambdaQueryWrapper<FlashcardAppUser>()
                .ge(FlashcardAppUser::getCreatedAt, today))));
        result.setCardCount(count(cardMapper.selectCount(userCardQuery())));
        result.setTodayCardCount(count(cardMapper.selectCount(userCardQuery()
                .ge(FlashcardCard::getCreatedAt, today))));
        result.setBannerCount(count(bannerMapper.selectCount(new LambdaQueryWrapper<FlashcardHomeBanner>()
                .eq(FlashcardHomeBanner::getStatus, "ACTIVE"))));
        result.setHomeCardCount(count(homeCardMapper.selectCount(new LambdaQueryWrapper<FlashcardHomeCard>()
                .eq(FlashcardHomeCard::getStatus, "ACTIVE"))));
        return result;
    }

    @Override
    public PageResult<FlashcardAdminDataResponse.CardItem> cards(String keyword, long page, long pageSize) {
        long safePage = Math.max(1, page);
        long safeSize = Math.min(100, Math.max(1, pageSize));
        LambdaQueryWrapper<FlashcardCard> query = userCardQuery().orderByDesc(FlashcardCard::getId);
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            query.and(item -> item.like(FlashcardCard::getCardName, value)
                    .or().like(FlashcardCard::getSeriesName, value)
                    .or().like(FlashcardCard::getDescription, value));
        }
        Page<FlashcardCard> result = cardMapper.selectPage(new Page<>(safePage, safeSize), query);
        Map<Long, FlashcardAppUser> users = usersByIds(result.getRecords().stream()
                .map(FlashcardCard::getCreatorUserId).collect(Collectors.toList()));
        ConfigIndex config = configIndex();
        List<FlashcardAdminDataResponse.CardItem> rows = result.getRecords().stream()
                .map(card -> toCard(card, users.get(card.getCreatorUserId()), config))
                .collect(Collectors.toList());
        return new PageResult<>(rows, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    public PageResult<FlashcardAdminDataResponse.UserItem> users(String keyword, String status, long page, long pageSize) {
        long safePage = Math.max(1, page);
        long safeSize = Math.min(100, Math.max(1, pageSize));
        LambdaQueryWrapper<FlashcardAppUser> query = new LambdaQueryWrapper<FlashcardAppUser>()
                .orderByDesc(FlashcardAppUser::getId);
        if (StringUtils.hasText(status)) query.eq(FlashcardAppUser::getStatus, status.trim().toUpperCase());
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            query.and(item -> {
                item.like(FlashcardAppUser::getNickname, value)
                        .or().like(FlashcardAppUser::getPhone, value);
                if (value.matches("\\d+")) {
                    item.or().eq(FlashcardAppUser::getId, Long.valueOf(value));
                }
            });
        }
        Page<FlashcardAppUser> result = userMapper.selectPage(new Page<>(safePage, safeSize), query);
        List<FlashcardAdminDataResponse.UserItem> rows = result.getRecords().stream()
                .map(this::toUser).collect(Collectors.toList());
        return new PageResult<>(rows, result.getTotal(), result.getSize(), result.getCurrent());
    }

    @Override
    public FlashcardAdminDataResponse.HomeConfig homeConfig() {
        List<FlashcardHomeBanner> banners = bannerMapper.selectList(new LambdaQueryWrapper<FlashcardHomeBanner>()
                .eq(FlashcardHomeBanner::getStatus, "ACTIVE")
                .orderByAsc(FlashcardHomeBanner::getSortOrder).orderByAsc(FlashcardHomeBanner::getId));
        List<FlashcardHomeCard> homeCards = homeCardMapper.selectList(new LambdaQueryWrapper<FlashcardHomeCard>()
                .eq(FlashcardHomeCard::getStatus, "ACTIVE")
                .orderByAsc(FlashcardHomeCard::getSortOrder).orderByAsc(FlashcardHomeCard::getId));
        List<Long> cardIds = new ArrayList<>();
        banners.forEach(item -> cardIds.add(item.getCardId()));
        homeCards.forEach(item -> cardIds.add(item.getCardId()));
        Map<Long, FlashcardCard> cards = cardsByIds(cardIds);
        Map<Long, FlashcardAppUser> users = usersByIds(cards.values().stream()
                .map(FlashcardCard::getCreatorUserId).collect(Collectors.toList()));
        ConfigIndex index = configIndex(banners, homeCards);
        FlashcardAdminDataResponse.HomeConfig result = new FlashcardAdminDataResponse.HomeConfig();
        result.setBanners(banners.stream().filter(item -> cards.containsKey(item.getCardId())).map(item -> {
            FlashcardCard card = cards.get(item.getCardId());
            FlashcardAdminDataResponse.BannerItem row = new FlashcardAdminDataResponse.BannerItem();
            row.setId(item.getId()); row.setCardId(item.getCardId()); row.setEyebrow(item.getEyebrow());
            row.setTitle(item.getTitle()); row.setSubtitle(item.getSubtitle());
            row.setImageUrl(StringUtils.hasText(item.getImageUrl()) ? item.getImageUrl() : card.getImageUrl());
            row.setSortOrder(item.getSortOrder()); row.setCard(toCard(card, users.get(card.getCreatorUserId()), index));
            return row;
        }).collect(Collectors.toList()));
        result.setCards(homeCards.stream().map(item -> cards.get(item.getCardId())).filter(java.util.Objects::nonNull)
                .map(card -> toCard(card, users.get(card.getCreatorUserId()), index)).collect(Collectors.toList()));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlashcardAdminDataResponse.HomeConfig updateHomeConfig(UpdateFlashcardHomeConfigRequest request) {
        if (request == null) throw badRequest("首页配置不能为空");
        List<UpdateFlashcardHomeConfigRequest.BannerItem> banners = request.getBanners() == null
                ? Collections.emptyList() : request.getBanners();
        List<Long> cardIds = request.getCardIds() == null ? Collections.emptyList() : request.getCardIds();
        if (banners.size() > MAX_BANNERS) throw badRequest("轮播图最多配置5张");
        if (cardIds.size() > MAX_HOME_CARDS) throw badRequest("首页闪卡最多配置50张");

        LinkedHashSet<Long> allIds = new LinkedHashSet<>();
        for (UpdateFlashcardHomeConfigRequest.BannerItem item : banners) {
            if (item == null || item.getCardId() == null) throw badRequest("轮播闪卡不能为空");
            if (!allIds.add(item.getCardId())) throw badRequest("同一张闪卡不能重复配置");
        }
        Set<Long> homeIds = new HashSet<>();
        for (Long cardId : cardIds) {
            if (cardId == null || !homeIds.add(cardId)) throw badRequest("首页闪卡不能重复配置");
            allIds.add(cardId);
        }
        Map<Long, FlashcardCard> cards = cardsByIds(new ArrayList<>(allIds));
        if (cards.size() != allIds.size()) throw badRequest("配置中包含不存在的用户闪卡");

        Long tenantId = TenantContext.requireTenantId();
        bannerMapper.deleteAllByTenant(tenantId);
        homeCardMapper.deleteAllByTenant(tenantId);
        Date now = new Date();
        for (int index = 0; index < banners.size(); index++) {
            UpdateFlashcardHomeConfigRequest.BannerItem source = banners.get(index);
            FlashcardCard card = cards.get(source.getCardId());
            FlashcardHomeBanner target = new FlashcardHomeBanner();
            target.setCardId(source.getCardId()); target.setEyebrow(trim(source.getEyebrow()));
            target.setTitle(StringUtils.hasText(source.getTitle()) ? source.getTitle().trim() : card.getRarity() + " " + card.getCardName());
            target.setSubtitle(StringUtils.hasText(source.getSubtitle()) ? source.getSubtitle().trim() : card.getDescription());
            target.setImageUrl(StringUtils.hasText(source.getImageUrl()) ? source.getImageUrl().trim() : card.getImageUrl());
            target.setSortOrder(index + 1); target.setStatus("ACTIVE"); target.setCreatedAt(now); target.setUpdatedAt(now); target.setDeleted(0);
            bannerMapper.insert(target);
        }
        for (int index = 0; index < cardIds.size(); index++) {
            FlashcardHomeCard target = new FlashcardHomeCard();
            target.setCardId(cardIds.get(index)); target.setSortOrder(index + 1); target.setStatus("ACTIVE");
            target.setCreatedAt(now); target.setUpdatedAt(now); target.setDeleted(0); homeCardMapper.insert(target);
        }
        return homeConfig();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FlashcardAdminDataResponse.UserItem updateUserStatus(Long userId, String status) {
        String normalized = StringUtils.hasText(status) ? status.trim().toUpperCase() : "";
        if (!"ACTIVE".equals(normalized) && !"DISABLED".equals(normalized)) throw badRequest("用户状态只能为ACTIVE或DISABLED");
        FlashcardAppUser user = userMapper.selectById(userId);
        if (user == null) throw new HopeException(HttpStatus.NOT_FOUND.value(), "用户不存在");
        user.setStatus(normalized); user.setUpdatedAt(new Date()); userMapper.updateById(user);
        return toUser(user);
    }

    private LambdaQueryWrapper<FlashcardCard> userCardQuery() {
        return new LambdaQueryWrapper<FlashcardCard>().isNotNull(FlashcardCard::getCreatorUserId);
    }

    private Map<Long, FlashcardCard> cardsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();
        return cardMapper.selectList(userCardQuery().in(FlashcardCard::getId, new LinkedHashSet<>(ids))).stream()
                .collect(Collectors.toMap(FlashcardCard::getId, item -> item));
    }

    private Map<Long, FlashcardAppUser> usersByIds(List<Long> ids) {
        Set<Long> values = ids == null ? Collections.emptySet() : ids.stream().filter(java.util.Objects::nonNull).collect(Collectors.toSet());
        if (values.isEmpty()) return Collections.emptyMap();
        return userMapper.selectBatchIds(values).stream().collect(Collectors.toMap(FlashcardAppUser::getId, item -> item));
    }

    private ConfigIndex configIndex() {
        return configIndex(
                bannerMapper.selectList(new LambdaQueryWrapper<FlashcardHomeBanner>().eq(FlashcardHomeBanner::getStatus, "ACTIVE")),
                homeCardMapper.selectList(new LambdaQueryWrapper<FlashcardHomeCard>().eq(FlashcardHomeCard::getStatus, "ACTIVE")));
    }

    private ConfigIndex configIndex(List<FlashcardHomeBanner> banners, List<FlashcardHomeCard> cards) {
        ConfigIndex result = new ConfigIndex();
        banners.forEach(item -> result.bannerOrders.put(item.getCardId(), item.getSortOrder()));
        cards.forEach(item -> result.homeOrders.put(item.getCardId(), item.getSortOrder()));
        return result;
    }

    private FlashcardAdminDataResponse.CardItem toCard(FlashcardCard source, FlashcardAppUser user, ConfigIndex config) {
        FlashcardAdminDataResponse.CardItem target = new FlashcardAdminDataResponse.CardItem();
        target.setId(source.getId()); target.setName(source.getCardName()); target.setRarity(source.getRarity());
        target.setSeries(source.getSeriesName()); target.setCategoryCode(source.getCategoryCode()); target.setDescription(source.getDescription());
        target.setImageUrl(source.getImageUrl()); target.setVisibility(source.getVisibility()); target.setStatus(source.getStatus());
        target.setFavoriteCount(source.getFavoriteCount()); target.setCreatorId(source.getCreatorUserId());
        target.setCreatorName(user == null ? "未知用户" : user.getNickname()); target.setCreatorAvatarUrl(user == null ? null : user.getAvatarUrl());
        target.setCreatedAt(format(source.getCreatedAt())); target.setInBanner(config.bannerOrders.containsKey(source.getId()));
        target.setBannerOrder(config.bannerOrders.get(source.getId())); target.setInHomeList(config.homeOrders.containsKey(source.getId()));
        target.setHomeOrder(config.homeOrders.get(source.getId())); return target;
    }

    private FlashcardAdminDataResponse.UserItem toUser(FlashcardAppUser source) {
        FlashcardAdminDataResponse.UserItem target = new FlashcardAdminDataResponse.UserItem();
        target.setId(source.getId()); target.setNickname(source.getNickname()); target.setAvatarUrl(source.getAvatarUrl());
        target.setPhone(maskPhone(source.getPhone())); target.setClientType(source.getClientType()); target.setStatus(source.getStatus());
        target.setCardCount(count(cardMapper.selectCount(userCardQuery().eq(FlashcardCard::getCreatorUserId, source.getId()))));
        target.setLastLoginAt(format(source.getLastLoginAt())); target.setCreatedAt(format(source.getCreatedAt())); return target;
    }

    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    private String format(Date date) { if (date == null) return null; synchronized (DATE_FORMAT) { return DATE_FORMAT.format(date); } }
    private String trim(String value) { return StringUtils.hasText(value) ? value.trim() : null; }
    private long count(Number value) { return value == null ? 0L : value.longValue(); }
    private HopeException badRequest(String message) { return new HopeException(HttpStatus.BAD_REQUEST.value(), message); }

    private static final class ConfigIndex {
        private final Map<Long, Integer> bannerOrders = new HashMap<>();
        private final Map<Long, Integer> homeOrders = new HashMap<>();
    }
}
