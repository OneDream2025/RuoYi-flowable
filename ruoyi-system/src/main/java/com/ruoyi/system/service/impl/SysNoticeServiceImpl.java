package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 公告 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        // 如果没有设置发布状态，默认为草稿
        if (notice.getPublishStatus() == null || notice.getPublishStatus().trim().isEmpty())
        {
            notice.setPublishStatus("0");
        }
        // 草稿状态默认关闭
        if ("0".equals(notice.getPublishStatus()))
        {
            notice.setStatus("1");
        }
        return noticeMapper.insertNotice(notice);
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }

    /**
     * 发布公告（立即发布或设置定时发布）
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int publishNotice(SysNotice notice)
    {
        Date now = new Date();
        Date publishTime = notice.getPublishTime();
        Date endTime = notice.getEndTime();

        // 如果没有设置发布时间，立即发布
        if (publishTime == null)
        {
            notice.setPublishTime(now);
            publishTime = now;
        }

        // 判断是立即发布还是定时发布
        if (publishTime.after(now))
        {
            // 发布时间在未来，设置为待发布状态
            notice.setPublishStatus("1");
            notice.setStatus("1"); // 关闭状态，等待定时任务发布
        }
        else
        {
            // 发布时间已到达或过去，立即发布
            notice.setPublishStatus("2");
            notice.setStatus("0"); // 正常状态
        }

        // 验证结束时间
        if (endTime != null && endTime.before(publishTime))
        {
            throw new RuntimeException("结束时间必须晚于发布时间");
        }

        if (notice.getNoticeId() != null)
        {
            return noticeMapper.updateNotice(notice);
        }
        else
        {
            return noticeMapper.insertNotice(notice);
        }
    }

    /**
     * 取消发布（将已发布或待发布公告变为草稿）
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int cancelPublish(Long noticeId)
    {
        SysNotice notice = new SysNotice();
        notice.setNoticeId(noticeId);
        notice.setPublishStatus("0"); // 草稿状态
        notice.setStatus("1"); // 关闭状态
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 处理待发布的公告（定时任务调用）
     * 将发布时间已到但未发布的公告状态更新为已发布
     */
    @Override
    public void processWaitPublishNotices()
    {
        List<SysNotice> waitPublishNotices = noticeMapper.selectWaitPublishNotices();
        if (waitPublishNotices != null && !waitPublishNotices.isEmpty())
        {
            List<Long> noticeIds = new ArrayList<>();
            for (SysNotice notice : waitPublishNotices)
            {
                noticeIds.add(notice.getNoticeId());
            }
            // 批量更新为已发布状态
            noticeMapper.updateNoticePublishStatus(
                noticeIds.toArray(new Long[0]), 
                "2", // 已发布
                "0"  // 正常状态
            );
        }
    }

    /**
     * 处理已过期的公告（定时任务调用）
     * 将结束时间已到但状态未更新的公告标记为已过期
     */
    @Override
    public void processExpiredNotices()
    {
        List<SysNotice> expiredNotices = noticeMapper.selectExpiredNotices();
        if (expiredNotices != null && !expiredNotices.isEmpty())
        {
            List<Long> noticeIds = new ArrayList<>();
            for (SysNotice notice : expiredNotices)
            {
                noticeIds.add(notice.getNoticeId());
            }
            // 批量更新为已过期状态
            noticeMapper.updateNoticePublishStatus(
                noticeIds.toArray(new Long[0]), 
                "3", // 已过期
                "1"  // 关闭状态
            );
        }
    }

    /**
     * 查询待发布的公告列表
     * 
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectWaitPublishNotices()
    {
        return noticeMapper.selectWaitPublishNotices();
    }

    /**
     * 查询已过期的公告列表
     * 
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectExpiredNotices()
    {
        return noticeMapper.selectExpiredNotices();
    }
}
