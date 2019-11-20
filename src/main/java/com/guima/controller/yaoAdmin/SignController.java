package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Sign;
import com.guima.services.ScoreRecordService;
import com.guima.services.SignService;
import com.guima.services.UserService;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by Ran on 2019/8/30.
 */
public class SignController extends BaseController{

    private SignService signService;

    public SignController()
    {
        signService=((SignService)ServiceManager.instance().getService("sign"));
    }

    /**
     * 获取公开的说说列表 时间倒序
     */
    public void listAllSigns(){
        Page<Sign> page=signService.listAllSigns(getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }
}
