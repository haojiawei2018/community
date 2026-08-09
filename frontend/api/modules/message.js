/**
 * 消息接口模块
 */
import http from '../http.js'
import env from '@/config/env.js'
import mock from '@/mock/message.mock.js'

export default {
  // 获取消息列表
  getList(params) {
    if (env.useMock) return mock.getList(params)
    return http.get('/message/list', { params })
  }
}
