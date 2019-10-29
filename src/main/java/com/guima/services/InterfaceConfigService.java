package com.guima.services;

import com.google.gson.Gson;
import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.OsResult;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.cache.CacheModel;
import com.guima.domain.InterfaceConfig;
import com.guima.kits.Kit;
import com.guima.kits.MapKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InterfaceConfigService extends BaseService_<InterfaceConfig>
{
    @Override
    public boolean getEnableCache()
    {
        this.enableCache = false;
        return super.getEnableCache();
    }

    @Override
    protected InterfaceConfig getConreteObject()
    {
        return new InterfaceConfig();
    }

    @Override
    public ModelWrapper<InterfaceConfig> getWrapper(InterfaceConfig interfaceConfig)
    {
        return new ModelWrapper<>(interfaceConfig);
    }

    @Override
    public InterfaceConfig getDao()
    {
        return InterfaceConfig.dao;
    }

    @Override
    public List<InterfaceConfig> list(QueryParam param)
    {
        param = param == null ? QueryParam.Builder() : param;
        if (!param.hasQueryItem("type"))
            return new ArrayList<>();
        param.ascBy("sort_idx");
        return super.list(param);
    }

    @Override
    public Page<InterfaceConfig> pageList(QueryParam param, String pageNumberStr, String pageSizeStr)
    {
        param = param == null ? QueryParam.Builder() : param;
        if (!param.hasQueryItem("type"))
            return new Page<>(new ArrayList<>(), 0, 0, 0, 0);
        param.ascBy("sort_idx");
        return super.pageList(param, pageNumberStr, pageSizeStr);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean create(Map<String, String[]> map)
    {
        OsResult<InterfaceConfig> result = new OsResult<>();
        String cStr = Kit.strTrim(MapKit.getValueFromMap(map, "config"));
        if (!cStr.equals(""))
        {
            Gson gson = new Gson();
            List<Map> cList = gson.fromJson(cStr, List.class);
            if (!cList.isEmpty())
            {
                result.status = Db.tx(() ->
                {
                    try
                    {
                        boolean allSaved = true;
                        String type = cList.get(0).get("type").toString();
                        if (clearConfigByType(type))
                        {
                            for (Map cMap : cList)
                            {
                                Map<String, String[]> configMap = MapKit.copyMap(cMap);
                                boolean isSuccess = super.create(configMap);
                                if (!isSuccess)
                                {
                                    allSaved = false;
                                    break;
                                }
                            }
                        } else
                            allSaved = false;
                        return allSaved;
                    } catch (Exception e)
                    {
                        return false;
                    }
                });
            }
        }
        return false;
    }

    private boolean clearConfigByType(String type)
    {
        QueryParam param = QueryParam.Builder().equalsTo("type", type);
        List<InterfaceConfig> configs = list(param);
        for (InterfaceConfig config : configs)
        {
            if (!new CacheModel<>(config).delete())
                return false;
        }
        return true;
    }

    public String getConfigValue(List<InterfaceConfig> configs, String key)
    {
        Optional<InterfaceConfig> optional = configs.stream()
                .filter(config -> config.getConfigKey().equals(key)).findFirst();
        return optional.isPresent() ? optional.get().getConfigValue() : null;
    }

    public String getConfigValue(List<InterfaceConfig> configs, String key, String defaultVal)
    {
        String val = getConfigValue(configs, key);
        return val == null ? defaultVal : val;
    }
}
