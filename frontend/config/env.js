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
  // 是否启用 mock 静态数据
  useMock: false,
  // 应用版本号
  appVersion: '1.0.0'
}
