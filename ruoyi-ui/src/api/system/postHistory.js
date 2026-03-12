import request from '@/utils/request'

// 查询岗位变更历史列表
export function listPostHistory(query) {
  return request({
    url: '/system/post/history/list',
    method: 'get',
    params: query
  })
}

// 根据岗位ID查询变更历史列表
export function getPostHistoryByPostId(postId) {
  return request({
    url: '/system/post/history/post/' + postId,
    method: 'get'
  })
}

// 查询岗位变更历史详细
export function getPostHistory(historyId) {
  return request({
    url: '/system/post/history/' + historyId,
    method: 'get'
  })
}

// 获取变更详情列表
export function getChangeDetail(historyId) {
  return request({
    url: '/system/post/history/detail/' + historyId,
    method: 'get'
  })
}

// 对比两个历史版本
export function compareVersions(data) {
  return request({
    url: '/system/post/history/compare',
    method: 'post',
    data: data
  })
}

// 回滚到指定历史版本
export function rollbackVersion(historyId) {
  return request({
    url: '/system/post/history/rollback/' + historyId,
    method: 'post'
  })
}
