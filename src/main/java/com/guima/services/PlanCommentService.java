package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Plan;
import com.guima.domain.PlanComment;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

import java.util.ArrayList;
import java.util.List;

public class PlanCommentService extends BaseService_<PlanComment>
{
    @Override
    protected PlanComment getConreteObject()
    {
        return new PlanComment();
    }

    @Override
    public ModelWrapper<PlanComment> getWrapper(PlanComment planComment)
    {
        return new ModelWrapper(planComment);
    }

    @Override
    public PlanComment getDao()
    {
        return PlanComment.dao;
    }

    public Page<PlanComment> pageList(String planId, int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("plan_id", planId);
        param.ascBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }



}
