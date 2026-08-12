<template>
  <view class="sign-page">
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#FFFFFF00">
      <view slot="back" class="nav-back" @tap="goBack"><text class="tn-icon-left-arrow"></text></view>
    </tn-nav-bar>

    <view class="top-bg">
      <view class="calendar-art tn-icon-calendar"></view>
    </view>

    <view class="page-content" :style="{ paddingTop: vuex_custom_bar_height + 10 + 'px' }">
      <view class="notice-box">
        <tn-notice-bar :list="notices" mode="vertical" leftIconName="starry"></tn-notice-bar>
      </view>

      <view class="points-block">
        <view><text class="points-value">{{ summary.totalPoints }}</text><text class="points-unit">积分</text></view>
        <view class="points-sub">已连续签到 {{ summary.streakDays }} 天</view>
      </view>

      <view class="check-card">
        <view class="week-line"></view>
        <view class="week-grid">
          <view v-for="(day, index) in weekDays" :key="index" class="day-item">
            <view class="day-badge" :class="{ checked: day.checked, today: day.today }">
              <text v-if="day.checked" class="tn-icon-success"></text>
              <text v-else>+{{ day.points }}</text>
            </view>
            <view class="day-date" :class="{ 'day-date--today': day.today }">{{ day.label }}</view>
          </view>
        </view>

        <tn-button shape="round" :backgroundColor="summary.checkedToday ? '#DFE4E8' : '#000000'"
          :fontColor="summary.checkedToday ? '#95A3A8' : '#FFFFFF'" padding="40rpx 0" width="100%"
          :fontSize="30" :disabled="summary.checkedToday || submitting" @tap="handleCheckIn">
          {{ submitting ? '签到中...' : (summary.checkedToday ? '今日已签到' : '立即签到') }}
        </tn-button>

        <view class="reward-tip">坚持每天连续签到可以获得更多奖励</view>
        <view class="rule-text">
          温馨提示：<br>
          每日签到获得 10 积分；连续签到第 7 天获得 30 积分。签到记录与当前社区账号绑定，同一天不会重复计分。
        </view>
      </view>

      <view class="record-card">
        <view class="record-title">最近签到</view>
        <view v-if="checkedDays.length">
          <view v-for="(item, index) in checkedDays" :key="index" class="record-row tn-flex tn-flex-row-between tn-flex-col-center">
            <view>
              <view class="record-date">{{ formatFullDate(item.date) }}</view>
              <view class="record-desc">连续签到 {{ item.streakText }} 天</view>
            </view>
            <view class="record-points">+{{ item.points }}</view>
          </view>
        </view>
        <view v-else-if="!loading" class="empty-record">还没有签到记录，今天开始坚持吧</view>
        <view v-else class="empty-record">正在加载...</view>
      </view>
    </view>

    <tn-landscape :show="showSuccess" @close="showSuccess = false" closePosition="bottom">
      <view class="success-panel">
        <view class="success-icon tn-icon-success"></view>
        <view class="success-title">签到成功</view>
        <view class="success-desc">已连续签到 <text>{{ summary.streakDays }}</text> 天，获得 <text>{{ summary.todayPoints }}</text> 积分</view>
        <view class="success-tip">连续签到第 7 天可获得 30 积分奖励</view>
      </view>
    </tn-landscape>

    <view class="bottom-safe"></view>
  </view>
</template>

<script>
  import { checkIn } from '@/api/index.js'
  import session from '@/utils/session.js'

  export default {
    data() {
      return {
        loading: false,
        submitting: false,
        showSuccess: false,
        summary: { checkedToday: false, streakDays: 0, totalPoints: 0, todayPoints: 10, week: [] },
        notices: ['每日签到获得 10 积分，连续第 7 天奖励 30 积分', '签到数据与当前社区账号同步，多端状态保持一致']
      }
    },
    computed: {
      weekDays() {
        const todayKey = this.dateKey(new Date())
        const rows = Array.isArray(this.summary.week) ? this.summary.week : []
        return rows.map(item => ({
          ...item,
          today: this.dateKey(item.date) === todayKey,
          label: this.formatShortDate(item.date)
        }))
      },
      checkedDays() {
        return this.weekDays.filter(item => item.checked).slice().reverse().map((item, index) => ({
          ...item,
          streakText: Math.max(Number(this.summary.streakDays) - index, 1)
        }))
      }
    },
    onLoad() {
      if (!session.isAccessTokenUsable()) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => uni.redirectTo({ url: '/pages/login/login' }), 500)
        return
      }
      this.loadSummary()
    },
    methods: {
      async loadSummary() {
        if (this.loading) return
        this.loading = true
        try {
          const result = await checkIn.getSummary()
          this.summary = Object.assign({}, this.summary, result || {})
        } finally {
          this.loading = false
        }
      },
      async handleCheckIn() {
        if (this.submitting || this.summary.checkedToday) return
        this.submitting = true
        try {
          const result = await checkIn.checkIn()
          this.summary = Object.assign({}, this.summary, result || {})
          this.showSuccess = true
        } finally {
          this.submitting = false
        }
      },
      goBack() { uni.navigateBack({ delta: 1 }) },
      parseDate(value) {
        if (value instanceof Date) return value
        if (Array.isArray(value) && value.length >= 3) {
          return new Date(Number(value[0]), Number(value[1]) - 1, Number(value[2]))
        }
        if (typeof value === 'string') {
          const matched = value.trim().match(/^(\d{4})[-/](\d{1,2})[-/](\d{1,2})/)
          if (matched) {
            return new Date(Number(matched[1]), Number(matched[2]) - 1, Number(matched[3]))
          }
        }
        return new Date(value)
      },
      dateKey(value) {
        const date = this.parseDate(value)
        if (!date || Number.isNaN(date.getTime())) return ''
        return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
      },
      formatShortDate(value) {
        const date = this.parseDate(value)
        if (!date || Number.isNaN(date.getTime())) return '--/--'
        return `${String(date.getMonth() + 1).padStart(2, '0')}/${String(date.getDate()).padStart(2, '0')}`
      },
      formatFullDate(value) {
        const date = this.parseDate(value)
        if (!date || Number.isNaN(date.getTime())) return '--'
        return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
      }
    }
  }
