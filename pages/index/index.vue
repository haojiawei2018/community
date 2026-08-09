<template>
  <view class="index" style="background-color: #F6F6F6;">

    <!-- 二级页面 -->
    <view v-if="tabberPageLoadFlag[0]" :style="{display: currentTabbarIndex === 0 ? '' : 'none'}">
      <!-- 首页固定导航栏 + 切换栏 -->
      <tn-nav-bar :isBack="false" :bottomShadow="false" backgroundColor="#F6F6F6">
        <view class="custom-nav tn-flex tn-flex-col-center tn-flex-row-left" @tap.stop="openUserModal">
          <view class="custom-nav__logo" @click="tn('')">
            <view class="game-pic" style="background-image:url('https://cdn.nlark.com/yuque/0/2024/png/280373/1717814074962-assets/web-upload/b68e6b5b-a8a2-4821-af06-5beda531901d.png')">
              <view class="game-image">
              </view>
            </view>
          </view>
          <view class="">
            <text class="tn-icon-down-triangle tn-color-gray--dark"></text>
          </view>
        </view>
      </tn-nav-bar>
      <view class="tabs-fixed" :style="{top: vuex_custom_bar_height + 'px'}">
        <view class="" style="background-color: #F6F6F6;padding: 10rpx 70rpx;">
          <tn-tabs-cool :list="fixedList" :current="current" :isScroll="false" activeColor="#000000" :barStyle="barStyle"
            inactiveColor="#000000" :bold="true" :fontSize="32" :badgeOffset="[20, 50]" @change="tabChange"
            backgroundColor="#F6F6F6" :height="70"></tn-tabs-cool>
        </view>
      </view>
      <scroll-view class="custom-tabbar-page home-scroll" :style="{ paddingTop: 'calc(' + vuex_custom_bar_height + 'px + 90rpx)' }" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <page-a ref="pageA"></page-a>
      </scroll-view>
    </view>
    <view v-if="tabberPageLoadFlag[1]" :style="{display: currentTabbarIndex === 1 ? '' : 'none'}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <page-b ref="pageB"></page-b>
      </scroll-view>
    </view>
    <view v-if="tabberPageLoadFlag[2]" :style="{display: currentTabbarIndex === 2 ? '' : 'none'}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <page-c ref="pageC"></page-c>
      </scroll-view>
    </view>
    <view v-if="tabberPageLoadFlag[3]" :style="{display: currentTabbarIndex === 3 ? '' : 'none'}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <page-d ref="pageD"></page-d>
      </scroll-view>
    </view>
    <view v-if="tabberPageLoadFlag[4]" :style="{display: currentTabbarIndex === 4 ? '' : 'none'}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
        <page-e ref="pageE"></page-e>
      </scroll-view>
    </view>
    <!-- 底部导航栏 -->
    <view class="tabbar dd-glass">
      <view class="tabbar__list">
        <block v-for="(item, index) in tabbar" :key="index">
          <view :id="`tabbar_item_${index}`" class="tabbar__item"
            :class="[{'tabbar__item--active': index === currentTabbarIndex}]" @click="changeTabbar(index)">
            <!-- 用字体图标的方式 -->
            <view class="tabbar__item__icon" :class="[item.icon]"></view>
            <!-- 底部文字，需要自行显示出来，显示出来后适当调整下位置-->
            <!-- <view class="tabbar__item__text">{{ item.name }}</view> -->
          </view>
        </block>
      </view>
      <view class="tabbar__select-active-bg" :animation="activeBgAnimation"></view>
    </view>
  </view>
</template>

