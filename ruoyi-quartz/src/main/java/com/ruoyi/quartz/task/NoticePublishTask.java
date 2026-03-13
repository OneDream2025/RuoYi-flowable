package com.ruoyi.quartz.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 通知公告定时发布任务
 * 
 * @author ruoyi
 */
@Component("noticePublishTask")
public class NoticePublishTask
{
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 处理待发布的公告
     */
    public void processPendingNotices()
    {
        int count = noticeService.processPendingNotices();
        if (count > 0)
        {
            System.out.println("定时任务执行：已发布 " + count + " 条公告");
        }
    }

    /**
     * 处理已过期的公告
     */
    public void processExpiredNotices()
    {
        int count = noticeService.processExpiredNotices();
        if (count > 0)
        {
            System.out.println("定时任务执行：已过期 " + count + " 条公告");
        }
    }

    /**
     * 处理所有定时公告（发布和过期）
     */
    public void processAllScheduledNotices()
    {
        processPendingNotices();
        processExpiredNotices();
    }
}
