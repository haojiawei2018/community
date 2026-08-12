/**
 * 公共图片上传模块。
 * 所有页面上传图片必须调用 uploadImage，统一处理地址、租户、Token 和响应格式。
 */
import env from '@/config/env.js'
import session from '@/utils/session.js'

function uploadHeaders() {
  const headers = {}
  const token = session.getAccessToken()
  if (token) headers.Authorization = `Bearer ${token}`
  if (env.communityCode) headers['X-Tenant-Code'] = env.communityCode
  return headers
}

function parseResponse(response) {
  let body = response && response.data
  if (typeof body === 'string') {
    try { body = JSON.parse(body) } catch (error) { throw new Error('图片上传响应格式错误') }
  }
  if (!response || response.statusCode < 200 || response.statusCode >= 300) {
    throw new Error((body && (body.message || body.msg)) || '图片上传失败')
  }
  if (body && body.code !== undefined && body.code !== 0 && body.code !== 200) {
    if (body.code === 401) session.clearAuthSession()
    throw new Error(body.message || body.msg || '图片上传失败')
  }
  const data = body && body.data !== undefined ? body.data : body
  const result = typeof data === 'string' ? { url: data } : (data || {})
  if (!result.url) throw new Error('图片上传结果缺少 URL')
  return result
}

export function uploadImage(filePath, options = {}) {
  if (!filePath) return Promise.reject(new Error('请选择图片'))
  return new Promise((resolve, reject) => {
    const task = uni.uploadFile({
      url: `${env.baseURL}/api/v1/files/images`,
      filePath,
      name: 'file',
      header: uploadHeaders(),
      formData: options.formData || {},
      success(response) {
        try { resolve(parseResponse(response)) } catch (error) { reject(error) }
      },
      fail: reject
    })
    if (task && options.onProgress && typeof task.onProgressUpdate === 'function') {
      task.onProgressUpdate(options.onProgress)
    }
  })
}

export default { uploadImage }
