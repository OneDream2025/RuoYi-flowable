package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysNotice;

/**
 * 公告 服务层
 * 
 * @author ruoyi
 */
public interface ISysNoticeService
{
    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 删除公告信息
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);
    
    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);

    /**
     * 发布公告（立即发布或设置定时发布）
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int publishNotice(SysNotice notice);

    /**
     * 取消发布（将已发布或待发布公告变为草稿）
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    public int cancelPublish(Long noticeId);

    /**
     * 处理待发布的公告（定时任务调用）
     * 将发布时间已到但未发布的公告状态更新为已发布
     */
    public void processWaitPublishNotices();

    /**
     * 处理已过期的公告（定时任务调用）
     * 将结束时间已到但状态未更新的公告标记为已过期
     */
    public void processExpiredNotices();

    /**
     * 查询待发布的公告列表
     * 
     * @return 公告集合
     */
    public List<SysNotice> selectWaitPublishNotices();

    /**
     * 查询已过期的公告列表
     * 
     * @return 公告集合
     */
    public List<SysNotice> selectExpiredNotices();
}
