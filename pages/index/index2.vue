<template>
  <view class="index" >

    <!-- 方格背景-->
    <!-- <view class="animation-wrapper">
    </view> -->
    
    <!-- 整体移动背景 -->
    <!-- <view class="bg-contaniner">
		  
		</view> -->
    
    <!-- 二级页面 -->
    <view v-if="tabberPageLoadFlag[0]" :style="{display: currentTabbarIndex === 0 ? '' : 'none'}">
      <scroll-view class="custom-tabbar-page" scroll-y enable-back-to-top @scrolltolower="tabbarPageScrollLower">
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
    <!-- <view class="bg-tabbar-shadow"></view> -->
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

  export default {
    components: {
      PageA,
      PageB,
      PageC,
      PageD
    },
    data() {
      return {
        activeBgPositionLeft: 0,
        prevTabbarIndex: 0,
        currentTabbarIndex: 0,
        tabbarRectInfo: [],
        activeBgAnimation: {},
        tabbar: [{
            name: '首 页',
            icon: 'tn-icon-menu-set',
            image: '/static/tabbar/tn-tabbar0.png'
          },
          {
            name: '商 品',
            icon: 'tn-icon-like-lack',
            image: '/static/tabbar/tn-tabbar1.png'
          },
          {
            name: '金 价',
            icon: 'tn-icon-data',
            image: '/static/tabbar/tn-tabbar2.png'
          },
          {
            name: '我 的',
            icon: 'tn-icon-my-simple',
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
      const index = Number(options.index || 0)
      // 根据底部tabbar菜单列表设置对应页面的加载情况
      this.tabberPageLoadFlag = this.tabbar.map((item, tabbar_index) => {
        return index === tabbar_index
      })
      this.changeTabbar(index)
    },
    onReady() {
      this.$nextTick(() => {
        this.getTabbarItemInfo()
      })
    },
    methods: {
      // 导航页面滚动到底部
      tabbarPageScrollLower(e) {
        // if (this.currentTabbarIndex === 2) {
        //   this.$refs.pageC.getRandomData && this.$refs.pageC.getRandomData()
        // }
      },

      // 切换导航页面
      _switchTabbarPage(index) {
        // tabbar短振动切换，不喜欢删掉下方一行
        wx.vibrateShort();
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
        for (let i = 0; i < this.tabbar.length; i++) {
          view.select('#tabbar_item_' + i).boundingClientRect()
        }
        view.exec(res => {
          if (!res.length) {
            setTimeout(() => {
              this.getTabbarItemInfo()
            }, 10)
            return
          }

          // 将信息存入数组中
          res.map((item) => {
            this.tabbarRectInfo.push({
              left: item.left,
              width: item.width
            })
          })
          this.updateActiveBgPosition(true)
          // console.log(this.tabbarRectInfo)
        })
      },
      // 更新激活时背景的位置
      updateActiveBgPosition(init = false) {
        const {
          width,
          left
        } = this.tabbarRectInfo[this.currentTabbarIndex]
        const oldActiveBgPositionLeft = this.activeBgPositionLeft
        this.activeBgPositionLeft = left + ((width - uni.upx2px(250)) / 2)
        if (!init) {
          const animation = uni.createAnimation({
            duration: 160,
            timingFunction: "linear"
          })
          animation.top(uni.upx2px(17)).left(oldActiveBgPositionLeft + ((this.activeBgPositionLeft -
            oldActiveBgPositionLeft) / 2)).scale(1).step()
          animation.left(this.activeBgPositionLeft).top(uni.upx2px(17)).scale(1).step()
          this.activeBgAnimation = animation.export()
        } else {
          const animation = uni.createAnimation({
            duration: 160,
            timingFunction: "linear"
          })
          animation.left(this.activeBgPositionLeft).top(uni.upx2px(17)).step()
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
        })
      },


    }
  }
</script>

<style lang="scss" scoped>
  /* 小点点背景*/
  .animation-wrapper {
    position: fixed;
    bottom: 0;
    height: 100vh;
    width: 100vw;
    // background: #16171D;
    // background: linear-gradient(90deg, #171717, #16171D, #171717);
    // background-blend-mode: screen;
    // background-size: cover;
    // -webkit-filter: grayscale(100%);
    //         filter: grayscale(100%);
    z-index: 0;


  }

  .animation-wrapper:before {
    content: "";
    position: absolute;
    top: -100vh;
    left: -94.5vw;
    bottom: 0;
    right: 0;
    z-index: -1;
    height: 300vh;
    width: 300vw;
    /* mask-image: linear-gradient(to bottom, transparent, black);
    background: linear-gradient(90deg, #16171D, #16171D);	 */
    --lc1: rgba(255, 255, 255, .1);
    --lc2: rgba(255, 255, 255, .1);
    --tc: rgba(0, 50, 100, 0);
    --bc: rgb(22, 23, 29);
    --bd: rgb(23, 23, 23);
    --gap: 120rpx;
    background-color: var(--bc);
    background-image: radial-gradient(var(--tc), var(--bd)), repeating-linear-gradient(0deg, var(--tc), var(--tc) var(--gap), var(--lc1) calc(var(--gap) + 1px), var(--tc) calc(var(--gap) + 1px), var(--tc) calc(var(--gap) * 2 + 1px), var(--lc1) calc(var(--gap) * 2 + 2px), var(--tc) calc(var(--gap) * 2 + 2px), var(--tc) calc(var(--gap) * 3 + 2px), var(--lc2) calc(var(--gap) * 3 + 3px)), repeating-linear-gradient(90deg, var(--tc), var(--tc) var(--gap), var(--lc1) calc(var(--gap) + 1px), var(--tc) calc(var(--gap) + 1px), var(--tc) calc(var(--gap) * 2 + 1px), var(--lc1) calc(var(--gap) * 2 + 2px), var(--tc) calc(var(--gap) * 2 + 2px), var(--tc) calc(var(--gap) * 3 + 2px), var(--lc2) calc(var(--gap) * 3 + 3px));
    transform: rotateX(15deg) rotateY(0) rotateZ(45deg);

  }


  /*整体样式 */
  .index {
    width: 100%;
    height: 100vh;
    position: relative;

    .custom-tabbar-page {
      // width: 100%;
      // height: calc(100vh - 10rpx);
      // box-sizing: border-box;
      // padding-bottom: 30rpx;
      // padding-bottom: calc(30rpx + constant(safe-area-inset-bottom));
      // padding-bottom: calc(30rpx + env(safe-area-inset-bottom));
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
        z-index: -1;
        top: calc(16rpx);
        left: calc(36rpx);
        transition: box-shadow .2s ease-out;
      }
    }

    /* 底部导航 end */
    
    /* 改变颜色*/
    .anim-hue {
      animation: hue 20s linear infinite;
      filter: hue-rotate(0deg);
    }
    
    @keyframes hue {
      100% {
        filter: hue-rotate(360deg);
      }
    }


    /* 移动背景部分 start*/
    .bg-contaniner {
      position: fixed;
      top: -0rpx;
      left: -300rpx;
      --text-color: hsl(0 95% 60%);
      --bg-color: #F6F6F6;
      --bg-size: 200px;
      height: 100%;
      display: grid;
      place-items: center;
      place-content: center;
      overflow: hidden;
      background-color: var(--bg-color);
      z-index: -1;
    }

    .bg-contaniner::before {
      --size: 150vmax;
      content: "";
      inline-size: var(--size);
      block-size: var(--size);
      background-image: url("https://cdn.nlark.com/yuque/0/2022/png/280373/1663568736856-assets/web-upload/97cab67d-6f17-4c6e-a3f9-ac81961d571a.png");
      background-size: var(--bg-size);
      background-repeat: repeat;
      transform: rotate(45deg);
      opacity: 0.1;
      animation: bg 6s linear infinite;
    }

    @media (prefers-reduced-motion: reduce) {
      .bg-contaniner::before {
        animation-duration: 0s;
      }
    }

    @keyframes bg {
      to {
        background-position: 0 calc(var(--bg-size) * -1);
      }
    }

    /* 移动背景部分 end*/
  }
</style>