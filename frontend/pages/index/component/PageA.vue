<template>
  <view class="pagesA tn-safe-area-inset-bottom">

    <!-- 旧版头部已迁移到 index.vue，空容器不再参与布局 -->
    <view v-if="false" class="dd-transform">
      <!-- 动画 -->
      <view class="time" :class="showUserModal==true?'tn-height-cur':'tn-height'">

        <view class="" :style="{paddingTop: vuex_custom_bar_height + 50 + 'px'}">

          <!-- 搜索栏
          <view class="tn-search-fixed">
            <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin-left tn-margin-right">
              <view class="justify-content-item align-content-item tn-bg-white" style="color: #838383;border-radius: 20rpx;padding: 8rpx;width: 83%;" @click="tn('../search/search')">
                <tn-notice-bar :list="searlist" mode="vertical" leftIconName="search" :duration="6000"></tn-notice-bar>
              </view>
              <view class="align-content-item">
                <view class="justify-content-item tn-text-center" style="border-radius: 20rpx;overflow: hidden;">
                  <tn-button backgroundColor="#000000" padding="42rpx 20rpx" width="90rpx" @tap.stop="openUserModal">
                    <text class="tn-color-white tn-icon-menu-set-fill tn-text-xl"></text>
                  </tn-button>
                </view>
              </view>
            </view>
          </view>
          搜索栏结束 -->
          
          <!-- 轮播图、功能入口、热门推荐、推荐广告
          <swiper class="card-swiper tn-margin-top-lg" :circular="true"
            :autoplay="true" duration="500" interval="12000" @change="cardSwiper"> 
            <swiper-item v-for="(item,index) in swiperList" :key="index" :class="cardCur==index?'cur':''">
              <view class="swiper-item image-banner">
                <image :src="item.url" mode="aspectFill" v-if="item.type=='image'"></image>
              </view>
              <view class="swiper-item-text">
                <view class="tn-color-white tn-padding-top-xs" style="font-size: 60rpx;">{{item.name}}</view>
                <view class="tn-text-sm tn-text-bold tn-color-white tn-padding-top-sm tn-padding-bottom-sm">{{item.text}}</view>
              </view>
            </swiper-item>
          </swiper>
          <view class="indication">
              <block v-for="(item,index) in swiperList" :key="index">
                  <view class="spot" :class="cardCur==index?'active':''"></view>
              </block>
          </view>
          
          <view class="tn-flex tn-flex-wrap tn-padding-sm tn-margin-top-sm">
            
            <view class="tn-padding-xs" style="width: 50%;" @click="tn('/xxx')">
              <view class="bg-linear-1">
                <view class="" style="padding: 40rpx 30rpx 20rpx 30rpx;z-index: 9;position: relative;">
                  <view class="tn-text-lg tn-text-bold" style="color: #1A1A1A;">
                    <text class="tn-text-ellipsis">社区</text>
                  </view>
                  <view class="tn-text-sm tn-padding-top-xs" style="color: #30313280;">
                    <text class="tn-text-ellipsis">游戏交流</text>
                  </view>
                </view>
                <view class="tn-flex tn-flex-row-right" style="margin: -140rpx 10rpx 0 0;z-index: 6;position: relative;">
                  <view class="icon__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-margin-bottom-xs" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/png/280373/1724233594078-assets/web-upload/3339e519-4521-4722-8f95-47c21d4b1dda.png');background-size:100% 100%;background-size: cover;">
                  </view>
                </view>
              </view>
            </view>
            
            <view class="tn-padding-xs" style="width: 50%;" @click="tn('../filter/filter')">
              <view class="bg-linear-2">
                <view class="" style="padding: 40rpx 30rpx 20rpx 30rpx;z-index: 9;position: relative;">
                  <view class="tn-text-lg tn-text-bold" style="color: #1A1A1A;">
                    <text class="tn-text-ellipsis">商城</text>
                  </view>
                  <view class="tn-text-sm tn-padding-top-xs" style="color: #30313280;">
                    <text class="tn-text-ellipsis">积分兑换</text>
                  </view>
                </view>
                <view class="tn-flex tn-flex-row-right" style="margin: -140rpx 10rpx 0 0;z-index: 6;position: relative;">
                  <view class="icon__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-margin-bottom-xs" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/png/280373/1724233594052-assets/web-upload/a6f58b5b-464a-4e85-a079-7d7f22de72fc.png');background-size:100% 100%;background-size: cover;">
                  </view>
                </view>
              </view>
            </view>
            
            <view class="tn-padding-xs" style="width: 50%;" @click="tn('/xxx')">
              <view class="bg-linear-3">
                <view class="" style="padding: 40rpx 30rpx 20rpx 30rpx;z-index: 9;position: relative;">
                  <view class="tn-text-lg tn-text-bold" style="color: #1A1A1A;">
                    <text class="tn-text-ellipsis">互动</text>
                  </view>
                  <view class="tn-text-sm tn-padding-top-xs" style="color: #30313280;">
                    <text class="tn-text-ellipsis">好友互动</text>
                  </view>
                </view>
                <view class="tn-flex tn-flex-row-right" style="margin: -140rpx 10rpx 0 0;z-index: 6;position: relative;">
                  <view class="icon__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-margin-bottom-xs" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/png/280373/1724233594321-assets/web-upload/b29aaa30-b94c-427b-8060-24bbb6e42f4c.png');background-size:100% 100%;background-size: cover;">
                  </view>
                </view>
              </view>
            </view>
            
            <view class="tn-padding-xs" style="width: 50%;" @click="tn('/xxx')">
              <view class="bg-linear-4">
                <view class="" style="padding: 40rpx 30rpx 20rpx 30rpx;z-index: 9;position: relative;">
                  <view class="tn-text-lg tn-text-bold" style="color: #1A1A1A;">
                    <text class="tn-text-ellipsis">反馈</text>
                  </view>
                  <view class="tn-text-sm tn-padding-top-xs" style="color: #30313280;">
                    <text class="tn-text-ellipsis">意见征集</text>
                  </view>
                </view>
                <view class="tn-flex tn-flex-row-right" style="margin: -140rpx 10rpx 0 0;z-index: 6;position: relative;">
                  <view class="icon__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-margin-bottom-xs" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/png/280373/1724233594092-assets/web-upload/ffd95877-1152-4213-8b96-17641267b8f6.png');background-size:100% 100%;background-size: cover;">
                  </view>
                </view>
              </view>
            </view>
            
          </view>
          
          <view class=""  style="padding: 15rpx;margin: 0 30rpx 30rpx 30rpx;border-radius: 20rpx;border: 8rpx solid #FFFFFF;background: linear-gradient(180deg, #E2FCE2,#EFFCF1);">
            <view class="tn-flex tn-flex-row-between tn-flex-col-center" style="padding: 15rpx;">
              <view class="tn-text-lg tn-text-bold">
                热门推荐
              </view>
              <view class="tn-color-gray tn-text-sm">
                每日精选 · 精彩纷呈
                <text class="tn-icon-starry tn-padding-left-sm"></text>
              </view>
            </view>
            <view class="tn-flex tn-flex-wrap">
              <block v-for="(item,index) in content" :key="index">
                <view class="" style="width: 25%" @click="tn('/homePages/xxx')">
                  <view class="" style="padding: 15rpx;">
                    <view class="coll-pic" :style="'background-image:url(' + item.mainImage + ')'">
                      <view class="coll-image">
                      </view>
                    </view>
                  </view>
                </view>
              </block>
            </view>
            按钮
            <view class="tn-flex" style="padding: 15rpx;">
              <view class="tn-flex-1 justify-content-item tn-text-center">
                <tn-button backgroundColor="#FFFFFF " padding="40rpx 0" width="100%" :fontSize="28" fontColor="#00000080" @click="tn('')">
                  <text class="">查看合集</text>
                  <text class="tn-icon-right-fill tn-padding-left-xs"></text>
                </tn-button>
              </view>
            </view>
          </view>
          
          推荐广告
          <view class="tn-margin tn-margin-top-xs">
            <view class="" style="background: linear-gradient(180deg, #FEF1D7,#FEF1D7, #FEF1D780);border-radius: 20rpx;border: 8rpx solid #FFFFFF;">
              
              <view class="advertise tn-flex tn-flex-row-between tn-flex-col-center">
                <view class="tn-margin-left-sm">
                  <view class="tn-color-black">
                    <text class="tn-text-bold" style="position: absolute;margin: -70rpx 0 0 0;font-size: 30rpx;">你可能感兴趣的用户</text>
                  </view>
                </view>
                <view class="" style="margin: -100rpx 20rpx 0 0;">
                  <tn-button size="sm" shape="round" backgroundColor="#000000" fontColor="#FFFFFF" padding="16rpx 0" width="130rpx">
                    <text class="tn-icon-vip tn-padding-right-sm tn-text-lg" v-if="false"></text>
                    <text class="tn-text-bold">查看更多</text>
                  </tn-button>
                </view>
              </view>
              
              <view class="tn-flex tn-flex-wrap tn-padding-sm" @click="tn('')" style="margin-top: -130rpx;">
                <view class="tn-flex-1 tn-margin-right-xs">
                  <view class="image-piccapsule" style="background-image:url('https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603396-assets/web-upload/a7f7dd1d-3618-4888-a20c-6b55a6aa69a4.jpeg');">
                    <view class="image-capsule">
                    </view>
                  </view>  
                </view>
                <view class="tn-flex-1 tn-margin-right-xs tn-margin-left-xs">
                  <view class="image-piccapsule" style="background-image:url('https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603532-assets/web-upload/c8f3f24f-789c-474f-9fdf-831bedb7833c.jpeg');">
                    <view class="image-capsule">
                    </view>
                  </view>  
                </view>  
                <view class="tn-flex-1 tn-margin-left-xs">
                  <view class="image-piccapsule" style="background-image:url('https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603530-assets/web-upload/ef2234e6-6419-4c2a-921b-927a61ecfffa.jpeg');">
                    <view class="image-capsule">
                    </view>
                  </view>  
                </view>    
              </view>
              
            </view>
          </view> -->

          <!-- tabs导航 固定在顶部导航栏下方 -->

          
          

        </view>
        
      </view>
    </view>
    
    <!-- 瀑布流内容-->
    <view class="time time-fill" :class="showUserModal==true?'tn-con-hide':'tn-con-show'">

      <view v-if="current === 0 && list.length > 0">
        <view class="home-list">
          <tn-waterfall ref="waterfall" v-model="list" @finish="handleWaterFallFinish">
            <template v-slot:left="{ leftList }">
              <!-- 日签能提高品牌形象的曝光量，靠自己去打造品牌形象的吖 -->
              <view class="image-pic" style="background-image:url('https://cdn.nlark.com/yuque/0/2023/jpeg/280373/1691422390842-assets/web-upload/a66161b0-ffd3-4bbe-bb7e-a64233fd0474.jpeg');background-size: cover;background-position: center;" @click="tn('../sign/sign')">
                <view class="image-design">
                  <view class="tn-text-center" style="width: 100%;height: 100%;background: linear-gradient(0deg, rgba(0,0,0,0.9), rgba(0,0,0,0));position: absolute;bottom: 0;border-radius: 18rpx;">
                    <view class="tn-text-lg tn-text-bold tn-padding-left tn-padding-right tn-color-white clamp-text-1" style="padding-top: 120rpx;">每日打卡</view>
                    <view class="tn-text-sm tn-text-bold tn-padding tn-color-white clamp-text-1">12.9万热度</view>
                    <view class="tn-flex tn-flex-col-center tn-flex-row-center">
                      <view class="">
                        <tn-avatar-group :lists="groupList" size="sm"></tn-avatar-group>
                      </view>
                      <view class="tn-color-white tn-text-sm tn-padding-left-sm">
                        129 人
                      </view>
                    </view>
                  </view>
                </view>
              </view>
              <view v-for="(item, index) in leftList" :key="item.id" class="game__item"
                @click="goPostDetail(item)">
                <view class="item__image">
                  <tn-lazy-load :threshold="6000" height="100%" :image="item.mainImage" :index="item.id"
                    imgMode="widthFix"></tn-lazy-load>
                </view>
                <view class="item__data">
                  <view class="item__title-container">
                    <text class="item__title tn-padding-top-xs">{{ item.title }}</text>
                  </view>
                  <view v-if="item.tags && item.tags.length > 0" class="item__tags-container">
                    <view v-for="(tagItem, tagIndex) in item.tags" :key="tagIndex" class="item__tag">{{ tagItem }}
                    </view>
                  </view>
      
                  <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding-top-xs">
                    <view class="justify-content-item">
                      <view class="tn-flex tn-flex-col-center tn-flex-row-left">
                        <view class="logo-pic">
                          <view class="logo-image">
                            <view class=""
                              :style="'background-image:url('+ item.userImage +');width: 40rpx;height: 40rpx;background-size: cover;'">
                            </view>
                          </view>
                        </view>
                        <view class="tn-padding-left-xs">
                          <text class="tn-color-gray tn-text-sm">{{ item.userName }}</text>
                        </view>
      
                      </view>
                    </view>
                    <view class="justify-content-item">
                      <text class="tn-icon-rocket tn-color-gray tn-padding-right-xs"></text>
                      <text class="tn-color-gray">{{ item.viewUser.viewUserCount }}</text>
                    </view>
                  </view>
      
      
                </view>
              </view>
            </template>
            <template v-slot:right="{ rightList }">
              <view v-for="(item, index) in rightList" :key="item.id" class="game__item"
                @click="goPostDetail(item)">
                <view class="item__image">
                  <tn-lazy-load :threshold="6000" height="100%" :image="item.mainImage" :index="item.id"
                    imgMode="widthFix"></tn-lazy-load>
                </view>
                <view class="item__data">
                  <view class="item__title-container">
                    <text class="item__title tn-padding-top-xs">{{ item.title }}</text>
                  </view>
                  <view class="item__tags-container">
                    <view v-for="(tagItem, tagIndex) in item.tags" :key="tagIndex" class="item__tag">{{ tagItem }}
                    </view>
                  </view>
                  <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding-top-xs">
                    <view class="justify-content-item">
                      <view class="tn-flex tn-flex-col-center tn-flex-row-left">
                        <view class="logo-pic">
                          <view class="logo-image">
                            <view class=""
                              :style="'background-image:url('+ item.userImage +');width: 40rpx;height: 40rpx;background-size: cover;'">
                            </view>
                          </view>
                        </view>
                        <view class="tn-padding-left-xs">
                          <text class="tn-color-gray tn-text-sm">{{ item.userName }}</text>
                        </view>
      
                      </view>
                    </view>
                    <view class="justify-content-item">
                      <text class="tn-icon-rocket tn-color-gray tn-padding-right-xs"></text>
                      <text class="tn-color-gray">{{ item.viewUser.viewUserCount }}</text>
                    </view>
                  </view>
                </view>
              </view>
            </template>
          </tn-waterfall>
        </view>
        <tn-load-more :status="loadStatus"></tn-load-more>
      </view>
      
      <view
        v-if="current === 0 && list.length === 0"
        class="empty-wrap"
        :style="{ height: 'calc(100vh - ' + vuex_custom_bar_height + 'px - 90rpx)', paddingBottom: 'calc(' + vuex_custom_bar_height + 'px + 90rpx)' }"
      >
        <view class="empty-page" style="background-color: #F6F6F6;">
          <view class="tn-text-center" style="font-size: 260rpx;">
            <text class="tn-icon-game tn-color-gray--disabled"></text>
          </view>
          <view class="tn-color-gray tn-text-center tn-text-lg">暂无内容</view>
        </view>
      </view>

      <view
        v-if="current==1"
        class="empty-wrap"
        :style="{ height: 'calc(100vh - ' + vuex_custom_bar_height + 'px - 90rpx)', paddingBottom: 'calc(' + vuex_custom_bar_height + 'px + 90rpx)' }"
      >
        <view class="empty-page" style="background-color: #F6F6F6;">
          <view class="tn-text-center" style="font-size: 260rpx;">
            <text class="tn-icon-game tn-color-gray--disabled"></text>
          </view>
          <view class="tn-color-gray tn-text-center tn-text-lg">暂无内容，嘤嘤嘤</view>
        </view>
      </view>

      <view class="empty-wrap" v-if="current==2" :style="{ height: 'calc(100vh - ' + vuex_custom_bar_height + 'px - 90rpx)', paddingBottom: 'calc(' + vuex_custom_bar_height + 'px + 90rpx)' }">
        <view class="empty-page" style="background-color: #F6F6F6;">
          <view class="tn-text-center" style="font-size: 260rpx;">
            <text class="tn-icon-game tn-color-gray--disabled"></text>
          </view>
          <view class="tn-color-gray tn-text-center tn-text-lg">暂无内容，嘤嘤嘤嘤嘤嘤</view>
        </view>
      </view>

      <view class="empty-wrap" v-if="current==3" :style="{ height: 'calc(100vh - ' + vuex_custom_bar_height + 'px - 90rpx)', paddingBottom: 'calc(' + vuex_custom_bar_height + 'px + 90rpx)' }">
        <view class="empty-page" style="background-color: #F6F6F6;">
          <view class="tn-text-center" style="font-size: 260rpx;">
            <text class="tn-icon-game tn-color-gray--disabled"></text>
          </view>
          <view class="tn-color-gray tn-text-center tn-text-lg">暂无内容，嘤嘤嘤嘤嘤嘤嘤</view>
        </view>
      </view>
      
    </view> 
    
    
    <!-- popup会有延迟，采用这种方式来优化弹窗的优化体验-->
    <user-modal v-model="showUserModal"></user-modal>
    
    <view class="tn-tabbar-height"></view>
  </view>
