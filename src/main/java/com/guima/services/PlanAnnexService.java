package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Plan;

import java.util.ArrayList;
import java.util.List;

public class PlanAnnexService extends BaseService_<Plan>
{
    @Override
    protected Plan getConreteObject()
    {
        return new Plan();
    }

    @Override
    public ModelWrapper<Plan> getWrapper(Plan Plan)
    {
        return new ModelWrapper(Plan);
    }

    @Override
    public Plan getDao()
    {
        return Plan.dao;
    }

    @Override
    public List<Plan> list(QueryParam param)
    {
        param = param == null ? QueryParam.Builder() : param;
        if (!param.hasQueryItem("type"))
            return new ArrayList<>();
        if (!param.hasQueryItem("status"))
            param.equalsTo("status", "0");
        if (param.getOrderItems().isEmpty())
            param.ascBy("sort_idx");
        return super.list(param);
    }



}
