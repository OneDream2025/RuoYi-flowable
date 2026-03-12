package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysPostHistory;
import com.ruoyi.system.service.ISysPostHistoryService;

/**
 * 岗位历史信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/post/history")
public class SysPostHistoryController extends BaseController
{
    @Autowired
    private ISysPostHistoryService postHistoryService;

    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping("/list")
    public TableDataInfo list(SysPostHistory postHistory)
    {
        startPage();
        List<SysPostHistory> list = postHistoryService.selectPostHistoryList(postHistory);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping("/{historyId}")
    public AjaxResult getInfo(@PathVariable Long historyId)
    {
        return success(postHistoryService.selectPostHistoryById(historyId));
    }

    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping("/post/{postId}")
    public AjaxResult getHistoryByPostId(@PathVariable Long postId)
    {
        return success(postHistoryService.selectPostHistoryByPostId(postId));
    }

    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping("/compare/{historyId1}/{historyId2}")
    public AjaxResult compareVersions(@PathVariable Long historyId1, @PathVariable Long historyId2)
    {
        Map<String, Map<String, Object>> result = postHistoryService.comparePostVersions(historyId1, historyId2);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('system:post:rollback')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PostMapping("/rollback/{historyId}")
    public AjaxResult rollback(@PathVariable Long historyId)
    {
        return toAjax(postHistoryService.rollbackPost(historyId));
    }
}
