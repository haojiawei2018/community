import axios from 'axios'
import { useSession } from './stores/session'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:10003'
export const http = axios.create({ baseURL, timeout: 15000 })

http.interceptors.request.use(config => {
  const token = useSession().token
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

http.interceptors.response.use(response => {
  const body = response.data
  if (body?.code !== undefined && body.code !== 200) return Promise.reject(new Error(body.message || '请求失败'))
  return body?.data === undefined ? body : body.data
}, error => Promise.reject(new Error(error.response?.data?.message || error.message || '请求失败')))

const root = '/api/admin/v1/pet-snack'
export const api = {
  login: (data: any) => http.post(`${root}/login`, data),
  overview: () => http.get(`${root}/overview`),
  store: () => http.get(`${root}/store`),
  saveStore: (data: any) => http.put(`${root}/store`, data),
  users: (params: any) => http.get(`${root}/users`, { params }),
  orders: (params: any) => http.get(`${root}/orders`, { params }),
  order: (id: string) => http.get(`${root}/orders/${id}`),
  categories: () => http.get(`${root}/categories`) as any,
  saveCategory: (data: any) => data.id ? http.put(`${root}/categories/${data.id}`, data) : http.post(`${root}/categories`, data),
  products: (params: any = {}) => http.get(`${root}/products`, { params }),
  product: (id: any) => http.get(`${root}/products/${id}`),
  saveProduct: (data: any) => data.id ? http.put(`${root}/products/${data.id}`, data) : http.post(`${root}/products`, data),
  upload: (file: File) => {
    const form = new FormData()
    form.append('file', file)
    return http.post(`${root}/images`, form)
  }
}
