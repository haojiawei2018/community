package org.hopeframework.biz.api.service.user;

import org.hopeframework.biz.api.entity.output.user.CheckInSummaryResponse;

public interface ICheckInService {
    CheckInSummaryResponse summary();
    CheckInSummaryResponse checkIn();
}
