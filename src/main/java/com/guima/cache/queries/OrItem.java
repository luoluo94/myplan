package com.guima.cache.queries;

import com.jfinal.plugin.activerecord.Model;
import com.guima.base.kits.QueryParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrItem implements ICacheQueryItem
{
    private final Map<String, String> orMap;

    public OrItem(QueryParam.QueryItem item)
    {
        this.orMap = item.orMap;
    }

    @Override
    public <M extends Model> List<M> doChain(List<M> chainData)
    {
        return chainData.stream().filter(m ->
        {
            for (String key : orMap.keySet())
            {
                String orValue = m.getStr(key);
                if (orValue.equals(orMap.get(key))) // 字段值命中一次or值，即返回true
                    return true;
            }
            return false;
        }).collect(Collectors.toList());
    }
}
