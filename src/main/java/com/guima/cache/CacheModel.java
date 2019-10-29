package com.guima.cache;

import com.jfinal.plugin.activerecord.Model;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.kits.Kit;

public class CacheModel<M extends Model<M>>
{
    private M m;

    public CacheModel(M m)
    {
        this.m = m;
    }

    @SuppressWarnings("unchecked")
    public boolean save()
    {
        boolean result = m.save();
        if (result)
        {
            m = m.findById(m.getStr("id"));
            String cacheName = getCacheName();
            BaseService_ service = ServiceManager.instance().getService(cacheName);
            if (service != null && service.getEnableCache())
                RedisCacheManager.instance().regNewCache(cacheName, m);
        }
        return result;
    }

    public boolean update()
    {
    	boolean result = m.update();
    	if(result){
    		String cacheName = getCacheName();
            BaseService_ service = ServiceManager.instance().getService(cacheName);
            if (service != null && service.getEnableCache())
                RedisCacheManager.instance().regNewCache(cacheName, m);
    	}
        return result;
    }

    public boolean delete()
    {
        boolean result = m.delete();
        if (result)
        {
            String cacheName = getCacheName();
            BaseService_ service = ServiceManager.instance().getService(cacheName);
            if (service != null && service.getEnableCache())
            {
                String id = m.getStr("id");
                RedisCacheManager.instance().removeCaches(cacheName, new String[]{id});
            }
        }
        return result;
    }

    private String getCacheName()
    {
        String tabName = Kit.camel2Line(m.getClass().getSimpleName());
        // TODO 应该保持下划线命名法，但需要与Service保持一致
        return tabName.contains("_") ? tabName.replaceAll("_", "") : tabName;
    }
}
