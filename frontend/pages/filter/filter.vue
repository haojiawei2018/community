<template>
  <view class="template-filter">
    <!-- 顶部自定义导航 -->
    <tn-nav-bar fixed customBack :bottomShadow="false" backgroundColor="#FFFFFF">
      <view slot="back" class='tn-custom-nav-bar__back'
        @click="goBack">
        <text class='icon tn-icon-left'></text>
      </view>
      <view class="tn-flex tn-flex-col-center tn-flex-row-center ">
        <text class="tn-text-bold tn-text-xl tn-color-black">应用专区</text>
      </view>
    </tn-nav-bar>
    
    <view class="top-fixed" :style="{paddingTop: vuex_custom_bar_height + 10 + 'px'}">
      <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding-left-sm tn-padding-right-sm">
        <view class="tn-flex tn-flex-col-center tn-padding-sm">
          <view class="tn-padding-right-xs">默认</view>
          <!-- <view class="tn-icon-title tn-color-gray--dark"></view> -->
        </view>
        <view class="tn-flex tn-flex-col-center tn-padding-sm" @tap="openTypeModal">
          <view class="tn-padding-right-xs">类型</view>
          <view class="tn-icon-down-triangle icon-active" v-if="showTypeModal"></view>
          <view class="tn-icon-down-triangle tn-color-gray--dark" v-else></view>
        </view>
        <view class="tn-flex tn-flex-col-center tn-padding-sm">
          <view class="tn-padding-right-xs">销量</view>
          <view class="tn-icon-sort tn-color-gray--dark"></view>
        </view>
        <view class="tn-flex tn-flex-col-center tn-padding-sm">
          <view class="tn-padding-right-xs">价格</view>
          <view class="tn-icon-sort tn-color-gray--dark"></view>
        </view>
        <view class="tn-flex tn-flex-col-center tn-padding-sm" @tap="openSelectModal">
          <view class="tn-padding-right-xs">综合</view>
          <view class="tn-icon-level-fill tn-color-gray--dark tn-text-sm"></view>
        </view>
      </view>
    </view>
    
    
    <view class="" :style="{paddingTop: vuex_custom_bar_height + 60 + 'px'}">
      
      <!-- 商品 start-->
      <view class="tn-flex tn-flex-wrap" style="margin: 10rpx 14rpx 0 14rpx;">
        <block v-for="(item, index) in product" :key="index">
          <view class="" style="width: 50%;" @click="tn('/shopPages/product')">
            <view class="shop-shadow" style="margin: 0 16rpx 30rpx 16rpx;">
              <view class="image-pic" :style="'background-image:url(' + item.mainImage + ')'">
                <view class="image-product">
                </view>
              </view> 
              
              <view class="tn-text-justify tn-padding-top-sm tn-padding-bottom-xs clamp-text-1 tn-text-bold" style="color: #333333;max-height: 60rpx;">
                <text class="tn-padding-left-sm tn-padding-right-sm">{{ item.desc }}</text>  
              </view>
              
              <view class="tn-flex tn-flex-row-between tn-flex-col-center tn-padding-bottom-sm tn-padding-top-sm">
                <view class="justify-content-item tn-flex tn-flex-col-center tn-padding-left-sm tn-padding-right-sm" style="margin-left: -6rpx;color: #333333;">
                  <text class="tn-text-sm">￥</text>
                  <text class="tn-padding-right-sm tn-text-lg tn-text-bold">{{ item.price }}</text>
                  <text class="tn-color-gray tn-text-sm" style="color: #C1CACF;"> {{ item.number }} 人已购</text>
                </view>
                <!-- <view class="justify-content-item tn-flex tn-flex-col-center">
                  <tn-button size="sm" shape="icon" backgroundColor="#3e486400" fontColor="#333333" padding="10rpx 0 0 0">
                    <text class="tn-icon-add-circle tn-text-lg"></text>
                  </tn-button>
                </view> -->
              </view>
            </view>
          </view>
        </block>
      </view>
      <!-- 商品 end-->
      
      <view class="tn-tabbar-height"></view>
      
    </view>
    
    <!-- popup会有延迟，采用这种方式来优化弹窗的优化体验-->
    <type-modal v-model="showTypeModal"></type-modal>
    
    <!-- popup会有延迟，采用这种方式来优化弹窗的优化体验-->
    <select-modal v-model="showSelectModal"></select-modal>
    

    
  </view>
