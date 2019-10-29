package com.guima.base.interceptor;

import com.guima.base.kits.SysMsg;
import com.guima.domain.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import java.util.Arrays;
import java.util.List;

public class CheckAdminPermitInterceptor implements Interceptor
{
    @Override
    public void intercept(Invocation inv)
    {
        String key = inv.getActionKey();
        List<String> ignoreList = Arrays.asList(SysMsg.Config.get("IGNORE_SESSION_CTL").split(","));
        if (ignoreList.contains(key))
//        if (!key.startsWith("/admin") || ignoreList.contains(key))
            inv.invoke();
//        else
//        {
//            Controller c = inv.getController();
//            User user = c.getSessionAttr(SysMsg.OsMsg.get("SESSION_KEY_ADMIN"));
//            List<String> pathList = user.getActionPathList();
//            if (pathList.contains(key))
//                inv.invoke();
//            else
//                c.renderError(500);
//        }
    }
}
