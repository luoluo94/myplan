package com.guima.cache.queries;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;
import java.util.stream.Collectors;

public abstract class KeyValueQueryItem implements ICacheQueryItem
{
    protected String fieldName;
    protected String fieldValue;

    @Override
    public <M extends Model> List<M> doChain(List<M> chainData)
    {
        return chainData.stream().filter(this::express).collect(Collectors.toList());
    }

    protected abstract <M extends Model> boolean express(M m);
}
