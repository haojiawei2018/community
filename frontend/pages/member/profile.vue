<template>
  <view class="member-page tn-safe-area-inset-bottom">
    <view class="member-gradient"></view>
    <view class="nav-capsule" :style="{ top: vuex_status_bar_height + 7 + 'px' }">
      <view class="nav-capsule__item" @tap="goBack"><text class="tn-icon-left"></text></view>
      <view class="nav-capsule__line"></view>
      <view class="nav-capsule__item" @tap="goHome"><text class="tn-icon-home-capsule-fill"></text></view>
    </view>

    <view class="page-content" :style="{ paddingTop: vuex_status_bar_height + 68 + 'px' }">
      <view v-if="profile" class="profile-header">
        <image class="profile-avatar" :src="profile.avatarUrl || defaultAvatar" mode="aspectFill"></image>
        <view class="profile-name">{{ profile.displayName || '社区用户' }}</view>
        <view class="profile-bio">{{ profile.bio || '这个人很低调，还没有填写简介' }}</view>
        <view class="profile-stats tn-flex tn-flex-row-center">
          <view class="profile-stat"><view class="profile-stat__value">{{ formatCount(profile.postCount) }}</view><view class="profile-stat__label">帖子</view></view>
          <view class="profile-stat__line"></view>
          <view class="profile-stat"><view class="profile-stat__value">{{ formatCount(profile.receivedLikeCount) }}</view><view class="profile-stat__label">获赞</view></view>
        </view>
      </view>
      <view v-else-if="loadingProfile" class="profile-header profile-loading">正在加载用户资料...</view>
      <view v-else-if="profileError" class="profile-header profile-loading">
        <view>{{ profileError }}</view>
        <tn-button class="tn-margin-top" size="sm" shape="round" backgroundColor="#000000"
          fontColor="#FFFFFF" @tap="refresh">重新加载</tn-button>
      </view>

      <view class="section-title tn-flex tn-flex-row-between tn-flex-col-center">
        <text class="tn-text-lg tn-text-bold">TA 的帖子</text>
        <text class="tn-color-gray tn-text-sm">{{ total }} 篇</text>
      </view>

      <view v-for="post in posts" :key="post.id" class="post-card" @tap="openPost(post.id)">
        <view class="post-title">{{ post.title || '未命名帖子' }}</view>
        <view v-if="post.summary || post.content" class="post-summary clamp-text-2">{{ post.summary || post.content }}</view>
        <view v-if="post.images && post.images.length" class="post-images" :class="{'post-images--single': post.images.length === 1}">
          <image v-for="(image, index) in post.images.slice(0, 3)" :key="index" class="post-image" :src="image" mode="aspectFill"></image>
        </view>
        <view class="post-meta tn-flex tn-flex-row-between tn-flex-col-center">
          <view><text>{{ post.circleName || '综合交流' }}</text><text class="meta-dot">·</text><text>{{ formatTime(post.createTime) }}</text></view>
          <view class="post-stats"><text class="tn-icon-eye"></text><text>{{ formatCount(post.viewCount) }}</text><text class="tn-icon-like-lack"></text><text>{{ formatCount(post.likeCount) }}</text><text class="tn-icon-message"></text><text>{{ formatCount(post.commentCount) }}</text></view>
        </view>
      </view>

      <view v-if="!loadingPosts && !posts.length" class="empty-state">
        <text class="tn-icon-edit-form empty-icon"></text>
        <view>TA 还没有发布帖子</view>
      </view>
      <view v-if="loadingPosts && !posts.length" class="loading-state">正在加载...</view>
      <tn-load-more v-else-if="posts.length" :status="loadStatus" @loadmore="loadPosts(false)"></tn-load-more>
    </view>
    <view class="bottom-safe"></view>
  </view>
</template>

