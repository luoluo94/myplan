package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.PlanComment;
import com.guima.services.*;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by Ran on 2019/8/30.
 */
public class CommentController extends BaseController{

    private PlanCommentService planCommentService;

    public CommentController()
    {
        planCommentService=((PlanCommentService)ServiceManager.instance().getService("plancomment"));
    }

    /**
     * 获取评论列表 时间倒序
     */
    public void listAllComments(){
        String isMarkDeleted=getPara("mark_deleted");
        Page<PlanComment> page=planCommentService.pageList(null,isMarkDeleted,getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

}
