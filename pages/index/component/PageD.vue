<template>
	<view class="pagesD tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F6">
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left">
        <!-- <view class="custom-nav__logo" @click="tn('')">
          <view class="tn-icon-tree leaf-color"></view>
        </view> -->
        <view class="">
          <text class="tn-margin-left tn-text-bold tn-text-xl leaf-color">消 息</text>
        </view>
      </view>
    </tn-nav-bar>
    
    <!-- 方式1 start-->
    <view class="tn-flex message-fixed" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
      <view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="tn('/momentPages/message')">
        <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
          <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-bg-white tn-color-black">
            <view class="tn-icon-topics-fill">
              <!-- <tn-badge backgroundColor="#EA5E55" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
                <text>16</text>
              </tn-badge> -->
            </view>
          </view>  
          <view class="tn-color-gray--dark tn-text-center">
            <text class="tn-text-ellipsis">互 动</text>
          </view>
        </view>
      </view>
      <view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="tn('/homePages/pending')">
        <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
          <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-bg-white tn-color-black">
            <view class="tn-icon-like-lack-fill">
              <!-- <tn-badge backgroundColor="#EA5E55" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
                <text>8</text>
              </tn-badge> -->
            </view>
          </view>  
          <view class="tn-color-gray--dark tn-text-center">
            <text class="tn-text-ellipsis">点 赞</text>
          </view>
        </view>
      </view>
      <view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="tn('/homePages/approval')">
        <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
          <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-bg-white tn-color-black">
            <view class="tn-icon-medal">
              <!-- <tn-badge backgroundColor="#EA5E55" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
                <text>12</text>
              </tn-badge> -->
            </view>
          </view>  
          <view class="tn-color-gray--dark tn-text-center">
            <text class="tn-text-ellipsis">活 动</text>
          </view>
        </view>
      </view>
      <view class="tn-flex-1 tn-padding-sm tn-margin-xs tn-radius" @click="tn('/homePages/notice')">
        <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
          <view class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-bg-white tn-color-black">
            <view class="tn-icon-notice-fill">
              <tn-badge backgroundColor="#EA5E55" fontColor="#FFFFFF" :absolute="true" :fontSize="16">
                <text>99+</text>
              </tn-badge>
            </view>
          </view>  
          <view class="tn-color-gray--dark tn-text-center">
            <text class="tn-text-ellipsis">系 统</text>
          </view>
        </view>
      </view>
    </view>
    <!-- 方式1 end-->
    
    <view class="tn-margin-top-sm" :style="{paddingTop: vuex_custom_bar_height + 95 +'px'}">
      <view class="">
        <view class="message-bg tn-flex tn-flex-col-center" @click="tn('/homePages/application')">
          <view class="">
            <view class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-color-white tn-bg-cyan">
              <view class="tn-icon-menu-fill"></view>
            </view>
          </view>
          <view class="tn-padding-left-sm" style="width: 62vw">
            <view class="tn-flex tn-flex-row-between tn-flex-col-between">
              <view class="justify-content-item">
                <text class="tn-text-lg">应用消息</text>
              </view>
            </view>
            <view class="tn-padding-top-xs tn-text-ellipsis">
              <text class="tn-color-gray tn-text-sm">新版来袭，确定不了解一下？</text>
            </view>
          </view>
          <view class="" style="width: 20vw;">
            <view class="tn-flex tn-flex-row-right tn-margin-bottom-xs tn-margin-top-xs">
              <text class="tn-color-gray tn-text-sm">18:00</text>
            </view>
            <view class="tn-flex tn-flex-row-right tn-margin-top-xs">
              <tn-badge backgroundColor="#EA5E55" fontColor="tn-color-white" :radius="30" margin="10rpx 0rpx 10rpx 10rpx"><text>1</text></tn-badge>
            </view>
          </view>
        </view>
        
        <view class="message-bg tn-flex tn-flex-col-center" @click="tn('/homePages/article')">
          <view class="">
            <view class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-color-white tn-bg-orangered">
              <view class="tn-icon-image-text-fill"></view>
            </view>
          </view>
          <view class="tn-padding-left-sm" style="width: 62vw">
            <view class="tn-flex tn-flex-row-between tn-flex-col-between">
              <view class="justify-content-item">
                <text class="tn-text-lg">行业资讯</text>
              </view>
            </view>
            <view class="tn-padding-top-xs tn-text-ellipsis">
              <text class="tn-color-gray tn-text-sm">开一局，说开就开</text>
            </view>
          </view>
          <view class="" style="width: 20vw;">
            <view class="tn-flex tn-flex-row-right tn-margin-bottom-xs tn-margin-top-xs">
              <text class="tn-color-gray tn-text-sm">12:29</text>
            </view>
            <view class="tn-flex tn-flex-row-right tn-margin-top-xs">
              <tn-badge backgroundColor="#EA5E55" fontColor="tn-color-white" :radius="30" margin="10rpx 0rpx 10rpx 10rpx"><text>2</text></tn-badge>
            </view>
          </view>
        </view>
        
        <!-- <view class="message-bg tn-flex tn-flex-col-center" @click="tn('/homePages/chat')">
          <view class="">
            <view class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-color-white tn-bg-orange">
              <view class="tn-icon-my-simple-fill"></view>
            </view>
          </view>
          <view class="tn-padding-left-sm" style="width: 62vw">
            <view class="tn-flex tn-flex-row-between tn-flex-col-between">
              <view class="justify-content-item">
                <text class="tn-text-lg">蔡东东</text>
              </view>
            </view>
            <view class="tn-padding-top-xs tn-text-ellipsis">
              <text class="tn-color-gray tn-text-sm">麻烦帮我抓住那只猪</text>
            </view>
          </view>
          <view class="" style="width: 20vw;">
            <view class="tn-flex tn-flex-row-right tn-margin-bottom-xs tn-margin-top-xs">
              <text class="tn-color-gray tn-text-sm">10:68</text>
            </view>
            <view class="tn-flex tn-flex-row-right tn-margin-top-xs">
              <tn-badge backgroundColor="#EA5E55" fontColor="tn-color-white" :radius="30" margin="10rpx 0rpx 10rpx 10rpx"><text>6</text></tn-badge>
            </view>
          </view>
        </view> -->
        
        
        
        <view class="message-bg tn-flex tn-flex-col-center" v-for="(item,index) in message" :key="index" @click="tn('/homePages/chat')">
          <view class="">
            <view class="logo-pic">
              <view class="logo-image">
                <view class="" :style="'background-image:url(' + item.mainImage + ');width: 90rpx;height: 90rpx;background-size: cover;'">
                </view>
              </view>
            </view>
          </view>
          <view class="tn-padding-left-sm" style="width: 62vw">
            <view class="tn-flex tn-flex-row-between tn-flex-col-between">
              <view class="justify-content-item">
                <text class="tn-text-lg">{{ item.title }}</text>
              </view>
              
            </view>
            <view class="tn-padding-top-xs tn-text-ellipsis">
              <text class="tn-color-gray tn-text-sm">{{ item.desc }}</text>
            </view>
          </view>
          <view class="" style="width: 20vw;">
            <view class="tn-flex tn-flex-row-right tn-margin-bottom-xs tn-margin-top-xs">
              <text class="tn-color-gray tn-text-sm">{{ item.time }}</text>
            </view>
            <view class="tn-flex tn-flex-row-right tn-margin-top-xs">
              <!-- <tn-badge backgroundColor="#EA5E55" fontColor="tn-color-white" :radius="30" margin="10rpx 0rpx 10rpx 10rpx"><text>6</text></tn-badge> -->
            </view>
          </view>
        </view>
        
      </view>
  
    </view>
    
    <view class="tn-tabbar-height"></view>
    
	</view>