<script>
  import { community } from '@/api/index.js'
  import { DEFAULT_AVATAR_URL } from '@/config/defaults.js'

  export default {
    name: 'MemberProfile',
    data() {
      return {
        memberId: '',
        profile: null,
        posts: [],
        page: 1,
        pageSize: 10,
        total: 0,
        hasMore: true,
        loadingProfile: false,
        profileError: '',
        loadingPosts: false,
        loadStatus: 'loadmore',
        defaultAvatar: DEFAULT_AVATAR_URL
      }
    },
    onLoad(options) {
      this.memberId = options.memberId || ''
      if (!this.memberId) {
        uni.showToast({ title: '用户信息不存在', icon: 'none' })
        return
      }
      this.refresh()
    },
    onReachBottom() { this.loadPosts(false) },
    async onPullDownRefresh() {
      try { await this.refresh() } finally { uni.stopPullDownRefresh() }
    },
    methods: {
      async refresh() {
        await Promise.all([this.loadProfile(), this.loadPosts(true)])
      },
      async loadProfile() {
        this.loadingProfile = true
        this.profileError = ''
        try { this.profile = await community.getMemberProfile(this.memberId) }
        catch (error) { this.profileError = '用户资料加载失败，请稍后重试' }
        finally { this.loadingProfile = false }
      },
      async loadPosts(reset = false) {
        if (this.loadingPosts || (!reset && !this.hasMore)) return
        if (reset) { this.page = 1; this.posts = []; this.total = 0; this.hasMore = true }
        this.loadingPosts = true
        this.loadStatus = 'loading'
        try {
          const result = await community.getMemberPosts(this.memberId, { page: this.page, pageSize: this.pageSize })
          const rows = (result && result.rows) || []
          this.total = Number(result && result.total) || rows.length
          this.posts = reset ? rows : this.posts.concat(rows)
          this.hasMore = this.posts.length < this.total
          this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
          if (this.hasMore) this.page++
        } catch (error) { this.loadStatus = 'loadmore' }
        finally { this.loadingPosts = false }
      },
      openPost(id) { if (id) uni.navigateTo({ url: `/pages/post/detail?id=${id}` }) },
      goBack() { uni.navigateBack({ delta: 1 }) },
      goHome() { uni.reLaunch({ url: '/pages/index/index' }) },
      formatCount(value) {
        const count = Number(value) || 0
        return count >= 10000 ? `${(count / 10000).toFixed(count >= 100000 ? 0 : 1)}万` : String(count)
      },
      formatTime(value) {
        if (!value) return '刚刚'
        const time = new Date(value).getTime()
        if (!time) return '刚刚'
        const diff = Math.max(Date.now() - time, 0)
        if (diff < 60000) return '刚刚'
        if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
        if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
        if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
        const date = new Date(time)
        return `${date.getMonth() + 1}-${date.getDate()}`
      }
    }
  }
</script>

<style lang="scss" scoped>
  page { background: #F6F6F6; }
  .member-page { position: relative; min-height: 100vh; background: #F6F6F6; }
  .member-gradient { position: fixed; top: 0; left: 0; width: 100%; height: 500rpx; background: linear-gradient(90deg, #c9febf, #F6F6F6); }
  .member-gradient::before { content: ''; position: absolute; inset: 0; background: linear-gradient(to bottom, transparent 32%, #F6F6F6 100%); }
  .nav-capsule {
    position: fixed; z-index: 999; left: 24rpx; display: flex; align-items: center; justify-content: space-around;
    width: 150rpx; height: 58rpx; overflow: hidden; box-sizing: border-box; color: #FFF; font-size: 31rpx;
    border: 1rpx solid rgba(255,255,255,.55); border-radius: 100rpx; background: rgba(0,0,0,.52);
  }
  .nav-capsule__item { display: flex; flex: 1; align-items: center; justify-content: center; height: 100%; }
  .nav-capsule__line { width: 1rpx; height: 30rpx; background: rgba(255,255,255,.55); }
  .page-content { position: relative; z-index: 1; padding-left: 24rpx; padding-right: 24rpx; }
  .profile-header { padding: 40rpx 30rpx 34rpx; text-align: center; }
  .profile-avatar { width: 150rpx; height: 150rpx; border: 8rpx solid #FFF; border-radius: 50%; background: #EEE; box-shadow: 0 10rpx 28rpx rgba(0,0,0,.10); }
  .profile-name { padding-top: 18rpx; color: #171717; font-size: 36rpx; font-weight: 700; }
  .profile-bio { max-width: 560rpx; margin: 0 auto; padding-top: 10rpx; color: #858585; font-size: 24rpx; line-height: 1.5; }
  .profile-stats { padding-top: 26rpx; }
  .profile-stat { min-width: 140rpx; text-align: center; }
  .profile-stat__value { color: #111; font-size: 30rpx; font-weight: 700; }
  .profile-stat__label { padding-top: 4rpx; color: #999; font-size: 21rpx; }
  .profile-stat__line { width: 1rpx; height: 44rpx; margin: 0 24rpx; background: rgba(0,0,0,.10); }
  .profile-loading, .loading-state { padding: 110rpx 0; color: #999; font-size: 24rpx; text-align: center; }
  .section-title { padding: 20rpx 8rpx 18rpx; }
  .post-card { margin-bottom: 18rpx; padding: 26rpx; border-radius: 22rpx; background: #FFF; }
  .post-title { color: #222; font-size: 29rpx; font-weight: 700; line-height: 1.5; }
  .post-summary { padding-top: 10rpx; color: #777; font-size: 24rpx; line-height: 1.55; }
  .post-images { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8rpx; padding-top: 18rpx; }
  .post-image { width: 100%; height: 180rpx; border-radius: 12rpx; background: #EEE; }
  .post-images--single { display: block; }
  .post-images--single .post-image { height: 320rpx; }
  .post-meta { padding-top: 18rpx; color: #AAA; font-size: 21rpx; }
  .meta-dot { padding: 0 8rpx; }
  .post-stats { display: flex; align-items: center; gap: 7rpx; }
  .post-stats text:nth-child(3), .post-stats text:nth-child(5) { margin-left: 12rpx; }
  .empty-state { padding: 110rpx 0; color: #999; text-align: center; }
  .empty-icon { display: block; padding-bottom: 20rpx; color: #D0D0D0; font-size: 80rpx; }
  .bottom-safe { height: calc(50rpx + env(safe-area-inset-bottom)); }
  .clamp-text-2 { display: -webkit-box; overflow: hidden; -webkit-box-orient: vertical; -webkit-line-clamp: 2; }
</style>
