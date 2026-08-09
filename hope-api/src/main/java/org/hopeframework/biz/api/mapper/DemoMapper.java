package org.hopeframework.biz.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.hopeframework.biz.api.model.Demo;

/**
 * 演示 Mapper：继承 BaseMapper 即可获得单表 CRUD。
 * 需要自定义 SQL 时，可在 src/main/resources/xml/DemoMapper.xml 中编写。
 */
@Mapper
public interface DemoMapper extends BaseMapper<Demo> {

    /**
     * 自定义 SQL 示例：按名称模糊统计数量
     */
    int countByName(String name);
}