package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.PlanCalendar;
import com.guima.domain.User;
import com.guima.services.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ran on 2019/11/7.
 */
public class StatistcsController extends BaseController {

    private DictionaryService dictionaryService;
    private PlanService planService;
    private UserService userService;
    private PlanAnnexService planAnnexService;
    private PlanDetailService planDetailService;
    private PlanCommentService planCommentService;
    private PlanCalendarService planCalendarService;
    private PlanDetailAnnexService planDetailAnnexService;

    public StatistcsController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        s=planService;
        userService=((UserService) ServiceManager.instance().getService("user"));
        planAnnexService=((PlanAnnexService)ServiceManager.instance().getService("planannex"));
        planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
        planCommentService=((PlanCommentService)ServiceManager.instance().getService("plancomment"));
        planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        planDetailAnnexService=((PlanDetailAnnexService) ServiceManager.instance().getService("plandetailannex"));
    }

    public void getUserStatistics(){
        User user=getMyUser();
        //获取用户计划数
        PlanCalendar planCalendar=planCalendarService.findByCreator(user.getId());
        Map<String,Object> data=new HashMap<>();
        data.put("finish_num",planCalendar.getFinishedNum());
        data.put("finish_rate",planCalendar.getFinishRate());
    }


}
