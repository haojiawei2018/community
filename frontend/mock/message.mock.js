/**
 * 消息页 Mock 数据
 * 数据来源：PageD.vue 原 data.message 数组（7 条）
 */
import { mockResponse } from './index.js'

// 消息列表静态数据
const list = [
  {
    title: '蛋仔派对',
    time: '3天前',
    desc: '蛋仔派对 X INSTINCTOY 梦幻联动，全民乐园狂欢活动盛大开启！',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716013944651-assets/web-upload/32eafb0d-1e25-452b-9ad9-d14c18ec90f6.jpeg'
  },
  {
    title: '保卫萝卜',
    time: '1个月前',
    desc: '全新保卫萝卜系列手游上线，新版保卫萝卜你是否喜欢？',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png'
  },
  {
    title: '旅行青蛙',
    time: '2个月前',
    desc: '谁能拒绝云养一个那么可爱的青蛙呢',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014232876-assets/web-upload/5e1d7c68-048e-4b54-afda-68164a5bebd0.jpeg'
  },
  {
    title: '辐射避难所',
    time: '6个月前',
    desc: '只要胆子大，人口一次性升二十个',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg'
  },
  {
    title: '王国保卫战',
    time: '1年前',
    desc: '策略为王，火力全开',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025891640-assets/web-upload/8b4440ce-710b-4d3c-afff-5d49e6c7a16a.jpeg'
  },
  {
    title: '崩坏 · 星穹铁道',
    time: '1年前',
    desc: '知更鸟的麦被抢了，嘤嘤',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399286-assets/web-upload/7a715327-f597-4a8e-ac3c-2ba7ab45b6b9.jpeg'
  },
  {
    title: '纪念碑谷',
    time: '2年前',
    desc: '发布8年仍有9.7的评分，真正的神作',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014593433-assets/web-upload/6e1ba4f3-4757-4693-b827-b8803072c5c9.jpeg'
  }
]

export default {
  // 获取消息列表
  getList(params) {
    return mockResponse({
      list,
      total: list.length
    })
  }
}
