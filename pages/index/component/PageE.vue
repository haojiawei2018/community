<template>
  <view class="pagesE tn-safe-area-inset-bottom">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F600">
      <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left">
        <!-- <view class="custom-nav__logo" @click="tn('')">
          <view class="tn-icon-tree leaf-color"></view>
        </view> -->
        <view class="">
          <text class="tn-margin-left tn-text-bold tn-text-xl leaf-color">我 的</text>
        </view>
      </view>
    </tn-nav-bar>
    
    <!-- 顶部渐变底色，需要的显示出来即可 -->
    <view class="mine-fixed">
    </view>
    
    <view class="" :style="{paddingTop: vuex_custom_bar_height + 'px'}" style="z-index: 1 !important;position: relative;">
      <!-- 图标logo/头像 -->
      <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-margin">
        <view class="justify-content-item" @click="handleUserClick">
          <view class="tn-flex tn-flex-col-center tn-flex-row-left">
            <view class="logo-pic">
              <view class="logo-image" :style="avatarStyle">
              </view>
            </view>
            <view class="tn-padding-right">
              <view class="tn-padding-right tn-padding-left-sm">
                <text class="tn-color-black tn-text-xl tn-text-bold">{{ userInfo.nickname || userInfo.username || '点击登录' }}</text>
              </view>
              <view class="tn-padding-right tn-padding-top-sm tn-padding-left-sm tn-text-ellipsis" v-if="isLoggedIn">
                <text class="tn-color-gray--dark">Uid: {{ userInfo.uid || userInfo.id || '--' }}</text>
                <text class="tn-color-gray--dark tn-padding-left-xs tn-icon-qr-code"></text>
              </view>
              <view class="tn-padding-right tn-padding-top-sm tn-padding-left-sm tn-text-ellipsis" v-else>
                <text class="tn-color-gray--dark">登录后体验更多功能</text>
              </view>
            </view>

          </view>
        </view>
        <view class="justify-content-item">
          <view class="tn-icon-install tn-color-gray--dark" style="font-size: 50rpx;opacity: 0.5;">
          </view>
        </view>
      </view>
      
      <!-- 没有授权，则显示这个授权按钮-->
      <!-- <view class="tn-flex tn-flex-row-between" @click="tn('/minePages/login')">
        <view class="tn-flex-1 justify-content-item tn-margin-xs tn-text-center">
          <tn-button shape="round" backgroundColor="#000000" fontColor="#ffffff" padding="36rpx 0" width="40%">
            <text class="tn-icon-wechat tn-padding-right-xs tn-text-xl"></text>
            <text class="">授权登录</text>
          </tn-button>
        </view>
      </view> -->

      
      <!-- 更多信息-->
      <!-- 方式12 start-->
      <view class="">
        
        <view class="tn-flex tn-flex-row-between" style="padding: 30rpx 60rpx 10rpx 60rpx;">
          <view class="tn-padding-sm tn-radius" @click="tn('../xxx/xxx')">
            <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
              <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center" style="background-color: #000000;">
                <view class="tn-icon-star"></view>
              </view>
              <view class="tn-text-center">
                <text class="tn-text-ellipsis">收 藏</text>
              </view>
            </view>
          </view>
          <view class="tn-padding-sm tn-radius" @click="tn('../sign/sign')">
            <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
              <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center" style="background-color: #000000;">
                <view class="tn-icon-calendar"></view>
              </view>
              <view class="tn-text-center">
                <text class="tn-text-ellipsis">打 卡</text>
              </view>
            </view>
          </view>
          <view class="tn-padding-sm tn-radius" @click="goPublish">
            <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
              <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center" style="background-color: #000000;">
                <view class="tn-icon-edit-form"></view>
              </view>
              <view class="tn-text-center">
                <text class="tn-text-ellipsis">发 布</text>
              </view>
            </view>
          </view>
          <view class="tn-padding-sm tn-radius" @click="tn('../xxx/xxx')">
            <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center">
              <view class="icon13__item--icon tn-flex tn-flex-row-center tn-flex-col-center" style="background-color: #000000;">
                <view class="tn-icon-circle-lack"></view>
              </view>
              <view class="tn-text-center">
                <text class="tn-text-ellipsis">圈 子</text>
              </view>
            </view>
          </view>
          
        </view>
      </view>
      
      <view class="tn-bg-white" style="border-radius: 18rpx;margin: 20rpx 30rpx 20rpx 30rpx;">
        <view class="tn-flex tn-flex-row-between tn-padding-top-sm tn-padding-left tn-padding-right" @click="tn('/minePages/xxx')">
          <view class="justify-content-item tn-text-lg tn-text-bold tn-margin-top-sm">
            应用服务
          </view>
          <view class="justify-content-item tn-text-sm tn-color-gray tn-margin-top-sm">
            <text class="tn-padding-xs">全部</text>
            <text class="tn-icon-right"></text>
          </view>
        </view>
        <view class="tn-flex tn-flex-wrap tn-padding-top-sm">
          <view v-for="(item, index) in linksData" :key="index" style="width: 25%;">
            <view class="tn-margin-bottom tn-margin-top-sm"  @click="tn('/minePages/xxx')">
              <view class="tn-flex tn-flex-direction-column tn-flex-row-center tn-flex-col-center" style="color: #484848;">
                <!-- 预留的图片形式 -->
                <view class="icon12__item--icon tn-flex tn-flex-row-center tn-flex-col-center" :style="'background-image:url('+ item.url +');background-size:100% 100%;background-size: cover;'">
                </view>
                <!-- 字体图标形式-->
                <!-- <view class="icon12__item--icon tn-flex tn-flex-row-center tn-flex-col-center" :class="['tn-cool-bg-color-' + item.color]">
                  <view class="tn-icon-my-simple-fill"></view>
                </view> --> 
                <view class="tn-text-center tn-margin-top-xs">
                  <text class="tn-text-ellipsis tn-text-sm">{{ item.title }}</text>
                </view>
              </view>
            </view>
         </view>
        </view>
        <!-- 方式12 end-->
      
      </view>

      <!-- 更多信息-->
      <view class="tn-padding-top-sm tn-padding-bottom-sm tn-bg-white" style="border-radius: 18rpx;margin: 20rpx 30rpx 20rpx 30rpx;">
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx" @click="copySource">
          <view class="tn-flex tn-flex-col-center">
            <view
              class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center">
              <view class="tn-icon-discover-fill"></view>
            </view>
            <view class="tn-margin-left-sm tn-flex-1 tn-text-lg">开源地址</view>
            <view class="tn-color-gray tn-icon-right"></view>
          </view>
        </tn-list-cell>
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx" @click="tn('/minePages/public')">
          <view class="tn-flex tn-flex-col-center">
            <view
              class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center">
              <view class="tn-icon-bookmark-fill"></view>
            </view>
            <view class="tn-margin-left-sm tn-flex-1 tn-text-lg">关注我们</view>
            <view class="tn-color-gray tn-icon-right"></view>
          </view>
        </tn-list-cell>
      </view>
      
      
      <view class="tn-padding-top-sm tn-padding-bottom-sm tn-bg-white" style="border-radius: 18rpx;margin: 20rpx 30rpx 20rpx 30rpx;">
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx">
          <button class="tn-flex tn-flex-col-center tn-button--clear-style" open-type="contact">
            <view
              class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center">
              <view class="tn-icon-service-fill"></view>
            </view>
            <view class="tn-flex tn-flex-row-between" style="width: 100%;">
              <view class="tn-margin-left-sm tn-text-lg">在线客服</view>
              <view class="tn-color-gray tn-icon-right"></view>
            </view>
          </button>
        </tn-list-cell>
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx">
          <button class="tn-flex tn-flex-col-center tn-button--clear-style" open-type="feedback">
            <view
              class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center">
              <view class="tn-icon-tip-fill"></view>
            </view>
            <view class="tn-flex tn-flex-row-between" style="width: 100%;">
              <view class="tn-margin-left-sm tn-text-lg">问题反馈</view>
              <view class="tn-color-gray tn-icon-right"></view>
            </view>
          </button>
        </tn-list-cell>
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx" @click="callPhoneNumber" data-number="18266666666">
          <view class="tn-flex tn-flex-col-center">
            <view
              class="icon1__item--icon tn-flex tn-flex-row-center tn-flex-col-center">
              <view class="tn-icon-tel-circle-fill"></view>
            </view>
            <view class="tn-margin-left-sm tn-flex-1 tn-text-lg">技术热线</view>
            <view
              class="tn-margin-left-sm tn-padding-left-xs tn-padding-right-xs tn-bg-gray--light tn-text-sm tn-round" style="color: #000000;">
              188****8888</view>
          </view>
        </tn-list-cell>
      </view>

      <!-- 退出登录（仅登录状态显示） -->
      <view v-if="isLoggedIn" class="tn-padding-top-sm tn-padding-bottom-sm tn-bg-white" style="border-radius: 18rpx;margin: 20rpx 30rpx 20rpx 30rpx;">
        <tn-list-cell :hover="true" :unlined="true" :radius="true" :fontSize="30" padding="26rpx 30rpx" @click="handleLogout">
          <view class="tn-flex tn-flex-col-center tn-flex-row-center">
            <view class="tn-icon-logout tn-text-lg"></view>
            <view class="tn-margin-left-sm tn-text-lg" style="color: #E53935;">退出登录</view>
          </view>
        </tn-list-cell>
      </view>
      
      
      <view class="tn-text-center tn-margin-top-xl tn-padding-top tn-padding-bottom-xl">
      	<view @click="navTuniaoUI">
          <text class="tn-padding-xs" style="color: #000000;">图鸟科技</text>
          <text class="tn-color-gray">提供技术支持</text>
        </view>
      </view>

    </view>

    
    <tn-modal v-model="show1" :custom="true">
      <view class="custom-modal-content">
        <image @tap="previewQRCodeImage" src='https://resource.tuniaokj.com/images/advertise/qrcode.jpg' mode='aspectFill' style="width: 100%;"></image>
        <view class="tn-text-center tn-padding-top" @click="copyWechat">
          <text class="">我的好友码：10262008 </text>
          <text class="tn-color-blue--disabled tn-padding-left-xs tn-text-df tn-icon-copy"></text>
          </view>
        <!-- <view class="tn-text-center tn-padding-top tn-text-lg">点击上图，可识别微信二维码</view> -->
        
        <!-- 悬浮按钮-->
        <view class="tn-flex tn-padding">
          <view class="tn-flex-1 justify-content-item tn-text-center">
            <tn-button backgroundColor="#000000 " padding="40rpx 0" width="100%" :fontSize="28" fontColor="#FFFFFF" shape="round" @click="tn('')">
              <text class="">保存到相册</text>
            </tn-button>
          </view>
        </view>
        
      </view>
    </tn-modal>

    <view class="tn-tabbar-height"></view>

  </view>
