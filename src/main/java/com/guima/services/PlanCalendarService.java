package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.PlanCalendar;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Page;

public class PlanCalendarService extends BaseService_<PlanCalendar>
{

    @Override
    protected PlanCalendar getConreteObject()
    {
        return new PlanCalendar();
    }

    @Override
    public ModelWrapper<PlanCalendar> getWrapper(PlanCalendar planCalendar)
    {
        return new ModelWrapper(planCalendar);
    }

    @Override
    public PlanCalendar getDao()
    {
        return PlanCalendar.dao;
    }

    public Page<PlanCalendar> listPublicPlanCalendars(int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.gt("create_time", DateKit.getStartTime());
        param.equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE);
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

    public Page<PlanCalendar> listMyPlanCalendars(User user,int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("creator", user.getId());
        param.equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE);
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

    public Page<PlanCalendar> listAllPlanCalendars(int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

}
