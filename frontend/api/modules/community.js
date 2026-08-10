/**
 * 社区接口模块
 * 对应 Hope Framework 论坛帖子、评论、点赞相关接口
 */
import http from '../http.js'
import env from '@/config/env.js'
import mock from '@/mock/community.mock.js'

function useForumMock() {
  return env.useMock || !env.forumApiEnabled
}

function normalizePage(data, page, pageSize) {
  const value = data || {}
  const rows = value.rows || value.records || value.list || []
  return {
    rows,
    total: Number(value.total) || rows.length,
    page: Number(value.page) || page,
    pageSize: Number(value.pageSize) || pageSize
  }
}

export default {
  /** 获取当前社区基础信息和功能开关 */
  getBootstrap(options = {}) {
    if (env.useMock) return mock.getBootstrap()
    return http.get('/api/v1/bootstrap', options)
  },

  /* ===================== 帖子相关 ===================== */

  /**
   * 获取帖子列表（分页）
   * GET /api/v1/posts
   * @param {Object} params - 查询参数
   * @param {number} [params.page=1] - 当前页码
   * @param {number} [params.pageSize=10] - 每页条数
   * @returns {Promise<{ rows: Array, total: number, page: number, pageSize: number }>}
   */
  async getPostList(params = {}) {
    const { page = 1, pageSize = 10 } = params
    if (useForumMock()) return mock.getPostList({ page, pageSize })
    const data = await http.get('/api/v1/posts', { params })
    return normalizePage(data, page, pageSize)
  },

  /**
   * 获取帖子详情
   * GET /api/v1/posts/{id}
   * @param {number|string} id - 帖子ID
   * @returns {Promise<object>} 帖子详情对象
   */
  getPostDetail(id) {
    if (useForumMock()) return mock.getPostDetail(id)
    return http.get(`/api/v1/posts/${id}`)
  },

  /**
   * 发布帖子
   * POST /api/v1/posts
   * @param {Object} data - 帖子内容
   * @param {string} data.title - 标题（必填）
   * @param {string} [data.content] - 正文内容
   * @returns {Promise<object>} 新建的帖子对象
   */
  publishPost(data) {
    if (useForumMock()) return mock.publishPost(data)
    return http.post('/api/v1/posts', data)
  },

  /* ===================== 评论相关 ===================== */

  /**
   * 获取帖子的评论列表（分页）
   * GET /api/v1/posts/{postId}/comments
   * @param {number|string} postId - 帖子ID
   * @param {Object} [params] - 分页参数
   * @param {number} [params.page=1] - 当前页码
   * @param {number} [params.pageSize=10] - 每页条数
   * @returns {Promise<{ rows: Array, total: number }>}
   */
  async getCommentList(postId, params = {}) {
    const { page = 1, pageSize = 10 } = params
    if (useForumMock()) return mock.getCommentList(postId, { page, pageSize })
    const data = await http.get(`/api/v1/posts/${postId}/comments`, { params: { page, pageSize } })
    return normalizePage(data, page, pageSize)
  },

  /**
   * 发表评论
   * POST /api/v1/posts/{postId}/comments
   * @param {Object} data - 评论内容
   * @param {number|string} data.postId - 帖子ID（必填）
   * @param {string} data.content - 评论内容（必填）
   * @returns {Promise<object>} 新建的评论对象
   */
  addComment(data) {
    if (useForumMock()) return mock.addComment(data)
    const { postId, ...payload } = data
    return http.post(`/api/v1/posts/${postId}/comments`, payload)
  },

  /* ===================== 点赞相关 ===================== */

  /**
   * 点赞帖子
   * PUT /api/v1/posts/{postId}/like
   * @param {number|string} postId - 帖子ID
   * @returns {Promise<boolean>}
   */
  likePost(postId) {
    if (useForumMock()) return mock.setPostLiked(postId, true)
    return http.put(`/api/v1/posts/${postId}/like`)
  },

  /**
   * 取消点赞
   * DELETE /api/v1/posts/{postId}/like
   * @param {number|string} postId - 帖子ID
   * @returns {Promise<boolean>}
   */
  unlikePost(postId) {
    if (useForumMock()) return mock.setPostLiked(postId, false)
    return http.delete(`/api/v1/posts/${postId}/like`)
  },

  /* ===================== 兼容旧接口（瀑布流/轮播图） ===================== */

  /**
   * 获取轮播图列表（旧接口，保留兼容）
   * @returns {Promise<{ list: Array, total: number }>}
   */
  getSwiperList() {
    return mock.getSwiperList()
  },

  /**
   * 获取瀑布流内容列表（旧接口，保留兼容）
   * 实际指向帖子列表接口
   * @returns {Promise<{ rows: Array, total: number }>}
   */
  async getContentList() {
    const page = await this.getPostList({ page: 1, pageSize: 20 })
    return { list: page.rows, total: page.total }
  }
}
