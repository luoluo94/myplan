package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Plan;
import com.guima.services.*;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by Ran on 2019/11/2.
 */
public class PlanController extends BaseController {

    private PlanService planService;

    public PlanController()
    {
        planService=((PlanService)ServiceManager.instance().getService("plan"));
    }

    /**
     * 获取公开的计划列表 时间倒序
     */
    public void listAllPlans(){
        Page<Plan> page=planService.listAllPlans(getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }
}
