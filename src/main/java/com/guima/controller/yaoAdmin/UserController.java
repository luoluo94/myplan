package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Admin;
import com.guima.domain.User;
import com.guima.domain.UserRecord;
import com.guima.kits.Constant;
import com.guima.services.DictionaryService;
import com.guima.services.InterfaceConfigService;
import com.guima.services.UserRecordService;
import com.guima.services.UserService;
import com.jfinal.plugin.activerecord.Page;

import java.util.Date;
import java.util.List;

/**
 * Created by Ran on 2019/10/26.
 */
public class UserController extends BaseController {

    private final DictionaryService dictionaryService;
    private final UserRecordService userRecordService;
    private final UserService userService;

    public UserController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        userService=((UserService) ServiceManager.instance().getService("user"));
        userRecordService = ((UserRecordService) ServiceManager.instance().getService("userrecord"));
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
     * 禁用用户/解禁用户
     */
    public void banned(){
        //被操作用户id/name
        String creator=getPara("creator");
        String creatorName=getPara("creatorName");
        //操作者描述
        String recordContent=getPara("recordContent");
        //true 为禁用 false 为解禁
        Boolean isBanned=Boolean.valueOf(getPara("isBanned"));
        UserRecord userRecord=new UserRecord();
        userRecord.setCreateTime(new Date());
        userRecord.setCreator(creator);
        userRecord.setRecordContent(isBanned?"禁用理由："+recordContent:"解禁："+recordContent);
        userRecord.setName(creatorName);
        Admin admin=getMyAdmin();
        userRecord.setOperator(admin.getUserName());
        User user=userService.findById(creator);
        doRender(userRecordService.banned(userRecord,user,isBanned));
    }

    /**
     * 列出用户记录
     */
    public void listUserRecords(){
        String creator=getPara("creatorId");
        List<UserRecord> userRecordList=userRecordService.list(creator);
        doRenderSuccess(userRecordList);
    }

}
