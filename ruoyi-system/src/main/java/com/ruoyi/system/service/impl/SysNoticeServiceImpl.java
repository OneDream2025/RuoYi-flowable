package com.ruoyi.system.service.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(SysNoticeServiceImpl.class);

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
     * 处理待发布的公告
     * 将发布时间已到且状态为"待发布"的公告状态改为"正常"
     * 
     * @return 处理的公告数量
     */
    @Override
    public int processPendingNotices()
    {
        List<SysNotice> noticesToPublish = noticeMapper.selectNoticesToPublish();
        int count = 0;
        for (SysNotice notice : noticesToPublish)
        {
            notice.setStatus("0");
            noticeMapper.updateNotice(notice);
            count++;
            log.info("公告【{}】已自动发布", notice.getNoticeTitle());
        }
        return count;
    }

    /**
     * 处理已过期的公告
     * 将结束时间已过且状态为"正常"的公告状态改为"已过期"
     * 
     * @return 处理的公告数量
     */
    @Override
    public int processExpiredNotices()
    {
        List<SysNotice> noticesToExpire = noticeMapper.selectNoticesToExpire();
        int count = 0;
        for (SysNotice notice : noticesToExpire)
        {
            notice.setStatus("3");
            noticeMapper.updateNotice(notice);
            count++;
            log.info("公告【{}】已自动过期", notice.getNoticeTitle());
        }
        return count;
    }
}
