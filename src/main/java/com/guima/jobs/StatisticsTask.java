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
public class StatisticsTask implements Job{

    private PlanCreateNumService planCreateNumService;

    public StatisticsTask(){
        planCreateNumService=((PlanCreateNumService) ServiceManager.instance().getService("plancreatenum"));
    }

    /**
     * 计算每天的计划创建量
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        planCreateNumService.countPlanNum();
    }
}
