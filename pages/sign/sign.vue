<template>
  <view class="template-spread">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#FFFFFF00">
      <view slot="back" class='tn-custom-nav-bar__back'
        @click="goBack">
        <text class='icon tn-icon-left-arrow'></text>
      </view>
      <!-- <view class="tn-flex tn-flex-col-center tn-flex-row-center ">
        <text class="tn-text-xl tn-color-black">签到</text>
      </view> -->
    </tn-nav-bar>
    
    <!-- 顶部渐变底色，需要的显示出来即可 -->
    <view class="top-bg">
      <view class="bg-pic" style="position: absolute;right: 40rpx;bottom: 120rpx;">
        <view class="bg-image" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/png/280373/1719968962717-assets/web-upload/b6758518-c11e-400f-9d59-3874e2a74a12.png');background-size: cover;">
        </view>
      </view>
    </view>
    
    <view class="" :style="{paddingTop: vuex_custom_bar_height + 10 + 'px'}">
      
      <view class="" style="position: relative;">
        
        <view class="bg-notice">
          <tn-notice-bar :list="list" mode="vertical" leftIconName="starry"></tn-notice-bar>
        </view>
        <view class="tn-margin tn-padding-top-sm" style="color: #001E50;font-size: 50rpx;">
          <text class="tn-text-bold">62,888</text>
          <text class="tn-padding-left-xs tn-text-xs tn-color-gray">积分</text>
        </view>
        <view class="tn-margin tn-text-sm" style="color: #001E50;">
          积分记录 <text class="tn-icon-right tn-padding-left-xs tn-color-gray"></text>
        </view>
        
      </view>
      
      
      
      <!-- 组件对应可选项容器 -->
      <view class="" style="position: relative;">
        
        <view class="tn-margin tn-padding-sm tn-bg-white" style="border-radius: 20rpx;">
          <view class="" style="border-bottom: 4rpx solid #ECF0F9;position: relative;top: 80rpx;width: 80%;margin: 0 auto;">
          </view>
          <!-- 方式12 start-->
          <view class="tn-flex tn-flex-wrap tn-flex-row-center tn-padding-top-lg tn-padding-bottom">
            <view v-for="(item, index) in 3" :key="index" style="width: 14%;">
              <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
                <!-- icon -->
                <view class="icon12__item--icon tn-flex tn-flex-row-center tn-flex-col-center" style="border: 1rpx solid #E5BC81;color: #E5BC81;background-color: #F7EFE6;">
                  <view class="tn-icon-success"></view>
                </view> 
                <view class="tn-text-center tn-padding-bottom-sm" style="color: #95A3A8;">
                  <text class="tn-text-ellipsis tn-text-sm">08/{{index + 13}}</text>
                </view>
              </view>
           </view>
           <view v-for="(item, index) in 3" :key="index" style="width: 14%;">
              <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
                <!-- 数字-->
                <view class="icon12__item--icon tn-flex tn-flex-row-center tn-flex-col-center">
                  <view class="">+30</view>
                </view> 
                <view class="tn-text-center tn-padding-bottom-sm" style="color: #95A3A8;">
                  <text class="tn-text-ellipsis tn-text-sm">08/{{index + 16}}</text>
                </view>
              </view>
           </view>
           <view v-for="(item, index) in 1" :key="index" style="width: 14%;">
              <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
                <!-- 图片形式 -->
                <view class="icon12__item--icon tn-flex tn-flex-row-center tn-flex-col-center" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/png/280373/1719937310080-assets/web-upload/f979073b-e335-4e18-8bf2-757756236a7d.png');background-size: cover;">
                </view>
                <view class="tn-text-center tn-padding-bottom-sm" style="color: #95A3A8;">
                  <text class="tn-text-ellipsis tn-text-sm">08/{{index + 19}}</text>
                </view>
              </view>
           </view>
          </view>
          <!-- 方式12 end-->
          
          <!-- 未签到-->
          <view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
            <tn-button shape="round" backgroundColor="#000000" fontColor="#FFFFFF" padding="40rpx 0" width="100%" :fontSize="30" @tap="showLandscape">
              <text class="">立即签到</text>
            </tn-button>
          </view>
          
          <!-- 已签到-->
          <!-- <view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
            <tn-button shape="round" backgroundColor="#DFE4E8" fontColor="#95A3A8" padding="40rpx 0" width="100%" :fontSize="30" @click="tn('')">
              <text class="">今日已签到</text>
            </tn-button>
          </view> -->
          
          <view class="tn-strip-bottom-min tn-padding" style="color: #95A3A8;">
            坚持每天连续签到可以获得多重奖励哦
          </view>
          
          <view class="tn-padding-left tn-pright tn-padding-top tn-padding-bottom tn-text-sm" style="color: #95A3A8;">
            温馨提示：<br>
            每日签到可获得10积分奖励，连续完成7天签到可能获得额外奖励，连续签到满7天后将会重置签到记录。
          </view>
          
        </view>
        
        
        <view class="tn-padding-top-sm">
          <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin" @click="tn('')">
            <view class="justify-content-item tn-text-bold tn-text-lg tn-padding-left-xs">
              做任务领金币
            </view>
            <view class="justify-content-item tn-text-df" style="color: #C1CACF;">
              <text class="tn-padding-xs">更多</text>
              <text class="tn-icon-right"></text>
            </view>
          </view>
          
          <view class="bg-shadow" style="background-color: #FFFFFF;border-radius: 24rpx;margin: 0 30rpx 30rpx 30rpx;" v-for="(item, index) in 6" :key="index">
            <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding" style="color: #333333;">
              <view class="" style="max-width: 50vw;">
                <view class="tn-text-bold">
                  任务名称
                </view>
                <view class="tn-color-gray tn-text-sm tn-padding-top-xs">
                  抓住那只猪吖
                </view>
              </view>
              <view class="justify-content-item">
                <tn-button shape="round" size="sm" backgroundColor="#000000" fontColor="#FFFFFF" padding="30rpx 0" width="160rpx">
                  <text class="tn-text-bold tn-text-sm">去完成</text>
                  <text class="tn-icon-right-fill tn-padding-left-xs tn-text-lg"></text>
                </tn-button>
              </view>
            </view>
            
          </view>
          
        </view>
        
        <view class="tn-tabbar-height"></view>
        
      </view> 
      
      <!-- 压屏窗-->
      <tn-landscape :show="show" @close="closeLandscape" closePosition="bottom">
        <view class="tn-text-center" style="padding-bottom: 10vh;">
          <view class="" style="z-index: 0;height: 420rpx;margin: 0 0 20rpx 0;">
            <image src="https://cdn.nlark.com/yuque/0/2024/png/280373/1719936825450-assets/web-upload/12a47786-b172-4b93-8e2a-eaec578e71de.png" mode="widthFix"></image>
          </view>

          <view class="" style="background-color: #FFFFFF;border-radius: 20rpx;width: 70vw;margin: 0 auto;margin-top: -220rpx;position: relative;z-index: 999;">
            <view class="" style="position: absolute;top: -160rpx;left: 120rpx;">
              <image src="https://cdn.nlark.com/yuque/0/2024/png/280373/1719936460577-assets/web-upload/697ed6f1-6126-4734-bd80-bed28296e16d.png" mode="aspectFit" style="width: 320rpx;height: 330rpx;"></image>
            </view>
            <view class="tn-text-xl tn-text-bold" style="padding-top: 120rpx;">
              签到成功
            </view>
            <view class="tn-padding tn-color-gray--dark">
              <text>已连续签到</text>
              <text class="tn-color-orangered" style="padding-left: 6rpx;padding-right: 6rpx">3</text>
              <text>天，获得</text>
              <text class="tn-color-orangered" style="padding-left: 6rpx;padding-right: 6rpx">20</text>
              <text>积分</text>
            </view>
            <view class=" tn-padding tn-color-gray">
              <text>连续签到</text>
              <text class="tn-color-orangered" style="padding-left: 6rpx;padding-right: 6rpx;">7</text>
              <text>天可获得额外奖励哦！</text>
            </view>
          </view>

        </view>
      </tn-landscape>

    </view>
    
    
  </view>
