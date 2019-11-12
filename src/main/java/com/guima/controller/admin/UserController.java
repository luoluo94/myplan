package com.guima.controller.admin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.User;
import com.guima.kits.Constant;
import com.guima.services.DictionaryService;
import com.guima.services.UserService;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * Created by Ran on 2019/10/26.
 */
public class UserController extends BaseController {

    private final DictionaryService dictionaryService;
    private final UserService userService;

    public UserController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        userService=((UserService) ServiceManager.instance().getService("user"));
    }

    public void listUser(){
        Page<User> page=userService.pageList(getPara("name"),getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }


    public void listBackgroundMusicTheme(){
        doRenderSuccess(dictionaryService.listBackgroundMusicTheme());
    }

    public void listMusicTheme(){
        doRenderSuccess(dictionaryService.listType(Constant.MUSIC_THEME,null));
    }

    /**
     * 禁用用户
     */
    public void forbidden(){

    }

}
