/**
 * Mock 辅助工具
 * 提供统一的 mock 响应延迟模拟，并从 env 读取 useMock 开关
 */
import env from '@/config/env.js'

/**
 * 模拟接口响应
 * @param {Object} data mock 数据
 * @param {Number} delay 模拟网络延迟（毫秒），默认 300
 * @returns {Promise} resolve 出传入的 data
 */
export function mockResponse(data, delay = 300) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(data)
    }, delay)
  })
}

// 导出 useMock 开关，便于各 api 模块判断
export const useMock = env.useMock

export default {
  mockResponse,
  useMock
}
