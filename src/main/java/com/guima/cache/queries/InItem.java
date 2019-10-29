package com.guima.cache.queries;

import com.jfinal.plugin.activerecord.Model;
import com.guima.base.kits.QueryParam;

import java.util.List;
import java.util.stream.Collectors;

public class InItem implements ICacheQueryItem
{
    private final String fieldName;
    private final String[] fieldValue;

    public InItem(QueryParam.QueryItem item)
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
            for (String inValue : fieldValue)
            {
                if (mValue.equals(inValue))
                    return true;
            }
            return false;
        }).collect(Collectors.toList());
    }
}
