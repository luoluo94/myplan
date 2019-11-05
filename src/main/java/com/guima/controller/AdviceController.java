package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Advice;
import com.guima.domain.Sign;
import com.guima.domain.User;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.services.AdviceService;
import com.guima.services.ScoreRecordService;
import com.guima.services.SignService;
import com.guima.services.UserService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by Ran on 2019/8/30.
 */
public class AdviceController extends BaseController{

    private AdviceService adviceService;

    public AdviceController()
    {
        adviceService=((AdviceService) ServiceManager.instance().getService("advice"));
    }

    /**
     * 创建问题
     */
    public void saveAdvice(){
        User user=getMyUser();
        checkUser(user);
        String content=getPara("content");
        if(StrKit.isBlank(content)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        Advice advice=new Advice();
        advice.init(user.getHeaderUrl(),user.getId(),user.getName(),content);
        advice.save();
        doRender(StrKit.notBlank(advice.getId()));
    }

    /**
     * 获取问题反馈列表 时间倒序
     */
    public void listMyAdvices(){
        User user=getMyUser();
        checkUser(user);
        Page<Advice> page=adviceService.listAdvices(user,getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }
}
