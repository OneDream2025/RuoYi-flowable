package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.domain.SysPostChangeDetail;
import com.ruoyi.system.service.ISysPostHistoryService;

/**
 * 岗位变更历史记录操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/post/history")
public class SysPostHistoryController extends BaseController
{
    @Autowired
    private ISysPostHistoryService postHistoryService;

    /**
     * 查询岗位变更历史列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:history:query')")
    @GetMapping("/list")
    public TableDataInfo list(SysPostHistory postHistory)
    {
        startPage();
        List<SysPostHistory> list = postHistoryService.selectPostHistoryList(postHistory);
        return getDataTable(list);
    }

    /**
     * 根据岗位ID查询变更历史列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:history:query')")
    @GetMapping("/post/{postId}")
    public AjaxResult getHistoryByPostId(@PathVariable Long postId)
    {
        List<SysPostHistory> list = postHistoryService.selectPostHistoryByPostId(postId);
        return success(list);
    }

    /**
     * 获取岗位变更历史详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:post:history:query')")
    @GetMapping(value = "/{historyId}")
    public AjaxResult getInfo(@PathVariable Long historyId)
    {
        return success(postHistoryService.selectPostHistoryById(historyId));
    }

    /**
     * 获取变更详情列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:history:query')")
    @GetMapping("/detail/{historyId}")
    public AjaxResult getChangeDetail(@PathVariable Long historyId)
    {
        List<SysPostChangeDetail> details = postHistoryService.selectChangeDetailByHistoryId(historyId);
        return success(details);
    }

    /**
     * 对比两个历史版本
     */
    @PreAuthorize("@ss.hasPermi('system:post:history:compare')")
    @PostMapping("/compare")
    public AjaxResult compareVersions(@RequestBody Map<String, Long> params)
    {
        Long historyId1 = params.get("historyId1");
        Long historyId2 = params.get("historyId2");
        
        if (historyId1 == null || historyId2 == null)
        {
            return error("参数错误：需要传入两个历史版本ID");
        }
        
        Map<String, Object> result = postHistoryService.compareHistoryVersions(historyId1, historyId2);
        return success(result);
    }

    /**
     * 回滚到指定历史版本
     */
    @PreAuthorize("@ss.hasPermi('system:post:history:rollback')")
    @Log(title = "岗位变更历史", businessType = BusinessType.UPDATE)
    @PostMapping("/rollback/{historyId}")
    public AjaxResult rollback(@PathVariable Long historyId)
    {
        try
        {
            int result = postHistoryService.rollbackToVersion(historyId, getUsername());
            return toAjax(result);
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }
}
