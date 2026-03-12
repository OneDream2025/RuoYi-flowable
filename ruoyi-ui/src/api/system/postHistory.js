import request from '@/utils/request'

// 查询岗位变更历史列表
export function listPostHistory(query) {
  return request({
    url: '/system/postHistory/list',
    method: 'get',
    params: query
  })
}

// 查询岗位变更历史详细
export function getPostHistory(historyId) {
  return request({
    url: '/system/postHistory/' + historyId,
    method: 'get'
  })
}

// 根据岗位ID获取变更历史列表
export function getHistoryByPostId(postId) {
  return request({
    url: '/system/postHistory/post/' + postId,
    method: 'get'
  })
}

// 获取变更详情
export function getChangeDetails(historyId) {
  return request({
    url: '/system/postHistory/details/' + historyId,
    method: 'get'
  })
}

// 对比两个历史版本
export function compareHistory(historyId1, historyId2) {
  return request({
    url: '/system/postHistory/compare',
    method: 'get',
    params: { historyId1, historyId2 }
  })
}

// 回滚岗位信息
export function rollbackPost(historyId) {
  return request({
    url: '/system/postHistory/rollback/' + historyId,
    method: 'post'
  })
}