<script>
  import PageA from './component/PageA.vue'
  import PageB from './component/PageB.vue'
  import PageC from './component/PageC.vue'
  import PageD from './component/PageD.vue'
  import PageE from './component/PageE.vue'

  export default {
    components: {
      PageA,
      PageB,
      PageC,
      PageD,
      PageE
    },
    data() {
      return {
        activeBgPositionLeft: 0,
        prevTabbarIndex: 0,
        currentTabbarIndex: 0,
        tabbarRectInfo: [],
        activeBgAnimation: {},
        // 首页顶部切换栏
        current: 0,
        barStyle: {
          color: '#000000'
        },
        fixedList: [
          {name: '推荐'},
          {name: '热门'},
          {name: '独家'},
          {name: '热卖'}
        ],
        tabbar: [{
            name: '首 页',
            icon: 'tn-icon-home-fill',
            image: '/static/tabbar/tn-tabbar0.png'
          },
          {
            name: '发 现',
            icon: 'tn-icon-discover-fill',
            image: '/static/tabbar/tn-tabbar1.png'
          },
          {
            name: '排行榜',
            icon: 'tn-icon-mansion',
            image: '/static/tabbar/tn-tabbar2.png'
          },
          {
            name: '消 息',
            icon: 'tn-icon-stack',
            image: '/static/tabbar/tn-tabbar3.png'
          },
          {
            name: '我 的',
            icon: 'tn-icon-my-lack-fill',
            image: '/static/tabbar/tn-tabbar3.png'
          }
        ],

        // 自定义底栏对应页面的加载情况
        tabberPageLoadFlag: []
      }
    },
    computed: {
    },
    onLoad(options) {
      // 支持 tab=xxx 参数，用于登录成功后直接切到"我的"
      const index = Number(options.tab || options.index || 0)
      // 根据底部tabbar菜单列表设置对应页面的加载情况
      this.tabberPageLoadFlag = this.tabbar.map((item, tabbar_index) => {
        return index === tabbar_index
      })
      this.currentTabbarIndex = index
    },
    onReady() {
      this.$nextTick(() => {
        this.getTabbarItemInfo()
        // 如果首次进入就处于我的tab，尝试刷新用户信息（v-if 渲染需要时间，加兜底）
        if (this.currentTabbarIndex === 4) {
          this.$nextTick(() => {
            if (this.$refs.pageE && this.$refs.pageE.refresh) {
              this.$refs.pageE.refresh()
            } else {
              setTimeout(() => {
                if (this.$refs.pageE && this.$refs.pageE.refresh) {
                  this.$refs.pageE.refresh()
                }
              }, 200)
            }
          })
        }
      })
    },
    methods: {
      // 首页顶部切换栏
      tabChange(index) {
        this.current = index
        if (this.$refs.pageA) {
          this.$refs.pageA.current = index
        }
      },
      // 打开用户弹窗
      openUserModal() {
        if (this.$refs.pageA) {
          this.$refs.pageA.openUserModal && this.$refs.pageA.openUserModal()
        }
      },
      // 页面跳转
      tn(e) {
        if (e) {
          uni.navigateTo({ url: e })
        }
      },
      // 导航页面滚动到底部
      tabbarPageScrollLower(e) {
        if (this.currentTabbarIndex === 0) {
          // 触底加载更多帖子
          this.$refs.pageA.fetchPostList && this.$refs.pageA.fetchPostList(false)
        }
      },

      // 切换导航页面
      _switchTabbarPage(index) {
        // tabbar短振动切换，不喜欢删掉下方一行
        // #ifdef MP-WEIXIN
        wx.vibrateShort();
        // #endif
        const selectPageFlag = this.tabberPageLoadFlag[index]
        if (selectPageFlag === undefined) {
          return
        }
        if (selectPageFlag === false) {
          this.tabberPageLoadFlag[index] = true
        }
      },

      // 获取底部元素的位置
      getTabbarItemInfo() {
        const view = uni.createSelectorQuery().in(this)
        // boundingClientRect 返回的 left 是相对视口的，先获取 tabbar
        // 容器的位置，再将每个 item 换算为容器内坐标。
        view.select('.tabbar').boundingClientRect()
        for (let i = 0; i < this.tabbar.length; i++) {
          view.select('#tabbar_item_' + i).boundingClientRect()
        }
        view.exec(res => {
          if (!res || res.length !== this.tabbar.length + 1 || res.some(item => !item)) {
            setTimeout(() => {
              this.getTabbarItemInfo()
            }, 10)
            return
          }

          const tabbarLeft = res[0].left
          // 重新测量时直接替换，避免旧坐标重复累加。
          this.tabbarRectInfo = res.slice(1).map((item) => {
            return {
              left: item.left - tabbarLeft,
              width: item.width
            }
          })
          this.updateActiveBgPosition(true)
        })
      },
      // 更新激活时背景的位置
      updateActiveBgPosition(init = false) {
        if (!this.tabbarRectInfo.length) return
        const currentItem = this.tabbarRectInfo[this.currentTabbarIndex]
        if (!currentItem) return
        const {
          width,
          left
        } = currentItem
        const oldActiveBgPositionLeft = this.activeBgPositionLeft
        this.activeBgPositionLeft = left + ((width - uni.upx2px(90)) / 2)
        if (!init) {
          const animation = uni.createAnimation({
            duration: 160,
            timingFunction: "linear"
          })
          animation.top(uni.upx2px(16)).left(oldActiveBgPositionLeft + ((this.activeBgPositionLeft -
            oldActiveBgPositionLeft) / 2)).scale(1).step()
          animation.left(this.activeBgPositionLeft).top(uni.upx2px(16)).scale(1).step()
          this.activeBgAnimation = animation.export()
        } else {
          const animation = uni.createAnimation({
            duration: 160,
            timingFunction: "linear"
          })
          animation.left(this.activeBgPositionLeft).top(uni.upx2px(16)).step()
          this.activeBgAnimation = animation.export()
        }

      },
      // 修改当前选中的tabbar
      changeTabbar(index) {
        if (this.currentTabbarIndex === index) return
        this._switchTabbarPage(index)
        this.prevTabbarIndex = this.currentTabbarIndex
        this.currentTabbarIndex = index
        this.$nextTick(() => {
          this.updateActiveBgPosition()
          // 切换到"我的"（第4个index=4）时主动刷新用户信息
          // v-if 懒加载渲染需要时间，双 nextTick + setTimeout 兜底
          if (index === 4) {
            this.$nextTick(() => {
              if (this.$refs.pageE && this.$refs.pageE.refresh) {
                this.$refs.pageE.refresh()
              } else {
                setTimeout(() => {
                  if (this.$refs.pageE && this.$refs.pageE.refresh) {
                    this.$refs.pageE.refresh()
                  }
                }, 200)
              }
            })
          }
        })
      },


    }
  }
