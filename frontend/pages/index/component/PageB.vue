<template>
  <view class="pagesB tn-safe-area-inset-bottom">
    <view class="page-gradient-bg"></view>
    <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F600">
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left">
        <view class="custom-nav__publish tn-icon-add-circle" @tap="openPublish"></view>
        <view class="custom-nav__tabs">
          <tn-tabs :list="scrollList" :current="current" @change="tabChange"
            activeColor="#000000" :bold="true" :fontSize="36"></tn-tabs>
        </view>
      </view>
    </tn-nav-bar>

    <view class="discover-content" :style="{ paddingTop: vuex_custom_bar_height + 10 + 'px' }">
      <view class="search-box tn-flex tn-flex-col-center">
        <text class="tn-icon-search search-box__icon"></text>
        <input v-model="keyword" class="search-box__input" confirm-type="search"
          :placeholder="searchPlaceholder" @confirm="handleSearch" />
        <text v-if="keyword" class="tn-icon-close search-box__clear" @tap="clearSearch"></text>
      </view>

      <!-- 圈子：保留原社区卡片、四宫格和圆角列表 -->
      <block v-if="current === 0">
        <swiper v-if="bannerCircles.length" class="circle-swiper" circular autoplay
          :interval="4200" :duration="500" indicator-dots indicator-color="rgba(255,255,255,.55)"
          indicator-active-color="#D3F427">
          <swiper-item v-for="circle in bannerCircles" :key="circle.id" @tap="openCircle(circle.id)">
            <view class="circle-banner">
              <image v-if="circle.coverUrl || circle.iconUrl" class="circle-banner__image"
                :src="circle.coverUrl || circle.iconUrl" mode="aspectFill"></image>
              <view v-else class="circle-banner__fallback"></view>
              <view class="circle-banner__shade"></view>
              <view class="circle-banner__content">
                <view class="circle-banner__name clamp-text-1">{{ circle.circleName }}</view>
                <view class="circle-banner__desc clamp-text-1">{{ circle.description || '欢迎加入圈子，一起分享与交流' }}</view>
                <view class="circle-banner__meta">
                  <text>{{ formatCount(circle.memberCount) }} 位成员</text>
                  <text class="circle-banner__dot">·</text>
                  <text>{{ formatCount(circle.postCount) }} 篇帖子</text>
                </view>
              </view>
              <view class="circle-banner__arrow"><text class="tn-icon-right"></text></view>
            </view>
          </swiper-item>
        </swiper>
        <view v-else-if="circleLoading" class="circle-swiper circle-swiper--loading"></view>

        <view class="section-title tn-flex tn-flex-row-between tn-flex-col-center">
          <view class="tn-text-bold tn-text-lg">热门圈子</view>
          <view class="tn-color-gray tn-text-sm">{{ filteredCircles.length }} 个圈子</view>
        </view>
        <view v-if="featuredCircles.length" class="circle-grid tn-flex tn-flex-wrap">
          <view v-for="circle in featuredCircles" :key="circle.id" class="circle-grid__item" @tap="openCircle(circle.id)">
            <view class="circle-grid__icon-wrap">
              <image v-if="circle.iconUrl || circle.coverUrl" class="circle-grid__image"
                :src="circle.iconUrl || circle.coverUrl" mode="aspectFill"></image>
              <view v-else class="circle-grid__fallback tn-icon-topics-fill"></view>
            </view>
            <view class="circle-grid__name clamp-text-1">{{ circle.circleName }}</view>
            <view class="circle-grid__count">{{ formatCount(circle.postCount) }}篇帖子</view>
          </view>
        </view>

        <view class="section-title"><view class="tn-text-bold tn-text-lg">全部圈子</view></view>
        <view class="circle-list">
          <view v-for="circle in filteredCircles" :key="circle.id" class="circle-card" @tap="openCircle(circle.id)">
            <image v-if="circle.coverUrl || circle.iconUrl" class="circle-card__cover"
              :src="circle.coverUrl || circle.iconUrl" mode="aspectFill"></image>
            <view v-else class="circle-card__cover circle-card__cover--fallback tn-icon-topics-fill"></view>
            <view class="circle-card__content">
              <view class="tn-flex tn-flex-row-between tn-flex-col-center">
                <view class="tn-text-bold tn-text-lg">{{ circle.circleName }}</view>
                <text class="tn-icon-right tn-color-gray"></text>
              </view>
              <view class="circle-card__desc clamp-text-2">{{ circle.description || '欢迎加入圈子参与讨论' }}</view>
              <view class="tn-color-gray tn-text-xs">
                {{ formatCount(circle.memberCount) }}位成员 · {{ formatCount(circle.postCount) }}篇帖子
              </view>
            </view>
          </view>
        </view>
        <view v-if="!filteredCircles.length && !circleLoading" class="empty-state">
          <text class="tn-icon-topics empty-state__icon"></text><view>没有找到相关圈子</view>
        </view>
      </block>

      <!-- 热榜：继续使用原来的纵向帖子卡片 -->
      <view v-else-if="current === 1" class="post-list">
        <view class="rank-tip tn-flex tn-flex-row-between tn-flex-col-center">
          <text class="tn-text-bold">社区热榜</text><text class="tn-color-gray tn-text-sm">综合点赞、评论和浏览</text>
        </view>
        <view v-for="(post, index) in posts" :key="post.id" class="post-card">
          <view class="post-card__author tn-flex tn-flex-row-between tn-flex-col-center">
            <view class="tn-flex tn-flex-col-center">
              <view class="rank-number" :class="{'rank-number--top': index < 3}">{{ index + 1 }}</view>
              <image class="author-avatar" :src="post.userAvatar" mode="aspectFill"></image>
              <view class="author-info"><view class="tn-text-bold clamp-text-1">{{ post.userName }}</view>
                <view class="tn-color-gray tn-text-xs tn-padding-top-xs">{{ post.date }}</view></view>
            </view>
            <picker v-if="!isOwnPost(post)" :range="postActions" @change="handlePostAction($event, post)">
              <text class="tn-icon-group-square tn-color-gray tn-text-xxl post-more"></text>
            </picker>
          </view>
          <view class="post-card__body" @tap="openPost(post.id)">
            <view class="post-title">{{ post.title }}</view>
            <view v-if="post.desc" class="post-summary clamp-text-2">{{ post.desc }}</view>
            <view v-if="post.mainImage.length" class="post-images" :class="{'post-images--single': post.mainImage.length === 1}">
              <image v-for="(image, imageIndex) in post.mainImage.slice(0, 3)" :key="imageIndex"
                class="post-images__item" :src="image" mode="aspectFill"></image>
            </view>
          </view>
          <view class="post-card__footer tn-flex tn-flex-row-between tn-flex-col-center">
            <view class="circle-tag" @tap="openCircle(post.circleId)"><text class="tn-icon-topics-fill"></text><text>{{ post.circleName }}</text></view>
            <view class="post-stats tn-color-gray"><text class="tn-icon-meteor"></text><text>{{ post.commentCount }}</text>
              <text class="tn-icon-rocket"></text><text>{{ post.likeCount }}</text></view>
          </view>
        </view>
        <view v-if="!posts.length && !loading" class="empty-state">
          <text class="tn-icon-game empty-state__icon"></text><view>{{ loadError ? '热榜加载失败，请稍后重试' : '暂时没有上榜内容' }}</view>
        </view>
        <tn-load-more v-else :status="loadStatus" @loadmore="fetchPostList(false)"></tn-load-more>
      </view>

      <!-- 活动：读取后端 forum_topic，不展示模板假数据 -->
      <view v-else class="topic-list">
        <view v-for="topic in filteredTopics" :key="topic.id" class="topic-card" @tap="openTopic(topic)">
          <image v-if="topic.coverUrl" class="topic-card__cover" :src="topic.coverUrl" mode="aspectFill"></image>
          <view v-else class="topic-card__cover topic-card__cover--fallback tn-icon-flag-fill"></view>
          <view class="topic-card__content">
            <view class="tn-text-bold tn-text-lg">{{ topic.topicName }}</view>
            <view class="topic-card__desc clamp-text-2">{{ topic.description || '参与社区话题，分享你的观点' }}</view>
            <view class="tn-flex tn-flex-row-between tn-color-gray tn-text-xs">
              <text>{{ topic.circleName || '全社区' }}</text><text>{{ topicTime(topic) }}</text>
            </view>
          </view>
        </view>
        <view v-if="!topicLoading && !filteredTopics.length" class="empty-state">
          <text class="tn-icon-flag empty-state__icon"></text><view>{{ keyword ? '没有找到相关活动' : '暂无进行中的社区活动' }}</view>
        </view>
      </view>
    </view>
    <view class="tn-tabbar-height"></view>
  </view>
