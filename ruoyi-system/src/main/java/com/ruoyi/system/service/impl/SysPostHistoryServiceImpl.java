package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.domain.SysPostChangeDetail;
import com.ruoyi.system.mapper.SysPostHistoryMapper;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.service.ISysPostHistoryService;

/**
 * 岗位变更历史记录 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysPostHistoryServiceImpl implements ISysPostHistoryService
{
    @Autowired
    private SysPostHistoryMapper postHistoryMapper;

    @Autowired
    private SysPostMapper postMapper;

    /**
     * 变更类型常量
     */
    private static final String CHANGE_TYPE_INSERT = "1";
    private static final String CHANGE_TYPE_UPDATE = "2";
    private static final String CHANGE_TYPE_DELETE = "3";
    private static final String CHANGE_TYPE_ROLLBACK = "4";

    /**
     * 查询岗位变更历史列表
     */
    @Override
    public List<SysPostHistory> selectPostHistoryList(SysPostHistory postHistory)
    {
        return postHistoryMapper.selectPostHistoryList(postHistory);
    }

    /**
     * 根据岗位ID查询变更历史列表
     */
    @Override
    public List<SysPostHistory> selectPostHistoryByPostId(Long postId)
    {
        return postHistoryMapper.selectPostHistoryByPostId(postId);
    }

    /**
     * 查询岗位变更历史详细
     */
    @Override
    public SysPostHistory selectPostHistoryById(Long historyId)
    {
        SysPostHistory history = postHistoryMapper.selectPostHistoryById(historyId);
        if (history != null)
        {
            List<SysPostChangeDetail> details = postHistoryMapper.selectChangeDetailByHistoryId(historyId);
            history.setChangeDetails(details);
        }
        return history;
    }

    /**
     * 记录岗位新增历史
     */
    @Override
    @Transactional
    public int recordInsertHistory(SysPost post, String changeBy)
    {
        SysPostHistory history = new SysPostHistory();
        history.setPostId(post.getPostId());
        history.setPostCode(post.getPostCode());
        history.setPostName(post.getPostName());
        history.setPostSort(post.getPostSort());
        history.setStatus(post.getStatus());
        history.setRemark(post.getRemark());
        history.setVersion(1);
        history.setChangeType(CHANGE_TYPE_INSERT);
        history.setChangeBy(changeBy);

        return postHistoryMapper.insertPostHistory(history);
    }

    /**
     * 记录岗位修改历史
     */
    @Override
    @Transactional
    public int recordUpdateHistory(SysPost oldPost, SysPost newPost, String changeBy)
    {
        // 获取当前最大版本号
        Integer maxVersion = getMaxVersion(oldPost.getPostId());
        int newVersion = (maxVersion == null ? 1 : maxVersion) + 1;

        // 创建历史记录
        SysPostHistory history = new SysPostHistory();
        history.setPostId(newPost.getPostId());
        history.setPostCode(newPost.getPostCode());
        history.setPostName(newPost.getPostName());
        history.setPostSort(newPost.getPostSort());
        history.setStatus(newPost.getStatus());
        history.setRemark(newPost.getRemark());
        history.setVersion(newVersion);
        history.setChangeType(CHANGE_TYPE_UPDATE);
        history.setChangeBy(changeBy);

        int result = postHistoryMapper.insertPostHistory(history);

        // 记录变更详情
        List<SysPostChangeDetail> details = compareAndCreateDetails(oldPost, newPost, history.getHistoryId());
        if (!details.isEmpty())
        {
            postHistoryMapper.batchInsertChangeDetail(details);
        }

        return result;
    }

    /**
     * 记录岗位删除历史
     */
    @Override
    @Transactional
    public int recordDeleteHistory(SysPost post, String changeBy)
    {
        Integer maxVersion = getMaxVersion(post.getPostId());
        int newVersion = (maxVersion == null ? 1 : maxVersion) + 1;

        SysPostHistory history = new SysPostHistory();
        history.setPostId(post.getPostId());
        history.setPostCode(post.getPostCode());
        history.setPostName(post.getPostName());
        history.setPostSort(post.getPostSort());
        history.setStatus(post.getStatus());
        history.setRemark(post.getRemark());
        history.setVersion(newVersion);
        history.setChangeType(CHANGE_TYPE_DELETE);
        history.setChangeBy(changeBy);

        return postHistoryMapper.insertPostHistory(history);
    }

    /**
     * 记录岗位回滚历史
     */
    @Override
    @Transactional
    public int recordRollbackHistory(SysPost post, String changeBy)
    {
        Integer maxVersion = getMaxVersion(post.getPostId());
        int newVersion = (maxVersion == null ? 1 : maxVersion) + 1;

        SysPostHistory history = new SysPostHistory();
        history.setPostId(post.getPostId());
        history.setPostCode(post.getPostCode());
        history.setPostName(post.getPostName());
        history.setPostSort(post.getPostSort());
        history.setStatus(post.getStatus());
        history.setRemark(post.getRemark());
        history.setVersion(newVersion);
        history.setChangeType(CHANGE_TYPE_ROLLBACK);
        history.setChangeBy(changeBy);

        return postHistoryMapper.insertPostHistory(history);
    }

    /**
     * 获取变更详情列表
     */
    @Override
    public List<SysPostChangeDetail> selectChangeDetailByHistoryId(Long historyId)
    {
        return postHistoryMapper.selectChangeDetailByHistoryId(historyId);
    }

    /**
     * 对比两个历史版本
     */
    @Override
    public Map<String, Object> compareHistoryVersions(Long historyId1, Long historyId2)
    {
        Map<String, Object> result = new HashMap<>();
        
        SysPostHistory history1 = selectPostHistoryById(historyId1);
        SysPostHistory history2 = selectPostHistoryById(historyId2);
        
        if (history1 == null || history2 == null)
        {
            return result;
        }

        result.put("history1", history1);
        result.put("history2", history2);

        // 对比字段差异
        List<Map<String, Object>> differences = new ArrayList<>();
        
        // 对比岗位编码
        if (!StringUtils.equals(history1.getPostCode(), history2.getPostCode()))
        {
            Map<String, Object> diff = new HashMap<>();
            diff.put("fieldName", "postCode");
            diff.put("fieldLabel", "岗位编码");
            diff.put("oldValue", history1.getPostCode());
            diff.put("newValue", history2.getPostCode());
            differences.add(diff);
        }

        // 对比岗位名称
        if (!StringUtils.equals(history1.getPostName(), history2.getPostName()))
        {
            Map<String, Object> diff = new HashMap<>();
            diff.put("fieldName", "postName");
            diff.put("fieldLabel", "岗位名称");
            diff.put("oldValue", history1.getPostName());
            diff.put("newValue", history2.getPostName());
            differences.add(diff);
        }

        // 对比岗位排序
        if (!StringUtils.equals(String.valueOf(history1.getPostSort()), String.valueOf(history2.getPostSort())))
        {
            Map<String, Object> diff = new HashMap<>();
            diff.put("fieldName", "postSort");
            diff.put("fieldLabel", "岗位排序");
            diff.put("oldValue", history1.getPostSort());
            diff.put("newValue", history2.getPostSort());
            differences.add(diff);
        }

        // 对比状态
        if (!StringUtils.equals(history1.getStatus(), history2.getStatus()))
        {
            Map<String, Object> diff = new HashMap<>();
            diff.put("fieldName", "status");
            diff.put("fieldLabel", "状态");
            diff.put("oldValue", history1.getStatus());
            diff.put("newValue", history2.getStatus());
            differences.add(diff);
        }

        // 对比备注
        if (!StringUtils.equals(history1.getRemark(), history2.getRemark()))
        {
            Map<String, Object> diff = new HashMap<>();
            diff.put("fieldName", "remark");
            diff.put("fieldLabel", "备注");
            diff.put("oldValue", history1.getRemark());
            diff.put("newValue", history2.getRemark());
            differences.add(diff);
        }

        result.put("differences", differences);
        return result;
    }

    /**
     * 回滚到指定历史版本
     */
    @Override
    @Transactional
    public int rollbackToVersion(Long historyId, String changeBy)
    {
        SysPostHistory history = selectPostHistoryById(historyId);
        if (history == null)
        {
            throw new RuntimeException("历史版本不存在");
        }

        // 查询当前岗位信息
        SysPost currentPost = postMapper.selectPostById(history.getPostId());
        if (currentPost == null)
        {
            throw new RuntimeException("岗位不存在或已被删除");
        }

        // 创建回滚后的岗位信息
        SysPost rollbackPost = new SysPost();
        rollbackPost.setPostId(history.getPostId());
        rollbackPost.setPostCode(history.getPostCode());
        rollbackPost.setPostName(history.getPostName());
        rollbackPost.setPostSort(history.getPostSort());
        rollbackPost.setStatus(history.getStatus());
        rollbackPost.setRemark(history.getRemark());
        rollbackPost.setUpdateBy(changeBy);

        // 更新岗位信息
        int result = postMapper.updatePost(rollbackPost);

        // 记录回滚历史
        recordRollbackHistory(rollbackPost, changeBy);

        return result;
    }

    /**
     * 获取岗位当前最大版本号
     */
    @Override
    public Integer getMaxVersion(Long postId)
    {
        return postHistoryMapper.selectMaxVersionByPostId(postId);
    }

    /**
     * 对比并创建变更详情
     */
    private List<SysPostChangeDetail> compareAndCreateDetails(SysPost oldPost, SysPost newPost, Long historyId)
    {
        List<SysPostChangeDetail> details = new ArrayList<>();

        // 对比岗位编码
        if (!StringUtils.equals(oldPost.getPostCode(), newPost.getPostCode()))
        {
            SysPostChangeDetail detail = new SysPostChangeDetail();
            detail.setHistoryId(historyId);
            detail.setPostId(newPost.getPostId());
            detail.setFieldName("postCode");
            detail.setFieldLabel("岗位编码");
            detail.setOldValue(oldPost.getPostCode());
            detail.setNewValue(newPost.getPostCode());
            details.add(detail);
        }

        // 对比岗位名称
        if (!StringUtils.equals(oldPost.getPostName(), newPost.getPostName()))
        {
            SysPostChangeDetail detail = new SysPostChangeDetail();
            detail.setHistoryId(historyId);
            detail.setPostId(newPost.getPostId());
            detail.setFieldName("postName");
            detail.setFieldLabel("岗位名称");
            detail.setOldValue(oldPost.getPostName());
            detail.setNewValue(newPost.getPostName());
            details.add(detail);
        }

        // 对比岗位排序
        if (oldPost.getPostSort() != null && newPost.getPostSort() != null 
            && !oldPost.getPostSort().equals(newPost.getPostSort()))
        {
            SysPostChangeDetail detail = new SysPostChangeDetail();
            detail.setHistoryId(historyId);
            detail.setPostId(newPost.getPostId());
            detail.setFieldName("postSort");
            detail.setFieldLabel("岗位排序");
            detail.setOldValue(String.valueOf(oldPost.getPostSort()));
            detail.setNewValue(String.valueOf(newPost.getPostSort()));
            details.add(detail);
        }

        // 对比状态
        if (!StringUtils.equals(oldPost.getStatus(), newPost.getStatus()))
        {
            SysPostChangeDetail detail = new SysPostChangeDetail();
            detail.setHistoryId(historyId);
            detail.setPostId(newPost.getPostId());
            detail.setFieldName("status");
            detail.setFieldLabel("状态");
            detail.setOldValue(oldPost.getStatus());
            detail.setNewValue(newPost.getStatus());
            details.add(detail);
        }

        // 对比备注
        if (!StringUtils.equals(oldPost.getRemark(), newPost.getRemark()))
        {
            SysPostChangeDetail detail = new SysPostChangeDetail();
            detail.setHistoryId(historyId);
            detail.setPostId(newPost.getPostId());
            detail.setFieldName("remark");
            detail.setFieldLabel("备注");
            detail.setOldValue(oldPost.getRemark());
            detail.setNewValue(newPost.getRemark());
            details.add(detail);
        }

        return details;
    }
}