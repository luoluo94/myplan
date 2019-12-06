package com.guima.jobs;

import com.guima.base.service.ServiceManager;
import com.guima.services.PlanCreateNumService;
import com.guima.services.PlanService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by Ran on 2019/11/6.
 */
public class OfficialPlanTask implements Job{

    private PlanService planService;

    public OfficialPlanTask(){
        planService=((PlanService) ServiceManager.instance().getService("plan"));
    }

    /**
     * 每晚定时修改官方任务的状态
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        planService.officialPlanTask();
    }
}
