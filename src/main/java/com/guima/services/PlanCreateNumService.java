package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.PlanCreateNum;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

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


    /**
     * 获取每天新增的计划量
     * @param startDatetime
     * @return
     */
    public Long countPlanNum(String startDatetime){
        String endDatetime=DateKit.getDaytime();
        System.out.println(endDatetime);
        return Db.queryLong("select count(*) as num from plan where create_time>= '"+startDatetime+"' and create_time<'"+endDatetime+"'");
    }

    /**
     * 计算每天新增的计划数量
     */
    public void countPlanNum(){
        try{
            String startDate=DateKit.getIntervalDate(-1);
            Long num=countPlanNum(startDate+" 00:00:00");
            PlanCreateNum planCreateNum=new PlanCreateNum();
            planCreateNum.setId(UUID.randomUUID().toString());
            planCreateNum.setCreateTime(new Date());
            planCreateNum.setCreateDate(startDate);
            planCreateNum.setNum(num.intValue());
            planCreateNum.save();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
