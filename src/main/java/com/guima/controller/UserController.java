package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Config;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.services.*;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ran on 2018/5/31.
 */
public class UserController extends BaseController {

    private final DictionaryService dictionaryService;
    private final UserService userService;
    private ConfigService configService;


    public UserController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        userService=((UserService) ServiceManager.instance().getService("user"));
        s=userService;
        configService = ((ConfigService) ServiceManager.instance().getService("config"));
    }


    public void getHelpInfo(){
        Map<String,String> map=new HashMap<>();
        map.put("qrcode_url",SysMsg.Config.get("WeChatQrcode"));
        map.put("message",SysMsg.OsMsg.get("HELP"));
        doRenderSuccess(map);
    }
}
