package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Plan;
import com.guima.domain.PlanDetailAnnex;
import com.guima.kits.Constant;
import com.guima.kits.NumberConstant;

import java.util.Date;
import java.util.List;

public class PlanDetailAnnexService extends BaseService_<PlanDetailAnnex>
{
    @Override
    protected PlanDetailAnnex getConreteObject()
    {
        return new PlanDetailAnnex();
    }

    @Override
    public ModelWrapper<PlanDetailAnnex> getWrapper(PlanDetailAnnex planDetailAnnex)
    {
        return new ModelWrapper(planDetailAnnex);
    }

    @Override
    public PlanDetailAnnex getDao()
    {
        return PlanDetailAnnex.dao;
    }

    public List<PlanDetailAnnex> list(String planDetailId){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("plan_detail_id", planDetailId);
        param.equalsTo("is_deleted", Constant.ACTIVE);
        return super.list(param);
    }

}
