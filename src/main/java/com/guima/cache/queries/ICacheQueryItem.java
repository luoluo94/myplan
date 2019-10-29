package com.guima.cache.queries;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

public interface ICacheQueryItem
{
    <M extends Model> List<M> doChain(List<M> chainData);
}