</template>

<script>
  import { community } from '@/api/index.js'
  import session from '@/utils/session.js'

  const FALLBACK_AVATARS = [
    'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg',
    'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
    'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
    'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
  ]

  export default {
    name: 'PageB',
    props: { communityInfo: { type: Object, default: () => ({ communityName: '开源社区' }) } },
    data() {
      return {
        current: 0,
        scrollList: [{ name: '圈子' }, { name: '热榜' }, { name: '活动' }],
        keyword: '',
        circleList: [], circleLoading: false, activeCircleId: '',
        topics: [], topicLoading: false,
        posts: [], page: 1, pageSize: 10, hasMore: true, loading: false, loadError: false, loadStatus: 'loadmore',
        postActions: ['投诉举报', '屏蔽此人']
      }
    },
    computed: {
      communityName() { return this.communityInfo.communityName || '开源社区' },
      filteredCircles() {
        const keyword = this.keyword.trim().toLowerCase()
        return keyword ? this.circleList.filter(item => `${item.circleName || ''}${item.description || ''}`.toLowerCase().includes(keyword)) : this.circleList
      },
      featuredCircles() { return this.filteredCircles.slice(0, 4) },
      bannerCircles() { return this.filteredCircles.slice(0, 5) },
      filteredTopics() {
        const keyword = this.keyword.trim().toLowerCase()
        return keyword ? this.topics.filter(item => `${item.topicName || ''}${item.description || ''}`.toLowerCase().includes(keyword)) : this.topics
      },
      latestUserAvatar() { return this.posts.slice(0, 4).map(item => ({ src: item.userAvatar })) },
      communityMemberText() {
        const total = this.circleList.reduce((sum, item) => sum + (Number(item.memberCount) || 0), 0)
        return `${this.formatCount(total)} 人`
      },
      searchPlaceholder() {
        return this.current === 0 ? '搜索圈子' : (this.current === 1 ? '搜索热榜帖子' : '搜索活动')
      }
    },
    created() {
      this.fetchCircles(); this.fetchTopics(); this.fetchPostList(true)
      uni.$on('forum-post-published', this.handlePostPublished)
    },
    beforeDestroy() { uni.$off('forum-post-published', this.handlePostPublished) },
    methods: {
      handlePostPublished() { if (this.current === 1) this.fetchPostList(true) },
      async fetchCircles() {
        this.circleLoading = true
        try { const data = await community.getCircleList(); this.circleList = Array.isArray(data) ? data : [] }
        catch (error) { console.error('[PageB] 圈子列表加载失败:', error) }
        finally { this.circleLoading = false }
      },
      async fetchTopics() {
        this.topicLoading = true
        try { const data = await community.getTopicList(); this.topics = Array.isArray(data) ? data : [] }
        catch (error) { console.error('[PageB] 活动列表加载失败:', error) }
        finally { this.topicLoading = false }
      },
      async fetchPostList(reset = false) {
        if (this.loading || this.current !== 1 || (!reset && !this.hasMore)) return
        if (reset) { this.page = 1; this.hasMore = true; this.posts = []; this.loadError = false }
        this.loading = true; this.loadStatus = 'loading'
        try {
          const result = await community.getPostList({ page: this.page, pageSize: this.pageSize,
            circleId: this.activeCircleId || undefined, keyword: this.keyword.trim() || undefined, sort: 'HOT' })
          const rows = (result && result.rows) || []
          const adapted = rows.map(item => this.adaptPost(item))
          this.posts = reset ? adapted : this.posts.concat(adapted)
          this.hasMore = this.posts.length < (Number(result && result.total) || 0)
          this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
          if (this.hasMore) this.page++
        } catch (error) { this.loadError = this.posts.length === 0; this.loadStatus = 'loadmore' }
        finally { this.loading = false }
      },
      adaptPost(item) {
        const id = Number(item.id) || 0
        return { id: item.id, circleId: item.circleId, circleName: item.circleName || '综合交流', authorMemberId: item.authorMemberId,
          userAvatar: item.avatar || FALLBACK_AVATARS[id % FALLBACK_AVATARS.length],
          userName: item.username || `社区用户${item.authorMemberId || ''}`, date: this.formatTime(item.createTime),
          title: item.title || '社区动态', desc: item.summary || item.content || '',
          mainImage: Array.isArray(item.images) ? item.images : [], commentCount: Number(item.commentCount) || 0,
          likeCount: Number(item.likeCount) || 0 }
      },
      tabChange(index) {
        if (this.current === index) return
        this.current = index; this.keyword = ''; this.activeCircleId = ''
        if (index === 1) this.fetchPostList(true)
        else if (index === 2) this.fetchTopics()
      },
      handleSearch() { if (this.current === 1) this.fetchPostList(true) },
      clearSearch() { this.keyword = ''; if (this.current === 1) this.fetchPostList(true) },
      openCircle(circleId) { this.activeCircleId = circleId; this.current = 1; this.keyword = ''; this.fetchPostList(true) },
      openTopic(topic) { if (topic.circleId) this.openCircle(topic.circleId) },
      loadMore() { if (this.current === 1) this.fetchPostList(false) },
      openPost(id) { if (id) uni.navigateTo({ url: `/pages/post/detail?id=${id}` }) },
      openPublish() { uni.navigateTo({ url: '/pages/post/publish' }) },
      isOwnPost(post) {
        const currentMemberId = session.getUser().memberId
        return !!currentMemberId && String(currentMemberId) === String(post && post.authorMemberId)
      },
      handlePostAction(event, post) {
        const index = Number(event && event.detail && event.detail.value)
        if (index === 0) this.confirmReport(post)
        else if (index === 1) this.confirmBlock(post)
      },
      confirmReport(post) {
        if (!post || !post.id) return
        uni.showActionSheet({
          itemList: ['垃圾广告', '不友善内容', '违法违规', '其他原因'],
          success: async ({ tapIndex }) => {
            const reasons = ['SPAM', 'ABUSE', 'ILLEGAL', 'OTHER']
            try {
              await community.reportPost(post.id, { reasonCode: reasons[tapIndex] || 'OTHER' })
              uni.showToast({ title: '举报已提交', icon: 'success' })
            } catch (error) {}
          }
        })
      },
      confirmBlock(post) {
        if (!post || !post.authorMemberId) return
        uni.showModal({
          title: '屏蔽此人',
          content: `确定屏蔽“${post.userName || '该用户'}”吗？`,
          confirmText: '屏蔽',
          confirmColor: '#EA5E55',
          success: async ({ confirm }) => {
            if (!confirm) return
            try {
              await community.blockMember(post.authorMemberId)
              this.posts = this.posts.filter(item => item.authorMemberId !== post.authorMemberId)
              uni.showToast({ title: '已屏蔽', icon: 'success' })
            } catch (error) {}
          }
        })
      },
      formatTime(value) {
        if (!value) return '刚刚'; const time = new Date(value).getTime(); if (!time) return '刚刚'
        const diff = Math.max(Date.now() - time, 0)
        if (diff < 60000) return '刚刚'; if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
        if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`; if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
        const date = new Date(time); return `${date.getMonth() + 1}-${date.getDate()}`
      },
      topicTime(topic) {
        if (!topic.endAt) return '长期活动'; const date = new Date(topic.endAt); return `至 ${date.getMonth() + 1}-${date.getDate()}`
      },
      formatCount(value) { const count = Number(value) || 0; return count >= 10000 ? `${(count / 10000).toFixed(count >= 100000 ? 0 : 1)}万` : String(count) }
    }
  }
</script>

<style lang="scss" scoped>
  .pagesB { position: relative; min-height: 100%; box-sizing: border-box; background: #F6F6F6; }
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
  .custom-nav__publish { margin-left: 30rpx; font-size: 42rpx; }
  .custom-nav__tabs { width: 62vw; margin-top: 36rpx; overflow: hidden; }
  .discover-content { position: relative; z-index: 1; padding-bottom: 30rpx; }
  .search-box { margin: 14rpx 30rpx 20rpx; padding: 0 24rpx; height: 78rpx; border-radius: 40rpx; background: #FFF; }
  .search-box__icon, .search-box__clear { color: #888; font-size: 30rpx; }
  .search-box__input { flex: 1; height: 78rpx; padding: 0 18rpx; font-size: 27rpx; }
  .circle-swiper {
    height: 250rpx;
    margin: 20rpx 30rpx;
    overflow: hidden;
    border-radius: 26rpx;
    box-shadow: 0 12rpx 30rpx rgba(0,0,0,.10);
  }
  .circle-swiper--loading { background: linear-gradient(115deg, #F7FFE5, #DDF769); }
  .circle-banner { position: relative; width: 100%; height: 100%; overflow: hidden; border-radius: 26rpx; }
  .circle-banner__image, .circle-banner__fallback { width: 100%; height: 100%; }
  .circle-banner__fallback { background: linear-gradient(115deg, #20251A, #879A32); }
  .circle-banner__shade {
    position: absolute; inset: 0;
    background: linear-gradient(90deg, rgba(0,0,0,.68) 0%, rgba(0,0,0,.32) 58%, rgba(0,0,0,.08) 100%);
  }
  .circle-banner__content {
    position: absolute; z-index: 1; left: 28rpx; right: 100rpx; bottom: 42rpx; color: #FFF;
    text-shadow: 0 2rpx 8rpx rgba(0,0,0,.28);
  }
  .circle-banner__name { font-size: 34rpx; font-weight: 700; }
  .circle-banner__desc { padding-top: 9rpx; color: rgba(255,255,255,.88); font-size: 23rpx; }
  .circle-banner__meta { display: flex; padding-top: 12rpx; color: rgba(255,255,255,.82); font-size: 20rpx; }
  .circle-banner__dot { padding: 0 10rpx; }
  .circle-banner__arrow {
    position: absolute; z-index: 1; right: 24rpx; top: 50%; display: flex; align-items: center; justify-content: center;
    width: 54rpx; height: 54rpx; margin-top: -27rpx; border-radius: 50%; background: rgba(255,255,255,.78);
    color: #333; font-size: 23rpx;
  }
  .section-title { margin: 38rpx 30rpx 20rpx; }
  .circle-grid { padding: 0 16rpx 10rpx; }
  .circle-grid__item { width: 25%; padding: 12rpx 8rpx 22rpx; text-align: center; box-sizing: border-box; }
  .circle-grid__icon-wrap { width: 116rpx; height: 116rpx; margin: 0 auto; overflow: hidden; border: 6rpx solid #FFF; border-radius: 28rpx; box-shadow: 0 8rpx 24rpx rgba(0,0,0,.08); }
  .circle-grid__image, .circle-grid__fallback { width: 100%; height: 100%; }
  .circle-grid__fallback { background: #111; color: #D3F427; font-size: 44rpx; line-height: 116rpx; }
  .circle-grid__name { padding-top: 12rpx; color: #1D1F24; font-size: 24rpx; font-weight: 700; }
  .circle-grid__count { padding-top: 6rpx; color: #929292; font-size: 20rpx; }
  .circle-list, .topic-list { padding: 6rpx 24rpx 20rpx; }
  .circle-card, .topic-card { display: flex; margin-bottom: 18rpx; padding: 18rpx; border-radius: 22rpx; background: #FFF; }
  .circle-card__cover, .topic-card__cover { width: 150rpx; height: 150rpx; flex-shrink: 0; border-radius: 18rpx; }
  .circle-card__cover--fallback, .topic-card__cover--fallback { background: #111; color: #D3F427; font-size: 54rpx; line-height: 150rpx; text-align: center; }
  .circle-card__content, .topic-card__content { min-width: 0; flex: 1; padding: 8rpx 4rpx 4rpx 22rpx; }
  .circle-card__desc, .topic-card__desc { min-height: 66rpx; padding: 10rpx 0; color: #666; font-size: 24rpx; line-height: 1.45; }
  .rank-tip { margin: 28rpx 30rpx 8rpx; }
  .post-card { margin: 18rpx 24rpx; padding: 26rpx; border-radius: 22rpx; background: #FFF; }
  .post-card__author { margin-bottom: 20rpx; } .post-card__footer { padding-top: 20rpx; }
  .post-more { display: block; padding: 14rpx 4rpx 14rpx 24rpx; }
  .rank-number { width: 42rpx; color: #999; font-size: 28rpx; font-weight: 700; }
  .rank-number--top { color: #EA5E55; }
  .author-avatar { width: 70rpx; height: 70rpx; flex-shrink: 0; border-radius: 50%; background: #EEE; }
  .author-info { min-width: 0; padding-left: 18rpx; }
  .post-title { color: #1D1F24; font-size: 30rpx; font-weight: 700; line-height: 1.5; }
  .post-summary { padding-top: 12rpx; color: #555; font-size: 27rpx; line-height: 1.65; }
  .post-images { display: grid; grid-template-columns: repeat(3,1fr); gap: 10rpx; padding-top: 20rpx; }
  .post-images__item { width: 100%; height: 200rpx; border-radius: 14rpx; background: #EEE; }
  .post-images--single { display: block; } .post-images--single .post-images__item { width: 100%; height: 360rpx; }
  .circle-tag { max-width: 360rpx; padding: 7rpx 16rpx; overflow: hidden; border-radius: 100rpx; background: #F2F2F2; color: #222; font-size: 22rpx; text-overflow: ellipsis; white-space: nowrap; }
  .circle-tag text:first-child { padding-right: 8rpx; }
  .post-stats { display: flex; align-items: center; gap: 9rpx; color: #999; font-size: 24rpx; }
  .post-stats text:nth-child(3) { margin-left: 18rpx; }
  .empty-state { padding: 90rpx 30rpx; color: #999; text-align: center; }
  .empty-state__icon { display: block; padding-bottom: 20rpx; font-size: 150rpx; color: #D5D5D5; }
  .tn-tabbar-height { min-height: 160rpx; height: calc(160rpx + env(safe-area-inset-bottom)); background: #F6F6F6; }
  .clamp-text-1 { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .clamp-text-2 { display: -webkit-box; overflow: hidden; -webkit-box-orient: vertical; -webkit-line-clamp: 2; }
</style>
