<template>
  <view class="pagesC tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <!-- <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F600">
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left">
        <view class="">
          <text class="tn-margin-left tn-text-bold tn-text-xl leaf-color">排行榜</text>
        </view>
      </view>
    </tn-nav-bar> -->
    
    <!-- 背景图片-->
    <!-- <view class="game-fixed" style="background-image:url();">
    </view> -->
    
    <!-- banner形式，图鸟内部群反馈说加个banner会好一些-->
    <view class="banner">
      
      <swiper class="card-swiper" :circular="true"
        :autoplay="true" duration="800" interval="8000" @change="cardSwiper"> 
        <swiper-item v-for="(item,index) in swiperList" :key="index" :class="cardCur==index?'cur':''">
          <view class="swiper-item image-banner">
            <image :src="item.url" mode="aspectFill" v-if="item.type=='image'"></image>
          </view>
          <view class="swiper-item-text">
            <view class="tn-text-xxl tn-text-bold tn-color-white">{{item.title}}</view>
          </view>
        </swiper-item>
      </swiper>
      <view class="indication">
          <block v-for="(item,index) in swiperList" :key="index">
              <view class="spot" :class="cardCur==index?'active':''"></view>
          </block>
      </view>
    </view>
    
    <view class="" :style="{paddingTop: vuex_custom_bar_height + 200 + 'px'}" >
      
      <!-- 组件对应可选项容器 -->
      <view class="fire-bg" style="position: fixed;bottom:0;width:100%;border-radius: 50rpx 50rpx 0 0;padding: 20rpx 0;">
        <view class="tn-margin-sm">
          <tn-tabs :list="fixedList" :current="current" :isScroll="false" barHeight="6" barWidth="22" :showBar="true"
            :activeItemStyle="activeItemStyle" :inactiveItemStyle="inactiveItemStyle" :barStyle="barStyle" inactiveColor="#1E1E1E" activeColor="#F6F6F6" :bold="false" :fontSize="26" :badgeOffset="[10, 30]"
            @change="tabChange" backgroundColor="#F6F6F600" :height="66"></tn-tabs>
        </view>
        
        <scroll-view scroll-y="true" style="max-height: 56vh;">
          <view class="" v-if="current==0">
            <block v-for="(item,index) in content" :key="index">
              <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin">
                <view class="justify-content-item">
                  <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                    <view class="tn-flex tn-flex-row-center tn-padding-right" style="min-width: 90rpx;">
                      <text class="" :class="['tn-icon-' + item.color]" v-if="index<3"></text>
                      <text class="tn-text-xxl" v-else>{{ item.userNumber }}</text>
                    </view>
                    <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                      <view class="avatar-all" :class="['game-bg-' + item.color]">
                        <view class="" :style="'background-image:url('+ item.userAvatar + ');width: 100rpx;height: 100rpx;background-size: cover;'">
                        </view>
                      </view>
                      <view class="tn-padding-right tn-text-ellipsis">
                        <view class="tn-padding-right tn-padding-left-sm tn-text-bold">{{ item.userName }}</view>
                        <view class="tn-padding-right tn-padding-left-sm tn-color-gray tn-text-xs tn-padding-top-xs">
                          <text class="tn-padding-right-xs">闪耀值 {{ item.fire }}</text>
                          <text :class="['tn-icon-' + item.trend + '-arrow']"></text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
                <view class="justify-content-item tn-flex-row-center tn-margin-top-xs tn-margin-right-xs">
                  <text class="tn-icon-rocket-fill icon-ranking"></text> 
                </view>
              </view>
            </block>
          </view>
          
          <view class="" v-if="current==1">
            <block v-for="(item,index) in content1" :key="index">
              <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin">
                <view class="justify-content-item">
                  <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                    <view class="tn-flex tn-flex-row-center tn-padding-right" style="min-width: 90rpx;">
                      <text class="" :class="['tn-icon-' + item.color]" v-if="index<3"></text>
                      <text class="tn-text-xxl" v-else>{{ item.userNumber }}</text>
                    </view>
                    <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                      <view class="avatar-all" :class="['game-bg-' + item.color]">
                        <view class="" :style="'background-image:url('+ item.userAvatar + ');width: 100rpx;height: 100rpx;background-size: cover;'">
                        </view>
                      </view>
                      <view class="tn-padding-right tn-text-ellipsis">
                        <view class="tn-padding-right tn-padding-left-sm tn-text-bold">{{ item.userName }}</view>
                        <view class="tn-padding-right tn-padding-left-sm tn-color-gray tn-text-xs tn-padding-top-xs">
                          <text class="tn-padding-right-xs">闪耀值 {{ item.fire }}</text>
                          <text :class="['tn-icon-' + item.trend + '-arrow']"></text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
                <view class="justify-content-item tn-flex-row-center tn-margin-top-xs tn-margin-right-xs">
                  <text class="tn-icon-rocket-fill icon-ranking"></text> 
                </view>
              </view>
            </block>
          </view>
          
          <view class="" v-if="current==2">
            <block v-for="(item,index) in content2" :key="index">
              <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin">
                <view class="justify-content-item">
                  <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                    <view class="tn-flex tn-flex-row-center tn-padding-right" style="min-width: 90rpx;">
                      <text class="" :class="['tn-icon-' + item.color]" v-if="index<3"></text>
                      <text class="tn-text-xxl" v-else>{{ item.userNumber }}</text>
                    </view>
                    <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                      <view class="avatar-all" :class="['game-bg-' + item.color]">
                        <view class="" :style="'background-image:url('+ item.userAvatar + ');width: 100rpx;height: 100rpx;background-size: cover;'">
                        </view>
                      </view>
                      <view class="tn-padding-right tn-text-ellipsis">
                        <view class="tn-padding-right tn-padding-left-sm tn-text-bold">{{ item.userName }}</view>
                        <view class="tn-padding-right tn-padding-left-sm tn-color-gray tn-text-xs tn-padding-top-xs">
                          <text class="tn-padding-right-xs">闪耀值 {{ item.fire }}</text>
                          <text :class="['tn-icon-' + item.trend + '-arrow']"></text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
                <view class="justify-content-item tn-flex-row-center tn-margin-top-xs tn-margin-right-xs">
                  <text class="tn-icon-rocket-fill icon-ranking"></text> 
                </view>
              </view>
            </block>
          </view>
          
          <view class="" v-if="current==3">
            <block v-for="(item,index) in content3" :key="index">
              <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin">
                <view class="justify-content-item">
                  <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                    <view class="tn-flex tn-flex-row-center tn-padding-right" style="min-width: 90rpx;">
                      <text class="" :class="['tn-icon-' + item.color]" v-if="index<3"></text>
                      <text class="tn-text-xxl" v-else>{{ item.userNumber }}</text>
                    </view>
                    <view class="tn-flex tn-flex-row-center tn-flex-col-center">
                      <view class="avatar-all" :class="['game-bg-' + item.color]">
                        <view class="" :style="'background-image:url('+ item.userAvatar + ');width: 100rpx;height: 100rpx;background-size: cover;'">
                        </view>
                      </view>
                      <view class="tn-padding-right tn-text-ellipsis">
                        <view class="tn-padding-right tn-padding-left-sm tn-text-bold">{{ item.userName }}</view>
                        <view class="tn-padding-right tn-padding-left-sm tn-color-gray tn-text-xs tn-padding-top-xs">
                          <text class="tn-padding-right-xs">闪耀值 {{ item.fire }}</text>
                          <text :class="['tn-icon-' + item.trend + '-arrow']"></text>
                        </view>
                      </view>
                    </view>
                  </view>
                </view>
                <view class="justify-content-item tn-flex-row-center tn-margin-top-xs tn-margin-right-xs">
                  <text class="tn-icon-rocket-fill icon-ranking"></text> 
                </view>
              </view>
            </block>
          </view>
          
          <view class="tn-tabbar-height"></view>
        
        </scroll-view>
      </view> 
      
    </view>
    
    <view class="">
      <view class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur button-1" @click="tn('/momentPages/edit')">
        <view class="tn-icon-help-fill tn-color-white">
          <tn-badge backgroundColor="#EA5E55" :dot="true" :radius="12" :absolute="true" :translateCenter="false" top="8rpx" right="8rpx"></tn-badge>
        </view>
      </view>
      <!-- <view class="icon15__item--icon tn-flex tn-flex-row-center tn-flex-col-center tn-shadow-blur button-2"  @click="tn('/momentPages/blogger')">
        <view class="tn-icon-pacman tn-color-white">
        </view>
      </view> -->
    </view> 
    

  </view>
