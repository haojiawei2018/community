<template>
  <view class="template-publish tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#FFFFFF">
      <view slot="back" class="tn-custom-nav-bar__back" @click="goBack">
        <text class="icon tn-icon-left-arrow"></text>
      </view>
      <view class="tn-flex tn-flex-col-center tn-flex-row-between tn-padding-left tn-padding-right">
        <text class="tn-text-bold tn-text-lg">发布帖子</text>
        <view class="publish-btn" :class="{ disabled: submitting }" @click="handleSubmit">
          <text class="tn-color-white tn-text-sm">{{ submitting ? '发布中...' : '发布' }}</text>
        </view>
      </view>
    </tn-nav-bar>

    <!-- 内容区域 -->
    <view class="publish-wrap" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
      <view class="form-card tn-bg-white">
        <!-- 标题 -->
        <view class="form-row">
          <input
            class="title-input"
            type="text"
            v-model="formData.title"
            placeholder="给帖子起个标题（必填）"
            placeholder-class="input-placeholder"
            maxlength="50"
          />
        </view>

        <view class="form-divider"></view>

        <!-- 正文 -->
        <view class="form-row">
          <textarea
            class="content-input"
            v-model="formData.content"
            placeholder="分享你的游戏心得、攻略或趣事..."
            placeholder-class="input-placeholder"
            maxlength="2000"
            auto-height
          />
        </view>

        <view class="form-divider"></view>

        <!-- 图片上传 -->
        <view class="form-row">
          <view class="row-label">
            <text class="tn-icon-image-fill tn-text-lg tn-color-gray"></text>
            <text class="tn-padding-left-xs tn-text-sm tn-color-gray">添加图片（最多9张）</text>
          </view>
          <tn-image-upload
            ref="imageUpload"
            :action="uploadAction"
            :header="uploadHeader"
            :fileList="fileList"
            :maxCount="9"
            :autoUpload="true"
            :showProgress="true"
            @on-success="handleUploadSuccess"
            @on-error="handleUploadError"
            @on-remove="handleUploadRemove"
            @on-list-change="handleListChange"
          ></tn-image-upload>
        </view>

        <view class="form-divider"></view>

        <!-- 标签 -->
        <view class="form-row">
          <view class="row-label">
            <text class="tn-icon-tag-fill tn-text-lg tn-color-gray"></text>
            <text class="tn-padding-left-xs tn-text-sm tn-color-gray">添加标签</text>
          </view>
          <!-- 已选标签 -->
          <view class="tag-list tn-flex tn-flex-wrap tn-margin-top-sm" v-if="formData.tags.length">
            <view class="tag-item" v-for="(tag, idx) in formData.tags" :key="idx">
              <text class="tn-text-sm">{{ tag }}</text>
              <text class="tn-icon-close tn-padding-left-xs" @click="removeTag(idx)"></text>
            </view>
          </view>
          <!-- 标签输入 -->
          <view class="tag-input-wrap tn-flex tn-flex-col-center tn-margin-top-sm">
            <input
              class="tag-input"
              type="text"
              v-model="tagInput"
              placeholder="输入标签后回车添加"
              placeholder-class="input-placeholder"
              maxlength="8"
              confirm-type="done"
              @confirm="handleAddTag"
            />
            <view class="tag-add-btn" @click="handleAddTag">
              <text class="tn-icon-add tn-text-sm"></text>
            </view>
          </view>
        </view>
      </view>

      <!-- 字数统计 -->
      <view class="word-count tn-text-right tn-padding-right tn-text-sm tn-color-gray">
        {{ formData.content.length }} / 2000
      </view>
    </view>
  </view>
</template>

<script>
  import { community } from '@/api/index.js'
  import env from '@/config/env.js'

  // 日志前缀，便于在控制台过滤
  const LOG_TAG = '[PostPublish]'

  export default {
    name: 'postPublish',
    data() {
      return {
        // 表单数据
        formData: {
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
        submitting: false
      }
    },
    computed: {
      // 图片上传地址：后端文件上传接口
      uploadAction() {
        return env.baseURL + '/file/upload'
      },
      // 上传请求头：携带 token
      uploadHeader() {
        const token = uni.getStorageSync('token')
        const header = {}
        if (token) header['token'] = token
        return header
      }
    },
    methods: {
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
        // 后端返回 { code, data, message } 结构，data 为图片URL
        let imageUrl = ''
        if (data) {
          // 兼容两种返回格式：data 直接是 URL 字符串 / data 是对象含 url 字段
          if (typeof data === 'string') {
            imageUrl = data
          } else if (data.data) {
            imageUrl = typeof data.data === 'string' ? data.data : (data.data.url || data.data.path || '')
          } else if (data.url) {
            imageUrl = data.url
          }
        }
        if (imageUrl) {
          this.uploadedImages.push(imageUrl)
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
          title: this.formData.title.trim(),
          content: this.formData.content.trim()
        }
        console.log(LOG_TAG, '>>> 请求发布帖子, payload:', JSON.stringify(payload))
        const startTime = Date.now()
        try {
          const res = await community.publishPost(payload)
          console.log(LOG_TAG, '<<< 发布帖子响应:', JSON.stringify(res))
          console.log(LOG_TAG, `发布帖子接口耗时: ${Date.now() - startTime}ms`)
          uni.showToast({ title: '发布成功', icon: 'success' })
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
  .template-publish {
    min-height: 100vh;
    background-color: #F6F6F6;
  }

  .tn-custom-nav-bar__back {
    .icon {
      font-size: 38rpx;
    }
  }

  .publish-btn {
    padding: 10rpx 30rpx;
    background-color: #000000;
    border-radius: 30rpx;
    &.disabled {
      background-color: #CCCCCC;
    }
  }

  .publish-wrap {
    padding: 20rpx 0;
  }

  .form-card {
    margin: 0 30rpx;
    padding: 30rpx;
    border-radius: 20rpx;
  }

  .form-row {
    padding: 20rpx 0;
  }

  .form-divider {
    height: 1rpx;
    background-color: #EFEFEF;
  }

  .title-input {
    width: 100%;
    font-size: 34rpx;
    font-weight: bold;
    height: 60rpx;
    line-height: 60rpx;
  }

  .content-input {
    width: 100%;
    min-height: 300rpx;
    font-size: 30rpx;
    line-height: 1.7;
    color: #333333;
  }

  .input-placeholder {
    color: #BBBBBB;
  }

  .row-label {
    display: flex;
    flex-direction: row;
    align-items: center;
  }

  /* 标签 */
  .tag-list {
    .tag-item {
      display: flex;
      align-items: center;
      padding: 8rpx 20rpx;
      margin-right: 16rpx;
      margin-bottom: 16rpx;
      background-color: #F2F2F2;
      color: #333333;
      border-radius: 20rpx;
    }
  }

  .tag-input-wrap {
    .tag-input {
      flex: 1;
      height: 64rpx;
      background-color: #F6F6F6;
      border-radius: 32rpx;
      padding: 0 30rpx;
      font-size: 28rpx;
    }
    .tag-add-btn {
      margin-left: 16rpx;
      width: 64rpx;
      height: 64rpx;
      line-height: 64rpx;
      text-align: center;
      background-color: #000000;
      color: #FFFFFF;
      border-radius: 50%;
    }
  }

  .word-count {
    margin-top: 20rpx;
  }
</style>
