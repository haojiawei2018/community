/**
 * 用户信息 Mock 数据
 */
import { mockResponse } from './index.js'

// 用户信息静态数据
const userInfo = {
  userId: '202406886',
  memberId: '202406886',
  tenantId: '1',
  username: 'player_01',
  nickname: '抓住那只猪',
  displayName: '抓住那只猪',
  avatarUrl: 'https://resource.tuniaokj.com/images/flower/guye1.jpg',
  memberStatus: 'ACTIVE',
  roles: ['MEMBER'],
  permissions: []
}

export default {
  // 获取用户信息
  getUserInfo() {
    return mockResponse(userInfo)
  },
  getTokenResponse(overrides = {}) {
    const user = Object.assign({}, userInfo, overrides)
    return mockResponse({
      tokenType: 'Bearer',
      accessToken: 'mock-access-token',
      expiresIn: 7200,
      refreshToken: 'mock-refresh-token',
      user
    })
  }
}
