package com.ruoyi.system.service.impl;

import java.util.Objects;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.model.LoginUser;
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

    @Override
    public boolean matches(SysUserOnline online, String ipaddr, String userName, Long deptId, Long roleId, Long loginTimeBegin, Long loginTimeEnd)
    {
        if (StringUtils.isNotEmpty(ipaddr) && !StringUtils.contains(online.getIpaddr(), ipaddr))
        {
            return false;
        }
        if (StringUtils.isNotEmpty(userName) && !StringUtils.contains(online.getUserName(), userName))
        {
            return false;
        }
        if (StringUtils.isNotNull(deptId) && !Objects.equals(deptId, online.getDeptId()))
        {
            return false;
        }
        if (StringUtils.isNotNull(roleId) && !Objects.equals(roleId, online.getRoleId()))
        {
            return false;
        }
        if (StringUtils.isNotNull(loginTimeBegin) && online.getLoginTime() < loginTimeBegin)
        {
            return false;
        }
        if (StringUtils.isNotNull(loginTimeEnd) && online.getLoginTime() > loginTimeEnd)
        {
            return false;
        }
        return true;
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
        sysUserOnline.setDeptId(user.getDeptId());
        if (StringUtils.isNotNull(user.getUser().getDept()))
        {
            sysUserOnline.setDeptName(user.getUser().getDept().getDeptName());
        }
        if (StringUtils.isNotEmpty(user.getUser().getRoles()))
        {
            sysUserOnline.setRoleId(user.getUser().getRoles().get(0).getRoleId());
        }
        return sysUserOnline;
    }
}
