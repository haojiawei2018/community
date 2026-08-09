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
      
      <view class="tn-flex tn-flex-col-top" :style="{paddingTop: vuex_custom_bar_height - 40 +'px'}">
            
        <view class="tn-padding-bottom">
          <view class="logo-pic">
            <view class="logo-image" style="background-image:url('https://resource.tuniaokj.com/images/flower/guye1.jpg');width: 90rpx;height: 90rpx;background-size: cover;overflow: hidden;">
            </view>
          </view>
          <!-- <view class="tn-icon-fire tn-color-red" style="position: absolute;margin: -105rpx 0 0 72rpx;border-radius: 100rpx;padding: 5rpx;"></view> -->
        </view>
        <view class="tn-padding-left-sm" style="width: 100%;">
          <view class="tn-padding-top-xs">
            <text class="tn-text-bold tn-text-lg">抓住那只猪</text>
          </view>
          <view class="tn-flex tn-flex-row-between tn-flex-col-between tn-padding-top-xs">
            <view class="justify-content-item">
              <text class="tn-text-sm">眼里有光，世界将为你亮起！</text>
            </view>
            <view class="justify-content-item tn-round tn-text-xs tn-color-gray--light">
              <!-- <text class="tn-padding-right-xs"></text> 预留 -->
            </view>
          </view>
        </view>
      </view>
      
      <scroll-view scroll-y="true" style="max-height: 70vh;margin: 20rpx 0;">
        <view class="tn-margin-top-sm" style="border-radius: 24rpx;color: #1D1F24;">
          <view class="tn-flex tn-flex-row-between tn-flex-col-center" v-for="(item,index) in app" :key="index" @click="tn('')" style="padding: 30rpx 10rpx;">
            <view class="justify-content-item tn-flex tn-flex-col-center">
              <view class="app-pic">
                <view class="app-image" :style="'background-image:url(' + item.mainImage + ');background-size: cover;'">
                </view>
              </view>
              <view class="tn-padding-left-xs clamp-text-1">{{ item.title }}</view>
            </view>
            <view class="justify-content-item">
              <text class="tn-icon-right"></text>
            </view>
          </view>
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
      }
    },
    data() {
      return {
        openModal: false,
        
        app: [
          {
            title: '蛋仔派对',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716013944651-assets/web-upload/32eafb0d-1e25-452b-9ad9-d14c18ec90f6.jpeg',
          },
          {
            title: '保卫萝卜',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            title: '旅行青蛙',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014232876-assets/web-upload/5e1d7c68-048e-4b54-afda-68164a5bebd0.jpeg',
          },
          {
            title: '辐射避难所',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg',
          },
          {
            title: '王国保卫战',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025891640-assets/web-upload/8b4440ce-710b-4d3c-afff-5d49e6c7a16a.jpeg',
          },
          {
            title: '崩坏 · 星穹铁道',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399286-assets/web-upload/7a715327-f597-4a8e-ac3c-2ba7ab45b6b9.jpeg',
          },
          {
            title: '纪念碑谷',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014593433-assets/web-upload/6e1ba4f3-4757-4693-b827-b8803072c5c9.jpeg',
          },
          {
            title: '蛋仔派对',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716013944651-assets/web-upload/32eafb0d-1e25-452b-9ad9-d14c18ec90f6.jpeg',
          },
          {
            title: '保卫萝卜',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            title: '旅行青蛙',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014232876-assets/web-upload/5e1d7c68-048e-4b54-afda-68164a5bebd0.jpeg',
          },
          {
            title: '辐射避难所',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg',
          },
          {
            title: '王国保卫战',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025891640-assets/web-upload/8b4440ce-710b-4d3c-afff-5d49e6c7a16a.jpeg',
          },
          {
            title: '崩坏 · 星穹铁道',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399286-assets/web-upload/7a715327-f597-4a8e-ac3c-2ba7ab45b6b9.jpeg',
          },
          {
            title: '纪念碑谷',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014593433-assets/web-upload/6e1ba4f3-4757-4693-b827-b8803072c5c9.jpeg',
          },
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
        wx.vibrateShort();
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