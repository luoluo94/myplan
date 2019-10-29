package com.guima.base.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;


public class CrossDomainInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller c = inv.getController();
        String origin = c.getRequest().getHeader("origin");

        c.getResponse().setHeader("Access-Control-Allow-Origin",origin);
        c.getResponse().setHeader("Access-Control-Allow-Credentials","true");
        c.getResponse().setHeader("Access-Control-Allow-Methods","POST, GET, DELETE, PUT, OPTIONS");
        c.getResponse().setHeader("Access-Control-Allow-Headers","x-requested-with,content-type");

        if (!"OPTIONS".equals(c.getRequest().getMethod())){
            inv.invoke();
        }else {
            c.renderNull();
        }
    }
}
