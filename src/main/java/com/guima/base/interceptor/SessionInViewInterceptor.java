package com.guima.base.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class SessionInViewInterceptor implements Interceptor {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void intercept(Invocation ai) {
        ServletContext context = JFinal.me().getServletContext();
        Enumeration<String> applicationNames = context.getAttributeNames();
        Map application = new HashMap();
        ai.getController().setAttr("application", application);
        ai.getController().setAttr("request", ai.getController().getRequest());
        for (Enumeration<String> names = applicationNames; names.hasMoreElements(); ) {
            String name = names.nextElement();
            application.put(name, context.getAttribute(name));
        }

        HttpSession hs = ai.getController().getSession(false);
        if (hs != null) {
            Map session = new HashMap();
            ai.getController().setAttr("session", session);
            for (Enumeration<String> names = hs.getAttributeNames(); names.hasMoreElements(); ) {
                String name = names.nextElement();
                session.put(name, hs.getAttribute(name));
            }
        }

        ai.invoke();
    }
}