</template>

<script>
  import TypeModal from '@/components/filter/type.vue'
  import SelectModal from '@/components/filter/select.vue'
  import template_page_mixin from '@/libs/mixin/template_page_mixin.js'
  export default {
    components: { TypeModal , SelectModal },
    name: 'TemplateFilter',
    mixins: [template_page_mixin],
    data(){
      return {
        /* 选择弹窗*/
        showTypeModal: false,
        
        /* 选择弹窗*/
        showSelectModal: true,
        
        product: [
          {
            price:'129',
            number:'231',
            desc: '蛋仔派对',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716013944651-assets/web-upload/32eafb0d-1e25-452b-9ad9-d14c18ec90f6.jpeg',
          },
          {
            price:'288',
            number:'157',
            desc: '保卫萝卜',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
          },
          {
            price:'299',
            number:'876',
            desc: '旅行青蛙',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014232876-assets/web-upload/5e1d7c68-048e-4b54-afda-68164a5bebd0.jpeg',
          },
          {
            price:'199',
            number:'29',
            desc: '辐射避难所',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg',
          },
          {
            price:'389',
            number:'643',
            desc: '王国保卫战',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025891640-assets/web-upload/8b4440ce-710b-4d3c-afff-5d49e6c7a16a.jpeg',
          },
          {
            price:'298',
            number:'386',
            desc: '崩坏 · 星穹铁道',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399286-assets/web-upload/7a715327-f597-4a8e-ac3c-2ba7ab45b6b9.jpeg',
          },
          {
            price:'368',
            number:'439',
            desc: '纪念碑谷',
            mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014593433-assets/web-upload/6e1ba4f3-4757-4693-b827-b8803072c5c9.jpeg',
          }
        ],
      }
    },
    methods: {
      // 打开选择弹框
      openTypeModal() {
        wx.vibrateShort();
        this.showTypeModal = true
      },
      // 打开选择弹框
      openSelectModal() {
        wx.vibrateShort();
        this.showSelectModal = true
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
  /* 胶囊*/
  .tn-custom-nav-bar__back {
    width: 40%;
    height: 100%;
    position: relative;
    display: flex;
    justify-content: space-evenly;
    align-items: center;
    box-sizing: border-box;
    // background-color: rgba(0, 0, 0, 0.15);
    // border: 1rpx solid rgba(255, 255, 255, 0.5);
    color: #000000;
    font-size: 18px;
    
    .icon {
      display: block;
      flex: 1;
      margin: auto;
      text-align: center;
    }
    
  }
  
  .top-fixed{
    position: fixed;
    background-color: rgba(255,255,255,1);
    top: 0;
    width: 100%;
    z-index: 100;
  }
  
  .icon-active{
    animation:acitve 0.2s forwards 1;
  }
  
  @keyframes acitve{
    from{
      
    }
    to{
      -webkit-transform: rotate(-180deg);
      -ms-transform: rotate(-180deg);
      transform: rotate(-180deg);
      color: #000000;
    }
  }
  
  /* 自定义导航栏内容 end */
  .bg-radius{
    background-color: #FFFFFF;
    border-radius: 16rpx;
    margin: 30rpx;
    padding: 30rpx;
  }
  
  .shop-shadow{
    background-color: #FFFFFF;
    border-radius: 24rpx;
  }
  
  .image-product{
    padding: 145rpx 0rpx;
    font-size: 16rpx;
    font-weight: 300;
    position: relative;
  }
  .image-pic{
    background-size: cover;
    background-repeat:no-repeat;
    // background-attachment:fixed;
    background-position:center;
    border-radius: 24rpx;
    border: 10rpx solid #FFFFFF;;
  }
  
  /* 底部安全边距 start*/
  .tn-tabbar-height {
    min-height: 100rpx;
    height: calc(120rpx + env(safe-area-inset-bottom) / 2);
    height: calc(120rpx + constant(safe-area-inset-bottom));
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
