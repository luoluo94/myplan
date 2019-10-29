package com.guima.base.kits;

import com.guima.kits.Kit;

public class MetaInfo
{
    public String baseFiledName;
    public String fieldName;
    public String remark;

    public String getRemark()
    {
        return Kit.isNull(remark) ? fieldName : remark;
    }
}
