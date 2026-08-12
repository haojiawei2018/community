<template>
  <view class="template-details tn-safe-area-inset-bottom">
    <!-- 恢复模板原有的返回 / 首页双按钮导航 -->
    <tn-nav-bar fixed alpha customBack :bottomShadow="false">
      <view slot="back" class="nav-capsule">
        <view class="nav-capsule__item" @tap="goBack">
          <text class="tn-icon-left"></text>
        </view>
        <view class="nav-capsule__line"></view>
        <view class="nav-capsule__item" @tap="goHome">
          <text class="tn-icon-home-capsule-fill"></text>
        </view>
      </view>
    </tn-nav-bar>

    <scroll-view scroll-y class="detail-scroll" enable-back-to-top>
      <view class="detail-content" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
        <!-- 模板式帖子主体 -->
        <view v-if="postDetail" class="blogger__item">
          <view class="blogger__author tn-flex tn-flex-row-between tn-flex-col-center">
            <view class="tn-flex tn-flex-col-center">
              <view class="avatar avatar--author" :style="authorAvatarStyle" @tap="openMember(postDetail.authorMemberId)"></view>
              <view class="author-info">
                <view class="author-info__name">{{ postDetail.userName || '匿名用户' }}</view>
                <view class="author-info__meta">{{ postDetail.circleName || '社区玩家' }}</view>
              </view>
            </view>
            <text class="tn-icon-more-vertical blogger__more"></text>
          </view>

          <view class="blogger__body">
            <view v-if="postDetail.tags && postDetail.tags.length" class="post-tags">
              <view v-for="(tag, idx) in postDetail.tags" :key="idx" class="post-tag">
                <text class="tn-icon-spiral"></text>
                <text>{{ tag }}</text>
              </view>
            </view>

            <view v-if="postDetail.title" class="post-title">{{ postDetail.title }}</view>
            <view class="post-content">{{ postDetail.content }}</view>

            <!-- 延续原模板 1/2/4/多图布局 -->
            <view v-if="postDetail.images && postDetail.images.length" class="post-images">
              <image
                v-for="(img, idx) in postDetail.images"
                :key="idx"
                :src="img"
                mode="aspectFill"
                class="post-image"
                :class="{
                  'post-image--one': postDetail.images.length === 1,
                  'post-image--double': postDetail.images.length === 2 || postDetail.images.length === 4,
                  'post-image--grid': [1, 2, 4].indexOf(postDetail.images.length) === -1
                }"
                @tap="previewImage(idx)"
              ></image>
            </view>

            <view class="post-meta tn-flex tn-flex-row-between tn-flex-col-center">
              <text>{{ formatTime(postDetail.createTime) }}</text>
              <view class="post-meta__actions">
                <text class="tn-icon-message"></text>
                <text>{{ commentTotal }}</text>
                <view class="like-action" @tap="handleLike">
                  <text :class="isLiked ? 'tn-icon-like-fill like-action--active' : 'tn-icon-like-lack'"></text>
                  <text :class="{ 'like-action--active': isLiked }">{{ likeCount }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-if="!postDetail && loadingDetail" class="detail-loading">
          <view class="skeleton skeleton--short"></view>
          <view class="skeleton"></view>
          <view class="skeleton"></view>
        </view>

        <view class="section-strip"></view>

        <!-- 模板式评论区域，数据改为真实接口 -->
        <view class="comment-section">
          <view class="comment-header tn-flex tn-flex-row-between tn-flex-col-center">
            <text class="comment-header__title">全部 {{ commentTotal }} 条评论</text>
            <text class="comment-header__action" @tap="focusComment">写评论 <text class="tn-icon-edit-write"></text></text>
          </view>

          <view v-for="(item, index) in commentList" :key="item.id || index" class="comment-item">
            <view class="tn-flex tn-flex-row-between tn-flex-col-center">
              <view class="tn-flex tn-flex-col-center">
                <view class="avatar avatar--comment" :style="commentAvatarStyle(item)" @tap="openMember(item.authorMemberId)"></view>
                <view class="comment-author">
                  <view class="comment-author__name">{{ item.userName || '匿名用户' }}</view>
                  <view class="comment-author__meta">社区玩家</view>
                </view>
              </view>
              <text class="tn-icon-more-vertical comment-more"></text>
            </view>
            <view class="comment-body">{{ item.content }}</view>
            <view class="comment-meta">
              <text>{{ formatTime(item.createTime) }}</text>
              <text class="comment-meta__reply">回复</text>
            </view>
          </view>

          <view v-if="!commentList.length && !loadingComment" class="comment-empty">
            暂无评论，快来抢沙发
          </view>
          <view v-if="loadingComment" class="comment-loading">评论加载中...</view>
          <view v-if="commentList.length < commentTotal" class="load-more" @tap="loadMoreComments">
            点击加载更多
          </view>
        </view>

        <view class="bottom-placeholder"></view>
      </view>
    </scroll-view>

    <!-- 保留真实评论接口的底部互动栏 -->
    <view class="comment-footer">
      <input
        class="comment-input"
        type="text"
        v-model="commentText"
        :focus="commentFocus"
        placeholder="请文明发言"
        placeholder-class="comment-placeholder"
        confirm-type="send"
        @blur="commentFocus = false"
        @confirm="handleSubmitComment"
      />
      <view class="footer-like" @tap="handleLike">
        <text :class="isLiked ? 'tn-icon-like-fill footer-like--active' : 'tn-icon-like-lack'"></text>
        <text>{{ likeCount }}</text>
      </view>
      <view
        class="send-btn"
        :class="{ 'send-btn--disabled': !commentText.trim() || submitting }"
        @tap="handleSubmitComment"
      >{{ submitting ? '发送中' : '发送' }}</view>
    </view>
  </view>
</template>

<script>
  import { community } from '@/api/index.js'

  // 日志前缀，便于在控制台过滤
  const LOG_TAG = '[PostDetail]'

  export default {
    name: 'postDetail',
    data() {
      return {
        // 帖子ID（由路由参数传入）
        postId: '',
        // 帖子详情
        postDetail: null,
        // 详情加载中
        loadingDetail: false,
        // 评论列表
        commentList: [],
        // 评论总数
        commentTotal: 0,
        // 评论分页
        commentPage: {
          page: 1,
          pageSize: 10
        },
        // 评论加载中
        loadingComment: false,
        // 点赞状态
        isLiked: false,
        // 点赞数
        likeCount: 0,
        // 评论输入内容
        commentText: '',
        // 是否聚焦底部评论框
        commentFocus: false,
        // 评论提交中
        submitting: false
      }
    },
    computed: {
      // 作者头像样式：无图时用兜底（按id取模稳定）
      authorAvatarStyle() {
        const fallbackAvatars = [
          'https://resource.tuniaokj.com/images/flower/guye1.jpg',
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603396-assets/web-upload/a7f7dd1d-3618-4888-a20c-6b55a6aa69a4.jpeg',
          'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711183180435-assets/web-upload/a091b9f2-c587-412d-96c4-adbedbb0a889.jpeg',
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603532-assets/web-upload/c8f3f24f-789c-474f-9fdf-831bedb7833c.jpeg'
        ]
        const detail = this.postDetail || {}
        let url = detail.userImage
        if (!url) {
          const id = Number(detail.id || detail.userId || this.postId || 0) || 0
          url = fallbackAvatars[id % fallbackAvatars.length]
        }
        return `background-image:url('${url}');width: 80rpx;height: 80rpx;background-size: cover;border-radius: 50%;`
      }
    },
    onLoad(options) {
      console.log(LOG_TAG, 'onLoad options:', options)
      this.postId = options.id || options.postId || ''
      if (!this.postId) {
        console.warn(LOG_TAG, '缺少帖子ID参数，无法加载详情')
        uni.showToast({ title: '参数错误', icon: 'none' })
        setTimeout(() => this.goBack(), 1000)
        return
      }
      console.log(LOG_TAG, '当前帖子ID:', this.postId)
      this.initData()
    },
    methods: {
      openMember(memberId) {
        if (!memberId) return
        uni.navigateTo({ url: `/pages/member/profile?memberId=${memberId}` })
      },
      // 返回上一页
      goBack() {
        uni.navigateBack({ delta: 1 })
      },

      goHome() {
        uni.reLaunch({ url: '/pages/index/index' })
      },

      focusComment() {
        this.commentFocus = false
        this.$nextTick(() => {
          this.commentFocus = true
        })
      },

      // 评论项头像样式：无图时用兜底（按评论id取模稳定）
      commentAvatarStyle(item) {
        const fallbackAvatars = [
          'https://resource.tuniaokj.com/images/flower/guye1.jpg',
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603396-assets/web-upload/a7f7dd1d-3618-4888-a20c-6b55a6aa69a4.jpeg',
          'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711183180435-assets/web-upload/a091b9f2-c587-412d-96c4-adbedbb0a889.jpeg',
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603532-assets/web-upload/c8f3f24f-789c-474f-9fdf-831bedb7833c.jpeg'
        ]
        let url = item && item.userImage
        if (!url) {
          const id = Number((item && (item.id || item.userId)) || 0) || 0
          url = fallbackAvatars[id % fallbackAvatars.length]
        }
        return `background-image:url('${url}');width: 60rpx;height: 60rpx;background-size: cover;border-radius: 50%;`
      },

      // 时间格式化（单一入口，兼容毫秒/秒时间戳、ISO字符串、已有格式化字符串）
      // 输出: YYYY-MM-DD HH:mm:ss
      formatTime(time) {
        if (time === null || time === undefined || time === '') return ''
        let ts = time
        // 纯数字字符串转数字
        if (typeof ts === 'string' && /^\d+$/.test(ts)) ts = Number(ts)
        // 数字：10位按秒，13位按毫秒
        if (typeof ts === 'number') {
          const len = String(ts).length
          if (len === 10) ts = ts * 1000
          // 非 10/13 位的纯数字，尝试直接当毫秒
        }
        try {
          const d = new Date(ts)
          if (isNaN(d.getTime())) return String(time)
          const pad = (n) => String(n).padStart(2, '0')
          return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
        } catch (e) {
          return String(time)
        }
      },

      // 初始化数据：并行加载详情和评论
      initData() {
        this.fetchPostDetail()
        this.fetchCommentList(true)
      },

      // 获取帖子详情
      async fetchPostDetail() {
        this.loadingDetail = true
        console.log(LOG_TAG, '>>> 请求帖子详情, postId:', this.postId)
        const startTime = Date.now()
        try {
          const res = await community.getPostDetail(this.postId)
          console.log(LOG_TAG, '<<< 帖子详情响应:', JSON.stringify(res))
          console.log(LOG_TAG, `帖子详情接口耗时: ${Date.now() - startTime}ms`)
          // 适配后端字段：username -> userName
          if (res) {
            res.userName = res.username || res.userName || '匿名用户'
            res.userImage = res.userImage || res.avatar || ''
          }
          this.postDetail = res || null
          // 同步点赞状态与点赞数（若后端返回）
          if (res) {
            this.isLiked = !!res.isLiked
            this.likeCount = res.likeCount || 0
          }
        } catch (err) {
          console.error(LOG_TAG, '<<< 帖子详情请求失败:', err)
        } finally {
          this.loadingDetail = false
        }
      },

      // 获取评论列表
      async fetchCommentList(isRefresh = false) {
        if (isRefresh) {
          this.commentPage.page = 1
          this.commentList = []
        }
        this.loadingComment = true
        const params = { ...this.commentPage }
        console.log(LOG_TAG, '>>> 请求评论列表, postId:', this.postId, 'params:', params)
        const startTime = Date.now()
        try {
          const res = await community.getCommentList(this.postId, params)
          console.log(LOG_TAG, '<<< 评论列表响应:', JSON.stringify(res))
          console.log(LOG_TAG, `评论列表接口耗时: ${Date.now() - startTime}ms`)
          const list = (res && res.rows) || []
          const total = (res && res.total) || 0
          // 适配后端字段：username -> userName
          list.forEach(item => {
            item.userName = item.username || item.userName || '匿名用户'
            item.userImage = item.userImage || item.avatar || ''
          })
          this.commentList = isRefresh ? list : this.commentList.concat(list)
          this.commentTotal = total
          console.log(LOG_TAG, `评论列表已加载 ${this.commentList.length}/${this.commentTotal} 条`)
        } catch (err) {
          console.error(LOG_TAG, '<<< 评论列表请求失败:', err)
        } finally {
          this.loadingComment = false
        }
      },

      // 加载更多评论
      loadMoreComments() {
        if (this.commentList.length >= this.commentTotal) {
          console.log(LOG_TAG, '评论已全部加载，无需翻页')
          return
        }
        this.commentPage.page++
        this.fetchCommentList(false)
      },

      // 点赞 / 取消点赞
      async handleLike() {
        console.log(LOG_TAG, '>>> 点击点赞, 当前状态 isLiked:', this.isLiked)
        const liked = this.isLiked
        const startTime = Date.now()
        try {
          let res
          if (liked) {
            console.log(LOG_TAG, '>>> 请求取消点赞, postId:', this.postId)
            res = await community.unlikePost(this.postId)
          } else {
            console.log(LOG_TAG, '>>> 请求点赞, postId:', this.postId)
            res = await community.likePost(this.postId)
          }
          console.log(LOG_TAG, '<<< 点赞操作响应:', JSON.stringify(res))
          console.log(LOG_TAG, `点赞接口耗时: ${Date.now() - startTime}ms`)
          // 更新本地状态
          this.isLiked = !liked
          // 以后端返回的 likeCount 为准，无返回则本地增减
          if (res && typeof res.likeCount === 'number') {
            this.likeCount = res.likeCount
          } else {
            this.likeCount = liked ? this.likeCount - 1 : this.likeCount + 1
            if (this.likeCount < 0) this.likeCount = 0
          }
          console.log(LOG_TAG, '点赞后本地状态 isLiked:', this.isLiked, 'likeCount:', this.likeCount)
        } catch (err) {
          console.error(LOG_TAG, '<<< 点赞操作失败:', err)
        }
      },

      // 提交评论
      async handleSubmitComment() {
        const content = this.commentText.trim()
        if (!content) {
          console.log(LOG_TAG, '评论内容为空，忽略提交')
          return
        }
        if (this.submitting) {
          console.log(LOG_TAG, '正在提交中，忽略重复点击')
          return
        }
        this.submitting = true
        const payload = { postId: this.postId, content }
        console.log(LOG_TAG, '>>> 请求发表评论, payload:', payload)
        const startTime = Date.now()
        try {
          const res = await community.addComment(payload)
          console.log(LOG_TAG, '<<< 发表评论响应:', JSON.stringify(res))
          console.log(LOG_TAG, `发表评论接口耗时: ${Date.now() - startTime}ms`)
          uni.showToast({ title: '评论成功', icon: 'success' })
          this.commentText = ''
          // 刷新评论列表
          this.fetchCommentList(true)
        } catch (err) {
          console.error(LOG_TAG, '<<< 发表评论失败:', err)
        } finally {
          this.submitting = false
        }
      },

      // 预览图片
      previewImage(index) {
        const urls = this.postDetail.images || []
        console.log(LOG_TAG, '预览图片, index:', index, 'urls:', urls)
        uni.previewImage({
          current: urls[index],
          urls
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  .template-detail {
    min-height: 100vh;
    background-color: #F6F6F6;
  }

  .tn-custom-nav-bar__back {
    .icon {
      font-size: 38rpx;
    }
  }

  .detail-scroll {
    height: 100vh;
    box-sizing: border-box;
    padding-bottom: 120rpx;
  }

  /* 帖子卡片 */
  .post-card {
    margin: 20rpx 30rpx;
    padding: 30rpx;
    border-radius: 20rpx;
  }

  .post-title {
    line-height: 1.5;
  }

  .post-content {
    line-height: 1.7;
    color: #333333;
  }

  .post-images {
    .image-item {
      margin-bottom: 20rpx;
      border-radius: 16rpx;
      overflow: hidden;
      image {
        width: 100%;
        display: block;
      }
    }
  }

  .post-tags {
    display: flex;
    flex-wrap: wrap;
    .tag-item {
      padding: 6rpx 20rpx;
      margin-right: 16rpx;
      margin-bottom: 16rpx;
      background-color: #F2F2F2;
      color: #666666;
      font-size: 24rpx;
      border-radius: 20rpx;
    }
  }

  .post-actions {
    padding-top: 20rpx;
    border-top: 1rpx solid #EFEFEF;
    .action-item {
      padding: 0 20rpx;
    }
  }

  /* 骨架屏 */
  .tn-skeleton {
    height: 40rpx;
    background-color: #F0F0F0;
    border-radius: 8rpx;
  }

  /* 评论区域 */
  .comment-section {
    margin: 20rpx 30rpx;
    padding: 20rpx 30rpx;
    border-radius: 20rpx;
  }

  .comment-item {
    padding: 24rpx 0;
    border-bottom: 1rpx solid #F5F5F5;
    &:last-child {
      border-bottom: none;
    }
  }

  .comment-body {
    min-width: 0;
  }

  .comment-empty {
    padding: 60rpx 0;
  }

  /* 底部评论输入栏 */
  .comment-input-bar {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    padding: 16rpx 30rpx;
    box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.04);
    z-index: 10;
  }

  .comment-input {
    flex: 1;
    height: 72rpx;
    background-color: #F2F2F2;
    border-radius: 36rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
  }

  .comment-placeholder {
    color: #BBBBBB;
    font-size: 28rpx;
  }

  .send-btn {
    margin-left: 20rpx;
    width: 100rpx;
    height: 72rpx;
    line-height: 72rpx;
    text-align: center;
    background-color: #000000;
    border-radius: 36rpx;
    &.disabled {
      background-color: #CCCCCC;
    }
  }

  /* 原 momentPages/details 模板布局 */
  .template-details {
    min-height: 100vh;
    overflow: hidden;
    background-color: #FFFFFF;
  }

  .nav-capsule {
    display: flex;
    align-items: center;
    justify-content: space-around;
    width: 150rpx;
    height: 58rpx;
    box-sizing: border-box;
    color: #FFFFFF;
    font-size: 31rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.55);
    border-radius: 100rpx;
    background-color: rgba(0, 0, 0, 0.52);

    &__item {
      display: flex;
      flex: 1;
      align-items: center;
      justify-content: center;
      height: 100%;
    }

    &__line {
      width: 1rpx;
      height: 30rpx;
      background-color: rgba(255, 255, 255, 0.55);
    }
  }

  .detail-scroll {
    width: 100%;
    height: 100vh;
    box-sizing: border-box;
    padding-bottom: 0;
  }

  .blogger__item {
    padding: 34rpx 30rpx 30rpx;
  }

  .blogger__author {
    min-height: 76rpx;
  }

  .blogger__more,
  .comment-more {
    color: #AAAAAA;
    font-size: 34rpx;
  }

  .avatar {
    flex-shrink: 0;
    overflow: hidden;
    border-radius: 50%;
    background-position: center;
    background-size: cover;
    background-color: #F1F1F1;

    &--author {
      width: 72rpx;
      height: 72rpx;
    }

    &--comment {
      width: 60rpx;
      height: 60rpx;
    }
  }

  .author-info {
    min-width: 0;
    padding-left: 18rpx;

    &__name {
      max-width: 420rpx;
      overflow: hidden;
      color: #242424;
      font-size: 29rpx;
      font-weight: bold;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &__meta {
      margin-top: 8rpx;
      color: #A6A6A6;
      font-size: 22rpx;
    }
  }

  .blogger__body {
    margin-left: 90rpx;
  }

  .post-tags {
    display: flex;
    flex-wrap: wrap;
    margin-top: 22rpx;
  }

  .post-tag {
    margin: 0 14rpx 14rpx 0;
    padding: 7rpx 15rpx;
    color: #444444;
    font-size: 22rpx;
    border-radius: 10rpx;
    background-color: #F2F2F2;

    .tn-icon-spiral {
      padding-right: 8rpx;
    }
  }

  .post-title {
    margin-top: 18rpx;
    color: #222222;
    font-size: 34rpx;
    font-weight: bold;
    line-height: 1.45;
  }

  .post-content {
    margin-top: 14rpx;
    color: #333333;
    font-size: 28rpx;
    line-height: 1.8;
    text-align: justify;
    white-space: pre-wrap;
    word-break: break-all;
  }

  .post-images {
    display: flex;
    flex-wrap: wrap;
    margin-top: 20rpx;
  }

  .post-image {
    box-sizing: border-box;
    margin: 0 12rpx 12rpx 0;
    border-radius: 16rpx;
    background-color: #F2F2F2;

    &--one {
      width: 82%;
      height: 360rpx;
    }

    &--double {
      width: calc(50% - 12rpx);
      height: 240rpx;
    }

    &--grid {
      width: calc(33.333% - 12rpx);
      height: 176rpx;
    }
  }

  .post-meta {
    margin-top: 18rpx;
    color: #A6A6A6;
    font-size: 22rpx;

    &__actions {
      display: flex;
      align-items: center;
    }
  }

  .post-meta__actions > text + text,
  .like-action > text + text {
    padding-left: 8rpx;
  }

  .like-action {
    display: flex;
    align-items: center;
    margin-left: 18rpx;

    &--active {
      color: #E64A4A;
    }
  }

  .detail-loading {
    padding: 50rpx 30rpx;
  }

  .skeleton {
    width: 100%;
    height: 34rpx;
    margin-bottom: 24rpx;
    border-radius: 8rpx;
    background-color: #F1F1F1;

    &--short {
      width: 44%;
    }
  }

  .section-strip {
    height: 18rpx;
    background-color: #F5F5F5;
  }

  .comment-section {
    margin: 0;
    padding: 0 30rpx;
    border-radius: 0;
  }

  .comment-header {
    min-height: 102rpx;
    border-bottom: 1rpx solid #F0F0F0;

    &__title {
      color: #222222;
      font-size: 29rpx;
      font-weight: bold;
    }

    &__action {
      color: #999999;
      font-size: 24rpx;
    }
  }

  .comment-item {
    padding: 30rpx 0 28rpx;
    border-bottom: 1rpx solid #F1F1F1;
  }

  .comment-author {
    padding-left: 16rpx;

    &__name {
      color: #333333;
      font-size: 26rpx;
      font-weight: bold;
    }

    &__meta {
      margin-top: 5rpx;
      color: #AAAAAA;
      font-size: 20rpx;
    }
  }

  .comment-body {
    min-width: 0;
    margin: 18rpx 20rpx 10rpx 76rpx;
    color: #333333;
    font-size: 27rpx;
    line-height: 1.65;
    white-space: pre-wrap;
    word-break: break-all;
  }

  .comment-meta {
    margin-left: 76rpx;
    color: #AAAAAA;
    font-size: 21rpx;

    &__reply {
      padding-left: 24rpx;
      color: #777777;
    }
  }

  .comment-empty,
  .comment-loading,
  .load-more {
    padding: 60rpx 0;
    color: #AAAAAA;
    font-size: 24rpx;
    text-align: center;
  }

  .load-more {
    padding: 30rpx 0;
  }

  .bottom-placeholder {
    height: calc(150rpx + constant(safe-area-inset-bottom));
    height: calc(150rpx + env(safe-area-inset-bottom));
  }

  .comment-footer {
    position: fixed;
    z-index: 1000;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    box-sizing: border-box;
    padding: 16rpx 24rpx calc(16rpx + constant(safe-area-inset-bottom));
    padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
    background-color: rgba(255, 255, 255, 0.96);
    box-shadow: 0 -4rpx 24rpx rgba(0, 0, 0, 0.06);
  }

  .comment-input {
    flex: 1;
    height: 68rpx;
    padding: 0 24rpx;
    color: #333333;
    font-size: 25rpx;
    border-radius: 36rpx;
    background-color: #F4F4F4;
  }

  .comment-placeholder {
    color: #AAAAAA;
    font-size: 25rpx;
  }

  .footer-like {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-width: 64rpx;
    margin-left: 16rpx;
    color: #888888;
    font-size: 20rpx;

    > text:first-child {
      font-size: 32rpx;
    }

    &--active {
      color: #E64A4A;
    }
  }

  .send-btn {
    min-width: 104rpx;
    width: auto;
    height: 68rpx;
    margin-left: 14rpx;
    padding: 0 18rpx;
    color: #FFFFFF;
    font-size: 24rpx;
    line-height: 68rpx;
    text-align: center;
    border-radius: 36rpx;
    background-color: #000000;

    &--disabled {
      background-color: #C4C4C4;
    }
  }
</style>
