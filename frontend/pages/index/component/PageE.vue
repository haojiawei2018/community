<template>
  <view class="pagesE tn-safe-area-inset-bottom">
    <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F600">
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left">
        <text class="tn-margin-left tn-text-bold tn-text-xl leaf-color">我 的</text>
      </view>
    </tn-nav-bar>

    <!-- 保留原模板顶部渐变和头像区 -->
    <view class="mine-fixed"></view>
    <view :style="{paddingTop: vuex_custom_bar_height + 'px'}" class="mine-content">
      <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin">
        <view class="justify-content-item" @click="handleUserClick">
          <view class="tn-flex tn-flex-col-center tn-flex-row-left">
            <view class="logo-pic">
              <view class="logo-image" :style="avatarStyle"></view>
            </view>
            <view class="tn-padding-right">
              <view class="tn-padding-right tn-padding-left-sm">
                <text class="tn-color-black tn-text-xl tn-text-bold">{{ displayName }}</text>
              </view>
              <view v-if="isLoggedIn" class="tn-padding-right tn-padding-top-sm tn-padding-left-sm tn-text-ellipsis profile-subtitle">
                <text class="tn-color-gray--dark">{{ userInfo.bio || '这个人很低调，还没有填写简介' }}</text>
              </view>
              <view v-else class="tn-padding-right tn-padding-top-sm tn-padding-left-sm tn-text-ellipsis">
                <text class="tn-color-gray--dark">登录后管理帖子和个人资料</text>
              </view>
            </view>
          </view>
        </view>
        <view class="justify-content-item" @click="openProfileEditor">
          <view class="tn-icon-install tn-color-gray--dark setting-icon"></view>
        </view>
      </view>

      <!-- 我的帖子、获赞、评论、新帖快捷入口暂时隐藏，后续需要时可恢复。
      <view class="tn-flex tn-flex-row-between quick-actions">
        <view class="tn-padding-sm tn-radius" @click="goMyPosts">
          <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
            <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center quick-icon">
              <view class="tn-icon-edit-form"></view>
            </view>
            <view class="tn-text-center"><text class="tn-text-ellipsis">帖 子</text></view>
          </view>
        </view>
        <view class="tn-padding-sm tn-radius">
          <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
            <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center quick-icon">
              <view class="tn-icon-like-fill"></view>
            </view>
            <view class="tn-text-center"><text class="tn-text-ellipsis">获 赞</text></view>
          </view>
        </view>
        <view class="tn-padding-sm tn-radius">
          <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
            <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center quick-icon">
              <view class="tn-icon-message-fill"></view>
            </view>
            <view class="tn-text-center"><text class="tn-text-ellipsis">评 论</text></view>
          </view>
        </view>
        <view class="tn-padding-sm tn-radius" @click="goPublish">
          <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
            <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center quick-icon">
              <view class="tn-icon-add"></view>
            </view>
            <view class="tn-text-center"><text class="tn-text-ellipsis">新 帖</text></view>
          </view>
        </view>
      </view>
      -->

      <!-- 恢复原模板“应用服务”宫格，将入口替换为社区真实功能 -->
      <view class="service-card">
        <view class="tn-flex tn-flex-row-between tn-flex-col-center service-header">
          <view class="tn-text-lg tn-text-bold">应用服务</view>
          <view class="tn-color-gray tn-text-sm">社区功能</view>
        </view>
        <view class="tn-flex tn-flex-wrap service-grid">
          <view v-for="item in serviceLinks" :key="item.key" class="service-item" @click="openService(item.key)">
            <view class="service-icon" :class="item.color">
              <text :class="item.icon"></text>
            </view>
            <view class="service-name">{{ item.title }}</view>
          </view>
        </view>
      </view>

      <view class="tn-padding-top-sm tn-padding-bottom-sm tn-bg-white common-card">
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx" @click="openProfileEditor">
          <view class="tn-flex tn-flex-col-center">
            <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center"><view class="tn-icon-my-circle-fill"></view></view>
            <view class="tn-margin-left-sm tn-flex-1 tn-text-lg">编辑资料</view>
            <view class="tn-color-gray tn-icon-right"></view>
          </view>
        </tn-list-cell>
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx" @click="copySource">
          <view class="tn-flex tn-flex-col-center">
            <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center"><view class="tn-icon-discover-fill"></view></view>
            <view class="tn-margin-left-sm tn-flex-1 tn-text-lg">开源地址</view>
            <view class="tn-color-gray tn-icon-right"></view>
          </view>
        </tn-list-cell>
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx">
          <button class="tn-flex tn-flex-col-center tn-button--clear-style" open-type="feedback">
            <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center"><view class="tn-icon-tip-fill"></view></view>
            <view class="tn-flex tn-flex-row-between" style="width: 100%;">
              <view class="tn-margin-left-sm tn-text-lg">问题反馈</view>
              <view class="tn-color-gray tn-icon-right"></view>
            </view>
          </button>
        </tn-list-cell>
      </view>

      <!-- 原模板“关注我们、在线客服、技术热线、图鸟科技支持”暂不属于社区 P0，已注释后置。 -->

      <view v-if="isLoggedIn" class="tn-padding-top-sm tn-padding-bottom-sm tn-bg-white common-card">
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx" @click="handleLogout">
          <view class="tn-flex tn-flex-col-center tn-flex-row-center">
            <view class="tn-icon-logout tn-text-lg"></view>
            <view class="tn-margin-left-sm tn-text-lg logout-text">退出登录</view>
          </view>
        </tn-list-cell>
      </view>

      <view class="opensource-signature">程序员小程 开源作品</view>
    </view>

    <tn-modal v-model="showProfileEditor" :custom="true">
      <view class="profile-modal">
        <view class="tn-text-xl tn-text-bold tn-text-center">编辑个人资料</view>
        <view class="profile-avatar-editor" @click="changeAvatar">
          <image class="profile-avatar-image" :src="profileForm.avatarUrl || defaultAvatar" mode="aspectFill"></image>
          <view class="profile-avatar-tip">{{ avatarUploading ? '上传中...' : '更换头像' }}</view>
        </view>
        <view class="form-label">社区昵称</view>
        <input v-model="profileForm.displayName" class="form-input" maxlength="32" placeholder="请输入社区昵称" />
        <view class="form-label">个人简介</view>
        <textarea v-model="profileForm.bio" class="form-textarea" maxlength="255" placeholder="介绍一下自己" />
        <view class="tn-flex tn-margin-top-lg">
          <view class="modal-button modal-button--light" @click="showProfileEditor = false">取消</view>
          <view class="modal-button modal-button--dark" @click="saveProfile">{{ profileSaving ? '保存中' : '保存' }}</view>
        </view>
      </view>
    </tn-modal>

    <view class="tn-tabbar-height"></view>
  </view>
