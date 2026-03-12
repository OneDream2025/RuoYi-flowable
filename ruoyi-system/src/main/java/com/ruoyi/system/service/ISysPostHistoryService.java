package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysPostHistory;

/**
 * 岗位历史 服务层
 * 
 * @author ruoyi
 */
public interface ISysPostHistoryService
{
    /**
     * 查询岗位历史列表
     * 
     * @param postHistory 岗位历史
     * @return 岗位历史集合
     */
    public List<SysPostHistory> selectPostHistoryList(SysPostHistory postHistory);

    /**
     * 通过岗位ID查询岗位历史列表
     * 
     * @param postId 岗位ID
     * @return 岗位历史集合
     */
    public List<SysPostHistory> selectPostHistoryByPostId(Long postId);

    /**
     * 通过历史ID查询岗位历史
     * 
     * @param historyId 历史ID
     * @return 岗位历史
     */
    public SysPostHistory selectPostHistoryById(Long historyId);

    /**
     * 新增岗位历史
     * 
     * @param postHistory 岗位历史
     * @return 结果
     */
    public int insertPostHistory(SysPostHistory postHistory);

    /**
     * 记录岗位变更历史
     * 
     * @param post 岗位信息
     * @param operationType 操作类型（1新增 2修改 3删除）
     * @param changeReason 变更原因
     * @return 结果
     */
    public int recordPostHistory(SysPost post, String operationType, String changeReason);

    /**
     * 对比两个版本的差异
     * 
     * @param historyId1 历史ID1
     * @param historyId2 历史ID2
     * @return 差异结果
     */
    public Map<String, Map<String, Object>> comparePostVersions(Long historyId1, Long historyId2);

    /**
     * 岗位回滚
     * 
     * @param historyId 历史ID
     * @return 结果
     */
    public int rollbackPost(Long historyId);
}
