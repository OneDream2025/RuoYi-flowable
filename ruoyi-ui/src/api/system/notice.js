import request from '@/utils/request'

// 查询公告列表
export function listNotice(query) {
  return request({
    url: '/system/notice/list',
    method: 'get',
    params: query
  })
}

// 查询公告详细
export function getNotice(noticeId) {
  return request({
    url: '/system/notice/' + noticeId,
    method: 'get'
  })
}

// 新增公告
export function addNotice(data) {
  return request({
    url: '/system/notice',
    method: 'post',
    data: data
  })
}

// 修改公告
export function updateNotice(data) {
  return request({
    url: '/system/notice',
    method: 'put',
    data: data
  })
}

// 删除公告
export function delNotice(noticeId) {
  return request({
    url: '/system/notice/' + noticeId,
    method: 'delete'
  })
}

// 发布公告（立即发布或定时发布）
export function publishNotice(data) {
  return request({
    url: '/system/notice/publish',
    method: 'put',
    data: data
  })
}

// 取消发布公告
export function cancelPublishNotice(noticeId) {
  return request({
    url: '/system/notice/cancel/' + noticeId,
    method: 'put'
  })
}

// 查询待发布公告列表
export function listWaitPublishNotices() {
  return request({
    url: '/system/notice/waitPublish',
    method: 'get'
  })
}

// 查询已过期公告列表
export function listExpiredNotices() {
  return request({
    url: '/system/notice/expired',
    method: 'get'
  })
}
