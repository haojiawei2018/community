package org.hopeframework.biz.api.entity.output.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CheckInDayResponse {
    private Date date;
    private Boolean checked;
    private Integer points;
}