</template>

<script>
  import UserModal from '@/components/user/user.vue'
  import { community } from '@/api/index.js'

  // 日志前缀，便于在控制台过滤
  const LOG_TAG = '[PageA]'

  export default {
    components: { UserModal },
    name: 'PageA',
    data() {
      return {
        /* 选择弹窗*/
        showUserModal: false,

        /* 分页参数 */
        page: 1,
        pageSize: 10,
        /* 是否还有更多数据 */
        hasMore: true,
        /* 是否正在加载 */
        loading: false,
        
        content: [{
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014593433-assets/web-upload/6e1ba4f3-4757-4693-b827-b8803072c5c9.jpeg',
          },
          {
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015239-assets/web-upload/3f22e659-42d3-4106-be67-46521c22eb99.jpeg',
          },
          {
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1715959853964-assets/web-upload/2c4f9b49-0014-4714-a9c6-60b2e5c06d94.jpeg',
          }
        ],
        
        /* 搜索*/
        searlist: [
          '搜索 蛋仔派对',
          '搜索 王国保卫战',
          '搜索 保卫萝卜'
        ],
        barStyle: {
          color: '#000000'
        },
        current: 0,
        fixedList: [
          {name: '推荐'},
          {name: '热门'},
          {name: '独家'},
          {name: '热卖'}
        ],
        
        cardCur: 0,
        swiperList: [{
          id: 0,
          type: 'image',
          name: '原神 她来了',
          text: '开启全新的探索之旅',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716047166485-assets/web-upload/ee3aa0c0-8589-41d6-b920-fae1a90a67d8.jpeg',
        }, {
          id: 1,
          type: 'image',
          name: '',
          text: '',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716184526452-assets/web-upload/dc5aadb2-d6bb-4cd3-8db2-2faddcd51c54.jpeg',
        }, {
          id: 2,
          type: 'image',
          name: '',
          text: '',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399137-assets/web-upload/51b8b734-e1db-4744-82f5-56186cb36f6d.jpeg',
        }, {
          id: 3,
          type: 'image',
          name: '微信号 tnkewo',
          text: '商业合作请联系作者',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039723058-assets/web-upload/28e928b8-4811-4331-8030-a6086efe4e4b.jpeg',
        }, {
          id: 4,
          type: 'image',
          name: '努力成为大佬',
          text: '一起加油吖',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1715959853964-assets/web-upload/2c4f9b49-0014-4714-a9c6-60b2e5c06d94.jpeg',
        }],
        
        groupList: [
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'},
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'},
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'},
          {src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'},
        ],
        
        /* 瀑布流*/
        loadStatus: 'loadmore',
        list: [],
        data: [{
            title: '纪念碑谷打卡',
            userName: '试试就逝世',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014593433-assets/web-upload/6e1ba4f3-4757-4693-b827-b8803072c5c9.jpeg',
            userImage: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1664005699098-assets/web-upload/e8b29292-72fc-4c1e-9d7c-fd9dba31cb62.jpeg',
            tags: ['解谜', '简约'],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 338
            },
          },
          {
            title: '王国保卫战-塔防启动',
            userName: '你的名字',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025891640-assets/web-upload/8b4440ce-710b-4d3c-afff-5d49e6c7a16a.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 289
            },
          },
          {
            title: '',
            userName: '青梅煮马',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            tags: ['钓鱼', '萝卜'],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 381
            },
          },
          {
            title: '钓鱼模式，启动',
            userName: '你的名字',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1715959853964-assets/web-upload/2c4f9b49-0014-4714-a9c6-60b2e5c06d94.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 526
            },
          },
          {
            title: '',
            userName: '凶一下试试',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 526
            },
          },
          {
            title: '春暖花开',
            userName: '凶一下试试',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015216-assets/web-upload/ff424f75-5d37-4b80-8242-bb21e415875e.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 526
            },
          },
          {
            title: '',
            userName: '坟头草三米高',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015239-assets/web-upload/3f22e659-42d3-4106-be67-46521c22eb99.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 372
            },
          },
          {
            title: '来一局蛋仔派对',
            userName: '你的猪猪',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039722992-assets/web-upload/ae74a7d3-dc20-4cb5-9178-c77f9aa10462.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            tags: ['蛋仔', '活动'],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 694
            },
          },
          {
            title: '',
            userName: '试试就逝世',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399113-assets/web-upload/6ebf5f25-4c3a-488a-b221-0497f2945549.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 508
            },
          },
          {
            title: '',
            userName: '你的名字',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716047166485-assets/web-upload/ee3aa0c0-8589-41d6-b920-fae1a90a67d8.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 923
            },
          },
          {
            title: '',
            userName: '图鸟东东',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015493-assets/web-upload/d949d756-59ea-4047-8edb-afa72d2ff8af.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 989
            },
          },
          {
            title: '',
            userName: '抓住那只猪吖',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399125-assets/web-upload/f75e889a-57a6-418a-9315-2814e3441ec1.jpeg',
            userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            tags: [],
            viewUser: {
              latestUserAvatar: [{
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg'
                },
                {
                  src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
                },
              ],
              viewUserCount: 129
            },
          }
        ],
        
      }
    },
    
    created() {
      /* 瀑布流：首次加载真实接口数据 */
      this.fetchPostList(true)
    },

    methods: {
      // 打开选择弹框
      openUserModal() {
        if (typeof wx !== 'undefined' && wx.vibrateShort) {
          wx.vibrateShort();
        }
        this.showUserModal = true
      },
      // cardSwiper
      cardSwiper(e) {
        this.cardCur = e.detail.current
      },
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
      /* 瀑布流：对接真实帖子列表接口 */
      // 获取帖子列表
      async fetchPostList(isRefresh = false) {
        if (this.loading) {
          console.log(LOG_TAG, '正在加载中，忽略重复请求')
          return
        }
        if (isRefresh) {
          this.page = 1
          this.hasMore = true
          this.list = []
        }
        if (!this.hasMore) {
          console.log(LOG_TAG, '没有更多数据了')
          this.loadStatus = 'nomore'
          return
        }
        this.loading = true
        this.loadStatus = 'loading'
        const params = {
          page: this.page,
          pageSize: this.pageSize
        }
        console.log(LOG_TAG, '>>> 请求帖子列表, params:', params)
        const startTime = Date.now()
        try {
          const res = await community.getPostList(params)
          console.log(LOG_TAG, '<<< 帖子列表响应:', JSON.stringify(res))
          console.log(LOG_TAG, `帖子列表接口耗时: ${Date.now() - startTime}ms`)
          const list = (res && res.rows) || []
          const total = (res && res.total) || 0
          // 适配后端字段到前端展示结构
          const adaptedList = list.map(item => this.adaptPostItem(item))
          this.list = isRefresh ? adaptedList : this.list.concat(adaptedList)
          // 判断是否还有更多
          this.hasMore = this.list.length < total
          this.loadStatus = this.hasMore ? 'loadmore' : 'nomore'
          // 页码递增，为下次加载准备
          if (this.hasMore) this.page++
          console.log(LOG_TAG, `已加载 ${this.list.length}/${total} 条, hasMore:`, this.hasMore)
        } catch (err) {
          console.error(LOG_TAG, '<<< 帖子列表请求失败:', err)
          this.loadStatus = 'loadmore'
          // 接口失败时使用本地静态数据兜底，避免页面空白
          if (isRefresh && this.list.length === 0) {
            console.warn(LOG_TAG, '接口失败，使用本地静态数据兜底')
            this.fallbackToStaticData()
          }
        } finally {
          this.loading = false
        }
      },
      // 适配后端帖子数据到瀑布流展示结构
      adaptPostItem(item) {
        // 帖子主图兜底列表
        const fallbackImages = [
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603396-assets/web-upload/a7f7dd1d-3618-4888-a20c-6b55a6aa69a4.jpeg',
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603532-assets/web-upload/c8f3f24f-789c-474f-9fdf-831bedb7833c.jpeg',
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603530-assets/web-upload/ef2234e6-6419-4c2a-921b-927a61ecfffa.jpeg',
          'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711183180435-assets/web-upload/a091b9f2-c587-412d-96c4-adbedbb0a889.jpeg',
          'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711182957902-assets/web-upload/c9ac3d07-81cb-40a6-9944-31a8e5f0386b.jpeg',
          'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711186780372-assets/web-upload/a7f072c1-5161-42ed-b707-0baa473936e5.jpeg'
        ]
        // 用户头像兜底列表
        const fallbackAvatars = [
          'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg',
          'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
          'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
          'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg'
        ]
        const rawId = Number(item.id) || 0
        const userId = Number(item.userId) || rawId
        const fallbackImg = fallbackImages[rawId % fallbackImages.length]
        const fallbackAvatar = fallbackAvatars[userId % fallbackAvatars.length]
        return {
          id: item.id || this.$t.uuid(),
          title: item.title || '',
          userName: item.username || item.userName || '匿名用户',
          mainImage: item.mainImage || (item.images && item.images[0]) || fallbackImg,
          userImage: item.userImage || item.avatar || fallbackAvatar,
          tags: item.tags || [],
          likeCount: item.likeCount || 0,
          commentCount: item.commentCount || 0,
          viewCount: item.viewCount || 0,
          viewUser: {
            latestUserAvatar: (item.viewUser && item.viewUser.latestUserAvatar) || [],
            viewUserCount: (item.viewUser && item.viewUser.viewUserCount) || item.viewCount || 0
          }
        }
      },
      // 本地静态数据兜底（接口不可用时）
      fallbackToStaticData() {
        for (let i = 0; i < 10; i++) {
          let index = this.$t.number.randomInt(0, this.data.length - 1)
          let item = JSON.parse(JSON.stringify(this.data[index]))
          item.id = this.$t.uuid()
          this.list.push(item)
        }
      },
      // 瀑布流加载完毕事件
      handleWaterFallFinish() {
        this.loadStatus = 'loadmore'
      },
      // 跳转帖子详情（携带 id）
      goPostDetail(item) {
        console.log(LOG_TAG, '跳转帖子详情, id:', item.id)
        uni.navigateTo({
          url: '/pages/post/detail?id=' + item.id
        })
      }
    }
  }
