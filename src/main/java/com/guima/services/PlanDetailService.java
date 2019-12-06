package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Plan;
import com.guima.domain.PlanDetail;
import com.guima.domain.PlanDetailAnnex;
import com.guima.domain.Sign;
import com.guima.enums.ConstantEnum;
import com.guima.enums.ScoreTypeEnum;
import com.guima.kits.Constant;
import com.guima.kits.Kit;
import com.guima.kits.NumberConstant;
import com.jfinal.kit.StrKit;
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

    public List<PlanDetail> listUnFinishPlanDetails(String planId){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("plan_id", planId);
        param.equalsTo("status",ConstantEnum.STATUS_ONGOING.getValue());
        param.equalsTo("is_deleted",Constant.MARK_ZERO_STR);
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
     * 标记全部完成
     * @param planId
     * @return
     */
    public boolean markFinish2(String planId){
        List<PlanDetail> details=listPlanDetails(planId);
        Date date=new Date();
        for (PlanDetail planDetail:details){
            if(planDetail.getFinishPercentage()== NumberConstant.ZERO){
                planDetail.setFinishPercentage(NumberConstant.ONE);
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

    /**
     * 根据PlanId和序号查找PlanDetail
     * @param planId
     * @param sortIndex
     * @return
     */
    public PlanDetail find(String planId,int sortIndex){
        return findFirst(QueryParam.Builder().equalsTo("plan_id",planId).equalsTo("sort_index",sortIndex+""));
    }

    /**
     * 判断是否已经全部完成
     * @param planId
     * @param planDetailId
     * @return
     */
    public boolean isAllFinish(String planId,String planDetailId){
        PlanDetail planDetail= findFirst(QueryParam.Builder().equalsTo("plan_id",planId).equalsTo("is_finish",Constant.ACTIVE).notEquals("id",planDetailId));
        return planDetail==null;
    }



    /**
     * 关联计划打卡
     * @param planId
     * @param planDetailId
     * @param sign
     * @return
     */
    public boolean savePlanSign(String planId, String planDetailId, Sign sign){
        PlanService planService=((PlanService) ServiceManager.instance().getService("plan"));
        return Db.tx(()->{
            boolean isSuccess=true;
            PlanDetail planDetail=findById(planDetailId);
            if(planDetail==null){
                return false;
            }
            //判断打卡次数不允许超过限制
            if(planDetail.getSignMaxNum()!=null && planDetail.getFinishPercentage()>=planDetail.getSignMaxNum()){
                return false;
            }
            planDetail.setFinishPercentage(planDetail.getFinishPercentage()+1);
            isSuccess=sign.save() && planDetail.update();
            //判断是否为官方计划
            Plan plan=planService.findById(planId);
            String parentPlanId=plan.getParentId();
            if(!StrKit.isBlank(parentPlanId)){
                PlanDetail parentPlanDetail=find(parentPlanId,planDetail.getSortIndex());
                Sign parentSign=new Sign();
                parentSign.init(sign.getCreator(),"\""+planDetail.getPlanDetail()+"\" +1",ConstantEnum.PRIVACY_SELF.getValue(),null,parentPlanId,parentPlanDetail.getId());
                isSuccess=isSuccess && parentSign.save();
                parentPlanDetail.setFinishPercentage(parentPlanDetail.getFinishPercentage()+1);
                isSuccess=isSuccess && parentPlanDetail.update();

                //判断是否已经全部完成打卡 变更该计划状态 及父级计划的完成数目
                //判断该详情是否完成
                if(planDetail.getFinishPercentage()>=planDetail.getSignMaxNum()){
                    planDetail.setIsFinish(Constant.MARK_ONE);
                    isSuccess=isSuccess && planDetail.update();
                    //判断该计划外的其他事项是否全部完成
                    if(isAllFinish(planId,planDetailId)){
                        //变更该计划的状态
                        plan.setStatus(ConstantEnum.STATUS_FINISH.getValue());

                        Plan parentPlan=planService.findById(parentPlanId);
                        parentPlan.setFinishNum(parentPlan.getFinishNum()+1);

                        isSuccess=isSuccess && plan.update()&& parentPlan.update();
                    }
                }
            }
            return isSuccess;
        });
    }



}
