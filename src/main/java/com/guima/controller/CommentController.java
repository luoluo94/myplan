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

    private PlanService planService;
    private PlanCommentService planCommentService;

    public CommentController()
    {
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        s=planService;
        planCommentService=((PlanCommentService)ServiceManager.instance().getService("plancomment"));
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
