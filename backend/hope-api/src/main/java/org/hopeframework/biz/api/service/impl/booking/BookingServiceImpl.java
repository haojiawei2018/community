package org.hopeframework.biz.api.service.impl.booking;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.booking.CreateBookingAppointmentRequest;
import org.hopeframework.biz.api.entity.input.booking.RescheduleBookingAppointmentRequest;
import org.hopeframework.biz.api.entity.output.booking.BookingAppointmentListResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingAppointmentResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingAvailabilityResponse;
import org.hopeframework.biz.api.entity.output.booking.BookingBootstrapResponse;
import org.hopeframework.biz.api.mapper.booking.BookingAppointmentMapper;
import org.hopeframework.biz.api.mapper.booking.BookingCategoryMapper;
import org.hopeframework.biz.api.mapper.booking.BookingProductMapper;
import org.hopeframework.biz.api.mapper.booking.BookingStoreMapper;
import org.hopeframework.biz.api.mapper.booking.BookingStoreSlotOverrideMapper;
import org.hopeframework.biz.api.model.booking.BookingAppointment;
import org.hopeframework.biz.api.model.booking.BookingCategory;
import org.hopeframework.biz.api.model.booking.BookingProduct;
import org.hopeframework.biz.api.model.booking.BookingStore;
import org.hopeframework.biz.api.model.booking.BookingStoreSlotOverride;
import org.hopeframework.biz.api.service.booking.IBookingService;
import org.hopeframework.biz.api.service.booking.IBookingWechatPayService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements IBookingService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int DEFAULT_ADVANCE_MINUTES = 120;
    private static final int DEFAULT_SLOT_CAPACITY = 2;
    private static final int MAX_BOOKING_DAYS = 90;
    private static final int PAYMENT_TIMEOUT_MINUTES = 5;
    private static final int RESCHEDULE_NOTICE_HOURS = 48;

    private final BookingStoreMapper storeMapper;
    private final BookingCategoryMapper categoryMapper;
    private final BookingProductMapper productMapper;
    private final BookingAppointmentMapper appointmentMapper;
    private final BookingStoreSlotOverrideMapper slotOverrideMapper;
    private final IBookingWechatPayService wechatPayService;

    public BookingServiceImpl(BookingStoreMapper storeMapper,
                              BookingCategoryMapper categoryMapper,
                              BookingProductMapper productMapper,
                              BookingAppointmentMapper appointmentMapper,
                              BookingStoreSlotOverrideMapper slotOverrideMapper,
                              IBookingWechatPayService wechatPayService) {
        this.storeMapper = storeMapper;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.appointmentMapper = appointmentMapper;
        this.slotOverrideMapper = slotOverrideMapper;
        this.wechatPayService = wechatPayService;
    }

    @Override
    public BookingBootstrapResponse bootstrap(Long storeId) {
        List<BookingStore> stores = storeMapper.selectList(new LambdaQueryWrapper<BookingStore>()
                .eq(BookingStore::getStatus, "ACTIVE")
                .orderByAsc(BookingStore::getSortOrder)
                .orderByAsc(BookingStore::getId));
        if (stores.isEmpty()) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "暂无可用店铺");
        }
        BookingStore currentStore = resolveStore(stores, storeId);

        List<BookingCategory> categories = categoryMapper.selectList(new LambdaQueryWrapper<BookingCategory>()
                .eq(BookingCategory::getStatus, "ACTIVE")
                .orderByAsc(BookingCategory::getSortOrder)
                .orderByAsc(BookingCategory::getId));
        List<BookingProduct> products = productMapper.selectList(new LambdaQueryWrapper<BookingProduct>()
                .eq(BookingProduct::getStoreId, currentStore.getId())
                .eq(BookingProduct::getStatus, "ACTIVE")
                .orderByAsc(BookingProduct::getSortOrder)
                .orderByAsc(BookingProduct::getId));

        BookingBootstrapResponse response = new BookingBootstrapResponse();
        response.setCurrentStoreId(currentStore.getId());
        response.setServicePhone(currentStore.getServicePhone());
        response.setStores(stores.stream().map(this::toStoreResponse).collect(Collectors.toList()));
        response.setCategories(categories.stream().map(this::toCategoryResponse).collect(Collectors.toList()));
        response.setProducts(products.stream().map(this::toProductResponse).collect(Collectors.toList()));
        return response;
    }

    @Override
    public BookingAvailabilityResponse availability(Long storeId, Long productId, String dateText) {
        if (storeId == null || productId == null) {
            throw badRequest("店铺和商品不能为空");
        }
        LocalDate date = parseAndValidateDate(dateText);
        BookingStore store = requireStore(storeId);
        BookingProduct product = requireProduct(storeId, productId);
        int advanceMinutes = positiveOrDefault(store.getAdvanceMinutes(), DEFAULT_ADVANCE_MINUTES);

        BookingAvailabilityResponse response = new BookingAvailabilityResponse();
        response.setStoreId(storeId);
        response.setProductId(productId);
        response.setDate(date.format(DATE_FORMAT));
        response.setSalePrice(product.getSalePrice());
        response.setDepositAmount(wechatPayService.resolvePaymentAmount(product.getDepositAmount()));
        response.setAdvanceMinutes(advanceMinutes);

        List<BookingAvailabilityResponse.Slot> slots = new ArrayList<>();
        for (SlotConfig config : effectiveSlotConfigs(store, date)) {
            int booked = safeInt(appointmentMapper.sumReservedPeople(
                    TenantContext.requireTenantId(), storeId, Date.valueOf(date), Time.valueOf(config.time)));
            int remaining = Math.max(config.capacity - booked, 0);
            LocalDateTime appointmentAt = LocalDateTime.of(date, config.time);
            boolean expired = appointmentAt.isBefore(LocalDateTime.now().plusMinutes(advanceMinutes));

            BookingAvailabilityResponse.Slot slot = new BookingAvailabilityResponse.Slot();
            slot.setTime(config.time.format(TIME_FORMAT));
            slot.setCapacityPeople(config.capacity);
            slot.setRemainingPeople(remaining);
            if (expired) {
                slot.setAvailable(false);
                slot.setStatus("EXPIRED");
                slot.setStatusText("已过期");
            } else if (remaining <= 0) {
                slot.setAvailable(false);
                slot.setStatus("FULL");
                slot.setStatusText("已约满");
            } else {
                slot.setAvailable(true);
                slot.setStatus("AVAILABLE");
                slot.setStatusText("可预约");
            }
            slots.add(slot);
        }
        response.setSlots(slots);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAppointmentResponse createAppointment(CreateBookingAppointmentRequest request, String clientIp) {
        validateCreateRequest(request);
        AuthPrincipal principal = AuthContext.require();
        LocalDate date = parseAndValidateDate(request.getAppointmentDate());
        LocalTime time = parseTime(request.getSlotTime());
        int peopleCount = request.getPeopleCount() == null ? 1 : request.getPeopleCount();
        if (peopleCount < 1) throw badRequest("预约人数不能少于1人");

        BookingStore store = storeMapper.lockActiveStore(TenantContext.requireTenantId(), request.getStoreId());
        if (store == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "店铺不存在或已停用");
        }
        BookingProduct product = requireProduct(request.getStoreId(), request.getProductId());
        SlotConfig slot = requireSlot(store, date, time);
        if (peopleCount > slot.capacity) {
            throw badRequest("该档期单次最多可预约" + slot.capacity + "人");
        }
        int advanceMinutes = positiveOrDefault(store.getAdvanceMinutes(), DEFAULT_ADVANCE_MINUTES);
        if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now().plusMinutes(advanceMinutes))) {
            throw badRequest("该档期已过预约时间");
        }
        int booked = safeInt(appointmentMapper.sumReservedPeople(
                TenantContext.requireTenantId(), store.getId(), Date.valueOf(date), Time.valueOf(time)));
        if (booked + peopleCount > slot.capacity) {
            throw badRequest("该档期剩余名额不足");
        }

        String remark = request.getRemark() == null ? null : request.getRemark().trim();
        if (remark != null && remark.length() > 500) {
            throw badRequest("预约备注不能超过500字");
        }
        java.util.Date now = new java.util.Date();
        BookingAppointment appointment = new BookingAppointment();
        appointment.setAppointmentNo(newAppointmentNo());
        appointment.setUserId(principal.getUserId());
        appointment.setMemberId(principal.getMemberId());
        appointment.setStoreId(store.getId());
        appointment.setProductId(product.getId());
        appointment.setAppointmentDate(Date.valueOf(date));
        appointment.setSlotTime(Time.valueOf(time));
        appointment.setPeopleCount(peopleCount);
        appointment.setSaleAmount(product.getSalePrice().multiply(BigDecimal.valueOf(peopleCount)));
        appointment.setDepositAmount(wechatPayService.resolvePaymentAmount(product.getDepositAmount(), peopleCount));
        appointment.setRemark(StringUtils.hasText(remark) ? remark : null);
        appointment.setStatus("PENDING_PAYMENT");
        appointment.setPaymentStatus("UNPAID");
        appointment.setExpiresAt(new java.util.Date(
                now.getTime() + PAYMENT_TIMEOUT_MINUTES * 60 * 1000L));
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);
        appointment.setDeleted(0);
        appointmentMapper.insert(appointment);
        // 短信通知由独立异步模块接入，预约创建不因短信服务异常失败。

        BookingAppointmentResponse response = toAppointmentResponse(appointment, store, product);
        response.setPaymentRequired(true);
        response.setPaymentParams(wechatPayService.createJsapiPayment(
                appointment, product, principal.getUserId(), clientIp));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAppointmentResponse continuePayment(Long appointmentId, String clientIp) {
        AuthPrincipal principal = AuthContext.require();
        BookingAppointment appointment = appointmentMapper.lockByIdForMember(
                TenantContext.requireTenantId(), appointmentId, principal.getMemberId());
        if (appointment == null) throw new HopeException(HttpStatus.NOT_FOUND.value(), "预约订单不存在");
        if (!"PENDING_PAYMENT".equals(appointment.getStatus())
                || !"UNPAID".equals(appointment.getPaymentStatus())) {
            throw badRequest("该订单当前不能继续支付");
        }
        if (appointment.getExpiresAt() == null || !appointment.getExpiresAt().after(new java.util.Date())) {
            appointment.setStatus("CANCELLED");
            appointment.setCancelledAt(new java.util.Date());
            appointment.setUpdatedAt(new java.util.Date());
            appointmentMapper.updateById(appointment);
            throw badRequest("订单支付已超时，请重新预约");
        }
        BookingStore store = requireStore(appointment.getStoreId());
        BookingProduct product = requireProduct(appointment.getStoreId(), appointment.getProductId());
        BookingAppointmentResponse response = toAppointmentResponse(appointment, store, product);
        response.setPaymentRequired(true);
        response.setPaymentParams(wechatPayService.createJsapiPayment(
                appointment, product, principal.getUserId(), clientIp));
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAppointmentResponse cancelPendingAppointment(Long appointmentId) {
        AuthPrincipal principal = AuthContext.require();
        BookingAppointment appointment = appointmentMapper.lockByIdForMember(
                TenantContext.requireTenantId(), appointmentId, principal.getMemberId());
        if (appointment == null) throw new HopeException(HttpStatus.NOT_FOUND.value(), "预约订单不存在");
        if (!"PENDING_PAYMENT".equals(appointment.getStatus())
                || !"UNPAID".equals(appointment.getPaymentStatus())) {
            throw badRequest("只有待支付订单可以取消");
        }
        java.util.Date now = new java.util.Date();
        appointment.setStatus("CANCELLED");
        appointment.setCancelledAt(now);
        appointment.setUpdatedAt(now);
        appointmentMapper.updateById(appointment);
        BookingStore store = storeMapper.selectById(appointment.getStoreId());
        BookingProduct product = productMapper.selectById(appointment.getProductId());
        return toAppointmentResponse(appointment, store, product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAppointmentListResponse myAppointments() {
        AuthPrincipal principal = AuthContext.require();
        appointmentMapper.expirePending(TenantContext.requireTenantId(), principal.getMemberId());
        List<BookingAppointment> appointments = appointmentMapper.selectList(
                new LambdaQueryWrapper<BookingAppointment>()
                        .eq(BookingAppointment::getMemberId, principal.getMemberId())
                        .orderByDesc(BookingAppointment::getCreatedAt)
                        .orderByDesc(BookingAppointment::getId));

        Map<Long, BookingStore> stores = loadStores(appointments);
        Map<Long, BookingProduct> products = loadProducts(appointments);
        BookingAppointmentListResponse response = new BookingAppointmentListResponse();
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("PENDING_PAYMENT", 0);
        counts.put("CONFIRMED", 0);
        counts.put("REFUND_PENDING", 0);
        counts.put("REFUNDED", 0);
        counts.put("CANCELLED", 0);
        for (BookingAppointment appointment : appointments) {
            counts.put(appointment.getStatus(), counts.getOrDefault(appointment.getStatus(), 0) + 1);
            response.getAppointments().add(toAppointmentResponse(
                    appointment, stores.get(appointment.getStoreId()), products.get(appointment.getProductId())));
        }
        response.setStatusCounts(counts);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingAppointmentResponse rescheduleAppointment(
            Long appointmentId, RescheduleBookingAppointmentRequest request) {
        if (appointmentId == null || request == null) throw badRequest("订单和新档期不能为空");
        AuthPrincipal principal = AuthContext.require();
        BookingAppointment appointment = appointmentMapper.lockByIdForMember(
                TenantContext.requireTenantId(), appointmentId, principal.getMemberId());
        if (appointment == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "预约订单不存在");
        }
        if (!"CONFIRMED".equals(appointment.getStatus()) || !"PAID".equals(appointment.getPaymentStatus())) {
            throw badRequest("只有已支付且已预约的订单可以改期");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime originalAt = LocalDateTime.of(
                appointment.getAppointmentDate().toLocalDate(), appointment.getSlotTime().toLocalTime());
        if (originalAt.isBefore(now.plusHours(RESCHEDULE_NOTICE_HOURS))) {
            throw badRequest("距离到店时间不足48小时，请联系客服处理");
        }

        LocalDate newDate = parseAndValidateDate(request.getAppointmentDate());
        LocalTime newTime = parseTime(request.getSlotTime());
        LocalDateTime newAppointmentAt = LocalDateTime.of(newDate, newTime);
        if (newAppointmentAt.isBefore(now.plusHours(RESCHEDULE_NOTICE_HOURS))) {
            throw badRequest("新档期需距离当前时间48小时以上");
        }
        if (newDate.equals(appointment.getAppointmentDate().toLocalDate())
                && newTime.equals(appointment.getSlotTime().toLocalTime())) {
            throw badRequest("请选择与原预约不同的档期");
        }

        BookingStore store = storeMapper.lockActiveStore(
                TenantContext.requireTenantId(), appointment.getStoreId());
        if (store == null) throw new HopeException(HttpStatus.NOT_FOUND.value(), "店铺不存在或已停用");
        SlotConfig slot = requireSlot(store, newDate, newTime);
        int booked = safeInt(appointmentMapper.sumReservedPeople(
                TenantContext.requireTenantId(), store.getId(), Date.valueOf(newDate), Time.valueOf(newTime)));
        if (booked + safeInt(appointment.getPeopleCount()) > slot.capacity) {
            throw badRequest("该档期剩余名额不足");
        }

        appointment.setAppointmentDate(Date.valueOf(newDate));
        appointment.setSlotTime(Time.valueOf(newTime));
        appointment.setUpdatedAt(new java.util.Date());
        appointmentMapper.updateById(appointment);
        BookingProduct product = productMapper.selectById(appointment.getProductId());
        return toAppointmentResponse(appointment, store, product);
    }

    private BookingStore resolveStore(List<BookingStore> stores, Long storeId) {
        if (storeId == null) {
            return stores.get(0);
        }
        return stores.stream().filter(store -> storeId.equals(store.getId())).findFirst()
                .orElseThrow(() -> new HopeException(HttpStatus.NOT_FOUND.value(), "店铺不存在或已停用"));
    }

    private BookingStore requireStore(Long storeId) {
        BookingStore store = storeMapper.selectById(storeId);
        if (store == null || !"ACTIVE".equals(store.getStatus())) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "店铺不存在或已停用");
        }
        return store;
    }

    private BookingProduct requireProduct(Long storeId, Long productId) {
        BookingProduct product = productMapper.selectOne(new LambdaQueryWrapper<BookingProduct>()
                .eq(BookingProduct::getId, productId)
                .eq(BookingProduct::getStoreId, storeId)
                .eq(BookingProduct::getStatus, "ACTIVE"));
        if (product == null) {
            throw new HopeException(HttpStatus.NOT_FOUND.value(), "商品不存在、已下架或不属于当前店铺");
        }
        if (product.getSalePrice() == null || product.getDepositAmount() == null) {
            throw new HopeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "商品价格配置不完整");
        }
        return product;
    }

    private BookingBootstrapResponse.Store toStoreResponse(BookingStore store) {
        BookingBootstrapResponse.Store response = new BookingBootstrapResponse.Store();
        response.setId(store.getId());
        response.setName(store.getStoreName());
        response.setLogoUrl(store.getLogoUrl());
        response.setAddress(store.getAddress());
        response.setLatitude(store.getLatitude());
        response.setLongitude(store.getLongitude());
        response.setBusinessHours(store.getBusinessHours());
        response.setServicePhone(store.getServicePhone());
        return response;
    }

    private BookingBootstrapResponse.Category toCategoryResponse(BookingCategory category) {
        BookingBootstrapResponse.Category response = new BookingBootstrapResponse.Category();
        response.setId(category.getId());
        response.setCode(category.getCategoryCode());
        response.setName(category.getCategoryName());
        response.setIconUrl(category.getIconUrl());
        response.setIconIndex(category.getIconIndex());
        response.setSortOrder(category.getSortOrder());
        return response;
    }

    private BookingBootstrapResponse.Product toProductResponse(BookingProduct product) {
        BookingBootstrapResponse.Product response = new BookingBootstrapResponse.Product();
        response.setId(product.getId());
        response.setStoreId(product.getStoreId());
        response.setCategoryId(product.getCategoryId());
        response.setName(product.getProductName());
        response.setCoverUrl(product.getCoverUrl());
        response.setCoverIndex(product.getCoverIndex());
        response.setGalleryUrls(parseGallery(product.getGalleryUrls()));
        response.setDescription(product.getDescription());
        response.setNoticeContent(product.getNoticeContent());
        response.setUnavailableContent(product.getUnavailableContent());
        response.setSalePrice(product.getSalePrice());
        response.setDepositAmount(product.getDepositAmount());
        response.setFeatured(Integer.valueOf(1).equals(product.getFeatured()));
        response.setSortOrder(product.getSortOrder());
        return response;
    }

    private BookingAppointmentResponse toAppointmentResponse(BookingAppointment appointment,
                                                             BookingStore store,
                                                             BookingProduct product) {
        BookingAppointmentResponse response = new BookingAppointmentResponse();
        response.setId(appointment.getId());
        response.setAppointmentNo(appointment.getAppointmentNo());
        response.setStoreId(appointment.getStoreId());
        response.setStoreName(store == null ? null : store.getStoreName());
        response.setProductId(appointment.getProductId());
        response.setProductName(product == null ? null : product.getProductName());
        response.setAppointmentDate(appointment.getAppointmentDate().toLocalDate().format(DATE_FORMAT));
        response.setSlotTime(appointment.getSlotTime().toLocalTime().format(TIME_FORMAT));
        response.setPeopleCount(appointment.getPeopleCount());
        response.setSaleAmount(appointment.getSaleAmount());
        response.setDepositAmount(appointment.getDepositAmount());
        response.setRemark(appointment.getRemark());
        response.setStatus(appointment.getStatus());
        response.setStatusText(statusText(appointment.getStatus()));
        response.setPaymentStatus(appointment.getPaymentStatus());
        response.setServicePhone(store == null ? null : store.getServicePhone());
        boolean confirmed = "CONFIRMED".equals(appointment.getStatus())
                && "PAID".equals(appointment.getPaymentStatus());
        LocalDateTime appointmentAt = LocalDateTime.of(
                appointment.getAppointmentDate().toLocalDate(), appointment.getSlotTime().toLocalTime());
        boolean outsideNoticeWindow = !appointmentAt.isBefore(
                LocalDateTime.now().plusHours(RESCHEDULE_NOTICE_HOURS));
        response.setCanReschedule(confirmed && outsideNoticeWindow);
        if (!confirmed) {
            response.setRescheduleReason("只有已预约订单可以改期");
        } else if (!outsideNoticeWindow) {
            response.setRescheduleReason("距离到店时间不足48小时，请联系客服处理");
        }
        if (appointment.getCreatedAt() != null) {
            response.setCreatedAt(appointment.getCreatedAt().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime().format(DATE_TIME_FORMAT));
        }
        if ("CANCELLED".equals(appointment.getStatus())) {
            response.setCancelReason("PAID".equals(appointment.getPaymentStatus())
                    ? "预约已取消" : "未支付，系统自动取消");
        }
        response.setPaymentRequired("PENDING_PAYMENT".equals(appointment.getStatus()));
        return response;
    }

    private List<String> parseGallery(String galleryUrls) {
        if (!StringUtils.hasText(galleryUrls)) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseArray(galleryUrls, String.class);
        } catch (RuntimeException exception) {
            return Collections.emptyList();
        }
    }

    private List<SlotConfig> parseSlotConfig(String slotConfig) {
        if (!StringUtils.hasText(slotConfig)) {
            return Collections.emptyList();
        }
        try {
            JSONArray array = JSON.parseArray(slotConfig);
            List<SlotConfig> result = new ArrayList<>();
            for (int index = 0; index < array.size(); index++) {
                JSONObject item = array.getJSONObject(index);
                LocalTime time = parseTime(item.getString("time"));
                int capacity = positiveOrDefault(item.getInteger("capacity"), DEFAULT_SLOT_CAPACITY);
                result.add(new SlotConfig(time, capacity));
            }
            result.sort((left, right) -> left.time.compareTo(right.time));
            return result;
        } catch (RuntimeException exception) {
            throw new HopeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "店铺档期配置格式错误");
        }
    }

    private List<SlotConfig> effectiveSlotConfigs(BookingStore store, LocalDate date) {
        BookingStoreSlotOverride override = slotOverrideMapper.findByStoreAndDate(
                TenantContext.requireTenantId(), store.getId(), Date.valueOf(date));
        return parseSlotConfig(override == null ? store.getSlotConfig() : override.getSlotConfig());
    }

    private SlotConfig requireSlot(BookingStore store, LocalDate date, LocalTime time) {
        return effectiveSlotConfigs(store, date).stream()
                .filter(slot -> slot.time.equals(time))
                .findFirst()
                .orElseThrow(() -> badRequest("所选档期不存在或不可预约"));
    }

    private LocalDate parseAndValidateDate(String dateText) {
        if (!StringUtils.hasText(dateText)) {
            throw badRequest("预约日期不能为空");
        }
        try {
            LocalDate date = LocalDate.parse(dateText.trim(), DATE_FORMAT);
            LocalDate today = LocalDate.now();
            if (date.isBefore(today) || date.isAfter(today.plusDays(MAX_BOOKING_DAYS))) {
                throw badRequest("预约日期只能选择今天起90天内");
            }
            return date;
        } catch (DateTimeParseException exception) {
            throw badRequest("预约日期格式应为yyyy-MM-dd");
        }
    }

    private LocalTime parseTime(String timeText) {
        if (!StringUtils.hasText(timeText)) {
            throw badRequest("预约档期不能为空");
        }
        try {
            return LocalTime.parse(timeText.trim(), TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw badRequest("预约档期格式应为HH:mm");
        }
    }

    private void validateCreateRequest(CreateBookingAppointmentRequest request) {
        if (request == null || request.getStoreId() == null || request.getProductId() == null) {
            throw badRequest("店铺和商品不能为空");
        }
    }

    private Map<Long, BookingStore> loadStores(List<BookingAppointment> appointments) {
        List<Long> ids = appointments.stream().map(BookingAppointment::getStoreId).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return storeMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(BookingStore::getId, value -> value, (left, right) -> left, HashMap::new));
    }

    private Map<Long, BookingProduct> loadProducts(List<BookingAppointment> appointments) {
        List<Long> ids = appointments.stream().map(BookingAppointment::getProductId).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return productMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(BookingProduct::getId, value -> value, (left, right) -> left, HashMap::new));
    }

    private String statusText(String status) {
        if ("PENDING_PAYMENT".equals(status)) {
            return "待确定";
        }
        if ("CONFIRMED".equals(status)) {
            return "已预约";
        }
        if ("REFUND_PENDING".equals(status)) {
            return "待退款";
        }
        if ("REFUNDED".equals(status)) {
            return "已退款";
        }
        if ("CANCELLED".equals(status)) {
            return "已取消";
        }
        return status == null ? "" : status.toUpperCase(Locale.ROOT);
    }

    private String newAppointmentNo() {
        return "B" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private int positiveOrDefault(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private HopeException badRequest(String message) {
        return new HopeException(HttpStatus.BAD_REQUEST.value(), message);
    }

    private static final class SlotConfig {
        private final LocalTime time;
        private final int capacity;

        private SlotConfig(LocalTime time, int capacity) {
            this.time = time;
            this.capacity = capacity;
        }
    }
}



