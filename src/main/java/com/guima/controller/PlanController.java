package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Plan;
import com.guima.domain.PlanComment;
import com.guima.domain.PlanDetail;
import com.guima.domain.User;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.guima.kits.Kit;
import com.guima.services.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;
import java.util.List;

/**
 * Created by Ran on 2019/8/30.
 */
public class PlanController extends BaseController{

    private DictionaryService dictionaryService;
    private PlanService planService;
    private UserService userService;
    private PlanAnnexService planAnnexService;
    private PlanDetailService planDetailService;
    private PlanCommentService planCommentService;
    private ScoreRecordService scoreRecordService;

    public PlanController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        s=planService;
        userService=((UserService) ServiceManager.instance().getService("user"));
        planAnnexService=((PlanAnnexService)ServiceManager.instance().getService("planannex"));
        planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        planCommentService=((PlanCommentService)ServiceManager.instance().getService("plancomment"));
        scoreRecordService=((ScoreRecordService) ServiceManager.instance().getService("scorerecord"));
    }

    /**
     * 创建/编辑计划
     */
    public void savePlan(){
        User user=getMyUser();
        String id=getPara("id");
        Plan plan=new Plan();
        if(Kit.isNotNull(id)){
            plan=planService.findById(id);
            if(plan==null){
                doRenderError("该计划不存在");
                return;
            }
            if(!plan.getStatus().equals(ConstantEnum.STATUS_DRAFT.getValue())){
                doRenderError("该计划不允许更改");
                return;
            }
            if(plan.getIsDeleted().equals(Constant.DELETED)){
                doRenderError("该计划已删除，无法更改");
                return;
            }
        }

        String title=getPara("title");
        String category=getPara("category");
        String endDate=getPara("endDate");
        String privacy=getPara("privacy");
        String startDate=getPara("startDate");
        String status=getPara("status");
        //计划的具体事项
        String[] planDetails=getParaValues("details");
        if(planDetails.length==0){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        if(StrKit.isBlank(title)||Kit.isNull(category)
                || StrKit.isBlank(endDate)|| StrKit.isBlank(privacy)
                || StrKit.isBlank(startDate)|| StrKit.isBlank(status)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        plan.init(title,category,user.getId(),user.getHeaderUrl(),user.getName(),new Date(),DateKit.stringToDate(endDate),
                privacy,DateKit.stringToDate(startDate),status);
        planService.createPlan(plan,planDetails);
        doRender("plan_id",plan.getId(),StrKit.notBlank(plan.getId()));
    }

    /**
     * 获取公开的计划列表 时间倒序
     */
    public void listPublicPlan(){
        String category=getPara("category");
        Page<Plan> page=planService.listPublicPlans(category,getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

    /**
     * 获取我的计划列表 时间倒序
     */
    public void listMyPlan(){
        User user=getMyUser();
        String status=getPara("status");
        Page<Plan> page=planService.listMyPlans(user,status,getPageNumber(),getPageSize());
//        List<Plan> planList=page.getList();
//        for (Record plan:planList){
//            plan.set("endDateDesc",DateKit.getDateDesc(plan.getEndDate()));
//            plan.setEndDateDesc(DateKit.getDateDesc(plan.getEndDate()));
//            if(plan.getStatus().equals(ConstantEnum.STATUS_ONGOING.getValue())){
//                plan.setOverdue(DateKit.isOverdueIncludeDay(DateKit.now(),DateKit.dateToString(plan.getEndDate())));
//            }
//        }
        doRenderPageRecord(page);
    }

    /**
     * 获取计划事项 时间正序
     */
    public void listPlanDetails(){
        String planId=getPara("plan_id");
        List<PlanDetail> details=planDetailService.list(planId);
        doRenderSuccess(details);
    }

    /**
     * 获取某个计划的信息
     */
    public void getPlan(){
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        doRenderSuccess(plan);
    }

    /**
     * 删除计划
     */
    public void removePlan(){
        User user=getMyUser();
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        if(plan==null){
            doRenderError("该计划不存在");
            return;
        }
        if(!plan.getCreator().equals(user.getId())){
            doRenderError();
        }
        plan.setIsDeleted(Constant.IS_DELETED_YES);
        doRender(plan.update());
    }

    /**
     * 设置计划事项的完成度
     */
    public void setFinishPercentage(){
        String planDetailId=getPara("plan_detail_id");
        Integer finishPercentage=Integer.valueOf(getPara("finish_percentage"));
        PlanDetail planDetail=planDetailService.findById(planDetailId);
        planDetail.setFinishPercentage(finishPercentage);
        planDetail.setMarkTime(new Date());
        doRender(planDetail.update());
    }

    /**
     * 添加评论
     */
    public void addComment(){
        comment(getPara("comment"));
    }

    /**
     * 点赞
     */
    public void doLike(){
        comment("我为你点赞");
    }

    /**
     * 标记完成
     */
    public void markFinish(){
        User user=getMyUser();
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        if(!plan.getCreator().equals(user.getId())){
            doRenderError();
        }
        doRender(planService.markFinish(plan));
    }

    /**
     * 标记未完成
     */
    public void markNotFinish(){
        User user=getMyUser();
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        if(!plan.getCreator().equals(user.getId())){
            doRenderError();
        }
        plan.setStatus(ConstantEnum.STATUS_NOT_FINISH.getValue());
        doRender(plan.update());
    }

    /**
     * 复制计划
     */
    public void copyPlan(){
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        String newPlanId=planService.copyPlan(plan);
        doRender("plan_id",newPlanId,StrKit.notBlank(newPlanId));
    }

    private void comment(String comment){
        User user=getMyUser();
        String planId=getPara("plan_id");
        PlanComment planComment=new PlanComment();
        planComment.setComment(comment);
        planComment.setCreateTime(new Date());
        planComment.setCreatorHeaderUrl(user.getHeaderUrl());
        planComment.setCreatorName(user.getName());
        planComment.setCreatorId(user.getId());
        planComment.setIsDeleted(Constant.IS_DELETED_NO);
        planComment.setPlanId(planId);
        doRender(planComment.save());
    }

}
