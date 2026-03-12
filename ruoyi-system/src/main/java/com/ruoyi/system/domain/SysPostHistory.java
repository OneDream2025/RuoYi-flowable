package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 岗位变更历史表 sys_post_history
 */
public class SysPostHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "历史记录ID", cellType = ColumnType.NUMERIC)
    private Long historyId;

    @Excel(name = "岗位ID")
    private Long postId;

    @Excel(name = "岗位编码")
    private String postCode;

    @Excel(name = "岗位名称")
    private String postName;

    @Excel(name = "岗位排序")
    private Integer postSort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String remark;

    private Integer version;

    @Excel(name = "变更类型", readConverterExp = "1=新增,2=修改,3=删除")
    private String changeType;

    private String changeContent;

    private String beforeData;

    private String afterData;

    @Excel(name = "操作人")
    private String operBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    @Excel(name = "操作IP")
    private String operIp;

    public Long getHistoryId()
    {
        return historyId;
    }

    public void setHistoryId(Long historyId)
    {
        this.historyId = historyId;
    }

    public Long getPostId()
    {
        return postId;
    }

    public void setPostId(Long postId)
    {
        this.postId = postId;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public String getPostName()
    {
        return postName;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    public Integer getPostSort()
    {
        return postSort;
    }

    public void setPostSort(Integer postSort)
    {
        this.postSort = postSort;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public String getChangeType()
    {
        return changeType;
    }

    public void setChangeType(String changeType)
    {
        this.changeType = changeType;
    }

    public String getChangeContent()
    {
        return changeContent;
    }

    public void setChangeContent(String changeContent)
    {
        this.changeContent = changeContent;
    }

    public String getBeforeData()
    {
        return beforeData;
    }

    public void setBeforeData(String beforeData)
    {
        this.beforeData = beforeData;
    }

    public String getAfterData()
    {
        return afterData;
    }

    public void setAfterData(String afterData)
    {
        this.afterData = afterData;
    }

    public String getOperBy()
    {
        return operBy;
    }

    public void setOperBy(String operBy)
    {
        this.operBy = operBy;
    }

    public Date getOperTime()
    {
        return operTime;
    }

    public void setOperTime(Date operTime)
    {
        this.operTime = operTime;
    }

    public String getOperIp()
    {
        return operIp;
    }

    public void setOperIp(String operIp)
    {
        this.operIp = operIp;
    }

    @Override
    public String toString()
    {
        return "SysPostHistory{" +
                "historyId=" + historyId +
                ", postId=" + postId +
                ", postCode='" + postCode + '\'' +
                ", postName='" + postName + '\'' +
                ", postSort=" + postSort +
                ", status='" + status + '\'' +
                ", version=" + version +
                ", changeType='" + changeType + '\'' +
                ", operBy='" + operBy + '\'' +
                ", operTime=" + operTime +
                '}';
    }
}
