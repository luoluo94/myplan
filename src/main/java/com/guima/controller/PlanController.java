package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.*;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.guima.kits.Kit;
import com.guima.services.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;

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
    private PlanDetailAnnexService planDetailAnnexService;

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
        planDetailAnnexService=((PlanDetailAnnexService) ServiceManager.instance().getService("plandetailannex"));
    }

    /**
     * 创建/编辑计划
     */
    public void savePlan(){
        User user=getMyUser();
        Plan plan=new Plan();
        String title=getPara("title");
        String category=getPara("category");
        String endDate=getPara("endDate");
        String privacy=getPara("privacy");
        String startDate=getPara("startDate");
        String status=getPara("status");
        //计划的具体事项
        String[] planDetails=getParaValues("details");
        if(planDetails.length==0){
            doRenderParamError();
            return;
        }
        if(StrKit.isBlank(title)||Kit.isNull(category)
                || StrKit.isBlank(endDate)|| StrKit.isBlank(privacy)
                || StrKit.isBlank(startDate)|| StrKit.isBlank(status)){
            doRenderParamError();
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
        Page<Plan> page=planService.listPublicPlans(getPageNumber(),getPageSize());
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
//        for (Plan plan:planList){
//            plan.setEndDateDesc(DateKit.getDateDesc(plan.getEndDate()));
//            if(plan.getStatus().equals(ConstantEnum.STATUS_ONGOING.getValue())){
//                plan.setOverdue(DateKit.isOverdueIncludeDay(DateKit.now(),DateKit.dateToString(plan.getEndDate())));
//            }
//            plan.setStatusDesc(getStatusDesc(plan.getStatus()));
//        }
        doRenderPageRecord(page);
    }

    private String getStatusDesc(String planStatus){
        if (planStatus.equals(ConstantEnum.STATUS_ONGOING.getValue())){
            return ConstantEnum.STATUS_ONGOING.getDesc();
        }
        if (planStatus.equals(ConstantEnum.STATUS_FINISH.getValue())){
            return ConstantEnum.STATUS_FINISH.getDesc();
        }
        if (planStatus.equals(ConstantEnum.STATUS_NOT_FINISH.getValue())){
            return ConstantEnum.STATUS_NOT_FINISH.getDesc();
        }
        return "";
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
        List<PlanDetail> details=planDetailService.list(planId);
        Map renderData=new HashMap<>();
        renderData.put("plan",plan);
        renderData.put("details",details);
        doRenderSuccess(renderData);
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
     * 删除评论
     */
    public void removeComment(){
        User user=getMyUser();
        String planCommentId=getPara("plan_comment_id");
        PlanComment planComment=planCommentService.findById(planCommentId);
        if(planComment==null){
            doRenderError("该条评论不存在");
            return;
        }
        Plan plan=planService.findById(planComment.getPlanId());
        if(plan==null){
            doRenderParamNull();
            return;
        }
        checkCreator(user,plan.getCreator());
        planComment.setIsDeleted(Constant.IS_DELETED_YES);
        doRender(planComment.update());
    }

    /**
     * 点赞
     */
    public void doLike(){
        comment("我为你点赞");
    }

    /**
     * 标记完成／未完成
     */
    public void markStatus(){
        User user=getMyUser();
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        checkCreator(user,plan.getCreator());
        String isFinishStr=getPara("is_finish");
        Boolean isFinish=isFinishStr.equals(Constant.ACTIVE)?true:false;
        if(isFinish){
            doRender(planService.markFinish(plan));
        }else{
            doRender(planService.markUnFinish(plan));
        }
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

    /**
     * 获取某个计划详情的附件
     */
    public void listPlanAnnex(){
        String planDetailId=getPara("plan_detail_id");
        List<PlanDetailAnnex> list=planDetailAnnexService.list(planDetailId);
        doRenderSuccess(list);
    }

    /**
     * 添加某个计划详情的附件
     */
    public void addPlanAnnex(){
        String planDetailId=getPara("plan_detail_id");
        String annexUrl=getPara("annex_url");
        PlanDetailAnnex planAnnex=new PlanDetailAnnex();
        planAnnex.setPlanDetailId(planDetailId);
        planAnnex.setAnnexUrl(annexUrl);
        planAnnex.setIsDeleted(Constant.IS_DELETED_YES);
        doRender(planDetailService.addPlanAnnex(planDetailId,planAnnex));
    }

    /**
     * 删除某个计划详情的附件
     */
    public void removePlanAnnex(){
        User user=getMyUser();
        String planDetailAnnexId=getPara("plan_detail_annex_id");
        PlanDetailAnnex planDetailAnnex=planDetailAnnexService.findById(planDetailAnnexId);
        if(planDetailAnnex==null || Constant.IS_DELETED_YES==planDetailAnnex.getIsDeleted()){
            doRenderParamNull();
            return;
        }
        PlanDetail planDetail=planDetailService.findById(planDetailAnnex.getPlanDetailId());
        Plan plan=planService.findById(planDetail.getPlanId());
        checkCreator(user,plan.getCreator());
        doRender(planDetailService.removePlanAnnex(planDetailAnnex));
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
        planComment.setMarkDeleted(Constant.MARK_ZERO);
        doRender(planComment.save());
    }

}
