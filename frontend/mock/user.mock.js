/**
 * 用户信息 Mock 数据
 */
import { mockResponse } from './index.js'

// 用户信息静态数据
const userInfo = {
  name: '抓住那只猪',
  uid: '202406886',
  avatar: 'https://resource.tuniaokj.com/images/flower/guye1.jpg'
}

export default {
  // 获取用户信息
  getUserInfo() {
    return mockResponse(userInfo)
  }
}
