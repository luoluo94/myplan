package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.domain.Plan;
import com.guima.domain.PlanCalendar;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.guima.kits.NumberKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public boolean updateCreatePlanNum(String creator){
        return updatePlanNum("createNum",creator);
    }

    public boolean updateFinishPlanNum(String creator){
        return updatePlanNum("finishNum",creator);
    }

    public boolean updateUnFinishPlanNum(String creator){
        return updatePlanNum("unFinishNum",creator);
    }

    public boolean updatePlanNum(String type,String creator){
        PlanCalendar planCalendar=findByCreator(creator);
        synchronized (planCalendar){
            if(type.equals("createNum")){
                planCalendar.setCreateNum(planCalendar.getCreateNum()+1);
                planCalendar.setFinishRate(NumberKit.getIntPercent(planCalendar.getFinishedNum(),planCalendar.getCreateNum()));
            }else if(type.equals("finishNum")){
                planCalendar.setFinishedNum(planCalendar.getFinishedNum()+1);
                planCalendar.setFinishRate(NumberKit.getIntPercent(planCalendar.getFinishedNum(),planCalendar.getCreateNum()));
            }else if(type.equals("unFinishNum")){
                planCalendar.setUnfinishedNum(planCalendar.getUnfinishedNum()+1);
            }
        }
        planCalendar.setUpdateTime(new Date());
        return planCalendar.update();
    }

    public PlanCalendar findByCreator(String creator){
        return findFirst(QueryParam.Builder().equalsTo("creator",creator));
    }

    /**
     * 初始化
     * @param creator
     * @return
     */
    public boolean savePlanCalendar(String creator){
        PlanCalendar planCalendar=new PlanCalendar();
        planCalendar.setCreator(creator);
        planCalendar.setCreateNum(0);
        planCalendar.setCreateTime(new Date());
        planCalendar.setFinishedNum(0);
        planCalendar.setUnfinishedNum(0);
        planCalendar.setUpdateTime(new Date());
        planCalendar.setId(UUID.randomUUID().toString());
        planCalendar.setFinishRate(0);
        return planCalendar.save();
    }

    public Long getFinishRate(Integer rate){
        return Db.queryLong("SELECT COUNT(*) from plan_calendar WHERE finish_rate < ?",new Object[]{rate});
    }

    public Long getMyFinishNum(Integer num){
        return Db.queryLong("SELECT COUNT(*) from plan_calendar WHERE finished_num < ?",new Object[]{num});
    }

    public Long getTotalNum(){
        return Db.queryLong("SELECT COUNT(*) from plan_calendar");
    }



    public Map getStatics(String creator){
        PlanCalendar planCalendar=findByCreator(creator);
        Integer total=getTotalNum().intValue();
        Integer totalFinishRate=NumberKit.getIntPercent(getFinishRate(planCalendar.getFinishRate()),total);
        Integer totalFinishNumRate=NumberKit.getIntPercent(getMyFinishNum(planCalendar.getFinishRate()),total);
        Map<String,Object> data=new HashMap<>();
        //完成数
        data.put("finish_num",planCalendar.getFinishedNum());
        //个人完成率
        data.put("personal_finish_rate",planCalendar.getFinishRate());
        //完成率占比
        data.put("total_finish_rate",totalFinishRate);
        //个人完成数占比
        data.put("personal_finish_num_rate",totalFinishNumRate);
        return data;
    }



}
