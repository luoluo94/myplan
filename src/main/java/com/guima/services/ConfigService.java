package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Config;
import com.guima.kits.Constant;
import java.util.List;

public class ConfigService extends BaseService_<Config>
{
    @Override
    protected Config getConreteObject()
    {
        return new Config();
    }

    @Override
    public ModelWrapper<Config> getWrapper(Config config)
    {
        return new ModelWrapper(config);
    }

    @Override
    public Config getDao()
    {
        return Config.dao;
    }



    private Config getConfig(String type){
        QueryParam param = QueryParam.Builder().equalsTo("type", type);
        param.equalsTo("status", Constant.ACTIVE);
        List<Config> list=list(param);
        return list==null || list.size()<=0?null:list.get(0);
    }

    public Config getShareConfig(){
        return getConfig(Constant.SHARE);
    }

}