</template>

<script>
  export default {
    name: 'PageC',
    data(){
      return {
        cardCur: 0,
        swiperList: [{
          id: 0,
          type: 'image',
          title: '保卫萝卜',
          name: '',
          text: '',
          url: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
        }, {
          id: 1,
          type: 'image',
          title: '原神',
          name: '',
          text: '',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716047166485-assets/web-upload/ee3aa0c0-8589-41d6-b920-fae1a90a67d8.jpeg',
        }, {
          id: 2,
          type: 'image',
          title: '旅行青蛙',
          name: '',
          text: '',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015604-assets/web-upload/85d809de-b5bf-4407-b8b8-940fc0ea1dfe.jpeg',
        }, {
          id: 3,
          type: 'image',
          title: '纪念碑谷',
          name: '',
          text: '',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716024633880-assets/web-upload/1179e435-59cc-403d-948e-71293de74606.jpeg',
        }, {
          id: 4,
          type: 'image',
          title: '辐射避难所',
          name: '',
          text: '',
          url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg',
        }],
        
        /* showBar为true，就显示出来这个了*/
        barStyle: {
          borderRadius: '100rpx',
          backgroundColor: '#000000'
        },
        activeItemStyle: {
          borderRadius: '20rpx',
          // border: "1rpx solid #1D1F24",
          backgroundColor: '#1D1F24',
          margin: "0 20rpx"
        },
        inactiveItemStyle: {
          borderRadius: '20rpx',
          // border: "1rpx solid #1D1F24",
          backgroundColor: '#FFFFFF',
          margin: "0 20rpx"
        },
        
        current: 0,
        fixedList: [
          {name: '周榜单'},
          {name: '月榜单'},
          {name: '年榜单'},
          {name: '总榜单'}
        ],
        
        content: [
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '1',
            userName: '抓住那只猪',
            color: 'first',
            fire: 540980,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            userNumber: '2',
            userName: '迷路的猪',
            color: 'second',
            fire: 203432,
            trend: "down"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            userNumber: '3',
            userName: '猪不见了',
            color: 'third',
            fire: 129641,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            userNumber: '4',
            userName: '天降大猪',
            color: 'n',
            fire: 89432,
            trend: "up"
          },
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1663473055854-assets/web-upload/9a874711-1603-49d2-b551-ee5936fdfa9b.jpeg',
            userNumber: '5',
            userName: '吖猪吖猪',
            color: 'n',
            fire: 80762,
            trend: "up"
          },
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1663473055830-assets/web-upload/a1a3957a-ef3c-40a3-a067-2dfe9a8d4c3d.jpeg',
            userNumber: '6',
            userName: '寻找猪之路',
            date: '2021年12月20日',
            color: 'n',
            fire: 79864,
            trend: "down"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '7',
            userName: '看见烤乳猪',
            color: 'n',
            fire: 77437,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '8',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 76665,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '9',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 68806,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '10',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 56098,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '11',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 54675,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '12',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 46878,
            trend: "up"
          }
        ],
        
        content1: [
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1663473055792-assets/web-upload/8c18faf8-92b4-49bd-8db5-0f6876401406.jpeg',
            userNumber: '1',
            userName: '奇怪的猪',
            color: 'first',
            fire: 540980,
            trend: "up"
          },
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1663473055820-assets/web-upload/b3abbc05-1dc1-40e1-b46f-cb830a5b4148.jpeg',
            userNumber: '2',
            userName: '粉色的猪',
            color: 'second',
            fire: 203432,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            userNumber: '3',
            userName: '猪不见了',
            color: 'third',
            fire: 129641,
            trend: "down"
          },
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1663571007436-assets/web-upload/e3fdeb91-d8d1-4187-8d91-593559543af3.jpeg',
            userNumber: '4',
            userName: '吖猪吖猪',
            color: 'n',
            fire: 89762,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            userNumber: '5',
            userName: '天降大猪',
            color: 'n',
            fire: 86432,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/y11.jpg',
            userNumber: '6',
            userName: '寻找猪之路',
            date: '2021年12月20日',
            color: 'n',
            fire: 79864,
            trend: "down"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '7',
            userName: '看见烤乳猪',
            color: 'n',
            fire: 77437,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '8',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 76665,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '9',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 68806,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '10',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 56098,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '11',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 54675,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '12',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 46878,
            trend: "up"
          }
        ],
        
        content2: [
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1663473055848-assets/web-upload/dc3a3ce9-6989-4813-8236-5752abc6c832.jpeg',
            userNumber: '1',
            userName: '抓住那只猪',
            color: 'first',
            fire: 540980,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            userNumber: '2',
            userName: '迷路的猪',
            color: 'second',
            fire: 203432,
            trend: "down"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            userNumber: '3',
            userName: '猪不见了',
            color: 'third',
            fire: 129641,
            trend: "up"
          },
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2023/jpeg/280373/1683906918806-assets/web-upload/a9f24fd8-3fde-4e96-a0c5-773cb10829b1.jpeg',
            userNumber: '4',
            userName: '吖猪吖猪',
            color: 'n',
            fire: 89762,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            userNumber: '5',
            userName: '天降大猪',
            color: 'n',
            fire: 86432,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/y11.jpg',
            userNumber: '6',
            userName: '寻找猪之路',
            date: '2021年12月20日',
            color: 'n',
            fire: 79864,
            trend: "down"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '7',
            userName: '看见烤乳猪',
            color: 'n',
            fire: 77437,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '8',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 76665,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '9',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 68806,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '10',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 56098,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '11',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 54675,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '12',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 46878,
            trend: "up"
          }
        ],
        
        content3: [
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '1',
            userName: '抓住那只猪',
            color: 'first',
            fire: 540980,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
            userNumber: '2',
            userName: '迷路的猪',
            color: 'second',
            fire: 203432,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
            userNumber: '3',
            userName: '猪不见了',
            color: 'third',
            fire: 129641,
            trend: "up"
          },
          {
            userAvatar: 'https://cdn.nlark.com/yuque/0/2023/jpeg/280373/1683905940454-assets/web-upload/4a4d5700-d88a-47ab-ab5e-fbbb50609528.jpeg',
            userNumber: '4',
            userName: '吖猪吖猪',
            color: 'n',
            fire: 89762,
            trend: "down"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
            userNumber: '5',
            userName: '天降大猪',
            color: 'n',
            fire: 86432,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/blogger/y11.jpg',
            userNumber: '6',
            userName: '寻找猪之路',
            date: '2021年12月20日',
            color: 'n',
            fire: 79864,
            trend: "down"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '7',
            userName: '看见烤乳猪',
            color: 'n',
            fire: 77437,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '8',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 76665,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '9',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 68806,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '10',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 56098,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '11',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 54675,
            trend: "up"
          },
          {
            userAvatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
            userNumber: '12',
            userName: '抓住那只猪吖',
            color: 'n',
            fire: 46878,
            trend: "up"
          }
        ],
        
      }
    },
    methods: {
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
    }
  }
