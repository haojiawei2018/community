package org.hopeframework.biz.api.entity.output.user;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CheckInSummaryResponse {
    private Boolean checkedToday;
    private Integer streakDays;
    private Integer totalPoints;
    private Integer todayPoints;
    private List<CheckInDayResponse> week = new ArrayList<>();
}
