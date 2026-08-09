<template>
  <view
    v-if="openModal"
    class="wx-modal"
  >
    <view
      class="wam__mask"
      @touchmove.prevent=""
      @tap.stop="closeModal"
    ></view>
    
    <!-- 内容区域 -->
    <view class="wam__wrapper">
      <!-- 关闭按钮 -->
      <view class="wam__close-btn" @tap.stop="closeModal">
        <tn-button size="lg" shape="icon" backgroundColor="#00000000" fontColor="#AAAAAA">
          <text class="tn-icon-close"></text>
        </tn-button>
        <!-- <text class="tn-icon-close-fill tn-color-white"></text> -->
      </view>
      <view class="tn-margin-left tn-flex tn-flex-col-center">
        <view class="tn-text-bold tn-text-lg">应用类型</view>
      </view>
      
      <view class="tn-margin-top-sm" style="border-top: 1rpx solid #F8F7F8;">
        
        <scroll-view scroll-y="true" style="max-height: 55vh;">
          <!-- 内容 start-->
          <view class="tn-flex tn-flex-wrap tn-padding-top-sm" style="margin: 0 20rpx;">
            <block v-for="(item, index) in content" :key="index">
              <view class="" style="width: 50%;" @click="tn('/pageA/video/video')">
                <view class="real-content">
                  <view class="image-pic2" :style="'background-image:url(' + item.mainImage + ')'">
                    <view class="image-real">
                      <view class="tn-flex tn-flex-col-center tn-text-center" style="height: 38rpx;position: absolute;top: 10rpx;left:10rpx;background-color: #00000080;border-radius: 10rpx;">
                        <view class="tn-margin-xs tn-color-white">热门</view>
                      </view>
                      <view class="tn-text-df" style="width: 100%;height: 40rpx;background: linear-gradient(0deg, rgba(0,0,0,0.6), rgba(0,0,0,0));position: absolute;bottom: 0;">
                        <view class="tn-padding-left-xs tn-padding-right-xs tn-color-white clamp-text-1">
                          <text class="tn-icon-my"></text>
                          <text class="tn-padding-left-xs">{{ item.play }} 人选择</text>
                        </view>
                      </view>
                    </view>
                  </view>
          
                  <view class="tn-text-justify tn-padding-top-sm">
                    <text class="clamp-text-1">{{ item.title }}</text>
                  </view>
                </view>
              </view>
            </block>
          </view>
          <!-- 内容 end-->
        
        </scroll-view>
        
        <!-- 悬浮按钮-->
        <view class="tn-flex tn-footerfixed" style="border-top: 1rpx solid #F8F7F8;">
          <view class="tn-flex-1 justify-content-item tn-text-center">
            <tn-button backgroundColor="#000000 " padding="40rpx 0" width="100%" :fontSize="28" fontColor="#FFFFFF" shape="round" @click="tn('')">
              <text class="">确定选择</text>
            </tn-button>
          </view>
        </view>
        
        <view class="tn-tabbar-height"></view>
        
        
      </view>
      
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
      }
    },
    data() {
      return {
        openModal: false,
        content: [{
            play: '12K',
            number: '16',
            title: '经营策略',
            mainImage: 'https://cdn.nlark.com/yuque/0/2023/jpeg/280373/1685709204898-assets/web-upload/30641b38-b047-4df8-abc3-034dd7b44b0f.jpeg',
          },
          {
            play: '1.08W',
            number: '17',
            title: '动作射击',
            mainImage: 'https://cdn.nlark.com/yuque/0/2023/jpeg/280373/1685709204901-assets/web-upload/f665e2cd-9e54-42b2-b347-c4fac4ff7c49.jpeg',
          },
          {
            play: '2.06K',
            number: '12',
            title: '角色扮演',
            mainImage: 'https://cdn.nlark.com/yuque/0/2023/jpeg/280373/1685709204932-assets/web-upload/8323e8a1-b33e-47ac-96e1-82bc2e84e180.jpeg',
          },
          {
            play: '1.02W',
            number: '22',
            title: '休闲益智',
            mainImage: 'https://cdn.nlark.com/yuque/0/2023/jpeg/280373/1685709204911-assets/web-upload/f97f8bf5-ec93-4a35-b28e-d37387af66a0.jpeg',
          },
          {
            play: '2.92W',
            number: '12',
            title: '体育竞速',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '2.03W',
            number: '5',
            title: '棋牌桌游',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.69W',
            number: '8',
            title: '其他',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          }
        ],
 
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
      // 跳转
      tn(e) {
        uni.navigateTo({
          url: e,
        });
      },
      // 关闭弹框
      closeModal() {
        this.$emit('input', false)
      },
      
    }
  }
</script>

<style lang="scss" scoped>
  
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
      z-index: 9999 !important;
      /* mask */
      &__mask {
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        opacity: 0;
        animation: showMask 0.25s linear 0s forwards;
      }
      
      /* close-btn */
      &__close-btn {
        position: absolute;
        top: 20rpx;
        right: 10rpx;
        z-index: 99999;
        font-size: 40rpx;
      }
      
      /* wrapper */
      &__wrapper {
        position: absolute;
        left: 0;
        bottom: 0;
        width: 100%;
        background-color: #FFFFFF;
        border-radius: 40rpx 40rpx 0rpx 0rpx;
        padding-top: 40rpx;
        // padding-bottom: 40rpx;
        // padding-bottom: calc(constant(safe-area-inset-bottom) + 40rpx);
        // padding-bottom: calc(env(safe-area-inset-bottom) + 40rpx);
        transform-origin: center bottom;
        transform: scaleY(0);
        animation: showWrapper 0.25s linear 0s forwards;
        z-index: 99999;
      }
      
    }
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
      transform: scaleY(0);
      opacity: 0;
    }
    100% {
      transform: scaleY(1);
      opacity: 1;
    }
  }
  
  /* 底部悬浮按钮 start*/
  .tn-tabbar-height {
  	min-height: 20rpx;
  	height: calc(40rpx + env(safe-area-inset-bottom) / 2);
    height: calc(40rpx + constant(safe-area-inset-bottom));
  }
  .tn-footerfixed {
    width: 100%;
    margin-bottom: calc(env(safe-area-inset-bottom)- 20rpx );
    padding: 30rpx;
    z-index: 1024;
    box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0);
  
  }
  /* 底部悬浮按钮 end*/
  
  /* 内容 start*/
  .real-content {
    // box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.07);
    border-radius: 15rpx;
    // background-color: rgba(255, 255, 255, 1);
    margin: 10rpx 10rpx 30rpx 10rpx;
  }
  
  .image-real {
    height: 200rpx;
    font-size: 16rpx;
    font-weight: 300;
    position: relative;
    border-radius: 15rpx;
    overflow: hidden;
  }
  
  .image-pic2 {
    background-size: cover;
    background-repeat: no-repeat;
    // background-attachment:fixed;
    background-position: center;
    border-radius: 15rpx;
  }
  
</style>