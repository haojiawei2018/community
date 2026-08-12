package org.hopeframework.biz.api.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.output.user.CheckInDayResponse;
import org.hopeframework.biz.api.entity.output.user.CheckInSummaryResponse;
import org.hopeframework.biz.api.mapper.user.MemberCheckInMapper;
import org.hopeframework.biz.api.model.user.MemberCheckIn;
import org.hopeframework.biz.api.service.user.ICheckInService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckInServiceImpl implements ICheckInService {
    private final MemberCheckInMapper checkInMapper;

    public CheckInServiceImpl(MemberCheckInMapper checkInMapper) {
        this.checkInMapper = checkInMapper;
    }

    @Override
    public CheckInSummaryResponse summary() {
        return buildSummary(AuthContext.require().getMemberId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckInSummaryResponse checkIn() {
        Long memberId = AuthContext.require().getMemberId();
        Date today = startOfDay(new Date());
        if (findByDate(memberId, today) == null) {
            Date yesterday = addDays(today, -1);
            MemberCheckIn previous = findByDate(memberId, yesterday);
            int streak = previous == null ? 1 : previous.getStreakDays() + 1;
            int points = streak % 7 == 0 ? 30 : 10;
            MemberCheckIn record = new MemberCheckIn();
            record.setTenantId(TenantContext.requireTenantId());
            record.setMemberId(memberId);
            record.setCheckInDate(today);
            record.setStreakDays(streak);
            record.setPoints(points);
            record.setCreatedAt(new Date());
            record.setDeleted(0);
            try {
                checkInMapper.insert(record);
            } catch (DuplicateKeyException ignored) {
                // 同一成员并发签到由唯一索引保证幂等。
            }
        }
        return buildSummary(memberId);
    }

    private CheckInSummaryResponse buildSummary(Long memberId) {
        Date today = startOfDay(new Date());
        Date weekStart = addDays(today, -6);
        List<MemberCheckIn> recent = checkInMapper.selectList(new LambdaQueryWrapper<MemberCheckIn>()
                .eq(MemberCheckIn::getMemberId, memberId)
                .ge(MemberCheckIn::getCheckInDate, weekStart)
                .le(MemberCheckIn::getCheckInDate, today)
                .orderByAsc(MemberCheckIn::getCheckInDate));
        List<MemberCheckIn> all = checkInMapper.selectList(new LambdaQueryWrapper<MemberCheckIn>()
                .eq(MemberCheckIn::getMemberId, memberId));
        Map<String, MemberCheckIn> byDate = new HashMap<>();
        for (MemberCheckIn item : recent) byDate.put(key(item.getCheckInDate()), item);
        MemberCheckIn todayRecord = byDate.get(key(today));
        MemberCheckIn latest = all.stream().max((a, b) -> a.getCheckInDate().compareTo(b.getCheckInDate())).orElse(null);
        int currentStreak = latest != null && !latest.getCheckInDate().before(addDays(today, -1))
                ? latest.getStreakDays() : 0;
        int nextPoints = (currentStreak + 1) % 7 == 0 ? 30 : 10;
        CheckInSummaryResponse response = new CheckInSummaryResponse();
        response.setCheckedToday(todayRecord != null);
        response.setTodayPoints(todayRecord == null ? nextPoints : todayRecord.getPoints());
        response.setStreakDays(currentStreak);
        response.setTotalPoints(all.stream().mapToInt(item -> item.getPoints() == null ? 0 : item.getPoints()).sum());
        List<CheckInDayResponse> week = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Date date = addDays(weekStart, i);
            MemberCheckIn record = byDate.get(key(date));
            week.add(new CheckInDayResponse(date, record != null,
                    record == null ? (i == 6 ? nextPoints : 10) : record.getPoints()));
        }
        response.setWeek(week);
        return response;
    }

    private MemberCheckIn findByDate(Long memberId, Date date) {
        return checkInMapper.selectOne(new LambdaQueryWrapper<MemberCheckIn>()
                .eq(MemberCheckIn::getMemberId, memberId)
                .eq(MemberCheckIn::getCheckInDate, date));
    }

    private Date startOfDay(Date date) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    private String key(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
    }
}
