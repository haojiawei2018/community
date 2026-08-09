/**
 * 社区接口模块
 * 对应 Hope Framework 论坛帖子、评论、点赞相关接口
 */
import http from '../http.js'
import env from '@/config/env.js'
import mock from '@/mock/community.mock.js'

export default {
  /* ===================== 帖子相关 ===================== */

  /**
   * 获取帖子列表（分页）
   * GET /forum/post/page
   * @param {Object} params - 查询参数
   * @param {number} [params.page=1] - 当前页码
   * @param {number} [params.pageSize=10] - 每页条数
   * @returns {Promise<{ rows: Array, total: number, page: number, pageSize: number }>}
   */
  getPostList(params = {}) {
    if (env.useMock) return mock.getContentList()
    const { page = 1, pageSize = 10 } = params
    return http.get('/forum/post/page', { params: { page, pageSize } })
  },

  /**
   * 获取帖子详情
   * GET /forum/post/{id}
   * @param {number|string} id - 帖子ID
   * @returns {Promise<object>} 帖子详情对象
   */
  getPostDetail(id) {
    return http.get(`/forum/post/${id}`)
  },

  /**
   * 发布帖子
   * POST /forum/post
   * @param {Object} data - 帖子内容
   * @param {string} data.title - 标题（必填）
   * @param {string} [data.content] - 正文内容
   * @returns {Promise<object>} 新建的帖子对象
   */
  publishPost(data) {
    return http.post('/forum/post', data)
  },

  /* ===================== 评论相关 ===================== */

  /**
   * 获取帖子的评论列表（分页）
   * GET /forum/comment/page/{postId}
   * @param {number|string} postId - 帖子ID
   * @param {Object} [params] - 分页参数
   * @param {number} [params.page=1] - 当前页码
   * @param {number} [params.pageSize=10] - 每页条数
   * @returns {Promise<{ rows: Array, total: number }>}
   */
  getCommentList(postId, params = {}) {
    const { page = 1, pageSize = 10 } = params
    return http.get(`/forum/comment/page/${postId}`, {
      params: { page, pageSize }
    })
  },

  /**
   * 发表评论
   * POST /forum/comment
   * @param {Object} data - 评论内容
   * @param {number|string} data.postId - 帖子ID（必填）
   * @param {string} data.content - 评论内容（必填）
   * @returns {Promise<object>} 新建的评论对象
   */
  addComment(data) {
    return http.post('/forum/comment', data)
  },

  /* ===================== 点赞相关 ===================== */

  /**
   * 点赞帖子
   * POST /forum/post/like
   * @param {number|string} postId - 帖子ID
   * @returns {Promise<boolean>}
   */
  likePost(postId) {
    return http.post('/forum/post/like', { postId })
  },

  /**
   * 取消点赞
   * DELETE /forum/post/like
   * @param {number|string} postId - 帖子ID
   * @returns {Promise<boolean>}
   */
  unlikePost(postId) {
    return http.delete('/forum/post/like', { postId })
  },

  /* ===================== 兼容旧接口（瀑布流/轮播图） ===================== */

  /**
   * 获取轮播图列表（旧接口，保留兼容）
   * @returns {Promise<{ list: Array, total: number }>}
   */
  getSwiperList() {
    if (env.useMock) return mock.getSwiperList()
    return http.get('/community/swiper')
  },

  /**
   * 获取瀑布流内容列表（旧接口，保留兼容）
   * 实际指向帖子列表接口
   * @returns {Promise<{ rows: Array, total: number }>}
   */
  getContentList() {
    if (env.useMock) return mock.getContentList()
    return http.get('/forum/post/page', { params: { page: 1, pageSize: 20 } })
  }
}
