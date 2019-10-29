package com.guima.controller;

import com.guima.base.kits.SysMsg;
import com.guima.domain.Admin;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;


public class AdminController extends Controller
{
    private String adminPath = PropKit.use("controller.properties").get("adminPath");
    private String index = PropKit.use("controller.properties").get("index");
    private String suffix = PropKit.use("controller.properties").get("suffix");

    /**
     * 若不含session信息，默认跳转到后台首页
     */
    public void index()
    {
        render(adminPath + "/" + index + suffix);
    }

    public void main()
    {
        render(adminPath + "/" + "main" + suffix);
    }

    /**
     * 后台页面转跳服务，调用方式/admin/v?p=xxx
     */
    public void v()
    {
        String page = getPara("p", "index");
        Admin user = getSessionAttr(SysMsg.OsMsg.get("SESSION_KEY_ADMIN"));
        if(user!=null){
            render(adminPath + "/" + page + suffix+"?id="+getPara("id"));
        }else
            renderError(500);
    }
}