</template>

<script>
  import { user } from '@/api/index.js'

  // 日志前缀，便于在控制台过滤
  const LOG_TAG = '[PageE]'

  export default {
    name: 'PageE',
    data() {
      return {
        show1: false,
        // 用户信息（从本地存储读取）
        userInfo: {},
        // 是否已登录
        isLoggedIn: false,

        linksData: [{
            url: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1647138042384-assets/web-upload/d3907e69-e568-449f-8ac0-a44e7682b574.jpeg',
            title: '预算沟通',
            icon:'platform-fill',
            color:'1',
            path: ''
          },
          {
            url: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1647138042421-assets/web-upload/17a3bee9-461b-467b-9ccc-2e1e39ab44a1.jpeg',
            title: '品牌荣誉',
            icon:'platform-fill',
            color:'2',
            path: ''
          },
          {
            url: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1647138042440-assets/web-upload/91b7ad5a-aaec-4936-bccd-cf4ad5c5df54.jpeg',
            title: '上线模板',
            icon:'platform-fill',
            color:'3',
            path: ''
          },
          {
            url: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1647138042374-assets/web-upload/9e334ff4-a876-4684-8f61-f1d79ecd7b7c.jpeg',
            title: '开发项目',
            icon:'platform-fill',
            color:'4',
            path: ''
          },
          {
            url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711183180435-assets/web-upload/a091b9f2-c587-412d-96c4-adbedbb0a889.jpeg',
            title: '联系作者',
            icon:'platform-fill',
            color:'5',
            path: ''
          },
          {
            url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711182957902-assets/web-upload/c9ac3d07-81cb-40a6-9944-31a8e5f0386b.jpeg',
            title: '文件资源',
            icon:'platform-fill',
            color:'6',
            path: ''
          },
          {
            url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711186780372-assets/web-upload/a7f072c1-5161-42ed-b707-0baa473936e5.jpeg',
            title: '图鸟商城',
            icon:'platform-fill',
            color:'7',
            path: ''
          },
          {
            url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711184777099-assets/web-upload/861176d1-4245-415e-8869-3246293a3344.jpeg',
            title: '发现更多',
            icon:'platform-fill',
            color:'8',
            path: ''
          }
        ],
      }
    },
    computed: {
      // 头像样式：已登录用用户头像，无图时用兜底（按id取模稳定展示）
      avatarStyle() {
        const fallbackAvatars = [
          'https://resource.tuniaokj.com/images/flower/guye1.jpg',
          'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1668321603396-assets/web-upload/a7f7dd1d-3618-4888-a20c-6b55a6aa69a4.jpeg',
          'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1711183180435-assets/web-upload/a091b9f2-c587-412d-96c4-adbedbb0a889.jpeg'
        ]
        const avatar = this.userInfo.avatar || this.userInfo.userImage
        const id = Number(this.userInfo.id || this.userInfo.uid || 0) || 0
        const url = avatar || fallbackAvatars[id % fallbackAvatars.length]
        return `background-image:url('${url}');width: 110rpx;height: 110rpx;background-size: cover;overflow: hidden;`
      }
    },
    // 子组件创建时立即加载用户信息（子组件不触发 onShow）
    created() {
      console.log(LOG_TAG, '组件 created，立即加载用户信息')
      this.loadUserInfo()
    },
    // 组件挂载后再刷一次（应对 created 时 storage 还未写好的边界情况）
    mounted() {
      console.log(LOG_TAG, '组件 mounted，再次确认用户信息')
      this.$nextTick(() => this.loadUserInfo())
    },
    methods: {
      // 父组件切换到我的tab时主动调用刷新
      refresh() {
        this.loadUserInfo()
      },
      // 加载本地存储的用户信息，并做字段归一化
      loadUserInfo() {
        const token = uni.getStorageSync('token')
        const rawInfo = uni.getStorageSync('userInfo') || {}
        this.isLoggedIn = !!token
        // 字段归一化：后端 username / email 兼容前端 nickname / uid
        const normalized = Object.assign({}, rawInfo)
        if (!normalized.nickname && normalized.username) normalized.nickname = normalized.username
        if (!normalized.uid && normalized.id) normalized.uid = normalized.id
        this.userInfo = normalized
        console.log(LOG_TAG, '加载用户信息, isLoggedIn:', this.isLoggedIn, 'userInfo:', this.userInfo)
      },

      // 点击用户信息区域：未登录跳登录页，已登录刷新用户信息
      handleUserClick() {
        if (this.isLoggedIn) {
          console.log(LOG_TAG, '已登录，可扩展为编辑资料页')
          // 预留：跳转编辑资料页
        } else {
          console.log(LOG_TAG, '未登录，跳转登录页')
          uni.navigateTo({ url: '/pages/login/login' })
        }
      },

      // 退出登录
      handleLogout() {
        uni.showModal({
          title: '提示',
          content: '确定要退出登录吗？',
          success: (res) => {
            if (res.confirm) {
              console.log(LOG_TAG, '用户确认退出登录')
              // 清除本地存储的登录信息
              uni.removeStorageSync('token')
              uni.removeStorageSync('userInfo')
              this.isLoggedIn = false
              this.userInfo = {}
              uni.showToast({ title: '已退出登录', icon: 'success' })
              console.log(LOG_TAG, '已清除本地 token 和 userInfo')
            }
          }
        })
      },

      // 跳转发布帖子页
      goPublish() {
        if (!this.isLoggedIn) {
          uni.showToast({ title: '请先登录', icon: 'none' })
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/login/login' })
          }, 1000)
          return
        }
        console.log(LOG_TAG, '跳转发布帖子页')
        uni.navigateTo({ url: '/pages/post/publish' })
      },

      // 跳转
      tn(e) {
        uni.navigateTo({
          url: e,
        });
      },
      
      // 跳转到图鸟UI
      navTuniaoUI() {
        uni.navigateToMiniProgram({
          appId: 'wxf3d81a452b88ff4b'
        })
      },
      
      //拨打固定电话
      callPhoneNumber() {
        uni.makePhoneCall({
          phoneNumber: "18219128888",
        });
      },

      
      // 复制id
      copyWechat() {
        if (typeof wx !== 'undefined' && wx.vibrateShort) {
          wx.vibrateShort();
        }
        uni.setClipboardData({
          data: "10262008",
        })
      },
      
      // 预览作者图片
      previewQRCodeImage() {
        wx.previewImage({
          urls: ['https://resource.tuniaokj.com/images/advertise/qrcode.jpg']
        })
      },
      
      // 弹出模态框1
      showModal(event) {
        this.openModal()
      },
      // 打开模态框
      openModal() {
        this.show1 = true
      },
      
      // 复制开源地址
      copySource() {
        if (typeof wx !== 'undefined' && wx.vibrateShort) {
          wx.vibrateShort();
        }
        uni.setClipboardData({
          data: "https://ext.dcloud.net.cn/publisher?id=356088",
        })
      },
    }
  }
