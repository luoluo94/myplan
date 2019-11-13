package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Plan;
import com.guima.services.*;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by Ran on 2019/11/2.
 */
public class PlanController extends BaseController {

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
        userService=((UserService) ServiceManager.instance().getService("user"));
        planAnnexService=((PlanAnnexService)ServiceManager.instance().getService("planannex"));
        planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        planCommentService=((PlanCommentService)ServiceManager.instance().getService("plancomment"));
        scoreRecordService=((ScoreRecordService) ServiceManager.instance().getService("scorerecord"));
    }

    /**
     * 获取公开的计划列表 时间倒序
     */
    public void listAllPlans(){
        Page<Plan> page=planService.listAllPlans(getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }
}
