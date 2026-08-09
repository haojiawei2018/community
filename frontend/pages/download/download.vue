<template>
  <view class="template-list">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#FFFFFF">
      <view slot="back" class='tn-custom-nav-bar__back'
        @click="goBack">
        <text class='icon tn-icon-left'></text>
      </view>
      <view class="tn-flex tn-flex-col-center tn-flex-row-center ">
        <text class="tn-text-bold tn-text-lg tn-color-black">资料图库</text>
      </view>
    </tn-nav-bar>
    
    <view class="tn-search-fixed">
      <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin" :style="{paddingTop: vuex_custom_bar_height + 'px'}">
        
        <view class="justify-content-item align-content-item" style="width: 100vw;">
          <view class="tn-flex tn-flex-col-center" style="border: 1rpx solid #D2D2D2;border-radius: 100rpx;padding: 10rpx 20rpx 10rpx 20rpx;width: 90%;">
            <text class="tn-icon-search justify-content-item tn-padding-right-xs tn-color-gray tn-text-lg"></text>
            <input class="justify-content-item" placeholder="请输入搜索关键词" name="input" placeholder-style="color:#AAAAAA" style="width: 90%;"></input>
          </view>
        </view>
        
        <view class="align-content-item">
          <view class="justify-content-item tn-text-center">
            <!-- 没输入前-->
            <tn-button backgroundColor="#3C97FF80" shape="round" padding="20rpx 20rpx" width="150rpx" @tap="">
              <text class="tn-color-white">搜 索</text>
            </tn-button>
            <!-- 输入后 -->
            <!-- <tn-button backgroundColor="#3C97FF" shape="round" padding="20rpx 20rpx" width="150rpx" @tap="">
              <text class="tn-color-white">搜 索</text>
            </tn-button> -->
          </view>
        </view>
      </view>
    </view>
    
    <view class="tn-safe-area-inset-bottom" :style="{paddingTop: vuex_custom_bar_height + 60 + 'px'}">
      
      <!-- 搜索为空 -->
      <!-- <view class="" style="padding: 5vh 20rpx;opacity: 0.8;">
        <view class="tn-text-center" style="font-size: 260rpx;padding-top: 30rpx;">
          <text class="tn-icon-stack tn-color-gray--disabled"></text>
        </view>
        <view class="tn-text-center" style="color: #979797;">搜索内容为空</view>
      </view> -->
      
      <!-- 当前页面内容 -->
      <view class="tn-bg-white tn-padding-bottom-xs">
        <view class="list-bg tn-flex tn-flex-row-left tn-flex-col-center" v-for="(item,index) in list" :key="index" @click="tn('/homePages/details')">
          <view class="">
            <view class="type-pic">
              <view class="type-image" :style="'background-image:url(' + item.mainImage + ');background-size: cover;'">
              </view>
            </view>
          </view>
          <view class="tn-padding-left clamp-text-2">
            <text class="">{{ item.desc }}</text>
          </view>
        </view>
      </view>
      
    </view>
  </view>
</template>

<script>
  import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
  export default {
    name: 'List',
    mixins: [template_page_mixin],
    data(){
      return {
        list: [
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828216161-assets/web-upload/d6ecc4c6-a675-4e30-9681-45b105578b55.jpeg',
          },
          {
            desc: '游戏攻略图游戏攻略图游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828359865-assets/web-upload/9e232257-0b68-4169-a997-e23195b2e0aa.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828485984-assets/web-upload/dd7b313d-e16c-45f3-ad5a-8bb27c2913b8.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828486008-assets/web-upload/bbea1e86-0db3-4f39-b240-abafa8769d8b.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828617900-assets/web-upload/8a6c98e4-ed47-4308-92bc-84b224d31e0c.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828745355-assets/web-upload/7de060fc-d1fc-4890-b6c0-6982fb19b972.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828818144-assets/web-upload/7a3f1f98-149a-46f2-84cf-31aaa5b84b2e.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724828933196-assets/web-upload/35cda0e5-c495-4ebe-9ed9-3bc1ec3d1645.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724829158713-assets/web-upload/1c3b07ff-d64c-4fe3-8626-275044886400.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724829158748-assets/web-upload/2d83bd1a-1451-44a1-8115-55cadd636b5e.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724829158754-assets/web-upload/9e74ec62-2ae8-46d2-9199-b8fccb62b8a0.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724829158764-assets/web-upload/069cccaa-2439-4cb2-8b96-e53e8b442145.jpeg',
          },
          {
            desc: '游戏攻略图',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1724830125897-assets/web-upload/e7e50a8b-202f-4cc5-9089-05d4b28dd2c8.jpeg',
          }
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
    }
  }
</script>

<style lang="scss" scoped>
  /* 胶囊*/
  .tn-custom-nav-bar__back {
    width: 50%;
    height: 100%;
    position: relative;
    display: flex;
    justify-content: space-evenly;
    color: #000000;
    font-size: 16px;
    
    .icon {
      display: block;
      flex: 1;
      margin: auto;
      padding-left: 10rpx;
      text-align: left;
    }
    
  }
  
  .tn-search-fixed{
    position: fixed;
    top: 0rpx;
    width: 100%;
    transition: all 0.25s ease-out;
    z-index: 1;
    background-color: #FFFFFF;
  }
  
  .list-bg{
    margin: 30rpx;
    padding-top: 30rpx;
  }
  
  .list-bg + .list-bg{
    border-top: 1rpx solid #F1F1F1;
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
  
  /* 文件头像 start */
  .type-image {
    width: 102rpx;
    height: 120rpx;
    position: relative;
  }
  
  .type-pic {
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
</style>
