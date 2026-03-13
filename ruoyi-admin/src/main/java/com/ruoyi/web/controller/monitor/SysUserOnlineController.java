package com.ruoyi.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.domain.SysUserOnlineQuery;
import com.ruoyi.system.service.ISysUserOnlineService;

/**
 * 在线用户监控
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController
{
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询在线用户列表（支持多条件筛选和排序）
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUserOnlineQuery query)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        
        for (String key : keys)
        {
            LoginUser user = redisCache.getCacheObject(key);
            SysUserOnline online = userOnlineService.loginUserToUserOnline(user);
            if (online != null && userOnlineService.matchesQuery(online, query))
            {
                userOnlineList.add(online);
            }
        }
        
        // 排序
        if (StringUtils.isNotEmpty(query.getOrderByColumn()))
        {
            sortUserOnlineList(userOnlineList, query);
        }
        else
        {
            // 默认按登录时间倒序
            Collections.reverse(userOnlineList);
        }
        
        return getDataTable(userOnlineList);
    }

    /**
     * 导出在线用户列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:export')")
    @Log(title = "在线用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUserOnlineQuery query)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        
        for (String key : keys)
        {
            LoginUser user = redisCache.getCacheObject(key);
            SysUserOnline online = userOnlineService.loginUserToUserOnline(user);
            if (online != null && userOnlineService.matchesQuery(online, query))
            {
                // 计算会话时长
                if (online.getLoginTime() != null)
                {
                    online.setSessionDuration(formatDuration(System.currentTimeMillis() - online.getLoginTime()));
                }
                userOnlineList.add(online);
            }
        }
        
        // 排序
        if (StringUtils.isNotEmpty(query.getOrderByColumn()))
        {
            sortUserOnlineList(userOnlineList, query);
        }
        else
        {
            Collections.reverse(userOnlineList);
        }
        
        ExcelUtil<SysUserOnline> util = new ExcelUtil<>(SysUserOnline.class);
        util.exportExcel(response, userOnlineList, "在线用户数据");
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId)
    {
        redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return success();
    }

    /**
     * 对在线用户列表进行排序
     * 
     * @param list 在线用户列表
     * @param query 查询条件（包含排序信息）
     */
    private void sortUserOnlineList(List<SysUserOnline> list, SysUserOnlineQuery query)
    {
        if (StringUtils.isEmpty(query.getOrderByColumn()))
        {
            return;
        }
        
        Comparator<SysUserOnline> comparator = null;
        switch (query.getOrderByColumn())
        {
            case "loginTime":
                comparator = Comparator.comparing(SysUserOnline::getLoginTime, 
                    Comparator.nullsLast(Long::compareTo));
                break;
            case "userName":
                comparator = Comparator.comparing(SysUserOnline::getUserName, 
                    Comparator.nullsLast(String::compareTo));
                break;
            case "ipaddr":
                comparator = Comparator.comparing(SysUserOnline::getIpaddr, 
                    Comparator.nullsLast(String::compareTo));
                break;
            case "deptName":
                comparator = Comparator.comparing(SysUserOnline::getDeptName, 
                    Comparator.nullsLast(String::compareTo));
                break;
            case "sessionDuration":
                comparator = Comparator.comparing(
                    o -> o.getLoginTime() != null ? System.currentTimeMillis() - o.getLoginTime() : null,
                    Comparator.nullsLast(Long::compareTo));
                break;
            default:
                return;
        }
        
        if (comparator != null)
        {
            if ("desc".equalsIgnoreCase(query.getIsAsc()))
            {
                comparator = comparator.reversed();
            }
            list.sort(comparator);
        }
    }

    /**
     * 格式化时长
     * 
     * @param millis 毫秒数
     * @return 格式化后的时长字符串
     */
    private String formatDuration(long millis)
    {
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis % (1000 * 60 * 60)) / (1000 * 60);
        if (hours > 0)
        {
            return hours + "小时" + minutes + "分钟";
        }
        else
        {
            return minutes + "分钟";
        }
    }
}
