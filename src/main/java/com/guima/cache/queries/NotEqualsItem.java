package com.guima.cache.queries;

import com.jfinal.plugin.activerecord.Model;
import com.guima.base.kits.QueryParam;
import com.guima.kits.Kit;

import java.util.List;
import java.util.stream.Collectors;

public class NotEqualsItem extends KeyValueQueryItem
{
    public NotEqualsItem(QueryParam.QueryItem item)
    {
        this.fieldName = item.fieldName;
        this.fieldValue = item.value;
    }

    @Override
    public <M extends Model> List<M> doChain(List<M> chainData)
    {
        return chainData.stream().filter(m -> !m.getStr(fieldName).equals(fieldValue))
                .collect(Collectors.toList());
    }

    @Override
    protected <M extends Model> boolean express(M m)
    {
        return !Kit.strTrim(m.getStr(fieldName)).equals(fieldValue);
    }

}
