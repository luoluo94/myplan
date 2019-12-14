package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.PlanCalendar;
import com.guima.domain.User;
import com.guima.kits.DateKit;
import com.guima.services.*;
import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ran on 2019/11/7.
 */
public class StatisticsController extends BaseController {

    private PlanService planService;
    private PlanCalendarService planCalendarService;
    private UserActiveRecordService userActiveRecordService;

    public StatisticsController()
    {
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        planCalendarService=((PlanCalendarService) ServiceManager.instance().getService("plancalendar"));
        userActiveRecordService=((UserActiveRecordService) ServiceManager.instance().getService("useractiverecord"));
    }

    public void getUserStatistics(){
        User user=getMyUser();
        //获取用户计划数
        Map map=planCalendarService.getStatics(user.getId());
        String beginDate= DateKit.format(user.getCreateTime(),DateKit.DATE_PATTERN);
        int loginDays=DateKit.compareDays(beginDate,DateKit.getToday());
        map.put("login_days",Math.abs(loginDays)+1);
        doRenderSuccess(map);
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
