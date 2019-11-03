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
        ScoreRecordService scoreRecordService=((ScoreRecordService)ServiceManager.instance().getService("scorerecord"));
        return Db.tx(()->{
            //创建计划
            if(Kit.isNull(plan.getId())){
                plan.save();
                //记录得分
                scoreRecordService.createScore(ScoreTypeEnum.CREATE_PLAN,plan.getCreator());
            }else {
                plan.update();
            }
            //创建计划的具体事项
            return planDetailService.createPlanDetail(plan,details);
        });
    }

    /**
     * 增加热度
     * 创建一个活动 + 100
     * 每个好友进入 + 100
     * 好友点一首歌 + 200
     */
    public synchronized boolean addHot(String userId,String hot){
        UserService userService=(UserService) ServiceManager.instance().getService("user");
        User user=userService.findById(userId);
        user.setHotPoint(new BigDecimal(user.getHotPoint()).add(new BigDecimal(hot)).toString());
        return user.update();
    }

    /**
     * 全部标记完成
     * @param plan
     * @return
     */
    public boolean markFinish(Plan plan){
        PlanDetailService planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        ScoreRecordService scoreRecordService=((ScoreRecordService)ServiceManager.instance().getService("scorerecord"));
        return Db.tx(()->{
            //将各事项标记为完成
            boolean isDetailFinish=planDetailService.markFinish(plan.getId());
            //将计划标记完成
            plan.setStatus(ConstantEnum.STATUS_FINISH.getValue());
            boolean isPlanFinish=plan.update();
            //增加得分
            return isDetailFinish && isPlanFinish && scoreRecordService.createScore(ScoreTypeEnum.FINISH_PLAN,plan.getCreator());
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



}
