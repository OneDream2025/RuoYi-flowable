package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.domain.dto.PostChangeDetail;
import com.ruoyi.system.service.ISysPostHistoryService;

/**
 * 岗位变更历史 Controller
 */
@RestController
@RequestMapping("/system/postHistory")
public class SysPostHistoryController extends BaseController
{
    @Autowired
    private ISysPostHistoryService postHistoryService;

    /**
     * 查询岗位变更历史列表
     */
    @PreAuthorize("@ss.hasPermi('system:postHistory:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysPostHistory postHistory)
    {
        startPage();
        List<SysPostHistory> list = postHistoryService.selectPostHistoryList(postHistory);
        return getDataTable(list);
    }

    /**
     * 获取岗位变更历史详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:postHistory:query')")
    @GetMapping(value = "/{historyId}")
    public AjaxResult getInfo(@PathVariable Long historyId)
    {
        return success(postHistoryService.selectPostHistoryById(historyId));
    }

    /**
     * 根据岗位ID获取变更历史列表
     */
    @PreAuthorize("@ss.hasPermi('system:postHistory:query')")
    @GetMapping("/post/{postId}")
    public AjaxResult getHistoryByPostId(@PathVariable Long postId)
    {
        return success(postHistoryService.selectPostHistoryByPostId(postId));
    }

    /**
     * 获取变更详情
     */
    @PreAuthorize("@ss.hasPermi('system:postHistory:query')")
    @GetMapping("/details/{historyId}")
    public AjaxResult getChangeDetails(@PathVariable Long historyId)
    {
        List<PostChangeDetail> details = postHistoryService.getChangeDetails(historyId);
        return success(details);
    }

    /**
     * 对比两个历史版本
     */
    @PreAuthorize("@ss.hasPermi('system:postHistory:query')")
    @GetMapping("/compare")
    public AjaxResult compareHistory(@RequestParam Long historyId1, @RequestParam Long historyId2)
    {
        Map<String, Object> result = postHistoryService.compareHistory(historyId1, historyId2);
        return success(result);
    }

    /**
     * 回滚岗位信息到指定历史版本
     */
    @PreAuthorize("@ss.hasPermi('system:postHistory:rollback')")
    @Log(title = "岗位变更历史", businessType = BusinessType.UPDATE)
    @PostMapping("/rollback/{historyId}")
    public AjaxResult rollback(@PathVariable Long historyId)
    {
        return toAjax(postHistoryService.rollbackPost(historyId));
    }
}
