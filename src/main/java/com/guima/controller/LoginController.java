package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.kits.SysMsg;
import com.guima.base.kits.miniProgram.MiniProgramKit;
import com.guima.base.kits.miniProgram.MiniProgramLoginInfo;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Admin;
import com.guima.domain.User;
import com.guima.kits.Kit;
import com.guima.services.AdminService;
import com.guima.services.UserService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import java.util.HashMap;
import java.util.Map;

public class LoginController extends BaseController
{
    private final UserService s;
    private final AdminService adminService;

    public LoginController()
    {
        this.s = (UserService) ServiceManager.instance().getService("user");
        adminService=(AdminService) ServiceManager.instance().getService("admin");
    }


    /**
     * 用户登录
     */
    public void userLogin()
    {
        createUserOrLogin(true);
    }

    /**
     * 创建用户
     */
    public void createUser()
    {
        createUserOrLogin(false);
    }

    /**
     * 后台用户登录
     */
    public void adminLogin(){
        String userName = this.getPara("user_name");
        String password = this.getPara("password");
        if (Kit.isNotNull(userName) && Kit.isNotNull(password)&& validateCaptcha("captcha"))
        {
            Admin admin = adminService.get(userName,Kit.MD5(password));
            if (admin != null)
            {
                setSessionAttr(SysMsg.OsMsg.get("SESSION_KEY_ADMIN"), admin);
                setDomainName();
                doRenderSuccess("");
            }
        }else{
            doRenderError("请检查账户名、密码、验证码！");
        }
    }

    public void captcha()
    {
        renderCaptcha();
    }


    /**
     * 设置资源访问前缀
     */
    private void setDomainName(){
        setSessionAttr("DOMAIN_NAME", SysMsg.Config.get("DOMAIN_NAME"));
    }

    private void createUserOrLogin(boolean isLogin){
        MiniProgramLoginInfo accessToken= MiniProgramKit.getLoginInfo(getPara("code"));
        String openid=accessToken.getOpenid();
        if(Kit.isNull(openid)){
            LogKit.error("无法获取openid"+accessToken.getErrorCode()+":"+accessToken.getErrorMsg());
            doRenderError("无法获取openid");
            return;
        }
        User user=s.findByOpenid(openid);
        if(user==null){
            user=new User();
            String name=getPara("name");
            if(StrKit.isBlank(name)){
                doRenderError(SysMsg.OsMsg.get("PARAM_ERROR"));
                return;
            }
            user.init(openid,name,getPara("header_url"),getPara("city")
                    ,getPara("country"),getPara("lanuage"),getPara("gender"),
                    getPara("province"),accessToken.getSessionKey(),accessToken.getUnionid());
            user.superSave();
        }
        Map<String,String> map=new HashMap<>();
        map.put("user_id",user.getId());
        map.put("open_id",openid);
        if(isLogin){
            setSessionAttr(SysMsg.OsMsg.get("SESSION_KEY"),user);
            map.put("token",user.getToken());
        }
        doRenderSuccess(map);
    }
}
