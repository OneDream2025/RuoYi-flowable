package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.service.ISysUserOnlineService;

/**
 * 在线用户 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysUserOnlineServiceImpl implements ISysUserOnlineService
{
    /**
     * 通过登录地址查询信息
     * 
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过用户名称查询信息
     * 
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user)
    {
        if (StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过登录地址/用户名称查询信息
     * 
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    @Override
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user)
    {
        if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
        {
            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 设置在线用户信息
     * 
     * @param user 用户信息
     * @return 在线用户
     */
    @Override
    public SysUserOnline loginUserToUserOnline(LoginUser user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUser()))
        {
            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        sysUserOnline.setIpaddr(user.getIpaddr());
        sysUserOnline.setLoginLocation(user.getLoginLocation());
        sysUserOnline.setBrowser(user.getBrowser());
        sysUserOnline.setOs(user.getOs());
        sysUserOnline.setLoginTime(user.getLoginTime());
        sysUserOnline.setExpireTime(user.getExpireTime());
        if (StringUtils.isNotNull(user.getUser().getDept()))
        {
            sysUserOnline.setDeptId(user.getUser().getDept().getDeptId());
            sysUserOnline.setDeptName(user.getUser().getDept().getDeptName());
        }
        if (StringUtils.isNotEmpty(user.getUser().getRoles()))
        {
            List<Long> roleIds = user.getUser().getRoles().stream()
                    .map(SysRole::getRoleId)
                    .collect(Collectors.toList());
            sysUserOnline.setRoleIds(roleIds);
            String roleNames = user.getUser().getRoles().stream()
                    .map(SysRole::getRoleName)
                    .collect(Collectors.joining(","));
            sysUserOnline.setRoleName(roleNames);
        }
        long currentTime = System.currentTimeMillis();
        if (user.getLoginTime() != null)
        {
            sysUserOnline.setSessionDuration((currentTime - user.getLoginTime()) / 1000);
        }
        return sysUserOnline;
    }

    /**
     * 判断用户是否匹配筛选条件
     * 
     * @param user 登录用户
     * @param query 查询条件
     * @return 是否匹配
     */
    @Override
    public boolean matchesFilter(LoginUser user, SysUserOnline query)
    {
        if (user == null || user.getUser() == null)
        {
            return false;
        }
        if (StringUtils.isNotEmpty(query.getIpaddr()) 
                && !StringUtils.contains(user.getIpaddr(), query.getIpaddr()))
        {
            return false;
        }
        if (StringUtils.isNotEmpty(query.getUserName()) 
                && !StringUtils.contains(user.getUsername(), query.getUserName()))
        {
            return false;
        }
        if (query.getDeptIdParam() != null)
        {
            if (user.getUser().getDept() == null 
                    || !query.getDeptIdParam().equals(user.getUser().getDept().getDeptId()))
            {
                return false;
            }
        }
        if (query.getRoleIdParam() != null)
        {
            if (StringUtils.isEmpty(user.getUser().getRoles()))
            {
                return false;
            }
            boolean hasRole = user.getUser().getRoles().stream()
                    .anyMatch(role -> query.getRoleIdParam().equals(role.getRoleId()));
            if (!hasRole)
            {
                return false;
            }
        }
        if (query.getParams() != null)
        {
            String beginTime = (String) query.getParams().get("beginTime");
            String endTime = (String) query.getParams().get("endTime");
            if (StringUtils.isNotEmpty(beginTime) && user.getLoginTime() != null)
            {
                long beginTimestamp = DateUtils.parseDate(beginTime).getTime();
                if (user.getLoginTime() < beginTimestamp)
                {
                    return false;
                }
            }
            if (StringUtils.isNotEmpty(endTime) && user.getLoginTime() != null)
            {
                long endTimestamp = DateUtils.parseDate(endTime).getTime() + 86400000;
                if (user.getLoginTime() > endTimestamp)
                {
                    return false;
                }
            }
        }
        return true;
    }
}
