package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Plan;
import com.guima.domain.PlanDetail;
import com.guima.services.*;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * Created by Ran on 2019/11/2.
 */
public class PlanController extends BaseController {

    private PlanService planService;
    private PlanDetailService planDetailService;

    public PlanController()
    {
        planService=((PlanService)ServiceManager.instance().getService("plan"));
        planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
    }

    /**
     * 获取公开的计划列表 时间倒序
     */
    public void listAllPlans(){
        Page<Record> page=planService.listAllPlans(getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }


    /**
     * 获取自定义的计划
     */
    public void listCustomPlans(){
        Page<Record> page=planService.listCustomPlans(1,200);
        doRenderPageRecord(page);
    }

    /**
     * 获取计划详情
     */
    public void getDetails(){
        List<PlanDetail> list=planDetailService.list(getPara("plan_id"));
        doRenderSuccess(list);
    }


}
