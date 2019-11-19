package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Advice;
import com.guima.services.AdviceService;
import com.jfinal.plugin.activerecord.Page;

import java.util.Date;

/**
 * Created by Ran on 2019/11/4.
 */
public class AdviceController extends BaseController{

    private AdviceService adviceService;

    public AdviceController()
    {
        adviceService=((AdviceService) ServiceManager.instance().getService("advice"));
    }

    /**
     * 获取问题反馈列表 时间倒序
     */
    public void listAllAdvices(){
        Page<Advice> page=adviceService.listAdvices(null,getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

    /**
     * 添加回复
     */
    public void addReply(){
        String adviceId=getPara("advice_id");
        String reply=getPara("reply");
        Advice advice=adviceService.findById(adviceId);
        advice.setReply(reply);
        advice.setReplyTime(new Date());
        doRender(advice.update());
    }


}
