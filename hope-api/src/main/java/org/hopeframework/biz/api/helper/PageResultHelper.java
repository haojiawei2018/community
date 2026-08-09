package org.hopeframework.biz.api.helper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import org.hopeframework.biz.api.entity.PageResult;

import java.util.List;

public class PageResultHelper {


    public static PageResult page(Page page){

        return new PageResult(page.getRecords(),
                page.getTotal(),
                page.getSize(),
                page.getCurrent()
        );
    }

    public static PageResult pageHelper(List list){

        PageInfo pageInfo = new PageInfo(list);

        return new PageResult(pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getSize(),
                pageInfo.getPageNum()
        );
    }
}
