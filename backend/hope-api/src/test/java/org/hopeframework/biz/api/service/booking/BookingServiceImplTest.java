package org.hopeframework.biz.api.service.booking;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.input.booking.CreateBookingAppointmentRequest;
import org.hopeframework.biz.api.entity.input.booking.RescheduleBookingAppointmentRequest;
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
import org.hopeframework.biz.api.service.impl.booking.BookingServiceImpl;
import org.hopeframework.core.exception.HopeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.Time;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingServiceImplTest {

    private BookingStoreMapper storeMapper;
    private BookingStoreSlotOverrideMapper slotOverrideMapper;
    private BookingCategoryMapper categoryMapper;
    private BookingProductMapper productMapper;
    private BookingAppointmentMapper appointmentMapper;
    private IBookingWechatPayService wechatPayService;
    private BookingServiceImpl service;

    @Before
    public void setUp() {
        initTable(BookingStore.class, "bookingStoreTest");
        initTable(BookingCategory.class, "bookingCategoryTest");
        initTable(BookingProduct.class, "bookingProductTest");
        initTable(BookingAppointment.class, "bookingAppointmentTest");
        storeMapper = mock(BookingStoreMapper.class);
        categoryMapper = mock(BookingCategoryMapper.class);
        productMapper = mock(BookingProductMapper.class);
        appointmentMapper = mock(BookingAppointmentMapper.class);
        slotOverrideMapper = mock(BookingStoreSlotOverrideMapper.class);
        wechatPayService = mock(IBookingWechatPayService.class);
        when(wechatPayService.resolvePaymentAmount(any(BigDecimal.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(wechatPayService.resolvePaymentAmount(any(BigDecimal.class), anyInt()))
                .thenAnswer(invocation -> ((BigDecimal) invocation.getArgument(0))
                        .multiply(BigDecimal.valueOf(((Integer) invocation.getArgument(1)).longValue())));
        service = new BookingServiceImpl(
                storeMapper, categoryMapper, productMapper, appointmentMapper, slotOverrideMapper, wechatPayService);
        TenantContext.set(1L, "default");
    }

    @After
    public void tearDown() {
        AuthContext.clear();
        TenantContext.clear();
    }

    @Test
    public void shouldReturnPageDataInSingleBootstrapCall() {
        BookingStore store = store();
        BookingCategory category = new BookingCategory();
        category.setId(11L);
        category.setCategoryCode("DATANG");
        category.setCategoryName("大唐风¥269");
        category.setStatus("ACTIVE");
        BookingProduct product = product();
        when(storeMapper.selectList(any())).thenReturn(Collections.singletonList(store));
        when(categoryMapper.selectList(any())).thenReturn(Collections.singletonList(category));
        when(productMapper.selectList(any())).thenReturn(Collections.singletonList(product));

        BookingBootstrapResponse response = service.bootstrap(null);

        assertEquals(Long.valueOf(1L), response.getCurrentStoreId());
        assertEquals("18660123456", response.getServicePhone());
        assertEquals(1, response.getStores().size());
        assertEquals(1, response.getCategories().size());
        assertEquals("大唐风", response.getProducts().get(0).getName());
        assertEquals(new BigDecimal("50.00"), response.getProducts().get(0).getDepositAmount());
    }

    @Test
    public void shouldMarkSlotFullWhenCapacityIsConsumed() {
        BookingStore store = store();
        store.setSlotConfig("[{\"time\":\"16:30\",\"capacity\":2}]");
        when(storeMapper.selectById(1L)).thenReturn(store);
        when(productMapper.selectOne(any())).thenReturn(product());
        when(appointmentMapper.sumReservedPeople(anyLong(), anyLong(), any(), any())).thenReturn(2);

        BookingAvailabilityResponse response = service.availability(
                1L, 21L, LocalDate.now().plusDays(1).toString());

        assertEquals(1, response.getSlots().size());
        assertFalse(response.getSlots().get(0).getAvailable());
        assertEquals("FULL", response.getSlots().get(0).getStatus());
        assertEquals("已约满", response.getSlots().get(0).getStatusText());
    }

    @Test
    public void shouldUseDateSlotOverrideForAvailability() {
        BookingStore store = store();
        when(storeMapper.selectById(1L)).thenReturn(store);
        when(productMapper.selectOne(any())).thenReturn(product());
        when(slotOverrideMapper.findByStoreAndDate(anyLong(), anyLong(), any()))
                .thenReturn(slotOverride(LocalDate.now().plusDays(1), "[{\"time\":\"09:00\",\"capacity\":3}]"));
        when(appointmentMapper.sumReservedPeople(anyLong(), anyLong(), any(), any())).thenReturn(1);

        BookingAvailabilityResponse response = service.availability(
                1L, 21L, LocalDate.now().plusDays(1).toString());

        assertEquals(1, response.getSlots().size());
        assertEquals("09:00", response.getSlots().get(0).getTime());
        assertEquals(Integer.valueOf(3), response.getSlots().get(0).getCapacityPeople());
        assertEquals(Integer.valueOf(2), response.getSlots().get(0).getRemainingPeople());
        assertTrue(response.getSlots().get(0).getAvailable());
    }

    @Test
    public void shouldUseDateSlotOverrideWhenCreatingAppointment() {
        BookingStore store = store();
        when(storeMapper.lockActiveStore(1L, 1L)).thenReturn(store);
        when(productMapper.selectOne(any())).thenReturn(product());
        when(slotOverrideMapper.findByStoreAndDate(anyLong(), anyLong(), any()))
                .thenReturn(slotOverride(LocalDate.now().plusDays(1), "[{\"time\":\"09:00\",\"capacity\":3}]"));
        AuthContext.set(new AuthPrincipal(7L, 8L, 1L));
        CreateBookingAppointmentRequest request = new CreateBookingAppointmentRequest();
        request.setStoreId(1L);
        request.setProductId(21L);
        request.setAppointmentDate(LocalDate.now().plusDays(1).toString());
        request.setSlotTime("16:30");
        request.setPeopleCount(1);

        try {
            service.createAppointment(request, "127.0.0.1");
        } catch (HopeException exception) {
            assertEquals("所选档期不存在或不可预约", exception.getMessage());
            return;
        }
        throw new AssertionError("Expected HopeException");
    }

    @Test
    public void shouldUseDateSlotOverrideWhenRescheduling() {
        BookingAppointment appointment = confirmedAppointment(LocalDate.now().plusDays(3), "16:30");
        BookingStore store = store();
        LocalDate newDate = LocalDate.now().plusDays(4);
        when(appointmentMapper.lockByIdForMember(1L, 31L, 8L)).thenReturn(appointment);
        when(storeMapper.lockActiveStore(1L, 1L)).thenReturn(store);
        when(slotOverrideMapper.findByStoreAndDate(anyLong(), anyLong(), any()))
                .thenReturn(slotOverride(newDate, "[{\"time\":\"17:30\",\"capacity\":3}]"));
        when(appointmentMapper.sumReservedPeople(anyLong(), anyLong(), any(), any())).thenReturn(0);
        when(productMapper.selectById(21L)).thenReturn(product());
        AuthContext.set(new AuthPrincipal(7L, 8L, 1L));
        RescheduleBookingAppointmentRequest request = new RescheduleBookingAppointmentRequest();
        request.setAppointmentDate(newDate.toString());
        request.setSlotTime("17:30");

        BookingAppointmentResponse response = service.rescheduleAppointment(31L, request);

        ArgumentCaptor<BookingAppointment> captor = ArgumentCaptor.forClass(BookingAppointment.class);
        verify(appointmentMapper).updateById(captor.capture());
        assertEquals("17:30", captor.getValue().getSlotTime().toLocalTime().toString());
        assertEquals(newDate.toString(), response.getAppointmentDate());
    }

    @Test
    public void shouldMultiplyConfiguredDepositByPeopleCount() {
        BookingStore store = store();
        BookingProduct product = product();
        when(storeMapper.lockActiveStore(1L, 1L)).thenReturn(store);
        when(productMapper.selectOne(any())).thenReturn(product);
        when(appointmentMapper.sumReservedPeople(anyLong(), anyLong(), any(), any())).thenReturn(0);
        AuthContext.set(new AuthPrincipal(7L, 8L, 1L));
        CreateBookingAppointmentRequest request = new CreateBookingAppointmentRequest();
        request.setStoreId(1L);
        request.setProductId(21L);
        request.setAppointmentDate(LocalDate.now().plusDays(1).toString());
        request.setSlotTime("16:30");
        request.setPeopleCount(2);
        request.setRemark("敏感肌");

        BookingAppointmentResponse response = service.createAppointment(request, "127.0.0.1");

        ArgumentCaptor<BookingAppointment> captor = ArgumentCaptor.forClass(BookingAppointment.class);
        verify(appointmentMapper).insert(captor.capture());
        assertEquals(new BigDecimal("538.00"), captor.getValue().getSaleAmount());
        assertEquals(new BigDecimal("100.00"), captor.getValue().getDepositAmount());
        assertEquals("PENDING_PAYMENT", response.getStatus());
        assertTrue(response.getPaymentRequired());
    }

    @Test
    public void shouldRescheduleConfirmedAppointmentOutside48Hours() {
        BookingAppointment appointment = confirmedAppointment(LocalDate.now().plusDays(3), "16:30");
        BookingStore store = store();
        store.setSlotConfig("[{\"time\":\"17:30\",\"capacity\":3}]");
        when(appointmentMapper.lockByIdForMember(1L, 31L, 8L)).thenReturn(appointment);
        when(storeMapper.lockActiveStore(1L, 1L)).thenReturn(store);
        when(appointmentMapper.sumReservedPeople(anyLong(), anyLong(), any(), any())).thenReturn(0);
        when(productMapper.selectById(21L)).thenReturn(product());
        AuthContext.set(new AuthPrincipal(7L, 8L, 1L));
        RescheduleBookingAppointmentRequest request = new RescheduleBookingAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(4).toString());
        request.setSlotTime("17:30");

        BookingAppointmentResponse response = service.rescheduleAppointment(31L, request);

        ArgumentCaptor<BookingAppointment> captor = ArgumentCaptor.forClass(BookingAppointment.class);
        verify(appointmentMapper).updateById(captor.capture());
        assertEquals("17:30", captor.getValue().getSlotTime().toLocalTime().toString());
        assertEquals(request.getAppointmentDate(), response.getAppointmentDate());
        assertTrue(response.getCanReschedule());
    }

    @Test
    public void shouldRejectRescheduleInside48Hours() {
        BookingAppointment appointment = confirmedAppointment(LocalDate.now().plusDays(1), "16:30");
        when(appointmentMapper.lockByIdForMember(1L, 31L, 8L)).thenReturn(appointment);
        AuthContext.set(new AuthPrincipal(7L, 8L, 1L));
        RescheduleBookingAppointmentRequest request = new RescheduleBookingAppointmentRequest();
        request.setAppointmentDate(LocalDate.now().plusDays(4).toString());
        request.setSlotTime("17:30");

        try {
            service.rescheduleAppointment(31L, request);
        } catch (HopeException exception) {
            assertEquals("距离到店时间不足48小时，请联系客服处理", exception.getMessage());
            return;
        }
        throw new AssertionError("Expected HopeException");
    }

    private BookingAppointment confirmedAppointment(LocalDate date, String time) {
        BookingAppointment appointment = new BookingAppointment();
        appointment.setId(31L);
        appointment.setAppointmentNo("B202609010001");
        appointment.setUserId(7L);
        appointment.setMemberId(8L);
        appointment.setStoreId(1L);
        appointment.setProductId(21L);
        appointment.setAppointmentDate(Date.valueOf(date));
        appointment.setSlotTime(Time.valueOf(time + ":00"));
        appointment.setPeopleCount(1);
        appointment.setSaleAmount(new BigDecimal("269.00"));
        appointment.setDepositAmount(new BigDecimal("50.00"));
        appointment.setStatus("CONFIRMED");
        appointment.setPaymentStatus("PAID");
        appointment.setCreatedAt(new java.util.Date());
        return appointment;
    }

    private BookingStore store() {
        BookingStore store = new BookingStore();
        store.setId(1L);
        store.setStoreName("大喜汉服【旗舰店】");
        store.setServicePhone("18660123456");
        store.setAdvanceMinutes(120);
        store.setSlotConfig("[{\"time\":\"16:30\",\"capacity\":2}]");
        store.setStatus("ACTIVE");
        return store;
    }

    private BookingStoreSlotOverride slotOverride(LocalDate date, String slotConfig) {
        BookingStoreSlotOverride override = new BookingStoreSlotOverride();
        override.setId(51L);
        override.setStoreId(1L);
        override.setSlotDate(Date.valueOf(date));
        override.setSlotConfig(slotConfig);
        return override;
    }

    private BookingProduct product() {
        BookingProduct product = new BookingProduct();
        product.setId(21L);
        product.setStoreId(1L);
        product.setCategoryId(11L);
        product.setProductName("大唐风");
        product.setGalleryUrls("[\"/static/booking/hanfu-hero.jpg\"]");
        product.setSalePrice(new BigDecimal("269.00"));
        product.setDepositAmount(new BigDecimal("50.00"));
        product.setFeatured(1);
        product.setStatus("ACTIVE");
        return product;
    }

    private void initTable(Class<?> modelClass, String namespace) {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), namespace), modelClass);
    }
}


