package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Plan;
import com.guima.domain.PlanDetail;
import com.guima.domain.PlanDetailAnnex;
import com.guima.enums.ConstantEnum;
import com.guima.enums.ScoreTypeEnum;
import com.guima.kits.Constant;
import com.guima.kits.Kit;
import com.guima.kits.NumberConstant;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanDetailService extends BaseService_<PlanDetail>
{
    @Override
    protected PlanDetail getConreteObject()
    {
        return new PlanDetail();
    }

    @Override
    public ModelWrapper<PlanDetail> getWrapper(PlanDetail plan)
    {
        return new ModelWrapper(plan);
    }

    @Override
    public PlanDetail getDao()
    {
        return PlanDetail.dao;
    }

    public List<PlanDetail> list(String planId){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("plan_id", planId);
        param.ascBy("sort_index");
        return super.list(param);
    }

    public boolean createPlanDetail(Plan plan ,String[] details){
        boolean removeOldDetails = removePlanDetail(plan);
        if (!removeOldDetails)
            return false;
        PlanDetail planDetail;
        int index = 0;
        for (String detail : details) {
            planDetail = new PlanDetail();
            planDetail.init(plan.getId(),detail,index++);
            planDetail.save();
        }
        return true;
    }

    public boolean removePlanDetail(Plan plan){
        List<PlanDetail> details=listPlanDetails(plan.getId());
        if(details.isEmpty())
            return true;
        for (PlanDetail planDetail:details){
            planDetail.delete();
        }
        return true;
    }

    public List<String> listPlanDetailIds(String planId){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("plan_id",planId);
        return listOnlyId(param);
    }

    public List<PlanDetail> listPlanDetails(String planId){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("plan_id",planId);
        return list(param);
    }

    /**
     * 标记全部完成
     * @param planId
     * @return
     */
    public boolean markFinish(String planId){
        List<PlanDetail> details=listPlanDetails(planId);
        Date date=new Date();
        for (PlanDetail planDetail:details){
            if(planDetail.getFinishPercentage()!= NumberConstant.ONE_HUNDRED){
                planDetail.setFinishPercentage(NumberConstant.ONE_HUNDRED);
                planDetail.setMarkTime(date);
                planDetail.update();
            }

        }
        return true;
    }

    /**
     * 给某个计划事项添加附件
     */
    public boolean addPlanAnnex(String planDetailId, PlanDetailAnnex annex){

        PlanDetail planDetail=findById(planDetailId);
        planDetail.setHasAnnex(Constant.MARK_ONE);
        return Db.tx(()->{
            return planDetail.update() && annex.save();
        });
    }

    /**
     * 删除某个计划事项的附件
     */
    public boolean removePlanAnnex(PlanDetailAnnex annex){

        PlanDetail planDetail=findById(annex.getPlanDetailId());
        planDetail.setHasAnnex(Constant.MARK_ZERO);
        annex.setIsDeleted(Constant.IS_DELETED_YES);
        return Db.tx(()->{
            return planDetail.update() && annex.update();
        });
    }



}
