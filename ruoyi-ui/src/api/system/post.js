import request from '@/utils/request'

// 查询岗位列表
export function listPost(query) {
  return request({
    url: '/system/post/list',
    method: 'get',
    params: query
  })
}

// 查询岗位详细
export function getPost(postId) {
  return request({
    url: '/system/post/' + postId,
    method: 'get'
  })
}

// 新增岗位
export function addPost(data) {
  return request({
    url: '/system/post',
    method: 'post',
    data: data
  })
}

// 修改岗位
export function updatePost(data) {
  return request({
    url: '/system/post',
    method: 'put',
    data: data
  })
}

// 删除岗位
export function delPost(postId) {
  return request({
    url: '/system/post/' + postId,
    method: 'delete'
  })
}

// 查询岗位历史列表
export function listPostHistory(query) {
  return request({
    url: '/system/post/history/list',
    method: 'get',
    params: query
  })
}

// 查询岗位历史详细
export function getPostHistory(historyId) {
  return request({
    url: '/system/post/history/' + historyId,
    method: 'get'
  })
}

// 根据岗位ID查询历史列表
export function getPostHistoryByPostId(postId) {
  return request({
    url: '/system/post/history/post/' + postId,
    method: 'get'
  })
}

// 对比岗位版本
export function comparePostVersions(historyId1, historyId2) {
  return request({
    url: '/system/post/history/compare/' + historyId1 + '/' + historyId2,
    method: 'get'
  })
}

// 岗位回滚
export function rollbackPost(historyId) {
  return request({
    url: '/system/post/history/rollback/' + historyId,
    method: 'post'
  })
}
