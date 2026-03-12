package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.domain.SysPostChangeDetail;

/**
 * 岗位变更历史记录 服务层
 * 
 * @author ruoyi
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
     * 根据岗位ID查询变更历史列表
     * 
     * @param postId 岗位ID
     * @return 岗位变更历史集合
     */
    public List<SysPostHistory> selectPostHistoryByPostId(Long postId);

    /**
     * 查询岗位变更历史详细
     * 
     * @param historyId 历史记录ID
     * @return 岗位变更历史
     */
    public SysPostHistory selectPostHistoryById(Long historyId);

    /**
     * 记录岗位新增历史
     * 
     * @param post 岗位信息
     * @param changeBy 变更人
     * @return 结果
     */
    public int recordInsertHistory(SysPost post, String changeBy);

    /**
     * 记录岗位修改历史
     * 
     * @param oldPost 修改前岗位信息
     * @param newPost 修改后岗位信息
     * @param changeBy 变更人
     * @return 结果
     */
    public int recordUpdateHistory(SysPost oldPost, SysPost newPost, String changeBy);

    /**
     * 记录岗位删除历史
     * 
     * @param post 岗位信息
     * @param changeBy 变更人
     * @return 结果
     */
    public int recordDeleteHistory(SysPost post, String changeBy);

    /**
     * 记录岗位回滚历史
     * 
     * @param post 岗位信息
     * @param changeBy 变更人
     * @return 结果
     */
    public int recordRollbackHistory(SysPost post, String changeBy);

    /**
     * 获取变更详情列表
     * 
     * @param historyId 历史记录ID
     * @return 变更详情列表
     */
    public List<SysPostChangeDetail> selectChangeDetailByHistoryId(Long historyId);

    /**
     * 对比两个历史版本
     * 
     * @param historyId1 历史记录ID1
     * @param historyId2 历史记录ID2
     * @return 对比结果
     */
    public Map<String, Object> compareHistoryVersions(Long historyId1, Long historyId2);

    /**
     * 回滚到指定历史版本
     * 
     * @param historyId 历史记录ID
     * @param changeBy 变更人
     * @return 结果
     */
    public int rollbackToVersion(Long historyId, String changeBy);

    /**
     * 获取岗位当前最大版本号
     * 
     * @param postId 岗位ID
     * @return 版本号
     */
    public Integer getMaxVersion(Long postId);
}
