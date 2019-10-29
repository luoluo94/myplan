package com.guima.base.kits;

import com.guima.base.interceptor.*;
import com.guima.controller.AdminController;
import com.guima.controller.IndexController;
import com.guima.domain._MappingKit;
import com.guima.kits.Constant;
import com.jfinal.config.*;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.guima.base.service.ServiceManager;
import com.guima.cache.RedisCacheManager;
import com.guima.kits.Kit;
import java.io.File;

public class OsConfig extends JFinalConfig
{
    public static String domainName = Constant.SONG;

    @Override
    public void configConstant(Constants me)
    {
        me.setBaseDownloadPath(SysMsg.Config.get("UPLOAD_DIR"));
        me.setBaseUploadPath(SysMsg.Config.get("UPLOAD_DIR"));
        me.setMaxPostSize(SysMsg.Config.getInt("UPLOAD_MAX"));
        me.setDevMode(SysMsg.Config.getBoolean("dev_mode"));
        me.setViewType(ViewType.JSP);

        me.setError500View(SysMsg.Ctrl.get("path") + "/" + SysMsg.Ctrl.get("err_500")
                + SysMsg.Ctrl.get("suffix"));
        me.setError404View(SysMsg.Ctrl.get("path") + "/" + SysMsg.Ctrl.get("err_404")
                + SysMsg.Ctrl.get("suffix"));
    }

    @Override
    public void configRoute(Routes me)
    {
    	me.add("/", IndexController.class);
        me.add("/admin", AdminController.class);
    	
        String cPath = PathKit.getRootClassPath() + "/com/guima/controller";
        String packageName = "com.guima.controller";
        File f = new File(cPath);
        if (f.isDirectory())
        {
            File[] allFile = f.listFiles();

            for (File controller : allFile)
            {
                buildController(packageName, controller, me, "");
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void buildController(String packageName, File controller, Routes me, String baseName)
    {
        String fileName = controller.getName();
        if (controller.isFile())
        {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            if (!fileName.endsWith("Controller"))
                return;
            String cName = packageName + "." + fileName;
            try
            {
                Class cls = Class.forName(cName);
                String key = fileName.replaceAll("Controller", "").toLowerCase();
                me.add(baseName + "/" + key, cls);
            } catch (Exception e)
            {
                String msg = String.format(SysMsg.OsMsg.get("SORRY_CONTROLLER_NOT_FOUND"), cName);
                LogKit.error(msg);
            }
        } else
        {
            baseName = "/" + fileName;
            packageName += "." + fileName;
            File[] sub = controller.listFiles();
            for (File subFile : sub)
            {
                buildController(packageName, subFile, me, baseName);
            }
        }
    }

    @Override
    public void configPlugin(Plugins me)
    {
    	String url = SysMsg.SqlConfig.get("url");
		String user = SysMsg.SqlConfig.get("user");
		String password = SysMsg.SqlConfig.get("password");
		DruidPlugin druidPlugin = new DruidPlugin(url.trim(), user.trim(), password.trim());
		// druidPlugin.setMaxWait(1);
		druidPlugin.setMaxActive(Kit.isNum(SysMsg.SqlConfig.get("MaxActive"))
				? Integer.parseInt(SysMsg.SqlConfig.get("MaxActive")) : 100);
		druidPlugin.setMinIdle(
				Kit.isNum(SysMsg.SqlConfig.get("MinIdle")) ? Integer.parseInt(SysMsg.SqlConfig.get("MinIdle")) : 10);
		druidPlugin.setInitialSize(Kit.isNum(SysMsg.SqlConfig.get("InitialSize"))
				? Integer.parseInt(SysMsg.SqlConfig.get("InitialSize")) : 10);
		me.add(druidPlugin);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		_MappingKit.mapping(arp);
		me.add(arp);
    }

    @Override
    public void configInterceptor(Interceptors me)
    {
        me.add(new CrossDomainInterceptor());
        me.add(new GlobalSessionInterceptor());
        me.add(new SessionInViewInterceptor());
        me.add(new AppInterceptor());
    }

    @Override
    public void configHandler(Handlers me)
    {
        me.add(new UrlSkipHandler("^/websocket", true));
    }

    @Override
    public void afterJFinalStart()
    {
        super.afterJFinalStart();
        // 加载所有配置表
        ServiceManager.instance();
        // 加载缓存
        if (SysMsg.Config.getBoolean("use_cache"))
            RedisCacheManager.instance();
        // 加载表格元数据（导出excel支持）
        TableMetaFactory.instance();
    }
}
