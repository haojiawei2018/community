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
        <view class="tn-text-bold tn-text-lg">综合筛选</view>
      </view>
      
      <view class="tn-margin-top-sm" style="border-top: 1rpx solid #F8F7F8;">
        
        <scroll-view scroll-y="true" style="max-height: 55vh;">
          <!-- 内容 start-->
          <view class="tn-padding-top-sm" style="margin: 30rpx;">
            
            <view class="">
              应用权益
            </view>
            
            <view class="justify-content-item">
              <view class="tn-tag-content tn-text-justify">
                <view v-for="(item, index) in redpacket" :key="index" class="tn-tag-content__item tn-margin-right tn-text-sm" :class="[item.select ? 'bg-select' : 'bg-select-no']"  @click="handleRedpacketClick(index)">
                   {{ item.title }}
                </view>
              </view>
            </view>
            
            <view class="tn-margin-top-xl">
              价格区间
            </view>
            
            <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin-top">
              <view class="" style="width:30vw;border-radius: 100rpx;background-color: #F5F5F5;">
                <view class="tn-color-gray tn-color-black tn-text-center" style="padding: 14rpx;">
                  <input type="number" placeholder="最低价" name="input" placeholder-style="color:#AAAAAA;margin-left: 10rpx" value=""></input>
                </view>
              </view>
              <view class="tn-icon-sequence">
                
              </view>
              <view class="" style="width:30vw;border-radius: 100rpx;background-color: #F5F5F5;">
                <view class="tn-color-gray tn-color-black tn-text-center" style="padding: 14rpx;">
                  <input type="number" placeholder="最高价" name="input" placeholder-style="color:#AAAAAA;margin-left: 10rpx" value=""></input>
                </view>
              </view>
            </view>
            
          </view>
          <!-- 内容 end-->
        
        </scroll-view>
        
        <!-- 悬浮按钮-->
        <view class="tn-flex tn-footerfixed" style="border-top: 1rpx solid #F8F7F8;">
          <view class="tn-flex-1 justify-content-item tn-text-center tn-margin-right-sm">
            <tn-button :plain="true" backgroundColor="#AAAAAA " padding="39rpx 0" width="100%" :fontSize="28" fontColor="#AAAAAA" shape="round" @click="tn('')">
              <text class="">重 置</text>
            </tn-button>
          </view>
          <view class="tn-flex-1 justify-content-item tn-text-center tn-margin-left-sm">
            <tn-button backgroundColor="#000000 " padding="40rpx 0" width="100%" :fontSize="28" fontColor="#FFFFFF" shape="round" @click="tn('')">
              <text class="">确 定</text>
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
            title: '类型名称',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.08W',
            number: '17',
            title: '类型名称',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '2.06K',
            number: '12',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.02W',
            number: '22',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '2.92W',
            number: '12',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '2.03W',
            number: '5',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.69W',
            number: '8',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '118K',
            number: '6',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '2.03W',
            number: '16',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '2.16W',
            number: '8',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.89W',
            number: '6',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '3.03W',
            number: '6',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.32W',
            number: '18',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.92W',
            number: '6',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '1.52W',
            number: '16',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '4.35W',
            number: '15',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '4.57W',
            number: '19',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            play: '4.86W',
            number: '12',
            title: '那只猪不见了',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          }
        ],
        
        redpacket: [
          {
            title: "包邮",
            select: true
          },
          {
            title: "积分抵扣",
            select: false
          },
          {
            title: "24小时发货",
            select: false
          },
          {
            title: "赠送运险费",
            select: false
          },
          {
            title: "会员折扣",
            select: false
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
        uni.navigateTo({
          url: e,
        });
      },
      // 关闭弹框
      closeModal() {
        this.$emit('input', false)
      },
      
      // 处理点击事件
      handleRedpacketClick(index) {
        this.redpacket[index].select = !this.redpacket[index].select
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
  
  /* 标签内容 start*/
  .tn-tag-content {
    &__item {
      display: inline-block;
      line-height: 45rpx;
      padding: 8rpx 24rpx;
      margin: 24rpx 30rpx 5rpx 0rpx;
      border-radius: 100rpx;
      min-width: 140rpx;
      text-align: center;
      
      &--prefix {
        padding-right: 10rpx;
      }  
    }
  }
  /* 标签内容 end*/
  
  .bg-select-no{
    background-color: #F5F5F5;
    border: 1rpx solid #F5F5F5;
  }
  
  .bg-select{
    background-color: #00000020;
    border: 1rpx solid #000000;
    color: #000000;
  }
  
</style>