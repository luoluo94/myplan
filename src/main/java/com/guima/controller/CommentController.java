package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Plan;
import com.guima.domain.PlanComment;
import com.guima.domain.PlanDetail;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.guima.kits.Kit;
import com.guima.services.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.taobao.api.internal.toplink.embedded.websocket.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Ran on 2019/8/30.
 */
public class CommentController extends BaseController{

    private DictionaryService dictionaryService;
    private PlanService planService;
    private UserService userService;
    private PlanAnnexService planAnnexService;
    private PlanDetailService planDetailService;
    private PlanCommentService planCommentService;
    private ScoreRecordService scoreRecordService;

    public CommentController()
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
     * 获取评论列表 时间正序
     */
    public void listComment(){
        String planId=getPara("plan_id");
        if(StringUtils.isEmpty(planId)){
            doRenderParamError();
            return;
        }
        Page<PlanComment> page=planCommentService.pageList(planId,null,getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

}
