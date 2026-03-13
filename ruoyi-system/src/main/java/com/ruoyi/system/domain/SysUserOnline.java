package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;

/**
 * 当前在线会话
 * 
 * @author ruoyi
 */
public class SysUserOnline
{
    /** 会话编号 */
    @Excel(name = "会话编号")
    private String tokenId;

    /** 部门ID */
    private Long deptId;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 登录IP地址 */
    @Excel(name = "登录IP地址")
    private String ipaddr;

    /** 登录地址 */
    @Excel(name = "登录地点")
    private String loginLocation;

    /** 浏览器类型 */
    @Excel(name = "浏览器类型")
    private String browser;

    /** 操作系统 */
    @Excel(name = "操作系统")
    private String os;

    /** 登录时间 */
    @Excel(name = "登录时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Long loginTime;

    /** 会话时长（导出用） */
    @Excel(name = "会话时长")
    private String sessionDuration;

    public String getTokenId()
    {
        return tokenId;
    }

    public void setTokenId(String tokenId)
    {
        this.tokenId = tokenId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getIpaddr()
    {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr)
    {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation()
    {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation)
    {
        this.loginLocation = loginLocation;
    }

    public String getBrowser()
    {
        return browser;
    }

    public void setBrowser(String browser)
    {
        this.browser = browser;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public Long getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Long loginTime)
    {
        this.loginTime = loginTime;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getSessionDuration()
    {
        return sessionDuration;
    }

    public void setSessionDuration(String sessionDuration)
    {
        this.sessionDuration = sessionDuration;
    }
}
