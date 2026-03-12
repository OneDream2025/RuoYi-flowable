package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.domain.dto.PostChangeDetail;

/**
 * 岗位变更历史 服务层
 */
public interface ISysPostHistoryService
{
    /**
     * 查询岗位变更历史列表
     * 
     * @param postHistory 岗位变更历史
     * @return 岗位变更历史集合
     */
    public List<SysPostHistory> selectPostHistoryList(SysPostHistory postHistory);

    /**
     * 通过历史记录ID查询岗位变更历史
     * 
     * @param historyId 历史记录ID
     * @return 岗位变更历史
     */
    public SysPostHistory selectPostHistoryById(Long historyId);

    /**
     * 通过岗位ID查询变更历史列表
     * 
     * @param postId 岗位ID
     * @return 岗位变更历史集合
     */
    public List<SysPostHistory> selectPostHistoryByPostId(Long postId);

    /**
     * 保存岗位变更历史
     * 
     * @param post 岗位信息
     * @param changeType 变更类型（1新增 2修改 3删除）
     * @param beforePost 变更前的岗位信息
     */
    public void saveHistory(SysPost post, String changeType, SysPost beforePost);

    /**
     * 获取变更详情列表
     * 
     * @param historyId 历史记录ID
     * @return 变更详情列表
     */
    public List<PostChangeDetail> getChangeDetails(Long historyId);

    /**
     * 对比两个历史版本
     * 
     * @param historyId1 历史记录ID1
     * @param historyId2 历史记录ID2
     * @return 对比结果
     */
    public Map<String, Object> compareHistory(Long historyId1, Long historyId2);

    /**
     * 回滚岗位信息到指定历史版本
     * 
     * @param historyId 历史记录ID
     * @return 结果
     */
    public int rollbackPost(Long historyId);
}
