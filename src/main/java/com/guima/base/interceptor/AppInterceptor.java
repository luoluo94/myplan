package com.guima.base.interceptor;


import com.guima.base.kits.SysMsg;
import com.guima.kits.DateKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.guima.kits.ShowInfoKit;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Ran on 2018/1/25.
 * APP接口拦截器 处理未知异常
 */
public class AppInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        Controller controller=invocation.getController();
        try {
            invocation.invoke();
        }catch (Exception e){
            e.printStackTrace();
            String errorMsg=e.getMessage();
            if(e.getCause()!=null){
                errorMsg+=e.getCause().getMessage();
                if(e.getCause().getCause()!=null){
                    errorMsg+=e.getCause().getCause().getMessage();
                }
            }
            Method method=invocation.getMethod();
            LogKit.error(DateKit.now()+":"+"方法："+method==null?"":method.getName()+errorMsg,e);
            controller.renderText(JsonKit.toJson(ShowInfoKit.getErrorMap(SysMsg.OsMsg.get("ERROR"))));
        }
    }

}
