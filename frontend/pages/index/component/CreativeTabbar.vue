<template>
  <view class="creative-tabbar">
    <view class="tabbar-shadow" :class="['shadow-color-' + currentTab, { 'shadow-animate': animating }]"></view>
    <view class="tabbar-container" :class="['tab-bg-' + currentTab, { 'container-animate': animating }]">
      <view class="tabbar-highlight"></view>
      <view class="tabbar-glass-effect"></view>
      <view class="tab-content">
        <view v-for="(item, index) in items" :key="item.name" class="tab-item"
          :class="['tab-item-' + index, { active: currentTab === index, publish: item.action === 'publish' }]"
          @tap="handleClick(index, item)">
          <view class="icon-wrapper">
            <view class="layer-container">
              <view class="layer-effect">
                <view v-for="layer in 4" :key="layer" class="layer-item"></view>
                <view class="icon-container">
                  <text :class="item.icon" class="tab-icon"></text>
                  <view class="filled-background"></view>
                  <view class="icon-glow"></view>
                </view>
              </view>
            </view>
            <text class="tab-label" :class="{ 'show-label': currentTab === index }">{{ item.name.replace(/\s/g, '') }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
  export default {
    name: 'CreativeTabbar',
    props: {
      currentTab: { type: Number, default: 0 },
      items: { type: Array, default: () => [] }
    },
    data() {
      return { animating: false, timer: null }
    },
    watch: {
      currentTab() { this.playAnimation() }
    },
    beforeDestroy() {
      if (this.timer) clearTimeout(this.timer)
    },
    methods: {
      handleClick(index) {
        this.playAnimation()
        this.$emit('changeTabbar', index)
      },
      playAnimation() {
        this.animating = false
        this.$nextTick(() => {
          this.animating = true
          if (this.timer) clearTimeout(this.timer)
          this.timer = setTimeout(() => { this.animating = false }, 700)
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
  .creative-tabbar {
    --c0: #111111; --c1: #10B981; --c2: #111111; --c3: #F59E0B; --c4: #8B5CF6;
    position: fixed; z-index: 998; left: 5%; right: 5%; bottom: 34rpx;
    bottom: calc(34rpx + env(safe-area-inset-bottom) / 2);
  }
  .tabbar-shadow {
    position: absolute; z-index: 0; left: 40%; right: 40%; bottom: -8rpx; height: 16rpx;
    border-radius: 100%; opacity: .3; filter: blur(15px); background: rgba(0,0,0,.25);
  }
  .shadow-color-1 { background: rgba(16,185,129,.35); }
  .shadow-color-3 { background: rgba(245,158,11,.35); }
  .shadow-color-4 { background: rgba(139,92,246,.35); }
  .shadow-animate { animation: shadow-spread .65s cubic-bezier(.2,.9,.3,1.3); }
  .tabbar-container {
    position: relative; height: 130rpx; overflow: visible; border-radius: 30rpx;
    background: rgba(248,247,248,.94); border: 1rpx solid rgba(255,255,255,.8);
    box-shadow: 10rpx 10rpx 20rpx rgba(174,174,192,.35), -10rpx -10rpx 20rpx rgba(255,255,255,.75);
    backdrop-filter: blur(20rpx); -webkit-backdrop-filter: blur(20rpx);
  }
  .container-animate { animation: container-pulse .6s cubic-bezier(.2,.9,.3,1.3); }
  .tabbar-highlight {
    position: absolute; z-index: 2; left: 0; right: 0; top: 0; height: 50%; pointer-events: none;
    border-radius: 30rpx; background: linear-gradient(to bottom, rgba(255,255,255,.85), rgba(255,255,255,0));
  }
  .tabbar-glass-effect {
    position: absolute; z-index: 1; inset: 0; pointer-events: none; border-radius: 30rpx;
    background: linear-gradient(135deg, rgba(255,255,255,.28), rgba(255,255,255,.06));
  }
  .tab-content { position: relative; z-index: 10; display: flex; width: 100%; height: 100%; }
  .tab-item { flex: 1; position: relative; display: flex; justify-content: center; overflow: visible; }
  .icon-wrapper { position: relative; display: flex; justify-content: center; width: 100%; height: 126rpx; }
  .layer-container { position: relative; width: 62rpx; height: 62rpx; margin-top: 31rpx; transition: all .35s cubic-bezier(.34,1.56,.64,1); }
  .layer-effect, .layer-item, .icon-container { position: absolute; inset: 0; }
  .layer-item {
    border-radius: 14rpx; opacity: .16; border: 1rpx solid rgba(60,60,60,.28); background: rgba(0,0,0,.12);
    transition: all .35s cubic-bezier(.34,1.56,.64,1);
  }
  .icon-container {
    z-index: 5; display: flex; align-items: center; justify-content: center; overflow: hidden; border-radius: 14rpx;
    background: #F8F7F8; box-shadow: 5rpx 5rpx 10rpx rgba(174,174,192,.38), -5rpx -5rpx 10rpx #FFF;
  }
  .tab-icon { position: relative; z-index: 3; color: #555; font-size: 34rpx; transition: all .35s ease; }
  .filled-background { position: absolute; z-index: 1; left: 0; right: 0; bottom: 0; height: 0; transition: height .55s cubic-bezier(.34,1.56,.64,1); }
  .tab-label {
    position: absolute; bottom: 3rpx; opacity: 0; color: #555; font-size: 21rpx; font-weight: 600;
    transform: translateY(8rpx); transition: all .3s ease; white-space: nowrap;
  }
  .tab-label.show-label { bottom: 8rpx; opacity: 1; transform: translateY(0); }
  .tab-item.active .layer-container { transform: translateY(-15rpx) rotate(-12deg) skew(7deg); animation: float 3s ease-in-out infinite; }
  .tab-item.active .layer-item:nth-child(2) { transform: translate(4rpx,-4rpx); opacity: .35; }
  .tab-item.active .layer-item:nth-child(3) { transform: translate(8rpx,-8rpx); opacity: .5; }
  .tab-item.active .layer-item:nth-child(4) { transform: translate(12rpx,-12rpx); opacity: .7; }
  .tab-item.active .filled-background { height: 100%; }
  .tab-item.active .tab-icon { color: #FFF; transform: rotate(12deg); }
  .tab-item-0.active .filled-background, .tab-item-2 .filled-background { background: var(--c0); }
  .tab-item-1.active .filled-background { background: var(--c1); }
  .tab-item-3.active .filled-background { background: var(--c3); }
  .tab-item-4.active .filled-background { background: var(--c4); }
  .tab-item-0.active .tab-label { color: var(--c0); }
  .tab-item-1.active .tab-label { color: var(--c1); }
  .tab-item-3.active .tab-label { color: var(--c3); }
  .tab-item-4.active .tab-label { color: var(--c4); }
  .tab-item.publish .icon-container { border-radius: 50%; background: #111; transform: scale(1.08); }
  .tab-item.publish .tab-icon { color: #FFF; font-size: 40rpx; }
  .tab-item.publish:active .layer-container { transform: scale(.78) rotate(-12deg); }
  @keyframes float { 50% { transform: translateY(-21rpx) rotate(-12deg) skew(7deg); } }
  @keyframes container-pulse { 50% { transform: scale(1.02); } }
  @keyframes shadow-spread { 50% { left: 10%; right: 10%; opacity: .8; filter: blur(24px); } 100% { left: 12%; right: 12%; } }
</style>
