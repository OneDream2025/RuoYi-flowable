package com.ruoyi.system.service.impl;

import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.domain.SysUserOnlineQuery;
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
        sysUserOnline.setDeptId(user.getDeptId());
        if (StringUtils.isNotNull(user.getUser().getDept()))
        {
            sysUserOnline.setDeptName(user.getUser().getDept().getDeptName());
        }
        return sysUserOnline;
    }

    /**
     * 判断在线用户是否匹配查询条件（支持模糊匹配）
     * 
     * @param online 在线用户信息
     * @param query 查询条件
     * @return 是否匹配
     */
    @Override
    public boolean matchesQuery(SysUserOnline online, SysUserOnlineQuery query)
    {
        // IP地址模糊匹配
        if (StringUtils.isNotEmpty(query.getIpaddr()) 
            && (StringUtils.isEmpty(online.getIpaddr()) 
                || !StringUtils.contains(online.getIpaddr(), query.getIpaddr())))
        {
            return false;
        }
        
        // 用户名模糊匹配
        if (StringUtils.isNotEmpty(query.getUserName()) 
            && (StringUtils.isEmpty(online.getUserName()) 
                || !StringUtils.contains(online.getUserName(), query.getUserName())))
        {
            return false;
        }
        
        // 部门ID筛选
        if (query.getDeptId() != null 
            && !query.getDeptId().equals(online.getDeptId()))
        {
            return false;
        }
        
        // 部门名称模糊匹配
        if (StringUtils.isNotEmpty(query.getDeptName()) 
            && (StringUtils.isEmpty(online.getDeptName()) 
                || !StringUtils.contains(online.getDeptName(), query.getDeptName())))
        {
            return false;
        }
        
        // 登录时间范围筛选
        if (query.getLoginTimeStart() != null 
            && (online.getLoginTime() == null || online.getLoginTime() < query.getLoginTimeStart()))
        {
            return false;
        }
        if (query.getLoginTimeEnd() != null 
            && (online.getLoginTime() == null || online.getLoginTime() > query.getLoginTimeEnd()))
        {
            return false;
        }
        
        // 浏览器类型精确匹配
        if (StringUtils.isNotEmpty(query.getBrowser()) 
            && !StringUtils.equals(online.getBrowser(), query.getBrowser()))
        {
            return false;
        }
        
        // 操作系统精确匹配
        if (StringUtils.isNotEmpty(query.getOs()) 
            && !StringUtils.equals(online.getOs(), query.getOs()))
        {
            return false;
        }
        
        return true;
    }

    /**
     * 判断IP地址是否匹配（模糊匹配）
     * 
     * @param ipaddr IP地址
     * @param user 登录用户信息
     * @return 是否匹配
     */
    @Override
    public boolean matchesIpaddr(String ipaddr, LoginUser user)
    {
        return StringUtils.isNotEmpty(user.getIpaddr()) 
            && StringUtils.contains(user.getIpaddr(), ipaddr);
    }

    /**
     * 判断用户名称是否匹配（模糊匹配）
     * 
     * @param userName 用户名称
     * @param user 登录用户信息
     * @return 是否匹配
     */
    @Override
    public boolean matchesUserName(String userName, LoginUser user)
    {
        return StringUtils.isNotEmpty(user.getUsername()) 
            && StringUtils.contains(user.getUsername(), userName);
    }
}
