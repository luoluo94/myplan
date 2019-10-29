package com.guima.cache.queries;

import com.guima.kits.Kit;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Model;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.ServiceManager;
import com.guima.cache.RedisCacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SubQueryItem implements ICacheQueryItem
{
    private final String fieldName;
    private final QueryParam subParam;
    private final String subTab;
    private final String subFileName;

    public SubQueryItem(QueryParam.QueryItem item)
    {
        this.fieldName = item.fieldName;
        this.subParam = item.sub;
        this.subTab = item.subTab;
        this.subFileName = Kit.isNotNull(subParam.getResultFields()) ? subParam.getResultFields() : "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M extends Model> List<M> doChain(List<M> chainData)
    {
        // TODO 应该保持下划线命名法，但需要与Service保持一致
        String cacheName = subTab.contains("_") ? subTab.replaceAll("_", "") : subTab;
        List<M> subData;
        if (RedisCacheManager.instance().hasCache(cacheName)) // 有缓存优先从缓存中查询
            subData = RedisCacheManager.instance().list(cacheName, subParam);
        else // 没有缓存从Service中查询
        {
            LogKit.warn(subTab + "没有缓存，将从Service中查询数据...");
            subData = ServiceManager.instance().getService(cacheName).list(subParam);
        }
        if (subData == null)
            return new ArrayList<>();

        return chainData.stream().filter(m ->
        {
            String mValue = m.getStr(fieldName);
            for (M subM : subData)
            {
                if (mValue.equals(subM.getStr(subFileName))) // 子对象中的指定属性命中一次即返回true
                    return true;
            }
            return false;
        }).collect(Collectors.toList());
    }
}
