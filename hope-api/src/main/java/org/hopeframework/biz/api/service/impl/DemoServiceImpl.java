package org.hopeframework.biz.api.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.hopeframework.biz.api.entity.PageResult;
import org.hopeframework.biz.api.entity.input.DemoInput;
import org.hopeframework.biz.api.entity.output.DemoOutput;
import org.hopeframework.biz.api.helper.PageResultHelper;
import org.hopeframework.biz.api.mapper.DemoMapper;
import org.hopeframework.biz.api.model.Demo;
import org.hopeframework.biz.api.service.IDemoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Demo 服务实现
 * @DS("master") 表示该方法/类使用名为 master 的数据源（见 application-*.yml dynamic 配置）。
 */
@Service
@DS("master")
public class DemoServiceImpl implements IDemoService {

    private final DemoMapper demoMapper;

    public DemoServiceImpl(DemoMapper demoMapper) {
        this.demoMapper = demoMapper;
    }

    @Override
    public DemoOutput getById(Long id) {
        return toOutput(demoMapper.selectById(id));
    }

    @Override
    public List<DemoOutput> list(DemoInput input) {
        return toOutputs(demoMapper.selectList(buildWrapper(input)));
    }

    @Override
    public PageResult page(DemoInput input) {
        Page<Demo> page = new Page<>(input.getPage(), input.getPageSize());
        return PageResultHelper.page(demoMapper.selectPage(page, buildWrapper(input)));
    }

    @Override
    public boolean save(DemoInput input) {
        Demo demo = new Demo();
        BeanUtils.copyProperties(input, demo);
        demo.setId(null);
        demo.setCreateTime(new Date());
        return demoMapper.insert(demo) > 0;
    }

    @Override
    public boolean update(DemoInput input) {
        Demo demo = new Demo();
        BeanUtils.copyProperties(input, demo);
        return demoMapper.updateById(demo) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return demoMapper.deleteById(id) > 0;
    }

    private LambdaQueryWrapper<Demo> buildWrapper(DemoInput input) {
        LambdaQueryWrapper<Demo> wrapper = new LambdaQueryWrapper<>();
        if (input != null) {
            wrapper.like(StringUtils.hasText(input.getName()), Demo::getName, input.getName());
            wrapper.eq(StringUtils.hasText(input.getPhone()), Demo::getPhone, input.getPhone());
        }
        wrapper.orderByDesc(Demo::getId);
        return wrapper;
    }

    private List<DemoOutput> toOutputs(List<Demo> list) {
        List<DemoOutput> outputs = new ArrayList<>();
        for (Demo demo : list) {
            outputs.add(toOutput(demo));
        }
        return outputs;
    }

    private DemoOutput toOutput(Demo demo) {
        if (demo == null) {
            return null;
        }
        DemoOutput output = new DemoOutput();
        BeanUtils.copyProperties(demo, output);
        return output;
    }
}