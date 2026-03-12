package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPostHistory;

/**
 * 岗位变更历史 数据层
 */
public interface SysPostHistoryMapper
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
     * 查询岗位最新版本历史记录
     * 
     * @param postId 岗位ID
     * @return 岗位变更历史
     */
    public SysPostHistory selectLatestHistoryByPostId(Long postId);

    /**
     * 新增岗位变更历史
     * 
     * @param postHistory 岗位变更历史
     * @return 结果
     */
    public int insertPostHistory(SysPostHistory postHistory);

    /**
     * 批量删除岗位变更历史
     * 
     * @param historyIds 需要删除的历史记录ID
     * @return 结果
     */
    public int deletePostHistoryByIds(Long[] historyIds);

    /**
     * 查询岗位最大版本号
     * 
     * @param postId 岗位ID
     * @return 最大版本号
     */
    public Integer selectMaxVersionByPostId(Long postId);
}
