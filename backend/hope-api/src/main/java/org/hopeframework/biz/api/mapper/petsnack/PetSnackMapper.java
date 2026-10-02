package org.hopeframework.biz.api.mapper.petsnack;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetSnackMapper {
    Map<String, Object> selectStore();
    List<Map<String, Object>> selectBanners();
    List<Map<String, Object>> selectCategories();
    List<Map<String, Object>> selectProducts(@Param("categoryId") Long categoryId,
                                             @Param("keyword") String keyword,
                                             @Param("tag") String tag,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);
    long countProducts(@Param("categoryId") Long categoryId,
                       @Param("keyword") String keyword,
                       @Param("tag") String tag);
    Map<String, Object> selectProduct(@Param("productId") Long productId);
    List<Map<String, Object>> selectProductSkus(@Param("productId") Long productId);
    Map<String, Object> selectSkuForOrder(@Param("skuId") Long skuId);

    Map<String, Object> selectUserByWechat(@Param("appId") String appId, @Param("openid") String openid);
    Map<String, Object> selectUserById(@Param("userId") Long userId);
    int insertUser(@Param("user") Map<String, Object> user);
    int updateUserLogin(@Param("userId") Long userId, @Param("unionid") String unionid,
                        @Param("clientIp") String clientIp);
    int updateUserProfile(@Param("userId") Long userId, @Param("nickname") String nickname,
                          @Param("avatarUrl") String avatarUrl);
    int updateUserAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);

    List<Map<String, Object>> selectAddresses(@Param("userId") Long userId);
    Map<String, Object> selectAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);
    int countAddresses(@Param("userId") Long userId);
    int clearDefaultAddresses(@Param("userId") Long userId);
    int insertAddress(@Param("address") Map<String, Object> address);
    int updateAddress(@Param("address") Map<String, Object> address);
    int deleteAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);
    Long selectLatestAddressId(@Param("userId") Long userId);
    int setDefaultAddress(@Param("addressId") Long addressId, @Param("userId") Long userId);

    List<Map<String, Object>> selectUserCoupons(@Param("userId") Long userId,
                                                @Param("status") String status,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);
    long countUserCoupons(@Param("userId") Long userId, @Param("status") String status);
    Map<String, Object> selectUserCoupon(@Param("userCouponId") Long userCouponId,
                                         @Param("userId") Long userId);
    int lockUserCoupon(@Param("userCouponId") Long userCouponId,
                       @Param("userId") Long userId,
                       @Param("orderId") Long orderId);
    int releaseUserCoupon(@Param("userCouponId") Long userCouponId,
                          @Param("userId") Long userId,
                          @Param("orderId") Long orderId);

    int decreaseSkuStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);
    int restoreSkuStock(@Param("skuId") Long skuId, @Param("quantity") int quantity);
    Map<String, Object> selectOrderByClientRequest(@Param("userId") Long userId,
                                                   @Param("clientRequestNo") String clientRequestNo);
    int insertOrder(@Param("order") Map<String, Object> order);
    int insertOrderItem(@Param("item") Map<String, Object> item);
    List<Map<String, Object>> selectOrders(@Param("userId") Long userId,
                                           @Param("status") String status,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    long countOrders(@Param("userId") Long userId, @Param("status") String status);
    List<Map<String, Object>> countOrdersByStatus(@Param("userId") Long userId);
    Map<String, Object> selectOrder(@Param("orderId") Long orderId, @Param("userId") Long userId);
    Map<String, Object> lockOrderByOrderNo(@Param("orderNo") String orderNo);
    int markOrderPaid(@Param("orderId") Long orderId, @Param("transactionId") String transactionId);
    List<Map<String, Object>> selectOrderItems(@Param("orderId") Long orderId);
    int updateOrderStatus(@Param("orderId") Long orderId,
                          @Param("userId") Long userId,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("targetStatus") String targetStatus);
    int updateOrderRefundStatus(@Param("orderId") Long orderId, @Param("userId") Long userId);
    int insertRefund(@Param("refund") Map<String, Object> refund);

    List<Map<String, Object>> selectHelpArticles();
    Map<String, Object> selectHelpArticle(@Param("articleId") Long articleId);
    int insertFeedback(@Param("feedback") Map<String, Object> feedback);

    long adminCountUsers(@Param("keyword") String keyword, @Param("status") String status);
    List<Map<String, Object>> adminSelectUsers(@Param("keyword") String keyword, @Param("status") String status,
                                               @Param("offset") int offset, @Param("limit") int limit);
    long adminCountOrders(@Param("keyword") String keyword, @Param("status") String status);
    List<Map<String, Object>> adminSelectOrders(@Param("keyword") String keyword, @Param("status") String status,
                                                @Param("offset") int offset, @Param("limit") int limit);
    Map<String, Object> adminSelectOrder(@Param("orderId") Long orderId);
    Map<String, Object> adminOverview();
    int adminUpdateStore(@Param("store") Map<String, Object> store);
    List<Map<String, Object>> adminSelectCategories();
    int adminInsertCategory(@Param("category") Map<String, Object> category);
    int adminUpdateCategory(@Param("category") Map<String, Object> category);
    List<Map<String, Object>> adminSelectProducts(@Param("keyword") String keyword,
                                                  @Param("categoryId") Long categoryId);
    Map<String, Object> adminSelectProduct(@Param("productId") Long productId);
    int adminInsertProduct(@Param("product") Map<String, Object> product);
    int adminUpdateProduct(@Param("product") Map<String, Object> product);
    int adminInsertSku(@Param("sku") Map<String, Object> sku);
    int adminUpdatePrimarySku(@Param("productId") Long productId, @Param("salePrice") Object salePrice,
                              @Param("marketPrice") Object marketPrice, @Param("stock") Integer stock);
}
