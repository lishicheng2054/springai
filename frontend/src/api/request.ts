import axios from 'axios'
import type { Result } from '../types/common'

const request = axios.create({
  baseURL: import.meta.env.PROD ? '' : '',
  timeout: 30000,
})

// 响应拦截器：统一处理 Result 格式
request.interceptors.response.use(
  (response) => {
    const result = response.data as Result<unknown>
    if (result.code === 0) {
      // 成功：直接返回 data
      response.data = result.data
      return response
    }
    // 失败：抛出业务错误
    return Promise.reject(new Error(result.message || '请求失败'))
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(message))
  },
)

export default request