</template>

<script>
  import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
  export default {
    name: 'Sign',
    mixins: [template_page_mixin],
    data(){
      return {
        show: true,
        maskCloseable: true,
        
        list: [
          '恭喜 付*其 连续7天签到，获得 300积分 奖励',
          '恭喜 徐*圆 连续7天签到，获得 300积分 奖励',
          '恭喜 蔡*东 连续7天签到，获得 300积分 奖励',
        ],
        
      }
    },
    methods: {
      // 跳转
      tn(e) {
      	uni.navigateTo({
      		url: e,
      	});
      },
      
      // 弹出压屏窗
      showLandscape() {
        this.openLandscape()
      },
      // 打开压屏窗
      openLandscape() {
        this.show = true
      },
      // 关闭压屏窗
      closeLandscape() {
        this.show = false
      },
    }
  }
</script>

<style lang="scss" scoped>
  /* 胶囊*/
  .tn-custom-nav-bar__back {
    width: 38%;
    height: 100%;
    position: relative;
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    box-sizing: border-box;
    background-color: rgba(0, 0, 0, 0.15);
    border-radius: 1000rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.5);
    color: #FFFFFF;
    font-size: 18px;
    
    .icon {
      display: block;
      flex: 1;
      margin: auto;
      text-align: center;
    }
    
  }
  .template-spread{
    min-height: 100vh;
  }
  
  .icon12 {
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
        width: 70rpx;
        height: 70rpx;
        font-size: 30rpx;
        margin-bottom: 18rpx;
        position: relative;
        z-index: 1;
        color: #6A767D;
        border-radius: 100rpx;
        background-color: #ECF0F9;
        
        &::after {
          content: " ";
          position: absolute;
          z-index: -1;
          width: 100%;
          height: 100%;
          padding: 20rpx;
          left: 0;
          bottom: 0;
          opacity: 1;
          transform: scale(1, 1);
          background-size: 100% 100%;
        }
      }
    }
  }
  
  .top-bg{
    position: absolute;
    // background: linear-gradient(90deg, #d8feda, #F6F6F6);
    background: linear-gradient(120deg, #FEFDD3, #FAFCEF, #E5FDF5, #FEFBEA);
    top: 0;
    width: 100%;
    height: 580rpx;
    transition: all 0.25s ease-out;
    z-index: 0;
  }
  .top-bg:before{
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    z-index: 0;
    mask-image: linear-gradient(to bottom, transparent, black);
    background: linear-gradient(90deg, #F6F7F9, #F6F7F9);	
  }
  
  .bg-notice{
    color: #FFFFFF;
    background-color: #001E5020;
    padding: 4rpx;
    margin: 0 30rpx 30rpx 30rpx;
    border-radius: 16rpx;
  }
  
  /* 日历 */
  .bg-image {
    width: 240rpx;
    height: 192rpx;
    position: relative;
    overflow: hidden;
  }
  
  .bg-pic {
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;
    overflow: hidden;
  }
  
  /* 标签 */
  .tag-border-cyan {
    display: inline-block;
    padding: 8rpx 16rpx;
    margin: 0 15rpx 10rpx 0;
    border-radius: 10rpx;
    background-color: #2DE8BD08;
    color: #2DE8BD;
    border: 1rpx solid #2DE8BD;
    font-size: 16rpx;
  }
  .tag-border-yellow {
    display: inline-block;
    padding: 8rpx 16rpx;
    margin: 0 15rpx 10rpx 0;
    border-radius: 10rpx;
    background-color: #FFD10008;
    color: #FFD100;
    border: 1rpx solid #FFD100;
    font-size: 16rpx;
  }
  .tag-border-orange {
    display: inline-block;
    padding: 8rpx 16rpx;
    margin: 0 15rpx 10rpx 0;
    border-radius: 10rpx;
    background-color: #FEA50008;
    color: #FEA500;
    border: 1rpx solid #FEA500;
    font-size: 16rpx;
  }
  .tag-border-green {
    display: inline-block;
    padding: 8rpx 16rpx;
    margin: 0 15rpx 10rpx 0;
    border-radius: 10rpx;
    background-color: #1FC77308;
    color: #1FC773;
    border: 1rpx solid #1FC773;
    font-size: 16rpx;
  }
  .tag-bg {
    display: inline-block;
    padding: 8rpx 16rpx;
    margin: 0 15rpx 10rpx 0;
    border-radius: 10rpx;
    background-color: #E6E9EE;
    color: #001E50;
    border: 1rpx solid #001E5080;
    font-size: 16rpx;
  }
  
  /* 品牌 */
  .logo-image {
    width: 48rpx;
    height: 48rpx;
    position: relative;
    overflow: hidden;
    border-radius: 100rpx;
    border: 1rpx solid #DFE4E8;
  }
  
  .logo-image-yes {
    width: 48rpx;
    height: 48rpx;
    position: relative;
    overflow: hidden;
    border-radius: 100rpx;
    border: 1rpx solid #1FC773;
  }
  
  .logo-image-no {
    width: 48rpx;
    height: 48rpx;
    position: relative;
    overflow: hidden;
    border-radius: 100rpx;
    border: 1rpx solid #FF0033;
  }
  
  .logo-pic {
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center;
    // box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
    overflow: hidden;
    // background-color: #FFFFFF;
  }
  
  /* 商品 */
  .image-product {
    border-radius: 8rpx;
    width: 160rpx;
    height: 160rpx;
    position: relative;
    border: 1rpx solid #DFE4E8;
  }
  
  .image-pic{
    width: 160rpx;
    height: 160rpx;
    background-size: cover;
    background-repeat:no-repeat;
    background-position:center;
    border-radius: 10rpx;
  }
  
  /* 间隔线 start*/
  .tn-strip-bottom-min {
    width: 100%;
    border-bottom: 1rpx solid #F8F9FB;
  }
  
  .tn-strip-bottom {
   width: 100%;
   border-bottom: 20rpx solid rgba(241, 241, 241, 0.8);
  }
   /* 间隔线 end*/
   
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
  
  /* 底部安全边距 start*/
  .tn-tabbar-height {
  	min-height: 40rpx;
  	height: calc(60rpx + env(safe-area-inset-bottom) / 2);
    height: calc(60rpx + constant(safe-area-inset-bottom));
  }
  
</style>
