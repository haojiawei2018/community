<template>
  <view class="template-detail tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#FFFFFF">
      <view slot="back" class="tn-custom-nav-bar__back" @click="goBack">
        <text class="icon tn-icon-left-arrow"></text>
      </view>
      <view class="tn-flex tn-flex-col-center tn-flex-row-center">
        <text class="tn-text-bold tn-text-lg">帖子详情</text>
      </view>
    </tn-nav-bar>

    <!-- 内容区域 -->
    <scroll-view scroll-y class="detail-scroll" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
      <!-- 帖子主体 -->
      <view class="post-card tn-bg-white" v-if="postDetail">
        <!-- 作者信息 -->
        <view class="post-author tn-flex tn-flex-col-center tn-flex-row-between">
          <view class="tn-flex tn-flex-col-center">
            <view class="logo-pic">
              <view class="logo-image" :style="authorAvatarStyle"></view>
            </view>
            <view class="tn-padding-left-sm">
              <view class="tn-text-bold">{{ postDetail.userName || '匿名用户' }}</view>
              <view class="tn-color-gray tn-text-sm tn-margin-top-xs">{{ formatTime(postDetail.createTime) }}</view>
            </view>
          </view>
        </view>

        <!-- 标题 -->
        <view class="post-title tn-margin-top" v-if="postDetail.title">
          <text class="tn-text-bold tn-text-xl">{{ postDetail.title }}</text>
        </view>

        <!-- 正文 -->
        <view class="post-content tn-margin-top-sm">
          <text class="tn-text-lg">{{ postDetail.content }}</text>
        </view>

        <!-- 图片列表 -->
        <view class="post-images tn-margin-top" v-if="postDetail.images && postDetail.images.length">
          <view class="image-item" v-for="(img, idx) in postDetail.images" :key="idx">
            <image :src="img" mode="widthFix" @click="previewImage(idx)"></image>
          </view>
        </view>

        <!-- 标签 -->
        <view class="post-tags tn-margin-top" v-if="postDetail.tags && postDetail.tags.length">
          <view class="tag-item" v-for="(tag, idx) in postDetail.tags" :key="idx">{{ tag }}</view>
        </view>

        <!-- 操作栏：点赞 / 评论数 -->
        <view class="post-actions tn-flex tn-flex-row-between tn-flex-col-center tn-margin-top">
          <view class="action-item tn-flex tn-flex-col-center" @click="handleLike">
            <text
              class="tn-text-lg"
              :class="isLiked ? 'tn-icon-like-fill tn-color-red' : 'tn-icon-like-lack tn-color-gray'"
            ></text>
            <text class="tn-padding-left-xs tn-text-sm" :class="isLiked ? 'tn-color-red' : 'tn-color-gray'">
              {{ likeCount }}
            </text>
          </view>
          <view class="action-item tn-flex tn-flex-col-center">
            <text class="tn-icon-comment tn-text-lg tn-color-gray"></text>
            <text class="tn-padding-left-xs tn-text-sm tn-color-gray">{{ commentTotal }}</text>
          </view>
        </view>
      </view>

      <!-- 骨架屏：加载中 -->
      <view class="post-card tn-bg-white" v-if="!postDetail && loadingDetail">
        <view class="tn-skeleton tn-margin-bottom"></view>
        <view class="tn-skeleton tn-margin-bottom"></view>
        <view class="tn-skeleton"></view>
      </view>

      <!-- 评论列表 -->
      <view class="comment-section tn-bg-white tn-margin-top-sm" v-if="commentList.length || !loadingComment">
        <view class="section-title tn-padding">
          <text class="tn-text-bold tn-text-lg">评论 {{ commentTotal ? '(' + commentTotal + ')' : '' }}</text>
        </view>

        <!-- 评论项 -->
        <view class="comment-item tn-flex" v-for="(item, index) in commentList" :key="item.id || index">
          <view class="logo-pic">
            <view class="logo-image" :style="commentAvatarStyle(item)"></view>
          </view>
          <view class="comment-body tn-flex-1 tn-padding-left-sm">
            <view class="tn-text-bold tn-text-sm">{{ item.userName || '匿名用户' }}</view>
            <view class="tn-margin-top-xs tn-text-sm">{{ item.content }}</view>
            <view class="tn-color-gray tn-text-xs tn-margin-top-xs">{{ formatTime(item.createTime) }}</view>
          </view>
        </view>

        <!-- 评论空状态 -->
        <view class="comment-empty tn-text-center tn-padding" v-if="!commentList.length && !loadingComment">
          <text class="tn-color-gray tn-text-sm">暂无评论，快来抢沙发~</text>
        </view>

        <!-- 加载更多 -->
        <view class="tn-text-center tn-padding" v-if="commentList.length < commentTotal" @click="loadMoreComments">
          <text class="tn-color-gray tn-text-sm">点击加载更多</text>
        </view>
      </view>

      <!-- 底部占位，防止被评论输入框遮挡 -->
      <view style="height: 140rpx;"></view>
    </scroll-view>

    <!-- 底部评论输入 -->
    <view class="comment-input-bar tn-flex tn-flex-col-center tn-bg-white">
      <input
        class="comment-input"
        type="text"
        v-model="commentText"
        placeholder="写下你的评论..."
        placeholder-class="comment-placeholder"
        confirm-type="send"
        @confirm="handleSubmitComment"
      />
      <view class="send-btn" :class="{ disabled: !commentText.trim() || submitting }" @click="handleSubmitComment">
        <text class="tn-color-white tn-text-sm">发送</text>
      </view>
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
      // 返回上一页
      goBack() {
        uni.navigateBack({ delta: 1 })
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
</style>
