package org.hopeframework.biz.api.service.impl.petsnack;

import com.alibaba.fastjson.JSON;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.mapper.petsnack.PetSnackMapper;
import org.hopeframework.biz.api.service.petsnack.IPetSnackAdminService;
import org.hopeframework.core.exception.HopeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PetSnackAdminServiceImpl implements IPetSnackAdminService {
    private final PetSnackMapper mapper;
    public PetSnackAdminServiceImpl(PetSnackMapper mapper) { this.mapper = mapper; }

    public Map<String, Object> overview() { return mapper.adminOverview(); }
    public Map<String, Object> store() { return mapper.selectStore(); }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateStore(Map<String, Object> request) {
        Map<String, Object> current = mapper.selectStore();
        if (current == null) throw new HopeException(404, "店铺不存在");
        current.put("name", text(request.get("name"), "店铺名称"));
        current.put("logoUrl", text(request.get("logoUrl"), "店铺图片"));
        current.put("description", text(request.get("description"), "店铺介绍"));
        current.put("servicePhone", request.get("servicePhone") == null ? "" : String.valueOf(request.get("servicePhone")).trim());
        Object tags = request.get("serviceTags");
        current.put("serviceTagsJson", JSON.toJSONString(tags instanceof List ? tags : Collections.emptyList()));
        if (mapper.adminUpdateStore(current) != 1) throw new HopeException(409, "店铺资料保存失败");
        return mapper.selectStore();
    }

    public PageResult<Map<String, Object>> users(String keyword, String status, long page, long pageSize) {
        int size = safeSize(pageSize), offset = safeOffset(page, size);
        return new PageResult<>(mapper.adminSelectUsers(clean(keyword), clean(status), offset, size),
                mapper.adminCountUsers(clean(keyword), clean(status)), size, Math.max(1, page));
    }
    public PageResult<Map<String, Object>> orders(String keyword, String status, long page, long pageSize) {
        int size = safeSize(pageSize), offset = safeOffset(page, size);
        return new PageResult<>(mapper.adminSelectOrders(clean(keyword), clean(status), offset, size),
                mapper.adminCountOrders(clean(keyword), clean(status)), size, Math.max(1, page));
    }
    public Map<String, Object> order(Long orderId) {
        Map<String, Object> order = mapper.adminSelectOrder(orderId);
        if (order == null) throw new HopeException(404, "订单不存在");
        order.put("items", mapper.selectOrderItems(orderId));
        return order;
    }
    public List<Map<String, Object>> categories() { return mapper.adminSelectCategories(); }
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveCategory(Long id, Map<String, Object> request) {
        Map<String,Object> data = new java.util.HashMap<>();
        data.put("id", id); data.put("code", text(request.get("code"), "分类编码"));
        data.put("name", text(request.get("name"), "分类名称"));
        data.put("iconUrl", request.get("iconUrl")); data.put("status", value(request,"status","ACTIVE"));
        data.put("sortOrder", integer(request.get("sortOrder"),0));
        if(id==null) mapper.adminInsertCategory(data); else if(mapper.adminUpdateCategory(data)!=1) throw new HopeException(404,"分类不存在");
        return mapper.adminSelectCategories().stream().filter(x->String.valueOf(x.get("id")).equals(String.valueOf(data.get("id")))).findFirst().orElse(data);
    }
    public List<Map<String, Object>> products(String keyword, Long categoryId) { return mapper.adminSelectProducts(clean(keyword),categoryId); }
    public Map<String, Object> product(Long productId) {
        Map<String,Object> result=mapper.adminSelectProduct(productId);if(result==null)throw new HopeException(404,"商品不存在");return result;
    }
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveProduct(Long id, Map<String, Object> request) {
        Map<String,Object> data=new java.util.HashMap<>();data.put("id",id);
        data.put("categoryId",Long.valueOf(String.valueOf(request.get("categoryId"))));data.put("name",text(request.get("name"),"商品名称"));
        data.put("coverUrl",text(request.get("coverUrl"),"商品主图"));data.put("galleryUrlsJson",JSON.toJSONString(request.get("galleryUrls") instanceof List?request.get("galleryUrls"):Collections.emptyList()));
        data.put("description",value(request,"description",""));data.put("purchaseNotice",value(request,"purchaseNotice",""));
        data.put("salePrice",decimal(request.get("salePrice")));data.put("marketPrice",decimal(request.get("marketPrice")));
        data.put("salesCount",integer(request.get("salesCount"),0));data.put("stock",integer(request.get("stock"),0));
        data.put("hot",boolInt(request.get("hot")));data.put("newProduct",boolInt(request.get("newProduct")));
        data.put("status",value(request,"status","ACTIVE"));data.put("sortOrder",integer(request.get("sortOrder"),0));
        if(id==null){mapper.adminInsertProduct(data);Map<String,Object> sku=new java.util.HashMap<>();sku.put("productId",data.get("id"));sku.put("skuCode","PET-"+data.get("id"));sku.put("salePrice",data.get("salePrice"));sku.put("marketPrice",data.get("marketPrice"));sku.put("stock",data.get("stock"));mapper.adminInsertSku(sku);}else{if(mapper.adminUpdateProduct(data)!=1)throw new HopeException(404,"商品不存在");if(mapper.adminUpdatePrimarySku(id,data.get("salePrice"),data.get("marketPrice"),(Integer)data.get("stock"))==0){Map<String,Object> sku=new java.util.HashMap<>();sku.put("productId",id);sku.put("skuCode","PET-"+id);sku.put("salePrice",data.get("salePrice"));sku.put("marketPrice",data.get("marketPrice"));sku.put("stock",data.get("stock"));mapper.adminInsertSku(sku);}}
        return product(Long.valueOf(String.valueOf(data.get("id"))));
    }
    private int safeSize(long size) { return (int) Math.min(100, Math.max(1, size)); }
    private int safeOffset(long page, int size) { return (int) ((Math.max(1, page) - 1) * size); }
    private String clean(String value) { return value == null || value.trim().isEmpty() ? null : value.trim(); }
    private String text(Object value, String name) {
        String result = value == null ? "" : String.valueOf(value).trim();
        if (result.isEmpty()) throw new HopeException(400, name + "不能为空");
        return result;
    }
    private String value(Map<String,Object> map,String key,String fallback){Object v=map.get(key);return v==null?fallback:String.valueOf(v).trim();}
    private Integer integer(Object v,int fallback){try{return v==null?fallback:Integer.valueOf(String.valueOf(v));}catch(Exception e){return fallback;}}
    private java.math.BigDecimal decimal(Object v){try{return new java.math.BigDecimal(String.valueOf(v));}catch(Exception e){throw new HopeException(400,"价格格式不正确");}}
    private int boolInt(Object v){return Boolean.TRUE.equals(v)||"1".equals(String.valueOf(v))||"true".equalsIgnoreCase(String.valueOf(v))?1:0;}
}
