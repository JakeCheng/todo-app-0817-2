import request from './request'

/**
 * Todo 相关接口（全部需要 token）
 */
export const todoApi = {
  // 查询当前用户全部待办
  list: () => request.get('/api/todo'),

  // 新增待办
  create: (data) => request.post('/api/todo', data),

  // 修改待办（标题、完成状态）
  update: (id, data) => request.put(`/api/todo/${id}`, data),

  // 切换完成状态
  toggle: (id) => request.put(`/api/todo/${id}/toggle`),

  // 删除待办
  remove: (id) => request.delete(`/api/todo/${id}`)
}

export default todoApi
