package com.ruoyi.system.domain.dto;

import java.io.Serializable;

/**
 * 岗位变更详情DTO
 */
public class PostChangeDetail implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String field;

    private String fieldLabel;

    private String oldValue;

    private String newValue;

    public PostChangeDetail()
    {
    }

    public PostChangeDetail(String field, String fieldLabel, String oldValue, String newValue)
    {
        this.field = field;
        this.fieldLabel = fieldLabel;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
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
    public String toString()
    {
        return "PostChangeDetail{" +
                "field='" + field + '\'' +
                ", fieldLabel='" + fieldLabel + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                '}';
    }
}
