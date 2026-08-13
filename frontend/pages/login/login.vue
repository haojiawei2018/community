<template>
  <view class="template-login tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar
      :isBack="false"
      :bottomShadow="false"
      backgroundColor="#FFFFFF00"
      fontColor="#FFFFFF"
    >
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-center">
        <text class="nav-title tn-text-bold tn-text-xl">{{ mode === 'login' ? '登录' : '注册' }}</text>
      </view>
    </tn-nav-bar>

    <!-- 顶部渐变背景 -->
    <view
      class="top-bg"
      :style="{ height: 'calc(' + vuex_custom_bar_height + 'px + 440rpx)' }"
    >
      <view class="top-bg__halo top-bg__halo--left"></view>
      <view class="top-bg__halo top-bg__halo--right"></view>
    </view>

    <view
      class="login-wrap"
      :style="{ paddingTop: 'calc(' + vuex_custom_bar_height + 'px + 28rpx)' }"
    >
      <!-- Logo / 标题 -->
      <view class="login-header tn-text-center">
        <view class="logo-icon tn-flex tn-flex-row-center tn-flex-col-center">
          <image v-if="community.logoUrl" class="community-logo" :src="community.logoUrl" mode="aspectFill"></image>
          <text v-else class="tn-icon-game-fill tn-text-xxxl"></text>
        </view>
        <view class="community-name tn-text-bold">{{ community.communityName || '游戏社区' }}</view>
        <view class="community-slogan">发现更多游戏乐趣</view>
      </view>

      <!-- 表单区域 -->
      <view class="login-form">
        <view class="auth-tabs">
          <view
            class="auth-tabs__item"
            :class="{ 'auth-tabs__item--active': mode === 'login' }"
            @tap="setMode('login')"
          >登录</view>
          <view
            class="auth-tabs__item"
            :class="{ 'auth-tabs__item--active': mode === 'register' }"
            @tap="setMode('register')"
          >注册</view>
        </view>

        <view class="form-intro">
          <view class="form-intro__title">{{ mode === 'login' ? '欢迎回来' : '创建社区账号' }}</view>
          <view class="form-intro__desc">
            {{ mode === 'login' ? '登录后参与讨论，发现更多同好' : '注册后即可发布帖子和参与互动' }}
          </view>
        </view>

        <!-- 账号输入 -->
        <view class="form-item">
          <view class="form-item__label">
            <text class="tn-icon-my tn-text-lg"></text>
          </view>
          <input
            class="form-item__input"
            type="text"
            v-model="formData.username"
            placeholder="请输入账号"
            placeholder-class="form-item__placeholder"
            maxlength="32"
          />
        </view>

        <!-- 密码输入 -->
        <view class="form-item">
          <view class="form-item__label">
            <text class="tn-icon-lock tn-text-lg"></text>
          </view>
          <input
            class="form-item__input"
            :password="!showPassword"
            v-model="formData.password"
            placeholder="请输入密码"
            placeholder-class="form-item__placeholder"
            maxlength="72"
          />
          <view class="form-item__suffix" @click="showPassword = !showPassword">
            <text :class="showPassword ? 'tn-icon-eye' : 'tn-icon-eye-hide'" class="tn-text-lg tn-color-gray"></text>
          </view>
        </view>

        <!-- 注册模式下追加社区昵称 -->
        <view class="form-item" v-if="mode === 'register'">
          <view class="form-item__label">
            <text class="tn-icon-my-simple tn-text-lg"></text>
          </view>
          <input
            class="form-item__input"
            type="text"
            v-model="formData.nickname"
            placeholder="请输入社区昵称"
            placeholder-class="form-item__placeholder"
            maxlength="20"
          />
        </view>

        <view v-if="mode === 'register'" class="register-tip">
          账号以字母开头，可使用字母、数字和下划线；密码为 8-72 位
        </view>

        <!-- 登录按钮 -->
        <view class="tn-margin-top-xl">
          <tn-button
            backgroundColor="#000000"
            fontColor="#FFFFFF"
            width="100%"
            padding="40rpx 0"
            shape="round"
            :loading="loading"
            :disabled="loading"
            @click="handleSubmit"
          >
            <text class="tn-text-bold">{{ mode === 'login' ? '登 录' : '注 册' }}</text>
          </tn-button>
        </view>

        <!-- 切换登录/注册 -->
        <view class="tn-text-center tn-margin-top-lg" @click="toggleMode">
          <text class="tn-color-gray tn-text-sm">
            {{ mode === 'login' ? '还没有账号？去注册' : '已有账号？去登录' }}
          </text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  import { user, community as communityApi } from '@/api/index.js'
  import session from '@/utils/session.js'

  export default {
    name: 'login',
    data() {
      return {
        // 当前模式：login 登录 / register 注册
        mode: 'login',
        // 表单数据
        formData: {
          username: '',
          password: '',
          nickname: ''
        },
        community: session.getCommunity(),
        // 是否明文显示密码
        showPassword: false,
        // 提交中
        loading: false,
        // 从帖子互动进入登录页时，登录成功后返回原页面
        returnAfterLogin: false
      }
    },
    onLoad(options = {}) {
      this.returnAfterLogin = String(options.returnAfterLogin || '') === '1'
      this.loadCommunity()
    },
    methods: {
      async loadCommunity() {
        try {
          const value = await communityApi.getBootstrap({ custom: { silent: true } })
          this.community = value || {}
          session.saveCommunity(this.community)
        } catch (error) {
          this.community = session.getCommunity()
        }
      },

      // 切换登录/注册模式
      toggleMode() {
        this.setMode(this.mode === 'login' ? 'register' : 'login')
      },

      setMode(mode) {
        if (this.mode === mode) return
        this.mode = mode
        if (mode === 'login') this.formData.nickname = ''
      },

      // 表单校验
      validate() {
        const { username, password, nickname } = this.formData
        if (!username.trim()) {
          uni.showToast({ title: '请输入账号', icon: 'none' })
          return false
        }
        if (!password) {
          uni.showToast({ title: '请输入密码', icon: 'none' })
          return false
        }
        if (this.mode === 'register') {
          if (!/^[A-Za-z][A-Za-z0-9_]{3,31}$/.test(username.trim())) {
            uni.showToast({ title: '账号须以字母开头，长度 4-32 位', icon: 'none' })
            return false
          }
          if (password.length < 8 || password.length > 72) {
            uni.showToast({ title: '密码长度须为 8-72 位', icon: 'none' })
            return false
          }
          if (!nickname.trim()) {
            uni.showToast({ title: '请输入社区昵称', icon: 'none' })
            return false
          }
        }
        return true
      },

      // 提交登录/注册
      async handleSubmit() {
        if (!this.validate()) return
        this.loading = true
        try {
          const { username, password, nickname } = this.formData
          let res
          if (this.mode === 'login') {
            res = await user.login(username.trim(), password)
          } else {
            res = await user.register(username.trim(), password, nickname.trim())
          }
          if (!session.saveAuthSession(res)) {
            console.error('[login] 返回数据缺少 accessToken:', res)
            uni.showToast({ title: '登录失败，请检查账号和密码', icon: 'none' })
            return
          }
          uni.showToast({ title: this.mode === 'login' ? '登录成功' : '注册成功', icon: 'success' })
          console.log('[login] 已写入标准登录会话:', JSON.stringify(res && res.user))
          // 帖子互动登录后返回原详情页；普通入口仍切换到“我的”。
          setTimeout(() => {
            if (this.returnAfterLogin) {
              uni.navigateBack({ delta: 1 })
            } else {
              uni.reLaunch({ url: '/pages/index/index?tab=4' })
            }
          }, 800)
        } catch (err) {
          // 响应拦截器已统一提示 message，这里兜底确保一定有提示
          console.error('[login] 提交失败：', err)
          const msg = (err && (err.message || err.msg)) || (this.mode === 'login' ? '登录失败，请检查账号和密码' : '注册失败，请稍后重试')
          // 拦截器可能已弹过 toast，这里仅在没有 message 时补充
          if (!err || (!err.message && !err.msg)) {
            uni.showToast({ title: msg, icon: 'none' })
          }
        } finally {
          this.loading = false
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
  .template-login {
    min-height: 100vh;
    box-sizing: border-box;
    overflow-x: hidden;
    background-color: #FFFFFF;
    padding-bottom: calc(48rpx + constant(safe-area-inset-bottom));
    padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
  }

  .custom-nav {
    width: 100%;
    height: 100%;
  }

  .nav-title {
    color: #FFFFFF;
  }

  /* 顶部渐变背景 */
  .top-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    background: linear-gradient(180deg, #1A1A1A 0%, #000000 100%);
    border-bottom-left-radius: 60rpx;
    border-bottom-right-radius: 60rpx;
    z-index: 0;
    overflow: hidden;

    &__halo {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.055);

      &--left {
        width: 320rpx;
        height: 320rpx;
        left: -160rpx;
        bottom: -130rpx;
      }

      &--right {
        width: 220rpx;
        height: 220rpx;
        right: -70rpx;
        top: 150rpx;
      }
    }
  }

  .login-wrap {
    position: relative;
    z-index: 1;
    box-sizing: border-box;
    padding-left: 52rpx;
    padding-right: 52rpx;
  }

  /* Logo / 标题 */
  .login-header {
    padding-top: 12rpx;
    .logo-icon {
      width: 124rpx;
      height: 124rpx;
      border-radius: 28rpx;
      background-color: #FFFFFF;
      margin: 0 auto;
      box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.15);
      text {
        color: #000000;
      }
      .community-logo {
        width: 100%;
        height: 100%;
        border-radius: 28rpx;
      }
    }

    .community-name {
      margin-top: 24rpx;
      padding: 0 24rpx;
      color: #FFFFFF;
      font-size: 38rpx;
      line-height: 52rpx;
      word-break: break-all;
    }

    .community-slogan {
      margin-top: 8rpx;
      color: rgba(255, 255, 255, 0.72);
      font-size: 26rpx;
      line-height: 38rpx;
    }
  }

  /* 表单 */
  .login-form {
    background-color: #FFFFFF;
    border-radius: 24rpx;
    padding: 50rpx 40rpx;
    margin-top: 48rpx;
    box-shadow: 0 8rpx 40rpx rgba(0, 0, 0, 0.06);
  }

  .auth-tabs {
    display: flex;
    align-items: center;
    padding: 6rpx;
    margin-bottom: 38rpx;
    border-radius: 18rpx;
    background-color: #F4F4F4;

    &__item {
      flex: 1;
      height: 68rpx;
      color: #999999;
      font-size: 28rpx;
      line-height: 68rpx;
      text-align: center;
      border-radius: 14rpx;
      transition: all 0.2s ease;

      &--active {
        color: #FFFFFF;
        font-weight: bold;
        background-color: #000000;
        box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.14);
      }
    }
  }

  .form-intro {
    margin-bottom: 18rpx;

    &__title {
      color: #222222;
      font-size: 34rpx;
      font-weight: bold;
      line-height: 48rpx;
    }

    &__desc {
      margin-top: 6rpx;
      color: #AAAAAA;
      font-size: 24rpx;
      line-height: 36rpx;
    }
  }

  @media screen and (max-height: 700px) {
    .login-header {
      padding-top: 0;

      .logo-icon {
        width: 108rpx;
        height: 108rpx;
      }

      .community-name {
        margin-top: 18rpx;
      }
    }

    .login-form {
      margin-top: 36rpx;
      padding-top: 38rpx;
      padding-bottom: 38rpx;
    }
  }

  .form-item {
    display: flex;
    flex-direction: row;
    align-items: center;
    border-bottom: 1rpx solid #EFEFEF;
    padding: 24rpx 0;
    &__label {
      width: 60rpx;
      text {
        color: #888888;
      }
    }
    &__input {
      flex: 1;
      font-size: 30rpx;
      color: #333333;
      height: 60rpx;
      line-height: 60rpx;
    }
    &__placeholder {
      color: #BBBBBB;
      font-size: 30rpx;
    }
    &__suffix {
      width: 60rpx;
      text-align: center;
    }
  }

  .register-tip {
    margin-top: 18rpx;
    padding: 18rpx 20rpx;
    color: #888888;
    font-size: 22rpx;
    line-height: 34rpx;
    border-radius: 12rpx;
    background-color: #F7F7F7;
  }
</style>