</template>

<script>
	import { message as messageApi } from '@/api/index.js'
	export default {
    name: 'PageD',
	data() {
			return {
        message: [],

		}
		},
		onLoad() {
      this.loadMessage()
		},
		methods: {
      // 加载消息列表
      async loadMessage() {
        try {
          const res = await messageApi.getList()
          this.message = res.list
        } catch (e) {}
      },
      // 跳转
      tn(e) {
        uni.navigateTo({
          url: e,
        });
      },
		}
	}
</script>

<style lang="scss" scoped>
	.pagesD{
	  max-height: 100vh;
	}
  
  /* 自定义导航栏内容 start */
  .custom-nav {
    height: 100%;
  
    &__logo {
      margin: auto 5rpx;
      font-size: 50rpx;
      margin-right: 10rpx;
      margin-left: 30rpx;
      flex-basis: 5%;
    }
  }
  /* 自定义导航栏内容 end */
  
  
  .message-bg{
    background-color: #FFFFFF;
    border-radius: 18rpx;
    margin: 20rpx 30rpx;
    padding: 20rpx;
  }
  
  // 四个角渐变底色
  .message-fixed{
    position: fixed;
    // background: linear-gradient(90deg, #d8feda, #F6F6F6);
    // background: linear-gradient(90deg, #c9febf, #F6F6F6);
    background: #F6F6F6;
    top: 0;
    width: 100%;
    transition: all 0.25s ease-out;
    z-index: 100;
  }
  // .message-fixed:before{
  //   content: "";
  //   position: absolute;
  //   top: 0;
  //   left: 0;
  //   bottom: 0;
  //   right: 0;
  //   z-index: -1;
  //   mask-image: linear-gradient(to bottom, transparent, black);
  //   background: linear-gradient(90deg, #F6F6F6, #F6F6F6);	
  // }
  
  /* 图标容器1 start */
  .icon1 {
    &__item {
      width: 30%;
      background-color: #FFFFFF;
      border-radius: 10rpx;
      padding: 30rpx;
      margin: 20rpx 10rpx;
      transform: scale(1);
      transition: transform 0.3s linear;
      transform-origin: center center;
      
      &--icon {
        width: 100rpx;
        height: 100rpx;
        font-size: 60rpx;
        border-radius: 50%;
        margin-bottom: 18rpx;
        position: relative;
        z-index: 1;
        
        &::after {
          content: " ";
          position: absolute;
          z-index: -1;
          width: 100%;
          height: 100%;
          left: 0;
          bottom: 0;
          border-radius: inherit;
          opacity: 1;
          transform: scale(1, 1);
          background-size: 100% 100%;
          background-image: url(https://resource.tuniaokj.com/images/cool_bg_image/icon_bg5.png);
        }
      }
    }
  }
  
  /* 图标容器15 start */
  .icon15 {
    &__item {
      width: 30%;
      background-color: #FFFFFF;
      padding: 30rpx;
      margin: 20rpx 10rpx;
      transform: scale(1);
      transition: transform 0.3s linear;
      transform-origin: center center;
      
      &--icon {
        width: 90rpx;
        height: 90rpx;
        font-size: 60rpx;
        border-radius: 18rpx;
        position: relative;
        z-index: 1;
        
        &::after {
          content: " ";
          position: absolute;
          z-index: -1;
          width: 100%;
          height: 100%;
          left: 0;
          bottom: 0;
          border-radius: inherit;
          opacity: 1;
          transform: scale(1, 1);
          background-size: 100% 100%;
  
            
        }
      }
    }
  }
  
  /* 用户头像 start */
  .logo-image {
    width: 90rpx;
    height: 90rpx;
    position: relative;
  }
  
  .logo-pic {
    background-size: cover;
    background-repeat: no-repeat;
    // background-attachment:fixed;
    background-position: center;
    // border: 1rpx solid rgba(255,255,255,0.05);
    // box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
    border-radius: 18rpx;
    overflow: hidden;
    // background-color: #FFFFFF;
  }
  
  /* 底部安全边距 start*/
  .tn-tabbar-height {
  	min-height: 120rpx;
  	height: calc(140rpx + env(safe-area-inset-bottom));
  	height: calc(140rpx + constant(safe-area-inset-bottom));
  }
  
  
</style>
