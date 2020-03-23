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

    private PlanDetailService planDetailService;

    public SignController()
    {
        signService=((SignService)ServiceManager.instance().getService("sign"));
        planDetailService=((PlanDetailService)ServiceManager.instance().getService("plandetail"));
    }

    /**
     * 创建打卡
     */
    public void savePlanSign(){
        User user=getMyUser();
        checkUser(user);
        Sign sign=new Sign();
        String describer=getPara("describer");
        String photoUrl=getPara("photoUrl");
        String planId=getPara("plan_id");
        String planDetailId=getPara("plan_detail_id");
        String privacy=getPara("privacy");
        if(StrKit.isBlank(privacy)){
            privacy=ConstantEnum.PRIVACY_SELF.getValue();
        }
        if(StrKit.isBlank(describer) || StrKit.isBlank(planDetailId) || StrKit.isBlank(planId)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        sign.init(user.getId(),describer,privacy,photoUrl,planId,planDetailId);
        doRender(planDetailService.savePlanSign(planId,planDetailId,sign));
    }

    /**
     * 记录心情
     */
    public void saveSign(){
        User user=getMyUser();
        checkUser(user);
        Sign sign=new Sign();
        String describer=getPara("describer");
        String photoUrl=getPara("photoUrl");
        if(StrKit.isBlank(describer)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        sign.init(user.getId(),describer,ConstantEnum.PRIVACY_SELF.getValue(),photoUrl,null,null);
        doRender(sign.save());
    }

    /**
     * 获取公开的说说列表 时间倒序
     */
    public void listPublicSigns(){
        Page<Record> page=signService.listPublicSigns(getPageNumberInt(),getPageSizeInt());
        doRenderPageRecord(page);
    }

    /**
     * 获取计划下的打卡记录 时间倒序
     */
    public void listPlanSigns(){
        String planId=getPara("plan_id");
        if(StrKit.isBlank(planId)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        Page<Record> page=signService.listPlanSigns(planId,getPageNumberInt(),getPageSizeInt());
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
        doRender(signService.removeSign(sign));
    }
}
