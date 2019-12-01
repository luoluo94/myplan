package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.*;
import com.guima.enums.ConstantEnum;
import com.guima.kits.Constant;
import com.guima.kits.DateKit;
import com.guima.kits.Kit;
import com.guima.services.*;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;
import java.util.List;

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
     * 创建说说
     */
    public void saveSign(){
        User user=getMyUser();
        checkUser(user);
        checkBanned(user);
        Sign sign=new Sign();
        String describer=getPara("describer");
        String photoUrl=getPara("photoUrl");
        if(StrKit.isBlank(describer)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        sign.init(user.getId(),describer,ConstantEnum.PRIVACY_PUBLIC.getValue(),photoUrl);
        sign.save();
        doRender("sign_id",sign.getId(),StrKit.notBlank(sign.getId()));
    }

    /**
     * 获取公开的说说列表 时间倒序
     */
    public void listPublicSigns(){
        Page<Record> page=signService.listPublicSigns(getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

    /**
     * 获取我的计划列表 时间倒序
     */
    public void listMySigns(){
        User user=getMyUser();
        Page<Record> page=signService.listMySigns(user,getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

    /**
     * 删除打卡记录
     */
    public void removeSign(){
        User user=getMyUser();
        String signId=getPara("sign_id");
        Sign sign=signService.findById(signId);
        if(sign==null){
            doRenderError("该条打卡记录不存在");
            return;
        }
        if(!sign.getCreator().equals(user.getId())){
            doRenderError();
        }
        sign.setIsDeleted(Constant.IS_DELETED_YES);
        doRender(sign.update());
    }
}
