package com.ruoyi.system.service.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.mapper.SysPostHistoryMapper;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.service.ISysPostHistoryService;

/**
 * 岗位历史 服务层实现
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

    @Override
    public List<SysPostHistory> selectPostHistoryList(SysPostHistory postHistory)
    {
        return postHistoryMapper.selectPostHistoryList(postHistory);
    }

    @Override
    public List<SysPostHistory> selectPostHistoryByPostId(Long postId)
    {
        return postHistoryMapper.selectPostHistoryByPostId(postId);
    }

    @Override
    public SysPostHistory selectPostHistoryById(Long historyId)
    {
        return postHistoryMapper.selectPostHistoryById(historyId);
    }

    @Override
    public int insertPostHistory(SysPostHistory postHistory)
    {
        return postHistoryMapper.insertPostHistory(postHistory);
    }

    @Override
    public int recordPostHistory(SysPost post, String operationType, String changeReason)
    {
        SysPostHistory history = SysPostHistory.fromSysPost(post);
        history.setOperationType(operationType);
        history.setChangeReason(changeReason);
        
        Integer maxVersion = postHistoryMapper.selectMaxVersionByPostId(post.getPostId());
        if (maxVersion == null) {
            maxVersion = 0;
        }
        history.setVersion(maxVersion + 1);
        
        if (StringUtils.isEmpty(history.getCreateBy())) {
            history.setCreateBy(SecurityUtils.getUsername());
        }
        
        return postHistoryMapper.insertPostHistory(history);
    }

    @Override
    public Map<String, Map<String, Object>> comparePostVersions(Long historyId1, Long historyId2)
    {
        SysPostHistory history1 = postHistoryMapper.selectPostHistoryById(historyId1);
        SysPostHistory history2 = postHistoryMapper.selectPostHistoryById(historyId2);
        
        if (history1 == null || history2 == null) {
            throw new ServiceException("历史记录不存在");
        }
        
        Map<String, Map<String, Object>> result = new HashMap<>();
        result.put("version1", buildVersionInfo(history1));
        result.put("version2", buildVersionInfo(history2));
        result.put("changes", compareObjects(history1, history2));
        
        return result;
    }

    @Override
    @Transactional
    public int rollbackPost(Long historyId)
    {
        SysPostHistory history = postHistoryMapper.selectPostHistoryById(historyId);
        if (history == null) {
            throw new ServiceException("历史记录不存在");
        }
        
        SysPost currentPost = postMapper.selectPostById(history.getPostId());
        if (currentPost == null) {
            throw new ServiceException("岗位已被删除，无法回滚");
        }
        
        SysPost rollbackPost = history.toSysPost();
        rollbackPost.setUpdateBy(SecurityUtils.getUsername());
        
        int result = postMapper.updatePost(rollbackPost);
        if (result > 0) {
            recordPostHistory(rollbackPost, "2", "回滚到版本" + history.getVersion());
        }
        
        return result;
    }

    private Map<String, Object> buildVersionInfo(SysPostHistory history)
    {
        Map<String, Object> info = new HashMap<>();
        info.put("historyId", history.getHistoryId());
        info.put("version", history.getVersion());
        info.put("operationType", history.getOperationType());
        info.put("createBy", history.getCreateBy());
        info.put("createTime", history.getCreateTime());
        info.put("changeReason", history.getChangeReason());
        info.put("postCode", history.getPostCode());
        info.put("postName", history.getPostName());
        info.put("postSort", history.getPostSort());
        info.put("status", history.getStatus());
        info.put("remark", history.getRemark());
        return info;
    }

    private Map<String, Object> compareObjects(SysPostHistory obj1, SysPostHistory obj2)
    {
        Map<String, Object> changes = new HashMap<>();
        String[] fields = {"postCode", "postName", "postSort", "status", "remark"};
        
        for (String fieldName : fields) {
            try {
                Field field = SysPostHistory.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value1 = field.get(obj1);
                Object value2 = field.get(obj2);
                
                if (!StringUtils.equals(String.valueOf(value1), String.valueOf(value2))) {
                    Map<String, Object> change = new HashMap<>();
                    change.put("oldValue", value1);
                    change.put("newValue", value2);
                    changes.put(fieldName, change);
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        return changes;
    }
}
