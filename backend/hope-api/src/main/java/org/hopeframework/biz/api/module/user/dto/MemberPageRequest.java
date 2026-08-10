package org.hopeframework.biz.api.module.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hopeframework.biz.api.entity.PageQo;

@Data
@EqualsAndHashCode(callSuper = true)
public class MemberPageRequest extends PageQo {
    private String keyword;
    private String status;
}
