package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPostHistory;

/**
 * 岗位历史 数据层
 * 
 * @author ruoyi
 */
public interface SysPostHistoryMapper
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
     * 查询岗位最大版本号
     * 
     * @param postId 岗位ID
     * @return 最大版本号
     */
    public Integer selectMaxVersionByPostId(Long postId);
}