</script>

<style lang="scss" scoped>

  /*整体样式 */
  .index {
    width: 100%;
    height: 100vh;
    position: relative;

    .custom-tabbar-page {
      width: 100%;
      height: 100vh;
      box-sizing: border-box;
      background-color: #F6F6F6;
    }
    /* 首页scroll-view需要给固定的导航栏和tabs留出空间 */
    .custom-tabbar-page-with-tabs {
      padding-top: calc(100rpx + constant(safe-area-inset-bottom));
      padding-top: calc(100rpx + env(safe-area-inset-bottom));
    }

    /* 首页顶部切换栏固定 */
    .tabs-fixed {
      position: fixed;
      left: 0;
      width: 100%;
      z-index: 997;
      background-color: #F6F6F6;
    }

    /* 自定义导航栏内容 */
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

    .game-image {
      z-index: 9999 !important;
      width: 195rpx;
      height: 50rpx;
      position: relative;
    }

    .game-pic {
      z-index: 9999 !important;
      background-size: cover;
      background-repeat: no-repeat;
      background-position: center;
    }

    /* 毛玻璃*/
    .dd-glass {
       width: 100%;
       backdrop-filter: blur(20rpx);
      -webkit-backdrop-filter: blur(20rpx);
      border-radius: 1000rpx;
    }

    /* 底部导航 statr */
    .tabbar {
      margin-left: 80rpx;
      margin-right: 80rpx;
      width: calc(100vw - 160rpx);
      height: 120rpx;

      position: fixed;
      bottom: 60rpx;
      bottom: calc(60rpx + constant(safe-area-inset-bottom) / 1.8);
      bottom: calc(60rpx + env(safe-area-inset-bottom) / 1.8);
      left: 0;
      right: 0;
      // background-color: transparent;
      background-color: rgba(255,255,255,0.6);
      z-index: 998;
      /* 图鸟温馨提醒，裁剪式也生效的阴影样式，不用box-shadow*/
      filter: drop-shadow(0rpx 0rpx 30rpx rgba(0, 0, 0, 0.04));

      &__list {
        position: absolute;
        z-index: 999;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: space-between;
      }

      &__item {
        height: 100%;
        width: 100%;
        flex: 1;
        text-align: center;
        font-size: 28rpx;
        position: relative;
        margin-top: 20rpx;

        &--active {
          .tabbar__item__icon {
            top: 28rpx;
            color: #FFFFFF;
          }

          .tabbar__item__text {
            opacity: 1;
            color: #FFFFFF;
            font-size: 20rpx;
          }
        }

        &__icon {
          font-size: 52rpx;
          position: absolute;
          left: 0;
          right: 0;
          top: 28rpx;
          transition: 0.5s;
          color: #202020;
        }

        &__text {
          position: absolute;
          left: 0;
          right: 0;
          bottom: 26rpx;
          bottom: calc(26rpx + constant(safe-area-inset-bottom));
          bottom: calc(26rpx + env(safe-area-inset-bottom));
          transition: 0.5s;
          opacity: 0;
        }
      }

      &__select-active-bg {
        position: absolute;
        width: 90rpx;
        height: 90rpx;
        border-radius: 50%;
        background-color: #0F0F0F;
        // transition: 0.5s;
        z-index: 0;
        top: calc(16rpx);
        left: calc(36rpx);
        transition: box-shadow .2s ease-out;
      }
    }

    /* 底部导航 end */
  }
</style>
