package org.hopeframework.biz.api.service;

import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.DemoInput;
import org.hopeframework.biz.api.entity.output.DemoOutput;

import java.util.List;

/**
 * Demo 服务
 * 业务开发时参照此接口 + impl 的模式新增自己的 Service。
 */
public interface IDemoService {

    DemoOutput getById(Long id);

    List<DemoOutput> list(DemoInput input);

    PageResult page(DemoInput input);

    boolean save(DemoInput input);

    boolean update(DemoInput input);

    boolean delete(Long id);
}