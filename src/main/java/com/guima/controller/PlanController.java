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
    private PlanDetailService planDetailService;
    private PlanCommentService planCommentService;
    private PlanDetailAnnexService planDetailAnnexService;
    private DoLikeService doLikeService;
    private SignService signService;

    public PlanController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        s=planService;
        planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        planCommentService=((PlanCommentService)ServiceManager.instance().getService("plancomment"));
        planDetailAnnexService=((PlanDetailAnnexService) ServiceManager.instance().getService("plandetailannex"));
        doLikeService=((DoLikeService) ServiceManager.instance().getService("dolike"));
        signService=((SignService) ServiceManager.instance().getService("sign"));
    }

    /**
     * 创建／编辑计划
     */
    public void savePlan(){
        User user=getMyUser();
        checkUser(user);
        checkBanned(user);
        Plan plan=new Plan();
        String title=getPara("title");
        String endDate=getPara("endDate");
        String startDate=getPara("startDate");
        String planId=getPara("id");
        //计划的具体事项
        String[] planDetails=getParaValues("details");
        String[] planDetailIds=getParaValues("detailIds");
        if(planDetails.length==0){
            doRenderParamError();
            return;
        }
        if(StrKit.isBlank(title)
                || StrKit.isBlank(endDate)
                || StrKit.isBlank(startDate)){
            doRenderParamError();
            return;
        }
        //判断编辑计划的必填项
        if(StrKit.notBlank(planId)){
            plan=planService.findById(planId);
            if(planDetailIds==null || planDetails.length!=planDetailIds.length){
                doRenderParamError();
                return;
            }
        }
        plan.init(title,user.getId(),DateKit.stringToDate(endDate),
                ConstantEnum.PRIVACY_SELF.getValue(),DateKit.stringToDate(startDate));
        planService.createPlan(plan,planDetails,planDetailIds);
        doRender("plan_id",plan.getId(),StrKit.notBlank(plan.getId()));
    }

    /**
     * 获取公开的计划列表 时间倒序
     */
    public void listPublicPlan(){
        Page<Record> page=planService.listPublicPlans(getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

    /**
     * 获取我的计划列表 时间倒序
     * 等待删除
     */
    public void listMyPlan(){
        User user=getMyUser();
        checkUser(user);
        String status=getPara("status");
        Page<Record> page=planService.listMyOwenPlans(user,status,getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

    /**
     * 获取我的计划列表 时间倒序
     */
    public void listMyOwenPlans(){
        User user=getMyUser();
        checkUser(user);
        String status=getPara("status");
        Page<Record> page=planService.listMyOwenPlans(user,status,getPageNumberInt(),getPageSizeInt());
        List list=page.getList();
        String today=DateKit.getToday();
        for (Object record:list){
            Record re=((Record)record);
            re.set("end_date_desc",DateKit.getDateDesc(today,DateKit.dateToDayString(re.get("end_date"))));
        }
        doRenderPageRecord(page);
    }

    /**
     * 获取官方挑战列表 时间倒序
     */
    public void listOfficialPlans(){
        String status=getPara("status");
        Page<Record> page=planService.listOfficialPlans(status,getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

    /**
     * 获取我的挑战列表
     */
    public void listMyChallengePlans(){
        User user=getMyUser();
        checkUser(user);
        String status=getPara("status");
        Page<Record> page=planService.listMyChallengePlans(user,status,getPageNumberInt(),getPageSizeInt());
        List list=page.getList();
        String today=DateKit.getToday();
        for (Object record:list){
            Record re=((Record)record);
            re.set("end_date_desc",DateKit.getDateDesc(today,DateKit.dateToDayString(re.get("end_date"))));
        }
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
        Record plan=planService.findRecordById(planId);
        if(plan.get("is_deleted").equals(Constant.DELETED)){
            doRenderError("该计划已删除");
        }
        List<PlanDetail> details=planDetailService.list(planId);
        Map renderData=new HashMap<>();
        renderData.put("plan",plan);
        renderData.put("details",details);
        doRenderSuccess(renderData);
    }

    public void getPlanDetail(){
        String planDetailId=getPara("plan_detail_id");
        PlanDetail planDetail=planDetailService.findById(planDetailId);
        doRenderSuccess(planDetail.getPlanDetail());
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
     * 判断编辑计划时能否删除某个计划详情
     * 该计划详情下如果含有打卡记录则进行提示
     */
    public void canRemoveDetail(){
        User user=getMyUser();
        String detailId=getPara("detail_id");
        String planId=getPara("plan_id");
        boolean hasSign=signService.hasSign(user.getId(),planId,detailId);
        doRenderSuccess(!hasSign);
    }

    /**
     * 编辑计划时删除某个计划详情
     */
    public void removeDetail(){
        User user=getMyUser();
        String detailId=getPara("detail_id");
        String planId=getPara("plan_id");
        boolean isSuccess=planDetailService.removeDetail(user.getId(),planId,detailId);
        doRenderSuccess(isSuccess);
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
        User user=getMyUser();
        String planId=getPara("plan_id");
        String comment=getPara("comment");
        PlanComment planComment=new PlanComment(comment,user.getId(),planId);
        doRender(planComment.save());
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
        User user=getMyUser();
        String planId=getPara("plan_id");
        String comment=" \uD83D\uDC4D  \uD83D\uDC4D  \uD83D\uDC4D ";
        PlanComment planComment=new PlanComment(comment,user.getId(),planId);
        Plan plan=planService.findById(planComment.getPlanId());
        doRenderSuccess(planService.doLike(planComment,plan,user));
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
     * 标记完成／未完成
     */
    public void markFinishStatus(){
        User user=getMyUser();
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        checkCreator(user,plan.getCreator());
        String isFinishStr=getPara("is_finish");
        Boolean isFinish=isFinishStr.equals(Constant.ACTIVE)?true:false;
        if(isFinish){
            doRender(planService.markFinish2(plan));
        }else{
            doRender(planService.markUnFinish(plan));
        }
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
        planAnnex.setIsDeleted(Constant.IS_DELETED_NO);
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

    /**
     * 判断该用户是否对该计划进行了点赞
     * true 为已点赞
     */
    public void isDoLike(){
        String planId=getPara("plan_id");
        User user=getMyUser();
        DoLike doLike=doLikeService.findDoLike(planId,user.getId());
        doRender(doLike!=null);
    }

    /**
     * 判断该用户是否加入了该计划
     * true 为已加入
     */
    public void isJoinChallenge(){
        String planId=getPara("plan_id");
        User user=getMyUser();
        Plan plan=planService.find(user.getId(),planId);
        doRender(plan!=null);
    }

    /**
     * 加入官方计划
     */
    public void joinChallenge(){
        User user=getMyUser();
        checkUser(user);
        String planId=getPara("plan_id");
        Plan plan=planService.findById(planId);
        if (plan==null){
            doRenderParamNull();
            return;
        }
        doRender(planService.joinChallenge(user,plan));
    }

}
