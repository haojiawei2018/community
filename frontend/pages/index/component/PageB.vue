<template>
  <view class="pagesB tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F6">
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left">
        <view class="custom-nav__back" @click="tn('/momentPages/institute')">
          <view class="tn-icon-add-circle"></view>
        </view>
        <view class="" style="width: 62vw;overflow: hidden;margin-top: 36rpx;">
          <tn-tabs :list="scrollList" :current="current" @change="tabChange" activeColor="#000" :bold="true" :fontSize="36"></tn-tabs>
        </view>
      </view>
    </tn-nav-bar>
    
    <view class="" :style="{paddingTop: vuex_custom_bar_height + 10 + 'px'}">
      
      <!-- <view class="tn-flex tn-flex-row-center">
        <view class="tn-flex tn-flex-col-center tn-bg-gray--light" style="border-radius: 100rpx;" @click="tn('/momentPages/message')">
          <view class="" style="padding: 10rpx;">
            <view class="message-pic">
              <view class="message-image" style="background-image:url('https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1671437658295-assets/web-upload/05620a1f-452e-4a14-9d30-f6c66ee4be1c.jpeg');width: 50rpx;height: 50rpx;background-size: cover;">
              </view>
            </view>
          </view>
          <view class="" style="padding: 10rpx 30rpx 10rpx 10rpx;">
            <view class="tn-flex tn-flex-row-between tn-flex-col-between">
              <view class="justify-content-item">
                <text class="tn-color-bdhook tn-text-df">3 条新消息</text>
              </view>
            </view>
          </view>
        </view>
      </view> -->
      
      <view class="tn-margin-top-sm" @click="tn('/circlePages/nav')">
        <view class="tn-flex tn-flex-row-between tn-round tn-padding-xs tn-margin" style="border: 1rpx solid #00000040;background: linear-gradient(-160deg, #000000, #D3F427, #000000, #D3F427);">
          <view class="justify-content-item tn-text-center tn-flex tn-flex-col-center" style="padding: 28rpx 30rpx 22rpx">
            <tn-avatar-group :lists="latestUserAvatar" size="sm"></tn-avatar-group> 
            <text class="tn-padding-left-xs">1.29W 人</text>
          </view>
          <view class="justify-content-item tn-text-right tn-padding-top-sm">
            <view class="tn-text-bold">
              香蕉猫 · 官方社区
            </view>
            <view class="tn-text-xs tn-color-gray--dark tn-padding-top-xs">
              美好社区，邀你共建
            </view>
          </view>
          <view class="justify-content-item tn-text-right tn-margin-right tn-padding-top-lg">
            <text class="tn-icon-right tn-color-gray--dark"></text>
          </view>
        </view>
      </view>
      
      <view class="tn-flex tn-flex-row-between tn-margin-top-lg tn-margin-left tn-margin-right">
        <view class="justify-content-item tn-text-bold tn-text-lg">
        精选应用
        </view>
        <view class="justify-content-item" @click="tn('../filter/filter')">
          <text class="tn-padding-xs">全部</text>
          <text class="tn-icon-right"></text>
        </view>
      </view>
      
      <view class="">
        <!-- 方式16 start-->
        <view class="tn-flex tn-flex-wrap tn-margin-bottom">
          <block v-for="(item, index) in bloggerList" :key="index">
            <view class="" style="width: 25%;" @click="tn('/circlePages/group')">
              <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center ">
                <view class="tn-radius tn-padding-sm">
                  <view class="image-pic" :style="'background-image:url('+ item.url +')'">
                    <view class="image-circle">
                      <view :class="['tn-icon-' + item.icon]" style="position: absolute;margin: 100rpx 0 0 41rpx;border-radius: 100rpx;background-color: #F6F6F6;color: #000000;"></view>
                    </view>
                  </view>
                  <view class="tn-text-center tn-text-bold tn-padding-top-sm tn-text-sm">{{item.name}}</view>
                  <view class="tn-text-center tn-text-xs tn-color-gray--dark tn-padding-top-xs">{{item.text}}</view>
                </view>
              </view>
            </view>
          </block>
        </view>
        <!-- 方式16 end-->
      </view>
      
      <view class="tn-flex tn-flex-direction-column tn-margin-top-sm tn-margin-bottom" v-if="current==0">
           
        <!-- 图文信息 -->
        <block v-for="(item,index) in content">
          <view class="blogger__item tn-margin-bottom-sm tn-margin-top-sm" :key="index">
            <view class="blogger__author tn-flex tn-flex-row-between tn-flex-col-center">
              <view class="justify__author__info" @click="tn('/momentPages/blogger_other')">
                <view class="tn-flex tn-flex-row-center">
                  <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                    <view class="logo-pic">
                      <view class="logo-image" :style="'background-image:url(' + item.userAvatar + ');width: 70rpx;height: 70rpx;background-size: cover;'">
                      </view>
                    </view>
                    
                    <view class="tn-padding-right tn-text-ellipsis">
                      <view class="tn-padding-right tn-padding-left-sm tn-text-bold">{{ item.userName }}</view>
                      <view class="tn-padding-right tn-padding-left-sm tn-padding-top-xs tn-color-gray tn-text-xs">{{ item.date }}</view>
                    </view>
                  </view>
                </view>
              </view>
              <view class="blogger__author__btn justify-content-item tn-flex-col-center tn-flex-row-center">
                <!-- 为什么不放关注按钮，因为快餐文化的世界，关注按钮放在外面没必要 -->
                <picker @change="bindPickerChange" :value="numindex" :range="array">
                  <text class="tn-icon-group-square tn-color-gray tn-text-bold tn-text-xxl"></text>
                </picker>
              </view>
            </view>
            
            <view class="blogger__desc tn-margin-top-sm tn-margin-bottom-sm tn-text-justify tn-flex-col-center tn-flex-row-left" @click="tn('/momentPages/details')">
              <view v-for="(label_item,label_index) in item.label" :key="label_index" class="blogger__desc__label tn-float-left tn-margin-right">
                <text class="blogger__desc__label--prefix tn-icon-topics-fill"></text> 
                <text class="tn-text-df">{{ label_item }}</text>
              </view>
              <!-- 不用限制长度了，因为发布的时候限制长度了-->
              <text v-if="!item.label || item.label.length < 4" class="blogger__desc__content tn-flex-1 tn-text-justify tn-text-df">{{ item.desc }}</text>  
            </view>
            
            <!-- 内容太多疲劳了-->
            <!-- <view
              v-if="item.content"
              class="blogger__content"
              :id="`blogger__content--${index}`"
            >
              <view
                class="blogger__content__data clamp-text-2">
                {{ item.content }}
              </view>
            </view> -->
            
            <block v-if="item.mainImage">
              <view v-if="[1,2,4].indexOf(item.mainImage.length) != -1" class="tn-padding-top-xs" @click="tn('/momentPages/details')">
                <image v-for="(image_item,image_index) in item.mainImage" :key="image_index" 
                  class="blogger__main-image"
                  :class="{
                    'blogger__main-image--1 tn-margin-bottom-sm': item.mainImage.length === 1,
                    'blogger__main-image--2 tn-margin-right-sm tn-margin-bottom-sm': item.mainImage.length === 2 || item.mainImage.length === 4
                  }"
                  :src="image_item"
                  mode="aspectFill"
                ></image>
              </view>
              <view v-else class="tn-padding-top-xs" style="" @click="tn('/momentPages/details')">
                <tn-grid  hoverClass="none" :col="3">
                  <block v-for="(image_item,image_index) in item.mainImage" :key="image_index">
                    <!-- #ifndef MP-WEIXIN -->
                    <tn-grid-item style="width: 29%;margin: 0 20rpx 20rpx 0;">
                      <image
                        class="blogger__main-image blogger__main-image--3"
                        :src="image_item"
                        mode="aspectFill"
                      ></image>
                    </tn-grid-item>
                    <!-- #endif-->
                    <!-- #ifdef MP-WEIXIN -->
                    <tn-grid-item style="width: 29%;margin: 0 20rpx 20rpx 0;">
                      <image
                        class="blogger__main-image blogger__main-image--3"
                        :src="image_item"
                        mode="aspectFill"
                      ></image>
                    </tn-grid-item>
                    <!-- #endif-->
                  </block>
                </tn-grid>
              </view>
            </block>
                         
            <view class="tn-flex tn-flex-row-between tn-flex-col-center" style="margin: 0 0 18rpx 0;">
              <!-- <view class="justify-content-item tn-flex tn-flex-col-center">
                <text class="tn-color-gray">{{ item.post }}</text>
                <view style="margin-right: 10rpx;margin-left: 20rpx;">
                  <tn-avatar-group :lists="item.viewUser.latestUserAvatar" size="sm"></tn-avatar-group>
                </view>
                <text class="tn-color-gray">{{ item.viewUser.viewUserCount }}人</text>
              </view> -->
              
              <view class="justify-content-item">
                <view class="tag-game" style="font-size: 18rpx;">
                  <view class="tn-flex tn-flex-col-center">
                    <view class="justify-content-item circle-pic">
                      <view class="circle-image" :style="'background-image:url(' + item.circleAvatar + ');'">
                      </view>
                    </view>
                    <view class="justify-content-item tn-color-black" style="padding: 0 18rpx 0 12rpx;line-height: 36rpx;">
                      <!-- <text class="tn-icon-reload-planet-fill"></text> -->
                      {{ item.post }}
                    </view>
                  </view>
                </view>
              </view>
              
              <view class="justify-content-item tn-color-gray tn-text-center">
                <!-- <text class="tn-icon-fire tn-text-lg" style="padding-right: 5rpx;"></text>
                <text class="tn-padding-right tn-text-df">{{ item.collectionCount }}</text> -->
                <text class="tn-icon-meteor tn-text-lg" style="padding-right: 5rpx;"></text>
                <text class="tn-padding-right tn-text-df">{{ item.commentCount }}</text>
                <text class="tn-icon-rocket tn-text-lg" style="padding-right: 5rpx;"></text>
                <text class="tn-text-df">{{ item.likeCount }}</text>
              </view>
            
            </view>
            
          </view>
          
          <!-- 边距间隔 -->
          <view class="tn-strip-bottom-min" v-if="index != content.length - 1"></view>
        </block>
        
        <!-- 边距间隔 -->
        <view class="tn-strip-bottom-min"></view>
        
        <!-- 图鸟广告 -->
        <view class="blogger__item tn-margin-top-sm tn-margin-bottom-sm" @click="tn('/momentPages/advertise')">
          <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin-bottom">
            <view class="justify-content-item">
              <view class="tn-flex tn-flex-col-center tn-flex-row-left">
                <!-- 图标logo -->
                <view class="logo-pic">
                  <view class="logo-image" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399286-assets/web-upload/7a715327-f597-4a8e-ac3c-2ba7ab45b6b9.jpeg');width: 70rpx;height: 70rpx;background-size: cover;">
                  </view>
                </view>
                <view class="tn-padding-right" style="width: 65vw;">
                  <view class="tn-padding-right tn-padding-left-sm tn-text-bold">崩坏 · 星穹铁道</view>
                  <view class="tn-padding-right tn-padding-left-sm tn-padding-top-xs tn-color-gray tn-text-xs">创意好玩，由你定义</view>
                </view>
              </view>
            </view>
            <view class="tn-color-gray">广告</view>
          </view>
          <view class="tn-padding-bottom-sm">
            <tn-stack-swiper ref="stackSwiper" :list="adList" :switchRate="30" :height="300" width="96%" :autoplay="adAutoplay"></tn-stack-swiper>
          </view>
        </view>
        
        <!-- 边距间隔 -->
        <view class="tn-strip-bottom-min"></view>
        
        <!-- 图文信息 -->
        <block v-for="(item,index) in content">
          <view class="blogger__item tn-margin-bottom-sm tn-margin-top-sm" :key="index">
            <view class="blogger__author tn-flex tn-flex-row-between tn-flex-col-center">
              <view class="justify__author__info" @click="tn('/momentPages/blogger_other')">
                <view class="tn-flex tn-flex-row-center">
                  <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                    <view class="logo-pic">
                      <view class="logo-image" :style="'background-image:url(' + item.userAvatar + ');width: 70rpx;height: 70rpx;background-size: cover;'">
                      </view>
                    </view>
                    
                    <view class="tn-padding-right tn-text-ellipsis">
                      <view class="tn-padding-right tn-padding-left-sm tn-text-bold">{{ item.userName }}</view>
                      <view class="tn-padding-right tn-padding-left-sm tn-padding-top-xs tn-color-gray tn-text-xs">{{ item.date }}</view>
                    </view>
                  </view>
                </view>
              </view>
              <view class="blogger__author__btn justify-content-item tn-flex-col-center tn-flex-row-center">
                <!-- 为什么不放关注按钮，因为快餐文化的世界，关注按钮放在外面没必要 -->
                <picker @change="bindPickerChange" :value="numindex" :range="array">
                  <text class="tn-icon-group-square tn-color-gray tn-text-bold tn-text-xxl"></text>
                </picker>
              </view>
            </view>
            
            <view class="blogger__desc tn-margin-top-sm tn-margin-bottom-sm tn-text-justify tn-flex-col-center tn-flex-row-left" @click="tn('/momentPages/details')">
              <view v-for="(label_item,label_index) in item.label" :key="label_index" class="blogger__desc__label tn-float-left tn-margin-right">
                <text class="blogger__desc__label--prefix tn-icon-topics-fill"></text> 
                <text class="tn-text-df">{{ label_item }}</text>
              </view>
              <!-- 不用限制长度了，因为发布的时候限制长度了-->
              <text v-if="!item.label || item.label.length < 4" class="blogger__desc__content tn-flex-1 tn-text-justify tn-text-df">{{ item.desc }}</text>  
            </view>
            
            <!-- 内容太多疲劳了-->
            <!-- <view
              v-if="item.content"
              class="blogger__content"
              :id="`blogger__content--${index}`"
            >
              <view
                class="blogger__content__data clamp-text-2">
                {{ item.content }}
              </view>
            </view> -->
            
            <block v-if="item.mainImage">
              <view v-if="[1,2,4].indexOf(item.mainImage.length) != -1" class="tn-padding-top-xs" @click="tn('/momentPages/details')">
                <image v-for="(image_item,image_index) in item.mainImage" :key="image_index" 
                  class="blogger__main-image"
                  :class="{
                    'blogger__main-image--1 tn-margin-bottom-sm': item.mainImage.length === 1,
                    'blogger__main-image--2 tn-margin-right-sm tn-margin-bottom-sm': item.mainImage.length === 2 || item.mainImage.length === 4
                  }"
                  :src="image_item"
                  mode="aspectFill"
                ></image>
              </view>
              <view v-else class="tn-padding-top-xs" style="" @click="tn('/momentPages/details')">
                <tn-grid  hoverClass="none" :col="3">
                  <block v-for="(image_item,image_index) in item.mainImage" :key="image_index">
                    <!-- #ifndef MP-WEIXIN -->
                    <tn-grid-item style="width: 29%;margin: 0 20rpx 20rpx 0;">
                      <image
                        class="blogger__main-image blogger__main-image--3"
                        :src="image_item"
                        mode="aspectFill"
                      ></image>
                    </tn-grid-item>
                    <!-- #endif-->
                    <!-- #ifdef MP-WEIXIN -->
                    <tn-grid-item style="width: 29%;margin: 0 20rpx 20rpx 0;">
                      <image
                        class="blogger__main-image blogger__main-image--3"
                        :src="image_item"
                        mode="aspectFill"
                      ></image>
                    </tn-grid-item>
                    <!-- #endif-->
                  </block>
                </tn-grid>
              </view>
            </block>
                         
            <view class="tn-flex tn-flex-row-between tn-flex-col-center" style="margin: 0 0 18rpx 0;">
              <!-- <view class="justify-content-item tn-flex tn-flex-col-center">
                <text class="tn-color-gray">{{ item.post }}</text>
                <view style="margin-right: 10rpx;margin-left: 20rpx;">
                  <tn-avatar-group :lists="item.viewUser.latestUserAvatar" size="sm"></tn-avatar-group>
                </view>
                <text class="tn-color-gray">{{ item.viewUser.viewUserCount }}人</text>
              </view> -->
              
              <view class="justify-content-item">
                <view class="tag-game" style="font-size: 18rpx;">
                  <view class="tn-flex tn-flex-col-center">
                    <view class="justify-content-item circle-pic">
                      <view class="circle-image" :style="'background-image:url(' + item.circleAvatar + ');'">
                      </view>
                    </view>
                    <view class="justify-content-item tn-color-black" style="padding: 0 18rpx 0 12rpx;line-height: 36rpx;">
                      <!-- <text class="tn-icon-reload-planet-fill"></text> -->
                      {{ item.post }}
                    </view>
                  </view>
                </view>
              </view>
              
              <view class="justify-content-item tn-color-gray tn-text-center">
                <!-- <text class="tn-icon-fire tn-text-lg" style="padding-right: 5rpx;"></text>
                <text class="tn-padding-right tn-text-df">{{ item.collectionCount }}</text> -->
                <text class="tn-icon-meteor tn-text-lg" style="padding-right: 5rpx;"></text>
                <text class="tn-padding-right tn-text-df">{{ item.commentCount }}</text>
                <text class="tn-icon-rocket tn-text-lg" style="padding-right: 5rpx;"></text>
                <text class="tn-text-df">{{ item.likeCount }}</text>
              </view>
            
            </view>
            
          </view>
          
          <!-- 边距间隔 -->
          <view class="tn-strip-bottom-min" v-if="index != content.length - 1"></view>
        </block>
         
            
      </view>
      
      <view class="" v-if="current==1">
        <view class="" style="padding: 6vh 20rpx;opacity: 0.6;">
          <view class="tn-text-center" style="font-size: 260rpx;padding-top: 30rpx;">
            <text class="tn-icon-game tn-color-gray--disabled"></text>
          </view>
          <view class="tn-color-gray tn-text-center tn-text-lg">暂无内容，嘤嘤嘤嘤</view>
        </view>
      </view>
      
      <view class="" v-if="current==2">
        <view class="" style="padding: 6vh 20rpx;opacity: 0.6;">
          <view class="tn-text-center" style="font-size: 260rpx;padding-top: 30rpx;">
            <text class="tn-icon-game tn-color-gray--disabled"></text>
          </view>
          <view class="tn-color-gray tn-text-center tn-text-lg">暂无内容，嘤嘤嘤嘤嘤嘤</view>
        </view>
      </view>
    
    </view>
    

    
    
    <!-- <view class="">
      <view class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur button-1" @click="tn('/momentPages/edit')">
        <view class="tn-icon-camera-fill tn-color-white"></view>
      </view>
      <view class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur button-2"  @click="tn('/momentPages/blogger')">
        <view class="tn-icon-ghost-fill tn-color-white">
          <tn-badge backgroundColor="#E72F8C" :dot="true" :radius="16" :absolute="true" :translateCenter="false" top="4rpx" right="4rpx"></tn-badge>
        </view>
      </view>
    </view>  -->
    

    <view class="tn-tabbar-height"></view>
    
  </view>
