import axios from 'axios'
import router from '@/router'

// 后端接口基础地址
// 开发环境：留空走相对路径，由 vite.config.js 的 proxy 代理转发到后端，规避跨域
// 生产环境：通过 VITE_API_BASE_URL 注入后端实际地址，如 'https://todo-backend.up.railway.app'
const BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

// 全局 loading 计数器（并发请求合并显示一个 loading）
let loadingCount = 0
let loadingInstance = null

function showLoading() {
  loadingCount++
  if (loadingCount === 1 && window.$message) {
    // 使用 message 的 loading 模式
    loadingInstance = window.$message.loading('加载中...', { duration: 0 })
  }
}

function hideLoading() {
  loadingCount = Math.max(0, loadingCount - 1)
  if (loadingCount === 0 && loadingInstance) {
    loadingInstance.destroy()
    loadingInstance = null
  }
}

// 创建 axios 实例
const request = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
})

// 请求拦截：自动携带 token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    showLoading()
    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

// 响应拦截：统一错误处理
request.interceptors.response.use(
  (response) => {
    hideLoading()
    const res = response.data

    // 文件流等特殊响应直接返回
    if (response.config.responseType === 'blob') {
      return response
    }

    // 业务成功
    if (res.code === 200) {
      return res
    }

    // 业务失败
    if (window.$message) {
      window.$message.error(res.message || '请求失败')
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    hideLoading()
    const { response } = error

    if (response) {
      // 401 未授权：清理 token 跳登录
      if (response.status === 401 || (response.data && response.data.code === 401)) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        if (window.$message) {
          window.$message.error('登录已过期，请重新登录')
        }
        if (router.currentRoute.value.name !== 'Login') {
          router.push({ name: 'Login' })
        }
        return Promise.reject(error)
      }

      const msg = (response.data && response.data.message) || `请求失败(${response.status})`
      if (window.$message) {
        window.$message.error(msg)
      }
    } else if (error.message && error.message.includes('timeout')) {
      if (window.$message) {
        window.$message.error('请求超时，请稍后重试')
      }
    } else {
      if (window.$message) {
        window.$message.error('网络异常，请检查后端服务是否启动')
      }
    }
    return Promise.reject(error)
  }
)

export default request
