<template>
  <view class="template-edit tn-safe-area-inset-bottom">
    <!-- 沿用原模板的取消按钮和发布类型导航 -->
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#FFFFFF">
      <view slot="back" class="cancel-btn" @tap="goBack">取消</view>
      <view class="edit-mode tn-flex tn-flex-col-center tn-flex-row-center">
        <view class="edit-mode__item edit-mode__item--active">发图文</view>
      </view>
    </tn-nav-bar>

    <scroll-view scroll-y class="edit-scroll" enable-back-to-top>
      <view class="edit-content" :style="{ paddingTop: vuex_custom_bar_height + 'px' }">
        <!-- 原发布页的图片拖拽区域 -->
        <view class="upload-section">
          <view class="section-title tn-flex tn-flex-row-between tn-flex-col-center">
            <view>
              <text class="tn-icon-image section-title__icon"></text>
              <text>添加图片</text>
            </view>
            <text class="section-title__extra">{{ fileList.length }}/9</text>
          </view>
          <tn-image-upload-drag
            ref="imageUpload"
            :customUpload="uploadImage"
            :fileList="fileList"
            :width="210"
            :height="210"
            :maxCount="9"
            :autoUpload="true"
            :showProgress="true"
            uploadText="选择图片"
            @on-success="handleUploadSuccess"
            @on-error="handleUploadError"
            @on-remove="handleUploadRemove"
            @on-list-change="handleListChange"
          ></tn-image-upload-drag>
        </view>

        <!-- 后端要求的帖子标题 -->
        <view class="title-field">
          <input
            class="title-input"
            type="text"
            v-model="formData.title"
            placeholder="添加标题，让更多人看到"
            placeholder-class="input-placeholder"
            maxlength="50"
          />
          <text class="title-count">{{ formData.title.length }}/50</text>
        </view>

        <!-- 原发布页的正文编辑区域 -->
        <view class="edit-textarea">
          <textarea
            v-model="formData.content"
            maxlength="2000"
            placeholder="添加正文，分享你的游戏心得、攻略或趣事..."
            placeholder-class="input-placeholder"
          ></textarea>
          <view class="textarea-count">{{ formData.content.length }}/2000</view>
        </view>

        <!-- 话题标签 -->
        <view class="tag-section">
          <view class="section-title">
            <text class="tn-icon-topics-fill section-title__icon"></text>
            <text>添加话题</text>
          </view>
          <view v-if="formData.tags.length" class="tag-list">
            <view class="tag-item" v-for="(tag, idx) in formData.tags" :key="idx">
              <text># {{ tag }}</text>
              <text class="tn-icon-close tag-item__close" @tap="removeTag(idx)"></text>
            </view>
          </view>
          <view class="tag-input-wrap tn-flex tn-flex-col-center">
            <input
              class="tag-input"
              type="text"
              v-model="tagInput"
              placeholder="输入话题，回车添加"
              placeholder-class="input-placeholder"
              maxlength="8"
              confirm-type="done"
              @confirm="handleAddTag"
            />
            <view class="tag-add-btn" @tap="handleAddTag">
              <text class="tn-icon-add"></text>
            </view>
          </view>
        </view>

        <!-- 用原模板的设置行承载圈子选择 -->
        <view class="option-section">
          <view class="option-row">
            <view class="option-row__left">
              <text class="tn-icon-reload-planet-fill option-row__icon"></text>
              <text>发布到圈子</text>
            </view>
            <text v-if="loadingCircles" class="option-row__hint">加载中...</text>
            <text v-else class="option-row__hint">请选择</text>
          </view>
          <scroll-view v-if="circleList.length" scroll-x class="circle-select" show-scrollbar="false">
            <view class="circle-select__inner">
              <view
                v-for="circle in circleList"
                :key="circle.id"
                class="circle-option"
                :class="{ 'circle-option--active': String(formData.circleId) === String(circle.id) }"
                @tap="formData.circleId = circle.id"
              >{{ circle.circleName }}</view>
            </view>
          </scroll-view>
          <view v-else-if="!loadingCircles" class="empty-circle">暂无可发布的圈子</view>

          <view class="option-row option-row--last">
            <view class="option-row__left">
              <text class="tn-icon-trusty-fill option-row__icon"></text>
              <text>帖子可见范围</text>
            </view>
            <view class="option-row__hint">
              <text>公开</text>
              <text class="tn-icon-right"></text>
            </view>
          </view>
        </view>

        <view class="footer-placeholder"></view>
      </view>
    </scroll-view>

    <!-- 原模板底部悬浮发布区 -->
    <view class="publish-footer">
      <view class="publish-tip">
        <text class="tn-icon-tip"></text>
        <text>文明发言，共建友好社区</text>
      </view>
      <view
        class="publish-button"
        :class="{ 'publish-button--disabled': submitting || !circleList.length }"
        @tap="handleSubmit"
      >
        <text>{{ submitting ? '发布中...' : '立即发布' }}</text>
      </view>
    </view>
  </view>
