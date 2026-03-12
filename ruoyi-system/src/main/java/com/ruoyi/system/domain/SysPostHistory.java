package com.ruoyi.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 岗位信息历史表 sys_post_history
 * 
 * @author ruoyi
 */
public class SysPostHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "历史记录ID", cellType = ColumnType.NUMERIC)
    private Long historyId;

    @Excel(name = "岗位ID", cellType = ColumnType.NUMERIC)
    private Long postId;

    @Excel(name = "岗位编码")
    private String postCode;

    @Excel(name = "岗位名称")
    private String postName;

    @Excel(name = "岗位排序")
    private Integer postSort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @Excel(name = "版本号", cellType = ColumnType.NUMERIC)
    private Integer version;

    @Excel(name = "操作类型", readConverterExp = "1=新增,2=修改,3=删除")
    private String operationType;

    @Excel(name = "变更原因")
    private String changeReason;

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

    @NotBlank(message = "岗位编码不能为空")
    @Size(min = 0, max = 64, message = "岗位编码长度不能超过64个字符")
    public String getPostCode()
    {
        return postCode;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    @NotBlank(message = "岗位名称不能为空")
    @Size(min = 0, max = 50, message = "岗位名称长度不能超过50个字符")
    public String getPostName()
    {
        return postName;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    @NotNull(message = "显示顺序不能为空")
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

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public String getOperationType()
    {
        return operationType;
    }

    public void setOperationType(String operationType)
    {
        this.operationType = operationType;
    }

    public String getChangeReason()
    {
        return changeReason;
    }

    public void setChangeReason(String changeReason)
    {
        this.changeReason = changeReason;
    }

    public static SysPostHistory fromSysPost(SysPost post)
    {
        SysPostHistory history = new SysPostHistory();
        history.setPostId(post.getPostId());
        history.setPostCode(post.getPostCode());
        history.setPostName(post.getPostName());
        history.setPostSort(post.getPostSort());
        history.setStatus(post.getStatus());
        history.setRemark(post.getRemark());
        history.setCreateBy(post.getCreateBy());
        history.setCreateTime(post.getCreateTime());
        return history;
    }

    public SysPost toSysPost()
    {
        SysPost post = new SysPost();
        post.setPostId(this.getPostId());
        post.setPostCode(this.getPostCode());
        post.setPostName(this.getPostName());
        post.setPostSort(this.getPostSort());
        post.setStatus(this.getStatus());
        post.setRemark(this.getRemark());
        return post;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("historyId", getHistoryId())
            .append("postId", getPostId())
            .append("postCode", getPostCode())
            .append("postName", getPostName())
            .append("postSort", getPostSort())
            .append("status", getStatus())
            .append("version", getVersion())
            .append("operationType", getOperationType())
            .append("changeReason", getChangeReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
