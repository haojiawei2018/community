/**
 * 请求实例封装
 * 基于 tuniao-ui 内置的 luch-request，统一处理 token 与响应数据
 * 后端约定返回结构：{ code, data, message }，code 为 0 或 200 视为成功
 */
import Request from '@/tuniao-ui/libs/luch-request/index.js'
import env from '@/config/env.js'

// 创建请求实例
const http = new Request({
  baseURL: env.baseURL,
  timeout: 10000
})

// 请求拦截器：自动携带 token
// Hope Framework 约定使用 token 请求头传递身份凭证
http.interceptors.request.use((config) => {
  const token = uni.getStorageSync('token')
  if (token) {
    config.header = config.header || {}
    config.header['token'] = token
  }
  return config
})

// 响应拦截器：统一处理业务状态码与网络错误
http.interceptors.response.use(
  (response) => {
    const res = response.data || {}
    // code 为 0 或 200 视为成功，返回 data 字段
    if (res.code === 0 || res.code === 200) {
      return res.data
    }
    // 业务错误：提示 message 并拒绝
    uni.showToast({
      title: res.message || '请求失败',
      icon: 'none'
    })
    return Promise.reject(res)
  },
  (error) => {
    // 网络错误按 statusCode 给中文提示
    const statusCode = error.statusCode
    let msg = '网络异常，请稍后重试'
    if (statusCode === 401) {
      msg = '登录已过期，请重新登录'
      // 401 时清除本地 token 并跳转登录页
      uni.removeStorageSync('token')
      // 避免重复跳转：仅当当前页不是登录页时跳转
      const pages = getCurrentPages()
      const currentRoute = pages.length ? '/' + pages[pages.length - 1].route : ''
      if (currentRoute !== '/pages/login/login') {
        uni.reLaunch({ url: '/pages/login/login' })
      }
    } else if (statusCode === 403) {
      msg = '没有权限访问该资源'
    } else if (statusCode === 404) {
      msg = '请求的资源不存在'
    } else if (statusCode === 500) {
      msg = '服务器内部错误'
    }
    uni.showToast({
      title: msg,
      icon: 'none'
    })
    return Promise.reject(error)
  }
)

export default http
