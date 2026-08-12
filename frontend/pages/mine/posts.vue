<template>
  <view class="my-posts-page">
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#F6F6F6">
      <view slot="back" class="nav-back" @tap="goBack">
        <text class="tn-icon-left"></text>
      </view>
      <view class="nav-title">我的帖子</view>
    </tn-nav-bar>

    <view class="page-content" :style="{ paddingTop: vuex_custom_bar_height + 12 + 'px' }">
      <view class="post-list">
        <view class="card-header tn-flex tn-flex-row-between tn-flex-col-center">
          <view>
            <view class="card-title">我的帖子</view>
            <view class="card-subtitle">记录你在社区发布的内容</view>
          </view>
          <view class="refresh-action" @tap="refreshPosts">
            <text>刷新</text>
            <text class="tn-icon-refresh"></text>
          </view>
        </view>

        <view v-for="item in posts" :key="item.id" class="post-item" @tap="openPost(item.id)">
          <view class="post-main tn-flex tn-flex-row-between">
            <view class="post-copy">
              <view class="post-title tn-text-ellipsis">{{ item.title || '未命名帖子' }}</view>
              <view v-if="item.summary || item.content" class="post-summary tn-text-ellipsis">
                {{ item.summary || item.content }}
              </view>
            </view>
            <text class="tn-icon-right post-arrow"></text>
          </view>
          <view class="post-meta tn-flex tn-flex-row-between tn-flex-col-center">
            <view class="post-meta__left">
              <text>{{ item.circleName || '综合交流' }}</text>
              <text class="meta-dot">·</text>
              <text>{{ formatTime(item.createTime) }}</text>
            </view>
            <view class="delete-action" :class="{'delete-action--disabled': deletingId === item.id}"
              @tap.stop="confirmDelete(item)">
              <text class="tn-icon-delete"></text>
              <text>{{ deletingId === item.id ? '删除中' : '删除' }}</text>
            </view>
          </view>
          <view class="post-stats tn-flex tn-flex-col-center">
            <text class="tn-icon-eye"></text><text>{{ formatCount(item.viewCount) }}</text>
            <text class="tn-icon-like-lack"></text><text>{{ formatCount(item.likeCount) }}</text>
            <text class="tn-icon-message"></text><text>{{ formatCount(item.commentCount) }}</text>
          </view>
        </view>

        <view v-if="!loading && !posts.length" class="empty-state">
          <view class="empty-icon tn-icon-edit-form"></view>
          <view class="empty-title">还没有发布帖子</view>
          <view class="empty-desc">分享你的游戏心得，认识更多同好</view>
          <view class="publish-button" @tap="goPublish">发布第一篇帖子</view>
        </view>

        <view v-if="loading && !posts.length" class="loading-state">正在加载...</view>
        <tn-load-more v-else-if="posts.length" :status="loadStatus" @loadmore="loadPosts(false)"></tn-load-more>
      </view>
    </view>

    <view class="bottom-safe"></view>
  </view>
</template>

