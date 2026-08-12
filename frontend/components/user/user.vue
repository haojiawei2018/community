<template>
  <view
    v-if="openModal"
    class="wx-modal"
  >
    <view
      class="wam__mask"
      @touchmove.prevent="closeModal"
      @tap.stop="closeModal"
    ></view>
    
    <!-- 内容区域 -->
    <view class="wam__wrapper dd-glass">
      <!-- 关闭按钮 -->
      <!-- <view class="wam__close-btn" @tap.stop="closeModal">
        <text class="tn-icon-close"></text>
      </view> -->
      
      <view class="community-head tn-flex tn-flex-col-center" :style="{ paddingTop: vuex_custom_bar_height - 40 + 'px' }">
        <view class="community-logo">
          <image v-if="community.logoUrl" class="community-logo__image" :src="community.logoUrl" mode="aspectFill"></image>
          <image v-else class="community-logo__image" src="/static/images/default-avatar.png" mode="aspectFill"></image>
        </view>
        <view class="tn-padding-left-sm community-head__content">
          <view class="tn-text-bold tn-text-lg clamp-text-1">{{ communityName }}</view>
          <view class="tn-padding-top-xs tn-text-sm tn-color-gray">选择圈子查看对应内容</view>
        </view>
      </view>
      
      <scroll-view scroll-y="true" style="max-height: 70vh;margin: 20rpx 0;">
        <view class="circle-list tn-margin-top-sm">
          <view class="circle-item tn-flex tn-flex-row-between tn-flex-col-center"
            :class="{ 'circle-item--active': !activeCircleId }" @tap.stop="selectCircle('')">
            <view class="justify-content-item tn-flex tn-flex-col-center">
              <view class="circle-item__icon tn-icon-home-fill"></view>
              <view class="tn-padding-left-xs clamp-text-1">全部内容</view>
            </view>
            <view v-if="!activeCircleId" class="circle-item__selected tn-icon-success"></view>
          </view>

          <view class="circle-item tn-flex tn-flex-row-between tn-flex-col-center"
            v-for="circle in circles" :key="circle.id"
            :class="{ 'circle-item--active': String(activeCircleId) === String(circle.id) }"
            @tap.stop="selectCircle(circle.id)">
            <view class="justify-content-item tn-flex tn-flex-col-center circle-item__main">
              <image v-if="circle.iconUrl" class="circle-item__image" :src="circle.iconUrl" mode="aspectFill"></image>
              <view v-else class="circle-item__icon tn-icon-topics-fill"></view>
              <view class="tn-padding-left-xs clamp-text-1">{{ circle.circleName }}</view>
            </view>
            <view v-if="String(activeCircleId) === String(circle.id)" class="circle-item__selected tn-icon-success"></view>
          </view>

          <view v-if="!circles.length" class="circle-empty tn-color-gray tn-text-center">当前社区暂无圈子</view>
        </view>
        
        <view class="tn-padding-bottom">
          
        </view>
        
      </scroll-view>
      
      <!-- 悬浮按钮-->
      <!-- <view class="tn-flex tn-footerfixed">
        <view class="tn-flex-1 justify-content-item tn-text-center">
          <tn-button backgroundColor="#000000" padding="40rpx 0" width="100%" :fontSize="28" fontColor="#FFFFFF" shape="round" @click="tn('')">
            <text class="">添加应用</text>
          </tn-button>
        </view>
      </view> -->
      
      
    </view>
  </view>
</template>

<script>
  
  export default {
    options: {
      // 在微信小程序中将组件节点渲染为虚拟节点，更加接近Vue组件的表现(不会出现shadow节点下再去创建元素)
      virtualHost: true
    },
    props: {
      value: {
        type: Boolean,
        default: false
      },
      community: {
        type: Object,
        default: () => ({ communityName: '开源社区' })
      },
      circles: {
        type: Array,
        default: () => []
      },
      activeCircleId: {
        type: [String, Number],
        default: ''
      }
    },
    data() {
      return {
        openModal: false
      }
    },
    computed: {
      communityName() {
        return this.community.communityName || '开源社区'
      }
    },
    watch: {
      value: {
        handler(val) {
          this.openModal = val
        },
        immediate: true
      }
    },
    methods: {
      selectCircle(circleId) {
        // #ifdef MP-WEIXIN
        wx.vibrateShort()
        // #endif
        this.$emit('select-circle', circleId)
        this.closeModal()
      },
      
      // 关闭弹框
      closeModal() {
        this.$emit('input', false)
      },
      
      
    }
  }
</script>

