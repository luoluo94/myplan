package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.ServiceManager;
import com.guima.domain.*;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.guima.services.*;
import com.jfinal.plugin.activerecord.Page;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ran on 2019/10/26.
 */
public class UserController extends BaseController {

    private final DictionaryService dictionaryService;
    private final UserRecordService userRecordService;
    private final UserService userService;
    private final AdminExceptionRecordService exceptionRecordService;
    private final CustomUserService customUserService;

    public UserController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        userService=((UserService) ServiceManager.instance().getService("user"));
        userRecordService = ((UserRecordService) ServiceManager.instance().getService("userrecord"));
        exceptionRecordService = ((AdminExceptionRecordService) ServiceManager.instance().getService("adminexceptionrecord"));
        customUserService= ((CustomUserService) ServiceManager.instance().getService("customuser"));
    }

    public void listUser(){
        Page<User> page=userService.pageList(getPara("name"),getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

    /**
     * 禁用用户/解禁用户
     */
    public void ban(){
        //被操作用户id/name
        String creatorId=getPara("creator");
        User creator=userService.findById(creatorId);
        //操作者描述
        String recordContent=getPara("recordContent");
        //true 为禁用 false 为解禁
        Boolean isBanned=Boolean.valueOf(getPara("isBanned"));
        UserRecord userRecord=new UserRecord();
        userRecord.setCreateTime(new Date());
        userRecord.setCreator(creatorId);
        userRecord.setRecordContent(isBanned?"禁用理由："+recordContent:"解禁："+recordContent);
        userRecord.setName(creator.getName());
        Admin admin=getMyAdmin();
        userRecord.setOperator(admin.getUserName());
        doRender(userRecordService.banned(userRecord,creator,isBanned));
    }

    /**
     * 列出用户记录
     */
    public void listUserRecords(){
        String creator=getPara("creatorId");
        List<UserRecord> userRecordList=userRecordService.list(creator);
        doRenderSuccess(userRecordList);
    }

    /**
     * 列出所有用户记录
     */
    public void listAllUserRecords(){
        Page<UserRecord> userRecordList=userRecordService.pageList(getPageNumber(),getPageSize());
        doRenderPageRecord(userRecordList);
    }

    /**
     * 列出异常登录记录
     */
    public void listAdminRecords(){
        Page<AdminExceptionRecord> userRecordList=exceptionRecordService.pageList(getPageNumber(),getPageSize());
        doRenderPageRecord(userRecordList);
    }

    /**
     * 创建用户
     */
    public void createUser(){
        String name=getPara("name");
        String headerUrl=getPara("header_url");
        String desc=getPara("desc");
        User user=new User();
        user.init(UUID.randomUUID().toString(),name,headerUrl,""
                ,"","","",
                "",UUID.randomUUID().toString(),UUID.randomUUID().toString());
        userService.createUser(user);
        CustomUser customUser=new CustomUser(user.getId(),name,headerUrl,desc,user.getToken());
        customUser.superSave();
        doRenderSuccess("");
    }

    public void listCustomUsers(){
        Page<CustomUser> page=customUserService.pageList(QueryParam.Builder(),getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

}
