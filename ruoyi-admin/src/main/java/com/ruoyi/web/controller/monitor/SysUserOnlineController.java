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
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysUserOnline;
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

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUserOnline query)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            LoginUser user = redisCache.getCacheObject(key);
            if (userOnlineService.matchesFilter(user, query))
            {
                SysUserOnline online = userOnlineService.loginUserToUserOnline(user);
                if (online != null)
                {
                    userOnlineList.add(online);
                }
            }
        }
        String orderByColumn = getOrderByColumn();
        String isAsc = getIsAsc();
        if (StringUtils.isNotEmpty(orderByColumn))
        {
            Comparator<SysUserOnline> comparator = null;
            switch (orderByColumn)
            {
                case "loginTime":
                    comparator = Comparator.comparing(SysUserOnline::getLoginTime, 
                            Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
                case "sessionDuration":
                    comparator = Comparator.comparing(SysUserOnline::getSessionDuration, 
                            Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
                case "userName":
                    comparator = Comparator.comparing(SysUserOnline::getUserName, 
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    break;
                case "ipaddr":
                    comparator = Comparator.comparing(SysUserOnline::getIpaddr, 
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    break;
                default:
                    break;
            }
            if (comparator != null)
            {
                if ("desc".equalsIgnoreCase(isAsc))
                {
                    comparator = comparator.reversed();
                }
                userOnlineList.sort(comparator);
            }
        }
        else
        {
            Collections.reverse(userOnlineList);
        }
        return getDataTable(userOnlineList);
    }

    private String getOrderByColumn()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        return pageDomain != null ? pageDomain.getOrderByColumn() : null;
    }

    private String getIsAsc()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        return pageDomain != null ? pageDomain.getIsAsc() : "asc";
    }

    @Log(title = "在线用户", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('monitor:online:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUserOnline query)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            LoginUser user = redisCache.getCacheObject(key);
            if (userOnlineService.matchesFilter(user, query))
            {
                SysUserOnline online = userOnlineService.loginUserToUserOnline(user);
                if (online != null)
                {
                    userOnlineList.add(online);
                }
            }
        }
        ExcelUtil<SysUserOnline> util = new ExcelUtil<SysUserOnline>(SysUserOnline.class);
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
}
