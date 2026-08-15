import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useSessionStore } from '@/stores/session'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://42.193.104.179:10003'
export const publicClient = axios.create({ baseURL, timeout:15000 })
export const apiClient = axios.create({ baseURL, timeout:15000 })

apiClient.interceptors.request.use((config) => {
  const token = useSessionStore().current.token
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

apiClient.interceptors.response.use((response) => response, async (error) => {
  if (error.response?.status === 401) {
    useSessionStore().clear()
    await router.push({ path:'/login', query:{ redirect:router.currentRoute.value.fullPath } })
  } else if (error.response?.status === 403) {
    ElMessage.error('当前商户账号没有访问权限')
  }
  return Promise.reject(error)
})
