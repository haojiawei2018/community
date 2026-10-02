package org.hopeframework.biz.api.service.impl.booking;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingProductRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingCategoryRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingStoreRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingStoreSlotOverrideRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.RescheduleBookingAdminAppointmentRequest;
import org.hopeframework.biz.api.entity.output.booking.admin.BookingAdminDataResponse;
import org.hopeframework.biz.api.entity.output.booking.admin.BookingStoreSlotOverrideResponse;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.mapper.booking.BookingAppointmentMapper;
import org.hopeframework.biz.api.mapper.booking.BookingCategoryMapper;
import org.hopeframework.biz.api.mapper.booking.BookingProductMapper;
import org.hopeframework.biz.api.mapper.booking.BookingStoreMapper;
import org.hopeframework.biz.api.mapper.booking.BookingStoreSlotOverrideMapper;
import org.hopeframework.biz.api.mapper.booking.BookingUserMapper;
import org.hopeframework.biz.api.model.booking.BookingAppointment;
import org.hopeframework.biz.api.model.booking.BookingCategory;
import org.hopeframework.biz.api.model.booking.BookingProduct;
import org.hopeframework.biz.api.model.booking.BookingStore;
import org.hopeframework.biz.api.model.booking.BookingStoreSlotOverride;
import org.hopeframework.biz.api.model.booking.BookingUser;
import org.hopeframework.biz.api.service.booking.IBookingAdminService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingAdminServiceImpl implements IBookingAdminService {

    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SLOT_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int MAX_BOOKING_DAYS = 90;
    private static final String SLOT_SOURCE_DEFAULT = "DEFAULT";
    private static final String SLOT_SOURCE_OVERRIDE = "OVERRIDE";

    private final BookingStoreMapper storeMapper;
    private final BookingCategoryMapper categoryMapper;
    private final BookingProductMapper productMapper;
    private final BookingUserMapper userMapper;
    private final BookingAppointmentMapper appointmentMapper;
    private final BookingStoreSlotOverrideMapper slotOverrideMapper;

    public BookingAdminServiceImpl(BookingStoreMapper storeMapper,
                                   BookingCategoryMapper categoryMapper,
                                   BookingProductMapper productMapper,
                                   BookingUserMapper userMapper,
                                   BookingAppointmentMapper appointmentMapper,
                                   BookingStoreSlotOverrideMapper slotOverrideMapper) {
        this.storeMapper = storeMapper;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.userMapper = userMapper;
        this.appointmentMapper = appointmentMapper;
        this.slotOverrideMapper = slotOverrideMapper;
    }

    @Override
    public BookingAdminDataResponse data() {
        List<BookingStore> stores = storeMapper.selectList(new LambdaQueryWrapper<BookingStore>()
                .orderByAsc(BookingStore::getSortOrder).orderByAsc(BookingStore::getId));
        List<BookingCategory> categories = categoryMapper.selectList(new LambdaQueryWrapper<BookingCategory>()
                .orderByAsc(BookingCategory::getSortOrder).orderByAsc(BookingCategory::getId));
        List<BookingProduct> products = productMapper.selectList(new LambdaQueryWrapper<BookingProduct>()
                .orderByAsc(BookingProduct::getSortOrder).orderByAsc(BookingProduct::getId));
        List<BookingUser> users = userMapper.selectList(new LambdaQueryWrapper<BookingUser>()
                .orderByDesc(BookingUser::getCreatedAt).orderByDesc(BookingUser::getId));
        List<BookingAppointment> appointments = appointmentMapper.selectList(new LambdaQueryWrapper<BookingAppointment>()
                .orderByDesc(BookingAppointment::getCreatedAt).orderByDesc(BookingAppointment::getId));

        Map<Long, BookingStore> storeMap = stores.stream().collect(Collectors.toMap(BookingStore::getId, item -> item));
        Map<Long, BookingCategory> categoryMap = categories.stream().collect(Collectors.toMap(BookingCategory::getId, item -> item));
        Map<Long, BookingProduct> productMap = products.stream().collect(Collectors.toMap(BookingProduct::getId, item -> item));
        Map<Long, BookingUser> userMap = users.stream().collect(Collectors.toMap(BookingUser::getId, item -> item));

        BookingAdminDataResponse response = new BookingAdminDataResponse();
        response.setStores(stores.stream().map(this::toStoreItem).collect(Collectors.toList()));
        response.setCategories(categories.stream().map(this::toCategoryItem).collect(Collectors.toList()));
        response.setProducts(products.stream().map(item -> toProductItem(item, storeMap, categoryMap)).collect(Collectors.toList()));
        response.setUsers(users.stream().map(this::toUserItem).collect(Collectors.toList()));
        response.setAppointments(appointments.stream()
                .map(item -> toAppointmentItem(item, storeMap, productMap, userMap))
                .collect(Collectors.toList()));

        BookingAdminDataResponse.Summary summary = new BookingAdminDataResponse.Summary();
        summary.setStoreCount(stores.size());
        summary.setProductCount(products.size());
        summary.setUserCount(users.size());
        summary.setOrderCount(appointments.size());
        summary.setPendingCount((int) appointments.stream().filter(item -> "PENDING_PAYMENT".equals(item.getStatus())).count());
        summary.setConfirmedCount((int) appointments.stream().filter(item -> "CONFIRMED".equals(item.getStatus())).count());
        summary.setTodayCount((int) appointments.stream().filter(item -> item.getAppointmentDate() != null
                && LocalDate.now().toString().equals(item.getAppointmentDate().toString())).count());
        response.setSummary(summary);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAdminDataResponse.StoreItem updateStore(Long storeId, UpdateBookingStoreRequest request) {
        BookingStore store = storeMapper.selectById(storeId);
        if (store == null) throw notFound("店铺不存在");
        if (request == null || !StringUtils.hasText(request.getStoreName())) throw badRequest("店铺名称不能为空");
        validateStatus(request.getStatus());
        if (request.getAdvanceMinutes() != null && request.getAdvanceMinutes() < 0) throw badRequest("提前预约分钟数不能小于0");
        List<UpdateBookingStoreRequest.SlotItem> slotConfig = validateSlotConfig(request.getSlotConfig());

        store.setStoreName(request.getStoreName().trim());
        store.setLogoUrl(trimToNull(request.getLogoUrl()));
        store.setAddress(trimToNull(request.getAddress()));
        store.setLatitude(request.getLatitude());
        store.setLongitude(request.getLongitude());
        store.setBusinessHours(trimToNull(request.getBusinessHours()));
        store.setServicePhone(trimToNull(request.getServicePhone()));
        store.setAdvanceMinutes(request.getAdvanceMinutes() == null ? 120 : request.getAdvanceMinutes());
        store.setSlotConfig(JSON.toJSONString(slotConfig));
        store.setStatus(request.getStatus());
        store.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        store.setUpdatedAt(new Date());
        storeMapper.updateById(store);
        return toStoreItem(store);
    }

    @Override
    public BookingStoreSlotOverrideResponse getSlotOverride(Long storeId, String dateText) {
        LocalDate date = parseAndValidateDate(dateText);
        BookingStore store = requireStore(storeId);
        return buildSlotOverrideResponse(store, date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingStoreSlotOverrideResponse updateSlotOverride(
            Long storeId, String dateText, UpdateBookingStoreSlotOverrideRequest request) {
        LocalDate date = parseAndValidateDate(dateText);
        if (request == null) throw badRequest("请求内容不能为空");
        List<UpdateBookingStoreRequest.SlotItem> slotConfig = validateSlotConfig(toStoreSlotItems(request.getSlotConfig()));

        BookingStore store = storeMapper.lockById(TenantContext.requireTenantId(), storeId);
        if (store == null) throw notFound("店铺不存在");
        BookingStoreSlotOverride override = slotOverrideMapper.findByStoreAndDate(
                TenantContext.requireTenantId(), storeId, java.sql.Date.valueOf(date));
        if (override == null) {
            override = new BookingStoreSlotOverride();
            override.setStoreId(storeId);
            override.setSlotDate(java.sql.Date.valueOf(date));
            override.setSlotConfig(JSON.toJSONString(slotConfig));
            override.setCreatedAt(new Date());
            override.setUpdatedAt(new Date());
            slotOverrideMapper.insert(override);
        } else {
            override.setSlotConfig(JSON.toJSONString(slotConfig));
            override.setUpdatedAt(new Date());
            slotOverrideMapper.updateById(override);
        }
        return buildSlotOverrideResponse(store, date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingStoreSlotOverrideResponse deleteSlotOverride(Long storeId, String dateText) {
        LocalDate date = parseAndValidateDate(dateText);
        BookingStore store = storeMapper.lockById(TenantContext.requireTenantId(), storeId);
        if (store == null) throw notFound("店铺不存在");
        BookingStoreSlotOverride override = slotOverrideMapper.findByStoreAndDate(
                TenantContext.requireTenantId(), storeId, java.sql.Date.valueOf(date));
        if (override != null) {
            slotOverrideMapper.deleteById(override.getId());
        }
        return buildSlotOverrideResponse(store, date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAdminDataResponse.CategoryItem updateCategory(Long categoryId, UpdateBookingCategoryRequest request) {
        BookingCategory category = categoryMapper.selectById(categoryId);
        if (category == null) throw notFound("分类不存在");
        if (request == null || !StringUtils.hasText(request.getCategoryName())) throw badRequest("分类名称不能为空");
        category.setCategoryName(request.getCategoryName().trim());
        category.setIconUrl(trimToNull(request.getIconUrl()));
        category.setUpdatedAt(new Date());
        categoryMapper.updateById(category);
        return toCategoryItem(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAdminDataResponse.CategoryItem createCategory(UpdateBookingCategoryRequest request) {
        if (request == null || !StringUtils.hasText(request.getCategoryName())) throw badRequest("分类名称不能为空");
        Date now = new Date();
        BookingCategory category = new BookingCategory();
        category.setCategoryCode("CAT_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        category.setCategoryName(request.getCategoryName().trim());
        category.setIconUrl(trimToNull(request.getIconUrl()));
        category.setIconIndex(0);
        category.setStatus(ACTIVE);
        category.setSortOrder(nextCategorySortOrder());
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        category.setDeleted(0);
        categoryMapper.insert(category);
        return toCategoryItem(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAdminDataResponse.ProductItem updateProduct(Long productId, UpdateBookingProductRequest request) {
        BookingProduct product = productMapper.selectById(productId);
        if (product == null) throw notFound("商品不存在");
        ProductReferences references = validateProductRequest(request);
        applyProductRequest(product, request);
        product.setUpdatedAt(new Date());
        productMapper.updateById(product);
        return toProductItem(product, references.stores, references.categories);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAdminDataResponse.ProductItem createProduct(UpdateBookingProductRequest request) {
        ProductReferences references = validateProductRequest(request);
        Date now = new Date();
        BookingProduct product = new BookingProduct();
        applyProductRequest(product, request);
        product.setCoverIndex(0);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setDeleted(0);
        productMapper.insert(product);
        return toProductItem(product, references.stores, references.categories);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAdminDataResponse.AppointmentItem cancelConfirmedAppointment(Long appointmentId) {
        if (appointmentId == null) throw badRequest("预约订单不能为空");
        BookingAppointment appointment = appointmentMapper.lockByIdForAdmin(
                TenantContext.requireTenantId(), appointmentId);
        if (appointment == null) throw notFound("预约订单不存在");
        if (!"CONFIRMED".equals(appointment.getStatus())) {
            throw badRequest("只有已预约订单可以由后台取消");
        }

        Date now = new Date();
        appointment.setStatus("CANCELLED");
        appointment.setCancelledAt(now);
        appointment.setUpdatedAt(now);
        appointmentMapper.updateById(appointment);

        BookingStore store = storeMapper.selectById(appointment.getStoreId());
        BookingProduct product = productMapper.selectById(appointment.getProductId());
        BookingUser user = userMapper.selectById(appointment.getUserId());
        Map<Long, BookingStore> stores = store == null ? Collections.emptyMap()
                : Collections.singletonMap(store.getId(), store);
        Map<Long, BookingProduct> products = product == null ? Collections.emptyMap()
                : Collections.singletonMap(product.getId(), product);
        Map<Long, BookingUser> users = user == null ? Collections.emptyMap()
                : Collections.singletonMap(user.getId(), user);
        return toAppointmentItem(appointment, stores, products, users);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAdminDataResponse.AppointmentItem rescheduleConfirmedAppointment(
            Long appointmentId, RescheduleBookingAdminAppointmentRequest request) {
        if (appointmentId == null || request == null) throw badRequest("订单和新档期不能为空");
        BookingAppointment appointment = appointmentMapper.lockByIdForAdmin(
                TenantContext.requireTenantId(), appointmentId);
        if (appointment == null) throw notFound("预约订单不存在");
        if (!"CONFIRMED".equals(appointment.getStatus()) || !"PAID".equals(appointment.getPaymentStatus())) {
            throw badRequest("只有已支付且已预约的订单可以由后台改期");
        }

        LocalDate newDate = parseAndValidateDate(request.getAppointmentDate());
        LocalTime newTime;
        try {
            newTime = LocalTime.parse(request.getSlotTime(), SLOT_TIME_FORMAT);
        } catch (RuntimeException exception) {
            throw badRequest("预约时间格式应为HH:mm");
        }
        if (!LocalDateTime.of(newDate, newTime).isAfter(LocalDateTime.now())) {
            throw badRequest("新档期必须晚于当前时间");
        }
        if (newDate.equals(appointment.getAppointmentDate().toLocalDate())
                && newTime.equals(appointment.getSlotTime().toLocalTime())) {
            throw badRequest("请选择与原预约不同的档期");
        }

        BookingStore store = storeMapper.lockActiveStore(TenantContext.requireTenantId(), appointment.getStoreId());
        if (store == null) throw badRequest("店铺不存在或已停用");
        BookingStoreSlotOverride override = slotOverrideMapper.findByStoreAndDate(
                TenantContext.requireTenantId(), store.getId(), java.sql.Date.valueOf(newDate));
        List<BookingAdminDataResponse.SlotItem> slots = parseSlotConfig(
                override == null ? store.getSlotConfig() : override.getSlotConfig());
        BookingAdminDataResponse.SlotItem selected = slots.stream()
                .filter(slot -> newTime.format(SLOT_TIME_FORMAT).equals(slot.getTime()))
                .findFirst().orElseThrow(() -> badRequest("所选档期不存在或不可预约"));
        int reserved = safeInt(appointmentMapper.sumReservedPeople(
                TenantContext.requireTenantId(), store.getId(), java.sql.Date.valueOf(newDate), java.sql.Time.valueOf(newTime)));
        if (reserved + safeInt(appointment.getPeopleCount()) > safeInt(selected.getCapacity())) {
            throw badRequest("该档期剩余名额不足");
        }

        appointment.setAppointmentDate(java.sql.Date.valueOf(newDate));
        appointment.setSlotTime(java.sql.Time.valueOf(newTime));
        appointment.setUpdatedAt(new Date());
        appointmentMapper.updateById(appointment);

        BookingProduct product = productMapper.selectById(appointment.getProductId());
        BookingUser user = userMapper.selectById(appointment.getUserId());
        Map<Long, BookingStore> stores = Collections.singletonMap(store.getId(), store);
        Map<Long, BookingProduct> products = product == null ? Collections.emptyMap()
                : Collections.singletonMap(product.getId(), product);
        Map<Long, BookingUser> users = user == null ? Collections.emptyMap()
                : Collections.singletonMap(user.getId(), user);
        return toAppointmentItem(appointment, stores, products, users);
    }

    private int nextCategorySortOrder() {
        return categoryMapper.selectList(new LambdaQueryWrapper<BookingCategory>()).stream()
                .map(BookingCategory::getSortOrder)
                .filter(value -> value != null)
                .max(Integer::compareTo)
                .orElse(0) + 10;
    }

    private ProductReferences validateProductRequest(UpdateBookingProductRequest request) {
        if (request == null || !StringUtils.hasText(request.getProductName())) throw badRequest("商品名称不能为空");
        if (request.getStoreId() == null || request.getCategoryId() == null) throw badRequest("所属店铺和分类不能为空");
        BookingStore store = storeMapper.selectById(request.getStoreId());
        if (store == null) throw badRequest("所选店铺不存在");
        BookingCategory category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) throw badRequest("所选分类不存在");
        validateMoney(request.getSalePrice(), "商品价格");
        validateMoney(request.getDepositAmount(), "预约定金");
        validateStatus(request.getStatus());
        Map<Long, BookingStore> stores = new HashMap<>();
        stores.put(store.getId(), store);
        Map<Long, BookingCategory> categories = new HashMap<>();
        categories.put(category.getId(), category);
        return new ProductReferences(stores, categories);
    }

    private void applyProductRequest(BookingProduct product, UpdateBookingProductRequest request) {
        product.setStoreId(request.getStoreId());
        product.setCategoryId(request.getCategoryId());
        product.setProductName(request.getProductName().trim());
        product.setCoverUrl(trimToNull(request.getCoverUrl()));
        product.setGalleryUrls(JSON.toJSONString(request.getGalleryUrls() == null ? Collections.emptyList() : request.getGalleryUrls()));
        product.setDescription(trimToNull(request.getDescription()));
        product.setNoticeContent(trimToNull(request.getNoticeContent()));
        product.setUnavailableContent(trimToNull(request.getUnavailableContent()));
        product.setSalePrice(request.getSalePrice());
        product.setDepositAmount(request.getDepositAmount());
        product.setFeatured(request.getFeatured() != null && request.getFeatured() == 1 ? 1 : 0);
        product.setStatus(request.getStatus());
        product.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    }

    private BookingStoreSlotOverrideResponse buildSlotOverrideResponse(BookingStore store, LocalDate date) {
        BookingStoreSlotOverride override = slotOverrideMapper.findByStoreAndDate(
                TenantContext.requireTenantId(), store.getId(), java.sql.Date.valueOf(date));
        String source = override == null ? SLOT_SOURCE_DEFAULT : SLOT_SOURCE_OVERRIDE;
        List<BookingAdminDataResponse.SlotItem> configs = parseSlotConfig(
                override == null ? store.getSlotConfig() : override.getSlotConfig());

        BookingStoreSlotOverrideResponse response = new BookingStoreSlotOverrideResponse();
        response.setStoreId(store.getId());
        response.setDate(date.format(DATE_FORMAT));
        response.setSource(source);
        for (BookingAdminDataResponse.SlotItem config : configs) {
            LocalTime time = LocalTime.parse(config.getTime(), SLOT_TIME_FORMAT);
            int booked = safeInt(appointmentMapper.sumReservedPeople(
                    TenantContext.requireTenantId(), store.getId(), java.sql.Date.valueOf(date), java.sql.Time.valueOf(time)));
            BookingStoreSlotOverrideResponse.SlotItem slot = new BookingStoreSlotOverrideResponse.SlotItem();
            slot.setTime(config.getTime());
            slot.setCapacity(config.getCapacity());
            slot.setBookedPeople(booked);
            slot.setRemainingPeople(Math.max(safeInt(config.getCapacity()) - booked, 0));
            response.getSlots().add(slot);
        }
        return response;
    }

    private BookingStore requireStore(Long storeId) {
        BookingStore store = storeMapper.selectById(storeId);
        if (store == null) throw notFound("店铺不存在");
        return store;
    }

    private LocalDate parseAndValidateDate(String dateText) {
        if (!StringUtils.hasText(dateText)) throw badRequest("日期不能为空");
        try {
            LocalDate date = LocalDate.parse(dateText.trim(), DATE_FORMAT);
            LocalDate today = LocalDate.now();
            if (date.isBefore(today) || date.isAfter(today.plusDays(MAX_BOOKING_DAYS))) {
                throw badRequest("日期只能选择今天起90天内");
            }
            return date;
        } catch (DateTimeParseException exception) {
            throw badRequest("日期格式应为yyyy-MM-dd");
        }
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private BookingAdminDataResponse.StoreItem toStoreItem(BookingStore source) {
        BookingAdminDataResponse.StoreItem target = new BookingAdminDataResponse.StoreItem();
        target.setId(source.getId());
        target.setStoreName(source.getStoreName());
        target.setLogoUrl(source.getLogoUrl());
        target.setAddress(source.getAddress());
        target.setLatitude(source.getLatitude());
        target.setLongitude(source.getLongitude());
        target.setBusinessHours(source.getBusinessHours());
        target.setServicePhone(source.getServicePhone());
        target.setAdvanceMinutes(source.getAdvanceMinutes());
        target.setSlotConfig(parseSlotConfig(source.getSlotConfig()));
        target.setStatus(source.getStatus());
        target.setSortOrder(source.getSortOrder());
        return target;
    }

    private BookingAdminDataResponse.CategoryItem toCategoryItem(BookingCategory source) {
        BookingAdminDataResponse.CategoryItem target = new BookingAdminDataResponse.CategoryItem();
        target.setId(source.getId());
        target.setCategoryName(source.getCategoryName());
        target.setIconUrl(source.getIconUrl());
        target.setStatus(source.getStatus());
        target.setSortOrder(source.getSortOrder());
        return target;
    }

    private BookingAdminDataResponse.ProductItem toProductItem(BookingProduct source,
                                                                Map<Long, BookingStore> stores,
                                                                Map<Long, BookingCategory> categories) {
        BookingAdminDataResponse.ProductItem target = new BookingAdminDataResponse.ProductItem();
        target.setId(source.getId());
        target.setStoreId(source.getStoreId());
        target.setStoreName(stores.containsKey(source.getStoreId()) ? stores.get(source.getStoreId()).getStoreName() : "-");
        target.setCategoryId(source.getCategoryId());
        target.setCategoryName(categories.containsKey(source.getCategoryId()) ? categories.get(source.getCategoryId()).getCategoryName() : "-");
        target.setProductName(source.getProductName());
        target.setCoverUrl(source.getCoverUrl());
        target.setGalleryUrls(parseStringList(source.getGalleryUrls()));
        target.setDescription(source.getDescription());
        target.setNoticeContent(source.getNoticeContent());
        target.setUnavailableContent(source.getUnavailableContent());
        target.setSalePrice(source.getSalePrice());
        target.setDepositAmount(source.getDepositAmount());
        target.setFeatured(source.getFeatured());
        target.setStatus(source.getStatus());
        target.setSortOrder(source.getSortOrder());
        return target;
    }

    private BookingAdminDataResponse.UserItem toUserItem(BookingUser source) {
        BookingAdminDataResponse.UserItem target = new BookingAdminDataResponse.UserItem();
        target.setId(source.getId());
        target.setNickname(StringUtils.hasText(source.getNickname()) ? source.getNickname() : "微信用户");
        target.setAvatarUrl(source.getAvatarUrl());
        target.setStatus(source.getStatus());
        target.setLastLoginAt(formatDate(source.getLastLoginAt()));
        target.setCreatedAt(formatDate(source.getCreatedAt()));
        return target;
    }

    private BookingAdminDataResponse.AppointmentItem toAppointmentItem(BookingAppointment source,
                                                                       Map<Long, BookingStore> stores,
                                                                       Map<Long, BookingProduct> products,
                                                                       Map<Long, BookingUser> users) {
        BookingAdminDataResponse.AppointmentItem target = new BookingAdminDataResponse.AppointmentItem();
        BookingUser user = users.get(source.getUserId());
        target.setId(source.getId());
        target.setAppointmentNo(source.getAppointmentNo());
        target.setUserId(source.getUserId());
        target.setNickname(user == null || !StringUtils.hasText(user.getNickname()) ? "微信用户" : user.getNickname());
        target.setAvatarUrl(user == null ? null : user.getAvatarUrl());
        target.setStoreId(source.getStoreId());
        target.setStoreName(stores.containsKey(source.getStoreId()) ? stores.get(source.getStoreId()).getStoreName() : "-");
        target.setProductId(source.getProductId());
        target.setProductName(products.containsKey(source.getProductId()) ? products.get(source.getProductId()).getProductName() : "-");
        target.setAppointmentDate(source.getAppointmentDate() == null ? null : source.getAppointmentDate().toString());
        target.setSlotTime(source.getSlotTime() == null ? null : source.getSlotTime().toString().substring(0, 5));
        target.setPeopleCount(source.getPeopleCount());
        target.setSaleAmount(source.getSaleAmount());
        target.setDepositAmount(source.getDepositAmount());
        target.setRemark(source.getRemark());
        target.setStatus(source.getStatus());
        target.setPaymentStatus(source.getPaymentStatus());
        target.setTransactionId(source.getTransactionId());
        target.setExpiresAt(formatDate(source.getExpiresAt()));
        target.setPaidAt(formatDate(source.getPaidAt()));
        target.setCancelledAt(formatDate(source.getCancelledAt()));
        target.setCreatedAt(formatDate(source.getCreatedAt()));
        return target;
    }

    private List<String> parseStringList(String json) {
        if (!StringUtils.hasText(json)) return Collections.emptyList();
        try {
            return JSON.parseArray(json, String.class);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private List<UpdateBookingStoreRequest.SlotItem> toStoreSlotItems(
            List<UpdateBookingStoreSlotOverrideRequest.SlotItem> slots) {
        if (slots == null) return null;
        List<UpdateBookingStoreRequest.SlotItem> result = new ArrayList<>();
        for (UpdateBookingStoreSlotOverrideRequest.SlotItem slot : slots) {
            if (slot == null) {
                result.add(null);
                continue;
            }
            UpdateBookingStoreRequest.SlotItem item = new UpdateBookingStoreRequest.SlotItem();
            item.setTime(slot.getTime());
            item.setCapacity(slot.getCapacity());
            result.add(item);
        }
        return result;
    }

    private List<UpdateBookingStoreRequest.SlotItem> validateSlotConfig(List<UpdateBookingStoreRequest.SlotItem> slots) {
        if (slots == null || slots.isEmpty()) throw badRequest("请至少配置一个预约时间档");
        List<UpdateBookingStoreRequest.SlotItem> normalized = new ArrayList<>();
        Set<String> times = new HashSet<>();
        for (UpdateBookingStoreRequest.SlotItem source : slots) {
            if (source == null || !StringUtils.hasText(source.getTime())) throw badRequest("预约时间档不能为空");
            String time = source.getTime().trim();
            try {
                time = LocalTime.parse(time, SLOT_TIME_FORMAT).format(SLOT_TIME_FORMAT);
            } catch (DateTimeParseException exception) {
                throw badRequest("预约时间档格式应为HH:mm");
            }
            if (!times.add(time)) throw badRequest("预约时间档不能重复：" + time);
            if (source.getCapacity() == null || source.getCapacity() < 1 || source.getCapacity() > 99) {
                throw badRequest("每个时间档的预约人数应为1至99人");
            }
            UpdateBookingStoreRequest.SlotItem target = new UpdateBookingStoreRequest.SlotItem();
            target.setTime(time);
            target.setCapacity(source.getCapacity());
            normalized.add(target);
        }
        normalized.sort(Comparator.comparing(UpdateBookingStoreRequest.SlotItem::getTime));
        return normalized;
    }

    private List<BookingAdminDataResponse.SlotItem> parseSlotConfig(String json) {
        if (!StringUtils.hasText(json)) return Collections.emptyList();
        try {
            return JSON.parseArray(json, BookingAdminDataResponse.SlotItem.class);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private String formatDate(Date date) {
        if (date == null) return null;
        synchronized (DATE_TIME_FORMAT) {
            return DATE_TIME_FORMAT.format(date);
        }
    }

    private void validateStatus(String status) {
        if (!ACTIVE.equals(status) && !INACTIVE.equals(status)) throw badRequest("状态只能为启用或停用");
    }

    private void validateMoney(BigDecimal amount, String name) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) throw badRequest(name + "不能小于0");
    }

    private String trimToNull(String text) {
        return StringUtils.hasText(text) ? text.trim() : null;
    }

    private HopeException badRequest(String message) {
        return new HopeException(HttpStatus.BAD_REQUEST.value(), message);
    }

    private HopeException notFound(String message) {
        return new HopeException(HttpStatus.NOT_FOUND.value(), message);
    }

    private static final class ProductReferences {
        private final Map<Long, BookingStore> stores;
        private final Map<Long, BookingCategory> categories;

        private ProductReferences(Map<Long, BookingStore> stores, Map<Long, BookingCategory> categories) {
            this.stores = stores;
            this.categories = categories;
        }
    }
}