</script>


<style lang="scss" scoped>
  .pagesA {
    min-height: 100%;
    background-color: #1E1E1E80;
  }

  .home-list {
    padding: 20rpx 20rpx 100rpx;
    background-color: #F6F6F6;
  }

  /* 自定义导航栏内容 start */
  .custom-nav {
    height: 100%;

    &__logo {
      margin: auto 5rpx;
      font-size: 60rpx;
      margin-right: 10rpx;
      margin-left: 30rpx;
      flex-basis: 5%;
    }
  }
  
  .game-image{
    z-index: 9999 !important;
    width: 195rpx;
    height: 50rpx;
    position: relative;
  }
  .game-pic{
    z-index: 9999 !important;
    background-size: cover;
    background-repeat:no-repeat;
    // background-attachment:fixed;
    background-position:center;
  }
  /* 自定义导航栏内容 end */

  /* 搜索 */
  .search-fixed{
    position: relative;
    width: 100%;
  }

  /* 轮播视觉差 start */
  .card-swiper {
    height: 230rpx !important;
  }
    
  .card-swiper swiper-item {
    width: 750rpx !important;
    left: 0rpx;
    box-sizing: border-box;
    padding: 0rpx 30rpx 0rpx 30rpx;
    overflow: initial;
  }
    
  .card-swiper swiper-item .swiper-item {
    width: 100%;
    display: block;
    height: 100%;
    border-radius: 20rpx;
    transform: scale(1);
    transition: all 0.2s ease-in 0s;
    overflow: hidden;
    // border: 8rpx solid #FFFFFF;
  }
    
  .card-swiper swiper-item.cur .swiper-item {
    transform: none;
    transition: all 0.2s ease-in 0s;
  }
    
  .card-swiper swiper-item .swiper-item-text {
    margin-top: -140rpx;
    width: 100%;
    display: block;
    height: 100%;
    border-radius: 10rpx;
    transform: translate(100rpx, -60rpx) scale(0.9, 0.9);
    transition: all 0.6s ease 0s;
    overflow: hidden;
  }
    
  .card-swiper swiper-item.cur .swiper-item-text {
    margin-top: -200rpx;
    width: 100%;
    text-shadow: 0 0 6rpx #000000;
    transform: translate(0rpx, 0rpx) scale(0.9, 0.9);
    transition: all 0.6s ease 0s;
  }
  
  .image-banner{
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .image-banner image{
    width: 100%;
    height: 100%;
  }
  
  /* 轮播指示点 start*/
  .indication{
    z-index: 0;
    width: 100%;
    height: 36rpx;
    position: absolute;
    display:flex;
    flex-direction:row;
    align-items:center;
    justify-content:center;
  }
  
  .spot{
    background-color: #FFFFFF;
    opacity: 0.6;
    width: 10rpx;
    height: 10rpx;
    border-radius: 20rpx;
    top: -40rpx;
    margin: 0 8rpx !important;
    position: relative;
  }
  
  .spot.active{
    opacity: 1;
    width: 30rpx;
    background-color: #FFFFFF;
  }
  
  /* 用户头像 start */
  .logo-image {
    width: 36rpx;
    height: 36rpx;
    position: relative;
  }
  
  .logo-pic {
    background-size: cover;
    background-repeat: no-repeat;
    // background-attachment:fixed;
    background-position: center;
    border: 1rpx solid rgba(255, 255, 255, 0.05);
    // box-shadow: 0rpx 0rpx 80rpx 0rpx rgba(0, 0, 0, 0.15);
    border-radius: 50%;
    overflow: hidden;
    // background-color: #FFFFFF;
  }
  
  /* 图标容器 start */
  .icon {
    &__item {
      &--icon {
        width: 160rpx;
        height: 160rpx;
        position: relative;
        z-index: 1;
      }
    }
  }  
  
  
  .bg-linear-1{
    background: linear-gradient(120deg, #FFF8DE, #FFF8E0);
    border-radius: 20rpx;
    border: 8rpx solid #FFFFFF;
  }
  .bg-linear-2{
    background: linear-gradient(120deg, #FFE4E0, #FFF1EF);
    border-radius: 20rpx;
    border: 8rpx solid #FFFFFF;
  }
  .bg-linear-3{
    background: linear-gradient(120deg, #E6F7E7, #EEF7EF);
    border-radius: 20rpx;
    border: 8rpx solid #FFFFFF;
  }
  .bg-linear-4{
    background: linear-gradient(120deg, #E5F0FF, #E8F0FF);
    border-radius: 20rpx;
    border: 8rpx solid #FFFFFF;
  }
  
  .coll-image{
    padding: 65rpx 0rpx;
    font-size: 16rpx;
    position: relative;
  }
  .coll-pic{
    background-size: cover;
    background-repeat:no-repeat;
    // background-attachment:fixed;
    background-position:center;
    border-radius: 10rpx;
    border: 1rpx solid #FFFFFF;
  }
  
  /* 广告*/
  .advertise {
    width: 100%;
    height: 200rpx;
    border-radius: 15rpx;
    position: relative;
    z-index: 1;
    
    &::after {
      content: " ";
      position: absolute;
      z-index: -1;
      width: 100%;
      height: 90rpx;
      left: 0;
      top: 0rpx;
      // border-radius: inherit;
      opacity: 1;
      transform: scale(1, 1);
      background-size: 100% 100%;
      background-image: url(https://cdn.nlark.com/yuque/0/2022/png/280373/1666688018137-assets/web-upload/577717f2-70eb-4cec-9c23-bcce95d34fb5.png);
    }    
  }
  
  /* 胶囊banner*/
  .image-capsule{
    padding: 102rpx 0rpx;
    position: relative;
  }
  .image-piccapsule{
    background-size: cover;
    background-repeat:no-repeat;
    // background-attachment:fixed;
    background-position:center;
    border-radius: 15rpx;
  }
  
  /* 瀑布流*/
  .game__item {
    background-color: #FFFFFF;
    overflow: hidden;
    border-radius: 18rpx;
    padding: 6rpx;
    margin: 0 10rpx 20rpx 10rpx;
    // box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
  
    .item {
  
      /* 图片 start */
      &__image {
        width: 100%;
        height: auto;
        background-color: #FFFFFF;
        border: 1rpx solid #F8F7F8;
        border-radius: 16rpx;
        overflow: hidden;
      }
  
      /* 图片 end */
  
      /* 内容 start */
      &__data {
        padding: 14rpx 10rpx;
      }
  
      /* 标题 start */
      &__title-container {
        text-align: justify;
        line-height: 38rpx;
        vertical-align: middle;
      }
  
      &__title {
        font-size: 26rpx;
      }
  
      /* 标题 end */
  
      /* 标签 start */
      &__tags-container {
        display: flex;
        flex-direction: row;
        flex-wrap: nowrap;
        align-items: center;
        justify-content: flex-start;
      }
  
      &__tag {
        margin: 10rpx;
        color: #7C8191;
        background-color: #F3F2F7;
        padding: 4rpx 14rpx 6rpx;
        border-radius: 10rpx;
        font-size: 20rpx;
  
        &:first-child {
          margin-left: 0rpx !important;
        }
      }
  
      /* 标签 end */
  
      /* 内容 end */
    }
  }
  
  /* 编辑内容 start*/
  .image-design{
    padding: 200rpx 0rpx;
    font-size: 40rpx;
    font-weight: 300;
    position: relative;
  }
  .image-pic{
    // border: 1rpx solid rgba(0, 0, 0, 0.04);
    background-size: cover;
    background-repeat:no-repeat;
    // background-attachment:fixed;
    background-position:top;
    margin: 0 10rpx 20rpx 10rpx;
    border-radius: 18rpx;
  }
  /* 编辑内容 end*/
  
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
  
  .dd-transform{
    -webkit-perspective: 1800px;
    perspective: 1800px;//透视太大会超过屏幕就不好了吖
    transform-style: preserve-3d;
    overflow: hidden;
  }
  
  .time{
    transition: all 0.3s ease-in-out;
    overflow: hidden;
  }
  
    /* 高度宽度 */
  .tn-height{
    // max-height: 100vh;
    background-color: #F6F6F6;
    margin: 0 auto 0;
    // transform: scale(1) translateX(0px) rotateY(0deg);
    // will-change: transform;
    transform-style: preserve-3d;
    border-radius: 0rpx;
    overflow: hidden;
  }
  .tn-height-cur{
    // max-height: 100vh;
    background-color: #F6F6F6;
    margin: 0 auto 0;
    transform-style: preserve-3d;
    transform: scale(0.9) translateX(50px) rotateY(-45deg);
    -webkit-transform: scale(0.9) translateX(50px) rotateY(-45deg);
    -moz-transform: scale(0.9) translateX(50px) rotateY(-45deg);
    // will-change: transform;
    border-radius: 80rpx;
    overflow: hidden;
  }
  
  .tn-con-hide{
    opacity: 0;
  }
  
  /* 底部安全边距 start*/
  .tn-tabbar-height {
    background-color: #F6F6F6;
    min-height: 60rpx;
    height: calc(80rpx + env(safe-area-inset-bottom) / 2);
    height: calc(80rpx + constant(safe-area-inset-bottom));
  }

  /* tabs固定在顶部 */
  .tabs-fixed {
    position: fixed;
    left: 0;
    width: 100%;
    z-index: 997;
    background-color: #F6F6F6;
  }

  /* 容器铺满页面底部，通过内边距让缺省内容与整个视口中心对齐 */
  .empty-wrap {
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    background-color: #F6F6F6;
  }

  /* 空页面居中 */
  .empty-page {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20rpx;
  }

</style>