</template>

<script>
  import { community, file } from '@/api/index.js'

  // 日志前缀，便于在控制台过滤
  const LOG_TAG = '[PostPublish]'

  export default {
    name: 'postPublish',
    data() {
      return {
        // 表单数据
        formData: {
          circleId: '',
          title: '',
          content: '',
          tags: []
        },
        // 标签输入框内容
        tagInput: '',
        // 图片上传组件已选文件列表（组件内部使用）
        fileList: [],
        // 已上传成功的图片URL列表
        uploadedImages: [],
        // 提交中
        submitting: false,
        loadingCircles: false,
        circleList: []
      }
    },
    onLoad() {
      this.fetchCircles()
    },
    methods: {
      uploadImage(filePath, options) {
        return file.uploadImage(filePath, options)
      },
      async fetchCircles() {
        this.loadingCircles = true
        try {
          const data = await community.getCircleList()
          this.circleList = Array.isArray(data) ? data : []
          if (!this.formData.circleId && this.circleList.length) {
            this.formData.circleId = this.circleList[0].id
          }
        } catch (error) {
          console.error(LOG_TAG, '圈子列表加载失败:', error)
        } finally {
          this.loadingCircles = false
        }
      },
      // 返回上一页
      goBack() {
        uni.navigateBack({ delta: 1 })
      },

      // 添加标签
      handleAddTag() {
        const tag = this.tagInput.trim()
        if (!tag) {
          console.log(LOG_TAG, '标签内容为空，忽略添加')
          return
        }
        if (this.formData.tags.includes(tag)) {
          uni.showToast({ title: '标签已存在', icon: 'none' })
          this.tagInput = ''
          return
        }
        if (this.formData.tags.length >= 5) {
          uni.showToast({ title: '最多添加5个标签', icon: 'none' })
          return
        }
        this.formData.tags.push(tag)
        console.log(LOG_TAG, '添加标签:', tag, '当前标签列表:', this.formData.tags)
        this.tagInput = ''
      },

      // 删除标签
      removeTag(idx) {
        console.log(LOG_TAG, '删除标签:', this.formData.tags[idx])
        this.formData.tags.splice(idx, 1)
      },

      // 图片上传成功
      handleUploadSuccess(data, index, lists) {
        console.log(LOG_TAG, '>>> 图片上传成功, index:', index)
        console.log(LOG_TAG, '<<< 上传响应 data:', JSON.stringify(data))
        console.log(LOG_TAG, '<<< 当前文件列表 lists:', JSON.stringify(lists))
        const imageUrl = data && data.url
        if (imageUrl) {
          this.$set(this.uploadedImages, index, imageUrl)
          console.log(LOG_TAG, '已收集图片URL:', imageUrl, '已上传图片列表:', this.uploadedImages)
        } else {
          console.warn(LOG_TAG, '未能从响应中解析图片URL，response:', JSON.stringify(data))
          uni.showToast({ title: '图片地址解析失败', icon: 'none' })
        }
      },

      // 图片上传失败
      handleUploadError(err, index, lists) {
        console.error(LOG_TAG, '<<< 图片上传失败, index:', index, 'error:', err)
        uni.showToast({ title: '图片上传失败', icon: 'none' })
      },

      // 图片删除
      handleUploadRemove(index, lists) {
        console.log(LOG_TAG, '删除图片, index:', index)
        // 同步移除已收集的图片URL
        if (this.uploadedImages[index]) {
          this.uploadedImages.splice(index, 1)
        }
        console.log(LOG_TAG, '删除后已上传图片列表:', this.uploadedImages)
      },

      // 文件列表变化（用于跟踪选择/上传状态）
      handleListChange(lists) {
        console.log(LOG_TAG, '文件列表变化, 数量:', lists.length)
        this.fileList = lists
      },

      // 表单校验
      validate() {
        const { title, content } = this.formData
        if (!this.formData.circleId) {
          uni.showToast({ title: '请选择发布圈子', icon: 'none' })
          return false
        }
        if (!title.trim()) {
          uni.showToast({ title: '请输入标题', icon: 'none' })
          return false
        }
        if (!content.trim()) {
          uni.showToast({ title: '请输入正文内容', icon: 'none' })
          return false
        }
        return true
      },

      // 提交发布
      async handleSubmit() {
        if (!this.validate()) return
        if (this.submitting) {
          console.log(LOG_TAG, '正在发布中，忽略重复点击')
          return
        }
        // 检查是否有图片正在上传中
        if (this.$refs.imageUpload && this.$refs.imageUpload.uploading) {
          uni.showToast({ title: '图片上传中，请稍候', icon: 'none' })
          return
        }
        this.submitting = true
        // 后端发布帖子接口仅需 title 和 content
        const payload = {
          circleId: this.formData.circleId,
          title: this.formData.title.trim(),
          content: this.formData.content.trim(),
          images: this.uploadedImages.filter(Boolean)
        }
        console.log(LOG_TAG, '>>> 请求发布帖子, payload:', JSON.stringify(payload))
        const startTime = Date.now()
        try {
          const res = await community.publishPost(payload)
          console.log(LOG_TAG, '<<< 发布帖子响应:', JSON.stringify(res))
          console.log(LOG_TAG, `发布帖子接口耗时: ${Date.now() - startTime}ms`)
          uni.showToast({ title: '发布成功', icon: 'success' })
          uni.$emit('forum-post-published', res)
          // 延迟返回，保证 toast 显示
          setTimeout(() => {
            uni.navigateBack({ delta: 1 })
          }, 1000)
        } catch (err) {
          console.error(LOG_TAG, '<<< 发布帖子失败:', err)
        } finally {
          this.submitting = false
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
  .template-edit {
    width: 100%;
    min-height: 100vh;
    overflow: hidden;
    background-color: #FFFFFF;
  }

  .cancel-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 108rpx;
    height: 58rpx;
    color: #333333;
    font-size: 25rpx;
    border: 1rpx solid #E7E7E7;
    border-radius: 100rpx;
    background-color: #F6F6F6;
  }

  .edit-mode {
    width: 100%;
    height: 100%;

    &__item {
      position: relative;
      padding: 0 24rpx;
      color: #222222;
      font-size: 30rpx;
      font-weight: bold;

      &--active::after {
        content: '';
        position: absolute;
        left: 50%;
        bottom: -10rpx;
        width: 34rpx;
        height: 6rpx;
        border-radius: 6rpx;
        background-color: #000000;
        transform: translateX(-50%);
      }
    }
  }

  .edit-scroll {
    width: 100%;
    height: 100vh;
  }

  .edit-content {
    box-sizing: border-box;
    padding-left: 32rpx;
    padding-right: 32rpx;
  }

  .upload-section,
  .tag-section,
  .option-section {
    margin-top: 28rpx;
  }

  .section-title {
    margin-bottom: 24rpx;
    color: #292929;
    font-size: 28rpx;
    font-weight: bold;

    &__icon {
      padding-right: 12rpx;
      font-size: 32rpx;
    }

    &__extra {
      color: #B1B1B1;
      font-size: 24rpx;
      font-weight: normal;
    }
  }

  .title-field {
    position: relative;
    margin-top: 34rpx;
    padding: 0 76rpx 20rpx 0;
    border-bottom: 1rpx solid #EFEFEF;
  }

  .title-input {
    width: 100%;
    height: 62rpx;
    color: #222222;
    font-size: 34rpx;
    font-weight: bold;
    line-height: 62rpx;
  }

  .title-count {
    position: absolute;
    right: 0;
    bottom: 34rpx;
    color: #C1C1C1;
    font-size: 22rpx;
  }

  .edit-textarea {
    position: relative;
    min-height: 300rpx;
    padding: 28rpx 0 50rpx;
    border-bottom: 1rpx solid #EFEFEF;

    textarea {
      width: 100%;
      height: 250rpx;
      color: #333333;
      font-size: 29rpx;
      line-height: 1.7;
    }

    .textarea-count {
      position: absolute;
      right: 0;
      bottom: 18rpx;
      color: #B8B8B8;
      font-size: 23rpx;
    }
  }

  .input-placeholder {
    color: #AAA8B7;
    font-weight: normal;
  }

  .tag-list {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 8rpx;
  }

  .tag-item {
    display: flex;
    align-items: center;
    margin: 0 16rpx 16rpx 0;
    padding: 10rpx 18rpx;
    color: #333333;
    font-size: 24rpx;
    border-radius: 28rpx;
    background-color: #F1F1F1;

    &__close {
      padding-left: 10rpx;
      color: #999999;
      font-size: 20rpx;
    }
  }

  .tag-input-wrap {
    padding-bottom: 26rpx;
    border-bottom: 1rpx solid #EFEFEF;
  }

  .tag-input {
    flex: 1;
    height: 66rpx;
    padding: 0 26rpx;
    color: #333333;
    font-size: 26rpx;
    border-radius: 34rpx;
    background-color: #F7F7F7;
  }

  .tag-add-btn {
    width: 66rpx;
    height: 66rpx;
    margin-left: 16rpx;
    color: #FFFFFF;
    font-size: 26rpx;
    line-height: 66rpx;
    text-align: center;
    border-radius: 50%;
    background-color: #000000;
  }

  .option-section {
    padding: 0 24rpx;
    border-radius: 22rpx;
    background-color: #F8F8F8;
  }

  .option-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-height: 92rpx;
    color: #383838;
    font-size: 27rpx;

    &--last {
      margin-top: 10rpx;
      border-top: 1rpx solid #E9E9E9;
    }

    &__left {
      display: flex;
      align-items: center;
      font-weight: bold;
    }

    &__icon {
      padding-right: 12rpx;
      font-size: 32rpx;
    }

    &__hint {
      color: #999999;
      font-size: 24rpx;
    }
  }

  .circle-select {
    width: 100%;
    padding-bottom: 22rpx;
    white-space: nowrap;
  }

  .circle-select__inner {
    display: inline-flex;
  }

  .circle-option {
    flex-shrink: 0;
    margin-right: 14rpx;
    padding: 12rpx 24rpx;
    color: #666666;
    font-size: 24rpx;
    border: 1rpx solid #E4E4E4;
    border-radius: 32rpx;
    background-color: #FFFFFF;

    &--active {
      color: #FFFFFF;
      border-color: #000000;
      background-color: #000000;
    }
  }

  .empty-circle {
    padding-bottom: 24rpx;
    color: #AAAAAA;
    font-size: 24rpx;
  }

  .footer-placeholder {
    height: calc(190rpx + constant(safe-area-inset-bottom));
    height: calc(190rpx + env(safe-area-inset-bottom));
  }

  .publish-footer {
    position: fixed;
    z-index: 1000;
    left: 30rpx;
    right: 30rpx;
    bottom: calc(24rpx + constant(safe-area-inset-bottom));
    bottom: calc(24rpx + env(safe-area-inset-bottom));
    display: flex;
    align-items: center;
    box-sizing: border-box;
    min-height: 106rpx;
    padding: 12rpx 14rpx 12rpx 26rpx;
    border-radius: 60rpx;
    background-color: #FFFFFF;
    box-shadow: 0 4rpx 32rpx rgba(0, 0, 0, 0.11);
  }

  .publish-tip {
    display: flex;
    flex: 1;
    align-items: center;
    color: #A0A0A0;
    font-size: 21rpx;

    .tn-icon-tip {
      padding-right: 10rpx;
      font-size: 32rpx;
    }
  }

  .publish-button {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 240rpx;
    height: 78rpx;
    color: #FFFFFF;
    font-size: 29rpx;
    font-weight: bold;
    border-radius: 42rpx;
    background-color: #000000;

    &--disabled {
      background-color: #BDBDBD;
    }
  }
</style>
