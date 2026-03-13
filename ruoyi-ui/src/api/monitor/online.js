import request from '@/utils/request'

// 查询在线用户列表
export function list(query) {
  return request({
    url: '/monitor/online/list',
    method: 'get',
    params: query
  })
}

// 导出在线用户
export function exportOnline(query) {
  return request({
    url: '/monitor/online/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}

// 强退用户
export function forceLogout(tokenId) {
  return request({
    url: '/monitor/online/' + tokenId,
    method: 'delete'
  })
}
