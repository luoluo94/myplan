package com.guima.services;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.domain.*;
import com.guima.enums.ConstantEnum;
import com.guima.enums.ScoreTypeEnum;
import com.guima.kits.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlanService extends BaseService_<Plan>
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

    public Page<Plan> listPublicPlans(int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.gt("create_time", DateKit.getStartTime());
        param.equalsTo("privacy", ConstantEnum.PRIVACY_PUBLIC.getValue());
        param.equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE);
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

    public Page<Plan> listAllPlans(int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

    public Page<Plan> listMyPlans(User user,String status, int pageNumberStr, int pageSizeStr){
        QueryParam param=QueryParam.Builder();
        param.equalsTo("creator", user.getId());
        if(ConstantEnum.STATUS_END.getValue().equals(status)){
            param.in("status",new String[]{ConstantEnum.STATUS_FINISH.getValue(),ConstantEnum.STATUS_NOT_FINISH.getValue()});
        }else {
            param.equalsTo("status",status);
        }
        param.equalsTo(Constant.IS_DELETED_MARK,Constant.ACTIVE);
        param.descBy("create_time");
        return super.pageList(param,pageNumberStr+"", pageSizeStr+"");
    }

    /**
     * 创建/编辑计划
     * @param plan
     * @return
     */
    public boolean createPlan(Plan plan,String[] details){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        PlanCalendarService planCalendarService=((PlanCalendarService)ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            boolean isSuccess=false;
            //创建计划
            if(Kit.isNull(plan.getId())){
                isSuccess=plan.save() && planCalendarService.updateCreatePlanNum(plan.getCreator());
            }else {
                isSuccess=plan.update();
            }
            //创建计划的具体事项
            return isSuccess && planDetailService.createPlanDetail(plan,details);
        });
    }

    /**
     * 全部标记完成
     * @param plan
     * @return
     */
    public boolean markFinish(Plan plan){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        PlanCalendarService planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            //将各事项标记为完成
            boolean isDetailFinish=planDetailService.markFinish(plan.getId());
            //将计划标记完成
            plan.setStatus(ConstantEnum.STATUS_FINISH.getValue());
            boolean isPlanFinish=plan.update();
            //增加得分
            return isDetailFinish && isPlanFinish && planCalendarService.updateFinishPlanNum(plan.getCreator());
        });
    }

    /**
     * 标记未完成
     * @return
     */
    public boolean markUnFinish(Plan plan){
        PlanCalendarService planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        return Db.tx(()->{
            plan.setStatus(ConstantEnum.STATUS_NOT_FINISH.getValue());
            return plan.update() && planCalendarService.updateUnFinishPlanNum(plan.getCreator());
        });
    }

    /**
     * 复制计划
     * @return
     */
    public String copyPlan(Plan plan){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        Plan newPlan=new Plan();
        newPlan._setAttrs(plan);
        newPlan.setStatus(ConstantEnum.STATUS_DRAFT.getValue());
        List<PlanDetail> details=planDetailService.listPlanDetails(plan.getId());
        boolean isSuccess=Db.tx(()->{
            if(!newPlan.save())
                return false;

            PlanDetail planDetail;
            for (PlanDetail detail : details) {
                planDetail = new PlanDetail();
                planDetail.init(newPlan.getId(),detail.getPlanDetail(),detail.getSortIndex());
                if(!planDetail.save())
                    return false;
            }
            return true;
        });

        return isSuccess?newPlan.getId():null;
    }

    /**
     * 计算每天新增的计划数量
     */
    public int countPlanNum(){
        String startDatetime=DateKit.getDateOfDay(-1);
        String endDatetime=DateKit.getDaytime();
        return Db.queryInt("select count(*) as num from plan where create_time>= "+startDatetime+" and create_time<"+endDatetime);
    }



}
