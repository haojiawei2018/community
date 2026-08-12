/**
 * API 汇总导出
 * 各业务模块统一从此处引入
 */
import user from './modules/user.js'
import community from './modules/community.js'
import message from './modules/message.js'
import file from './modules/file.js'
import checkIn from './modules/checkIn.js'

export default { user, community, message, file, checkIn }
export { user, community, message, file, checkIn }
