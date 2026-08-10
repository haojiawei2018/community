/**
 * 环境配置
 * 通过 process.env.NODE_ENV 区分开发环境与生产环境
 * - development: 本地开发，启用 mock，后端地址指向本地
 * - production:  线上生产，关闭 mock，后端地址指向正式域名
 */

// 当前环境字符串：development 或 production
const env = process.env.NODE_ENV || 'development'

// 是否为开发环境
const isDev = env === 'development'

export default {
  // 当前环境字符串
  env,
  // 后端接口基础地址（Hope Framework）
  baseURL: isDev ? 'http://localhost:10003' : 'https://api.your-domain.com',
  // SaaS 版用于解析租户；开源单社区版会安全忽略并使用后端固定社区配置
  communityCode: 'default',
  // 是否启用 mock 静态数据
  useMock: false,
  // 圈子、帖子、评论和点赞 P0 接口已落地；联调时可临时关闭以回到静态展示
  forumApiEnabled: true,
  // 应用版本号
  appVersion: '1.0.0'
}
