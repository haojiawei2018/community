package org.hopeframework.biz.api.helper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.hopeframework.biz.api.entity.PageResult;

public class PageResultHelper {


    public static PageResult page(Page page){

        return new PageResult(page.getRecords(),
                page.getTotal(),
                page.getSize(),
                page.getCurrent()
        );
    }

}