</template>

<script>
  import { user, file } from '@/api/index.js'
  import session from '@/utils/session.js'
  import { DEFAULT_AVATAR_URL } from '@/config/defaults.js'

  export default {
    name: 'PageE',
    data() {
      return {
        userInfo: {},
        isLoggedIn: false,
        summary: { postCount: 0, receivedLikeCount: 0, commentCount: 0 },
        serviceLinks: [
          { key: 'posts', title: '我的帖子', icon: 'tn-icon-edit-form', color: 'service-icon--pink' },
          { key: 'sign', title: '签到', icon: 'tn-icon-calendar', color: 'service-icon--purple' },
          { key: 'ranking', title: '排行榜', icon: 'tn-icon-order', color: 'service-icon--blue' },
          { key: 'activity', title: '社区活动', icon: 'tn-icon-flag-fill', color: 'service-icon--orange' }
        ],
        showProfileEditor: false,
        profileSaving: false,
        avatarUploading: false,
        defaultAvatar: DEFAULT_AVATAR_URL,
        profileForm: { displayName: '', bio: '', avatarUrl: '' }
      }
    },
    computed: {
      displayName() {
        return this.userInfo.displayName || this.userInfo.nickname || this.userInfo.username || '点击登录'
      },
      avatarStyle() {
        const url = this.userInfo.avatarUrl || this.userInfo.avatar || this.defaultAvatar
        return `background-image:url('${url}');width:110rpx;height:110rpx;background-size:cover;background-position:center;`
      }
    },
    created() {
      this.refresh()
      uni.$on('forum-post-published', this.handlePostPublished)
      uni.$on('forum-post-deleted', this.handlePostDeleted)
    },
    beforeDestroy() {
      uni.$off('forum-post-published', this.handlePostPublished)
      uni.$off('forum-post-deleted', this.handlePostDeleted)
    },
    methods: {
      async refresh() {
        const rawInfo = session.getUser()
        this.isLoggedIn = session.isAccessTokenUsable()
        this.userInfo = rawInfo || {}
        if (!this.isLoggedIn) {
          this.summary = { postCount: 0, receivedLikeCount: 0, commentCount: 0 }
          return
        }
        try {
          const results = await Promise.all([
            user.getCurrentUser({ custom: { silent: true, authRedirect: false } }),
            user.getCommunitySummary({ custom: { silent: true, authRedirect: false } })
          ])
          this.userInfo = results[0] || this.userInfo
          this.summary = Object.assign({}, this.summary, results[1] || {})
          session.saveUser(this.userInfo)
        } catch (error) {
          console.warn('[PageE] 用户资料或统计刷新失败，继续使用本地数据')
        }
      },
      handlePostPublished() {
        this.refresh()
      },
      handlePostDeleted() {
        this.refresh()
      },
      handleUserClick() {
        if (this.isLoggedIn) this.openProfileEditor()
        else this.goLogin()
      },
      openProfileEditor() {
        if (!this.isLoggedIn) {
          this.goLogin()
          return
        }
        this.profileForm = {
          displayName: this.userInfo.displayName || this.userInfo.nickname || '',
          bio: this.userInfo.bio || '',
          avatarUrl: this.userInfo.avatarUrl || this.defaultAvatar
        }
        this.showProfileEditor = true
      },
      async changeAvatar() {
        if (this.avatarUploading) return
        try {
          const chooseResult = await new Promise((resolve, reject) => {
            uni.chooseImage({ count: 1, sizeType: ['compressed'], sourceType: ['album', 'camera'], success: resolve, fail: reject })
          })
          const filePath = chooseResult.tempFilePaths && chooseResult.tempFilePaths[0]
          if (!filePath) return
          this.avatarUploading = true
          const uploaded = await file.uploadImage(filePath)
          this.profileForm.avatarUrl = uploaded.url
        } catch (error) {
          if (error && error.errMsg && error.errMsg.indexOf('cancel') !== -1) return
          uni.showToast({ title: '头像上传失败', icon: 'none' })
        } finally {
          this.avatarUploading = false
        }
      },
      async saveProfile() {
        const displayName = (this.profileForm.displayName || '').trim()
        if (!displayName) {
          uni.showToast({ title: '昵称不能为空', icon: 'none' })
          return
        }
        if (this.profileSaving) return
        this.profileSaving = true
        try {
          const currentUser = await user.updateProfile({
            displayName,
            avatarUrl: this.profileForm.avatarUrl || this.defaultAvatar,
            bio: (this.profileForm.bio || '').trim()
          })
          this.userInfo = currentUser
          session.saveUser(currentUser)
          this.showProfileEditor = false
          uni.showToast({ title: '资料已更新', icon: 'success' })
        } catch (error) {
        } finally {
          this.profileSaving = false
        }
      },
      goLogin() {
        uni.navigateTo({ url: '/pages/login/login' })
      },
      goPublish() {
        if (!this.isLoggedIn) {
          uni.showToast({ title: '请先登录', icon: 'none' })
          setTimeout(this.goLogin, 600)
          return
        }
        uni.navigateTo({ url: '/pages/post/publish' })
      },
      goMyPosts() {
        if (!this.isLoggedIn) {
          this.goLogin()
          return
        }
        uni.navigateTo({ url: '/pages/mine/posts' })
      },
      openService(key) {
        if (key === 'posts') {
          this.goMyPosts()
        } else if (key === 'sign') {
          uni.navigateTo({ url: '/pages/sign/sign' })
        } else if (key === 'ranking') {
          this.$emit('open-discover', 1)
        } else if (key === 'activity') {
          this.$emit('open-discover', 2)
        }
      },
      handleLogout() {
        uni.showModal({
          title: '提示',
          content: '确定要退出登录吗？',
          success: async (result) => {
            if (!result.confirm) return
            try { await user.logout() } catch (error) {}
            session.clearAuthSession()
            this.isLoggedIn = false
            this.userInfo = {}
            this.summary = { postCount: 0, receivedLikeCount: 0, commentCount: 0 }
            uni.showToast({ title: '已退出登录', icon: 'success' })
          }
        })
      },
      copySource() {
        uni.setClipboardData({ data: 'https://github.com/haojiawei2018/community.git' })
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
  .pagesE { min-height: 100vh; }
  .custom-nav { height: 100%; }
  .mine-content { z-index: 1; position: relative; }
  .mine-fixed {
    position: fixed; background: linear-gradient(90deg, #c9febf, #F6F6F6); top: 0;
    width: 100%; height: 450rpx; transition: all .25s ease-out; z-index: 0;
  }
  .mine-fixed::before {
    content: ""; position: absolute; inset: 0; z-index: 0;
    mask-image: linear-gradient(to bottom, transparent, black); background: #F6F6F6;
  }
  .logo-image { width: 110rpx; height: 110rpx; position: relative; overflow: hidden; border-radius: 50%; }
  .logo-pic {
    border: 8rpx solid rgba(255,255,255,.6); box-shadow: 0 0 50rpx rgba(0,0,0,.1);
    border-radius: 50%; overflow: hidden; flex-shrink: 0;
  }
  .profile-subtitle { max-width: 430rpx; }
  .setting-icon { font-size: 50rpx; opacity: .5; padding: 16rpx; }
  .quick-actions { padding: 30rpx 60rpx 10rpx; }
  .quick-icon {
    width: 58rpx; height: 58rpx; font-size: 42rpx; border-radius: 50%; margin-bottom: 10rpx;
    background: #000; color: #FFF; position: relative;
  }
  .common-card { background: #FFF; border-radius: 18rpx; margin: 20rpx 30rpx; }
  .service-card { margin: 20rpx 30rpx; padding: 26rpx 18rpx 20rpx; border-radius: 18rpx; background: #FFF; }
  .service-header { padding: 0 12rpx 20rpx; }
  .service-grid { width: 100%; }
  .service-item { width: 25%; padding: 18rpx 4rpx 20rpx; box-sizing: border-box; text-align: center; }
  .service-icon {
    width: 82rpx; height: 82rpx; margin: 0 auto; border-radius: 18rpx; color: #FFF;
    font-size: 45rpx; line-height: 82rpx; box-shadow: 10rpx 10rpx 0 rgba(0,0,0,.06);
  }
  .service-icon--pink { background: linear-gradient(145deg, #FFB5C7, #ED5A86); }
  .service-icon--purple { background: linear-gradient(145deg, #C6C0FF, #7A6EF2); }
  .service-icon--blue { background: linear-gradient(145deg, #9FC1FF, #668FEF); }
  .service-icon--orange { background: linear-gradient(145deg, #FFD19B, #F2A04B); }
  .service-name { padding-top: 18rpx; color: #444; font-size: 25rpx; white-space: nowrap; }
  .logout-text { color: #111111; }
  .opensource-signature {
    padding: 22rpx 30rpx 8rpx;
    color: #B8B8B8;
    font-size: 22rpx;
    letter-spacing: 2rpx;
    text-align: center;
  }
  .icon1__item--icon { width: 50rpx; height: 50rpx; font-size: 40rpx; border-radius: 50%; position: relative; z-index: 1; }
  .profile-modal { padding: 12rpx 4rpx; }
  .profile-avatar-editor { width: 150rpx; margin: 28rpx auto 0; padding-bottom: 10rpx; text-align: center; }
  .profile-avatar-image { width: 112rpx; height: 112rpx; border-radius: 50%; border: 6rpx solid #F2F2F2; }
  .profile-avatar-tip { height: 34rpx; color: #777; font-size: 22rpx; line-height: 34rpx; white-space: nowrap; }
  .form-label { margin: 22rpx 0 12rpx; color: #555; font-size: 25rpx; line-height: 36rpx; }
  .form-input, .form-textarea { width: 100%; box-sizing: border-box; background: #F5F5F5; border-radius: 14rpx; padding: 22rpx; font-size: 28rpx; }
  .form-input { height: 76rpx; line-height: 36rpx; }
  .form-textarea { height: 180rpx; }
  .modal-button { flex: 1; margin: 0 8rpx; padding: 22rpx; border-radius: 100rpx; text-align: center; }
  .modal-button--light { background: #F1F1F1; color: #555; }
  .modal-button--dark { background: #111; color: #FFF; }
  .tn-tabbar-height {
    min-height: 120rpx;
    height: calc(140rpx + env(safe-area-inset-bottom) / 2);
    height: calc(140rpx + constant(safe-area-inset-bottom));
  }
</style>
