package com.ruoyi.system.domain;

/**
 * 在线用户查询对象
 * 
 * @author ruoyi
 */
public class SysUserOnlineQuery
{
    /** 登录IP地址（模糊） */
    private String ipaddr;

    /** 用户名称（模糊） */
    private String userName;

    /** 部门ID */
    private Long deptId;

    /** 部门名称（模糊） */
    private String deptName;

    /** 角色ID */
    private Long roleId;

    /** 登录时间起始 */
    private Long loginTimeStart;

    /** 登录时间结束 */
    private Long loginTimeEnd;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 排序字段 */
    private String orderByColumn;

    /** 排序方式（asc/desc） */
    private String isAsc;

    public String getIpaddr()
    {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr)
    {
        this.ipaddr = ipaddr;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    public Long getLoginTimeStart()
    {
        return loginTimeStart;
    }

    public void setLoginTimeStart(Long loginTimeStart)
    {
        this.loginTimeStart = loginTimeStart;
    }

    public Long getLoginTimeEnd()
    {
        return loginTimeEnd;
    }

    public void setLoginTimeEnd(Long loginTimeEnd)
    {
        this.loginTimeEnd = loginTimeEnd;
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

    public String getOrderByColumn()
    {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn)
    {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc()
    {
        return isAsc;
    }

    public void setIsAsc(String isAsc)
    {
        this.isAsc = isAsc;
    }
}
