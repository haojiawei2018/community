/**
 * 用户接口模块
 * 对应 Hope Framework 论坛用户接口
 */
import http from '../http.js'
import env from '@/config/env.js'
import mock from '@/mock/user.mock.js'
import session from '@/utils/session.js'

export default {
  /**
   * 登录
   * POST /api/v1/auth/login
   * @param {string} username - 登录账号
   * @param {string} password - 密码
   * @returns {Promise<{ token: string, user: object }>}
   */
  login(username, password) {
    if (env.useMock) return mock.getTokenResponse()
    return http.post('/api/v1/auth/login', {
      username,
      password,
      deviceId: session.getDeviceId(),
      clientType: session.getClientType()
    })
  },

  /**
   * 注册
   * POST /api/v1/auth/register
   * @param {string} username - 登录账号
   * @param {string} password - 密码
   * @param {string} nickname - 社区昵称
   * @returns {Promise<object>}
   */
  register(username, password, nickname) {
    if (env.useMock) return mock.getTokenResponse({ username, nickname, displayName: nickname })
    return http.post('/api/v1/auth/register', {
      username,
      password,
      nickname,
      deviceId: session.getDeviceId(),
      clientType: session.getClientType()
    })
  },

  refresh() {
    if (env.useMock) return mock.getTokenResponse()
    return http.post('/api/v1/auth/refresh', {
      refreshToken: session.getRefreshToken(),
      deviceId: session.getDeviceId(),
      clientType: session.getClientType()
    }, { custom: { silent: true, authRedirect: false } })
  },

  async restoreSession() {
    if (session.isAccessTokenUsable()) return session.getUser()
    if (!session.getRefreshToken()) return null
    try {
      const tokenResponse = await this.refresh()
      session.saveAuthSession(tokenResponse)
      return tokenResponse.user || null
    } catch (error) {
      session.clearAuthSession()
      return null
    }
  },

  getCurrentUser(options = {}) {
    if (env.useMock) return mock.getUserInfo()
    return http.get('/api/v1/users/me', options)
  },

  updateProfile(data) {
    return http.put('/api/v1/users/me', data)
  },

  logout() {
    if (env.useMock) return Promise.resolve()
    return http.post('/api/v1/auth/logout', {
      refreshToken: session.getRefreshToken()
    }, { custom: { silent: true, authRedirect: false } })
  }
}
