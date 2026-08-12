package org.hopeframework.biz.api.entity.input.forum;

import lombok.Data;

@Data
public class CreateReportRequest {
    private String reasonCode;
    private String reasonText;
}
