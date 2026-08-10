const STORAGE_KEYS = {
  accessToken: 'gc_access_token',
  refreshToken: 'gc_refresh_token',
  accessTokenExpiresAt: 'gc_access_token_expires_at',
  user: 'gc_user',
  community: 'gc_community',
  deviceId: 'gc_device_id'
}

function getStored(key, fallback = '') {
  const value = uni.getStorageSync(key)
  return value === undefined || value === null || value === '' ? fallback : value
}

export function getAccessToken() {
  return getStored(STORAGE_KEYS.accessToken) || getStored('token')
}

export function getRefreshToken() {
  return getStored(STORAGE_KEYS.refreshToken)
}

export function getAccessTokenExpiresAt() {
  return Number(getStored(STORAGE_KEYS.accessTokenExpiresAt, 0)) || 0
}

export function isAccessTokenUsable() {
  const token = getAccessToken()
  const expiresAt = getAccessTokenExpiresAt()
  return !!token && (!expiresAt || expiresAt - Date.now() > 60 * 1000)
}

export function saveAuthSession(tokenResponse) {
  if (!tokenResponse || !tokenResponse.accessToken) return false
  const expiresIn = Number(tokenResponse.expiresIn) || 0
  uni.setStorageSync(STORAGE_KEYS.accessToken, tokenResponse.accessToken)
  uni.setStorageSync('token', tokenResponse.accessToken)
  if (tokenResponse.refreshToken) {
    uni.setStorageSync(STORAGE_KEYS.refreshToken, tokenResponse.refreshToken)
  }
  if (expiresIn > 0) {
    uni.setStorageSync(STORAGE_KEYS.accessTokenExpiresAt, Date.now() + expiresIn * 1000)
  }
  if (tokenResponse.user) saveUser(tokenResponse.user)
  return true
}

export function clearAuthSession() {
  Object.keys(STORAGE_KEYS).forEach((name) => {
    if (name !== 'community' && name !== 'deviceId') {
      uni.removeStorageSync(STORAGE_KEYS[name])
    }
  })
  uni.removeStorageSync('token')
  uni.removeStorageSync('userInfo')
}

export function saveUser(user) {
  const value = user || {}
  uni.setStorageSync(STORAGE_KEYS.user, value)
  uni.setStorageSync('userInfo', value)
}

export function getUser() {
  return getStored(STORAGE_KEYS.user, getStored('userInfo', {})) || {}
}

export function saveCommunity(community) {
  uni.setStorageSync(STORAGE_KEYS.community, community || {})
}

export function getCommunity() {
  return getStored(STORAGE_KEYS.community, {}) || {}
}

export function getDeviceId() {
  let deviceId = getStored(STORAGE_KEYS.deviceId)
  if (!deviceId) {
    deviceId = `gc-${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`
    uni.setStorageSync(STORAGE_KEYS.deviceId, deviceId)
  }
  return deviceId
}

export function getClientType() {
  let clientType = 'H5'
  // #ifdef MP-WEIXIN
  clientType = 'WECHAT'
  // #endif
  // #ifdef APP-PLUS
  clientType = 'APP'
  // #endif
  return clientType
}

export default {
  getAccessToken,
  getRefreshToken,
  getAccessTokenExpiresAt,
  isAccessTokenUsable,
  saveAuthSession,
  clearAuthSession,
  saveUser,
  getUser,
  saveCommunity,
  getCommunity,
  getDeviceId,
  getClientType
}