<style lang="scss" scoped>
  .community-head {
    padding-bottom: 28rpx;

    &__content {
      min-width: 0;
      flex: 1;
    }
  }

  .community-logo {
    width: 90rpx;
    height: 90rpx;
    overflow: hidden;
    border: 6rpx solid rgba(255, 255, 255, 0.7);
    border-radius: 50%;
    background-color: #F0F8EC;
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.12);

    &__image {
      width: 100%;
      height: 100%;
    }

    &__fallback {
      display: block;
      color: #FFFFFF;
      font-size: 38rpx;
      font-weight: 700;
      line-height: 90rpx;
      text-align: center;
    }
  }

  .circle-list {
    overflow: hidden;
    border-radius: 24rpx;
    background-color: rgba(255, 255, 255, 0.55);
  }

  .circle-item {
    min-height: 96rpx;
    padding: 20rpx;
    color: #1D1F24;
    border-bottom: 1rpx solid rgba(0, 0, 0, 0.05);

    &:last-child {
      border-bottom: 0;
    }

    &--active {
      background-color: rgba(255, 255, 255, 0.9);
      font-weight: 700;
    }

    &__main {
      min-width: 0;
    }

    &__icon,
    &__image {
      width: 52rpx;
      height: 52rpx;
      flex-shrink: 0;
      border-radius: 50%;
    }

    &__icon {
      background-color: #0F0F0F;
      color: #FFFFFF;
      font-size: 28rpx;
      line-height: 52rpx;
      text-align: center;
    }

    &__selected {
      color: #0F0F0F;
      font-size: 30rpx;
    }
  }

  .circle-empty {
    padding: 50rpx 20rpx;
  }
  
  /* 用户头像 start */
  .logo-image {
    width: 90rpx;
    height: 90rpx;
    position: relative;
    overflow: hidden;
    border-radius: 100rpx;
  }
  
  .logo-pic {
    background-size: cover;
    background-repeat: no-repeat;
    // background-attachment:fixed;
    background-position: top;
    border: 6rpx solid rgba(255,255,255,0.6);
    box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.1);
    border-radius: 100rpx;
    overflow: hidden;
    // background-color: #FFFFFF;
  }
  
  /* 应用头像 start */
  .app-image {
    width: 40rpx;
    height: 40rpx;
    position: relative;
    overflow: hidden;
    border-radius: 50%;
  }
  
  .app-pic {
    background-size: cover;
    background-repeat: no-repeat;
    // background-attachment:fixed;
    background-position: top;
    border: 1rpx solid rgba(255,255,255,0.6);
    box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.1);
    border-radius: 50%;
    overflow: hidden;
    // background-color: #FFFFFF;
  }
  
  .wx-modal {
    position: fixed;
    left: 0;
    top: 0;
    width: 100vw;
    height: 100vh;
    z-index: 99998 !important;
    
    view {
      box-sizing: border-box;
    }
    
    .image {
      width: 100%;
      height: 100%;
      border-radius: inherit;
    }
    
    .wam {
      /* mask */
      &__mask {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.2);
        opacity: 0;
        animation: showMask 0.25s ease 0s forwards;
      }
      
      /* close-btn */
      &__close-btn {
        position: absolute;
        top: 30rpx;
        right: 30rpx;
        z-index: 99999;
        font-size: 40rpx;
      }
      
      /* wrapper */
      &__wrapper {
        position: absolute;
        left: 0;
        top: 0;
        width: 64%;
        height: 100%;
        border-radius: 0rpx;
        padding: 20rpx;
        padding-top: 60rpx;
        padding-bottom: 40rpx;
        padding-bottom: calc(constant(safe-area-inset-bottom) + 40rpx);
        padding-bottom: calc(env(safe-area-inset-bottom) + 40rpx);
        transform-origin: center bottom;
        transform: scaleY(0);
        animation: showWrapper 0.4s ease 0.05s forwards;
        z-index: 99999;
      }
      
    }
  }
  
  .tn-btn-hover-class {
    box-shadow: inset 10rpx 2rpx 40rpx 0rpx rgba(0, 0, 0, 0.05);
  }
  
  @keyframes showMask {
    0% {
      opacity: 0;
    }
    100% {
      opacity: 1;
    }
  }
  @keyframes showWrapper {
    0% {
      transform: translateX(-80vw);
    }
    100% {
      transform: translateX(0vw);
    }
  }
  
  /* 文字截取*/
  .clamp-text-1 {
    -webkit-line-clamp: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  
  .clamp-text-2 {
    -webkit-line-clamp: 2;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  
  /* 毛玻璃*/
  .dd-glass {
    width: 100%;
    backdrop-filter: blur(8rpx);
    -webkit-backdrop-filter: blur(8rpx);
    // background-color: rgba(255,255,255,0.4);
    background: linear-gradient(90deg, #56FFFF80, #FFFFFF80);
    border-right: 2rpx solid #e1e1e1e0;
  }
  
  /* 底部悬浮按钮 start*/
  .tn-footerfixed {
    width: 100%;
    margin-top: 30rpx;
    margin-bottom: calc(60rpx + env(safe-area-inset-bottom) / 2);
    margin-bottom: calc(60rpx + constant(safe-area-inset-bottom));
    z-index: 1024;
    box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0);
  
  }
  /* 底部悬浮按钮 end*/
</style>