</script>

<style lang="scss" scoped>
  .pagesC{
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
  
  /* 图标 start */
  .icon-ranking {
    // background-color: #000000;
    background: linear-gradient(-160deg, #000000, #D3F427, #000000, #D3F427);
    color: #FFFFFF;
    font-variant: small-caps;
    width: 50rpx;
    height: 50rpx;
    line-height: 50rpx;
    margin-top: -10rpx;
    display: inline-flex;
    text-align: center;
    justify-content: center;
    vertical-align: middle;
    font-size: 30rpx;
    white-space: nowrap;
    opacity: 0.9;
    background-size: cover;
    background-position: 50%;
    border-radius: 100rpx;  
    border: 1rpx solid #FFFFFF;
  }
    
    
  .avatar-all {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    overflow: hidden;
  }
  /* 用户信息 end */
  
  
  /* 按钮 */
  .button-1 {
    background-color: rgba(0, 0, 0, 0.15);
    position: fixed;
    /* bottom:200rpx;
      right: 20rpx; */
    top: 18%;
    right: 30rpx;
    z-index: 1001;
    border-radius: 100px;
  }
  .button-2 {
    background-color: rgba(0, 0, 0, 0.15);
    position: fixed;
    /* bottom:200rpx;
      right: 20rpx; */
    top: 26%;
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
        width: 70rpx;
        height: 70rpx;
        font-size: 46rpx;
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
  
  /* 顶部*/
  .game-fixed{
    position: fixed;
    top: 0;
    width: 100%;
    height: 620rpx;
    background-size: cover;
    overflow: hidden;
    background-position: center;
    z-index: 0;
    min-height:620rpx;
  }
  
  .banner{
    position: fixed;
    top: 0;
    width: 100%;
    transition: all 0.25s ease-out;
    will-change: transform;
    z-index: 0;
  }
  
  /* 轮播视觉差 start */
  .card-swiper {
    height: 620rpx !important;
  }
    
  .card-swiper swiper-item {
    
    width: 750rpx !important;
    left: 0rpx;
    box-sizing: border-box;
    // padding: 0rpx 30rpx 90rpx 30rpx;
    overflow: initial;
  }
    
  .card-swiper swiper-item .swiper-item {
    width: 100%;
    display: block;
    height: 100%;
    transform: scale(1);
    transition: all 0.4s ease-in 0s;
    will-change: transform;
    overflow: hidden;
    filter: blur(120rpx);
    opacity: 0.4;
  }
    
  .card-swiper swiper-item.cur .swiper-item {
    transform: none;
    transition: all 0.4s ease-in 0s;
    will-change: transform;
    filter: blur(0rpx);
    opacity: 1;
  }
    
  .card-swiper swiper-item .swiper-item-text {
    background: linear-gradient(0deg, rgba(0,0,0,0.6), rgba(0,0,0,0));
    margin-top: -200rpx;
    width: 100%;
    padding: 40rpx;
    display: block;
    height: 50%;
    border-radius: 10rpx;
    // transform: translate(100rpx, -60rpx) scale(1);
    transition: all 0.6s ease 0s;
    will-change: transform;
    overflow: hidden;
  }
    
  .card-swiper swiper-item.cur .swiper-item-text {
    background: linear-gradient(0deg, rgba(0,0,0,0.6), rgba(0,0,0,0));
    margin-top: -200rpx;
    width: 100%;
    // transform: translate(0rpx, 0rpx) scale(1);
    transition: all 0.6s ease 0s;
    will-change: transform;
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
    z-index: 9999;
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
    top: -100rpx;
    margin: 0 8rpx !important;
    position: relative;
  }
  
  .spot.active{
    opacity: 1;
    width: 30rpx;
    background-color: #FFFFFF;
  }
  
  /* 背景底色*/
  .fire-bg{
    // position: fixed;
    // background-color: rgba(255,255,255,1);
    // box-shadow: 0rpx 0rpx 30rpx 0rpx rgba(0, 0, 0, 0.07);
    // background: linear-gradient(90deg, #B7D4F4, #99E3FE, #C2F3DC, #DBF2FE);
    // background: linear-gradient(90deg, #F1CDD1, #FDF4E3, #F6EDF9, #E7E5F5);
    // background: linear-gradient(90deg, #e643f150, #fdfa8650, #af98f950, #B7D4F450);
    background: linear-gradient(120deg, #FEFDD3, #FAFCEF, #E5FDF5, #FEFBEA);
    width: 100%;
    // transition: all 0.25s ease-out;
    // z-index: -1;
  }
  .fire-bg:before{
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    z-index: -1;
    mask-image: linear-gradient(to bottom, transparent, black);
    // background: linear-gradient(90deg, #E7E5F5, #F1CDD1, #F6EDFB, #F7EDF5);	
    // background: linear-gradient(90deg, #88c2f480, #e4adf1E6, #75eafb80, #f7bbf180);
    background: linear-gradient(0deg, #FCFDCD, #FCFDCD, #000000);
  }
  .fire-bg:after{
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    z-index: -1;
    mask-image: linear-gradient(to bottom, transparent, black);
    background: linear-gradient(90deg, #FDF5F2, #FBE8E7);	
  }
  
  .tn-icon-first{
    color: #FEC52D;
    font-size: 60rpx;
  }
  
  .tn-icon-second{
    color: #74AEE5;
    font-size: 60rpx;
  }
  
  .tn-icon-third{
    color: #F6B176;
    font-size: 60rpx;
  }
  
  .game-bg-first{
    border:5rpx solid #FEC52D;
  }
  
  .game-bg-second{
    border:5rpx solid #74AEE5;
  }
  
  .game-bg-third{
    border:5rpx solid #F6B176;
  }
  
  .game-bg-n{
    border:5rpx solid #F6F6F6;
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
  	min-height: 140rpx;
  	height: calc(160rpx + env(safe-area-inset-bottom) / 2);
    height: calc(160rpx + constant(safe-area-inset-bottom));
  }
  
</style>
