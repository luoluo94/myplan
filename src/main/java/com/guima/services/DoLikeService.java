package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.DoLike;
import com.guima.domain.User;
import com.jfinal.plugin.activerecord.Page;
import org.apache.commons.lang3.StringUtils;

public class DoLikeService extends BaseService_<DoLike>
{

    @Override
    protected DoLike getConreteObject()
    {
        return new DoLike();
    }

    @Override
    public ModelWrapper<DoLike> getWrapper(DoLike doLike)
    {
        return new ModelWrapper(doLike);
    }

    @Override
    public DoLike getDao()
    {
        return DoLike.dao;
    }

    public DoLike findDoLike(String planId,String creatorId){
        QueryParam param=QueryParam.Builder().equalsTo("plan_id", planId)
                .equalsTo("creator_id",creatorId);
        return findFirst(param);
    }

}
