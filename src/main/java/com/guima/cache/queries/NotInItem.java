package com.guima.cache.queries;

import com.jfinal.plugin.activerecord.Model;
import com.guima.base.kits.QueryParam;

import java.util.List;
import java.util.stream.Collectors;

public class NotInItem implements ICacheQueryItem
{
    private final String fieldName;
    private final String[] fieldValue;

    public NotInItem(QueryParam.QueryItem item)
    {
        this.fieldName = item.fieldName;
        this.fieldValue = item.values;
    }

    @Override
    public <M extends Model> List<M> doChain(List<M> chainData)
    {
        return chainData.stream().filter(m ->
        {
            String mValue = m.getStr(fieldName);
            for (String inNotValue : fieldValue)
            {
                if (mValue.equals(inNotValue))
                    return false;
            }
            return true;
        }).collect(Collectors.toList());
    }
}
