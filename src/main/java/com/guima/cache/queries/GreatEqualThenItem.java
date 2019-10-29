package com.guima.cache.queries;

import com.jfinal.plugin.activerecord.Model;
import com.guima.base.kits.QueryParam;
import com.guima.kits.Kit;

public class GreatEqualThenItem extends KeyValueQueryItem
{
    public GreatEqualThenItem(QueryParam.QueryItem item)
    {
        this.fieldName = item.fieldName;
        this.fieldValue = item.value;
    }

    @Override
    protected <M extends Model> boolean express(M m)
    {
        return Kit.strTrim(m.getStr(fieldName)).compareTo(fieldValue) >= 0;
    }
}