</template>

<script>
  export default {
    name: 'PageB',
    data(){
      return {
        current: 0,
        scrollList: [
          {name: '最新'},
          {name: '推荐'},
          {name: '关注'}
        ],
        
        latestUserAvatar: [
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
        ],
        bloggerList: [
        {
          id: 0,
          type: 'image',
          name: '保卫萝卜',
          text: '552人关注',
          icon: 'more-circle-fill',
          url: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
        }, {
          id: 1,
          type: 'image',
          name: '蛋仔派对',
          text: '688人关注',
          icon: 'more-circle-fill',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716013944651-assets/web-upload/32eafb0d-1e25-452b-9ad9-d14c18ec90f6.jpeg',
        }, {
          id: 2,
          type: 'image',
          name: '旅行青蛙',
          text: '105人关注',
          icon: 'add-fill',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014232876-assets/web-upload/5e1d7c68-048e-4b54-afda-68164a5bebd0.jpeg',
        },{
          id: 3,
          type: 'image',
          name: '纪念碑谷',
          text: '629人关注',
          icon: 'add-fill',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716024900620-assets/web-upload/83c5e7af-e288-4e33-b49c-38db5578edd1.jpeg',
        }],
        
        array: ['投诉举报', '屏蔽此人'],
      
        // 内容默认隐藏显示的高度
        contentHideShowHeight: 0,
        content: [
          {
            userAvatar: 'https://resource.tuniaokj.com/images/skit/video-4.jpg',
            userName: '那只猪',
            post: '行走大师',
            date: '12分钟前',
            label: [],
            circleAvatar: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716036487332-assets/web-upload/578ada4b-ace7-449d-ab65-a5c7359ba44d.jpeg',
            desc: '挺有意思的一款休闲小游戏，乐趣在于解锁关卡和获取角色，当然，也少不了一些操作技巧',
            content: '基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了',
            viewUser: {
              latestUserAvatar: [
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
              ],
              viewUserCount: 62
            },
            collectionCount: 439,
            commentCount: 46,
            likeCount: 83
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg',
            userName: '你的名字',
            post: '保卫萝卜',
            date: '2小时前',
            label: [],
            circleAvatar: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
            desc: '贺强至今仍未归，钓鱼大奖花落谁',
            content: '基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了',
            mainImage:[
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1715959853964-assets/web-upload/2c4f9b49-0014-4714-a9c6-60b2e5c06d94.jpeg'
            ],
            viewUser: {
              latestUserAvatar: [
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
              ],
              viewUserCount: 12
            },
            collectionCount: 902,
            commentCount: 64,
            likeCount: 83
          },
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2021/jpeg/280373/1632120391491-assets/web-upload/6dd513cc-d303-4e51-8305-ca51a8c6d17e.jpeg',
            userName: '图鸟北北',
            post: '辐射避难所',
            date: '1天前',
            label: ['沙盒','生存经营'],
            circleAvatar: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg',
            desc: '一个经典的沙盒模拟经营游戏，前期多修电厂，多探险，靠着耐力E打天下',
            content: '基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了 基础常用的布局元素，酷炫完善的配色体系，统一可增的图标 icon ，简便调用的功能组件，酷炫的前端页面，吖，编不下去了',
            mainImage:[
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025377399-assets/web-upload/5aa2565b-5c7e-4053-81a8-05d67a1ad90f.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025467263-assets/web-upload/024e9c25-72fa-437e-a741-c8c20945ad2b.jpeg',
            ],
            viewUser: {
              latestUserAvatar: [
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
              ],
              viewUserCount: 231
            },
            collectionCount: 780,
            commentCount: 89,
            likeCount: 82
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            userName: '图鸟南南',
            post: '纪念碑谷',
            date: '3天前',
            label: ['解谜','益智','唯美'],
            circleAvatar: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716024900620-assets/web-upload/83c5e7af-e288-4e33-b49c-38db5578edd1.jpeg',
            desc: '创意满满，清新纯粹的音乐，简单上手的游戏玩法！',
            content: '',
            mainImage:[
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716035397424-assets/web-upload/02b6486a-c2af-4999-ac53-4019c1cafebd.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716035532404-assets/web-upload/2a870ac3-53c5-4aa7-85a5-33a5cac1e29f.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716035532423-assets/web-upload/4bf63040-b17d-4fad-a8a2-6151c59e8ffd.jpeg',
            ],
            viewUser: {
              latestUserAvatar: [
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
              ],
              viewUserCount: 56
            },
            collectionCount: 431,
            commentCount: 26,
            likeCount: 84
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            userName: '图鸟西西',
            post: '王国保卫战',
            date: '6天前',
            label: ['塔防','策略'],
            circleAvatar: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025891640-assets/web-upload/8b4440ce-710b-4d3c-afff-5d49e6c7a16a.jpeg',
            desc: '每一代的故事线都很丰富，可玩性很高，塔防之光好吧',
            mainImage:[
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039552315-assets/web-upload/3357bbb2-f918-4303-86f3-cabbb6cb0fdd.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039552249-assets/web-upload/2967ac34-e1ce-4f70-87de-56452a96d18d.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039552249-assets/web-upload/04d7c4b2-6b9c-48d6-b011-13d577889124.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039552306-assets/web-upload/2bc9dc33-171a-40df-9382-cb8258c31403.jpeg',
            ],
            viewUser: {
              latestUserAvatar: [
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
              ],
              viewUserCount: 28
            },
            collectionCount: 432,
            commentCount: 33,
            likeCount: 12
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/skit/video-1.jpg',
            userName: '图鸟猪猪',
            post: '蛋仔派对',
            date: '1个月前',
            label: ['竞技','休闲','派对'],
            circleAvatar: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716013944651-assets/web-upload/32eafb0d-1e25-452b-9ad9-d14c18ec90f6.jpeg',
            desc: '游戏的模式很丰富，比较喜欢简单可爱的风格',
            mainImage:[
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039723287-assets/web-upload/4c6fae5e-f2f1-4e2b-b1e3-d1ba007ad09b.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039723098-assets/web-upload/3270b7f2-91e3-4aa3-ba6e-cfe3ad3e34f3.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039723074-assets/web-upload/3421b268-a0b2-47aa-a5e1-503cac9c4888.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039723058-assets/web-upload/28e928b8-4811-4331-8030-a6086efe4e4b.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039722992-assets/web-upload/ae74a7d3-dc20-4cb5-9178-c77f9aa10462.jpeg',
            ],
            viewUser: {
              latestUserAvatar: [
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
              ],
              viewUserCount: 65
            },
            collectionCount: 265,
            commentCount: 22,
            likeCount: 62
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            userName: '图鸟猪猪',
            post: '旅行青蛙',
            date: '1年前',
            label: ['放置','佛系','简约'],
            circleAvatar: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014232876-assets/web-upload/5e1d7c68-048e-4b54-afda-68164a5bebd0.jpeg',
            desc: '说走就走的旅行，人生在于简单快乐',
            mainImage:[
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015234-assets/web-upload/f8291448-ff72-4b6e-95d6-769dbdda0f8f.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015216-assets/web-upload/ff424f75-5d37-4b80-8242-bb21e415875e.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015526-assets/web-upload/302bf580-7493-425e-83f3-f4ecc6a929f4.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015299-assets/web-upload/5b0720dd-b913-42cd-bc8f-4c77ca55b7d2.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015239-assets/web-upload/3f22e659-42d3-4106-be67-46521c22eb99.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015217-assets/web-upload/4f8f4e19-9c23-4e37-acbe-72fcd51b0a97.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015604-assets/web-upload/85d809de-b5bf-4407-b8b8-940fc0ea1dfe.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015477-assets/web-upload/fec63309-26a6-4b84-b403-10034ce92704.jpeg',
              'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015222-assets/web-upload/e8c6e273-5c1a-4977-b447-54ceba4105c5.jpeg',
            ],
            viewUser: {
              latestUserAvatar: [
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
                {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
              ],
              viewUserCount: 65
            },
            collectionCount: 265,
            commentCount: 22,
            likeCount: 62
          }
        ],
        
        adList: [
          {image: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399137-assets/web-upload/51b8b734-e1db-4744-82f5-56186cb36f6d.jpeg'},
          {image: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399113-assets/web-upload/6ebf5f25-4c3a-488a-b221-0497f2945549.jpeg'},
          {image: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399125-assets/web-upload/f75e889a-57a6-418a-9315-2814e3441ec1.jpeg'},
          {image: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399000-assets/web-upload/8df56aaf-4974-40e4-a249-22deb21a74dd.jpeg'},
        ],
        adAutoplay: false,
        

      }
    },
    methods: {
      // tab选项卡切换
      tabChange(index) {
        this.current = index
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
  .pagesB{
    max-height: 100vh;
  }
  
/* 自定义导航栏内容 start */
  .custom-nav {
    height: 100%;
    
    &__back {
      margin: auto 5rpx;
      font-size: 50rpx;
      margin-right: 10rpx;
      margin-left: 30rpx;
      flex-basis: 5%;
    }
  }
  
  /* 按钮 */
  .button-1 {
    background-color: rgba(0, 0, 0, 0.15);
    position: fixed;
    /* bottom:200rpx;
      right: 20rpx; */
    bottom: 24%;
    right: 30rpx;
    z-index: 1001;
    border-radius: 100px;
  }
  .button-2 {
    background-color: rgba(0, 0, 0, 0.15);
    position: fixed;
    /* bottom:200rpx;
      right: 20rpx; */
    bottom: 15%;
    right: 30rpx;
    z-index: 1001;
    border-radius: 100px;
  }
  
  /* 图标容器15 start */
  .icon15 {
    &__item {
      width: 30%;
      
      border-radius: 10rpx;
      padding: 30rpx;
      margin: 20rpx 10rpx;
      transform: scale(1);
      transition: transform 0.3s linear;
      transform-origin: center center;
      
      &--icon {
        width: 90rpx;
        height: 90rpx;
        font-size: 50rpx;
        border-radius: 50%;
        margin-bottom: 18rpx;
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
  
  /* 悬浮 */
  .tnxuanfu{
    animation: suspension 3s ease-in-out infinite;
  }
  
  @keyframes suspension {
    0%, 100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-0.8rem);
    }
  }
  /* 悬浮按钮 */
  .button-shop {
    width: 90rpx;
    height: 90rpx;
    display: flex;
    flex-direction: row;
    position: fixed;
    /* bottom:200rpx;
      right: 20rpx; */
    left: 5rpx;
    top: 5rpx;
    z-index: 1001;
    border-radius: 100px;
    opacity: 0.9;
  }
  
  
  /* 文章内容 start*/
    .blogger {
      &__item {
        background-color: #FFFFFF;
        border-radius: 18rpx;
        margin: 0 30rpx 20rpx 30rpx;
        padding: 30rpx 30rpx 10rpx 30rpx;
      }
      
      &__author {
        &__btn {
          margin-right: -12rpx;
          opacity: 0.5;
        }
      }
      
      &__desc {
        line-height: 30rpx;
        
        &__label {
          
          color: #1D2541;
          background-color: #F3F2F7;
          border-radius: 10rpx;
          font-size: 22rpx;
          
          padding: 5rpx 15rpx;
          margin: 5rpx 18rpx 0 0;
          
          &--prefix {
            font-size: 24rpx;
            color: #1D2541;
            padding-right: 10rpx;
          }
        }
        &__content {
          line-height: 50rpx;
        }
      }
      
      &__content {
        margin-top: 18rpx;
        padding-right: 18rpx;
        
        &__data {
          line-height: 46rpx;
          text-align: justify;
          overflow: hidden;
          transition: all 0.25s ease-in-out;
  
        }
        
        &__status {
          margin-top: 10rpx;
          font-size: 26rpx;
          color: #82B2FF;
        }
      }
      
      &__main-image {
        border-radius: 16rpx;
        
        &--1 {
          max-width: 80%;
          max-height: 300rpx;
        }
        
        &--2 {
          max-width: 260rpx;
          max-height: 260rpx;
        }
        
        &--3 {
          height: 174rpx;
          width: 100%;
        }
      }
      
      &__count-icon {
        font-size: 40rpx;
        padding-right: 5rpx;
      }
      
      &__ad {
        width: 100%;
        height: 500rpx;
        transform: translate3d(0px, 0px, 0px) !important;
        
        ::v-deep .uni-swiper-slide-frame {
          transform: translate3d(0px, 0px, 0px) !important;
        }
        .uni-swiper-slide-frame {
          transform: translate3d(0px, 0px, 0px) !important;
        }
        
        &__item {
          position: absolute;
          width: 100%;
          height: 100%;
          transform-origin: left center;
          transform: translate3d(100%, 0px, 0px) scale(1) !important;
          transition: transform 0.25s ease-in-out;
          z-index: 1;
          
          &--0 {
            transform: translate3d(0%, 0px, 0px) scale(1) !important;
            z-index: 4;
          }
          &--1 {
            transform: translate3d(13%, 0px, 0px) scale(0.9) !important;
            z-index: 3;
          }
          &--2 {
            transform: translate3d(26%, 0px, 0px) scale(0.8) !important;
            z-index: 2;
          }
        }
        
        &__content {
          border-radius: 40rpx;
          width: 640rpx;
          height: 500rpx;
          overflow: hidden;
        }
        
        &__image {
          width: 100%;
          height: 100%;
        }
      }
    }
    /* 文章内容 end*/
    
    /* 用户头像 start */
    .logo-image {
      width: 70rpx;
      height: 70rpx;
      position: relative;
      background-position: center;
    }
    
    .logo-pic {
      background-size: cover;
      background-repeat: no-repeat;
      // background-attachment:fixed;
      background-position: center;
      // box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
      border-radius: 100rpx;
      border: 1rpx solid #F8F7F8;
      overflow: hidden;
      // background-color: #FFFFFF;
    }
    
    /* 圈子头像 start */
    .circle-image {
      width: 36rpx;
      height: 36rpx;
      position: relative;
      background-size: cover;
      background-position: center;
    }
    
    .circle-pic {
      background-size: cover;
      background-repeat: no-repeat;
      // background-attachment:fixed;
      background-position: center;
      // box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
      border-radius: 6rpx;
      // border: 1rpx solid #F8F7F8;
      overflow: hidden;
      // background-color: #FFFFFF;
    }
    
    /* 标签 start*/
    .tag-game {
      display: inline-block;
      // padding: 8rpx 14rpx 8rpx;
      border-radius: 10rpx;
      background-color: #000000;
    }
    /* 标签 end*/
     
    /* 间隔线 start*/
    .tn-strip-bottom-min {
      width: 100%;
      border-bottom: 1rpx solid #F8F7F8;
    } 
     
    .tn-strip-bottom {
     width: 100%;
     border-bottom: 20rpx solid #F8F7F8;
    }
     /* 间隔线 end*/
     
    
  /* 大圈子头像 start*/
  .image-circle{
    // padding: 95rpx;
    width: 120rpx;
    height: 120rpx;
    font-size: 40rpx;
    font-weight: 300;
    position: relative;
    background-position: center;
  }
  .image-pic{
    border: 1rpx solid #F8F7F8;
    background-size: cover;
    background-repeat:no-repeat;
    // background-attachment:fixed;
    background-position:center;
    border-radius: 18rpx;
  }
  
  /* 最新消息-头像 start */
  .message-image {
    width: 50rpx;
    height: 50rpx;
    position: relative;
  }
  
  .message-pic {
    background-size: cover;
    background-repeat: no-repeat;
    // background-attachment:fixed;
    background-position: center;
    // box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
    border-radius: 100rpx;
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
  
  
  /* 底部安全边距 start*/
  .tn-tabbar-height {
    min-height: 100rpx;
    height: calc(120rpx + env(safe-area-inset-bottom) / 2);
    height: calc(120rpx + constant(safe-area-inset-bottom));
  }
  
</style>