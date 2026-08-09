package org.hopeframework.biz.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQo implements Serializable {

    private Integer page = 1;


    private Integer pageSize = 10;
}
