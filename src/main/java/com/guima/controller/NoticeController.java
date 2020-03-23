package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.*;
import com.guima.kits.Constant;
import com.guima.services.AdviceService;
import com.guima.services.PlanNoticeService;
import com.guima.services.PlanNoticeStatusService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.taobao.api.internal.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ran on 2019/8/30.
 */
public class NoticeController extends BaseController{

    private PlanNoticeService planNoticeService;

    private PlanNoticeStatusService planNoticeStatusService;

    public NoticeController()
    {
        planNoticeService=((PlanNoticeService) ServiceManager.instance().getService("plannotice"));

        planNoticeStatusService=((PlanNoticeStatusService) ServiceManager.instance().getService("plannoticestatus"));
    }

    /**
     * 获取问题反馈列表 时间倒序
     */
    public void listMyNotice(){
        User user=getMyUser();
        checkUser(user);
        Page<Record> page=planNoticeStatusService.listUserNotices(user,getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

    /**
     * 页面加载时 生成用户通知 获取用户的未读通知个数
     */
    public void getMyUnReadNum(){
        User user=getMyUser();
        checkUser(user);
        //生成用户通知
        planNoticeStatusService.createMyNotices(user);
        Long unReadNum=planNoticeStatusService.getMyUnReadNum(user);
        doRenderSuccess(unReadNum==null?0:unReadNum);
    }

    public void getNotice(){
        String noticeId=getPara("notice_id");
        if(StrKit.isBlank(noticeId)){
            doRenderParamError();
            return;
        }
        PlanNotice planNotice=planNoticeService.findById(noticeId);
        doRenderSuccess(planNotice);
    }

    /**
     * 标记已读状态
     */
    public void markReadStatus(){
        User user=getMyUser();
        checkUser(user);
        String readNoticeId=getPara("read_notice_id");
        if(StrKit.isBlank(readNoticeId)){
            doRenderParamError();
            return;
        }
        PlanNoticeStatus planNoticeStatus=planNoticeStatusService.findById(readNoticeId);
        planNoticeStatus.setReadStatus(Constant.DELETED);
        planNoticeStatus.update();
        doRender(true);
    }

}
