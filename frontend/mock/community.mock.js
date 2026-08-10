/**
 * 社区/首页 Mock 数据
 * 数据来源：PageA.vue 的 swiperList（5 条）与 data 瀑布流（取前 6 条）
 */
import { mockResponse } from './index.js'

// 轮播图静态数据（5 条）
const swiperList = [
  {
    id: 0,
    type: 'image',
    name: '原神 她来了',
    text: '开启全新的探索之旅',
    url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716047166485-assets/web-upload/ee3aa0c0-8589-41d6-b920-fae1a90a67d8.jpeg'
  },
  {
    id: 1,
    type: 'image',
    name: '',
    text: '',
    url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716184526452-assets/web-upload/dc5aadb2-d6bb-4cd3-8db2-2faddcd51c54.jpeg'
  },
  {
    id: 2,
    type: 'image',
    name: '',
    text: '',
    url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716040399137-assets/web-upload/51b8b734-e1db-4744-82f5-56186cb36f6d.jpeg'
  },
  {
    id: 3,
    type: 'image',
    name: '微信号 tnkewo',
    text: '商业合作请联系作者',
    url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039723058-assets/web-upload/28e928b8-4811-4331-8030-a6086efe4e4b.jpeg'
  },
  {
    id: 4,
    type: 'image',
    name: '努力成为大佬',
    text: '一起加油吖',
    url: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1715959853964-assets/web-upload/2c4f9b49-0014-4714-a9c6-60b2e5c06d94.jpeg'
  }
]

// 瀑布流内容静态数据（取 PageA.vue data 前 6 条）
const contentList = [
  {
    title: '纪念碑谷打卡',
    userName: '试试就逝世',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716014593433-assets/web-upload/6e1ba4f3-4757-4693-b827-b8803072c5c9.jpeg',
    userImage: 'https://cdn.nlark.com/yuque/0/2022/jpeg/280373/1664005699098-assets/web-upload/e8b29292-72fc-4c1e-9d7c-fd9dba31cb62.jpeg',
    tags: ['解谜', '简约'],
    viewUser: {
      latestUserAvatar: [
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg' }
      ],
      viewUserCount: 338
    }
  },
  {
    title: '王国保卫战-塔防启动',
    userName: '你的名字',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025891640-assets/web-upload/8b4440ce-710b-4d3c-afff-5d49e6c7a16a.jpeg',
    userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
    tags: [],
    viewUser: {
      latestUserAvatar: [
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg' }
      ],
      viewUserCount: 289
    }
  },
  {
    title: '',
    userName: '青梅煮马',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/png/280373/1715960090731-assets/web-upload/156084cd-aa5a-41dc-98ca-eb681d4afcc5.png',
    userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
    tags: ['钓鱼', '萝卜'],
    viewUser: {
      latestUserAvatar: [
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg' }
      ],
      viewUserCount: 381
    }
  },
  {
    title: '钓鱼模式，启动',
    userName: '你的名字',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1715959853964-assets/web-upload/2c4f9b49-0014-4714-a9c6-60b2e5c06d94.jpeg',
    userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg',
    tags: [],
    viewUser: {
      latestUserAvatar: [
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg' }
      ],
      viewUserCount: 526
    }
  },
  {
    title: '',
    userName: '凶一下试试',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716025506668-assets/web-upload/b6d123a2-4f1d-46c9-9474-f62a87d05a74.jpeg',
    userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg',
    tags: [],
    viewUser: {
      latestUserAvatar: [
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg' }
      ],
      viewUserCount: 526
    }
  },
  {
    title: '春暖花开',
    userName: '凶一下试试',
    mainImage: 'https://cdn.nlark.com/yuque/0/2024/jpeg/280373/1716039015216-assets/web-upload/ff424f75-5d37-4b80-8242-bb21e415875e.jpeg',
    userImage: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg',
    tags: [],
    viewUser: {
      latestUserAvatar: [
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_1.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_2.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_3.jpeg' },
        { src: 'https://resource.tuniaokj.com/images/blogger/avatar_4.jpeg' }
      ],
      viewUserCount: 526
    }
  }
]

const posts = contentList.map((item, index) => Object.assign({
  id: index + 1,
  content: item.title || '分享一段有趣的游戏经历。',
  images: [item.mainImage],
  createTime: Date.now() - index * 3600 * 1000,
  likeCount: 20 + index,
  isLiked: false
}, item))

const comments = [
  { id: 1, userName: '游戏搭子', content: '这个分享很有意思！', createTime: Date.now() - 20 * 60 * 1000 },
  { id: 2, userName: '快乐玩家', content: '收藏了，晚点也去试试。', createTime: Date.now() - 10 * 60 * 1000 }
]

export default {
  getBootstrap() {
    return mockResponse({
      communityId: '1',
      communityCode: 'default',
      communityName: '游戏社区',
      logoUrl: '',
      edition: 'COMMUNITY',
      features: { circle: true, post: true, comment: true, moderation: true }
    })
  },
  getCircleList() {
    return mockResponse([
      {
        id: 1,
        circleCode: 'general',
        circleName: '综合交流',
        description: '分享游戏心得、攻略和社区动态',
        memberCount: 0,
        postCount: posts.length
      }
    ])
  },
  // 获取轮播图列表
  getSwiperList() {
    return mockResponse({ list: swiperList, total: swiperList.length })
  },
  getPostList({ page = 1, pageSize = 10 } = {}) {
    const start = (page - 1) * pageSize
    return mockResponse({ rows: posts.slice(start, start + pageSize), total: posts.length, page, pageSize })
  },
  getPostDetail(id) {
    const value = posts.find((item) => String(item.id) === String(id)) || posts[0]
    return mockResponse(Object.assign({}, value))
  },
  publishPost(data) {
    return mockResponse(Object.assign({ id: Date.now(), status: 'PENDING_REVIEW' }, data))
  },
  getCommentList(postId, { page = 1, pageSize = 10 } = {}) {
    const start = (page - 1) * pageSize
    return mockResponse({ rows: comments.slice(start, start + pageSize), total: comments.length, page, pageSize })
  },
  addComment(data) {
    return mockResponse(Object.assign({ id: Date.now(), createTime: Date.now() }, data))
  },
  setPostLiked(postId, liked) {
    return mockResponse(liked)
  },
  // 获取瀑布流内容列表
  getContentList() {
    return mockResponse({ list: posts, total: posts.length })
  }
}
