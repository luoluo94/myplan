package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Advice;
import com.guima.domain.PlanNotice;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import org.apache.commons.lang3.StringUtils;

public class PlanNoticeService extends BaseService_<PlanNotice>
{
    @Override
    protected PlanNotice getConreteObject()
    {
        return new PlanNotice();
    }

    @Override
    public ModelWrapper<PlanNotice> getWrapper(PlanNotice m)
    {
        return new ModelWrapper<>(m);
    }


    @Override
    public PlanNotice getDao()
    {
        return PlanNotice.dao;
    }

    public Page<PlanNotice> listNotices(String pageNumberStr, String pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("is_deleted", Constant.MARK_ZERO_STR);
        param.ascBy("create_date");
        return super.pageList(param,pageNumberStr, pageSizeStr);
    }

}
