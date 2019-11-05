package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Advice;
import com.guima.domain.User;
import com.jfinal.plugin.activerecord.Page;
import org.apache.commons.lang3.StringUtils;

public class AdviceService extends BaseService_<Advice>
{

    @Override
    protected Advice getConreteObject()
    {
        return new Advice();
    }

    @Override
    public ModelWrapper<Advice> getWrapper(Advice advice)
    {
        return new ModelWrapper(advice);
    }

    @Override
    public Advice getDao()
    {
        return Advice.dao;
    }

    public Page<Advice> listAdvices(User user,int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        if(user!=null && StringUtils.isNotEmpty(user.getId())){
            param.equalsTo("creator", user.getId());
        }
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

}
