package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.PlanNotice;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.services.PlanNoticeService;
import com.guima.services.PlanNoticeStatusService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

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
     * 创建／编辑通知
     */
    public void saveNotice(){
        String id=getPara("id");
        String title=getPara("title");
        String type=getPara("type");
        String receiverType=getPara("receiver_type");
        String createDate=getPara("create_date");
        String endDate=getPara("end_date");
        String content=getPara("content");
        if(StrKit.isBlank(title) || StrKit.isBlank(type) || StrKit.isBlank(receiverType)  || StrKit.isBlank(createDate)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        if(StrKit.isBlank(endDate)){
            endDate="2099-01-01";
        }
        PlanNotice notice;
        if(StrKit.isBlank(id)){
            notice=new PlanNotice();
            notice.init(title,type,receiverType,content,createDate,endDate);
            notice.setIsActive(Constant.IS_DELETED_YES);
            notice.save();
        }else{
            notice=planNoticeService.findById(id);
            notice.init(title,type,receiverType,content,createDate,endDate);
            notice.update();
        }
        doRender(StrKit.notBlank(notice.getId()));
    }



    /**
     * 获取所有通知
     */
    public void listAllNotices(){
        User user=getMyUser();
        checkUser(user);
        Page<PlanNotice> page=planNoticeService.listNotices(getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

    /**
     * 启用/禁用通知
     */
    public void enableNotice(){
        String noticeId=getPara("notice_id");
        if(StrKit.isBlank(noticeId)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        PlanNotice planNotice=planNoticeService.findById(noticeId);
        planNotice.setIsActive(planNotice.getIsActive()==0?1:0);
        planNotice.update();
        doRenderSuccess("");
    }

    /**
     * 生成通知
     */
    public void createNotice(){

    }

    public void getNotice(){
        String id=getPara("notice_id");
        PlanNotice planNotice=planNoticeService.findById(id);
        doRenderSuccess(planNotice);
    }
}
