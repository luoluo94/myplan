package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.*;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.guima.services.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ran on 2019/8/30.
 */
public class PlanCalendarController extends BaseController{

    private DictionaryService dictionaryService;
    private PlanService planService;
    private PlanDetailService planDetailService;
    private PlanCommentService planCommentService;
    private PlanDetailAnnexService planDetailAnnexService;
    private UserActiveRecordService userActiveRecordService;

    public PlanCalendarController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        s=planService;
        planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        planCommentService=((PlanCommentService)ServiceManager.instance().getService("plancomment"));
        planDetailAnnexService=((PlanDetailAnnexService) ServiceManager.instance().getService("plandetailannex"));
        DoLikeService doLikeService = ((DoLikeService) ServiceManager.instance().getService("dolike"));
        userActiveRecordService=((UserActiveRecordService) ServiceManager.instance().getService("useractiverecord"));
    }

    public void getActiveDays(){
        User user=getMyUser();
        checkUser(user);
        String month=getPara("month");
        if(StrKit.isBlank(month)){
            doRenderParamError();
            return;
        }
        doRenderSuccess(userActiveRecordService.listUserActiveRecords(user,month));
    }


}
