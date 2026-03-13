package com.ruoyi.quartz.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 公告定时任务
 * 用于处理公告的定时发布和自动过期
 * 
 * @author ruoyi
 */
@Component("noticeTask")
public class NoticeTask
{
    private static final Logger logger = LoggerFactory.getLogger(NoticeTask.class);

    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 处理待发布的公告
     * 将发布时间已到但未发布的公告状态更新为已发布
     * 
     * 建议执行频率：每分钟执行一次
     * cron表达式：0 0/1 * * * ?
     */
    public void processWaitPublishNotices()
    {
        logger.info("开始执行定时任务：处理待发布的公告");
        try
        {
            noticeService.processWaitPublishNotices();
            logger.info("定时任务执行完成：处理待发布的公告");
        }
        catch (Exception e)
        {
            logger.error("定时任务执行失败：处理待发布的公告", e);
        }
    }

    /**
     * 处理已过期的公告
     * 将结束时间已到但状态未更新的公告标记为已过期
     * 
     * 建议执行频率：每分钟执行一次
     * cron表达式：0 0/1 * * * ?
     */
    public void processExpiredNotices()
    {
        logger.info("开始执行定时任务：处理已过期的公告");
        try
        {
            noticeService.processExpiredNotices();
            logger.info("定时任务执行完成：处理已过期的公告");
        }
        catch (Exception e)
        {
            logger.error("定时任务执行失败：处理已过期的公告", e);
        }
    }

    /**
     * 综合处理公告定时任务
     * 同时处理待发布和已过期的公告
     * 
     * 建议执行频率：每分钟执行一次
     * cron表达式：0 0/1 * * * ?
     */
    public void processNoticeSchedule()
    {
        logger.info("开始执行定时任务：处理公告定时发布和过期");
        try
        {
            // 处理待发布的公告
            noticeService.processWaitPublishNotices();
            // 处理已过期的公告
            noticeService.processExpiredNotices();
            logger.info("定时任务执行完成：处理公告定时发布和过期");
        }
        catch (Exception e)
        {
            logger.error("定时任务执行失败：处理公告定时发布和过期", e);
        }
    }
}