</script>

<style lang="scss" scoped>
  .sign-page { position: relative; min-height: 100vh; box-sizing: border-box; background: #F6F7F9; }
  .nav-back {
    width: 76rpx; height: 58rpx; display: flex; align-items: center; justify-content: center;
    border: 1rpx solid rgba(255,255,255,.55); border-radius: 100rpx; background: rgba(0,0,0,.14); color: #FFF;
  }
  .top-bg {
    position: absolute; top: 0; width: 100%; height: 580rpx;
    background: linear-gradient(120deg, #FEFDD3, #FAFCEF, #E5FDF5, #FEFBEA);
  }
  .top-bg::before { content: ''; position: absolute; inset: 0; background: linear-gradient(to bottom, transparent 35%, #F6F7F9); }
  .calendar-art { position: absolute; right: 55rpx; bottom: 135rpx; color: rgba(0,30,80,.15); font-size: 180rpx; transform: rotate(8deg); }
  .page-content { position: relative; z-index: 1; }
  .notice-box { margin: 0 30rpx 28rpx; padding: 4rpx; border-radius: 16rpx; background: rgba(0,30,80,.08); }
  .points-block { margin: 0 30rpx 30rpx; color: #001E50; }
  .points-value { font-size: 54rpx; font-weight: 700; }
  .points-unit { padding-left: 10rpx; color: #89949D; font-size: 22rpx; }
  .points-sub { padding-top: 12rpx; font-size: 24rpx; }
  .check-card, .record-card { position: relative; margin: 0 30rpx 28rpx; padding: 30rpx; border-radius: 22rpx; background: #FFF; }
  .week-line { position: absolute; top: 76rpx; left: 10%; right: 10%; height: 4rpx; background: #ECF0F9; }
  .week-grid { position: relative; z-index: 1; display: flex; justify-content: space-between; padding: 12rpx 0 28rpx; }
  .day-item { width: 14.285%; text-align: center; }
  .day-badge {
    display: flex; align-items: center; justify-content: center; width: 66rpx; height: 66rpx; margin: 0 auto 16rpx;
    border-radius: 50%; background: #ECF0F9; color: #6A767D; font-size: 23rpx;
  }
  .day-badge.checked { border: 1rpx solid #E5BC81; background: #F7EFE6; color: #C98A32; }
  .day-badge.today { box-shadow: 0 0 0 8rpx rgba(201,254,191,.55); }
  .day-date { color: #95A3A8; font-size: 20rpx; }
  .day-date--today { color: #111; font-weight: 700; }
  .reward-tip { margin-top: 28rpx; padding-bottom: 24rpx; border-bottom: 1rpx solid #F2F2F2; color: #95A3A8; font-size: 23rpx; }
  .rule-text { padding-top: 24rpx; color: #95A3A8; font-size: 23rpx; line-height: 1.75; }
  .record-title { padding-bottom: 12rpx; color: #222; font-size: 30rpx; font-weight: 700; }
  .record-row { padding: 24rpx 0; border-bottom: 1rpx solid #F1F1F1; }
  .record-row:last-child { border-bottom: none; }
  .record-date { color: #333; font-size: 26rpx; }
  .record-desc { padding-top: 8rpx; color: #AAA; font-size: 21rpx; }
  .record-points { color: #E5A142; font-size: 28rpx; font-weight: 700; }
  .empty-record { padding: 55rpx 0; color: #AAA; font-size: 23rpx; text-align: center; }
  .success-panel { width: 70vw; margin: 0 auto 12vh; padding: 50rpx 34rpx; border-radius: 24rpx; background: #FFF; text-align: center; }
  .success-icon { width: 100rpx; height: 100rpx; margin: 0 auto; border-radius: 50%; background: #111; color: #FFF; font-size: 50rpx; line-height: 100rpx; }
  .success-title { padding-top: 28rpx; color: #111; font-size: 36rpx; font-weight: 700; }
  .success-desc { padding-top: 22rpx; color: #666; font-size: 25rpx; }
  .success-desc text { padding: 0 6rpx; color: #F05A3C; font-weight: 700; }
  .success-tip { padding-top: 22rpx; color: #AAA; font-size: 22rpx; }
  .bottom-safe { height: calc(40rpx + env(safe-area-inset-bottom)); }
</style>
