package com.guima.controller.yaoAdmin;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Sign;
import com.guima.domain.User;
import com.guima.enums.ConstantEnum;
import com.guima.services.ScoreRecordService;
import com.guima.services.SignService;
import com.guima.services.UserService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;

/**
 * Created by Ran on 2019/8/30.
 */
public class SignController extends BaseController{

    private SignService signService;
    private UserService userService;

    public SignController()
    {
        signService=((SignService)ServiceManager.instance().getService("sign"));
        userService=((UserService) ServiceManager.instance().getService("user"));
    }

    /**
     * 获取公开的说说列表 时间倒序
     */
    public void listAllSigns(){
        Page<Sign> page=signService.listAllSigns(getPageNumber(),getPageSize());
        doRenderPageRecord(page);
    }

    /**
     * 创建说说
     */
    public void saveSign(){
        User user=userService.findById(getPara("my_user_id"));
        String describer=getPara("describer");
        String photoUrl=getPara("photoUrl");
        if(StrKit.isBlank(describer)){
            doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
            return;
        }
        Sign sign=new Sign();
        sign.init(user.getHeaderUrl(),user.getId(),user.getName(),describer, ConstantEnum.PRIVACY_PUBLIC.getValue(),photoUrl);
        sign.save();
        doRender("sign_id",sign.getId(),StrKit.notBlank(sign.getId()));
    }
}
