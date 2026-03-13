package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.domain.SysUserOnlineQuery;

/**
 * 在线用户 服务层
 * 
 * @author ruoyi
 */
public interface ISysUserOnlineService
{
    /**
     * 通过登录地址查询信息
     * 
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user);

    /**
     * 通过用户名称查询信息
     * 
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user);

    /**
     * 通过登录地址/用户名称查询信息
     * 
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user);

    /**
     * 设置在线用户信息
     * 
     * @param user 用户信息
     * @return 在线用户
     */
    public SysUserOnline loginUserToUserOnline(LoginUser user);

    /**
     * 判断在线用户是否匹配查询条件（支持模糊匹配）
     * 
     * @param online 在线用户信息
     * @param query 查询条件
     * @return 是否匹配
     */
    public boolean matchesQuery(SysUserOnline online, SysUserOnlineQuery query);

    /**
     * 判断IP地址是否匹配（模糊匹配）
     * 
     * @param ipaddr IP地址
     * @param user 登录用户信息
     * @return 是否匹配
     */
    public boolean matchesIpaddr(String ipaddr, LoginUser user);

    /**
     * 判断用户名称是否匹配（模糊匹配）
     * 
     * @param userName 用户名称
     * @param user 登录用户信息
     * @return 是否匹配
     */
    public boolean matchesUserName(String userName, LoginUser user);
}