<script>
  import { user } from '@/api/index.js'
  import session from '@/utils/session.js'

  export default {
    data() {
      return {
        posts: [],
        page: 1,
        pageSize: 10,
        total: 0,
        hasMore: true,
        loading: false,
        loadStatus: 'loadmore',
        deletingId: null
      }
    },
    onLoad() {
      if (!session.isAccessTokenUsable()) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 500)
        return
      }
      this.loadPosts(true)
    },
    onReachBottom() {
      this.loadPosts(false)
    },
    async onPullDownRefresh() {
      try {
        await this.loadPosts(true)
      } finally {
        uni.stopPullDownRefresh()
      }
    },
    methods: {
      async loadPosts(reset = false) {
        if (this.loading || (!reset && !this.hasMore)) return
        if (reset) {
          this.page = 1
          this.total = 0
          this.hasMore = true
          this.loadStatus = 'loading'
        }
        this.loading = true
        try {
          const result = await user.getMyPosts({ page: this.page, pageSize: this.pageSize })
          const rows = (result && (result.rows || result.records || result.list)) || []
          this.total = Number(result && result.total) || rows.length
          this.posts = reset ? rows : this.posts.concat(rows)
          this.hasMore = this.posts.length < this.total
          this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
          if (this.hasMore) this.page++
        } catch (error) {
          this.loadStatus = 'loadmore'
        } finally {
          this.loading = false
        }
      },
      refreshPosts() {
        this.loadPosts(true)
      },
      openPost(id) {
        if (id) uni.navigateTo({ url: `/pages/post/detail?id=${id}` })
      },
      confirmDelete(item) {
        if (!item || !item.id || this.deletingId) return
        uni.showModal({
          title: '删除帖子',
          content: `确定删除“${item.title || '未命名帖子'}”吗？删除后无法恢复。`,
          confirmText: '删除',
          confirmColor: '#EA5E55',
          success: (result) => {
            if (result.confirm) this.deletePost(item.id)
          }
        })
      },
      async deletePost(postId) {
        this.deletingId = postId
        try {
          await user.deleteMyPost(postId)
          this.posts = this.posts.filter(item => item.id !== postId)
          this.total = Math.max(this.total - 1, 0)
          uni.$emit('forum-post-deleted', { postId })
          uni.showToast({ title: '已删除', icon: 'success' })
          if (!this.posts.length && this.total > 0) this.loadPosts(true)
        } finally {
          this.deletingId = null
        }
      },
      goPublish() {
        uni.navigateTo({ url: '/pages/post/publish' })
      },
      goBack() {
        uni.navigateBack({ delta: 1 })
      },
      formatCount(value) {
        const count = Number(value) || 0
        if (count >= 10000) return `${(count / 10000).toFixed(count >= 100000 ? 0 : 1)}万`
        return String(count)
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
  .my-posts-page { min-height: 100vh; background: #F6F6F6; }
  .nav-back {
    width: 76rpx; height: 56rpx; display: flex; align-items: center; justify-content: center;
    color: #111; font-size: 38rpx;
  }
  .nav-title { width: 100%; color: #111; font-size: 34rpx; font-weight: 700; text-align: center; }
  .page-content { padding-left: 24rpx; padding-right: 24rpx; }
  .post-list { padding-bottom: 20rpx; }
  .card-header { margin-bottom: 20rpx; padding: 30rpx 28rpx 26rpx; border-radius: 22rpx; background: #FFF; }
  .card-title { color: #111; font-size: 34rpx; font-weight: 700; }
  .card-subtitle { padding-top: 7rpx; color: #AAA; font-size: 23rpx; }
  .refresh-action { color: #AAA; font-size: 24rpx; }
  .refresh-action .tn-icon-refresh { margin-left: 8rpx; }
  .post-item { margin-bottom: 18rpx; padding: 26rpx 28rpx 22rpx; border-radius: 22rpx; background: #FFF; }
  .post-main { align-items: flex-start; }
  .post-copy { min-width: 0; flex: 1; padding-right: 20rpx; }
  .post-title { color: #222; font-size: 29rpx; font-weight: 700; }
  .post-summary { padding-top: 10rpx; color: #A2A2A2; font-size: 24rpx; }
  .post-arrow { padding-top: 8rpx; color: #B8B8B8; font-size: 30rpx; }
  .post-meta { padding-top: 18rpx; color: #B0B0B0; font-size: 21rpx; }
  .meta-dot { padding: 0 8rpx; }
  .delete-action { display: flex; align-items: center; padding: 8rpx 4rpx 8rpx 18rpx; color: #EA5E55; font-size: 22rpx; }
  .delete-action .tn-icon-delete { padding-right: 7rpx; font-size: 25rpx; }
  .delete-action--disabled { opacity: .45; }
  .post-stats { justify-content: flex-end; padding-top: 14rpx; color: #B0B0B0; font-size: 21rpx; }
  .post-stats text { margin-left: 8rpx; }
  .post-stats text[class^="tn-icon-"] { margin-left: 18rpx; }
  .empty-state { padding: 100rpx 30rpx 110rpx; text-align: center; }
  .empty-icon { color: #D0D0D0; font-size: 72rpx; }
  .empty-title { padding-top: 24rpx; color: #555; font-size: 28rpx; font-weight: 700; }
  .empty-desc { padding-top: 12rpx; color: #AAA; font-size: 23rpx; }
  .publish-button {
    display: inline-block; margin-top: 34rpx; padding: 18rpx 42rpx; border-radius: 100rpx;
    background: #111; color: #FFF; font-size: 25rpx;
  }
  .loading-state { padding: 100rpx 0; color: #AAA; font-size: 24rpx; text-align: center; }
  .bottom-safe { height: calc(50rpx + env(safe-area-inset-bottom)); }
</style>
