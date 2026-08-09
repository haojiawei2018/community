/**
 * 用户接口模块
 * 对应 Hope Framework 论坛用户接口
 */
import http from '../http.js'
import env from '@/config/env.js'
import mock from '@/mock/user.mock.js'

export default {
  /**
   * 登录
   * POST /forum/user/login
   * @param {string} email - 邮箱
   * @param {string} password - 密码
   * @returns {Promise<{ token: string, user: object }>}
   */
  login(email, password) {
    if (env.useMock) return mock.getUserInfo()
    return http.post('/forum/user/login', { email, password })
  },

  /**
   * 注册
   * POST /forum/user/register
   * @param {string} email - 邮箱
   * @param {string} password - 密码
   * @param {string} username - 用户名
   * @returns {Promise<object>}
   */
  register(email, password, username) {
    if (env.useMock) return mock.getUserInfo()
    return http.post('/forum/user/register', { email, password, username })
  },

  /**
   * 获取用户信息（通过用户ID）
   * GET /forum/user/{id}
   * @param {number|string} id - 用户ID
   * @returns {Promise<object>} 用户信息对象
   */
  getUserById(id) {
    if (env.useMock) return mock.getUserInfo()
    return http.get(`/forum/user/${id}`)
  }
}
