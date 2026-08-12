package org.hopeframework.biz.api.service.user;

import org.hopeframework.biz.api.common.security.AuthContext;
import org.hopeframework.biz.api.common.security.AuthPrincipal;
import org.hopeframework.biz.api.common.tenant.TenantContext;
import org.hopeframework.biz.api.entity.output.user.CheckInSummaryResponse;
import org.hopeframework.biz.api.mapper.user.MemberCheckInMapper;
import org.hopeframework.biz.api.model.user.MemberCheckIn;
import org.hopeframework.biz.api.service.impl.user.CheckInServiceImpl;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckInServiceImplTest {

    @After
    public void tearDown() {
        AuthContext.clear();
        TenantContext.clear();
    }

    @Test
    public void shouldCreateTenantScopedFirstCheckIn() {
        TableInfoHelper.initTableInfo(new org.apache.ibatis.builder.MapperBuilderAssistant(
                new MybatisConfiguration(), "test"), MemberCheckIn.class);
        MemberCheckInMapper mapper = mock(MemberCheckInMapper.class);
        when(mapper.selectOne(any())).thenReturn(null);
        when(mapper.selectList(any())).thenReturn(Collections.emptyList());
        CheckInServiceImpl service = new CheckInServiceImpl(mapper);
        AuthContext.set(new AuthPrincipal(1L, 2L, 3L));
        TenantContext.set(3L, "community-3");

        service.checkIn();

        ArgumentCaptor<MemberCheckIn> captor = ArgumentCaptor.forClass(MemberCheckIn.class);
        verify(mapper).insert(captor.capture());
        assertEquals(Long.valueOf(3L), captor.getValue().getTenantId());
        assertEquals(Long.valueOf(2L), captor.getValue().getMemberId());
        assertEquals(Integer.valueOf(1), captor.getValue().getStreakDays());
        assertEquals(Integer.valueOf(10), captor.getValue().getPoints());
    }

    @Test
    public void shouldReturnRealAccumulatedPoints() {
        TableInfoHelper.initTableInfo(new org.apache.ibatis.builder.MapperBuilderAssistant(
                new MybatisConfiguration(), "test"), MemberCheckIn.class);
        MemberCheckInMapper mapper = mock(MemberCheckInMapper.class);
        List<MemberCheckIn> records = new ArrayList<>();
        MemberCheckIn first = new MemberCheckIn();
        first.setCheckInDate(new java.util.Date());
        first.setStreakDays(1);
        first.setPoints(10);
        records.add(first);
        when(mapper.selectList(any())).thenReturn(records);
        CheckInServiceImpl service = new CheckInServiceImpl(mapper);
        AuthContext.set(new AuthPrincipal(1L, 2L, 3L));

        CheckInSummaryResponse result = service.summary();

        assertEquals(Integer.valueOf(10), result.getTotalPoints());
        assertEquals(Integer.valueOf(1), result.getStreakDays());
        assertTrue(result.getCheckedToday());
    }
}
