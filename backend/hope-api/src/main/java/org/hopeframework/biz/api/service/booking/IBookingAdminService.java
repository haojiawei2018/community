package org.hopeframework.biz.api.service.booking;

import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingProductRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingCategoryRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingStoreRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.UpdateBookingStoreSlotOverrideRequest;
import org.hopeframework.biz.api.entity.input.booking.admin.RescheduleBookingAdminAppointmentRequest;
import org.hopeframework.biz.api.entity.output.booking.admin.BookingAdminDataResponse;
import org.hopeframework.biz.api.entity.output.booking.admin.BookingStoreSlotOverrideResponse;

public interface IBookingAdminService {
    BookingAdminDataResponse data();

    BookingAdminDataResponse.StoreItem updateStore(Long storeId, UpdateBookingStoreRequest request);

    BookingAdminDataResponse.CategoryItem updateCategory(Long categoryId, UpdateBookingCategoryRequest request);

    BookingStoreSlotOverrideResponse getSlotOverride(Long storeId, String date);

    BookingStoreSlotOverrideResponse updateSlotOverride(Long storeId, String date, UpdateBookingStoreSlotOverrideRequest request);

    BookingStoreSlotOverrideResponse deleteSlotOverride(Long storeId, String date);

    BookingAdminDataResponse.CategoryItem createCategory(UpdateBookingCategoryRequest request);

    BookingAdminDataResponse.ProductItem updateProduct(Long productId, UpdateBookingProductRequest request);

    BookingAdminDataResponse.ProductItem createProduct(UpdateBookingProductRequest request);

    BookingAdminDataResponse.AppointmentItem cancelConfirmedAppointment(Long appointmentId);

    BookingAdminDataResponse.AppointmentItem rescheduleConfirmedAppointment(
            Long appointmentId, RescheduleBookingAdminAppointmentRequest request);
}


