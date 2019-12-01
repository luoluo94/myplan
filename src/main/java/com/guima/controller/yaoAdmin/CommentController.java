package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.PlanComment;
import com.guima.services.*;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.regexp.internal.RE;

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
        Page<Record> page=planCommentService.pageList(null,isMarkDeleted,getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

}
