package com.guima.controller;

import com.alibaba.fastjson.JSONObject;
import com.guima.base.controller.BaseController;
import com.guima.base.interceptor.AppInterceptor;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Config;
import com.guima.kits.Constant;
import com.guima.services.ConfigService;
import com.jfinal.aop.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ran on 2018/3/28.
 */
@Before(AppInterceptor.class)
public class ConfigController extends BaseController {

    private ConfigService configService;

    public ConfigController()
    {
        configService = ((ConfigService) ServiceManager.instance().getService("config"));
        s=configService;
    }

    public void get(){
        Config config=configService.getShareConfig();
        Map<String,String> map=new HashMap<>();
        map.put("text",config.getText()==null?"":config.getText());
        map.put("image",config.getImage()==null?"":config.getImage());
        doRenderSuccess(map);
    }
}
