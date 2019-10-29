package com.guima.cache;

import com.jfinal.kit.StrKit;

class CacheOrderItem
{
    private String fieldName;
    private boolean isDesc;

    CacheOrderItem(String fieldName, boolean isDesc)
    {
        this.fieldName = fieldName;
        this.isDesc = isDesc;
    }

    public boolean isDesc()
    {
        return isDesc;
    }

    public String getFieldName()
    {
        return StrKit.toCamelCase(fieldName);
    }
}
