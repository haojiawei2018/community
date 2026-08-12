/**
 * 消息接口模块
 */
import http from '../http.js'

export default {
  // 当前成员通知分页
  getList(params) {
    return http.get('/api/v1/notifications', { params })
  },
  // 四类未读数量
  getSummary() {
    return http.get('/api/v1/notifications/summary')
  },
  // 单条已读
  markRead(notificationId) {
    return http.put(`/api/v1/notifications/${notificationId}/read`)
  },
  // 当前分类或全部已读
  markAllRead(category) {
    return http.put('/api/v1/notifications/read-all', null, {
      params: category ? { category } : {}
    })
  }
}
