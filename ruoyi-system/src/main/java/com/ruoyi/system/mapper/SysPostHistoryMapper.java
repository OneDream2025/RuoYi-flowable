package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.domain.SysPostChangeDetail;

/**
 * 岗位变更历史记录 数据层
 * 
 * @author ruoyi
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
     * 新增岗位变更历史
     * 
     * @param postHistory 岗位变更历史
     * @return 结果
     */
    public int insertPostHistory(SysPostHistory postHistory);

    /**
     * 批量新增岗位变更历史
     * 
     * @param postHistoryList 岗位变更历史列表
     * @return 结果
     */
    public int batchInsertPostHistory(List<SysPostHistory> postHistoryList);

    /**
     * 删除岗位变更历史
     * 
     * @param historyId 历史记录ID
     * @return 结果
     */
    public int deletePostHistoryById(Long historyId);

    /**
     * 根据岗位ID删除变更历史
     * 
     * @param postId 岗位ID
     * @return 结果
     */
    public int deletePostHistoryByPostId(Long postId);

    /**
     * 获取岗位当前最大版本号
     * 
     * @param postId 岗位ID
     * @return 版本号
     */
    public Integer selectMaxVersionByPostId(Long postId);

    /**
     * 新增变更详情
     * 
     * @param changeDetail 变更详情
     * @return 结果
     */
    public int insertChangeDetail(SysPostChangeDetail changeDetail);

    /**
     * 批量新增变更详情
     * 
     * @param changeDetails 变更详情列表
     * @return 结果
     */
    public int batchInsertChangeDetail(List<SysPostChangeDetail> changeDetails);

    /**
     * 根据历史记录ID查询变更详情
     * 
     * @param historyId 历史记录ID
     * @return 变更详情列表
     */
    public List<SysPostChangeDetail> selectChangeDetailByHistoryId(Long historyId);

    /**
     * 根据岗位ID查询变更详情列表
     * 
     * @param postId 岗位ID
     * @return 变更详情列表
     */
    public List<SysPostChangeDetail> selectChangeDetailByPostId(Long postId);
}
