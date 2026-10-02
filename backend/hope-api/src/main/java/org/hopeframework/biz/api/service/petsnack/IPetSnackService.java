package org.hopeframework.biz.api.service.petsnack;

import org.hopeframework.biz.api.entity.input.petsnack.PetSnackRequests;
import org.hopeframework.biz.api.entity.output.auth.TokenResponse;
import org.hopeframework.biz.api.entity.output.file.ImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IPetSnackService {
    TokenResponse wechatLogin(PetSnackRequests.WechatLoginRequest request, String clientIp);
    Map<String, Object> bootstrap();
    Map<String, Object> store();
    List<Map<String, Object>> categories();
    Map<String, Object> products(Long categoryId, String keyword, String tag, int pageNo, int pageSize);
    Map<String, Object> productDetail(Long productId);
    Map<String, Object> currentUser();
    Map<String, Object> updateProfile(PetSnackRequests.ProfileRequest request);
    ImageUploadResponse uploadAvatar(MultipartFile file);
    List<Map<String, Object>> addresses();
    Map<String, Object> createAddress(PetSnackRequests.AddressRequest request);
    Map<String, Object> updateAddress(Long addressId, PetSnackRequests.AddressRequest request);
    void deleteAddress(Long addressId);
    void setDefaultAddress(Long addressId);
    Map<String, Object> coupons(String status, int pageNo, int pageSize);
    Map<String, Object> previewOrder(PetSnackRequests.OrderPreviewRequest request);
    Map<String, Object> createOrder(PetSnackRequests.CreateOrderRequest request);
    Map<String, Object> myOrders(String status, int pageNo, int pageSize);
    Map<String, Object> orderDetail(Long orderId);
    Map<String, Object> continuePayment(Long orderId, String clientIp);
    Map<String, Object> cancelOrder(Long orderId);
    Map<String, Object> confirmReceipt(Long orderId);
    Map<String, Object> applyRefund(Long orderId, PetSnackRequests.RefundRequest request);
    List<Map<String, Object>> helpArticles();
    Map<String, Object> helpArticle(Long articleId);
    Map<String, Object> createFeedback(PetSnackRequests.FeedbackRequest request);
}
