package com.guima.base.interceptor;

import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.User;
import com.guima.kits.Kit;
import com.guima.kits.ShowInfoKit;
import com.guima.services.UserService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import java.util.Arrays;
import java.util.List;

public class GlobalSessionInterceptor implements Interceptor {

    public void intercept(Invocation inv) {
		String key = inv.getActionKey();
		List<String> ignoreList = Arrays.asList(SysMsg.Config.get("IGNORE_SESSION_CTL").split(","));
		Controller c = inv.getController();
		boolean admin = key.startsWith("/admin");
		//需要登录验证
		if (!ignoreList.contains(key)){
			//前台用户
			if(!admin){
				if(key.equals("/")){
					inv.invoke();
				}else{
					String token=c.getPara("token");
					if(Kit.isNotNull(token) && checkToken(c,token)){
						inv.invoke();
					}else{
						c.renderText(JsonKit.toJson(ShowInfoKit.getErrorMap("",false,"登录验证失败")));
					}
				}
			}else {
				//后台用户
				if(c.getSessionAttr(SysMsg.OsMsg.get("SESSION_KEY_ADMIN"))==null){
					c.redirect("/admin");
				}else {
					if(key.equals("/")){
						c.redirect("/admin/main");
					}else{
						inv.invoke();
					}
				}
			}
		}else
			inv.invoke();
    }
    
    public boolean checkToken(Controller c, String token){
    	boolean flag = false;
    	UserService userService = (UserService) ServiceManager.instance().getService("user");
    	if(Kit.isNotNull(token))
    	{
			User user=null;
			try{
				user = userService.findByToken(token);
			}catch (Exception e){
				e.printStackTrace();
			}
    		if(user != null)
    		{
    			flag = true;
    			c.setSessionAttr(SysMsg.OsMsg.get("SESSION_KEY"), user);
    		}
    	}
    	c.removeAttr("token");
    	return flag;
    }

}
