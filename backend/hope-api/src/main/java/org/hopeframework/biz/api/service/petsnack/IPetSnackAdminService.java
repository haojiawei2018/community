package org.hopeframework.biz.api.service.petsnack;

import org.hopeframework.biz.api.entity.PageResult;
import java.util.Map;
import java.util.List;

public interface IPetSnackAdminService {
    Map<String, Object> overview();
    Map<String, Object> store();
    Map<String, Object> updateStore(Map<String, Object> request);
    PageResult<Map<String, Object>> users(String keyword, String status, long page, long pageSize);
    PageResult<Map<String, Object>> orders(String keyword, String status, long page, long pageSize);
    Map<String, Object> order(Long orderId);
    List<Map<String, Object>> categories();
    Map<String, Object> saveCategory(Long id, Map<String, Object> request);
    List<Map<String, Object>> products(String keyword, Long categoryId);
    Map<String, Object> product(Long productId);
    Map<String, Object> saveProduct(Long id, Map<String, Object> request);
}
