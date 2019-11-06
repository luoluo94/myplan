package com.guima.jobs;

import com.guima.base.service.ServiceManager;
import com.guima.services.PlanService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by Ran on 2019/11/6.
 */
public class StatisticsTask implements Job{

    private PlanService planService;

    public StatisticsTask(){
        planService=((PlanService) ServiceManager.instance().getService("plan"));
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("定时任务正在执行");
        System.out.println(planService.toString());
        System.out.println("定时任务正在执行");
    }
}
