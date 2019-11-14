package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.PlanCreateNum;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Page;

public class PlanCreateNumService extends BaseService_<PlanCreateNum>
{

    @Override
    protected PlanCreateNum getConreteObject()
    {
        return new PlanCreateNum();
    }

    @Override
    public ModelWrapper<PlanCreateNum> getWrapper(PlanCreateNum planCreateNum)
    {
        return new ModelWrapper(planCreateNum);
    }

    @Override
    public PlanCreateNum getDao()
    {
        return PlanCreateNum.dao;
    }

    public Page<PlanCreateNum> listAllPlanCreateNums(String pageNumberStr, String pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr, pageSizeStr);
    }

}
