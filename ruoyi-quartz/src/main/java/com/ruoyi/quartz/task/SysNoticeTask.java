package com.ruoyi.quartz.task;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;

@Component("sysNoticeTask")
public class SysNoticeTask
{
    @Autowired
    private ISysNoticeService noticeService;

    public void processNoticeStatus()
    {
        publishNotices();
        expireNotices();
    }

    private void publishNotices()
    {
        List<SysNotice> notices = noticeService.selectNoticesToPublish();
        for (SysNotice notice : notices)
        {
            notice.setStatus("0");
            notice.setUpdateBy("system");
            notice.setUpdateTime(new Date());
            noticeService.updateNotice(notice);
        }
    }

    private void expireNotices()
    {
        List<SysNotice> notices = noticeService.selectNoticesToExpire();
        for (SysNotice notice : notices)
        {
            notice.setStatus("1");
            notice.setUpdateBy("system");
            notice.setUpdateTime(new Date());
            noticeService.updateNotice(notice);
        }
    }
}