</script>

<style lang="scss" scoped>
  .pagesE{
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

  .mine-fixed{
    position: fixed;
    // background: linear-gradient(90deg, #d8feda, #F6F6F6);
    background: linear-gradient(90deg, #c9febf, #F6F6F6);
    top: 0;
    width: 100%;
    height: 450rpx;
    transition: all 0.25s ease-out;
    z-index: 0;
  }
  .mine-fixed:before{
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    z-index: 0;
    mask-image: linear-gradient(to bottom, transparent, black);
    background: linear-gradient(90deg, #F6F6F6, #F6F6F6);	

  }

  /* 用户头像 start */
  .logo-image {
    width: 110rpx;
    height: 110rpx;
    position: relative;
    overflow: hidden;
    border-radius: 50%;
  }

  .logo-pic {
    background-size: cover;
    background-repeat: no-repeat;
    // background-attachment:fixed;
    background-position: top;
    border: 8rpx solid rgba(255,255,255,0.6);
    box-shadow: 0rpx 0rpx 50rpx 0rpx rgba(0, 0, 0, 0.1);
    border-radius: 50%;
    overflow: hidden;
    // background-color: #FFFFFF;
  }

  /* 图标容器1 start */
  .icon1 {
    &__item {
      // width: 30%;
      background-color: #FFFFFF;
      border-radius: 10rpx;
      padding: 30rpx;
      margin: 20rpx 10rpx;
      transform: scale(1);
      transition: transform 0.3s linear;
      transform-origin: center center;
  
      &--icon {
        width: 50rpx;
        height: 50rpx;
        font-size: 40rpx;
        border-radius: 50%;
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
          // background-image: url(https://resource.tuniaokj.com/images/cool_bg_image/icon_bg.png);
        }
      }
    }
  }
  
  /* 图标容器1 end */
  
  .icon12 {
    &__item {
      width: 30%;
      background-color: #FFFFFF;
      padding: 30rpx;
      margin: 20rpx 10rpx;
      transform: scale(1);
      transition: transform 0.3s linear;
      transform-origin: center center;
      
      &--icon {
        width: 80rpx;
        height: 80rpx;
        font-size: 50rpx;
        // margin-bottom: 18rpx;
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
          // background-image: url(https://resource.tuniaokj.com/images/cool_bg_image/icon_bg6.png);
        }
      }
    }
  }
  
  /* 图标容器13 start */
  .icon13 {
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
        width: 15rpx;
        height: 15rpx;
        font-size: 50rpx;
        border-radius: 50%;
        margin-bottom: 28rpx;
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
  
  /* 底部安全边距 start*/
  .tn-tabbar-height {
  	min-height: 120rpx;
  	height: calc(140rpx + env(safe-area-inset-bottom) / 2);
    height: calc(140rpx + constant(safe-area-inset-bottom));
  }
  
</style>
