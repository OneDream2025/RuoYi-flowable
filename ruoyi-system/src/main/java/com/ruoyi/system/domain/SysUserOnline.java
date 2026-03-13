package com.ruoyi.system.domain;

import java.util.List;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 当前在线会话
 * 
 * @author ruoyi
 */
public class SysUserOnline extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 会话编号 */
    @Excel(name = "会话编号")
    private String tokenId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 用户名称 */
    @Excel(name = "用户名称")
    private String userName;

    /** 登录IP地址 */
    @Excel(name = "登录IP")
    private String ipaddr;

    /** 登录地址 */
    @Excel(name = "登录地点")
    private String loginLocation;

    /** 浏览器类型 */
    @Excel(name = "浏览器")
    private String browser;

    /** 操作系统 */
    @Excel(name = "操作系统")
    private String os;

    /** 登录时间 */
    @Excel(name = "登录时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Long loginTime;

    /** 过期时间 */
    @Excel(name = "过期时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Long expireTime;

    /** 会话时长(秒) */
    @Excel(name = "会话时长(秒)")
    private Long sessionDuration;

    /** 角色ID列表 */
    private List<Long> roleIds;

    /** 角色名称 */
    @Excel(name = "角色")
    private String roleName;

    /** 查询参数：部门ID */
    private Long deptIdParam;

    /** 查询参数：角色ID */
    private Long roleIdParam;

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

    public Long getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(Long expireTime)
    {
        this.expireTime = expireTime;
    }

    public Long getSessionDuration()
    {
        return sessionDuration;
    }

    public void setSessionDuration(Long sessionDuration)
    {
        this.sessionDuration = sessionDuration;
    }

    public List<Long> getRoleIds()
    {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds)
    {
        this.roleIds = roleIds;
    }

    public String getRoleName()
    {
        return roleName;
    }

    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    public Long getDeptIdParam()
    {
        return deptIdParam;
    }

    public void setDeptIdParam(Long deptIdParam)
    {
        this.deptIdParam = deptIdParam;
    }

    public Long getRoleIdParam()
    {
        return roleIdParam;
    }

    public void setRoleIdParam(Long roleIdParam)
    {
        this.roleIdParam = roleIdParam;
    }
}
