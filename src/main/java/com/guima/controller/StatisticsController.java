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
public class StatisticsController extends BaseController {

    private PlanService planService;
    private PlanCalendarService planCalendarService;

    public StatisticsController()
    {
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
    }

    public void getUserStatistics(){
        User user=getMyUser();
        //获取用户计划数
        doRenderSuccess(planCalendarService.getStatics(user.getId()));
    }


}
