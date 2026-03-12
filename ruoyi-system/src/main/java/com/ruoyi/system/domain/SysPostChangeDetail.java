package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 岗位变更对比字段表 sys_post_change_detail
 * 
 * @author ruoyi
 */
public class SysPostChangeDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 详情ID */
    @Excel(name = "详情ID", cellType = ColumnType.NUMERIC)
    private Long detailId;

    /** 历史记录ID */
    @Excel(name = "历史记录ID", cellType = ColumnType.NUMERIC)
    private Long historyId;

    /** 岗位ID */
    @Excel(name = "岗位ID", cellType = ColumnType.NUMERIC)
    private Long postId;

    /** 字段名称 */
    @Excel(name = "字段名称")
    private String fieldName;

    /** 字段标签 */
    @Excel(name = "字段标签")
    private String fieldLabel;

    /** 旧值 */
    @Excel(name = "旧值")
    private String oldValue;

    /** 新值 */
    @Excel(name = "新值")
    private String newValue;

    public Long getDetailId()
    {
        return detailId;
    }

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

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

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldLabel()
    {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel)
    {
        this.fieldLabel = fieldLabel;
    }

    public String getOldValue()
    {
        return oldValue;
    }

    public void setOldValue(String oldValue)
    {
        this.oldValue = oldValue;
    }

    public String getNewValue()
    {
        return newValue;
    }

    public void setNewValue(String newValue)
    {
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("historyId", getHistoryId())
            .append("postId", getPostId())
            .append("fieldName", getFieldName())
            .append("fieldLabel", getFieldLabel())
            .append("oldValue", getOldValue())
            .append("newValue", getNewValue())
            .toString();
    }
}
