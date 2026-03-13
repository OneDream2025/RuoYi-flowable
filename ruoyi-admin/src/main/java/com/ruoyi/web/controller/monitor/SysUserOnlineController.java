package com.ruoyi.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public TableDataInfo list(String ipaddr, String userName, Long deptId, Long roleId, String orderByColumn, String isAsc, HttpServletRequest request)
    {
        String beginTime = request.getParameter("params[beginTime]");
        String endTime = request.getParameter("params[endTime]");
        Long loginTimeBegin = StringUtils.isNotEmpty(beginTime) ? Long.parseLong(beginTime) : null;
        Long loginTimeEnd = StringUtils.isNotEmpty(endTime) ? Long.parseLong(endTime) : null;
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            LoginUser user = redisCache.getCacheObject(key);
            if (StringUtils.isNotNull(user) && StringUtils.isNotNull(user.getUser()))
            {
                SysUserOnline online = userOnlineService.loginUserToUserOnline(user);
                if (userOnlineService.matches(online, ipaddr, userName, deptId, roleId, loginTimeBegin, loginTimeEnd))
                {
                    userOnlineList.add(online);
                }
            }
        }
        sortUserOnlineList(userOnlineList, orderByColumn, isAsc);
        return getDataTable(userOnlineList);
    }

    private void sortUserOnlineList(List<SysUserOnline> list, String orderByColumn, String isAsc)
    {
        if (StringUtils.isEmpty(orderByColumn))
        {
            Collections.reverse(list);
            return;
        }
        Comparator<SysUserOnline> comparator = null;
        switch (orderByColumn)
        {
            case "userName":
                comparator = Comparator.comparing(SysUserOnline::getUserName);
                break;
            case "ipaddr":
                comparator = Comparator.comparing(SysUserOnline::getIpaddr);
                break;
            case "loginTime":
                comparator = Comparator.comparing(SysUserOnline::getLoginTime);
                break;
            case "sessionDuration":
                comparator = Comparator.comparingLong(online -> 
                    online.getExpireTime() != null && online.getLoginTime() != null 
                        ? online.getExpireTime() - online.getLoginTime() : 0);
                break;
            default:
                comparator = Comparator.comparing(SysUserOnline::getLoginTime);
                break;
        }
        if (comparator != null)
        {
            if ("desc".equals(isAsc) || "descending".equals(isAsc))
            {
                comparator = comparator.reversed();
            }
            list.sort(comparator);
        }
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
