<template>
  <view class="pagesD tn-safe-area-inset-bottom">
    <view class="page-gradient-bg"></view>
    <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F600">
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-between">
        <text class="tn-margin-left tn-text-bold tn-text-xl leaf-color">消 息</text>
        <text v-if="loggedIn && summary.totalUnread" class="read-all" @click.stop="markAllRead">全部已读</text>
      </view>
    </tn-nav-bar>

    <!-- 保留原模板的四分类入口，只将数量和筛选改为真实数据 -->
    <view class="tn-flex message-fixed" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
      <view v-for="item in categories" :key="item.value"
        class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius category-item"
        :class="{'category-active': activeCategory === item.value}"
        @click="selectCategory(item.value)">
        <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
          <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-bg-white tn-color-black">
            <view :class="item.icon">
              <tn-badge v-if="unreadOf(item)" backgroundColor="#EA5E55" fontColor="#FFFFFF"
                :absolute="true" :fontSize="16">
                <text>{{ badgeText(unreadOf(item)) }}</text>
              </tn-badge>
            </view>
          </view>
          <view class="tn-color-gray--dark tn-text-center">
            <text class="tn-text-ellipsis">{{ item.label }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="message-list tn-margin-top-sm" :style="{paddingTop: vuex_custom_bar_height + 95 + 'px'}">
      <view v-if="!loggedIn" class="empty-card tn-flex tn-flex-direction-column tn-flex-col-center">
        <view class="empty-icon tn-icon-message-fill"></view>
        <text class="tn-text-lg tn-text-bold">登录后查看消息</text>
        <text class="tn-color-gray tn-margin-top-xs">点赞、评论和系统通知都会出现在这里</text>
        <view class="login-button tn-margin-top-lg" @click="goLogin">去登录</view>
      </view>

      <block v-else>
        <view class="message-bg tn-flex tn-flex-col-center" v-for="item in messages" :key="item.id"
          :class="{'message-unread': !item.read}" @click="openNotification(item)">
          <view>
            <view v-if="item.senderAvatar" class="logo-pic">
              <image class="logo-image" :src="item.senderAvatar" mode="aspectFill"></image>
            </view>
            <view v-else class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-color-white"
              :class="categoryMeta(item.category).background">
              <view :class="categoryMeta(item.category).icon"></view>
            </view>
          </view>
          <view class="tn-padding-left-sm message-main">
            <view class="tn-flex tn-flex-row-between tn-flex-col-between">
              <text class="tn-text-lg tn-text-ellipsis message-title">{{ item.title || categoryMeta(item.category).title }}</text>
            </view>
            <view class="tn-padding-top-xs tn-text-ellipsis">
              <text class="tn-color-gray tn-text-sm">{{ item.content || '你收到了一条新消息' }}</text>
            </view>
          </view>
          <view class="message-side">
            <view class="tn-flex tn-flex-row-right tn-margin-bottom-xs tn-margin-top-xs">
              <text class="tn-color-gray tn-text-sm">{{ formatTime(item.createdAt) }}</text>
            </view>
            <view v-if="!item.read" class="tn-flex tn-flex-row-right tn-margin-top-xs">
              <view class="unread-dot"></view>
            </view>
          </view>
        </view>

        <view v-if="!loading && !messages.length" class="empty-card tn-flex tn-flex-direction-column tn-flex-col-center">
          <view class="empty-icon tn-icon-message"></view>
          <text class="tn-text-lg tn-text-bold">暂无{{ activeCategory ? activeLabel : '' }}消息</text>
          <text class="tn-color-gray tn-margin-top-xs">有新动态时会第一时间告诉你</text>
        </view>
        <view v-if="loading" class="list-tip">正在加载...</view>
        <view v-else-if="messages.length && !hasMore" class="list-tip">— 没有更多了 —</view>
      </block>
    </view>

    <view class="tn-tabbar-height"></view>
  </view>
</template>

<script>
  import { message as messageApi } from '@/api/index.js'
  import session from '@/utils/session.js'

  export default {
    name: 'PageD',
    data() {
      return {
        loggedIn: false,
        activeCategory: '',
        messages: [],
        page: 1,
        pageSize: 20,
        hasMore: true,
        loading: false,
        summary: {
          totalUnread: 0,
          interactionUnread: 0,
          likeUnread: 0,
          activityUnread: 0,
          systemUnread: 0
        },
        categories: [
          { value: 'INTERACTION', label: '互 动', icon: 'tn-icon-topics-fill', unreadKey: 'interactionUnread' },
          { value: 'LIKE', label: '点 赞', icon: 'tn-icon-like-lack-fill', unreadKey: 'likeUnread' },
          { value: 'ACTIVITY', label: '活 动', icon: 'tn-icon-medal', unreadKey: 'activityUnread' },
          { value: 'SYSTEM', label: '系 统', icon: 'tn-icon-notice-fill', unreadKey: 'systemUnread' }
        ]
      }
    },
    computed: {
      activeLabel() {
        const current = this.categories.find(item => item.value === this.activeCategory)
        return current ? current.label.replace(/\s/g, '') : ''
      }
    },
    created() {
      this.refresh()
    },
    methods: {
      async refresh() {
        this.loggedIn = session.isAccessTokenUsable()
        if (!this.loggedIn) {
          this.messages = []
          return
        }
        this.page = 1
        this.hasMore = true
        await Promise.all([this.loadSummary(), this.loadMessages(true)])
      },
      async loadSummary() {
        try {
          const result = await messageApi.getSummary()
          if (result) this.summary = Object.assign({}, this.summary, result)
        } catch (error) {
          console.error('[Message] 未读消息汇总加载失败:', error)
        }
      },
      async loadMessages(reset = false) {
        if (!this.loggedIn || this.loading || (!reset && !this.hasMore)) return
        this.loading = true
        try {
          const result = await messageApi.getList({
            category: this.activeCategory || undefined,
            page: reset ? 1 : this.page,
            pageSize: this.pageSize
          })
          const records = result && Array.isArray(result.records) ? result.records : []
          this.messages = reset ? records : this.messages.concat(records)
          const currentPage = Number(result && result.page) || (reset ? 1 : this.page)
          const total = Number(result && result.total) || 0
          this.page = currentPage + 1
          this.hasMore = this.messages.length < total
        } catch (error) {
          console.error('[Message] 消息列表加载失败:', error)
        } finally {
          this.loading = false
        }
      },
      loadMore() {
        this.loadMessages(false)
      },
      selectCategory(category) {
        this.activeCategory = this.activeCategory === category ? '' : category
        if (this.loggedIn) {
          this.page = 1
          this.hasMore = true
          this.loadMessages(true)
        }
      },
      async openNotification(item) {
        if (!item.read) {
          try {
            await messageApi.markRead(item.id)
            this.$set(item, 'read', true)
            this.decreaseUnread(item.category)
          } catch (error) {
            return
          }
        }
        if (item.bizType === 'POST' && item.bizId) {
          uni.navigateTo({ url: `/pages/post/detail?id=${item.bizId}` })
        }
      },
      async markAllRead() {
        try {
          await messageApi.markAllRead(this.activeCategory)
          this.messages.forEach(item => { item.read = true })
          await this.loadSummary()
          uni.showToast({ title: '已全部标为已读', icon: 'none' })
        } catch (error) {}
      },
      decreaseUnread(category) {
        const current = this.categories.find(item => item.value === category)
        if (current) this.summary[current.unreadKey] = Math.max(Number(this.summary[current.unreadKey]) - 1, 0)
        this.summary.totalUnread = Math.max(Number(this.summary.totalUnread) - 1, 0)
      },
      unreadOf(item) {
        return Number(this.summary[item.unreadKey]) || 0
      },
      badgeText(value) {
        return Number(value) > 99 ? '99+' : String(value)
      },
      categoryMeta(category) {
        const map = {
          INTERACTION: { title: '互动消息', icon: 'tn-icon-topics-fill', background: 'tn-bg-cyan' },
          LIKE: { title: '点赞消息', icon: 'tn-icon-like-fill', background: 'tn-bg-orangered' },
          ACTIVITY: { title: '活动消息', icon: 'tn-icon-medal', background: 'tn-bg-orange' },
          SYSTEM: { title: '系统消息', icon: 'tn-icon-notice-fill', background: 'tn-bg-blue' }
        }
        return map[category] || map.SYSTEM
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
      },
      goLogin() {
        uni.navigateTo({ url: '/pages/login/login' })
      }
    }
  }
</script>

<style lang="scss" scoped>
  .pagesD { position: relative; min-height: 100vh; background: #F6F6F6; }
  .page-gradient-bg {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 450rpx;
    z-index: 0;
    pointer-events: none;
    background: linear-gradient(90deg, #c9febf, #F6F6F6);
  }
  .page-gradient-bg::before {
    content: "";
    position: absolute;
    inset: 0;
    background: linear-gradient(to bottom, transparent 28%, #F6F6F6 100%);
  }
  .custom-nav { width: 100%; height: 100%; }
  .read-all { margin-right: 30rpx; color: #777; font-size: 24rpx; }
  .message-fixed {
    position: fixed;
    background: transparent;
    top: 0;
    width: 100%;
    transition: all 0.25s ease-out;
    z-index: 100;
  }
  .category-item { opacity: .72; transition: opacity .2s, transform .2s; }
  .category-active { opacity: 1; transform: translateY(-4rpx); }
  .message-list { position: relative; z-index: 1; min-height: 500rpx; }
  .message-bg {
    background-color: #FFFFFF;
    border-radius: 18rpx;
    margin: 20rpx 30rpx;
    padding: 20rpx;
    position: relative;
  }
  .message-unread { box-shadow: 0 8rpx 28rpx rgba(0, 0, 0, .035); }
  .message-main { width: 58vw; min-width: 0; }
  .message-title { display: block; max-width: 100%; }
  .message-side { width: 19vw; }
  .unread-dot { width: 14rpx; height: 14rpx; border-radius: 50%; background: #EA5E55; }
  .empty-card {
    background: #FFFFFF;
    border-radius: 18rpx;
    margin: 20rpx 30rpx;
    padding: 70rpx 30rpx;
    text-align: center;
  }
  .empty-icon { font-size: 72rpx; color: #C8C8C8; margin-bottom: 22rpx; }
  .login-button { min-width: 220rpx; padding: 18rpx 42rpx; border-radius: 100rpx; color: #FFF; background: #111; }
  .list-tip { color: #A8A8A8; font-size: 24rpx; text-align: center; padding: 24rpx 0; }
  .icon1__item--icon {
    width: 100rpx;
    height: 100rpx;
    font-size: 60rpx;
    border-radius: 50%;
    margin-bottom: 18rpx;
    position: relative;
    z-index: 1;
    &::after {
      content: " "; position: absolute; z-index: -1; width: 100%; height: 100%; left: 0; bottom: 0;
      border-radius: inherit; background-size: 100% 100%;
      background-image: url(https://resource.tuniaokj.com/images/cool_bg_image/icon_bg5.png);
    }
  }
  .icon15__item--icon { width: 90rpx; height: 90rpx; font-size: 54rpx; border-radius: 18rpx; }
  .logo-image { width: 90rpx; height: 90rpx; display: block; }
  .logo-pic { border-radius: 18rpx; overflow: hidden; }
  .tn-tabbar-height {
    min-height: 120rpx;
    height: calc(140rpx + env(safe-area-inset-bottom));
    height: calc(140rpx + constant(safe-area-inset-bottom));
  }
</style>
