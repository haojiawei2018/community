/**
 * 请求实例封装
 * 基于 tuniao-ui 内置的 luch-request，统一处理 token 与响应数据
 * 后端约定返回结构：{ code, data, message }，code 为 0 或 200 视为成功
 */
import Request from '@/tuniao-ui/libs/luch-request/index.js'
import env from '@/config/env.js'
import session from '@/utils/session.js'

// 创建请求实例
const http = new Request({
  baseURL: env.baseURL,
  timeout: 10000
})

function redirectToLogin() {
  const pages = getCurrentPages()
  const currentRoute = pages.length ? '/' + pages[pages.length - 1].route : ''
  if (currentRoute !== '/pages/login/login') {
    uni.reLaunch({ url: '/pages/login/login' })
  }
}

function showError(message, config) {
  if (config && config.custom && config.custom.silent) return
  uni.showToast({ title: message, icon: 'none' })
}

// 请求拦截器：统一携带租户编码和标准 Bearer Token
http.interceptors.request.use((config) => {
  config.header = config.header || {}
  if (env.communityCode) {
    config.header['X-Tenant-Code'] = env.communityCode
  }
  const token = session.getAccessToken()
  if (token) {
    config.header.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理业务状态码与网络错误
http.interceptors.response.use(
  (response) => {
    const res = response.data || {}
    const responseConfig = response.config || {}
    // code 为 0 或 200 视为成功，返回 data 字段
    if (res.code === 0 || res.code === 200) {
      return res.data
    }
    if (res.code === 401) {
      session.clearAuthSession()
      if (!responseConfig.custom || responseConfig.custom.authRedirect !== false) redirectToLogin()
    }
    showError(res.message || '请求失败', responseConfig)
    return Promise.reject(res)
  },
  (error) => {
    // 网络错误按 statusCode 给中文提示
    const statusCode = error.statusCode
    let msg = '网络异常，请稍后重试'
    if (statusCode === 401) {
      msg = '登录已过期，请重新登录'
      session.clearAuthSession()
      if (!error.config || !error.config.custom || error.config.custom.authRedirect !== false) redirectToLogin()
    } else if (statusCode === 403) {
      msg = '没有权限访问该资源'
    } else if (statusCode === 404) {
      msg = '请求的资源不存在'
    } else if (statusCode === 500) {
      msg = '服务器内部错误'
    }
    showError(msg, error.config)
    return Promise.reject(error)
  }
)

export default http
