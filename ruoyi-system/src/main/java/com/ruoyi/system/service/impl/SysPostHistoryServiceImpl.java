package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.domain.dto.PostChangeDetail;
import com.ruoyi.system.mapper.SysPostHistoryMapper;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.service.ISysPostHistoryService;

/**
 * 岗位变更历史 服务层处理
 */
@Service
public class SysPostHistoryServiceImpl implements ISysPostHistoryService
{
    private static final Map<String, String> FIELD_LABELS = new LinkedHashMap<>();
    static
    {
        FIELD_LABELS.put("postCode", "岗位编码");
        FIELD_LABELS.put("postName", "岗位名称");
        FIELD_LABELS.put("postSort", "岗位排序");
        FIELD_LABELS.put("status", "状态");
        FIELD_LABELS.put("remark", "备注");
    }

    private static final Map<String, String> STATUS_MAP = new HashMap<>();
    static
    {
        STATUS_MAP.put("0", "正常");
        STATUS_MAP.put("1", "停用");
    }

    @Autowired
    private SysPostHistoryMapper postHistoryMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Override
    public List<SysPostHistory> selectPostHistoryList(SysPostHistory postHistory)
    {
        return postHistoryMapper.selectPostHistoryList(postHistory);
    }

    @Override
    public SysPostHistory selectPostHistoryById(Long historyId)
    {
        return postHistoryMapper.selectPostHistoryById(historyId);
    }

    @Override
    public List<SysPostHistory> selectPostHistoryByPostId(Long postId)
    {
        return postHistoryMapper.selectPostHistoryByPostId(postId);
    }

    @Override
    @Transactional
    public void saveHistory(SysPost post, String changeType, SysPost beforePost)
    {
        SysPostHistory history = new SysPostHistory();
        history.setPostId(post.getPostId());
        history.setPostCode(post.getPostCode());
        history.setPostName(post.getPostName());
        history.setPostSort(post.getPostSort());
        history.setStatus(post.getStatus());
        history.setRemark(post.getRemark());
        history.setChangeType(changeType);

        Integer maxVersion = postHistoryMapper.selectMaxVersionByPostId(post.getPostId());
        history.setVersion(maxVersion == null ? 1 : maxVersion + 1);

        String afterData = JSON.toJSONString(post);
        history.setAfterData(afterData);

        if (beforePost != null)
        {
            String beforeData = JSON.toJSONString(beforePost);
            history.setBeforeData(beforeData);
            List<PostChangeDetail> changes = comparePosts(beforePost, post);
            history.setChangeContent(JSON.toJSONString(changes));
        }
        else
        {
            history.setBeforeData(null);
            history.setChangeContent("[]");
        }

        try
        {
            history.setOperBy(SecurityUtils.getUsername());
        }
        catch (Exception e)
        {
            history.setOperBy("system");
        }

        history.setOperTime(new Date());
        try
        {
            history.setOperIp(IpUtils.getIpAddr());
        }
        catch (Exception e)
        {
            history.setOperIp("");
        }

        postHistoryMapper.insertPostHistory(history);
    }

    private List<PostChangeDetail> comparePosts(SysPost before, SysPost after)
    {
        List<PostChangeDetail> changes = new ArrayList<>();

        if (!Objects.equals(before.getPostCode(), after.getPostCode()))
        {
            changes.add(new PostChangeDetail("postCode", "岗位编码",
                    before.getPostCode(), after.getPostCode()));
        }
        if (!Objects.equals(before.getPostName(), after.getPostName()))
        {
            changes.add(new PostChangeDetail("postName", "岗位名称",
                    before.getPostName(), after.getPostName()));
        }
        if (!Objects.equals(before.getPostSort(), after.getPostSort()))
        {
            changes.add(new PostChangeDetail("postSort", "岗位排序",
                    String.valueOf(before.getPostSort()), String.valueOf(after.getPostSort())));
        }
        if (!Objects.equals(before.getStatus(), after.getStatus()))
        {
            changes.add(new PostChangeDetail("status", "状态",
                    STATUS_MAP.getOrDefault(before.getStatus(), before.getStatus()),
                    STATUS_MAP.getOrDefault(after.getStatus(), after.getStatus())));
        }
        if (!Objects.equals(before.getRemark(), after.getRemark()))
        {
            changes.add(new PostChangeDetail("remark", "备注",
                    before.getRemark(), after.getRemark()));
        }

        return changes;
    }

    @Override
    public List<PostChangeDetail> getChangeDetails(Long historyId)
    {
        SysPostHistory history = selectPostHistoryById(historyId);
        if (history == null || history.getChangeContent() == null)
        {
            return new ArrayList<>();
        }
        return JSON.parseArray(history.getChangeContent(), PostChangeDetail.class);
    }

    @Override
    public Map<String, Object> compareHistory(Long historyId1, Long historyId2)
    {
        SysPostHistory h1 = selectPostHistoryById(historyId1);
        SysPostHistory h2 = selectPostHistoryById(historyId2);

        if (h1 == null || h2 == null)
        {
            throw new ServiceException("历史记录不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("history1", h1);
        result.put("history2", h2);

        SysPost p1 = JSON.parseObject(h1.getAfterData(), SysPost.class);
        SysPost p2 = JSON.parseObject(h2.getAfterData(), SysPost.class);

        result.put("diff", comparePosts(p1, p2));
        return result;
    }

    @Override
    @Transactional
    public int rollbackPost(Long historyId)
    {
        SysPostHistory history = selectPostHistoryById(historyId);
        if (history == null)
        {
            throw new ServiceException("历史记录不存在");
        }

        SysPost rollbackPost = JSON.parseObject(history.getAfterData(), SysPost.class);

        SysPost currentPost = postMapper.selectPostById(history.getPostId());
        if (currentPost == null)
        {
            throw new ServiceException("当前岗位不存在，无法回滚");
        }

        rollbackPost.setUpdateBy(SecurityUtils.getUsername());
        int result = postMapper.updatePost(rollbackPost);

        if (result > 0)
        {
            saveHistory(rollbackPost, "2", currentPost);
        }

        return result;
    }
}
