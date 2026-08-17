import request from './request'

/**
 * 用户相关接口
 */
export const userApi = {
  // 注册（成功后直接返回 token）
  register: (data) => request.post('/api/user/register', data),

  // 登录
  login: (data) => request.post('/api/user/login', data),

  // 忘记密码 / 修改密码
  updatePassword: (data) => request.post('/api/user/password', data),

  // 获取当前登录用户信息
  getInfo: () => request.get('/api/user/info'),

  // 修改个人信息
  updateProfile: (data) => request.put('/api/user/info', data)
}

export default userApi
